/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap523;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/** Locates one exact NeoForge mod artifact without loading any mod classes. */
final class ExactNeoForgeModArtifact {

    private static final int BUFFER_SIZE = 64 * 1024;
    private static final int MAX_RESOURCE_ROOTS = 4_096;
    private static final int MAX_MOD_DESCRIPTOR_BYTES = 1024 * 1024;
    private static final String MOD_DESCRIPTOR = "META-INF/neoforge.mods.toml";
    private static final Pattern MODS_TABLE = Pattern.compile(
            "^\\[\\[\\s*(?:mods|\\\"mods\\\"|'mods')\\s*\\]\\]$"
    );

    private final Pattern modIdDeclaration;
    private final String expectedSha256;
    private final long expectedBytes;
    private final String reasonPrefix;

    ExactNeoForgeModArtifact(
            String modId,
            String expectedSha256,
            long expectedBytes,
            String reasonPrefix
    ) {
        String exactModId = Objects.requireNonNull(modId, "modId");
        this.expectedSha256 = Objects.requireNonNull(expectedSha256, "expectedSha256");
        this.reasonPrefix = Objects.requireNonNull(reasonPrefix, "reasonPrefix");
        if (!exactModId.matches("[a-z0-9_]+")) {
            throw new IllegalArgumentException("modId is not a normalized NeoForge mod id");
        }
        if (expectedBytes < 0) {
            throw new IllegalArgumentException("expectedBytes must not be negative");
        }
        this.expectedBytes = expectedBytes;
        this.modIdDeclaration = Pattern.compile(
                "^(?:modId|\\\"modId\\\"|'modId')\\s*=\\s*"
                        + "(?:\\\"" + Pattern.quote(exactModId) + "\\\"|'"
                        + Pattern.quote(exactModId) + "')$"
        );
    }

    Detection detect(Iterable<Path> roots) throws IOException, InterruptedException {
        Objects.requireNonNull(roots, "roots");
        Set<Path> inspectedJars = new HashSet<>();
        Path candidate = null;
        int rootCount = 0;
        for (Path root : roots) {
            if (Thread.interrupted()) {
                throw new InterruptedException("Interrupted while identifying optional resources");
            }
            rootCount++;
            if (rootCount > MAX_RESOURCE_ROOTS) {
                throw new IOException("Too many resource roots while identifying optional resources");
            }
            if (!Files.isRegularFile(root)) {
                continue;
            }

            String fileName = root.getFileName().toString().toLowerCase(Locale.ROOT);
            if (!fileName.endsWith(".jar")) {
                continue;
            }

            Path realPath = root.toRealPath();
            if (!inspectedJars.add(realPath) || !declaresMod(realPath)) {
                continue;
            }
            if (candidate != null) {
                return Detection.inactive("multiple-" + reasonPrefix + "-artifacts");
            }
            candidate = realPath;
        }

        if (candidate == null) {
            return Detection.inactive(reasonPrefix + "-artifact-not-found");
        }
        if (Files.size(candidate) != expectedBytes || !expectedSha256.equals(sha256(candidate))) {
            return Detection.inactive("unsupported-" + reasonPrefix + "-artifact");
        }
        return new Detection(candidate, "exact-" + reasonPrefix + "-artifact");
    }

    private boolean declaresMod(Path jar) throws IOException {
        try (ZipFile zip = new ZipFile(jar.toFile())) {
            ZipEntry descriptor = zip.getEntry(MOD_DESCRIPTOR);
            if (descriptor == null || descriptor.isDirectory()) {
                return false;
            }
            if (descriptor.getSize() > MAX_MOD_DESCRIPTOR_BYTES) {
                throw new IOException("NeoForge mod descriptor exceeds the inspection limit");
            }

            byte[] bytes;
            try (InputStream input = zip.getInputStream(descriptor)) {
                bytes = input.readNBytes(MAX_MOD_DESCRIPTOR_BYTES + 1);
            }
            if (bytes.length > MAX_MOD_DESCRIPTOR_BYTES) {
                throw new IOException("NeoForge mod descriptor exceeds the inspection limit");
            }
            return descriptorDeclaresMod(decodeUtf8(bytes));
        }
    }

    private boolean descriptorDeclaresMod(String descriptor) {
        String normalized = descriptor.startsWith("\ufeff") ? descriptor.substring(1) : descriptor;
        LexicalState state = LexicalState.NORMAL;
        boolean inModsTable = false;
        for (String line : normalized.split("\\R", -1)) {
            SanitizedLine sanitized = sanitizeTomlLine(line, state);
            state = sanitized.state();
            String statement = sanitized.text().trim();
            if (statement.isEmpty()) {
                continue;
            }
            if (statement.startsWith("[")) {
                inModsTable = MODS_TABLE.matcher(statement).matches();
                continue;
            }
            if (inModsTable && modIdDeclaration.matcher(statement).matches()) {
                return true;
            }
        }
        return false;
    }

    private static String decodeUtf8(byte[] bytes) throws IOException {
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes))
                    .toString();
        } catch (CharacterCodingException exception) {
            throw new IOException("NeoForge mod descriptor is not valid UTF-8", exception);
        }
    }

    private static SanitizedLine sanitizeTomlLine(String line, LexicalState initialState) {
        StringBuilder result = new StringBuilder(line.length());
        LexicalState state = initialState;
        boolean basicString = false;
        boolean literalString = false;

        for (int index = 0; index < line.length();) {
            if (state != LexicalState.NORMAL) {
                String delimiter = state == LexicalState.MULTILINE_BASIC ? "\"\"\"" : "'''";
                if (line.startsWith(delimiter, index)
                        && (state != LexicalState.MULTILINE_BASIC || !isEscaped(line, index))) {
                    state = LexicalState.NORMAL;
                    index += delimiter.length();
                } else {
                    index++;
                }
                continue;
            }

            char character = line.charAt(index);
            if (basicString) {
                result.append(character);
                if (character == '"' && !isEscaped(line, index)) {
                    basicString = false;
                }
                index++;
                continue;
            }
            if (literalString) {
                result.append(character);
                if (character == '\'') {
                    literalString = false;
                }
                index++;
                continue;
            }
            if (line.startsWith("\"\"\"", index)) {
                result.append("\"\"\"");
                state = LexicalState.MULTILINE_BASIC;
                index += 3;
                continue;
            }
            if (line.startsWith("'''", index)) {
                result.append("'''");
                state = LexicalState.MULTILINE_LITERAL;
                index += 3;
                continue;
            }
            if (character == '#') {
                break;
            }
            result.append(character);
            if (character == '"') {
                basicString = true;
            } else if (character == '\'') {
                literalString = true;
            }
            index++;
        }
        return new SanitizedLine(result.toString(), state);
    }

    private static boolean isEscaped(String line, int index) {
        int backslashes = 0;
        for (int cursor = index - 1; cursor >= 0 && line.charAt(cursor) == '\\'; cursor--) {
            backslashes++;
        }
        return backslashes % 2 != 0;
    }

    private static String sha256(Path file) throws IOException {
        MessageDigest digest = newSha256();
        byte[] buffer = new byte[BUFFER_SIZE];
        try (InputStream input = Files.newInputStream(file)) {
            int read;
            while ((read = input.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    static MessageDigest newSha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    record Detection(Path path, String reason) {

        static Detection inactive(String reason) {
            return new Detection(null, reason);
        }

        boolean exact() {
            return path != null;
        }
    }

    private record SanitizedLine(String text, LexicalState state) {
    }

    private enum LexicalState {
        NORMAL,
        MULTILINE_BASIC,
        MULTILINE_LITERAL
    }
}

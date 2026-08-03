/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6;

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

/** Locates the exact FramedBlocks artifact among BlueMap's resource roots. */
public final class ExactArtifactDetector {

    private static final int BUFFER_SIZE = 64 * 1024;
    private static final int MAX_RESOURCE_ROOTS = 4_096;
    private static final int MAX_MOD_DESCRIPTOR_BYTES = 1024 * 1024;
    private static final String MOD_DESCRIPTOR = "META-INF/neoforge.mods.toml";
    private static final Pattern MODS_TABLE = Pattern.compile(
            "^\\[\\[\\s*(?:mods|\\\"mods\\\"|'mods')\\s*\\]\\]$"
    );
    private static final Pattern FRAMEDBLOCKS_MOD_ID = Pattern.compile(
            "^(?:modId|\\\"modId\\\"|'modId')\\s*=\\s*"
                    + "(?:\\\"framedblocks\\\"|'framedblocks')$"
    );

    private final String expectedSha256;
    private final long expectedBytes;

    public ExactArtifactDetector(String expectedSha256) {
        this(expectedSha256, FramedBlocks1061Profile.JAR_BYTES);
    }

    ExactArtifactDetector(String expectedSha256, long expectedBytes) {
        this.expectedSha256 = Objects.requireNonNull(expectedSha256, "expectedSha256");
        if (expectedBytes < 0) {
            throw new IllegalArgumentException("expectedBytes must not be negative");
        }
        this.expectedBytes = expectedBytes;
    }

    public Detection detect(Iterable<Path> roots) throws IOException, InterruptedException {
        Set<Path> inspectedJars = new HashSet<>();
        Path candidate = null;
        int rootCount = 0;
        for (Path root : roots) {
            if (Thread.interrupted()) {
                throw new InterruptedException("Interrupted while identifying FramedBlocks resources");
            }
            rootCount++;
            if (rootCount > MAX_RESOURCE_ROOTS) {
                throw new IOException("Too many resource roots while identifying FramedBlocks");
            }
            if (!Files.isRegularFile(root)) {
                continue;
            }

            String fileName = root.getFileName().toString().toLowerCase(Locale.ROOT);
            if (!fileName.endsWith(".jar")) {
                continue;
            }

            Path realPath = root.toRealPath();
            if (!inspectedJars.add(realPath) || !declaresFramedBlocks(realPath)) {
                continue;
            }

            if (candidate != null) {
                return new Detection(false, "multiple-framedblocks-artifacts");
            }
            candidate = realPath;
        }

        if (candidate == null) {
            return new Detection(false, "framedblocks-artifact-not-found");
        }
        if (Files.size(candidate) != expectedBytes || !expectedSha256.equals(sha256(candidate))) {
            return new Detection(false, "unsupported-framedblocks-artifact");
        }

        return new Detection(true, "exact-10.6.1");
    }

    private static boolean declaresFramedBlocks(Path jar) throws IOException {
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
            return descriptorDeclaresFramedBlocks(decodeUtf8(bytes));
        }
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

    private static boolean descriptorDeclaresFramedBlocks(String descriptor) {
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
            if (inModsTable && FRAMEDBLOCKS_MOD_ID.matcher(statement).matches()) {
                return true;
            }
        }
        return false;
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

    static String sha256(Path file) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }

        byte[] buffer = new byte[BUFFER_SIZE];
        try (InputStream input = Files.newInputStream(file)) {
            int read;
            while ((read = input.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    public record Detection(boolean exact, String reason) {
    }

    private record SanitizedLine(String text, LexicalState state) {
    }

    private enum LexicalState {
        NORMAL,
        MULTILINE_BASIC,
        MULTILINE_LITERAL
    }
}

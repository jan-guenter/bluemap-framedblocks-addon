/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/** Deterministically removes geometry which the exact support policy never routes. */
public final class GeometryProfileProjector {

    private GeometryProfileProjector() {
    }

    public static void main(String[] arguments) throws Exception {
        if (arguments.length != 2) {
            throw new IllegalArgumentException("Expected source and output paths");
        }
        Path source = Path.of(arguments[0]).toAbsolutePath().normalize();
        Path output = Path.of(arguments[1]).toAbsolutePath().normalize();
        if (source.equals(output)) {
            throw new IOException("Source client capture and projected output must differ");
        }
        if (!Files.isRegularFile(source, LinkOption.NOFOLLOW_LINKS)
                || Files.isSymbolicLink(source)
                || Files.size(source) != FramedBlocks1061Profile.SOURCE_EXPORT_GZIP_BYTES) {
            throw new IOException("Source client capture is not the exact pinned regular file");
        }

        byte[] compressedSource = Files.readAllBytes(source);
        GeometryTemplateProfile.validateRawExport(compressedSource);
        byte[] rawJson = decompressExact(compressedSource);
        JsonObject raw = JsonParser.parseString(
                new String(rawJson, StandardCharsets.UTF_8)
        ).getAsJsonObject();

        ProjectionCounts counts = removeStockFallbackGeometry(raw);
        verifyProjectionCounts(counts);
        JsonObject projected = addProjectionHeader(raw, counts);
        Gson gson = new GsonBuilder().serializeNulls().create();
        byte[] projectedJson = gson.toJson(projected).getBytes(StandardCharsets.UTF_8);
        byte[] projectedGzip = compress(projectedJson);
        if (!java.util.Arrays.equals(projectedGzip, compress(projectedJson))) {
            throw new IOException("FramedBlocks geometry gzip encoding is not deterministic");
        }

        GeometryTemplateProfile.validateProjectedExport(projectedGzip);
        writeAtomically(output, projectedGzip);
        GeometryTemplateProfile.validateProjectedExport(Files.readAllBytes(output));

        System.out.printf("projected.gzip.bytes=%d%n", projectedGzip.length);
        System.out.printf("projected.gzip.sha256=%s%n", sha256(projectedGzip));
        System.out.printf("projected.uncompressed.bytes=%d%n", projectedJson.length);
        System.out.printf("projected.uncompressed.sha256=%s%n", sha256(projectedJson));
        System.out.printf("projected.templates=%d%n", counts.totalTemplates());
        System.out.printf("projected.quads=%d%n", counts.projectedQuads());
        System.out.printf("excluded.blocks=%d%n", counts.excludedBlockIds().size());
        System.out.printf("excluded.templates=%d%n", counts.excludedTemplates());
        System.out.printf("excluded.quads=%d%n", counts.excludedQuads());
    }

    private static ProjectionCounts removeStockFallbackGeometry(JsonObject document)
            throws IOException {
        Set<String> policy = FramedBlocks1061Support.stockFallbackBlockIds();
        Set<String> excludedBlockIds = new HashSet<>();
        int excludedTemplates = 0;
        int excludedQuads = 0;
        int sourceQuads = 0;
        JsonArray templates = document.getAsJsonArray("templates");
        for (JsonElement element : templates) {
            JsonObject template = element.getAsJsonObject();
            String blockId = template.get("blockId").getAsString();
            JsonArray quads = template.getAsJsonArray("quads");
            sourceQuads = Math.addExact(sourceQuads, quads.size());
            if (policy.contains(blockId)) {
                excludedBlockIds.add(blockId);
                excludedTemplates++;
                excludedQuads = Math.addExact(excludedQuads, quads.size());
                template.add("quads", new JsonArray());
            }
        }
        return new ProjectionCounts(
                templates.size(),
                sourceQuads,
                sourceQuads - excludedQuads,
                Set.copyOf(excludedBlockIds),
                excludedTemplates,
                excludedQuads
        );
    }

    private static void verifyProjectionCounts(ProjectionCounts counts) throws IOException {
        Set<String> policy = FramedBlocks1061Support.stockFallbackBlockIds();
        if (counts.totalTemplates() != FramedBlocks1061Profile.CLIENT_TEMPLATE_COUNT
                || counts.sourceQuads() != FramedBlocks1061Profile.SOURCE_EXPORT_QUAD_COUNT
                || counts.projectedQuads() != FramedBlocks1061Profile.PROJECTED_QUAD_COUNT
                || !counts.excludedBlockIds().equals(policy)
                || counts.excludedTemplates()
                        != FramedBlocks1061Profile.EXCLUDED_GEOMETRY_TEMPLATE_COUNT
                || counts.excludedQuads()
                        != FramedBlocks1061Profile.EXCLUDED_GEOMETRY_QUAD_COUNT
                || !FramedBlocks1061Profile.EXCLUDED_GEOMETRY_BLOCK_IDS_SHA256.equals(
                        sha256Lines(policy)
                )) {
            throw new IOException("FramedBlocks geometry projection counts do not match policy");
        }
    }

    private static JsonObject addProjectionHeader(
            JsonObject raw,
            ProjectionCounts counts
    ) {
        JsonObject projected = new JsonObject();
        projected.addProperty("schemaVersion", FramedBlocks1061Profile.PROJECTED_SCHEMA_VERSION);
        projected.addProperty(
                "geometryProjection",
                FramedBlocks1061Profile.GEOMETRY_PROJECTION
        );
        projected.addProperty(
                "sourceSchemaVersion",
                FramedBlocks1061Profile.SOURCE_EXPORT_SCHEMA_VERSION
        );
        projected.addProperty(
                "sourceExportGzipSha256",
                FramedBlocks1061Profile.SOURCE_EXPORT_GZIP_SHA256
        );
        projected.addProperty(
                "sourceExportGzipBytes",
                FramedBlocks1061Profile.SOURCE_EXPORT_GZIP_BYTES
        );
        projected.addProperty(
                "sourceExportUncompressedSha256",
                FramedBlocks1061Profile.SOURCE_EXPORT_UNCOMPRESSED_SHA256
        );
        projected.addProperty(
                "sourceExportUncompressedBytes",
                FramedBlocks1061Profile.SOURCE_EXPORT_UNCOMPRESSED_BYTES
        );
        projected.addProperty("sourceQuadCount", counts.sourceQuads());
        projected.addProperty("excludedGeometryBlockCount", counts.excludedBlockIds().size());
        projected.addProperty(
                "excludedGeometryBlockIdsSha256",
                FramedBlocks1061Profile.EXCLUDED_GEOMETRY_BLOCK_IDS_SHA256
        );
        projected.addProperty("excludedGeometryTemplateCount", counts.excludedTemplates());
        projected.addProperty("excludedGeometryQuadCount", counts.excludedQuads());
        for (var entry : raw.entrySet()) {
            if ("schemaVersion".equals(entry.getKey())) {
                continue;
            }
            if ("quadCount".equals(entry.getKey())) {
                projected.addProperty("quadCount", counts.projectedQuads());
            } else {
                projected.add(entry.getKey(), entry.getValue().deepCopy());
            }
        }
        return projected;
    }

    private static byte[] decompressExact(byte[] compressed) throws IOException {
        try (InputStream gzip = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
            byte[] uncompressed = gzip.readNBytes(
                    Math.toIntExact(FramedBlocks1061Profile.SOURCE_EXPORT_UNCOMPRESSED_BYTES + 1L)
            );
            if (uncompressed.length
                    != FramedBlocks1061Profile.SOURCE_EXPORT_UNCOMPRESSED_BYTES
                    || gzip.read() != -1) {
                throw new IOException("Source client capture JSON length changed");
            }
            return uncompressed;
        }
    }

    private static byte[] compress(byte[] uncompressed) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(output)) {
            gzip.write(uncompressed);
        }
        return output.toByteArray();
    }

    private static void writeAtomically(Path output, byte[] bytes) throws IOException {
        Path parent = output.getParent();
        Files.createDirectories(parent);
        Path temporary = Files.createTempFile(parent, output.getFileName().toString(), ".tmp");
        try {
            Files.write(temporary, bytes);
            try {
                Files.move(
                        temporary,
                        output,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING
                );
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, output, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static String sha256Lines(Set<String> values) throws IOException {
        String canonical = String.join("\n", values.stream().sorted().toList()) + "\n";
        return sha256(canonical.getBytes(StandardCharsets.UTF_8));
    }

    private static String sha256(byte[] bytes) throws IOException {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(bytes)
            );
        } catch (NoSuchAlgorithmException exception) {
            throw new IOException("SHA-256 is unavailable", exception);
        }
    }

    private record ProjectionCounts(
            int totalTemplates,
            int sourceQuads,
            int projectedQuads,
            Set<String> excludedBlockIds,
            int excludedTemplates,
            int excludedQuads
    ) {
    }
}

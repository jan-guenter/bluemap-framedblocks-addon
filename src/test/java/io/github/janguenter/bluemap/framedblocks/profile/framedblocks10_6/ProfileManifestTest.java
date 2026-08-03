/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfileManifestTest {

    @Test
    void inventoriesRemainCanonicalAndEvidenceLocked() throws IOException {
        byte[] blockEntities = resource("block-entity-ids.txt");
        byte[] blockstates = resource("blockstate-ids.txt");

        assertTrue(new String(blockEntities).endsWith("\n"));
        assertTrue(new String(blockstates).endsWith("\n"));
        assertEquals(51, new String(blockEntities).lines().count());
        assertEquals(236, new String(blockstates).lines().count());
        assertEquals(51, FramedBlocks1061Profile.blockEntityKeys().size());
        assertEquals(236, FramedBlocks1061Profile.blockStateKeys().size());
        assertFalse(new String(blockEntities).contains("powered_framing_saw"));
        assertEquals(
                "faef8938e780a6997f59978221ff6fcb52de1e08d01d5d0a4b1c493eb4b0455b",
                sha256(blockEntities)
        );
        assertEquals(
                "e4aed367abf2f037d92496e5028fc9493ae7fb48c5e8dd6ffb85eeddb13330c9",
                sha256(blockstates)
        );
    }

    @Test
    void projectedGeometryPayloadRemainsEvidenceLocked() throws IOException {
        byte[] compressed = resource("geometry-templates.json.gz");
        byte[] uncompressed;
        try (InputStream gzip = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
            uncompressed = gzip.readAllBytes();
        }

        assertEquals(FramedBlocks1061Profile.PROJECTED_EXPORT_GZIP_BYTES, compressed.length);
        assertEquals(FramedBlocks1061Profile.PROJECTED_EXPORT_GZIP_SHA256, sha256(compressed));
        assertEquals(
                FramedBlocks1061Profile.PROJECTED_EXPORT_UNCOMPRESSED_BYTES,
                uncompressed.length
        );
        assertEquals(
                FramedBlocks1061Profile.PROJECTED_EXPORT_UNCOMPRESSED_SHA256,
                sha256(uncompressed)
        );
    }

    private static byte[] resource(String fileName) throws IOException {
        String path = "/bluemap-framedblocks/profiles/10.6.1/" + fileName;
        try (InputStream input = ProfileManifestTest.class.getResourceAsStream(path)) {
            assertNotNull(input, path);
            return input.readAllBytes();
        }
    }

    private static String sha256(byte[] value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}

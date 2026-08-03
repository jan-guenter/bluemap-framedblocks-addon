/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6;

import de.bluecolored.bluemap.core.util.Key;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

/** Exact identifiers and limits for the initial FramedBlocks 10.6.1 profile. */
public final class FramedBlocks1061Profile {

    public static final String VERSION = "10.6.1";
    public static final String JAR_SHA1 = "3007be0007d09c0225ca33b647461f342eac0503";
    public static final String JAR_SHA256 =
            "3337f29e1fa3331e8740eef9c20b0750d81fd86d1057fb81012a5c4792aa3369";
    public static final long JAR_BYTES = 4_306_703L;
    public static final String CLIENT_CONFIG_SHA256 =
            "02e7e1c004fc6a15247dd0ddb5c5210a9e0cc901f18f85ad689886eae3d3ea83";
    public static final String CLIENT_MODS_SHA256 =
            "7f0d771cf2c1dc430fa32153651dd9bec5ae5492f34a6c8a7bef8a067f5d50a7";
    public static final String CLIENT_RESOURCE_PACK_ID_SET_SHA256 =
            "1952cce499adb9e79cce0a422418537b3818ef353f100424692bb5dc94958be5";
    public static final String CLIENT_RESOURCE_PACKS_ORDERED_SHA256 =
            "b345c2cfa4743c2a46c5a3ddf1817ca601a1eb91ff446e8a58e6cd0da3e8ed3d";
    public static final String CLIENT_PACK_FINGERPRINT =
            "30d715deacda85316240c9a0f67ccc457f7a00667168dbffb82e16c82ddbcf42";
    public static final int CLIENT_MOD_COUNT = 430;
    public static final int CLIENT_RESOURCE_PACKS_ORDERED_COUNT = 439;
    public static final int CLIENT_RESOURCE_PACK_ID_SET_COUNT = 12;
    public static final int CLIENT_BLOCK_COUNT = 236;
    public static final int CLIENT_RAW_STATE_COUNT = 74_196;
    public static final String CLIENT_RAW_STATE_KEYS_SHA256 =
            "55c7bd013df47f3cd2034fd58a671e4610fe439350b67cc17c7e3d5378cf4421";
    public static final int CLIENT_RENDERABLE_STATE_COUNT = 74_180;
    public static final String CLIENT_RENDERABLE_STATE_KEYS_SHA256 =
            "d71c8a8d993da3eab20c19b5276769269595b2e6261791bba9b21b4ae11178be";
    public static final int CLIENT_TEMPLATE_COUNT = 5_382;
    public static final String CLIENT_TEMPLATE_STATE_KEYS_SHA256 =
            "c5f5e6af6d3841ef8a40c4d7bb5f406dff2a06e535ef72c474cf9b51aa9bd268";
    public static final String CLIENT_ALIAS_PAIRS_SHA256 =
            "a23df06570876f7ba44c8722a42742bf53487dca38227bfc23b5b4ad0ecb6a1c";
    public static final String SOURCE_EXPORT_GZIP_SHA256 =
            "390e7edb3e4c0bd6dbaefa90bebfee2918306cae876900d90d94fa2cbecd8234";
    public static final long SOURCE_EXPORT_GZIP_BYTES = 1_300_572L;
    public static final String SOURCE_EXPORT_UNCOMPRESSED_SHA256 =
            "f261fb5a6c6189ebb5795facb2dc4cd579724a45422b29aba9ef4898f40509a7";
    public static final long SOURCE_EXPORT_UNCOMPRESSED_BYTES = 59_147_042L;
    public static final int SOURCE_EXPORT_SCHEMA_VERSION = 2;
    public static final int SOURCE_EXPORT_QUAD_COUNT = 62_746;
    public static final int PROJECTED_SCHEMA_VERSION = 3;
    public static final String GEOMETRY_PROJECTION = "routed_block_families_only";
    public static final int PROJECTED_QUAD_COUNT = 58_708;
    public static final int EXCLUDED_GEOMETRY_BLOCK_COUNT = 28;
    public static final String EXCLUDED_GEOMETRY_BLOCK_IDS_SHA256 =
            "f0be4b79e8ee82686414b5745634d9096d272fc16730ef06bb1de7a15de62529";
    public static final int EXCLUDED_GEOMETRY_TEMPLATE_COUNT = 524;
    public static final int EXCLUDED_GEOMETRY_QUAD_COUNT = 4_038;
    public static final int PROJECTED_FIXED_SPRITE_COUNT = 18;
    public static final String PROJECTED_FIXED_SPRITES_SHA256 =
            "d41792e24bbd4b03b409fd482c07fe2ca623c1abbab50bf44fb1e1b013512067";
    public static final String PROJECTED_EXPORT_GZIP_SHA256 =
            "d5a91b78090116b9223f1e96d6903cfd6261d7076b8cbe8d67d469ea2de44253";
    public static final long PROJECTED_EXPORT_GZIP_BYTES = 1_256_231L;
    public static final String PROJECTED_EXPORT_UNCOMPRESSED_SHA256 =
            "e16b72167389c92b558dcb5a13d2cbae4e56a59da3f7cdb689c81637798cd001";
    public static final long PROJECTED_EXPORT_UNCOMPRESSED_BYTES = 56_464_720L;
    public static final int CLIENT_BASE_ROUTED_FAMILY_TEMPLATE_COUNT = 4_858;
    public static final int CLIENT_NULL_ALIAS_COUNT = 16;
    public static final int CLIENT_NULL_ALIAS_COUNT_PER_SAW = 8;
    public static final String BLOCK_ENTITY_IDS_SHA256 =
            "faef8938e780a6997f59978221ff6fcb52de1e08d01d5d0a4b1c493eb4b0455b";
    public static final String BLOCK_STATE_IDS_SHA256 =
            "e4aed367abf2f037d92496e5028fc9493ae7fb48c5e8dd6ffb85eeddb13330c9";

    public static final Key FRAMED_TILE = new Key("framedblocks", "framed_tile");
    public static final Key FRAMED_CUBE = new Key("framedblocks", "framed_cube");
    public static final Key SYNTHETIC_FRAMED_SHAPE =
            new Key("bluemap_framedblocks", "framed_shape");
    public static final Key RENDERER =
            new Key("bluemap_framedblocks", "framed_shape");
    public static final Key RESOURCE_EXTENSION =
            new Key("bluemap_framedblocks", "profile_10_6_1");

    private static final String PROFILE_ROOT =
            "/bluemap-framedblocks/profiles/10.6.1/";

    private FramedBlocks1061Profile() {
    }

    public static List<Key> blockEntityKeys() throws IOException {
        return readKeys("block-entity-ids.txt", 51, BLOCK_ENTITY_IDS_SHA256);
    }

    public static List<Key> blockStateKeys() throws IOException {
        return readKeys("blockstate-ids.txt", 236, BLOCK_STATE_IDS_SHA256);
    }

    private static List<Key> readKeys(
            String fileName,
            int expectedCount,
            String expectedSha256
    ) throws IOException {
        try (InputStream input = FramedBlocks1061Profile.class.getResourceAsStream(
                PROFILE_ROOT + fileName
        )) {
            if (input == null) {
                throw new IOException("Missing exact-profile identifier manifest: " + fileName);
            }
            byte[] bytes = input.readAllBytes();
            if (!expectedSha256.equals(sha256(bytes))) {
                throw new IOException("Exact-profile identifier manifest digest mismatch: "
                        + fileName);
            }
            List<Key> keys = new String(bytes, StandardCharsets.UTF_8).lines()
                    .filter(line -> !line.isBlank())
                    .map(Key::parse)
                    .toList();
            if (keys.size() != expectedCount || keys.stream().distinct().count() != expectedCount) {
                throw new IOException("Invalid exact-profile identifier manifest: " + fileName);
            }
            return keys;
        } catch (IllegalArgumentException exception) {
            throw new IOException("Invalid identifier in exact-profile manifest: " + fileName,
                    exception);
        }
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
}

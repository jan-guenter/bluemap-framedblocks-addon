/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap523;

import de.bluecolored.bluemap.core.resources.adapter.ResourcesGson;
import de.bluecolored.bluemap.core.resources.pack.PackVersion;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.FramedBlocks1061Profile;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.GeometryTemplateProfile;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FramedBlocksResourceExtensionTest {

    @Test
    void bundledProfileRoutesFormerBlockLevelFallbackFamilies() throws IOException {
        AdapterActivation activation = new AdapterActivation();
        activation.activate(GeometryTemplateProfile.loadBundled());
        FramedBlocksResourceExtension extension =
                new FramedBlocksResourceExtension(null, activation);

        assertEquals(
                FramedBlocks1061Profile.SYNTHETIC_FRAMED_SHAPE,
                extension.getBlockStateKey(Key.parse("framedblocks:framed_tank"))
        );
        assertEquals(
                FramedBlocks1061Profile.SYNTHETIC_FRAMED_SHAPE,
                extension.getBlockStateKey(Key.parse("framedblocks:framed_chest"))
        );
    }

    @Test
    void routedShapesDoNotInheritSyntheticMissingCubeOcclusion() throws IOException {
        AdapterActivation activation = new AdapterActivation();
        activation.activate(profileWithFramedCube());
        FramedBlocksResourceExtension extension =
                new FramedBlocksResourceExtension(null, activation);

        BlockProperties.Builder framedProperties = allCullingPropertiesEnabled();
        extension.getBlockProperties(
                BlockState.fromString(
                        "framedblocks:framed_cube[glowing=false,waterlogged=false]"
                ),
                framedProperties
        );
        BlockProperties framed = framedProperties.build();
        assertFalse(framed.isCulling());
        assertFalse(framed.isOccluding());
        assertFalse(framed.getCullingIdentical());

        BlockProperties.Builder stockProperties = allCullingPropertiesEnabled();
        extension.getBlockProperties(
                BlockState.fromString("minecraft:stone"),
                stockProperties
        );
        BlockProperties stock = stockProperties.build();
        assertTrue(stock.isCulling());
        assertTrue(stock.isOccluding());
        assertTrue(stock.getCullingIdentical());

        assertEquals(
                FramedBlocks1061Profile.SYNTHETIC_FRAMED_SHAPE,
                extension.getBlockStateKey(FramedBlocks1061Profile.FRAMED_CUBE)
        );
        assertEquals(
                FramedBlocks1061Profile.SYNTHETIC_FRAMED_SHAPE,
                extension.getBlockStateKey(
                        de.bluecolored.bluemap.core.util.Key.parse("framedblocks:framed_tank")
                )
        );

        BlockProperties.Builder dynamicProperties = allCullingPropertiesEnabled();
        extension.getBlockProperties(
                BlockState.fromString("framedblocks:framed_tank"),
                dynamicProperties
        );
        BlockProperties dynamic = dynamicProperties.build();
        assertFalse(dynamic.isCulling());
        assertFalse(dynamic.isOccluding());
        assertFalse(dynamic.getCullingIdentical());

        BlockProperties.Builder waterloggedProperties = allCullingPropertiesEnabled();
        extension.getBlockProperties(
                BlockState.fromString(
                        "framedblocks:framed_cube[glowing=false,waterlogged=true]"
                ),
                waterloggedProperties
        );
        BlockProperties waterlogged = waterloggedProperties.build();
        assertFalse(waterlogged.isCulling());
        assertFalse(waterlogged.isOccluding());
        assertFalse(waterlogged.getCullingIdentical());

        BlockProperties.Builder glowingProperties = allCullingPropertiesEnabled();
        extension.getBlockProperties(
                BlockState.fromString(
                        "framedblocks:framed_cube[glowing=true,waterlogged=false]"
                ),
                glowingProperties
        );
        BlockProperties glowing = glowingProperties.build();
        assertFalse(glowing.isCulling());
        assertFalse(glowing.isOccluding());
        assertFalse(glowing.getCullingIdentical());
    }

    @Test
    void validatesTheEffectiveSyntheticDispatchResourceStructurally() {
        assertTrue(BlueMap523Adapter.install());
        String exact = """
                {"variants":{"":{"renderer":"bluemap_framedblocks:framed_shape",
                                   "model":"bluemap:block/missing"}}}
                """;
        String wrongModel = exact.replace(
                "bluemap:block/missing",
                "minecraft:block/stone"
        );
        String wrongRenderer = exact.replace(
                "bluemap_framedblocks:framed_shape",
                "bluemap:default"
        );

        assertTrue(FramedBlocksResourceExtension.isExpectedSyntheticBlockState(
                parseBlockState(exact)
        ));
        assertFalse(FramedBlocksResourceExtension.isExpectedSyntheticBlockState(
                parseBlockState(wrongModel)
        ));
        assertFalse(FramedBlocksResourceExtension.isExpectedSyntheticBlockState(
                parseBlockState(wrongRenderer)
        ));
    }

    @Test
    void fixedProfileSpritesMustExistAfterResourceBake() throws IOException {
        ResourcePack resourcePack = new ResourcePack(new PackVersion(34, 0));
        Key fixed = Key.parse("framedblocks:block/fixed_overlay");

        assertFalse(FramedBlocksResourceExtension.hasAllFixedSprites(
                resourcePack,
                Set.of(fixed)
        ));

        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, 0xFFFFFFFF);
        resourcePack.getTextures().put(fixed, Texture.from(fixed, image));

        assertTrue(FramedBlocksResourceExtension.hasAllFixedSprites(
                resourcePack,
                Set.of(fixed)
        ));
        assertFalse(FramedBlocksResourceExtension.hasAllFixedSprites(
                resourcePack,
                Set.of(ResourcePack.MISSING_TEXTURE)
        ));
    }

    private static BlockProperties.Builder allCullingPropertiesEnabled() {
        return BlockProperties.builder()
                .culling(true)
                .occluding(true)
                .cullingIdentical(true);
    }

    private static de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState
            parseBlockState(String json) {
        return ResourcesGson.INSTANCE.fromJson(
                json,
                de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState.class
        );
    }

    private static GeometryTemplateProfile profileWithFramedCube() throws IOException {
        String document = """
                {"schemaVersion":2,"profileScope":"applied_camo",
                 "sourceModel":"canonical_opaque_full_cube",
                 "modelDataProfile":"diagnostic_applied_camo_no_dynamic_aux",
                 "dynamicModelDataIncluded":false,
                 "blockEntityRendererContentIncluded":false,
                 "uvSpace":"sprite_normalized_baked",
                 "resourcePackOrder":"selected_precedence_including_hidden",
                 "effectiveAmbientOcclusionAssumption":"zero_block_state_emission",
                 "randomSeed":"0x4652414d4544424c","smoothLightingEnabled":true,
                 "framedBlocksVersion":"10.6.1",
                 "framedBlocksJarSha256":
                   "3337f29e1fa3331e8740eef9c20b0750d81fd86d1057fb81012a5c4792aa3369",
                 "framedBlocksJarBytes":4306703,
                 "framedBlocksClientConfigSha256":
                   "02e7e1c004fc6a15247dd0ddb5c5210a9e0cc901f18f85ad689886eae3d3ea83",
                 "minecraftVersion":"1.21.1","neoForgeVersion":"21.1.234",
                 "packFingerprint":
                   "30d715deacda85316240c9a0f67ccc457f7a00667168dbffb82e16c82ddbcf42",
                 "modsSha256":
                   "7f0d771cf2c1dc430fa32153651dd9bec5ae5492f34a6c8a7bef8a067f5d50a7",
                 "resourcePacksOrderedSha256":
                   "b345c2cfa4743c2a46c5a3ddf1817ca601a1eb91ff446e8a58e6cd0da3e8ed3d",
                 "resourcePackIdSetSha256":
                   "1952cce499adb9e79cce0a422418537b3818ef353f100424692bb5dc94958be5",
                 "modCount":430,"resourcePacksOrderedCount":439,
                 "resourcePackIdSetCount":12,"blockCount":2,
                 "blockIdsSha256":
                   "a17999e5857b4cf8c0b9286db4ab92b1349d29f167215edc63684660ca110701",
                 "raw_state_count":4,
                 "raw_state_keys_sha256":
                   "93bd36887dec2684fd7f31dc267e4d8353536284c1fbd96cd3ead74fd22b4448",
                 "renderable_state_count":4,
                 "renderable_state_keys_sha256":
                   "93bd36887dec2684fd7f31dc267e4d8353536284c1fbd96cd3ead74fd22b4448",
                 "template_count":4,
                 "template_state_keys_sha256":
                   "93bd36887dec2684fd7f31dc267e4d8353536284c1fbd96cd3ead74fd22b4448",
                 "alias_pairs_sha256":
                   "dba8fceeb4f7802e51574b1137c32cdbd0aaa62434ab8e7f70c88b254c5a10b3",
                 "quadCount":3,
                 "nonCamoUtilityBlocks":["framedblocks:framing_saw",
                                         "framedblocks:powered_framing_saw"],
                 "excludedDynamicInputs":["world_block_entity_model_data",
                    "connected_texture_context","geometry_aux_model_data",
                    "flower_pot_contents","collapsible_and_copycat_offsets",
                    "adjustable_double_component_offsets","one_way_window_context",
                    "hidden_face_masks","block_entity_renderer_content"],
                 "templates":[{"blockId":"framedblocks:framed_cube",
                            "properties":{"glowing":"false","waterlogged":"false"},
                            "quads":[
                              {"layer":"solid","cullFace":"none",
                               "direction":"up","component":"fixed",
                               "sourceFace":"none",
                               "atlas":"minecraft:textures/atlas/blocks.png",
                               "sprite":"minecraft:block/stone","tintIndex":-1,
                               "shade":true,"ambientOcclusion":true,
                               "modelAmbientOcclusion":"true",
                               "effectiveAmbientOcclusionUnderZeroEmission":true,
                               "blockLight":0,"skyLight":0,
                               "vertices":[
                                 {"x":0,"y":1,"z":0,"u":0,"v":0,
                                  "blockLight":0,"skyLight":0},
                                 {"x":1,"y":1,"z":0,"u":1,"v":0,
                                  "blockLight":0,"skyLight":0},
                                 {"x":1,"y":1,"z":1,"u":1,"v":1,
                                  "blockLight":0,"skyLight":0},
                                 {"x":0,"y":1,"z":1,"u":0,"v":1,
                                  "blockLight":0,"skyLight":0}]}]},
                           {"blockId":"framedblocks:framed_cube",
                            "properties":{"glowing":"false","waterlogged":"true"},
                            "quads":[
                              {"layer":"solid","cullFace":"none",
                               "direction":"up","component":"fixed",
                               "sourceFace":"none",
                               "atlas":"minecraft:textures/atlas/blocks.png",
                               "sprite":"minecraft:block/stone","tintIndex":-1,
                               "shade":true,"ambientOcclusion":true,
                               "modelAmbientOcclusion":"true",
                               "effectiveAmbientOcclusionUnderZeroEmission":true,
                               "blockLight":0,"skyLight":0,
                               "vertices":[
                                 {"x":0,"y":1,"z":0,"u":0,"v":0,
                                  "blockLight":0,"skyLight":0},
                                 {"x":1,"y":1,"z":0,"u":1,"v":0,
                                  "blockLight":0,"skyLight":0},
                                 {"x":1,"y":1,"z":1,"u":1,"v":1,
                                  "blockLight":0,"skyLight":0},
                                 {"x":0,"y":1,"z":1,"u":0,"v":1,
                                  "blockLight":0,"skyLight":0}]}]},
                           {"blockId":"framedblocks:framed_cube",
                            "properties":{"glowing":"true","waterlogged":"false"},
                            "quads":[
                              {"layer":"solid","cullFace":"none",
                               "direction":"up","component":"fixed",
                               "sourceFace":"none",
                               "atlas":"minecraft:textures/atlas/blocks.png",
                               "sprite":"minecraft:block/stone","tintIndex":-1,
                               "shade":true,"ambientOcclusion":true,
                               "modelAmbientOcclusion":"true",
                               "effectiveAmbientOcclusionUnderZeroEmission":true,
                               "blockLight":0,"skyLight":0,
                               "vertices":[
                                 {"x":0,"y":1,"z":0,"u":0,"v":0,
                                  "blockLight":0,"skyLight":0},
                                 {"x":1,"y":1,"z":0,"u":1,"v":0,
                                  "blockLight":0,"skyLight":0},
                                 {"x":1,"y":1,"z":1,"u":1,"v":1,
                                  "blockLight":0,"skyLight":0},
                                 {"x":0,"y":1,"z":1,"u":0,"v":1,
                                  "blockLight":0,"skyLight":0}]}]},
                           {"blockId":"framedblocks:framed_tank",
                            "properties":{},"quads":[]}],
                 "states":[{"blockId":"framedblocks:framed_cube",
                            "properties":{"glowing":"false","waterlogged":"false"},
                            "template":0},
                           {"blockId":"framedblocks:framed_cube",
                            "properties":{"glowing":"false","waterlogged":"true"},
                            "template":1},
                           {"blockId":"framedblocks:framed_cube",
                            "properties":{"glowing":"true","waterlogged":"false"},
                            "template":2},
                           {"blockId":"framedblocks:framed_tank",
                            "properties":{},"template":3}]}
                """;
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(compressed)) {
            gzip.write(document.getBytes(StandardCharsets.UTF_8));
        }
        return GeometryTemplateProfile.loadGzip(
                new ByteArrayInputStream(compressed.toByteArray())
        );
    }
}

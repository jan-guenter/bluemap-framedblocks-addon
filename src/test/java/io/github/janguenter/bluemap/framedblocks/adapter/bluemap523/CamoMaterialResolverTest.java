/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap523;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector4f;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.map.hires.block.ResourceModelRenderer;
import de.bluecolored.bluemap.core.resources.ResourcePath;
import de.bluecolored.bluemap.core.resources.pack.PackVersion;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.VariantSet;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variants;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Element;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Face;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Rotation;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.TextureVariable;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Direction;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.NormalizedBlockState;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.NormalizedCamo;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CamoMaterialResolverTest {

    private static final ResourcePath<Model> MODEL_PATH =
            new ResourcePath<>("minecraft:block/test_cube");

    @Test
    void acceptsTheExactRegisteredCamolWrapperButNoOtherCustomRenderer() {
        Key camolKey = Key.parse("bluemap_camol:overlay");
        BlockRendererType camol = BlockRendererType.REGISTRY.get(camolKey);
        if (camol == null) {
            camol = new BlockRendererType.Impl(camolKey, ResourceModelRenderer::new);
            BlockRendererType.REGISTRY.register(camol);
        }

        assertTrue(CamoMaterialResolver.isSupportedMaterialRenderer(
                BlockRendererType.DEFAULT
        ));
        assertTrue(CamoMaterialResolver.isSupportedMaterialRenderer(camol));
        assertFalse(CamoMaterialResolver.isSupportedMaterialRenderer(
                BlockRendererType.LIQUID
        ));
    }

    @Test
    void acceptsOnlyCanonicalUntransformedFullCubeFaces() {
        Model canonical = model(Vector3f.ZERO, new Vector3f(16F, 16F, 16F), 0, true);
        assertTrue(CamoMaterialResolver.isSimpleStaticCube(
                new Variant(MODEL_PATH),
                canonical
        ));

        assertFalse(CamoMaterialResolver.isSimpleStaticCube(
                new Variant(MODEL_PATH, 0F, 90F, 0F),
                canonical
        ));
        Variant customRenderer = new Variant(MODEL_PATH);
        customRenderer.setRenderer(BlockRendererType.LIQUID);
        assertFalse(CamoMaterialResolver.isSimpleStaticCube(customRenderer, canonical));
        assertFalse(CamoMaterialResolver.isSimpleStaticCube(
                new Variant(MODEL_PATH, 0F, 0F, 0F, true, 1D),
                canonical
        ));
        assertFalse(CamoMaterialResolver.isSimpleStaticCube(
                new Variant(MODEL_PATH),
                model(Vector3f.ZERO, new Vector3f(16F, 15F, 16F), 0, true)
        ));
        assertFalse(CamoMaterialResolver.isSimpleStaticCube(
                new Variant(MODEL_PATH),
                model(Vector3f.ZERO, new Vector3f(16F, 16F, 16F), 90, true)
        ));
        assertFalse(CamoMaterialResolver.isSimpleStaticCube(
                new Variant(MODEL_PATH),
                model(Vector3f.ZERO, new Vector3f(16F, 16F, 16F), 0, false)
        ));
        assertFalse(CamoMaterialResolver.isSimpleStaticCube(
                new Variant(MODEL_PATH),
                model(Vector3f.ZERO, new Vector3f(16F, 16F, 16F), 0, true, 15)
        ));
        assertFalse(CamoMaterialResolver.isSimpleStaticCube(
                new Variant(MODEL_PATH),
                model(Vector3f.ZERO, new Vector3f(16F, 16F, 16F), 0, true, 0, 1)
        ));
    }

    @Test
    void rejectsCutoutAndTranslucentTexturesFromTheOpaqueLane() throws IOException {
        BufferedImage opaqueImage = new BufferedImage(2, 1, BufferedImage.TYPE_INT_ARGB);
        opaqueImage.setRGB(0, 0, 0xFFFFFFFF);
        opaqueImage.setRGB(1, 0, 0xFFFFFFFF);
        BufferedImage cutoutImage = new BufferedImage(2, 1, BufferedImage.TYPE_INT_ARGB);
        cutoutImage.setRGB(0, 0, 0xFFFFFFFF);
        cutoutImage.setRGB(1, 0, 0x00FFFFFF);
        BufferedImage translucentImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        translucentImage.setRGB(0, 0, 0x80FFFFFF);

        assertTrue(CamoMaterialResolver.isCanonicalOpaque(Texture.from(
                Key.parse("minecraft:block/opaque"),
                opaqueImage
        )));
        assertFalse(CamoMaterialResolver.isCanonicalOpaque(Texture.from(
                Key.parse("minecraft:block/cutout"),
                cutoutImage
        )));
        assertFalse(CamoMaterialResolver.isCanonicalOpaque(Texture.from(
                Key.parse("minecraft:block/translucent"),
                translucentImage
        )));
    }

    @Test
    void rejectsWeightedOrRandomBlockstateVariants() {
        Variant first = new Variant(MODEL_PATH);
        Variant second = new Variant(new ResourcePath<>("minecraft:block/test_cube_two"));
        BlockState state = BlockState.fromString("minecraft:test_block");

        assertSame(first, CamoMaterialResolver.selectSingleVariant(
                resource(new VariantSet(first)),
                state
        ));
        assertNull(CamoMaterialResolver.selectSingleVariant(
                resource(new VariantSet(first, second)),
                state
        ));
    }

    @Test
    void weightedUniformLaneAllowsOnlyUnambiguousQuarterTurnCubeTransforms() {
        Model mirrored = model(
                Vector3f.ZERO,
                new Vector3f(16F, 16F, 16F),
                0,
                true,
                0,
                -1,
                new Vector4f(16F, 0F, 0F, 16F),
                null
        );

        assertTrue(CamoMaterialResolver.isUniformOpaqueFullCubeVariant(
                new Variant(MODEL_PATH, 0F, 180F, 0F),
                mirrored
        ));
        assertFalse(CamoMaterialResolver.isUniformOpaqueFullCubeVariant(
                new Variant(MODEL_PATH, 0F, 180F, 0F, true, 1D),
                mirrored
        ));
        assertFalse(CamoMaterialResolver.isUniformOpaqueFullCubeVariant(
                new Variant(MODEL_PATH, 0F, 45F, 0F),
                mirrored
        ));
        assertFalse(CamoMaterialResolver.isUniformOpaqueFullCubeVariant(
                new Variant(MODEL_PATH, 0F, 180F, 0F, false, 0D),
                mirrored
        ));
    }

    @Test
    void normalizesMinecraft1211StyleWeightedUniformCubeMaterials() throws IOException {
        ResourcePack resourcePack = new ResourcePack(new PackVersion(34, 0));
        Key blockId = Key.parse("test:randomized_stone");
        Key textureId = Key.parse("minecraft:block/stone");
        Key normalModelId = Key.parse("test:block/stone");
        Key mirroredModelId = Key.parse("test:block/stone_mirrored");
        putOpaqueTexture(resourcePack, textureId);

        Model normal = model(
                Vector3f.ZERO,
                new Vector3f(16F, 16F, 16F),
                0,
                true,
                0,
                -1,
                new Vector4f(0F, 0F, 16F, 16F),
                textureId
        );
        Model mirrored = model(
                Vector3f.ZERO,
                new Vector3f(16F, 16F, 16F),
                0,
                true,
                0,
                -1,
                new Vector4f(16F, 0F, 0F, 16F),
                textureId
        );
        putModel(resourcePack, normalModelId, normal);
        putModel(resourcePack, mirroredModelId, mirrored);
        putDefaultVariants(
                resourcePack,
                blockId,
                new Variant(new ResourcePath<Model>(normalModelId)),
                new Variant(new ResourcePath<Model>(mirroredModelId)),
                new Variant(new ResourcePath<Model>(normalModelId), 0F, 180F, 0F),
                new Variant(new ResourcePath<Model>(mirroredModelId), 0F, 180F, 0F)
        );

        CamoMaterialResolver.MaterialPalette palette = new CamoMaterialResolver(resourcePack)
                .resolve(NormalizedCamo.block(new NormalizedBlockState(
                        blockId.getFormatted(),
                        Map.of()
                )), null);

        assertTrue(palette.resolved());
        assertEquals("ok", palette.reason());
        assertEquals(BlockState.fromString(blockId.getFormatted()), palette.tintState());
        for (Direction direction : Direction.values()) {
            assertEquals(textureId, palette.get(direction).texture());
        }
    }

    @Test
    void rejectsWeightedCubeAlternativesWithDifferentMaterials() throws IOException {
        ResourcePack resourcePack = new ResourcePack(new PackVersion(34, 0));
        Key blockId = Key.parse("test:materially_different_variants");
        Key firstTexture = Key.parse("test:block/first");
        Key secondTexture = Key.parse("test:block/second");
        Key firstModelId = Key.parse("test:block/first_model");
        Key secondModelId = Key.parse("test:block/second_model");
        putOpaqueTexture(resourcePack, firstTexture);
        putOpaqueTexture(resourcePack, secondTexture);
        putModel(resourcePack, firstModelId, modelWithTexture(firstTexture));
        putModel(resourcePack, secondModelId, modelWithTexture(secondTexture));
        putDefaultVariants(
                resourcePack,
                blockId,
                new Variant(new ResourcePath<Model>(firstModelId)),
                new Variant(new ResourcePath<Model>(secondModelId))
        );

        CamoMaterialResolver.MaterialPalette palette = new CamoMaterialResolver(resourcePack)
                .resolve(NormalizedCamo.block(new NormalizedBlockState(
                        blockId.getFormatted(),
                        Map.of()
                )), null);

        assertFalse(palette.resolved());
        assertEquals("weighted-variant-materials-differ", palette.reason());
    }

    @Test
    void rejectsDynamicBlockPropertiesFromTheStaticOpaqueLane() {
        assertTrue(CamoMaterialResolver.isSimpleStaticProperties(
                properties(false, false)
        ));
        assertFalse(CamoMaterialResolver.isSimpleStaticProperties(
                properties(true, false)
        ));
        assertFalse(CamoMaterialResolver.isSimpleStaticProperties(
                properties(false, true)
        ));
        assertFalse(CamoMaterialResolver.isSimpleStaticProperties(null));
    }

    private static BlockProperties properties(
            boolean alwaysWaterlogged,
            boolean randomOffset
    ) {
        return BlockProperties.builder()
                .culling(true)
                .occluding(true)
                .alwaysWaterlogged(alwaysWaterlogged)
                .randomOffset(randomOffset)
                .build();
    }

    private static de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState
            resource(VariantSet defaultVariant) {
        return new de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState(
                new Variants(new VariantSet[0], defaultVariant)
        );
    }

    private static Model model(
            Vector3f from,
            Vector3f to,
            int faceRotation,
            boolean canonicalCullFaces
    ) {
        return model(from, to, faceRotation, canonicalCullFaces, 0);
    }

    private static Model model(
            Vector3f from,
            Vector3f to,
            int faceRotation,
            boolean canonicalCullFaces,
            int lightEmission
    ) {
        return model(from, to, faceRotation, canonicalCullFaces, lightEmission, -1);
    }

    private static Model model(
            Vector3f from,
            Vector3f to,
            int faceRotation,
            boolean canonicalCullFaces,
            int lightEmission,
            int tintIndex
    ) {
        return model(
                from,
                to,
                faceRotation,
                canonicalCullFaces,
                lightEmission,
                tintIndex,
                new Vector4f(0F, 0F, 16F, 16F),
                null
        );
    }

    private static Model modelWithTexture(Key textureId) {
        return model(
                Vector3f.ZERO,
                new Vector3f(16F, 16F, 16F),
                0,
                true,
                0,
                -1,
                new Vector4f(0F, 0F, 16F, 16F),
                textureId
        );
    }

    private static Model model(
            Vector3f from,
            Vector3f to,
            int faceRotation,
            boolean canonicalCullFaces,
            int lightEmission,
            int tintIndex,
            Vector4f uv,
            Key uniformTexture
    ) {
        EnumMap<Direction, Face> faces = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            ResourcePath<Texture> texture = uniformTexture == null
                    ? new ResourcePath<>(
                            "minecraft:block/test_"
                                    + direction.name().toLowerCase(java.util.Locale.ROOT)
                    )
                    : new ResourcePath<>(uniformTexture);
            faces.put(direction, new Face(
                    uv,
                    new TextureVariable(texture),
                    canonicalCullFaces ? direction : null,
                    faceRotation,
                    tintIndex
            ));
        }
        Element element = new Element(
                from,
                to,
                Rotation.ZERO,
                true,
                lightEmission,
                faces
        );
        return new Model(Map.of(), new Element[]{element}, true);
    }

    private static void putOpaqueTexture(ResourcePack resourcePack, Key textureId)
            throws IOException {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, 0xFFFFFFFF);
        resourcePack.getTextures().put(textureId, Texture.from(textureId, image));
    }

    private static void putModel(ResourcePack resourcePack, Key modelId, Model model) {
        model.calculateProperties(resourcePack.getTextures());
        resourcePack.getModels().put(modelId, model);
    }

    private static void putDefaultVariants(
            ResourcePack resourcePack,
            Key blockId,
            Variant... variants
    ) {
        resourcePack.getBlockStates().put(
                blockId,
                resource(new VariantSet(variants))
        );
    }
}

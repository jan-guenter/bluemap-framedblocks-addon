/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap522;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModel;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.ResourceModelRenderer;
import de.bluecolored.bluemap.core.map.hires.block.color.BlockColorCalculator;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Direction;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.LightData;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import de.bluecolored.bluemap.core.world.block.ExtendedBlock;
import io.github.janguenter.bluemap.framedblocks.diagnostics.BoundedDiagnostics;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.CamoDecodeResult;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.FramedCamoDecoder;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.FramedBlocks1061Support;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.GeometryTemplateProfile;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.NormalizedCamo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Generic exact-profile renderer for every client-wrapped FramedBlocks block. */
final class FramedGeometryRenderer implements BlockRenderer {

    private static final Key FRAME_PLACEHOLDER =
            Key.parse("framedblocks:block/framed_block");
    private static final Set<String> ITEM_FRAME_IDS = Set.of(
            "framedblocks:framed_item_frame",
            "framedblocks:framed_glowing_item_frame"
    );
    private static final Set<String> MANUAL_BODY_IDS = Set.of(
            "framedblocks:framed_chest",
            "framedblocks:framed_flower_pot",
            "framedblocks:framed_sign",
            "framedblocks:framed_wall_sign",
            "framedblocks:framed_hanging_sign",
            "framedblocks:framed_wall_hanging_sign"
    );
    private static final Map<String, String> ADJUSTABLE_SURROGATES = Map.of(
            "framedblocks:framed_adj_double_panel",
            "framedblocks:framed_double_panel",
            "framedblocks:framed_adj_double_copycat_panel",
            "framedblocks:framed_double_panel",
            "framedblocks:framed_adj_double_slab",
            "framedblocks:framed_double_slab",
            "framedblocks:framed_adj_double_copycat_slab",
            "framedblocks:framed_double_slab"
    );
    private static final BlockState CUBE_SURROGATE = new BlockState(
            Key.parse("framedblocks:framed_cube"),
            Map.of(
                    "alt", "false",
                    "glowing", "false",
                    "propagates_skylight", "false",
                    "reinforced", "false",
                    "solid", "false",
                    "solid_bg", "false"
            )
    );

    private final ResourcePack resourcePack;
    private final TextureGallery textureGallery;
    private final RenderSettings renderSettings;
    private final AdapterActivation activation;
    private final ResourceModelRenderer stockRenderer;
    private final BlockColorCalculator blockColorCalculator;
    private final CamoMaterialResolver materialResolver;
    private final FramedCamoDecoder camoDecoder = new FramedCamoDecoder();

    FramedGeometryRenderer(
            ResourcePack resourcePack,
            TextureGallery textureGallery,
            RenderSettings renderSettings,
            AdapterActivation activation
    ) {
        this.resourcePack = resourcePack;
        this.textureGallery = textureGallery;
        this.renderSettings = renderSettings;
        this.activation = activation;
        this.stockRenderer = new ResourceModelRenderer(resourcePack, textureGallery, renderSettings);
        this.blockColorCalculator = resourcePack.createBlockColorCalculator();
        this.materialResolver = new CamoMaterialResolver(resourcePack);
    }

    @Override
    public void render(
            BlockNeighborhood block,
            Variant ignoredVariant,
            TileModelView tileModel,
            Color blockColor
    ) {
        int renderStart = tileModel.getStart();
        GeometryTemplateProfile profile = activation.geometryProfile().orElse(null);
        if (!activation.isActive() || profile == null) {
            renderOriginalSafely(block, tileModel, blockColor, renderStart);
            return;
        }

        try {
            FramedBlocks1061Support.Classification support = profile.support(
                    block.getBlockState()
            ).orElse(null);
            if (support == null) {
                fallback("unclassified-state", block, tileModel, blockColor, renderStart);
                return;
            }
            if (support.routed() && hasUnsupportedFramedNeighbor(block)) {
                fallback("neighbor-hidden-face-model-data-required",
                        block, tileModel, blockColor, renderStart);
                return;
            }

            GeometryTemplateProfile.StateTemplate template = profile.find(block.getBlockState())
                    .orElse(null);
            if (template == null) {
                fallback("geometry-state-missing", block, tileModel, blockColor, renderStart);
                return;
            }

            FramedBlockEntityData blockEntity = block.getBlockEntity()
                    instanceof FramedBlockEntityData data ? data : null;
            if (blockEntity == null) {
                fallback("missing-framed-block-entity", block, tileModel, blockColor, renderStart);
                return;
            }
            if (!blockEntity.hasRequiredBaseFields()) {
                fallback("missing-required-framed-nbt",
                        block, tileModel, blockColor, renderStart);
                return;
            }
            if (blockEntity.getUpdated() != 3) {
                fallback("unsupported-framed-nbt-version", block, tileModel, blockColor, renderStart);
                return;
            }
            if (blockEntity.isReinforced()) {
                fallback("reinforcement-model-data-required",
                        block, tileModel, blockColor, renderStart);
                return;
            }

            boolean requiresDyeOverlay = template.quads().stream()
                    .anyMatch(quad -> "fixed".equals(quad.component())
                            && quad.tintIndex() == 1_024);
            if (requiresDyeOverlay
                    && (!blockEntity.hasOverlayColor()
                            || blockEntity.getOverlayColor() < 0
                            || blockEntity.getOverlayColor() > 15)) {
                fallback("invalid-or-missing-overlay-color",
                        block, tileModel, blockColor, renderStart);
                return;
            }

            PaletteResolution primaryResolution = decodePalette(
                    blockEntity.getCamo(),
                    "primary-camo",
                    block
            );
            if (!primaryResolution.success()) {
                fallback(primaryResolution.reason(), block, tileModel, blockColor, renderStart);
                return;
            }

            if (!support.routed()) {
                renderFallbackFamily(
                        profile,
                        support,
                        primaryResolution.palette(),
                        blockEntity,
                        block,
                        tileModel,
                        blockColor,
                        renderStart
                );
                return;
            }

            boolean requiresSecondary = template.quads().stream()
                    .anyMatch(quad -> "secondary".equals(quad.component()));
            PaletteResolution secondaryResolution = requiresSecondary
                    ? decodePalette(blockEntity.getCamoTwo(), "secondary-camo", block)
                    : PaletteResolution.success(
                            CamoMaterialResolver.MaterialPalette.emptyPalette()
                    );
            if (!secondaryResolution.success()) {
                fallback(secondaryResolution.reason(), block, tileModel, blockColor, renderStart);
                return;
            }

            blockColor.set(0F, 0F, 0F, 0F, true);
            float colorOpacity = renderTemplate(
                    template,
                    primaryResolution.palette(),
                    secondaryResolution.palette(),
                    blockEntity,
                    block,
                    tileModel,
                    blockColor
            );
            if (blockColor.a > 0F) {
                blockColor.flatten().straight();
                blockColor.a = colorOpacity;
            }
            tileModel.initialize(renderStart);
        } catch (RuntimeException exception) {
            BoundedDiagnostics.warning(
                    "geometry-render-exception",
                    "BlueMap FramedBlocks geometry rendering failed; using the stock resource."
            );
            fallback("geometry-render-exception", block, tileModel, blockColor, renderStart);
        }
    }

    private void renderFallbackFamily(
            GeometryTemplateProfile profile,
            FramedBlocks1061Support.Classification support,
            CamoMaterialResolver.MaterialPalette primary,
            FramedBlockEntityData blockEntity,
            BlockNeighborhood block,
            TileModelView tileModel,
            Color blockColor,
            int renderStart
    ) {
        String blockId = block.getBlockState().getId().getFormatted();
        String surrogateId = ADJUSTABLE_SURROGATES.get(blockId);
        if (surrogateId != null) {
            GeometryTemplateProfile.StateTemplate surrogate = profile.find(new BlockState(
                    Key.parse(surrogateId),
                    block.getBlockState().getProperties()
            )).orElse(null);
            PaletteResolution secondary = decodePalette(
                    blockEntity.getCamoTwo(),
                    "secondary-camo",
                    block
            );
            if (surrogate == null || !secondary.success()) {
                fallback(
                        surrogate == null ? "adjustable-surrogate-state-missing"
                                : secondary.reason(),
                        block,
                        tileModel,
                        blockColor,
                        renderStart
                );
                return;
            }
            renderTemplateAndFinish(
                    surrogate,
                    primary,
                    secondary.palette(),
                    blockEntity,
                    block,
                    tileModel,
                    blockColor,
                    renderStart
            );
            return;
        }

        CamoMaterialResolver.Material uniform = uniformMaterial(primary);
        if (uniform == null) {
            fallback(
                    support.reason() + "-directional-camo-unsupported",
                    block,
                    tileModel,
                    blockColor,
                    renderStart
            );
            return;
        }

        if (MANUAL_BODY_IDS.contains(blockId)) {
            renderManualBody(
                    profile,
                    primary,
                    blockEntity,
                    block,
                    tileModel,
                    blockColor,
                    renderStart
            );
            return;
        }

        Color tint = calculateTint(
                new SelectedMaterial(uniform, primary.tintState(), uniform.tintIndex() >= 0),
                blockEntity,
                block
        );
        Set<Integer> placeholderMaterials = new HashSet<>();
        placeholderMaterials.add(textureGallery.get(FRAME_PLACEHOLDER));
        if (ITEM_FRAME_IDS.contains(blockId)) {
            placeholderMaterials.add(textureGallery.get(ResourcePack.MISSING_TEXTURE));
        }
        int camoMaterial = textureGallery.get(uniform.texture());
        tileModel.initialize(renderStart).reset();
        blockColor.set(0F, 0F, 0F, 0F, true);
        SubstitutedRender result = renderSubstitutedOriginal(
                block,
                tileModel,
                blockColor,
                placeholderMaterials,
                camoMaterial,
                uniform.lightEmission(),
                tint
        );
        if (!result.rendered() || !result.substituted()) {
            fallback(
                    result.rendered()
                            ? support.reason() + "-placeholder-missing"
                            : support.reason() + "-stock-model-missing",
                    block,
                    tileModel,
                    blockColor,
                    renderStart
            );
            return;
        }
        setCamoMapColor(uniform, tint, blockEntity, block, blockColor);
        tileModel.initialize(renderStart);
    }

    private void renderManualBody(
            GeometryTemplateProfile profile,
            CamoMaterialResolver.MaterialPalette primary,
            FramedBlockEntityData blockEntity,
            BlockNeighborhood block,
            TileModelView tileModel,
            Color blockColor,
            int renderStart
    ) {
        GeometryTemplateProfile.StateTemplate cube = profile.find(CUBE_SURROGATE).orElse(null);
        if (cube == null) {
            fallback("manual-body-surrogate-missing", block, tileModel, blockColor, renderStart);
            return;
        }

        String blockId = block.getBlockState().getId().getFormatted();
        tileModel.initialize(renderStart).reset();
        blockColor.set(0F, 0F, 0F, 0F, true);
        if ("framedblocks:framed_chest".equals(blockId)
                || "framedblocks:framed_hanging_sign".equals(blockId)
                || "framedblocks:framed_wall_hanging_sign".equals(blockId)) {
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState original =
                    resourcePack.getBlockStates().get(block.getBlockState().getId());
            renderResource(original, block.getBlockState(), block, tileModel, blockColor);
        }

        if ("framedblocks:framed_flower_pot".equals(blockId)) {
            renderScaledCube(cube, primary, blockEntity, block, tileModel, blockColor,
                    0.375F, 0.375F, 0.375F, 0.3125F, 0F, 0.3125F);
            renderFlower(blockEntity, block, tileModel, blockColor);
        } else if ("framedblocks:framed_sign".equals(blockId)) {
            renderScaledCube(cube, primary, blockEntity, block, tileModel, blockColor,
                    0.75F, 0.5F, 0.125F, 0.125F, 0.45F, 0.4375F);
            renderScaledCube(cube, primary, blockEntity, block, tileModel, blockColor,
                    0.125F, 0.45F, 0.125F, 0.4375F, 0F, 0.4375F);
        } else if ("framedblocks:framed_wall_sign".equals(blockId)) {
            renderScaledCube(cube, primary, blockEntity, block, tileModel, blockColor,
                    0.75F, 0.5F, 0.125F, 0.125F, 0.25F, 0.75F);
        } else if ("framedblocks:framed_chest".equals(blockId)) {
            renderScaledCube(cube, primary, blockEntity, block, tileModel, blockColor,
                    0.875F, 0.875F, 0.875F, 0.0625F, 0F, 0.0625F);
        } else {
            renderScaledCube(cube, primary, blockEntity, block, tileModel, blockColor,
                    0.75F, 0.5F, 0.125F, 0.125F, 0.25F, 0.4375F);
        }

        if (blockColor.a > 0F) {
            blockColor.flatten().straight();
        }
        tileModel.initialize(renderStart);
    }

    private void renderScaledCube(
            GeometryTemplateProfile.StateTemplate cube,
            CamoMaterialResolver.MaterialPalette primary,
            FramedBlockEntityData blockEntity,
            BlockNeighborhood block,
            TileModelView tileModel,
            Color blockColor,
            float scaleX,
            float scaleY,
            float scaleZ,
            float translateX,
            float translateY,
            float translateZ
    ) {
        int componentStart = tileModel.getTileModel().size();
        renderTemplate(
                cube,
                primary,
                CamoMaterialResolver.MaterialPalette.emptyPalette(),
                blockEntity,
                block,
                tileModel,
                blockColor
        );
        tileModel.initialize(componentStart)
                .scale(scaleX, scaleY, scaleZ)
                .translate(translateX, translateY, translateZ);
    }

    private void renderFlower(
            FramedBlockEntityData blockEntity,
            BlockNeighborhood block,
            TileModelView tileModel,
            Color blockColor
    ) {
        String flowerId = blockEntity.getFlower();
        if (flowerId == null || flowerId.isBlank()) {
            return;
        }
        BlockState flowerState;
        try {
            flowerState = BlockState.fromString(flowerId);
        } catch (IllegalArgumentException exception) {
            return;
        }
        de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState resource =
                resourcePack.getBlockStates().get(flowerState.getId());
        int flowerStart = tileModel.getTileModel().size();
        if (renderResource(resource, flowerState, block, tileModel, blockColor)) {
            tileModel.initialize(flowerStart)
                    .scale(0.75F, 0.75F, 0.75F)
                    .translate(0.125F, 0.375F, 0.125F);
        }
    }

    private void renderTemplateAndFinish(
            GeometryTemplateProfile.StateTemplate template,
            CamoMaterialResolver.MaterialPalette primary,
            CamoMaterialResolver.MaterialPalette secondary,
            FramedBlockEntityData blockEntity,
            BlockNeighborhood block,
            TileModelView tileModel,
            Color blockColor,
            int renderStart
    ) {
        blockColor.set(0F, 0F, 0F, 0F, true);
        float colorOpacity = renderTemplate(
                template,
                primary,
                secondary,
                blockEntity,
                block,
                tileModel,
                blockColor
        );
        if (blockColor.a > 0F) {
            blockColor.flatten().straight();
            blockColor.a = colorOpacity;
        }
        tileModel.initialize(renderStart);
    }

    private static CamoMaterialResolver.Material uniformMaterial(
            CamoMaterialResolver.MaterialPalette palette
    ) {
        CamoMaterialResolver.Material uniform = null;
        for (Direction direction : Direction.values()) {
            CamoMaterialResolver.Material material = palette.get(direction);
            if (material == null || uniform != null && !uniform.equals(material)) {
                return null;
            }
            uniform = material;
        }
        return uniform;
    }

    private void setCamoMapColor(
            CamoMaterialResolver.Material material,
            Color tint,
            FramedBlockEntityData blockEntity,
            BlockNeighborhood block,
            Color blockColor
    ) {
        Texture texture = materialResolver.texture(material);
        if (texture == null) {
            return;
        }
        Color mapColor = new Color().set(texture.getColorPremultiplied());
        mapColor.multiply(tint);
        LightLevels light = calculateBaseLight(Direction.UP, material, blockEntity, block);
        float combinedLight = Math.max(light.sunlight() / 15F, light.blocklight() / 15F);
        combinedLight = (1F - renderSettings.getAmbientLight()) * combinedLight
                + renderSettings.getAmbientLight();
        mapColor.r *= combinedLight;
        mapColor.g *= combinedLight;
        mapColor.b *= combinedLight;
        blockColor.set(0F, 0F, 0F, 0F, true);
        blockColor.add(mapColor);
        if (blockColor.a > 0F) {
            blockColor.flatten().straight();
            blockColor.a = mapColor.a;
        }
    }

    private float renderTemplate(
            GeometryTemplateProfile.StateTemplate template,
            CamoMaterialResolver.MaterialPalette primary,
            CamoMaterialResolver.MaterialPalette secondary,
            FramedBlockEntityData blockEntity,
            BlockNeighborhood block,
            TileModelView tileModel,
            Color blockColor
    ) {
        float colorOpacity = 0F;
        for (GeometryTemplateProfile.QuadTemplate quad : template.quads()) {
            Direction direction = Direction.fromString(quad.direction());
            float upwardNormal = upwardNormalComponent(quad, direction);
            if (!shouldRender(quad, upwardNormal, block)) {
                continue;
            }

            SelectedMaterial selected = selectMaterial(quad, primary, secondary);
            Color tint = calculateTint(selected, blockEntity, block);
            LightLevels baseLight = calculateBaseLight(
                    direction,
                    selected.material(),
                    blockEntity,
                    block
            );
            LightLevels faceLight = withExportedLight(baseLight, quad, 0, 1, 2, 3);
            if (isHiddenCave(block, faceLight)) {
                continue;
            }
            emitQuad(quad, selected.material(), tint, baseLight, block, tileModel);

            if (upwardNormal > 0.01F) {
                Texture texture = materialResolver.texture(selected.material());
                if (texture != null) {
                    Color mapColor = new Color().set(texture.getColorPremultiplied());
                    mapColor.multiply(tint);
                    float combinedLight = Math.max(
                            faceLight.sunlight() / 15F,
                            faceLight.blocklight() / 15F
                    );
                    combinedLight = (1F - renderSettings.getAmbientLight()) * combinedLight
                            + renderSettings.getAmbientLight();
                    mapColor.r *= combinedLight;
                    mapColor.g *= combinedLight;
                    mapColor.b *= combinedLight;
                    colorOpacity = Math.max(colorOpacity, mapColor.a);
                    blockColor.add(mapColor);
                }
            }
        }
        return colorOpacity;
    }

    private boolean shouldRender(
            GeometryTemplateProfile.QuadTemplate quad,
            float upwardNormal,
            BlockNeighborhood block
    ) {
        if (renderSettings.isRenderTopOnly() && upwardNormal < 0.01F) {
            return false;
        }
        if ("none".equals(quad.cullFace())) {
            return true;
        }

        Direction cullDirection = Direction.fromString(quad.cullFace());
        ExtendedBlock neighbor = neighbor(block, cullDirection);
        if (neighbor.getProperties().isCulling()) {
            return false;
        }
        return !(neighbor.getProperties().getCullingIdentical()
                && neighbor.getBlockState().equals(block.getBlockState()));
    }

    private PaletteResolution decodePalette(
            Object raw,
            String slot,
            BlockNeighborhood block
    ) {
        CamoDecodeResult decoded = camoDecoder.decode(raw);
        NormalizedCamo camo = decoded.camo().orElse(null);
        if (camo == null) {
            return PaletteResolution.failure(
                    slot + "-" + decoded.reason()
            );
        }
        CamoMaterialResolver.MaterialPalette palette = materialResolver.resolve(camo, block);
        if (!palette.resolved()) {
            return PaletteResolution.failure(slot + "-" + palette.reason());
        }
        if (!isAppliedCamoPalette(palette)) {
            // The bundled profile is intentionally scoped to geometry baked with an
            // applied camouflage. Reusing its diagnostic sentinel quads for an empty
            // slot would leak exporter-only textures into the map.
            return PaletteResolution.failure(slot + "-empty-camo-profile-unsupported");
        }
        return PaletteResolution.success(palette);
    }

    static boolean isAppliedCamoPalette(CamoMaterialResolver.MaterialPalette palette) {
        return palette.resolved() && !palette.empty();
    }

    private static SelectedMaterial selectMaterial(
            GeometryTemplateProfile.QuadTemplate quad,
            CamoMaterialResolver.MaterialPalette primary,
            CamoMaterialResolver.MaterialPalette secondary
    ) {
        if ("primary".equals(quad.component())) {
            Direction sourceDirection = Direction.fromString(quad.sourceFace());
            CamoMaterialResolver.Material material = primary.get(sourceDirection);
            return new SelectedMaterial(material, primary.tintState(), material.tintIndex() >= 0);
        }
        if ("secondary".equals(quad.component())) {
            Direction sourceDirection = Direction.fromString(quad.sourceFace());
            CamoMaterialResolver.Material material = secondary.get(sourceDirection);
            return new SelectedMaterial(material, secondary.tintState(), material.tintIndex() >= 0);
        }
        CamoMaterialResolver.Material material = new CamoMaterialResolver.Material(
                Key.parse(quad.sprite()),
                quad.tintIndex(),
                0
        );
        return new SelectedMaterial(material, null, quad.tintIndex() != -1);
    }

    private Color calculateTint(
            SelectedMaterial selected,
            FramedBlockEntityData blockEntity,
            BlockNeighborhood block
    ) {
        Color tint = new Color().set(1F, 1F, 1F, 1F, true);
        if (!selected.tinted()) {
            return tint;
        }
        if (selected.material().tintIndex() == 1_024 && blockEntity != null) {
            return dyeColor(blockEntity.getOverlayColor(), tint);
        }
        BlockState tintState = selected.tintState() == null
                ? block.getBlockState()
                : selected.tintState();
        blockColorCalculator.getBlockColor(block, tintState, tint);
        return tint;
    }

    private LightLevels calculateBaseLight(
            Direction direction,
            CamoMaterialResolver.Material material,
            FramedBlockEntityData blockEntity,
            BlockNeighborhood block
    ) {
        LightData own = block.getLightData();
        LightData adjacent = neighbor(block, direction).getLightData();
        int sunlight = Math.max(own.getSkyLight(), adjacent.getSkyLight());
        int blocklight = Math.max(own.getBlockLight(), adjacent.getBlockLight());
        blocklight = Math.max(blocklight, material.lightEmission());

        String blockId = block.getBlockState().getId().getFormatted();
        if ((blockEntity != null && blockEntity.isGlowing())
                || "framedblocks:framed_glowing_cube".equals(blockId)) {
            blocklight = 15;
        } else if ("framedblocks:framed_glowing_item_frame".equals(blockId)) {
            blocklight = Math.max(blocklight, 5);
        }
        return new LightLevels(sunlight, blocklight);
    }

    private static LightLevels withExportedLight(
            LightLevels base,
            GeometryTemplateProfile.QuadTemplate quad,
            int... vertexIndices
    ) {
        int sunlight = Math.max(base.sunlight(), quad.skyLight());
        int blocklight = Math.max(base.blocklight(), quad.blockLight());
        for (int vertexIndex : vertexIndices) {
            GeometryTemplateProfile.Vertex vertex = quad.vertices().get(vertexIndex);
            sunlight = Math.max(sunlight, vertex.skyLight());
            blocklight = Math.max(blocklight, vertex.blockLight());
        }
        return new LightLevels(sunlight, blocklight);
    }

    private boolean isHiddenCave(BlockNeighborhood block, LightLevels light) {
        return block.isRemoveIfCave()
                && (renderSettings.isCaveDetectionUsesBlockLight()
                        ? Math.max(light.blocklight(), light.sunlight())
                        : light.sunlight()) == 0;
    }

    private static Color dyeColor(int id, Color target) {
        int color = dyeTextColor(id);
        return target.set(
                (color >> 16 & 255) / 255F,
                (color >> 8 & 255) / 255F,
                (color & 255) / 255F,
                1F,
                true
        );
    }

    static int dyeTextColor(int id) {
        int[] colors = {
                0xFFFFFF, 0xFF681F, 0xFF00FF, 0x9AC0CD,
                0xFFFF00, 0xBFFF00, 0xFF69B4, 0x808080,
                0xD3D3D3, 0x00FFFF, 0xA020F0, 0x0000FF,
                0x8B4513, 0x00FF00, 0xFF0000, 0x000000
        };
        if (id < 0 || id >= colors.length) {
            throw new IllegalArgumentException("Dye color ID is outside the exact 1.21.1 range");
        }
        return colors[id];
    }

    private void emitQuad(
            GeometryTemplateProfile.QuadTemplate quad,
            CamoMaterialResolver.Material material,
            Color tint,
            LightLevels baseLight,
            BlockNeighborhood block,
            TileModelView tileModel
    ) {
        tileModel.initialize();
        tileModel.add(2);
        TileModel model = tileModel.getTileModel();
        int first = tileModel.getStart();
        int second = first + 1;
        List<GeometryTemplateProfile.Vertex> vertices = quad.vertices();
        setTriangle(model, first, vertices.get(0), vertices.get(1), vertices.get(2));
        setTriangle(model, second, vertices.get(0), vertices.get(2), vertices.get(3));

        int texture = textureGallery.get(material.texture());
        model.setMaterialIndex(first, texture);
        model.setMaterialIndex(second, texture);
        model.setColor(first, tint.r, tint.g, tint.b);
        model.setColor(second, tint.r, tint.g, tint.b);
        LightLevels firstLight = withExportedLight(baseLight, quad, 0, 1, 2);
        LightLevels secondLight = withExportedLight(baseLight, quad, 0, 2, 3);
        model.setSunlight(first, firstLight.sunlight());
        model.setSunlight(second, secondLight.sunlight());
        model.setBlocklight(first, firstLight.blocklight());
        model.setBlocklight(second, secondLight.blocklight());

        float firstAo = 1F;
        float secondAo = 1F;
        float thirdAo = 1F;
        float fourthAo = 1F;
        if (quad.ambientOcclusion()
                && quad.effectiveAmbientOcclusionUnderZeroEmission()) {
            Direction direction = Direction.fromString(quad.direction());
            firstAo = testAo(vertices.get(0), direction, block);
            secondAo = testAo(vertices.get(1), direction, block);
            thirdAo = testAo(vertices.get(2), direction, block);
            fourthAo = testAo(vertices.get(3), direction, block);
        }
        model.setAOs(first, firstAo, secondAo, thirdAo);
        model.setAOs(second, firstAo, thirdAo, fourthAo);
    }

    private static void setTriangle(
            TileModel model,
            int face,
            GeometryTemplateProfile.Vertex first,
            GeometryTemplateProfile.Vertex second,
            GeometryTemplateProfile.Vertex third
    ) {
        model.setPositions(
                face,
                first.x(), first.y(), first.z(),
                second.x(), second.y(), second.z(),
                third.x(), third.y(), third.z()
        );
        model.setUvs(
                face,
                first.u(), first.v(),
                second.u(), second.v(),
                third.u(), third.v()
        );
    }

    private static ExtendedBlock neighbor(BlockNeighborhood block, Direction direction) {
        return neighbor(
                block,
                direction.toVector().getX(),
                direction.toVector().getY(),
                direction.toVector().getZ()
        );
    }

    private static ExtendedBlock neighbor(
            BlockNeighborhood block,
            int x,
            int y,
            int z
    ) {
        return block.getNeighborBlock(x, y, z);
    }

    private static float testAo(
            GeometryTemplateProfile.Vertex vertex,
            Direction direction,
            BlockNeighborhood block
    ) {
        int x = boundaryOffset(vertex.x());
        int y = boundaryOffset(vertex.y());
        int z = boundaryOffset(vertex.z());
        int directionX = direction.toVector().getX();
        int directionY = direction.toVector().getY();
        int directionZ = direction.toVector().getZ();
        int occluding = 0;

        if (x * directionX + y * directionY > 0
                && neighbor(block, x, y, 0).getProperties().isOccluding()) {
            occluding++;
        }
        if (x * directionX + z * directionZ > 0
                && neighbor(block, x, 0, z).getProperties().isOccluding()) {
            occluding++;
        }
        if (y * directionY + z * directionZ > 0
                && neighbor(block, 0, y, z).getProperties().isOccluding()) {
            occluding++;
        }
        if (x * directionX + y * directionY + z * directionZ > 0
                && neighbor(block, x, y, z).getProperties().isOccluding()) {
            occluding++;
        }
        return 1F - Math.min(occluding, 3) * 0.25F;
    }

    static int boundaryOffset(float coordinate) {
        float epsilon = 1.0E-5F;
        if (Math.abs(coordinate - 1F) <= epsilon) {
            return 1;
        }
        if (Math.abs(coordinate) <= epsilon) {
            return -1;
        }
        return 0;
    }

    static boolean hasUnsupportedFramedNeighbor(BlockNeighborhood block) {
        for (Direction direction : Direction.values()) {
            BlockState neighborState = neighbor(block, direction).getBlockState();
            if ("framedblocks".equals(neighborState.getId().getNamespace())
                    && !isDoorCompanion(block.getBlockState(), neighborState, direction)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isDoorCompanion(
            BlockState state,
            BlockState neighborState,
            Direction direction
    ) {
        String blockId = state.getId().getFormatted();
        if ((direction != Direction.UP && direction != Direction.DOWN)
                || !blockId.equals(neighborState.getId().getFormatted())
                || !Set.of(
                        "framedblocks:framed_door",
                        "framedblocks:framed_iron_door"
                ).contains(blockId)) {
            return false;
        }
        String half = state.getProperties().get("half");
        String neighborHalf = neighborState.getProperties().get("half");
        if (!("lower".equals(half) && "upper".equals(neighborHalf)
                || "upper".equals(half) && "lower".equals(neighborHalf))) {
            return false;
        }
        Map<String, String> properties = new HashMap<>(state.getProperties());
        Map<String, String> neighborProperties = new HashMap<>(neighborState.getProperties());
        properties.remove("half");
        neighborProperties.remove("half");
        return properties.equals(neighborProperties);
    }

    private static float upwardNormalComponent(
            GeometryTemplateProfile.QuadTemplate quad,
            Direction nearestDirection
    ) {
        GeometryTemplateProfile.Vertex first = quad.vertices().get(0);
        GeometryTemplateProfile.Vertex second = quad.vertices().get(1);
        GeometryTemplateProfile.Vertex third = quad.vertices().get(2);
        float ax = second.x() - first.x();
        float ay = second.y() - first.y();
        float az = second.z() - first.z();
        float bx = third.x() - first.x();
        float by = third.y() - first.y();
        float bz = third.z() - first.z();
        float nx = ay * bz - az * by;
        float ny = az * bx - ax * bz;
        float nz = ax * by - ay * bx;
        float length = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (length < 1.0E-9F) {
            return nearestDirection.toVector().getY();
        }
        float dot = nx * nearestDirection.toVector().getX()
                + ny * nearestDirection.toVector().getY()
                + nz * nearestDirection.toVector().getZ();
        return (dot < 0F ? -ny : ny) / length;
    }

    private void fallback(
            String reason,
            BlockNeighborhood block,
            TileModelView tileModel,
            Color blockColor,
            int renderStart
    ) {
        BoundedDiagnostics.warning(
                reason,
                "BlueMap FramedBlocks exact-profile state is unavailable (reason: "
                        + reason
                        + "); using the stock resource."
        );
        tileModel.initialize(renderStart).reset();
        blockColor.set(0F, 0F, 0F, 0F, true);
        renderOriginalSafely(block, tileModel, blockColor, renderStart);
    }

    private void renderOriginalSafely(
            BlockNeighborhood block,
            TileModelView tileModel,
            Color blockColor,
            int renderStart
    ) {
        try {
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState original =
                    resourcePack.getBlockStates().get(block.getBlockState().getId());
            if (renderResource(original, block.getBlockState(), block, tileModel, blockColor)) {
                return;
            }
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState missing =
                    resourcePack.getBlockStates().get(ResourcePack.MISSING_BLOCK_STATE);
            renderResource(missing, BlockState.MISSING, block, tileModel, blockColor);
        } catch (RuntimeException exception) {
            tileModel.initialize(renderStart).reset();
            blockColor.set(0F, 0F, 0F, 0F, true);
            BoundedDiagnostics.warning(
                    "stock-fallback-exception",
                    "BlueMap FramedBlocks stock fallback failed; this block was omitted."
            );
        }
    }

    private SubstitutedRender renderSubstitutedOriginal(
            BlockNeighborhood block,
            TileModelView tileModel,
            Color blockColor,
            Set<Integer> placeholderMaterials,
            int camoMaterial,
            int camoLightEmission,
            Color tint
    ) {
        de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState resource =
                resourcePack.getBlockStates().get(block.getBlockState().getId());
        if (resource == null) {
            return new SubstitutedRender(false, false);
        }
        int modelStart = tileModel.getStart();
        List<Variant> variants = new ArrayList<>();
        resource.forEach(
                block.getBlockState(),
                block.getX(),
                block.getY(),
                block.getZ(),
                variants::add
        );
        if (variants.isEmpty()) {
            return new SubstitutedRender(false, false);
        }

        float colorOpacity = 0F;
        boolean rendered = false;
        boolean substituted = false;
        for (Variant variant : variants) {
            if (variant.getModel().getResource(resourcePack.getModels()::get) == null) {
                continue;
            }
            CamoSubstitutionTileModel proxy = new CamoSubstitutionTileModel(
                    tileModel.getTileModel(),
                    placeholderMaterials,
                    camoMaterial,
                    camoLightEmission,
                    tint
            );
            Color variantColor = new Color().set(0F, 0F, 0F, 0F, true);
            stockRenderer.render(block, variant, new TileModelView(proxy), variantColor);
            rendered = true;
            substituted |= proxy.substituted();
            colorOpacity = Math.max(colorOpacity, variantColor.a);
            blockColor.add(variantColor.premultiplied());
        }
        if (blockColor.a > 0F) {
            blockColor.flatten().straight();
            blockColor.a = colorOpacity;
        }
        tileModel.initialize(modelStart);
        return new SubstitutedRender(rendered, substituted);
    }

    private boolean renderResource(
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState resource,
            BlockState state,
            BlockNeighborhood block,
            TileModelView tileModel,
            Color blockColor
    ) {
        if (resource == null) {
            return false;
        }
        int modelStart = tileModel.getStart();
        List<Variant> variants = new ArrayList<>();
        resource.forEach(state, block.getX(), block.getY(), block.getZ(), variants::add);
        if (variants.isEmpty()) {
            return false;
        }

        float colorOpacity = 0F;
        blockColor.set(0F, 0F, 0F, 0F, true);
        boolean rendered = false;
        for (Variant variant : variants) {
            if (variant.getModel().getResource(resourcePack.getModels()::get) == null) {
                continue;
            }
            Color variantColor = new Color().set(0F, 0F, 0F, 0F, true);
            stockRenderer.render(block, variant, tileModel.initialize(), variantColor);
            rendered = true;
            colorOpacity = Math.max(colorOpacity, variantColor.a);
            blockColor.add(variantColor.premultiplied());
        }
        if (!rendered) {
            tileModel.initialize(modelStart);
            return false;
        }
        if (blockColor.a > 0F) {
            blockColor.flatten().straight();
            blockColor.a = colorOpacity;
        }
        tileModel.initialize(modelStart);
        return true;
    }

    private record SelectedMaterial(
            CamoMaterialResolver.Material material,
            BlockState tintState,
            boolean tinted
    ) {
    }

    private record LightLevels(int sunlight, int blocklight) {
    }

    private record SubstitutedRender(boolean rendered, boolean substituted) {
    }

    private record PaletteResolution(
            CamoMaterialResolver.MaterialPalette palette,
            String reason
    ) {
        static PaletteResolution success(CamoMaterialResolver.MaterialPalette palette) {
            return new PaletteResolution(palette, "ok");
        }

        static PaletteResolution failure(String reason) {
            return new PaletteResolution(null, reason);
        }

        boolean success() {
            return palette != null;
        }
    }

}

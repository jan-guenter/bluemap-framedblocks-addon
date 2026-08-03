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
import java.util.List;

/** Generic exact-profile renderer for every client-wrapped FramedBlocks block. */
final class FramedGeometryRenderer implements BlockRenderer {

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
            if (support == null || !support.routed()) {
                fallback(
                        support == null ? "unclassified-state" : support.reason(),
                        block,
                        tileModel,
                        blockColor,
                        renderStart
                );
                return;
            }
            if (hasFramedNeighbor(block)) {
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

    static boolean hasFramedNeighbor(BlockNeighborhood block) {
        for (Direction direction : Direction.values()) {
            if ("framedblocks".equals(neighbor(block, direction)
                    .getBlockState()
                    .getId()
                    .getNamespace())) {
                return true;
            }
        }
        return false;
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

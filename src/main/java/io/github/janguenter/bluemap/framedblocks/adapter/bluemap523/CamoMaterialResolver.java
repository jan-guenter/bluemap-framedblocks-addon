/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap523;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector4f;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.ResourcePath;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.VariantSet;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variants;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Element;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Face;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Direction;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.NormalizedBlockState;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.NormalizedCamo;

import java.util.EnumMap;
import java.util.Map;

/** Resolves only camouflage models that the exact-profile renderer can reproduce safely. */
final class CamoMaterialResolver {

    private static final int MAX_WEIGHTED_VARIANTS = 16;
    private static final Vector3f FULL_BLOCK_MIN = Vector3f.ZERO;
    private static final Vector3f FULL_BLOCK_MAX = new Vector3f(16F, 16F, 16F);
    private static final Vector4f FULL_FACE_UV = new Vector4f(0F, 0F, 16F, 16F);
    private static final Key CAMOL_OVERLAY_RENDERER_KEY =
            Key.parse("bluemap_camol:overlay");
    private static final Material MISSING = new Material(ResourcePack.MISSING_TEXTURE, -1, 0);

    private final ResourcePack resourcePack;

    CamoMaterialResolver(ResourcePack resourcePack) {
        this.resourcePack = resourcePack;
    }

    MaterialPalette resolve(NormalizedCamo camo, BlockNeighborhood block) {
        return switch (camo.kind()) {
            case EMPTY -> MaterialPalette.emptyPalette();
            case BLOCK -> resolveBlock(camo.blockState().orElseThrow(), block);
            // Fluid appearance is supplied by NeoForge client extensions. Texture IDs alone
            // do not preserve the client tint, flow rotation, UVs, or render-layer behavior.
            case FLUID -> MaterialPalette.missing(
                    BlockState.MISSING,
                    "fluid-camo-client-renderer-required"
            );
        };
    }

    Texture texture(Material material) {
        return resourcePack.getTextures().get(material.texture());
    }

    private MaterialPalette resolveBlock(
            NormalizedBlockState normalized,
            BlockNeighborhood block
    ) {
        BlockState state = new BlockState(Key.parse(normalized.id()), normalized.properties());
        de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState resource =
                resourcePack.getBlockState(state);
        if (resource == null) {
            return MaterialPalette.missing(state, "blockstate-resource-missing");
        }

        VariantSet variantSet = selectVariantSet(resource, state);
        if (variantSet == null
                || variantSet.getVariants() == null
                || variantSet.getVariants().length == 0) {
            return MaterialPalette.missing(state, "blockstate-variant-set-unsupported");
        }
        de.bluecolored.bluemap.core.world.BlockProperties properties =
                resourcePack.getBlockProperties(state);
        if (!isSimpleStaticProperties(properties)) {
            return MaterialPalette.missing(state, "block-properties-not-static-opaque");
        }

        Variant[] variants = variantSet.getVariants();
        if (variants.length == 1) {
            return resolveDirectionalVariant(state, variants[0]);
        }
        if (variants.length > MAX_WEIGHTED_VARIANTS) {
            return MaterialPalette.missing(state, "weighted-variant-count-unsupported");
        }
        return resolveUniformVariantSet(state, variants);
    }

    private MaterialPalette resolveDirectionalVariant(BlockState state, Variant variant) {
        Model model = variant == null
                ? null
                : variant.getModel().getResource(resourcePack.getModels()::get);
        if (!isSimpleStaticCube(variant, model)
                || !model.isOccluding()
                || !model.isCulling()) {
            return MaterialPalette.missing(state, "single-variant-model-unsupported");
        }
        EnumMap<Direction, Material> materials = resolveMaterials(model);
        if (materials == null) {
            return MaterialPalette.missing(state, "single-variant-material-unsupported");
        }
        return MaterialPalette.directional(state, materials);
    }

    private MaterialPalette resolveUniformVariantSet(BlockState state, Variant[] variants) {
        // FramedBlocks does not persist the random BakedModel alternative that its client
        // cache selected. For uniform full-cube materials only, normalize mirror/quarter-turn
        // UV differences while preserving the texture, tint and emission. This is intentionally
        // material-correct rather than pixel-identical for directional texture pixels.
        Material common = null;
        for (Variant variant : variants) {
            Model model = variant == null
                    ? null
                    : variant.getModel().getResource(resourcePack.getModels()::get);
            if (!isUniformOpaqueFullCubeVariant(variant, model)
                    || !model.isOccluding()
                    || !model.isCulling()) {
                return MaterialPalette.missing(state, "weighted-variant-model-unsupported");
            }
            EnumMap<Direction, Material> materials = resolveMaterials(model);
            if (materials == null) {
                return MaterialPalette.missing(state, "weighted-variant-material-unsupported");
            }
            Material uniform = uniformMaterial(materials);
            if (uniform == null) {
                return MaterialPalette.missing(state, "weighted-variant-material-not-uniform");
            }
            Texture texture = resourcePack.getTextures().get(uniform.texture());
            if (texture == null || texture.getAnimation() != null) {
                return MaterialPalette.missing(state, "weighted-variant-animation-unsupported");
            }
            if (common != null && !common.equals(uniform)) {
                return MaterialPalette.missing(state, "weighted-variant-materials-differ");
            }
            common = uniform;
        }
        if (common == null) {
            return MaterialPalette.missing(state, "blockstate-variant-set-unsupported");
        }
        return MaterialPalette.uniform(state, common);
    }

    private EnumMap<Direction, Material> resolveMaterials(Model model) {
        Element element = model.getElements()[0];
        EnumMap<Direction, Material> materials = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            Face face = element.getFaces().get(direction);
            ResourcePath<Texture> texture = face.getTexture()
                    .getTexturePath(model.getTextures()::get);
            Texture resolvedTexture = texture == null
                    ? null : resourcePack.getTextures().get(texture);
            if (texture == null
                    || ResourcePack.MISSING_TEXTURE.equals(texture)
                    || !isCanonicalOpaque(resolvedTexture)) {
                return null;
            }
            materials.put(direction, new Material(
                    texture,
                    face.getTintindex(),
                    element.getLightEmission()
            ));
        }
        return materials;
    }

    private static Material uniformMaterial(EnumMap<Direction, Material> materials) {
        Material common = null;
        for (Direction direction : Direction.values()) {
            Material material = materials.get(direction);
            if (material == null) {
                return null;
            }
            if (common != null && !common.equals(material)) {
                return null;
            }
            common = material;
        }
        return common;
    }

    static VariantSet selectVariantSet(
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState resource,
            BlockState state
    ) {
        if (resource == null || resource.getMultipart() != null) {
            return null;
        }
        Variants variants = resource.getVariants();
        if (variants == null) {
            return null;
        }

        VariantSet selected = null;
        for (VariantSet candidate : variants.getVariants()) {
            if (candidate.getCondition().matches(state)) {
                selected = candidate;
                break;
            }
        }
        if (selected == null) {
            selected = variants.getDefaultVariant();
        }
        return selected;
    }

    static Variant selectSingleVariant(
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState resource,
            BlockState state
    ) {
        VariantSet selected = selectVariantSet(resource, state);
        if (selected == null || selected.getVariants() == null
                || selected.getVariants().length != 1) {
            return null;
        }
        return selected.getVariants()[0];
    }

    static boolean isSimpleStaticCube(Variant variant, Model model) {
        if (variant == null
                || !isSupportedMaterialRenderer(variant.getRenderer())
                || variant.isUvlock()
                || variant.isTransformed()
                || model == null
                || !model.isAmbientocclusion()) {
            return false;
        }
        Element[] elements = model.getElements();
        if (elements == null || elements.length != 1 || elements[0] == null) {
            return false;
        }

        Element element = elements[0];
        if (!FULL_BLOCK_MIN.equals(element.getFrom())
                || !FULL_BLOCK_MAX.equals(element.getTo())
                || !element.isShade()
                || element.getLightEmission() != 0
                || element.getRotation().getX() != 0F
                || element.getRotation().getY() != 0F
                || element.getRotation().getZ() != 0F
                || element.getFaces().size() != Direction.values().length) {
            return false;
        }
        for (Direction direction : Direction.values()) {
            Face face = element.getFaces().get(direction);
            if (face == null
                    || face.getCullface() != direction
                    || face.getRotation() != 0
                    || face.getTintindex() < -1
                    || face.getTintindex() > 0
                    || !FULL_FACE_UV.equals(face.getUv())) {
                return false;
            }
        }
        return true;
    }

    static boolean isUniformOpaqueFullCubeVariant(Variant variant, Model model) {
        if (variant == null
                || !isSupportedMaterialRenderer(variant.getRenderer())
                || variant.isUvlock()
                || !isQuarterTurn(variant.getX())
                || !isQuarterTurn(variant.getY())
                || !isQuarterTurn(variant.getZ())
                || !Double.isFinite(variant.getWeight())
                || variant.getWeight() <= 0D
                || model == null
                || !model.isAmbientocclusion()) {
            return false;
        }
        Element[] elements = model.getElements();
        if (elements == null || elements.length != 1 || elements[0] == null) {
            return false;
        }

        Element element = elements[0];
        if (!hasCanonicalFullCubeBody(element)) {
            return false;
        }
        for (Direction direction : Direction.values()) {
            Face face = element.getFaces().get(direction);
            if (face == null
                    || face.getCullface() != direction
                    || !isQuarterTurn(face.getRotation())
                    || face.getTintindex() < -1
                    || face.getTintindex() > 0
                    || !isFullFaceUv(face.getUv())) {
                return false;
            }
        }
        return true;
    }

    static boolean isSupportedMaterialRenderer(BlockRendererType renderer) {
        return renderer == BlockRendererType.DEFAULT
                || (renderer != null
                        && renderer == BlockRendererType.REGISTRY.get(CAMOL_OVERLAY_RENDERER_KEY));
    }

    private static boolean hasCanonicalFullCubeBody(Element element) {
        return FULL_BLOCK_MIN.equals(element.getFrom())
                && FULL_BLOCK_MAX.equals(element.getTo())
                && element.isShade()
                && element.getLightEmission() == 0
                && element.getRotation().getX() == 0F
                && element.getRotation().getY() == 0F
                && element.getRotation().getZ() == 0F
                && element.getFaces().size() == Direction.values().length;
    }

    private static boolean isQuarterTurn(float angle) {
        if (!Float.isFinite(angle)) {
            return false;
        }
        float quarterTurns = angle / 90F;
        return Math.abs(quarterTurns - Math.round(quarterTurns)) <= 1.0E-6F;
    }

    private static boolean isFullFaceUv(Vector4f uv) {
        return uv != null
                && isZeroOrSixteen(uv.getX())
                && isZeroOrSixteen(uv.getY())
                && isZeroOrSixteen(uv.getZ())
                && isZeroOrSixteen(uv.getW())
                && uv.getX() != uv.getZ()
                && uv.getY() != uv.getW();
    }

    private static boolean isZeroOrSixteen(float value) {
        return value == 0F || value == 16F;
    }

    static boolean isCanonicalOpaque(Texture texture) {
        return texture != null
                && !texture.isHalfTransparent()
                && texture.getColorStraight().a >= 1F;
    }

    static boolean isSimpleStaticProperties(BlockProperties properties) {
        return properties != null
                && properties.isOccluding()
                && properties.isCulling()
                && !properties.isAlwaysWaterlogged()
                && !properties.isRandomOffset();
    }

    record Material(Key texture, int tintIndex, int lightEmission) {
    }

    record MaterialPalette(
            BlockState tintState,
            Map<Direction, Material> materials,
            Material fallback,
            boolean empty,
            boolean resolved,
            String reason
    ) {
        static MaterialPalette emptyPalette() {
            return new MaterialPalette(
                    BlockState.MISSING,
                    Map.of(),
                    MISSING,
                    true,
                    true,
                    "ok"
            );
        }

        static MaterialPalette uniform(Material material) {
            return uniform(BlockState.MISSING, material);
        }

        static MaterialPalette uniform(BlockState tintState, Material material) {
            EnumMap<Direction, Material> materials = new EnumMap<>(Direction.class);
            for (Direction direction : Direction.values()) {
                materials.put(direction, material);
            }
            return new MaterialPalette(
                    tintState,
                    Map.copyOf(materials),
                    material,
                    false,
                    true,
                    "ok"
            );
        }

        static MaterialPalette missing(BlockState tintState) {
            return missing(tintState, "material-unresolved");
        }

        static MaterialPalette missing(BlockState tintState, String reason) {
            return new MaterialPalette(tintState, Map.of(), MISSING, false, false, reason);
        }

        static MaterialPalette directional(
                BlockState tintState,
                EnumMap<Direction, Material> materials
        ) {
            return new MaterialPalette(
                    tintState,
                    Map.copyOf(materials),
                    materials.get(Direction.UP),
                    false,
                    true,
                    "ok"
            );
        }

        Material get(Direction direction) {
            return materials.getOrDefault(direction, fallback);
        }
    }
}

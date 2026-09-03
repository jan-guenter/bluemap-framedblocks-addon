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
import de.bluecolored.bluemap.core.util.math.VectorM3f;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.NormalizedBlockState;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.NormalizedCamo;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/** Resolves only camouflage models that the exact-profile renderer can reproduce safely. */
final class CamoMaterialResolver {

    private static final int MAX_WEIGHTED_VARIANTS = 16;
    private static final Vector3f FULL_BLOCK_MIN = Vector3f.ZERO;
    private static final Vector3f FULL_BLOCK_MAX = new Vector3f(16F, 16F, 16F);
    private static final String[] GENERATED_TILE_NAMESPACES = {
            "bluemap_connectedglass",
            "bluemap_glassential",
            "bluemap_rechiseled",
            "bluemap_rechiseled_create",
            "bluemap_crystalix"
    };
    private static final Key CAMOL_OVERLAY_RENDERER_KEY =
            Key.parse("bluemap_camol:overlay");
    private static final String CRYSTALIX_GLASS = "crystalix:crystalix_glass";
    private static final Key CRYSTALIX_COLORED_TEXTURE =
            Key.parse("crystalix:block/colored_crystalix_glass");
    private static final Key CRYSTALIX_TRANSPARENT_TEXTURE =
            Key.parse("crystalix:block/crystalix_glass");
    private static final Material MISSING = new Material(ResourcePack.MISSING_TEXTURE, -1, 0);

    private final ResourcePack resourcePack;

    CamoMaterialResolver(ResourcePack resourcePack) {
        this.resourcePack = resourcePack;
    }

    MaterialPalette resolve(NormalizedCamo camo, BlockNeighborhood block) {
        return switch (camo.kind()) {
            case EMPTY -> MaterialPalette.emptyPalette();
            case BLOCK -> resolveBlock(camo, block);
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

    private MaterialPalette resolveBlock(NormalizedCamo camo, BlockNeighborhood block) {
        NormalizedBlockState normalized = camo.blockState().orElseThrow();
        if (camo.fixedTintRgb() >= 0) {
            return resolveFixedTintBlock(normalized, camo.fixedTintRgb());
        }
        BlockState state = new BlockState(Key.parse(normalized.id()), normalized.properties());
        de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState resource =
                resourcePack.getBlockStates().get(state.getId());
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
        if (!isStableMaterialProperties(properties)) {
            return MaterialPalette.missing(state, "block-properties-not-stable");
        }

        Variant[] variants = variantSet.getVariants();
        if (variants.length == 1) {
            return resolveDirectionalVariant(state, variants[0]);
        }
        if (variants.length > MAX_WEIGHTED_VARIANTS) {
            return MaterialPalette.missing(state, "weighted-variant-count-unsupported");
        }
        MaterialPalette uniform = resolveUniformVariantSet(state, variants);
        if (uniform.resolved() || block == null) {
            return uniform;
        }

        Variant positional = selectVariantAt(
                variantSet,
                block.getX(),
                block.getY(),
                block.getZ()
        );
        if (positional == null) {
            return MaterialPalette.missing(state, "weighted-variant-selection-failed");
        }
        return resolveDirectionalVariant(state, positional);
    }

    private MaterialPalette resolveFixedTintBlock(NormalizedBlockState normalized, int rgb) {
        BlockState state = new BlockState(Key.parse(normalized.id()), normalized.properties());
        if (!CRYSTALIX_GLASS.equals(normalized.id())) {
            return MaterialPalette.missing(state, "fixed-tint-camo-state-unsupported");
        }
        Map<String, String> properties = normalized.properties();
        if (!Set.of("false", "true").contains(properties.get("transparent"))
                || !Set.of("false", "true").contains(properties.get("invisible"))
                || !"false".equals(properties.get("shadeless"))
                || !Set.of("none", "light", "fake_light").contains(properties.get("light"))
                || !"block_all".equals(properties.get("ghost"))
                || !"false".equals(properties.get("waterlogged"))) {
            return MaterialPalette.missing(state, "fixed-tint-camo-properties-unsupported");
        }
        if ("true".equals(properties.get("invisible"))) {
            return MaterialPalette.missing(state, "fixed-tint-invisible-camo-unsupported");
        }
        Key textureKey = "true".equals(properties.get("transparent"))
                ? CRYSTALIX_TRANSPARENT_TEXTURE
                : CRYSTALIX_COLORED_TEXTURE;
        if (!isUsableTexture(resourcePack.getTextures().get(textureKey))) {
            return MaterialPalette.missing(state, "fixed-tint-camo-texture-missing");
        }
        int emission = switch (properties.get("light")) {
            case "light", "fake_light" -> 15;
            default -> 0;
        };
        return MaterialPalette.uniform(
                state,
                new Material(textureKey, 0, emission),
                rgb
        );
    }

    private MaterialPalette resolveDirectionalVariant(BlockState state, Variant variant) {
        Model model = variant == null
                ? null
                : variant.getModel().getResource(resourcePack.getModels()::get);
        if (!isFullCubeMaterialVariant(variant, model)) {
            return MaterialPalette.missing(state, "single-variant-model-unsupported");
        }
        EnumMap<Direction, Material> materials = resolveMaterials(state, model);
        if (materials == null) {
            return MaterialPalette.missing(state, "single-variant-material-unsupported");
        }
        if (variant.isTransformed()) {
            EnumMap<Direction, Material> transformed = transformMaterials(materials, variant);
            if (transformed == null) {
                return MaterialPalette.missing(
                        state,
                        "transformed-directional-material-unsupported"
                );
            }
            return MaterialPalette.directional(state, transformed);
        }
        return MaterialPalette.directional(state, materials);
    }

    private static EnumMap<Direction, Material> transformMaterials(
            EnumMap<Direction, Material> source,
            Variant variant
    ) {
        EnumMap<Direction, Material> transformed = new EnumMap<>(Direction.class);
        for (Direction sourceDirection : Direction.values()) {
            VectorM3f vector = new VectorM3f(0F, 0F, 0F)
                    .set(sourceDirection.toVector())
                    .rotateAndScale(variant.getTransformMatrix());
            Direction targetDirection = cardinalDirection(vector);
            if (targetDirection == null
                    || transformed.put(targetDirection, source.get(sourceDirection)) != null) {
                return null;
            }
        }
        return transformed.size() == Direction.values().length ? transformed : null;
    }

    private static Direction cardinalDirection(VectorM3f vector) {
        int x = Math.round(vector.x);
        int y = Math.round(vector.y);
        int z = Math.round(vector.z);
        if (Math.abs(vector.x - x) > 1.0E-5F
                || Math.abs(vector.y - y) > 1.0E-5F
                || Math.abs(vector.z - z) > 1.0E-5F) {
            return null;
        }
        for (Direction direction : Direction.values()) {
            if (direction.toVector().getX() == x
                    && direction.toVector().getY() == y
                    && direction.toVector().getZ() == z) {
                return direction;
            }
        }
        return null;
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
            if (!isFullCubeMaterialVariant(variant, model)) {
                return MaterialPalette.missing(state, "weighted-variant-model-unsupported");
            }
            EnumMap<Direction, Material> materials = resolveMaterials(state, model);
            if (materials == null) {
                return MaterialPalette.missing(state, "weighted-variant-material-unsupported");
            }
            Material uniform = uniformMaterial(materials);
            if (uniform == null) {
                return MaterialPalette.missing(state, "weighted-variant-material-not-uniform");
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

    private EnumMap<Direction, Material> resolveMaterials(BlockState state, Model model) {
        Element element = model.getElements()[0];
        int lightEmission = effectiveLightEmission(state, element);
        EnumMap<Direction, Material> materials = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            Face face = element.getFaces().get(direction);
            ResourcePath<Texture> texture = face.getTexture()
                    .getTexturePath(model.getTextures()::get);
            if (texture == null
                    || ResourcePack.MISSING_TEXTURE.equals(texture)) {
                return null;
            }
            texture = materialTexture(texture);
            Texture resolvedTexture = resourcePack.getTextures().get(texture);
            if (!isUsableTexture(resolvedTexture)) {
                return null;
            }
            materials.put(direction, new Material(
                    texture,
                    face.getTintindex(),
                    lightEmission
            ));
        }
        return materials;
    }

    private ResourcePath<Texture> materialTexture(ResourcePath<Texture> source) {
        for (String namespace : GENERATED_TILE_NAMESPACES) {
            ResourcePath<Texture> tile = new ResourcePath<>(
                    namespace,
                    "tiles/" + source.getNamespace() + "/" + source.getValue() + "/0"
            );
            if (resourcePack.getTextures().get(tile) != null) {
                return tile;
            }
        }
        return source;
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

    static Variant selectVariantAt(VariantSet variants, int x, int y, int z) {
        if (variants == null || variants.getVariants() == null) {
            return null;
        }
        Variant[] candidates = variants.getVariants();
        if (candidates.length == 0 || candidates.length > MAX_WEIGHTED_VARIANTS) {
            return null;
        }
        for (Variant candidate : candidates) {
            if (candidate == null
                    || !Double.isFinite(candidate.getWeight())
                    || candidate.getWeight() <= 0D) {
                return null;
            }
        }
        Variant[] selected = new Variant[1];
        variants.forEach(x, y, z, variant -> selected[0] = variant);
        return selected[0];
    }

    static boolean isFullCubeMaterialVariant(Variant variant, Model model) {
        if (variant == null
                || !isSupportedMaterialRenderer(variant.getRenderer())
                || !isQuarterTurn(variant.getX())
                || !isQuarterTurn(variant.getY())
                || !isQuarterTurn(variant.getZ())
                || !Double.isFinite(variant.getWeight())
                || variant.getWeight() <= 0D
                || model == null) {
            return false;
        }
        Element[] elements = model.getElements();
        if (elements == null || elements.length != 1 || elements[0] == null) {
            return false;
        }

        Element element = elements[0];
        if (!hasFullCubeMaterialBody(element)) {
            return false;
        }
        for (Direction direction : Direction.values()) {
            Face face = element.getFaces().get(direction);
            if (face == null
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

    private static boolean hasFullCubeMaterialBody(Element element) {
        return FULL_BLOCK_MIN.equals(element.getFrom())
                && FULL_BLOCK_MAX.equals(element.getTo())
                && element.getLightEmission() >= 0
                && element.getLightEmission() <= 15
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

    static boolean isUsableTexture(Texture texture) {
        return texture != null && texture.getColorStraight() != null;
    }

    static boolean isStableMaterialProperties(BlockProperties properties) {
        return properties != null
                && !properties.isAlwaysWaterlogged()
                && !properties.isRandomOffset();
    }

    private static int effectiveLightEmission(
            BlockState state,
            Element element
    ) {
        int emission = element.getLightEmission();
        String namespace = state.getId().getNamespace();
        String value = state.getId().getValue();
        if (emission == 0
                && !element.isShade()
                && value.endsWith("_luminax_block")
                && !value.startsWith("dim_")
                && !value.startsWith("luminax_dim_")
                && ("luminax".equals(namespace)
                        || "dyenamicsandfriends".equals(namespace))) {
            // NeoForge's model metadata carries the exact value, but the pinned BlueMap
            // resource parser intentionally ignores neoforge_data. Luminax's dedicated
            // no-shade cube contract is otherwise preserved in the parsed model.
            return 15;
        }
        return emission;
    }

    record Material(Key texture, int tintIndex, int lightEmission) {
    }

    record MaterialPalette(
            BlockState tintState,
            Map<Direction, Material> materials,
            Material fallback,
            int fixedTintRgb,
            boolean empty,
            boolean resolved,
            String reason
    ) {
        static MaterialPalette emptyPalette() {
            return new MaterialPalette(
                    BlockState.MISSING,
                    Map.of(),
                    MISSING,
                    -1,
                    true,
                    true,
                    "ok"
            );
        }

        static MaterialPalette uniform(Material material) {
            return uniform(BlockState.MISSING, material);
        }

        static MaterialPalette uniform(BlockState tintState, Material material) {
            return uniform(tintState, material, -1);
        }

        static MaterialPalette uniform(
                BlockState tintState,
                Material material,
                int fixedTintRgb
        ) {
            EnumMap<Direction, Material> materials = new EnumMap<>(Direction.class);
            for (Direction direction : Direction.values()) {
                materials.put(direction, material);
            }
            return new MaterialPalette(
                    tintState,
                    Map.copyOf(materials),
                    material,
                    fixedTintRgb,
                    false,
                    true,
                    "ok"
            );
        }

        static MaterialPalette missing(BlockState tintState) {
            return missing(tintState, "material-unresolved");
        }

        static MaterialPalette missing(BlockState tintState, String reason) {
            return new MaterialPalette(
                    tintState,
                    Map.of(),
                    MISSING,
                    -1,
                    false,
                    false,
                    reason
            );
        }

        static MaterialPalette directional(
                BlockState tintState,
                EnumMap<Direction, Material> materials
        ) {
            return new MaterialPalette(
                    tintState,
                    Map.copyOf(materials),
                    materials.get(Direction.UP),
                    -1,
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

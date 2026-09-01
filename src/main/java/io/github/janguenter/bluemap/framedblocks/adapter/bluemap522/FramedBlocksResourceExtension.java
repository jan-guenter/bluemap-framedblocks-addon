/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap522;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePackExtension;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.VariantSet;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variants;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import io.github.janguenter.bluemap.framedblocks.diagnostics.BoundedDiagnostics;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.ExactArtifactDetector;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.FramedBlocks1061Profile;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.FramedBlocks1061Support;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.GeometryTemplateProfile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/** Exact-profile activation and namespace-disjoint blockstate routing. */
final class FramedBlocksResourceExtension implements ResourcePackExtension {

    private final ResourcePack resourcePack;
    private final AdapterActivation activation;
    private final ExactArtifactDetector artifactDetector =
            new ExactArtifactDetector(FramedBlocks1061Profile.JAR_SHA256);

    FramedBlocksResourceExtension(ResourcePack resourcePack, AdapterActivation activation) {
        this.resourcePack = resourcePack;
        this.activation = activation;
    }

    @Override
    public void loadResources(Iterable<Path> roots) throws IOException, InterruptedException {
        if (activation.isDisabled()) {
            return;
        }

        ExactArtifactDetector.Detection detection;
        try {
            detection = artifactDetector.detect(roots);
        } catch (IOException exception) {
            activation.disable("framedblocks-artifact-read-failed");
            BoundedDiagnostics.warning(
                    "framedblocks-artifact-read-failed",
                    "BlueMap FramedBlocks add-on is inactive: artifact identity could not be read."
            );
            return;
        }
        if (!detection.exact()) {
            activation.inactive(detection.reason());
            BoundedDiagnostics.warning(
                    detection.reason(),
                    "BlueMap FramedBlocks add-on is inactive: the exact FramedBlocks 10.6.1 artifact was not found."
            );
            return;
        }

        de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState
                syntheticBlockState = resourcePack.getBlockStates().get(
                        FramedBlocks1061Profile.SYNTHETIC_FRAMED_SHAPE
                );
        if (syntheticBlockState == null) {
            activation.inactive("synthetic-blockstate-missing");
            BoundedDiagnostics.warning(
                    "synthetic-blockstate-missing",
                    "BlueMap FramedBlocks add-on is inactive: its synthetic M0 blockstate was not loaded."
            );
            return;
        }
        if (!isExpectedSyntheticBlockState(syntheticBlockState)) {
            activation.inactive("synthetic-blockstate-invalid");
            BoundedDiagnostics.warning(
                    "synthetic-blockstate-invalid",
                    "BlueMap FramedBlocks add-on is inactive: its synthetic M0 blockstate was shadowed or changed."
            );
            return;
        }

        GeometryTemplateProfile geometryProfile;
        try {
            geometryProfile = GeometryTemplateProfile.loadBundled();
            if (geometryProfile.rawStateCount()
                            != FramedBlocks1061Profile.CLIENT_RAW_STATE_COUNT
                    || geometryProfile.renderableStateCount()
                            != FramedBlocks1061Profile.CLIENT_RENDERABLE_STATE_COUNT
                    || geometryProfile.templateCount()
                            != FramedBlocks1061Profile.CLIENT_TEMPLATE_COUNT
                    || geometryProfile.baseRoutedFamilyTemplateCount()
                            != FramedBlocks1061Profile
                                    .CLIENT_BASE_ROUTED_FAMILY_TEMPLATE_COUNT) {
                throw new IOException("Geometry profile state inventory does not match");
            }
            Set<String> expectedBlockIds = new HashSet<>();
            FramedBlocks1061Profile.blockStateKeys().stream()
                    .map(Key::getFormatted)
                    .filter(id -> !id.equals("framedblocks:framing_saw"))
                    .filter(id -> !id.equals("framedblocks:powered_framing_saw"))
                    .forEach(expectedBlockIds::add);
            if (!expectedBlockIds.equals(geometryProfile.renderedBlockIds())) {
                throw new IOException("Geometry profile block inventory does not match");
            }
        } catch (IOException | RuntimeException exception) {
            activation.inactive("geometry-profile-invalid");
            BoundedDiagnostics.warning(
                    "geometry-profile-invalid",
                    "BlueMap FramedBlocks add-on is inactive: its geometry profile is invalid."
            );
            return;
        }

        // BlueNBT snapshots the registered DTO set on first use. This must run
        // after every BlueMap add-on entrypoint has had a chance to register.
        if (!BlueMap522Adapter.probeBlockEntityRetention()) {
            activation.disable("bluenbt-retention-probe-failed");
            BoundedDiagnostics.warning(
                    "bluenbt-retention-probe-failed",
                    "BlueMap FramedBlocks add-on is inactive: the BlueNBT retention probe failed."
            );
            return;
        }

        activation.activate(geometryProfile);
        BoundedDiagnostics.info(
                "profile-activated",
                "BlueMap FramedBlocks exact 10.6.1 geometry profile activated."
        );
    }

    @Override
    public Set<Key> collectUsedTextureKeys() {
        return activation.geometryProfile().map(profile -> {
            Set<Key> textures = new HashSet<>(profile.usedSpriteKeys());
            textures.add(Key.parse("minecraft:block/water_still"));
            textures.add(Key.parse("minecraft:block/water_flow"));
            textures.add(Key.parse("minecraft:block/lava_still"));
            textures.add(Key.parse("minecraft:block/lava_flow"));
            return Set.copyOf(textures);
        }).orElse(Set.of());
    }

    @Override
    public void bake() {
        GeometryTemplateProfile profile = activation.geometryProfile().orElse(null);
        if (!activation.isActive() || profile == null) {
            return;
        }
        if (!hasAllFixedSprites(resourcePack, profile.usedSpriteKeys())) {
            activation.inactive("geometry-profile-fixed-texture-missing");
            BoundedDiagnostics.warning(
                    "geometry-profile-fixed-texture-missing",
                    "BlueMap FramedBlocks add-on is inactive: a fixed profile texture is missing."
            );
        }
    }

    static boolean hasAllFixedSprites(ResourcePack resourcePack, Set<Key> spriteKeys) {
        for (Key spriteKey : spriteKeys) {
            if (ResourcePack.MISSING_TEXTURE.equals(spriteKey)
                    || resourcePack.getTextures().get(spriteKey) == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Key getBlockStateKey(Key key) {
        if (activation.isActive()
                && activation.geometryProfile()
                        .map(profile -> profile.renderedBlockIds().contains(key.getFormatted()))
                        .orElse(false)) {
            return FramedBlocks1061Profile.SYNTHETIC_FRAMED_SHAPE;
        }
        return key;
    }

    @Override
    public void getBlockProperties(
            BlockState blockState,
            BlockProperties.Builder propertiesBuilder
    ) {
        if (activation.isActive()
                && activation.geometryProfile()
                        .map(profile -> profile.find(blockState).isPresent()
                                && profile.support(blockState)
                                        .map(FramedBlocksResourceExtension::usesSyntheticProperties)
                                        .orElse(false))
                        .orElse(false)) {
            // The one synthetic model is only a renderer dispatch target. Its
            // missing-model cube must not make every framed shape occluding or
            // hide faces of adjacent terrain and framed blocks.
            propertiesBuilder
                    .culling(false)
                    .occluding(false)
                    .cullingIdentical(false);
        }
    }

    private static boolean usesSyntheticProperties(
            FramedBlocks1061Support.Classification classification
    ) {
        if (classification.routed()) {
            return true;
        }
        return switch (classification.family()) {
            case BLOCK_ENTITY_RENDERER,
                    ADJUSTABLE,
                    COLLAPSIBLE,
                    FLOWER_POT,
                    ONE_WAY_WINDOW,
                    SPECIAL_CAMO_OVERLAY -> true;
            case WATERLOGGED_FLUID,
                    DYNAMIC_LIGHT,
                    DYNAMIC_SKYLIGHT,
                    STATIC_BAKED_MODEL -> false;
        };
    }

    static boolean isExpectedSyntheticBlockState(
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState resource
    ) {
        if (resource == null || resource.getMultipart() != null) {
            return false;
        }
        Variants variants = resource.getVariants();
        if (variants == null
                || variants.getVariants().length != 0
                || variants.getDefaultVariant() == null) {
            return false;
        }
        VariantSet defaultVariant = variants.getDefaultVariant();
        return defaultVariant.getVariants().length == 1
                && BlueMap522Adapter.isExpectedSyntheticVariant(
                        defaultVariant.getVariants()[0]
                );
    }
}

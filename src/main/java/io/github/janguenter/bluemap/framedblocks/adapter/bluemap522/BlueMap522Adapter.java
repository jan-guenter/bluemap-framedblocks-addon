/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap522;

import de.bluecolored.bluemap.core.logger.Logger;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.util.Keyed;
import de.bluecolored.bluemap.core.util.Registry;
import de.bluecolored.bluemap.core.world.BlockEntity;
import de.bluecolored.bluemap.core.world.mca.MCAUtil;
import de.bluecolored.bluemap.core.world.mca.blockentity.BlockEntityType;
import de.bluecolored.bluenbt.NBTWriter;
import io.github.janguenter.bluemap.framedblocks.diagnostics.BoundedDiagnostics;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.FramedBlocks1061Profile;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.FramedCamoDecoder;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.NormalizedBlockState;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** All direct BlueMap 5.22 internal-ABI integration lives in this package. */
public final class BlueMap522Adapter {

    private static final AdapterActivation ACTIVATION = new AdapterActivation();
    private static List<BlockEntityType> blockEntityTypes;
    private static final BlockRendererType RENDERER_TYPE = new BlockRendererType.Impl(
            FramedBlocks1061Profile.RENDERER,
            (resourcePack, textureGallery, renderSettings) -> new FramedGeometryRenderer(
                    resourcePack,
                    textureGallery,
                    renderSettings,
                    ACTIVATION
            )
    );
    private static final ResourcePack.Extension<FramedBlocksResourceExtension> RESOURCE_EXTENSION_TYPE =
            new FramedBlocksResourceExtensionType(ACTIVATION);

    private BlueMap522Adapter() {
    }

    public static synchronized boolean install() {
        if (!AdapterCompatibility.currentRuntimeSupported()) {
            ACTIVATION.disable("unsupported-bluemap-runtime");
            BoundedDiagnostics.warning(
                    "unsupported-bluemap-runtime",
                    "BlueMap FramedBlocks add-on is inactive: this BlueMap internal ABI was not audited."
            );
            return false;
        }

        List<BlockEntityType> exactBlockEntityTypes;
        try {
            exactBlockEntityTypes = blockEntityTypes();
        } catch (IOException exception) {
            ACTIVATION.disable("block-entity-manifest-invalid");
            BoundedDiagnostics.warning(
                    "block-entity-manifest-invalid",
                    "BlueMap FramedBlocks add-on is inactive: its exact block-entity manifest is invalid."
            );
            return false;
        }

        if (!exactBlockEntityTypes.stream().allMatch(
                type -> canRegisterExact(BlockEntityType.REGISTRY, type)
        )) {
            return disableForCollision("block-entity-registry-collision");
        }
        if (!canRegisterExact(BlockRendererType.REGISTRY, RENDERER_TYPE)) {
            return disableForCollision("renderer-registry-collision");
        }
        if (!canRegisterExact(ResourcePack.Extension.REGISTRY, RESOURCE_EXTENSION_TYPE)) {
            return disableForCollision("resource-extension-registry-collision");
        }

        for (BlockEntityType blockEntityType : exactBlockEntityTypes) {
            if (!registerExact(BlockEntityType.REGISTRY, blockEntityType)) {
                return disableForCollision("block-entity-registry-collision");
            }
        }
        if (!registerExact(BlockRendererType.REGISTRY, RENDERER_TYPE)) {
            return disableForCollision("renderer-registry-collision");
        }
        if (!registerExact(ResourcePack.Extension.REGISTRY, RESOURCE_EXTENSION_TYPE)) {
            return disableForCollision("resource-extension-registry-collision");
        }

        ACTIVATION.inactive("awaiting-exact-framedblocks-profile");
        Logger.global.logInfo(
                "BlueMap FramedBlocks add-on hooks installed; routing remains inactive until exact profile detection."
        );
        return true;
    }

    static AdapterActivation activationForTesting() {
        return ACTIVATION;
    }

    static boolean isExpectedSyntheticVariant(Variant variant) {
        return variant != null
                && variant.getRenderer() == RENDERER_TYPE
                && ResourcePack.MISSING_BLOCK_MODEL.equals(variant.getModel())
                && !variant.isTransformed()
                && !variant.isUvlock()
                && Double.compare(variant.getWeight(), 1D) == 0;
    }

    private static synchronized List<BlockEntityType> blockEntityTypes() throws IOException {
        if (blockEntityTypes == null) {
            List<BlockEntityType> candidates = new ArrayList<>();
            for (de.bluecolored.bluemap.core.util.Key key
                    : FramedBlocks1061Profile.blockEntityKeys()) {
                candidates.add(new BlockEntityType.Impl(key, FramedBlockEntityData.class));
            }
            blockEntityTypes = List.copyOf(candidates);
        }
        return blockEntityTypes;
    }

    private static boolean disableForCollision(String reason) {
        ACTIVATION.disable(reason);
        BoundedDiagnostics.warning(
                reason,
                "BlueMap FramedBlocks add-on is inactive because an internal registry key is already owned."
        );
        return false;
    }

    private static <T extends Keyed> boolean registerExact(Registry<T> registry, T candidate) {
        T existing = registry.get(candidate.getKey());
        if (existing == null) {
            registry.register(candidate);
            existing = registry.get(candidate.getKey());
        }
        return existing == candidate;
    }

    private static <T extends Keyed> boolean canRegisterExact(
            Registry<T> registry,
            T candidate
    ) {
        T existing = registry.get(candidate.getKey());
        return existing == null || existing == candidate;
    }

    static boolean probeBlockEntityRetention() {
        try {
            for (de.bluecolored.bluemap.core.util.Key id
                    : FramedBlocks1061Profile.blockEntityKeys()) {
                BlockEntity parsed = MCAUtil.BLUENBT.read(
                        new ByteArrayInputStream(createProbeNbt(id.getFormatted())),
                        BlockEntity.class
                );
                if (!(parsed instanceof FramedBlockEntityData data)
                        || !id.equals(data.getId())
                        || !probeFieldsRetained(data)) {
                    return false;
                }
            }
            return true;
        } catch (IOException | RuntimeException exception) {
            Logger.global.noFloodError(
                    "bluemap-framedblocks:bluenbt-probe-exception",
                    "BlueMap FramedBlocks BlueNBT retention probe raised an exception.",
                    exception
            );
            return false;
        }
    }

    private static boolean probeFieldsRetained(FramedBlockEntityData data) {
        if (data.getX() != 17
                || data.getY() != -23
                || data.getZ() != 41
                || !data.hasRequiredBaseFields()
                || !data.isGlowing()
                || !data.isIntangible()
                || !data.isReinforced()
                || data.getUpdated() != 3
                || data.getOffsets() != 123
                || data.getFirstHeight() != 8
                || !data.hasOverlayColor()
                || data.getOverlayColor() != 2
                || !"minecraft:poppy".equals(data.getFlower())
                || data.getRotation() != 4
                || !(data.getFluid() instanceof Map<?, ?>)
                || !(data.getItem() instanceof Map<?, ?>)
                || !(data.getFrontText() instanceof Map<?, ?>)
                || !(data.getBackText() instanceof Map<?, ?>)) {
            return false;
        }
        FramedCamoDecoder decoder = new FramedCamoDecoder();
        boolean primaryRetained = decoder.decode(data.getCamo()).state()
                .filter(new NormalizedBlockState("minecraft:stone", Map.of())::equals)
                .isPresent();
        boolean secondaryRetained = decoder.decode(data.getCamoTwo()).state()
                .filter(new NormalizedBlockState(
                        "minecraft:dirt",
                        Map.of("snowy", "false")
                )::equals)
                .isPresent();
        return primaryRetained && secondaryRetained;
    }

    private static byte[] createProbeNbt(String id) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (NBTWriter writer = new NBTWriter(bytes)) {
            writer.beginCompound();
            writer.name("id").value(id);
            writer.name("x").value(17);
            writer.name("y").value(-23);
            writer.name("z").value(41);
            writer.name("camo").beginCompound();
            writer.name("type").value("framedblocks:block");
            writer.name("state").beginCompound();
            writer.name("Name").value("minecraft:stone");
            writer.endCompound();
            writer.endCompound();
            writer.name("camo_two").beginCompound();
            writer.name("type").value("framedblocks:block");
            writer.name("state").beginCompound();
            writer.name("Name").value("minecraft:dirt");
            writer.name("Properties").beginCompound();
            writer.name("snowy").value("false");
            writer.endCompound();
            writer.endCompound();
            writer.endCompound();
            writer.name("glowing").value((byte) 1);
            writer.name("intangible").value((byte) 1);
            writer.name("reinforced").value((byte) 1);
            writer.name("updated").value((byte) 3);
            writer.name("offsets").value(123);
            writer.name("first_height").value(8);
            writer.name("overlay_color").value(2);
            writer.name("flower").value("minecraft:poppy");
            writer.name("fluid").beginCompound();
            writer.name("id").value("minecraft:water");
            writer.name("amount").value(1000);
            writer.endCompound();
            writer.name("item").beginCompound();
            writer.name("id").value("minecraft:stone");
            writer.name("count").value(1);
            writer.endCompound();
            writer.name("rotation").value((byte) 4);
            writer.name("front_text").beginCompound();
            writer.name("color").value("black");
            writer.endCompound();
            writer.name("back_text").beginCompound();
            writer.name("color").value("black");
            writer.endCompound();
            writer.endCompound();
        }
        return bytes.toByteArray();
    }
}

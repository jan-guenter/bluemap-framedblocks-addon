/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Machine-readable support policy for the exact FramedBlocks 10.6.1 profile. */
public final class FramedBlocks1061Support {

    public static final int RENDERED_BLOCK_COUNT = 234;
    public static final int ROUTED_BLOCK_COUNT = 206;
    public static final int STOCK_FALLBACK_BLOCK_COUNT = 28;

    private static final Classification STATIC = new Classification(
            Status.SUPPORTED,
            Family.STATIC_BAKED_MODEL,
            "state-only-baked-model"
    );
    private static final Classification WATERLOGGED = new Classification(
            Status.STOCK_FALLBACK,
            Family.WATERLOGGED_FLUID,
            "waterlogged-fluid-rendering-required"
    );
    private static final Classification DYNAMIC_LIGHT = new Classification(
            Status.STOCK_FALLBACK,
            Family.DYNAMIC_LIGHT,
            "dynamic-light-camo-required"
    );
    private static final Classification DYNAMIC_SKYLIGHT = new Classification(
            Status.STOCK_FALLBACK,
            Family.DYNAMIC_SKYLIGHT,
            "dynamic-skylight-camo-required"
    );

    private static final Map<String, Classification> EXCEPTIONS = buildExceptions();

    private FramedBlocks1061Support() {
    }

    public static Map<String, Classification> classifyAll(
            Set<String> blockIds
    ) throws IOException {
        Set<String> exactRenderedIds = new HashSet<>();
        FramedBlocks1061Profile.blockStateKeys().stream()
                .map(key -> key.getFormatted())
                .filter(id -> !"framedblocks:framing_saw".equals(id))
                .filter(id -> !"framedblocks:powered_framing_saw".equals(id))
                .forEach(exactRenderedIds::add);
        if (exactRenderedIds.size() != RENDERED_BLOCK_COUNT
                || !exactRenderedIds.containsAll(blockIds)
                || !exactRenderedIds.containsAll(EXCEPTIONS.keySet())) {
            throw new IOException("FramedBlocks support-policy inventory does not match");
        }

        Set<String> routedIds = new HashSet<>(exactRenderedIds);
        routedIds.removeAll(EXCEPTIONS.keySet());
        if (routedIds.size() != ROUTED_BLOCK_COUNT
                || EXCEPTIONS.size() != STOCK_FALLBACK_BLOCK_COUNT) {
            throw new IOException("FramedBlocks support-policy counts do not match");
        }

        Map<String, Classification> classifications = new HashMap<>();
        for (String blockId : blockIds) {
            Classification classification = routedIds.contains(blockId)
                    ? STATIC : EXCEPTIONS.get(blockId);
            if (classification == null) {
                throw new IOException("FramedBlocks support-policy entry is missing");
            }
            classifications.put(blockId, classification);
        }
        return Map.copyOf(classifications);
    }

    public static Classification waterloggedFallback() {
        return WATERLOGGED;
    }

    public static Classification dynamicLightFallback() {
        return DYNAMIC_LIGHT;
    }

    public static Classification dynamicSkylightFallback() {
        return DYNAMIC_SKYLIGHT;
    }

    /** Exact block-family policy used by both runtime routing and profile projection. */
    static Set<String> stockFallbackBlockIds() {
        return EXCEPTIONS.keySet();
    }

    private static Map<String, Classification> buildExceptions() {
        Map<String, Classification> classifications = new HashMap<>();
        add(classifications, Family.BLOCK_ENTITY_RENDERER, "block-entity-renderer-required",
                "framedblocks:framed_sign",
                "framedblocks:framed_wall_sign",
                "framedblocks:framed_hanging_sign",
                "framedblocks:framed_wall_hanging_sign",
                "framedblocks:framed_chest",
                "framedblocks:framed_item_frame",
                "framedblocks:framed_glowing_item_frame",
                "framedblocks:framed_tank");
        add(classifications, Family.ADJUSTABLE, "adjustable-model-data-required",
                "framedblocks:framed_adj_double_slab",
                "framedblocks:framed_adj_double_panel",
                "framedblocks:framed_adj_double_copycat_slab",
                "framedblocks:framed_adj_double_copycat_panel");
        add(classifications, Family.COLLAPSIBLE, "collapsible-model-data-required",
                "framedblocks:framed_collapsible_block",
                "framedblocks:framed_collapsible_copycat_block");
        add(classifications, Family.FLOWER_POT, "flower-model-data-required",
                "framedblocks:framed_flower_pot");
        add(classifications, Family.ONE_WAY_WINDOW, "one-way-window-model-data-required",
                "framedblocks:framed_one_way_window");
        add(classifications, Family.SPECIAL_CAMO_OVERLAY, "camo-overlay-model-data-required",
                "framedblocks:framed_bouncy_cube",
                "framedblocks:framed_redstone_block",
                "framedblocks:framed_stone_button",
                "framedblocks:framed_large_stone_button",
                "framedblocks:framed_stone_pressure_plate",
                "framedblocks:framed_obsidian_pressure_plate",
                "framedblocks:framed_gold_pressure_plate",
                "framedblocks:framed_iron_pressure_plate",
                "framedblocks:framed_waterloggable_stone_pressure_plate",
                "framedblocks:framed_waterloggable_obsidian_pressure_plate",
                "framedblocks:framed_waterloggable_gold_pressure_plate",
                "framedblocks:framed_waterloggable_iron_pressure_plate");
        return Map.copyOf(classifications);
    }

    private static void add(
            Map<String, Classification> classifications,
            Family family,
            String reason,
            String... blockIds
    ) {
        Classification classification = new Classification(
                Status.STOCK_FALLBACK,
                family,
                reason
        );
        for (String blockId : blockIds) {
            if (classifications.putIfAbsent(blockId, classification) != null) {
                throw new IllegalStateException("Duplicate FramedBlocks support classification");
            }
        }
    }

    public enum Status {
        SUPPORTED,
        STOCK_FALLBACK
    }

    public enum Family {
        STATIC_BAKED_MODEL,
        BLOCK_ENTITY_RENDERER,
        ADJUSTABLE,
        COLLAPSIBLE,
        FLOWER_POT,
        ONE_WAY_WINDOW,
        SPECIAL_CAMO_OVERLAY,
        WATERLOGGED_FLUID,
        DYNAMIC_LIGHT,
        DYNAMIC_SKYLIGHT
    }

    public record Classification(Status status, Family family, String reason) {
        public boolean routed() {
            return status == Status.SUPPORTED;
        }
    }
}

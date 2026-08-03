/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FramedBlocks1061SupportTest {

    @Test
    void exactDigestLockedInventoryHasOnlyExplicitlyRoutedBlocks() throws IOException {
        Set<String> renderedIds = FramedBlocks1061Profile.blockStateKeys().stream()
                .map(key -> key.getFormatted())
                .filter(id -> !"framedblocks:framing_saw".equals(id))
                .filter(id -> !"framedblocks:powered_framing_saw".equals(id))
                .collect(Collectors.toUnmodifiableSet());
        Map<String, FramedBlocks1061Support.Classification> support =
                FramedBlocks1061Support.classifyAll(renderedIds);

        assertEquals(FramedBlocks1061Support.RENDERED_BLOCK_COUNT, support.size());
        assertEquals(FramedBlocks1061Support.ROUTED_BLOCK_COUNT, support.values().stream()
                .filter(FramedBlocks1061Support.Classification::routed)
                .count());
        assertEquals(FramedBlocks1061Support.STOCK_FALLBACK_BLOCK_COUNT,
                support.values().stream()
                        .filter(classification -> !classification.routed())
                        .count());

        Map<FramedBlocks1061Support.Family, Long> fallbackFamilies = support.values().stream()
                .filter(classification -> !classification.routed())
                .collect(Collectors.groupingBy(
                        FramedBlocks1061Support.Classification::family,
                        Collectors.counting()
                ));
        assertEquals(8L, fallbackFamilies.get(
                FramedBlocks1061Support.Family.BLOCK_ENTITY_RENDERER
        ));
        assertEquals(4L, fallbackFamilies.get(
                FramedBlocks1061Support.Family.ADJUSTABLE
        ));
        assertEquals(2L, fallbackFamilies.get(
                FramedBlocks1061Support.Family.COLLAPSIBLE
        ));
        assertEquals(1L, fallbackFamilies.get(
                FramedBlocks1061Support.Family.FLOWER_POT
        ));
        assertEquals(1L, fallbackFamilies.get(
                FramedBlocks1061Support.Family.ONE_WAY_WINDOW
        ));
        assertEquals(12L, fallbackFamilies.get(
                FramedBlocks1061Support.Family.SPECIAL_CAMO_OVERLAY
        ));
        assertFalse(support.get("framedblocks:framed_tank").routed());
        assertEquals("block-entity-renderer-required",
                support.get("framedblocks:framed_tank").reason());
    }

    @Test
    void unknownIdsCannotEnterThePositiveAllowlist() {
        assertThrows(IOException.class, () -> FramedBlocks1061Support.classifyAll(
                Set.of("framedblocks:not_in_10_6_1")
        ));
    }
}

/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FramedCamoDecoderTest {

    private final FramedCamoDecoder decoder = new FramedCamoDecoder();

    @Test
    void decodesAndCanonicalizesTheNamedBlockCamoCodec() {
        Map<String, String> properties = new LinkedHashMap<>();
        properties.put("waterlogged", "false");
        properties.put("axis", "y");
        Object camo = Map.of(
                "type", "framedblocks:block",
                "state", Map.of(
                        "Name", "minecraft:oak_log",
                        "Properties", properties
                )
        );

        CamoDecodeResult result = decoder.decode(camo);

        assertTrue(result.state().isPresent());
        assertEquals(
                new NormalizedBlockState(
                        "minecraft:oak_log",
                        Map.of("axis", "y", "waterlogged", "false")
                ),
                result.state().orElseThrow()
        );
        assertEquals("ok", result.reason());
    }

    @Test
    void acceptsThePlainStoneM0Fixture() {
        CamoDecodeResult result = decoder.decode(Map.of(
                "type", "framedblocks:block",
                "state", Map.of("Name", "minecraft:stone")
        ));

        assertEquals(
                new NormalizedBlockState("minecraft:stone", Map.of()),
                result.state().orElseThrow()
        );
    }

    @Test
    void decodesEmptyAndFluidDiskCamos() {
        CamoDecodeResult empty = decoder.decode(Map.of());
        CamoDecodeResult fluid = decoder.decode(Map.of(
                "type", "framedblocks:fluid",
                "fluid", "minecraft:water",
                "flow_dir", "north"
        ));

        assertEquals(NormalizedCamo.Kind.EMPTY, empty.camo().orElseThrow().kind());
        assertEquals(NormalizedCamo.Kind.FLUID, fluid.camo().orElseThrow().kind());
        assertEquals("minecraft:water", fluid.camo().orElseThrow().fluidId());
        assertEquals("north", fluid.camo().orElseThrow().flowDirection());
    }

    @Test
    void rejectsUnsupportedMalformedAndRecursiveInputs() {
        assertEquals("missing-camo", decoder.decode(null).reason());
        assertEquals(
                "invalid-fluid-id",
                decoder.decode(Map.of("type", "framedblocks:fluid")).reason()
        );
        assertEquals(
                "invalid-camo-id",
                decoder.decode(Map.of(
                        "type", "framedblocks:block",
                        "state", Map.of("Name", "invalid id")
                )).reason()
        );
        assertEquals(
                "recursive-camo",
                decoder.decode(Map.of(
                        "type", "framedblocks:block",
                        "state", Map.of("Name", "framedblocks:framed_cube")
                )).reason()
        );
    }
}

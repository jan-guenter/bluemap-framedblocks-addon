/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6;

import java.util.Objects;
import java.util.Optional;

/** Exact-profile camouflage normalized without Minecraft or FramedBlocks classes. */
public record NormalizedCamo(
        Kind kind,
        Optional<NormalizedBlockState> blockState,
        String fluidId,
        String flowDirection,
        int fixedTintRgb
) {
    public NormalizedCamo {
        Objects.requireNonNull(kind, "kind");
        blockState = Objects.requireNonNull(blockState, "blockState");
        fluidId = Objects.requireNonNull(fluidId, "fluidId");
        flowDirection = Objects.requireNonNull(flowDirection, "flowDirection");
        if (fixedTintRgb < -1 || fixedTintRgb > 0x00ff_ffff) {
            throw new IllegalArgumentException("fixedTintRgb is outside the RGB range");
        }
    }

    public static NormalizedCamo empty() {
        return new NormalizedCamo(Kind.EMPTY, Optional.empty(), "", "down", -1);
    }

    public static NormalizedCamo block(NormalizedBlockState state) {
        return new NormalizedCamo(Kind.BLOCK, Optional.of(state), "", "down", -1);
    }

    public static NormalizedCamo fixedTintBlock(NormalizedBlockState state, int rgb) {
        return new NormalizedCamo(Kind.BLOCK, Optional.of(state), "", "down", rgb);
    }

    public static NormalizedCamo fluid(String fluidId, String flowDirection) {
        return new NormalizedCamo(Kind.FLUID, Optional.empty(), fluidId, flowDirection, -1);
    }

    public enum Kind {
        EMPTY,
        BLOCK,
        FLUID
    }
}

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
        String flowDirection
) {
    public NormalizedCamo {
        Objects.requireNonNull(kind, "kind");
        blockState = Objects.requireNonNull(blockState, "blockState");
        fluidId = Objects.requireNonNull(fluidId, "fluidId");
        flowDirection = Objects.requireNonNull(flowDirection, "flowDirection");
    }

    public static NormalizedCamo empty() {
        return new NormalizedCamo(Kind.EMPTY, Optional.empty(), "", "down");
    }

    public static NormalizedCamo block(NormalizedBlockState state) {
        return new NormalizedCamo(Kind.BLOCK, Optional.of(state), "", "down");
    }

    public static NormalizedCamo fluid(String fluidId, String flowDirection) {
        return new NormalizedCamo(Kind.FLUID, Optional.empty(), fluidId, flowDirection);
    }

    public enum Kind {
        EMPTY,
        BLOCK,
        FLUID
    }
}

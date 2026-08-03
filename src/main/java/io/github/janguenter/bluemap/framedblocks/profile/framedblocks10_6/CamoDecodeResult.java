/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6;

import java.util.Optional;

/** Bounded camouflage decode result with a stable diagnostic reason. */
public record CamoDecodeResult(Optional<NormalizedCamo> camo, String reason) {

    public static CamoDecodeResult success(NormalizedCamo camo) {
        return new CamoDecodeResult(Optional.of(camo), "ok");
    }

    public static CamoDecodeResult failure(String reason) {
        return new CamoDecodeResult(Optional.empty(), reason);
    }

    public Optional<NormalizedBlockState> state() {
        return camo.flatMap(NormalizedCamo::blockState);
    }
}

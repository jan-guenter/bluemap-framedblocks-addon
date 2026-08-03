/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6;

import java.util.Map;

/** Profile-neutral block-state reference decoded from persisted camouflage. */
public record NormalizedBlockState(String id, Map<String, String> properties) {

    public NormalizedBlockState {
        properties = Map.copyOf(properties);
    }
}

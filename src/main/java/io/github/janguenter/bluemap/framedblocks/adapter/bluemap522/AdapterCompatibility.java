/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap522;

import de.bluecolored.bluemap.core.BlueMap;

/** Exact runtime identities whose audited internal ABI is accepted. */
public final class AdapterCompatibility {

    public static final String UPSTREAM_VERSION = "5.22";
    public static final String UPSTREAM_COMMIT = "fe5115d5548a30d34175b8e0449aaca280af199f";
    public static final String BACKPORT_VERSION = "5.22-agent.backport-5.22-mc1.21.1-1";
    public static final String BACKPORT_COMMIT = "fe79cf5b9f4d8ca28f4e41c2aeb9ef792e336a8d";

    private AdapterCompatibility() {
    }

    public static boolean currentRuntimeSupported() {
        return supported(BlueMap.VERSION, BlueMap.GIT_HASH);
    }

    public static boolean supported(String version, String gitHash) {
        return (UPSTREAM_VERSION.equals(version) && UPSTREAM_COMMIT.equals(gitHash))
                || (BACKPORT_VERSION.equals(version) && BACKPORT_COMMIT.equals(gitHash));
    }
}

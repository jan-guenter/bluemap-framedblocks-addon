/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap522;

import de.bluecolored.bluemap.core.BlueMap;

/** Exact backport runtime identity whose audited internal ABI is accepted. */
public final class AdapterCompatibility {

    public static final String BACKPORT_VERSION = "5.22-agent.backport-5.22-mc1.21.1-2";
    public static final String BACKPORT_COMMIT = "9be321df995a1103808621d529eb72773e719d4d";

    private AdapterCompatibility() {
    }

    public static boolean currentRuntimeSupported() {
        return supported(BlueMap.VERSION, BlueMap.GIT_HASH);
    }

    public static boolean supported(String version, String gitHash) {
        return BACKPORT_VERSION.equals(version) && BACKPORT_COMMIT.equals(gitHash);
    }
}

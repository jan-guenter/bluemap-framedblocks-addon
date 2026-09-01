/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap522;

import de.bluecolored.bluemap.core.BlueMap;

/** Exact backport runtime identity whose audited internal ABI is accepted. */
public final class AdapterCompatibility {

    public static final String BACKPORT_VERSION =
            "5.22-feature.backport-5.23-stateless-java-web-server-46";
    public static final String BACKPORT_COMMIT =
            "7e07f4e74ec1e92a6ead9aa1e66054af3e133aac";

    private AdapterCompatibility() {
    }

    public static boolean currentRuntimeSupported() {
        return supported(BlueMap.VERSION, BlueMap.GIT_HASH);
    }

    public static boolean supported(String version, String gitHash) {
        return BACKPORT_VERSION.equals(version) && BACKPORT_COMMIT.equals(gitHash);
    }
}

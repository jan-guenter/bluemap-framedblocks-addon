/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap523;

import de.bluecolored.bluemap.core.BlueMap;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.BlueMapRuntimeCompatibility;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsumerRuntimeCompatibilityTest {

    private static final String EXPECTED_VERSION =
            "5.22-feature.backport-5.23-stateless-java-web-server-46";
    private static final String EXPECTED_COMMIT =
            "7e07f4e74ec1e92a6ead9aa1e66054af3e133aac";

    @Test
    void acceptsOnlyAuditedExactIdentities() {
        assertTrue(BlueMapRuntimeCompatibility.matches(
                EXPECTED_VERSION,
                EXPECTED_COMMIT
        ));
        assertFalse(BlueMapRuntimeCompatibility.matches(
                EXPECTED_VERSION,
                EXPECTED_COMMIT + " (dirty)"
        ));
        assertFalse(BlueMapRuntimeCompatibility.matches(
                "5.22-agent.backport-5.22-mc1.21.1-2",
                "9be321df995a1103808621d529eb72773e719d4d"
        ));
        assertFalse(BlueMapRuntimeCompatibility.matches(
                EXPECTED_VERSION + "-dirty",
                EXPECTED_COMMIT
        ));
        assertFalse(BlueMapRuntimeCompatibility.matches(
                "5.22",
                "fe5115d5548a30d34175b8e0449aaca280af199f"
        ));
    }

    @Test
    void compositeBuildCarriesTheExpectedBackportIdentity() {
        assertEquals(EXPECTED_VERSION, BlueMap.VERSION);
        assertEquals(EXPECTED_COMMIT, BlueMap.GIT_HASH);
        assertTrue(BlueMapRuntimeCompatibility.matchesCurrent());
    }
}

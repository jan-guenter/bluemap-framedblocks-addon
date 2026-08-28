/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap522;

import de.bluecolored.bluemap.core.BlueMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdapterCompatibilityTest {

    @Test
    void acceptsOnlyAuditedExactIdentities() {
        assertTrue(AdapterCompatibility.supported(
                AdapterCompatibility.BACKPORT_VERSION,
                AdapterCompatibility.BACKPORT_COMMIT
        ));
        assertFalse(AdapterCompatibility.supported(
                AdapterCompatibility.BACKPORT_VERSION,
                AdapterCompatibility.BACKPORT_COMMIT + " (dirty)"
        ));
        assertFalse(AdapterCompatibility.supported(
                "5.22-agent.backport-5.22-mc1.21.1-1",
                "fe79cf5b9f4d8ca28f4e41c2aeb9ef792e336a8d"
        ));
        assertFalse(AdapterCompatibility.supported(
                "5.22-agent.backport-5.22-mc1.21.1-2-agent.backport-5.22-mc1.21.1",
                AdapterCompatibility.BACKPORT_COMMIT
        ));
        assertFalse(AdapterCompatibility.supported(
                "5.22",
                "fe5115d5548a30d34175b8e0449aaca280af199f"
        ));
    }

    @Test
    void compositeBuildCarriesTheExpectedBackportIdentity() {
        assertEquals(AdapterCompatibility.BACKPORT_VERSION, BlueMap.VERSION);
        assertEquals(AdapterCompatibility.BACKPORT_COMMIT, BlueMap.GIT_HASH);
        assertTrue(AdapterCompatibility.currentRuntimeSupported());
    }
}

/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap523;

import de.bluecolored.bluemap.core.world.mca.blockentity.BlockEntityType;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.FramedBlocks1061Profile;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlueMap523AdapterTest {

    @Test
    void installsExactHooksWithoutFreezingBlueNbtDuringAddonDiscovery() throws IOException {
        assertTrue(BlueMap523Adapter.install());

        for (var key : FramedBlocks1061Profile.blockEntityKeys()) {
            assertEquals(
                    FramedBlockEntityData.class,
                    BlockEntityType.REGISTRY.get(key).getBlockEntityClass(),
                    key.getFormatted()
            );
        }

        AdapterActivation activation = BlueMap523Adapter.activationForTesting();
        assertFalse(activation.isActive());
        assertFalse(activation.isDisabled());
        assertEquals("awaiting-exact-framedblocks-profile", activation.reason());

        // Resource-extension loading runs only after every add-on entrypoint.
        assertTrue(BlueMap523Adapter.probeBlockEntityRetention());
    }
}

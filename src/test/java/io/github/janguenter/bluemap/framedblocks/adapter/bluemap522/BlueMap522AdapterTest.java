/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap522;

import de.bluecolored.bluemap.core.world.mca.blockentity.BlockEntityType;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.FramedBlocks1061Profile;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlueMap522AdapterTest {

    @Test
    void installsExactHooksWithoutFreezingBlueNbtDuringAddonDiscovery() throws IOException {
        assertTrue(BlueMap522Adapter.install());

        for (var key : FramedBlocks1061Profile.blockEntityKeys()) {
            assertEquals(
                    FramedBlockEntityData.class,
                    BlockEntityType.REGISTRY.get(key).getBlockEntityClass(),
                    key.getFormatted()
            );
        }

        AdapterActivation activation = BlueMap522Adapter.activationForTesting();
        assertFalse(activation.isActive());
        assertFalse(activation.isDisabled());
        assertEquals("awaiting-exact-framedblocks-profile", activation.reason());

        // Resource-extension loading runs only after every add-on entrypoint.
        assertTrue(BlueMap522Adapter.probeBlockEntityRetention());
    }
}

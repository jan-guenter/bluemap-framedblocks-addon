/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap522;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.Key;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.FramedBlocks1061Profile;

/** Factory registered before BlueMap constructs any resource pack. */
final class FramedBlocksResourceExtensionType
        implements ResourcePack.Extension<FramedBlocksResourceExtension> {

    private final AdapterActivation activation;

    FramedBlocksResourceExtensionType(AdapterActivation activation) {
        this.activation = activation;
    }

    @Override
    public Key getKey() {
        return FramedBlocks1061Profile.RESOURCE_EXTENSION;
    }

    @Override
    public FramedBlocksResourceExtension create(ResourcePack pack) {
        return new FramedBlocksResourceExtension(pack, activation);
    }
}

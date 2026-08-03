/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks;

import io.github.janguenter.bluemap.framedblocks.adapter.bluemap522.AdapterCompatibility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * BlueMap add-on entrypoint. BlueMap constructs this class and invokes
 * {@link #run()} before it creates the resource pack.
 */
public final class BlueMapFramedBlocksAddon implements Runnable {

    public BlueMapFramedBlocksAddon() {
    }

    @Override
    public void run() {
        try {
            if (!AdapterCompatibility.currentRuntimeSupported()) {
                inactive("unsupported BlueMap internal ABI", null);
                return;
            }

            Class<?> adapterType = Class.forName(
                    "io.github.janguenter.bluemap.framedblocks.adapter.bluemap522.BlueMap522Adapter",
                    true,
                    BlueMapFramedBlocksAddon.class.getClassLoader()
            );
            Method install = adapterType.getMethod("install");
            install.invoke(null);
        } catch (InvocationTargetException exception) {
            inactive("exact adapter initialization failed", exception.getCause());
        } catch (ReflectiveOperationException | LinkageError | RuntimeException exception) {
            inactive("exact adapter is unavailable", exception);
        }
    }

    private static void inactive(String reason, Throwable cause) {
        String detail = cause == null ? "" : " (" + cause.getClass().getSimpleName() + ")";
        System.err.println(
                "BlueMap FramedBlocks add-on is inactive: " + reason + detail + "."
        );
    }
}

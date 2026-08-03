/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.diagnostics;

import de.bluecolored.bluemap.core.logger.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/** Bounded, location-free diagnostics for fallback and activation reasons. */
public final class BoundedDiagnostics {

    private static final int MAX_REASON_KEYS = 64;
    private static final ConcurrentHashMap<String, LongAdder> COUNTERS = new ConcurrentHashMap<>();

    private BoundedDiagnostics() {
    }

    public static void warning(String reason, String message) {
        increment(reason);
        Logger.global.noFloodWarning("bluemap-framedblocks:" + reason, message);
    }

    public static void info(String reason, String message) {
        increment(reason);
        Logger.global.noFloodInfo("bluemap-framedblocks:" + reason, message);
    }

    public static long count(String reason) {
        LongAdder counter = COUNTERS.get(reason);
        return counter == null ? 0L : counter.sum();
    }

    private static void increment(String reason) {
        LongAdder existing = COUNTERS.get(reason);
        if (existing != null) {
            existing.increment();
            return;
        }

        if (COUNTERS.size() >= MAX_REASON_KEYS) {
            return;
        }

        COUNTERS.computeIfAbsent(reason, ignored -> new LongAdder()).increment();
    }
}

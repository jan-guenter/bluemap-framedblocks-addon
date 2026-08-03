/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap522;

import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.GeometryTemplateProfile;

import java.util.Optional;

/** Shared fail-closed state for registry hooks that BlueMap cannot unregister. */
final class AdapterActivation {

    private volatile State state = State.INACTIVE;
    private volatile String reason = "not-installed";
    private volatile GeometryTemplateProfile geometryProfile;

    boolean isActive() {
        return state == State.ACTIVE;
    }

    boolean isDisabled() {
        return state == State.DISABLED;
    }

    String reason() {
        return reason;
    }

    Optional<GeometryTemplateProfile> geometryProfile() {
        return Optional.ofNullable(geometryProfile);
    }

    synchronized void activate(GeometryTemplateProfile profile) {
        if (state != State.DISABLED) {
            geometryProfile = profile;
            state = State.ACTIVE;
            reason = "exact-10.6.1";
        }
    }

    synchronized void inactive(String newReason) {
        if (state != State.DISABLED) {
            geometryProfile = null;
            state = State.INACTIVE;
            reason = newReason;
        }
    }

    synchronized void disable(String newReason) {
        geometryProfile = null;
        state = State.DISABLED;
        reason = newReason;
    }

    private enum State {
        INACTIVE,
        ACTIVE,
        DISABLED
    }
}

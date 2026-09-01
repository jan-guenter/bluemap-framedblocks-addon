# Installation and rollback

The add-on is not production-ready. See [coverage](COVERAGE.md) for the
complete remaining-gate list.

When approved, install the single production JAR into
`config/bluemap/packs`, then restart the JVM. Do not use `/bluemap reload` as
an installation, upgrade, or removal procedure because this BlueMap host has no
add-on unload or registry-unregister lifecycle.

Rollback:

1. stop the server;
2. remove only the `bluemap-framedblocks-addon` JAR;
3. restart the server;
4. verify BlueMap loads its original FramedBlocks resources.

The add-on writes no required world data, configuration state, blocks, items,
entities, or network payloads. No world conversion or cleanup is expected.

## Current compatibility-candidate status

The All the Mons 1.2.0 / NeoForge 21.1.248 / BlueMap feature-backport
commit `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac` has not yet completed the
enabled-to-stock-to-restored lifecycle below. Until it does, the observed row
remains historical evidence rather than a current-host rollback claim. Its
targeted composite gallery passed owner visual review on 2026-09-01.

## Historical observed isolated lifecycle

The accepted `0.1.0-alpha.1` expanded exact-profile artifact completed this
full-restart sequence in disposable All the Mons 1.1.1 full-pack staging:

1. enabled add-on discovery, exact-profile activation, fixture assertion, and
   bounded BlueMap render;
2. stop, remove only the known installed add-on target, and restart;
3. verify no add-on/profile loaded and render stock BlueMap under a separate
   map ID while the saved gallery and renderer-path fixtures remained
   unchanged;
4. stop, restore the exact JAR and enabled map configuration, and restart;
5. verify the exact profile, completed task queue, bounded fallback reasons,
   and all 15 renderer-path semantic signatures after the intentional
   rerender.

The stock phase did not overwrite the separately named enabled map. After
re-enabling, all 58 compared rendered web artifacts outside `rstate` were
byte-identical to the accepted enabled baseline. Six `rstate` bookkeeping
files changed, so whole-map-tree byte identity is not claimed. The exact
tested JAR was 1,326,527 bytes with SHA-256
`e7cb77c683e0b67bc66ffb35c52ef31f184b7b6184a6c3f9cf7b39c2ddf59745`.
No world conversion was required.

This validates removal and restoration for the exact isolated runtime row; it
is not a production deployment recommendation. `/bluemap reload` remains
invalid for installation, removal, or rollback.

## Historical M0 checkpoint

The earlier cube-only M0 lifecycle used artifact SHA-256
`d88efd3799e43bd92e5d230635ed096696e2a4a8cc57a1af428688345065c27a`.
It established the original material-delegation fix and removal mechanics but
does not identify or limit the expanded artifact above.

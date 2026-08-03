# Agent guide: BlueMap FramedBlocks Add-on

This is the standalone `bluemap-framedblocks-addon` repository. Read this
file, `README.md`, and `docs/RELEASING.md` before changing it. Portfolio research remains in the
parent workspace under `bluemap-addons/framedblocks/` and is evidence, not a
source directory to copy wholesale.

## Exact Phase 3 target

| Component | Locked identity |
| --- | --- |
| All the Mons | `1.1.1`, pack commit `94a224acf6eace3edf7ea64e6033b458f5bda288` |
| Minecraft | `1.21.1` |
| NeoForge | `21.1.234` |
| Java | `21` |
| FramedBlocks | `10.6.1`, SHA-1 `3007be0007d09c0225ca33b647461f342eac0503`, SHA-256 `3337f29e1fa3331e8740eef9c20b0750d81fd86d1057fb81012a5c4792aa3369` |
| FramedBlocks source | release-correlated commit `99522893fce0c9cd543194be1e8cefd488e0eec8` |
| BlueMap upstream | `5.22`, commit `fe5115d5548a30d34175b8e0449aaca280af199f` |
| BlueMap backport | commit `fe79cf5b9f4d8ca28f4e41c2aeb9ef792e336a8d` |
| BlueMapAPI fork | commit `285c9a60eff3ac2b0cab308ce1058d1565be0971` |

The initial implementation supports only the exact FramedBlocks artifact.
Later FramedBlocks versions require Phase 4 profiles and fixtures.

## Project invariants

- This is a plain BlueMap add-on, not a NeoForge mod. Do not add
  `neoforge.mods.toml`, Mixins, Minecraft registrations, payloads, client
  hooks, or a FramedBlocks runtime dependency.
- Never bundle BlueMap, BlueNBT, FramedBlocks, Minecraft, NeoForge, modpack
  resources, worlds, screenshots, chunks, or third-party JARs.
- Compile against the exact Java 21 BlueMap backport source checkout while
  keeping all internal calls inside `adapter/bluemap522`.
- Unknown BlueMap or FramedBlocks builds remain inactive. Malformed NBT and
  unsupported states use the direct stock-resource fallback without
  interrupting BlueMap.
- Every registry insertion is verified by identity. Do not trust BlueMap
  5.22's inverted `Registry.register()` return value.
- Register block-entity DTO types in the add-on entrypoint, but never trigger
  BlueNBT deserialization there. BlueNBT snapshots the type registry on first
  use; capability probes belong in resource-extension loading after every
  add-on entrypoint has run.
- There is no unload lifecycle. Installation, update, removal, and rollback
  require a JVM restart; no required world state may be written.
- The exact positive-support profile is generated and digest-bound. Do not
  silently broaden its 206-ID route, material proof, or runtime gates.
- Keep diagnostics bounded and once-per-reason. Do not log NBT, locations,
  player data, server paths, or resource contents.
- Keep all source under `LGPL-3.0-only` unless a future file records a
  separately reviewed SPDX and provenance lane.

## Source and provenance

The repository uses the adapted-source lane. Update `provenance/upstreams.json`
and `docs/PROVENANCE.md` whenever a file is adapted from another exact source.
FramedBlocks-derived geometry, coordinate tables, UV rules, or culling logic
must not be moved into a permissive shared toolkit.

The tracked schema-v3 gzip is the preferred editable source form for the
shipped projected geometry. The private schema-v2 capture is provenance
evidence and optional regeneration input, not a normal build requirement.

## Release rules

- Every version increase after the initial public snapshot must enter `main`
  through a pull request.
- A release tag must be exactly `v<addon_version>` and point to `main`.
- Snapshot versions cannot be released. Prerelease suffixes produce GitHub
  prereleases.
- Never publish a Maven package or GitHub Release before CI, POM, production-
  JAR, provenance, exact-artifact, and reproducibility checks pass.
- Published Maven coordinates and release tags are immutable. Follow the
  retained-draft recovery procedure instead of republishing a version.

## Validation

Run from this repository:

```bash
./gradlew --no-daemon clean check build
./gradlew --no-daemon geometryProjectionClasses \
  generatePomFileForAddonPublication verifyPublicationPom
./gradlew --no-daemon \
  -PframedblocksJar=/absolute/path/FramedBlocks-10.6.1.jar \
  verifyPinnedArtifact
```

Run `python3 gallery/generate.py --check` and verify `gallery/SHA256SUMS`.
Inspect the production JAR. It must contain only project classes, the one
namespace-disjoint synthetic renderer blockstate, exact compatibility profile,
and license/provenance documents. Do not claim a BlueMap load, Anvil parse,
map render, client comparison, full-pack startup, or performance result unless
that exact gate ran and its evidence was retained privately.

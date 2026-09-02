# Contributing

This project is an experimental, exact-version BlueMap renderer add-on. Before
opening a change, read `README.md`, `AGENTS.md`, and the relevant architecture,
coverage, compatibility, and provenance documents under `docs/`.

## Scope

The current profile supports only FramedBlocks `10.6.1` and the exact Java 21
BlueMap 5.23 feature-backport commit identified in `AGENTS.md`. Compatibility with a different
artifact, BlueMap ABI, Minecraft version, resource stack, or FramedBlocks
release must be demonstrated rather than inferred.

Keep the production artifact a plain BlueMap add-on. Do not add a client or
NeoForge runtime dependency, bundle third-party JARs or assets, or silently
broaden a fail-closed rendering policy.

## Development

The default build expects the exact BlueMap backport commit on the named
feature branch in a clean sibling `../bluemap-backport` checkout. An
alternative matching checkout can be supplied with
`-PbluemapSourcePath=/absolute/path/to/BlueMap`.

Clone recursively or initialize both gitlinks before Gradle runs. The build
rejects any Adapter API checkout that differs from the commit and source-tree
pins in `settings.gradle`; never copy or edit those shared files locally.

Run before submitting a pull request:

```bash
python3 gallery/generate.py --check
(cd gallery && sha256sum --check SHA256SUMS)
python3 -m json.tool provenance/upstreams.json >/dev/null

./gradlew --no-daemon clean check build \
  geometryProjectionClasses \
  generatePomFileForAddonPublication \
  generateMetadataFileForAddonPublication \
  verifyPublicationPom
```

When the exact FramedBlocks `10.6.1` artifact is available locally, also run:

```bash
./gradlew --no-daemon \
  -PframedblocksJar=/absolute/path/FramedBlocks-10.6.1.jar \
  verifyPinnedArtifact
```

Do not commit that artifact, client resources, raw captures, worlds,
screenshots containing private server data, credentials, or generated build
output. Public CI downloads the pinned FramedBlocks input ephemerally and
verifies it before use.

## Changes and evidence

- Add or update tests for every behavior change.
- Preserve direct stock-resource fallback for unknown or unsupported input.
- Update `provenance/upstreams.json`, `NOTICE.md`, and `docs/PROVENANCE.md` when
  adapted inputs or derived geometry change.
- Record only tests that actually ran. Clearly distinguish technical captures
  from human-approved or pixel-repeatable visual acceptance.
- Keep Java and generated source under `LGPL-3.0-only` unless a separately
  reviewed file records another compatible license and provenance lane.

Every version increase after the initial public snapshot must enter `main`
through a pull request. Maintainer release steps are documented in
`docs/RELEASING.md`.

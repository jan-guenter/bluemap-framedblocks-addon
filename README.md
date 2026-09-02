# BlueMap FramedBlocks Add-on

[![CI](https://github.com/jan-guenter/bluemap-framedblocks-addon/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/jan-guenter/bluemap-framedblocks-addon/actions/workflows/ci.yml)
[![Release](https://github.com/jan-guenter/bluemap-framedblocks-addon/actions/workflows/release.yml/badge.svg)](https://github.com/jan-guenter/bluemap-framedblocks-addon/actions/workflows/release.yml)

A BlueMap 5.23 feature-backport add-on for rendering FramedBlocks without
loading Minecraft's client renderer.

## Current status

**Experimental pre-release — exact-version support only.** This alpha targets:

- All the Mons `1.2.0`, Minecraft `1.21.1`, NeoForge `21.1.248`, Java `21`;
- FramedBlocks `10.6.1` only;
- upstream BlueMap `5.23` behavior through exact Java 21 feature-backport
  build `5.22-feature.backport-5.23-stateless-java-web-server-46` at commit
  `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac`, with BlueMapAPI commit
  `285c9a60eff3ac2b0cab308ce1058d1565be0971`;
- the four shared integration primitives compiled from Adapter API
  `0.1.0-alpha.2` at commit
  `e81f08bc4bfbf02d810ec8949a019130e2e61634` and source tree
  `2f974c9bb2ba13888d69682f86f30f58922d30eb`.

The current exact-profile lane recognizes the hash-locked FramedBlocks
JAR, retains the 51 ordinary FramedBlocks block-entity payloads, and consumes a
schema-v3 production projection derived from a private, twice-byte-identical
schema-v2 physical capture of the exact client/config/resource-pack
fingerprint. It maps 74,180 renderable raw states onto 5,382 representative
geometry templates and retains 16 explicit null saw aliases. The projection
preserves every raw-state, template, and alias identity while emptying 524
templates for 28 client-dynamic families. It therefore ships 58,708 quads,
rather than the private capture's 62,746, and references 18 fixed sprites
confined to the `minecraft` and `framedblocks` namespaces. Those 28 families
now use bounded surrogate geometry, manual compact bodies, or placeholder-
only camouflage substitution over BlueMap's stock body model.

Runtime lookup requires the complete exact state key; unknown, omitted, or
extra properties fall back. The exact dispatch set contains all 234 rendered
block IDs. Of those, 206 use projected geometry and 28 BER, adjustable,
collapsible, flower-pot, one-way-window, and special-overlay families use
their bounded family renderers.

Routing is additionally fail-closed per state and render context. Waterlogged,
dynamic-light/skylight, reinforced, and unsupported framed-to-framed-adjacent
blocks use the original FramedBlocks resource fallback. Matching upper and
lower halves of the same door are handled as one shape. Fallback is a safety
path, not a claim of client-equivalent rendering for model-data- or
block-entity-renderer-driven blocks. Block camouflage is accepted when
BlueMap's baked resource metadata proves either one canonical untransformed
opaque full-cube variant or a bounded set of uniform-material opaque full-cube
variants. The latter lane admits Minecraft 1.21.1's normal, mirrored, and
quarter-turned stone alternatives only after every face of every alternative
resolves to the same non-animated texture, tint, and emission. It normalizes
their UV orientation because FramedBlocks does not persist the random client
cache choice in Anvil data; shape and material are reproduced, but random
texture orientation is not pixel-identical to an arbitrary client session.
Fluid, cutout, translucent, non-occluding, materially directional,
multipart, multi-element, random-offset, always-waterlogged, or otherwise
unproven camouflage falls back. BlueMap does not
expose the client `BakedQuad` or render-layer result, so metadata alone cannot
identify every mod-supplied client wrapper; that residual case remains outside
the accepted support claim.

The projection header binds its private source capture to an exact client
fingerprint, but the add-on does not cryptographically inventory and compare
the resource stack actually loaded by BlueMap at runtime. A differing
server-side resource stack is therefore a deployment and visual-acceptance
risk, not something the fingerprint gate proves absent.

The previously accepted `0.1.0-alpha.1` artifact passed its Java 21 build,
52-test suite, generated-profile integrity, production-JAR, POM,
exact-artifact, and reproducibility gates against the All the Mons 1.1.1 host.
In isolated staging it also passed a 234-anchor default-state gallery, a
15-case renderer-path matrix represented by 16 physical blocks, and a full
enabled-to-stock-to-re-enabled lifecycle. Three matrix cases used add-on
geometry; twelve intentionally used bounded stock fallback. All 58 compared
web-render artifacts outside `rstate` were byte-identical after re-enabling;
six `rstate` bookkeeping files changed. That artifact and host identity remain
historical rollback evidence, not validation of the current 1.2.0 candidate.

The `0.1.0-alpha.3` renderer passed its local Java 21 tests, build, POM,
production-JAR audit, exact-host activation, and targeted full-pack composite
render. The owner accepted its FramedBlocks gallery on 2026-09-01. The current
`0.1.0-alpha.4` candidate changes source ownership and packaging only. It must
repeat the combined integration render before inheriting that visual result.
The older enabled-to-stock-to-restored lifecycle remains historical evidence.

Two historical fixed-view modded-client captures and a BlueMap software-WebGL
overview provide qualitative technical evidence. The `0.1.0-alpha.3`
integration gallery has owner visual acceptance, but it is not a pixel-repeatable
comparison. The gallery places one default state for each
placeable/displayable ID, not all 74,196 projected states. Add-on-owned framed-
neighbor culling, comprehensive model-data/BER and non-default-state coverage,
malformed/reload/concurrency matrices, performance budgets, and production
deployment remain open. Intentional fallback preserves BlueMap operation but
is not necessarily client-equivalent.

This is an unofficial community add-on and is not affiliated with or endorsed
by BlueMap or FramedBlocks. Do not treat this alpha as production-ready.

## Architecture

The production JAR is a plain BlueMap add-on rather than a NeoForge mod. It
contains no FramedBlocks, Minecraft, NeoForge, BlueMap, or BlueNBT classes.

```text
BlueMap add-on entrypoint
        |
exact BlueMap 5.23 feature-backport adapter and inactive-by-default registry hooks
        |
FramedBlocks 10.6.1 artifact/profile gate
        |
bounded NBT decoder -> normalized camouflage state
        |
digest-validated schema-v3 projected aliases/templates + bounded dynamic-family renderers + conservative runtime gates
        |
strict or uniform-weighted opaque full-cube material substitution, otherwise original-resource fallback
```

See [architecture](docs/ARCHITECTURE.md),
[compatibility evidence](docs/COMPATIBILITY.md),
[provenance](docs/PROVENANCE.md), and [coverage](docs/COVERAGE.md).

## Build

Clone recursively. The build compiles four exact shared Adapter API source
files into the add-on and refuses a missing, dirty, or differently pinned
module checkout. It also requires the exact BlueMap feature-backport commit on
its named branch in a clean checkout:

```bash
git clone --recurse-submodules \
  https://github.com/jan-guenter/bluemap-framedblocks-addon.git
git -C ../bluemap-backport checkout \
  feature/backport-5.23-stateless-java-web-server
test "$(git -C ../bluemap-backport rev-parse HEAD)" = \
  7e07f4e74ec1e92a6ead9aa1e66054af3e133aac
./gradlew --no-daemon clean check build
```

For another checkout location:

```bash
./gradlew --no-daemon \
  -PbluemapSourcePath=/absolute/path/to/BlueMap \
  clean check build
```

To independently verify the locally acquired FramedBlocks artifact and the
tracked resource manifest:

```bash
./gradlew --no-daemon \
  -PframedblocksJar=/absolute/path/FramedBlocks-10.6.1.jar \
  verifyPinnedArtifact
```

The public CI additionally regenerates and checksum-checks the gallery,
validates provenance packaging, compiles the optional geometry projector, and
downloads the exact FramedBlocks test input ephemerally from its pinned
Modrinth version. That third-party JAR is verified, then discarded; it is never
published as a build artifact.

## Releases and Maven package

Tagged versions are published on the [GitHub Releases page](https://github.com/jan-guenter/bluemap-framedblocks-addon/releases).
The release workflow requires `tag == v<addon_version>`, two byte-identical
builds of the production JAR, sources JAR, POM, and Gradle module metadata,
the complete CI gate, and a changelog entry. It uploads the production JAR,
sources JAR, POM, SHA-256 checksums, and build-provenance attestations.
Versions containing a SemVer prerelease suffix are published as GitHub
prereleases.

The same release is published to GitHub Packages as:

```text
io.github.jan-guenter:bluemap-framedblocks-addon:<version>
```

GitHub's Maven registry requires authenticated reads, including for public
packages. The GitHub Release JAR is therefore the normal installation
artifact. The Maven package is intended for development and integration use.
The tagged repository—not the convenience sources JAR—is the complete source
distribution.

Maintainer publication and failure-recovery steps are documented in the
[release procedure](docs/RELEASING.md).

## Installation and rollback

No production installation is recommended yet. The isolated expanded
lifecycle used the single JAR in BlueMap's `config/bluemap/packs` directory.
BlueMap treats it both as an add-on and as a resource pack. A full JVM restart
is required.

Rollback is stop, remove the JAR, and restart. The add-on writes no required
world or add-on configuration state, so no migration or conversion is
expected. See [the observed isolated lifecycle](docs/ROLLBACK.md).

## License

`LGPL-3.0-only`. The complete LGPL/GPL texts are in [LICENSE](LICENSE) and
`LICENSES/`; see [NOTICE.md](NOTICE.md) and [THIRD_PARTY.md](THIRD_PARTY.md).
Contributions follow [CONTRIBUTING.md](CONTRIBUTING.md), and private security
reports follow [SECURITY.md](SECURITY.md).

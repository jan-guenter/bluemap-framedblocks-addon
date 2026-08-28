# BlueMap FramedBlocks Add-on

[![CI](https://github.com/jan-guenter/bluemap-framedblocks-addon/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/jan-guenter/bluemap-framedblocks-addon/actions/workflows/ci.yml)
[![Release](https://github.com/jan-guenter/bluemap-framedblocks-addon/actions/workflows/release.yml/badge.svg)](https://github.com/jan-guenter/bluemap-framedblocks-addon/actions/workflows/release.yml)

A BlueMap 5.22 add-on for rendering FramedBlocks without loading Minecraft's
client renderer.

## Current status

**Experimental pre-release — exact-version support only.** This alpha targets:

- All the Mons `1.2.0`, Minecraft `1.21.1`, NeoForge `21.1.248`, Java `21`;
- FramedBlocks `10.6.1` only;
- upstream BlueMap `5.22` internal renderer ABI through exact Java 21 backport
  `5.22-agent.backport-5.22-mc1.21.1-2` at commit
  `9be321df995a1103808621d529eb72773e719d4d`.

The current exact-profile lane recognizes the hash-locked FramedBlocks
JAR, retains the 51 ordinary FramedBlocks block-entity payloads, and consumes a
schema-v3 production projection derived from a private, twice-byte-identical
schema-v2 physical capture of the exact client/config/resource-pack
fingerprint. It maps 74,180 renderable raw states onto 5,382 representative
geometry templates and retains 16 explicit null saw aliases. The projection
preserves every raw-state, template, and alias identity while emptying 524
templates for the 28 block-level fallback IDs. It therefore ships 58,708
quads, rather than the private capture's 62,746, and references 18 fixed
sprites confined to the `minecraft` and `framedblocks` namespaces.

Runtime lookup requires the complete exact state key; unknown, omitted, or
extra properties fall back. The base policy positively routes 206 state-only
block IDs and keeps 28 BER, adjustable, collapsible, flower-pot,
one-way-window, and special-overlay IDs on BlueMap's original-resource
fallback.

Routing is additionally fail-closed per state and render context. Waterlogged,
dynamic-light/skylight, reinforced, and framed-to-framed-adjacent blocks use
the original FramedBlocks resource fallback. That fallback is a routing-safety
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

The current source retargets only the exact BlueMap backport host identity.
FramedBlocks 10.6.1 and every audited BlueMap adapter-facing source blob are
unchanged; the host's implementation delta is its NeoForge 21.1.248 compile
pin. A fresh build, exact-host load, gallery render, stock rollback, restored
render, and human visual acceptance remain required before this candidate can
replace the accepted historical artifact.

Two fixed-view modded-client captures and a BlueMap software-WebGL overview
provide qualitative technical evidence. They are not human-approved or
pixel-repeatable comparisons. The gallery places one default state for each
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
exact BlueMap 5.22 adapter and inactive-by-default registry hooks
        |
FramedBlocks 10.6.1 artifact/profile gate
        |
bounded NBT decoder -> normalized camouflage state
        |
digest-validated schema-v3 projected aliases/templates + positive support policy + conservative runtime gates
        |
strict or uniform-weighted opaque full-cube material substitution, otherwise original-resource fallback
```

See [architecture](docs/ARCHITECTURE.md),
[compatibility evidence](docs/COMPATIBILITY.md),
[provenance](docs/PROVENANCE.md), and [coverage](docs/COVERAGE.md).

## Build

The build requires the exact sibling BlueMap backport tag in a clean detached
checkout by default:

```bash
git -C ../bluemap-backport checkout --detach \
  v5.22-agent.backport-5.22-mc1.21.1-2
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

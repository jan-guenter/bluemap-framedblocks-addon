# Notices

Copyright (C) 2026 Jan Günter

This project is an independently packaged BlueMap add-on implementing
compatibility with FramedBlocks.

FramedBlocks is copyright XFactHD and contributors and is distributed under
the GNU Lesser General Public License v3.0 only. Persistence and rendering
behavior for the initial profile were studied at release-correlated commit
`99522893fce0c9cd543194be1e8cefd488e0eec8`.

BlueMap is copyright Blue (Lukas Rieger) and contributors and is distributed
under the MIT License. This project compiles against its interfaces but does
not bundle BlueMap source or binaries.

BlueMap Add-on Adapter API is copyright Jan Günter and contributors and is
distributed under the MIT License. This project compiles its four exact
gitlink-pinned production sources into the add-on. The module's MIT license is
packaged as `LICENSE-bluemap-addon-adapter-api`.

The complete source-use record is in `provenance/upstreams.json` and
`docs/PROVENANCE.md`.

## Modification notice

On 2026-08-02, this project independently adapted the render-relevant
FramedBlocks 10.6.1 persistence behavior into the reduced DTO and bounded
camouflage decoder identified in `provenance/upstreams.json`. These are
modified interoperability implementations; they are not verbatim copies and
do not bundle FramedBlocks classes or assets.

On 2026-08-03, this project also prepared an LGPL-3.0-only schema-v3 neutral
geometry projection from a private, twice-byte-identical schema-v2 physical
capture of the exact FramedBlocks client rendering result. The projection
preserves state, template, and alias identity but omits geometry for the 28
client-dynamic family IDs. The add-on bundles this derived geometry data and
sprite identifiers; it does not bundle the raw capture, FramedBlocks code or
binaries, textures, model files, source archives, or JARs.

The tracked schema-v3 gzip is canonical UTF-8 JSON under deterministic
compression and is the preferred form for modifying the geometry shipped by
the add-on. The private schema-v2 captures are optional provenance and
regeneration evidence; they are not required to build or modify the shipped
source.

On 2026-08-03, the BlueMap adapter also gained a bounded material-proof lane
for uniform weighted full-cube variants and stable, bounded fallback reasons.
The proof was tested with exact Minecraft 1.21.1 stone blockstate/model
resources, which remain non-bundled evidence. Because FramedBlocks does not
persist the random client model-cache choice in Anvil, the adapter normalizes
mirrored/rotated UV orientation and claims matching geometry and material, not
pixel-identical randomized texture orientation.

On 2026-09-01, the adapter added bounded rendering for the 28 client-dynamic
families. It uses projected surrogate geometry, independently defined compact
bodies, or placeholder-only camouflage substitution over BlueMap's stock body
model. No FramedBlocks or Minecraft model, texture, code, or binary was added.

On 2026-09-02, the add-on replaced its local runtime-identity, registry,
resource-extension-wrapper, and synthetic-dispatch helpers with the exact four
source-compiled Adapter API classes. Renderer, profile, camouflage, geometry,
activation, BlueNBT probe, and fallback behavior remain consumer-owned.

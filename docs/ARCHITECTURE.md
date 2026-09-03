# Architecture

## Boundaries

`profile/framedblocks10_6` contains the exact artifact identity, digest-checked
resource inventories, bounded persisted-data decoder, projected
geometry-template schema, and closed support classification. It has no
FramedBlocks or Minecraft class dependency.

`adapter/bluemap523` holds FramedBlocks-owned BlueMap integration. It installs
the block-entity DTO, custom renderer type, and resource extension before
BlueMap creates its resource pack.

The exact gitlink-pinned Adapter API contributes four source-compiled classes:
runtime identity, registry admission, the resource-extension wrapper, and
synthetic dispatch validation. The add-on keeps activation, registration
plans, BlueNBT probes, profile policy, renderer behavior, and failure reasons.
No Adapter API JAR is installed or nested.

`diagnostics` contains bounded reason counters and no-flood logging.

## Activation sequence

1. The shared runtime bootstrap rejects every BlueMap runtime except the exact
   5.23 feature-backport build and commit before reflectively loading the
   consumer-owned internal adapter; linkage failures remain contained at the
   entrypoint.
2. Preflight every `framedblocks:framed_tile` and namespace-disjoint renderer
   and resource-extension key with the shared registry guard, register them,
   verify their identities, then leave routing inactive.
3. Do not deserialize NBT during add-on discovery. BlueNBT snapshots all
   possible DTO types on first use, so every add-on must register its types
   before any add-on triggers that freeze.
4. During resource-extension loading, hash candidate FramedBlocks roots and
   continue only for the exact 10.6.1 SHA-256.
5. Require the synthetic blockstate's exact renderer/model structure rather
   than accepting its name alone.
6. Load and validate the schema-v3 production projection: 236 block IDs,
   74,196 raw states, 74,180 renderable aliases, 5,382 representative
   templates, 58,708 quads, 18 fixed sprites, the 16 expected null saw aliases,
   and the closed 206 projected-geometry/28 dynamic-family policy. The 28
   dynamic-family IDs are also bound by their canonical LF-list digest.
7. Prove BlueNBT retains the exact nested plain-stone payload, ID, coordinates,
   flags, and data version.
8. Activate only after all preceding checks pass.

Any collision or failed probe leaves the shared activation flag inactive.
Because BlueMap has no add-on unload hook, changes require a full restart.

Registry preflight prevents expected collisions from producing a partial
installation. BlueMap provides neither transactions nor unregister operations,
so an unrelated concurrent registry mutation between preflight and registration
cannot be made formally atomic by the add-on.

## Exact-profile rendering

All 234 rendered IDs are redirected to the synthetic dispatch resource. Each
render consults the machine-readable status/family/reason classification
again. The 206 state-only families use projected geometry. The 28 client-
dynamic families use bounded surrogates, manual bodies, or stock-model
placeholder substitution. Waterlogged, dynamic-light/skylight, and reinforced
contexts go directly to the original FramedBlocks resource. Routed blocks next
to another framed block remain in the projected path instead of exposing the
stock wooden frame.

The geometry renderer performs only an exact raw-state-key lookup and consumes
the referenced representative template. It does not recreate FramedBlocks'
`StateMerger` or infer default/subset properties. An unknown state or a runtime
state with omitted or extra properties therefore uses stock fallback. This is
intentional: accepting a plausible subset would silently bind geometry to the
wrong client state. Exported face UVs, winding, cull direction, ambient
occlusion, and block/sky light values remain explicit inputs. Primary
camouflage and a template-conditional secondary camouflage may supply face
materials. Projected faces with a cull direction use BlueMap's general
neighborhood culling properties. This removes faces against culling neighbors
but does not recreate every FramedBlocks client-side, shape-aware hidden-face
decision.

The private physical capture uses schema v2 to separate the complete registry
inventory from deduplicated geometry:

- `templates[]` is strictly sorted by representative canonical state key;
- `states[]` is strictly sorted by raw canonical state key and contains an
  integer template reference or an explicit `null` for a saw state;
- every template is referenced, has a representative self-alias, and can be
  referenced only by raw states with the same block ID;
- the two saw IDs are the only null-alias family and cannot own templates.

A canonical key starts with the block ID and appends NUL-separated
`name=value` pairs in property-name order. Property names and values are
non-empty printable ASCII and cannot contain `=`. Four headers independently
cover the sorted raw-state keys, sorted renderable-state keys, sorted template
keys, and raw-to-representative alias pairs. The alias byte stream contains two
LF-terminated UTF-8 lines per renderable raw state, ordered by raw key; null saw
rows are excluded. Even an empty inventory hashes one LF, matching the exporter.
Malformed fields, counts, ordering, duplicates, references, canonical
components, digests, or bounded sizes fail closed.

The bundled resource is a deterministic schema-v3 production projection of
that capture. It preserves the ordering and identity of all 74,196 state rows,
5,382 template rows, and 74,180 non-null alias relationships. It empties the
geometry lists of the 524 templates owned by the 28 client-dynamic family IDs,
removing 4,038 captured quads while retaining the template identities needed
for exact lookup and policy validation. The private capture contains 62,746
quads; the projection contains 58,708. Its exclusion-ID list is canonical, LF-terminated,
and bound by SHA-256
`f0be4b79e8ee82686414b5745634d9096d272fc16730ef06bb1de7a15de62529`.
The 18 remaining fixed sprite identifiers are restricted to the `minecraft`
and `framedblocks` namespaces.

The schema-v3 file contains neutral numeric geometry, state/alias identity,
and sprite identifiers derived from the FramedBlocks client rendering result.
It is therefore distributed under `LGPL-3.0-only`. The raw schema-v2 capture is
private and untracked. Neither form contributes a third-party binary, texture,
model file, source archive, or JAR to the production add-on.

The observed 4,858 figure counts templates whose block ID belongs to a base
projected-geometry family. It is an activation inventory check, not a
rendering-safety shortcut. Empty-geometry and routing decisions are still
evaluated for every complete raw state so waterlogged and dynamic-property fallbacks cannot inherit
the base-family result.

Camouflage substitution is deliberately narrower than ordinary BlueMap model
rendering. The strict lane requires exactly one untransformed variant and one
shaded, ambient-occluded, non-emissive 0..16 cube element. All six faces must
use full 0..16 UVs, zero face rotation, their matching cull face, and an opaque
texture. A second bounded lane accepts at most 16 weighted alternatives only
when every positive finite-weight, non-UV-locked default-renderer alternative
is a full cube with quarter-turn transforms and every face across every model
resolves to one identical opaque, non-animated texture, tint index, and
emission value. Both lanes require effective block properties to be culling
and occluding, non-random-offset, and not always-waterlogged.

The bounded weighted lane is a deliberate material-level approximation.
FramedBlocks 10.6.1 supplies its caller's random source to the camouflage baked
model and caches the chosen quads, but that client cache choice is not persisted
in Anvil. Minecraft 1.21.1 stone therefore has provably equivalent material
across its normal, mirrored, and 180-degree alternatives, while its random UV
orientation cannot be recovered by BlueMap. The add-on normalizes that UV
orientation and claims correct geometry/material, not client-pixel-identical
texture orientation. Multipart, materially directional, animated-weighted,
arbitrary-transform, cutout/translucent, emissive, fluid, or otherwise
unresolved camouflage falls back.

Fallback invokes the original FramedBlocks blockstate resource through
BlueMap's ordinary resource renderer while bypassing the add-on's synthetic
route. This prevents recursion and contains failures; it does not make dynamic
model-data or block-entity-renderer families client-equivalent.

The exact BlueMap 5.23 feature backport does not expose the modded client's
final `BakedQuad` stream or render-layer decision. Consequently the metadata proof cannot distinguish an
arbitrary client wrapper that presents canonical cube metadata but replaces it
later in the client pipeline. Such cases are outside the accepted support
claim and remain a candidate for a future explicit camouflage allowlist.

The client fingerprint carried through the projection identifies the capture
environment. Activation validates that recorded identity, but it does not
cryptographically enumerate BlueMap's live server-side resource stack and
compare it with the client's stack. Resource-pack order or content can
therefore diverge without failing activation. Exact deployment control and
client-versus-BlueMap visual acceptance remain required; the fingerprint is
not a runtime resource-stack attestation.

## M0 rendering

This is the historical narrow checkpoint retained for evidence; the
exact-profile lane above supersedes its implementation design.

Only a `framedblocks:framed_cube` whose retained primary camouflage decodes to
plain `minecraft:stone` enters the M0 delegate. The delegate selects the stone
resource variants and calls BlueMap's `ResourceModelRenderer` directly.

All other states use the original FramedBlocks blockstate resource directly
from `ResourcePack.getBlockStates()`. That bypasses extension routing and
prevents recursive fallback. Multipart variants are composed with isolated
model views and colors, and unresolved models or renderer exceptions fall
through to a guarded missing-resource path rather than escaping the tile.

The original neighborhood remains visible to the stock delegate. Therefore
M0 does not establish tinting, random offsets, connected behavior,
camouflage-aware face culling, fluids, second camouflage, modifiers, or custom
shape geometry.

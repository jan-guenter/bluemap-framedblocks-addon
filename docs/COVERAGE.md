# FramedBlocks 10.6.1 coverage

## Current exact-profile lane

The current implementation consumes a bounded schema-v3 production projection
derived from a private schema-v2 physical capture of the exact FramedBlocks
10.6.1 client/config/resource-pack fingerprint. It validates 236 block IDs,
74,196 complete raw states, 74,180 renderable aliases, 5,382 representative
geometry templates, 58,708 projected quads, and the canonical inventory,
alias, and exclusion-ID digests. The remaining 16 raw states are explicit null
aliases: eight states for each of the two saw IDs. Its closed, positive support
policy classifies the 234 rendered block IDs as follows:

| Classification | Block IDs | Behavior |
| --- | ---: | --- |
| State-only baked geometry | 206 | Projected-geometry renderer |
| Block-entity renderer | 8 | Manual body or stock-body placeholder substitution |
| Adjustable model data | 4 | Double-panel or double-slab surrogate geometry with two camouflage slots |
| Collapsible model data | 2 | Stock-body placeholder substitution |
| Flower model data | 1 | Manual pot body plus stock flower resource |
| One-way-window model data | 1 | Stock-body placeholder substitution |
| Special camouflage overlay | 12 | Stock-body placeholder substitution, including the Camol overlay renderer |

The private capture contains 62,746 quads. Projection leaves all state,
template, and alias identities intact but empties 524 templates belonging to
the 28 client-dynamic family IDs, removing 4,038 captured quads that the
family renderers do not use. The retained geometry uses 18 fixed sprite
identifiers, all in the `minecraft` or `framedblocks` namespace. Empty
dynamic-family templates remain in the inventory so policy and exact-state
validation cannot silently broaden coverage.

Unknown IDs never become supported implicitly. Dispatched IDs also fall back
for waterlogged, dynamic-light/skylight, or reinforced contexts. Routed blocks
beside other framed blocks remain on the projected geometry path, which avoids
exposing the stock wooden frame instead of camouflage. The renderer applies
BlueMap's general neighborhood culling metadata, not FramedBlocks' complete
client-side, shape-aware hidden-face model data.
The fallback renders the original resource without recursing through the
add-on. It keeps routing failures contained, but it cannot reconstruct omitted
client model data or block-entity-renderer output and is not classified as
client-equivalent support.

Runtime state matching is exact. The add-on deliberately performs no subset,
default-property, or merger completion: missing, extra, or unknown properties
produce stock fallback. Multiple raw states may reference one representative
template only within the same block ID, and every representative must retain a
self-alias.

Block camouflage is accepted when BlueMap resource metadata proves either a
single canonical untransformed opaque full cube or at most 16 weighted
full-cube alternatives whose faces all collapse to one identical opaque,
non-animated texture, tint, and emission. The weighted lane covers Minecraft
1.21.1 stone's normal, mirrored, and 180-degree alternatives, but normalizes
their random UV orientation: FramedBlocks' selected client-cache alternative
is not persisted in Anvil. This lane therefore claims correct material and
geometry, not pixel-identical random texture orientation. Random-offset,
always-waterlogged, multipart, materially directional, arbitrary-transform,
fluid, and all other unproven camouflage falls back. BlueMap does not expose
the actual modded-client
`BakedQuad` or render-layer result, so this proof cannot detect every possible
client wrapper whose resource metadata itself looks canonical. That is a
remaining limitation, not supported behavior.

The exported-geometry lane preserves exported ambient-occlusion and block/sky
light inputs. Its bounded expanded runtime fixtures have passed. The
`0.1.0-alpha.3` combined integration gallery passed owner visual review on
2026-09-01. The malformed-profile, concurrency, and performance limits below
still constrain the support claim.

Its recorded client fingerprint is not compared with a cryptographic inventory
of the resource stack BlueMap actually loads. A server-side resource-pack
difference can therefore change sprite or camouflage resolution without
causing the exact-profile gate to reject activation. Matching deployment inputs
and client-versus-BlueMap visual acceptance remain explicit requirements.

## Current integration review

The exact BlueMap feature-backport host activated the `0.1.0-alpha.3` profile
in the All the Mons 1.2.0 combined integration server. A targeted
FramedBlocks render completed with zero container restarts, and the owner
accepted the gallery on 2026-09-01. The `0.1.0-alpha.4` candidate retains the
same renderer and profile but changes shared-source ownership and packaging,
so it still needs a combined rerender. Neither result is a pixel-by-pixel
proof or an enabled-to-stock-to-restored lifecycle.

## Historical expanded exact-profile fixtures

The generated profile validates all 236 resource IDs, 74,196 complete raw
states, 74,180 renderable aliases, 5,382 templates, 58,708 projected quads,
18 fixed sprites, and 16 null saw aliases. This is a profile-integrity result,
not runtime visual acceptance of every state.

The All the Mons 1.1.1 isolated gallery constructed and server-verified one
generated default-state anchor for each of the 234 placeable/displayable IDs. Its
separate renderer-path matrix represented 15 logical cases with 16 physical
framed blocks:

- a stone cube, stone/gold double slab, and oriented stone slope used exact
  add-on geometry and materials;
- twelve cases intentionally used the original-resource fallback for selected
  model-data, BER, overlay, waterlogged, dynamic-light/skylight, reinforced,
  adjacent-framed, and non-opaque-material conditions.

The final exact artifact completed an enabled-to-stock-absent-to-re-enabled
full-restart lifecycle with zero container restarts in each accepted run. All
58 compared rendered web artifacts outside `rstate` were byte-identical after
re-enabling. Six `rstate` bookkeeping files changed, so whole-tree byte
identity is not claimed. Two fixed-view client captures and a BlueMap WebGL
overview passed as qualitative technical references only.

## Current renderer regression gallery

The unreleased adjacency candidate's canonical gallery build includes 16
logical renderer-path cases with 18 physical framed blocks. Five cases expect
projected add-on geometry:
the original single cube, double slab, and oriented slope, plus an adjacent
cube pair and an adjacent dual-camouflage double panel. Eleven cases retain
the bounded stock fallback. This generated fixture checks routing and saved
state, not client-exact hidden-face parity.

## Implemented M0 path

This section records the earlier narrow checkpoint; it is not evidence for the
expanded lane above.

| Dimension | Current coverage |
| --- | --- |
| Block | `framedblocks:framed_cube` only |
| Block entity | `framedblocks:framed_tile` |
| Camouflage | named disk codec, built-in block factory, plain `minecraft:stone` only |
| Secondary camouflage | Not implemented |
| Modifiers | Retained but not rendered |
| Geometry | BlueMap stock full-cube stone model only |
| Failure | Direct original FramedBlocks resource fallback |

The tracked manifests inventory all 236 blockstate resources (including the two
saws outside the rendered inventory) and the 51 ordinary framed block-entity
IDs. The historical M0 result is not an assertion that all their renderers are
implemented.

## Observed exact M0 runtime fixture

The isolated full-pack lab saved and flushed a real Anvil fixture containing
the exact supported `framed_cube` state and a built-in block camouflage naming
`minecraft:stone`. Server readback passed before the enabled render, after a
full restart with the add-on absent, and after re-enabling.

| Subject | Add-on enabled | Add-on absent |
| --- | --- | --- |
| Framed cube material | `minecraft:block/stone`, index `13895` | `framedblocks:block/framed_block`, index `29096` |
| Framed cube flat sample | `#7D7D7DFF` | `#745732AF` |
| Vanilla stone control | `minecraft:block/stone`, index `13895`, `#7D7D7DFF` | `minecraft:block/stone`, index `13895`, `#7D7D7DFF` |
| Mesh | 36 non-indexed vertices, 12 triangles, 1-by-1-by-1 cube | same envelope and topology |

Translation-normalized positions matched. The implemented path therefore
corrects camouflage material delegation; it does not replace missing cube
geometry. After the stock run, reinstalling the exact JAR reactivated the
profile and reproduced the original enabled flat tile, framed-cube PRBM, and
stone-control PRBM byte-for-byte.

## Pending gates

- non-default-state and comprehensive block-entity/model-data/BER matrices;
- secondary-camouflage and modifier matrices beyond the focused cases;
- client-exact, shape-aware framed-neighbor hidden-face behavior;
- pixel-repeatable modded-client reference;
- malformed/incompatible-profile, in-process resource-reload, and
  concurrent-render matrices;
- numeric dense/control performance budgets.

The expanded gallery materially supersedes the M0 placement scope, but still
tests only 234 generated default states rather than every projected state or
render-relevant block-entity payload.

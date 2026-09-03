# FramedBlocks 10.6.1 staging gallery

This directory contains a deterministic, isolated-staging datapack for one
default-state visual case for every FramedBlocks `10.6.1` blockstate resource
except the two framing saws. It is development evidence for the BlueMap add-on,
not gameplay content and not a production-world migration tool.

The generator reads and hash-checks the add-on's tracked 236-ID exact-artifact
manifest, excludes `framedblocks:framing_saw` and
`framedblocks:powered_framing_saw`, and assigns all remaining 234 IDs exactly
once. Every anchor receives `minecraft:stone` primary camouflage. The exact 84
`BlockType` entries whose `doubleBlock` field is true also receive
`minecraft:gold_block` secondary camouflage.

The generator also pins the exact 153 `BlockType` entries which can occlude
with a solid camouflage and places those states with `solid=true`. This is
needed because the bounded fixture uses disk-NBT merging rather than the
normal player interaction path that would otherwise reconcile that dynamic
state. The canonical build also invokes a disjoint 16-case renderer-path matrix,
and the datapack provides two guarded observation decks/poses. Each lane keeps
its own bounded functions for focused use.

## Audited layout

- The 234 anchors form an 18-by-13 grid at `y=100`, with three-block spacing:
  `x=196..247`, `z=196..232`.
- Only `x=195..248`, `y=98..102`, `z=195..233` is cleared or written.
- The renderer-path matrix has 16 logical cases and 18 framed blocks in a
  6-by-3 grid at `y=100`. Its anchors are `x=198..218`, `z=240..248`; only
  `x=196..220`, `y=98..102`, `z=238..250` is cleared or written by its
  functions.
- The elevated south observation deck clears only `x=214..228`, `y=106..110`,
  `z=251..257`. Its guarded pose is `221.5 108 254.5`, yaw `180`, pitch `14`.
- The elevated east observation deck clears only `x=265..273`, `y=106..110`,
  `z=207..221`. Its guarded pose is `269.5 108 214.5`, yaw `90`, pitch `14`.
- The full-height corridor `x=252..261`, `z=192..320` is protected. It contains
  the existing M0 fixture at `x=256..258`, `y=99..101`, `z=256`.
- The roster, matrix, and both deck clear AABBs are mutually disjoint, strictly
  inside the configured `192..320` map mask, and disjoint from the full-height
  M0 corridor. Each function touches only its documented lane.

Every anchor has a stone support below it. Wall-mounted torches, signs, item
frames, and the ladder receive an appropriate backing; the wall hanging sign
gets a side arm; the ceiling hanging sign gets an overhead support; and both
door IDs receive an upper half with matching primary camouflage.

## Renderer-path matrix

The compact matrix, included by `framedblocks_gallery:build`, has eight expected
add-on geometry cases: a single cube, a
double slab with primary and secondary camouflage, an explicitly oriented
slope, an adjacent cube pair, and an adjacent double panel with primary and
secondary camouflage, plus waterlogged, glowing, and skylight-propagating
states. Its eight expected stock-fallback cases cover adjustable model data,
collapsible offsets, a potted flower, one-way-window direction, a sign BER,
special camouflage overlay, reinforcement, and permitted-but-non-opaque glass
camouflage. The manifest records the exact expected reason for every path.

The six model-data/BER family cases remain on their original resource in the
resource extension. Reinforced and glass cases enter the add-on renderer and
then take its runtime fallback. Waterlogged, glowing, skylight-propagating, and
both adjacent-neighbor cases remain on the add-on geometry path. BlueMap adds
the standard water overlay after custom geometry, while the FramedBlocks
renderer applies persisted glowing light. The manifest and TSV record routing
separately from the final expected path.

Missing and malformed camouflage are deliberately not built. FramedBlocks
normalizes either form while loading the block entity and writes canonical
camouflage again on save, so a datapack cannot create a durable Anvil fixture
for those cases. The manifest records both omissions and their reason; raw
chunk mutation would require a separate stopped-server test authorization.

## Generate and inspect

From this repository:

```bash
python gallery/generate.py
python gallery/generate.py --check
cd gallery
sha256sum -c SHA256SUMS
```

`cases.json` is the complete machine-readable roster contract and `cases.tsv`
is its compact review view. `renderer-path-matrix.json` is the separate matrix,
deck, pose, bounds, function, count, and omission contract;
`renderer-path-matrix.tsv` and `observation-poses.tsv` are its compact review
views. `generic-double-ids.txt` is the exact 84-ID secondary-camouflage
classification. `SHA256SUMS` covers every generated file other than itself.

Install the `datapack/` directory only in the disposable FramedBlocks staging
world. After the server recognizes the pack, an operator can run:

```text
function framedblocks_gallery:build
function framedblocks_gallery:verify
function framedblocks_gallery:clear
```

`build` starts with the bounded clear function, so repeated builds converge on
the same gallery. `verify` checks anchors, primary and secondary camouflage
NBT, supports, and door companions. It does not verify every block state,
dynamic block-entity content, animations, neighbor-driven variants, client
pixels, or BlueMap pixels. Run `save-all flush` separately from the server
console before restart/readback evidence; it is deliberately not embedded in
the datapack because datapack function permission levels vary.

The renderer-path matrix is separately idempotent and clearable:

```text
function framedblocks_gallery:build_renderer_paths
function framedblocks_gallery:verify_renderer_paths
function framedblocks_gallery:clear_renderer_paths
```

The observation decks are also separate:

```text
function framedblocks_gallery:build_observation_decks
function framedblocks_gallery:verify_observation_decks
function framedblocks_gallery:clear_observation_decks
function framedblocks_gallery:pose_south
function framedblocks_gallery:pose_east
```

Each pose function checks its exact stone-brick floor plus empty foot and head
space before teleporting only its executor (`@s`). If any check fails, it does
not teleport. Structural verification proves blocks, states, selected NBT,
supports, and pose volumes; it does not prove which renderer ran or validate
client/BlueMap pixels. Preserve server and BlueMap evidence separately.

No deployment or server mutation is performed by the generator.

# SPDX-License-Identifier: LGPL-3.0-only
# Exact 10.6.1 renderer-path matrix: 16 cases and 18 framed anchors.
# Rebuilding is idempotent because its disjoint AABB is cleared first.
function framedblocks_gallery:clear_renderer_paths

# fb1061-rp-01 proven_static_single
setblock 198 99 240 minecraft:stone replace
setblock 198 100 240 framedblocks:framed_cube[alt=false,glowing=false,propagates_skylight=false,reinforced=false,solid=true,solid_bg=false] replace
data merge block 198 100 240 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b}

# fb1061-rp-02 proven_static_double
setblock 202 99 240 minecraft:stone replace
setblock 202 100 240 framedblocks:framed_double_slab[glowing=false,propagates_skylight=false,solid=true] replace
data merge block 202 100 240 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b}

# fb1061-rp-03 proven_static_oriented
setblock 206 99 240 minecraft:stone replace
setblock 206 100 240 framedblocks:framed_slope[facing=east,glowing=false,propagates_skylight=false,solid=true,type=bottom,waterlogged=false,yslope=false] replace
data merge block 206 100 240 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b}

# fb1061-rp-04 fallback_adjustable
setblock 210 99 240 minecraft:stone replace
setblock 210 100 240 framedblocks:framed_adj_double_slab[glowing=false,propagates_skylight=false,solid=true] replace
data merge block 210 100 240 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}},first_height:5,glowing:0b,intangible:0b,reinforced:0b,updated:3b}

# fb1061-rp-05 fallback_collapsible
setblock 214 99 240 minecraft:stone replace
setblock 214 100 240 framedblocks:framed_collapsible_block[face=up,glowing=false,propagates_skylight=false,rot_split_line=false,waterlogged=false] replace
data merge block 214 100 240 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},offsets:270600,glowing:0b,intangible:0b,reinforced:0b,updated:3b}

# fb1061-rp-06 fallback_flower
setblock 218 99 240 minecraft:stone replace
setblock 218 100 240 framedblocks:framed_flower_pot[glowing=false,hanging=false,propagates_skylight=false] replace
data merge block 218 100 240 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},flower:"minecraft:dandelion",glowing:0b,intangible:0b,reinforced:0b,updated:3b}

# fb1061-rp-07 fallback_one_way
setblock 198 99 244 minecraft:stone replace
setblock 198 100 244 framedblocks:framed_one_way_window[face=east,glowing=false,propagates_skylight=false] replace
data merge block 198 100 244 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b}

# fb1061-rp-08 fallback_block_entity_renderer
setblock 202 99 244 minecraft:stone replace
setblock 202 100 244 framedblocks:framed_sign[glowing=false,propagates_skylight=false,rotation=0,waterlogged=false] replace
data merge block 202 100 244 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b}

# fb1061-rp-09 fallback_special_camo_overlay
setblock 206 99 244 minecraft:stone replace
setblock 206 100 244 framedblocks:framed_bouncy_cube[glowing=false,propagates_skylight=false,solid=true] replace
data merge block 206 100 244 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b}

# fb1061-rp-10 fallback_waterlogged
setblock 210 99 244 minecraft:stone replace
setblock 210 100 244 framedblocks:framed_slab[glowing=false,propagates_skylight=false,solid=true,top=false,waterlogged=true] replace
data merge block 210 100 244 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b}

# fb1061-rp-11 proven_adjacent_framed_neighbor
setblock 214 99 244 minecraft:stone replace
setblock 214 100 244 framedblocks:framed_cube[alt=false,glowing=false,propagates_skylight=false,reinforced=false,solid=true,solid_bg=false] replace
data merge block 214 100 244 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b}
setblock 215 99 244 minecraft:stone replace
setblock 215 100 244 framedblocks:framed_cube[alt=false,glowing=false,propagates_skylight=false,reinforced=false,solid=true,solid_bg=false] replace
data merge block 215 100 244 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b}

# fb1061-rp-12 fallback_non_opaque_camo
setblock 218 99 244 minecraft:stone replace
setblock 218 100 244 framedblocks:framed_cube[alt=false,glowing=false,propagates_skylight=false,reinforced=false,solid=false,solid_bg=false] replace
data merge block 218 100 244 {camo:{type:"framedblocks:block",state:{Name:"minecraft:glass"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b}

# fb1061-rp-13 fallback_glowing
setblock 198 99 248 minecraft:stone replace
setblock 198 100 248 framedblocks:framed_slab[glowing=true,propagates_skylight=false,solid=true,top=false,waterlogged=false] replace
data merge block 198 100 248 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:1b,intangible:0b,reinforced:0b,updated:3b}

# fb1061-rp-14 fallback_propagates_skylight
setblock 202 99 248 minecraft:stone replace
setblock 202 100 248 framedblocks:framed_slab[glowing=false,propagates_skylight=true,solid=false,top=false,waterlogged=false] replace
data merge block 202 100 248 {camo:{type:"framedblocks:block",state:{Name:"minecraft:glass"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b}

# fb1061-rp-15 fallback_reinforced
setblock 206 99 248 minecraft:stone replace
setblock 206 100 248 framedblocks:framed_cube[alt=false,glowing=false,propagates_skylight=false,reinforced=false,solid=true,solid_bg=false] replace
data merge block 206 100 248 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:1b,updated:3b}

# fb1061-rp-16 proven_adjacent_framed_double_panel
setblock 210 99 248 minecraft:stone replace
setblock 210 100 248 framedblocks:framed_double_panel[solid=true] replace
data merge block 210 100 248 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b}
setblock 211 99 248 minecraft:stone replace
setblock 211 100 248 framedblocks:framed_cube[alt=false,glowing=false,propagates_skylight=false,reinforced=false,solid=true,solid_bg=false] replace
data merge block 211 100 248 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b}

tellraw @a [{"text":"Built the FramedBlocks renderer-path matrix (16 cases, 18 framed anchors).","color":"aqua"}]
function framedblocks_gallery:verify_renderer_paths

# SPDX-License-Identifier: LGPL-3.0-only
# Structural path-fixture verification; this does not validate rendered pixels.
scoreboard objectives add fbgr1061 dummy
scoreboard players set #failures fbgr1061 0

# fb1061-rp-01 proven_static_single
execute unless block 198 100 240 framedblocks:framed_cube[alt=false,glowing=false,propagates_skylight=false,reinforced=false,solid=true,solid_bg=false] run scoreboard players add #failures fbgr1061 1
execute unless data block 198 100 240 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b} run scoreboard players add #failures fbgr1061 1
execute unless block 198 99 240 minecraft:stone run scoreboard players add #failures fbgr1061 1

# fb1061-rp-02 proven_static_double
execute unless block 202 100 240 framedblocks:framed_double_slab[glowing=false,propagates_skylight=false,solid=true] run scoreboard players add #failures fbgr1061 1
execute unless data block 202 100 240 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b} run scoreboard players add #failures fbgr1061 1
execute unless block 202 99 240 minecraft:stone run scoreboard players add #failures fbgr1061 1

# fb1061-rp-03 proven_static_oriented
execute unless block 206 100 240 framedblocks:framed_slope[facing=east,glowing=false,propagates_skylight=false,solid=true,type=bottom,waterlogged=false,yslope=false] run scoreboard players add #failures fbgr1061 1
execute unless data block 206 100 240 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b} run scoreboard players add #failures fbgr1061 1
execute unless block 206 99 240 minecraft:stone run scoreboard players add #failures fbgr1061 1

# fb1061-rp-04 fallback_adjustable
execute unless block 210 100 240 framedblocks:framed_adj_double_slab[glowing=false,propagates_skylight=false,solid=true] run scoreboard players add #failures fbgr1061 1
execute unless data block 210 100 240 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}},first_height:5,glowing:0b,intangible:0b,reinforced:0b,updated:3b} run scoreboard players add #failures fbgr1061 1
execute unless block 210 99 240 minecraft:stone run scoreboard players add #failures fbgr1061 1

# fb1061-rp-05 fallback_collapsible
execute unless block 214 100 240 framedblocks:framed_collapsible_block[face=up,glowing=false,propagates_skylight=false,rot_split_line=false,waterlogged=false] run scoreboard players add #failures fbgr1061 1
execute unless data block 214 100 240 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},offsets:270600,glowing:0b,intangible:0b,reinforced:0b,updated:3b} run scoreboard players add #failures fbgr1061 1
execute unless block 214 99 240 minecraft:stone run scoreboard players add #failures fbgr1061 1

# fb1061-rp-06 fallback_flower
execute unless block 218 100 240 framedblocks:framed_flower_pot[glowing=false,hanging=false,propagates_skylight=false] run scoreboard players add #failures fbgr1061 1
execute unless data block 218 100 240 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},flower:"minecraft:dandelion",glowing:0b,intangible:0b,reinforced:0b,updated:3b} run scoreboard players add #failures fbgr1061 1
execute unless block 218 99 240 minecraft:stone run scoreboard players add #failures fbgr1061 1

# fb1061-rp-07 fallback_one_way
execute unless block 198 100 244 framedblocks:framed_one_way_window[face=east,glowing=false,propagates_skylight=false] run scoreboard players add #failures fbgr1061 1
execute unless data block 198 100 244 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b} run scoreboard players add #failures fbgr1061 1
execute unless block 198 99 244 minecraft:stone run scoreboard players add #failures fbgr1061 1

# fb1061-rp-08 fallback_block_entity_renderer
execute unless block 202 100 244 framedblocks:framed_sign[glowing=false,propagates_skylight=false,rotation=0,waterlogged=false] run scoreboard players add #failures fbgr1061 1
execute unless data block 202 100 244 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b} run scoreboard players add #failures fbgr1061 1
execute unless block 202 99 244 minecraft:stone run scoreboard players add #failures fbgr1061 1

# fb1061-rp-09 fallback_special_camo_overlay
execute unless block 206 100 244 framedblocks:framed_bouncy_cube[glowing=false,propagates_skylight=false,solid=true] run scoreboard players add #failures fbgr1061 1
execute unless data block 206 100 244 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b} run scoreboard players add #failures fbgr1061 1
execute unless block 206 99 244 minecraft:stone run scoreboard players add #failures fbgr1061 1

# fb1061-rp-10 proven_waterlogged
execute unless block 210 100 244 framedblocks:framed_slab[glowing=false,propagates_skylight=false,solid=true,top=false,waterlogged=true] run scoreboard players add #failures fbgr1061 1
execute unless data block 210 100 244 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b} run scoreboard players add #failures fbgr1061 1
execute unless block 210 99 244 minecraft:stone run scoreboard players add #failures fbgr1061 1

# fb1061-rp-11 proven_adjacent_framed_neighbor
execute unless block 214 100 244 framedblocks:framed_cube[alt=false,glowing=false,propagates_skylight=false,reinforced=false,solid=true,solid_bg=false] run scoreboard players add #failures fbgr1061 1
execute unless data block 214 100 244 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b} run scoreboard players add #failures fbgr1061 1
execute unless block 214 99 244 minecraft:stone run scoreboard players add #failures fbgr1061 1
execute unless block 215 100 244 framedblocks:framed_cube[alt=false,glowing=false,propagates_skylight=false,reinforced=false,solid=true,solid_bg=false] run scoreboard players add #failures fbgr1061 1
execute unless data block 215 100 244 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b} run scoreboard players add #failures fbgr1061 1
execute unless block 215 99 244 minecraft:stone run scoreboard players add #failures fbgr1061 1

# fb1061-rp-12 fallback_non_opaque_camo
execute unless block 218 100 244 framedblocks:framed_cube[alt=false,glowing=false,propagates_skylight=false,reinforced=false,solid=false,solid_bg=false] run scoreboard players add #failures fbgr1061 1
execute unless data block 218 100 244 {camo:{type:"framedblocks:block",state:{Name:"minecraft:glass"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b} run scoreboard players add #failures fbgr1061 1
execute unless block 218 99 244 minecraft:stone run scoreboard players add #failures fbgr1061 1

# fb1061-rp-13 proven_glowing
execute unless block 198 100 248 framedblocks:framed_slab[glowing=true,propagates_skylight=false,solid=true,top=false,waterlogged=false] run scoreboard players add #failures fbgr1061 1
execute unless data block 198 100 248 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:1b,intangible:0b,reinforced:0b,updated:3b} run scoreboard players add #failures fbgr1061 1
execute unless block 198 99 248 minecraft:stone run scoreboard players add #failures fbgr1061 1

# fb1061-rp-14 proven_propagates_skylight
execute unless block 202 100 248 framedblocks:framed_slab[glowing=false,propagates_skylight=true,solid=false,top=false,waterlogged=false] run scoreboard players add #failures fbgr1061 1
execute unless data block 202 100 248 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b} run scoreboard players add #failures fbgr1061 1
execute unless block 202 99 248 minecraft:stone run scoreboard players add #failures fbgr1061 1

# fb1061-rp-15 fallback_reinforced
execute unless block 206 100 248 framedblocks:framed_cube[alt=false,glowing=false,propagates_skylight=false,reinforced=false,solid=true,solid_bg=false] run scoreboard players add #failures fbgr1061 1
execute unless data block 206 100 248 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:1b,updated:3b} run scoreboard players add #failures fbgr1061 1
execute unless block 206 99 248 minecraft:stone run scoreboard players add #failures fbgr1061 1

# fb1061-rp-16 proven_adjacent_framed_double_panel
execute unless block 210 100 248 framedblocks:framed_double_panel[solid=true] run scoreboard players add #failures fbgr1061 1
execute unless data block 210 100 248 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b} run scoreboard players add #failures fbgr1061 1
execute unless block 210 99 248 minecraft:stone run scoreboard players add #failures fbgr1061 1
execute unless block 211 100 248 framedblocks:framed_cube[alt=false,glowing=false,propagates_skylight=false,reinforced=false,solid=true,solid_bg=false] run scoreboard players add #failures fbgr1061 1
execute unless data block 211 100 248 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}},glowing:0b,intangible:0b,reinforced:0b,updated:3b} run scoreboard players add #failures fbgr1061 1
execute unless block 211 99 248 minecraft:stone run scoreboard players add #failures fbgr1061 1

execute if score #failures fbgr1061 matches 0 run tellraw @a [{"text":"Renderer-path matrix verification passed: 16/16 cases and 18 framed anchors.","color":"green"}]
execute unless score #failures fbgr1061 matches 0 run tellraw @a [{"text":"Renderer-path matrix verification failed with ","color":"red"},{"score":{"name":"#failures","objective":"fbgr1061"}},{"text":" mismatches.","color":"red"}]

# SPDX-License-Identifier: LGPL-3.0-only
# Structural verification only; this does not validate client or BlueMap pixels.
scoreboard objectives add fbgv1061 dummy
scoreboard players set #failures fbgv1061 0

# fb1061-001 framedblocks:framed_activator_rail_slope
execute unless block 196 100 196 framedblocks:framed_activator_rail_slope[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 196 100 196 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 196 99 196 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-002 framedblocks:framed_adj_double_copycat_panel
execute unless block 199 100 196 framedblocks:framed_adj_double_copycat_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 199 100 196 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 199 100 196 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 199 99 196 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-003 framedblocks:framed_adj_double_copycat_slab
execute unless block 202 100 196 framedblocks:framed_adj_double_copycat_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 202 100 196 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 202 100 196 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 202 99 196 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-004 framedblocks:framed_adj_double_panel
execute unless block 205 100 196 framedblocks:framed_adj_double_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 205 100 196 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 205 100 196 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 205 99 196 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-005 framedblocks:framed_adj_double_slab
execute unless block 208 100 196 framedblocks:framed_adj_double_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 208 100 196 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 208 100 196 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 208 99 196 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-006 framedblocks:framed_bars
execute unless block 211 100 196 framedblocks:framed_bars run scoreboard players add #failures fbgv1061 1
execute unless data block 211 100 196 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 211 99 196 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-007 framedblocks:framed_bookshelf
execute unless block 214 100 196 framedblocks:framed_bookshelf[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 214 100 196 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 214 99 196 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-008 framedblocks:framed_bouncy_cube
execute unless block 217 100 196 framedblocks:framed_bouncy_cube[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 217 100 196 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 217 99 196 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-009 framedblocks:framed_button
execute unless block 220 100 196 framedblocks:framed_button[face=floor,facing=south,powered=false] run scoreboard players add #failures fbgv1061 1
execute unless data block 220 100 196 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 220 99 196 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-010 framedblocks:framed_centered_panel
execute unless block 223 100 196 framedblocks:framed_centered_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 223 100 196 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 223 99 196 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-011 framedblocks:framed_centered_slab
execute unless block 226 100 196 framedblocks:framed_centered_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 226 100 196 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 226 99 196 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-012 framedblocks:framed_chain
execute unless block 229 100 196 framedblocks:framed_chain run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 196 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 229 99 196 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-013 framedblocks:framed_checkered_cube
execute unless block 232 100 196 framedblocks:framed_checkered_cube[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 232 100 196 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 232 100 196 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 232 99 196 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-014 framedblocks:framed_checkered_cube_segment
execute unless block 235 100 196 framedblocks:framed_checkered_cube_segment run scoreboard players add #failures fbgv1061 1
execute unless data block 235 100 196 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 235 99 196 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-015 framedblocks:framed_checkered_panel
execute unless block 238 100 196 framedblocks:framed_checkered_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 196 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 196 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 238 99 196 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-016 framedblocks:framed_checkered_panel_segment
execute unless block 241 100 196 framedblocks:framed_checkered_panel_segment run scoreboard players add #failures fbgv1061 1
execute unless data block 241 100 196 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 241 99 196 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-017 framedblocks:framed_checkered_slab
execute unless block 244 100 196 framedblocks:framed_checkered_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 196 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 196 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 244 99 196 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-018 framedblocks:framed_checkered_slab_segment
execute unless block 247 100 196 framedblocks:framed_checkered_slab_segment run scoreboard players add #failures fbgv1061 1
execute unless data block 247 100 196 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 247 99 196 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-019 framedblocks:framed_chest
execute unless block 196 100 199 framedblocks:framed_chest run scoreboard players add #failures fbgv1061 1
execute unless data block 196 100 199 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 196 99 199 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-020 framedblocks:framed_chiseled_bookshelf
execute unless block 199 100 199 framedblocks:framed_chiseled_bookshelf[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 199 100 199 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 199 99 199 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-021 framedblocks:framed_collapsible_block
execute unless block 202 100 199 framedblocks:framed_collapsible_block run scoreboard players add #failures fbgv1061 1
execute unless data block 202 100 199 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 202 99 199 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-022 framedblocks:framed_collapsible_copycat_block
execute unless block 205 100 199 framedblocks:framed_collapsible_copycat_block run scoreboard players add #failures fbgv1061 1
execute unless data block 205 100 199 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 205 99 199 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-023 framedblocks:framed_compound_slope_panel
execute unless block 208 100 199 framedblocks:framed_compound_slope_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 208 100 199 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 208 99 199 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-024 framedblocks:framed_compound_slope_slab
execute unless block 211 100 199 framedblocks:framed_compound_slope_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 211 100 199 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 211 99 199 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-025 framedblocks:framed_corner_pillar
execute unless block 214 100 199 framedblocks:framed_corner_pillar run scoreboard players add #failures fbgv1061 1
execute unless data block 214 100 199 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 214 99 199 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-026 framedblocks:framed_corner_slope
execute unless block 217 100 199 framedblocks:framed_corner_slope[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 217 100 199 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 217 99 199 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-027 framedblocks:framed_corner_slope_edge
execute unless block 220 100 199 framedblocks:framed_corner_slope_edge run scoreboard players add #failures fbgv1061 1
execute unless data block 220 100 199 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 220 99 199 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-028 framedblocks:framed_corner_strip
execute unless block 223 100 199 framedblocks:framed_corner_strip run scoreboard players add #failures fbgv1061 1
execute unless data block 223 100 199 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 223 99 199 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-029 framedblocks:framed_corner_tube
execute unless block 226 100 199 framedblocks:framed_corner_tube[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 226 100 199 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 226 99 199 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-030 framedblocks:framed_cube
execute unless block 229 100 199 framedblocks:framed_cube[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 199 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 229 99 199 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-031 framedblocks:framed_detector_rail_slope
execute unless block 232 100 199 framedblocks:framed_detector_rail_slope[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 232 100 199 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 232 99 199 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-032 framedblocks:framed_divided_panel_horizontal
execute unless block 235 100 199 framedblocks:framed_divided_panel_horizontal[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 235 100 199 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 235 100 199 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 235 99 199 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-033 framedblocks:framed_divided_panel_vertical
execute unless block 238 100 199 framedblocks:framed_divided_panel_vertical[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 199 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 199 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 238 99 199 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-034 framedblocks:framed_divided_slab
execute unless block 241 100 199 framedblocks:framed_divided_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 241 100 199 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 241 100 199 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 241 99 199 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-035 framedblocks:framed_divided_slope
execute unless block 244 100 199 framedblocks:framed_divided_slope[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 199 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 199 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 244 99 199 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-036 framedblocks:framed_divided_stairs
execute unless block 247 100 199 framedblocks:framed_divided_stairs[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 247 100 199 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 247 100 199 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 247 99 199 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-037 framedblocks:framed_door
execute unless block 196 100 202 framedblocks:framed_door[facing=south,half=lower,hinge=left,open=false,powered=false,solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 196 100 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 196 99 202 minecraft:stone run scoreboard players add #failures fbgv1061 1
execute unless block 196 101 202 framedblocks:framed_door[facing=south,half=upper,hinge=left,open=false,powered=false,solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 196 101 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1

# fb1061-038 framedblocks:framed_double_corner
execute unless block 199 100 202 framedblocks:framed_double_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 199 100 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 199 100 202 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 199 99 202 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-039 framedblocks:framed_double_half_slope
execute unless block 202 100 202 framedblocks:framed_double_half_slope[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 202 100 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 202 100 202 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 202 99 202 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-040 framedblocks:framed_double_half_stairs
execute unless block 205 100 202 framedblocks:framed_double_half_stairs[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 205 100 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 205 100 202 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 205 99 202 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-041 framedblocks:framed_double_panel
execute unless block 208 100 202 framedblocks:framed_double_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 208 100 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 208 100 202 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 208 99 202 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-042 framedblocks:framed_double_prism_corner
execute unless block 211 100 202 framedblocks:framed_double_prism_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 211 100 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 211 100 202 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 211 99 202 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-043 framedblocks:framed_double_slab
execute unless block 214 100 202 framedblocks:framed_double_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 214 100 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 214 100 202 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 214 99 202 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-044 framedblocks:framed_double_slope
execute unless block 217 100 202 framedblocks:framed_double_slope[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 217 100 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 217 100 202 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 217 99 202 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-045 framedblocks:framed_double_slope_panel
execute unless block 220 100 202 framedblocks:framed_double_slope_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 220 100 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 220 100 202 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 220 99 202 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-046 framedblocks:framed_double_slope_slab
execute unless block 223 100 202 framedblocks:framed_double_slope_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 223 100 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 223 100 202 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 223 99 202 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-047 framedblocks:framed_double_stairs
execute unless block 226 100 202 framedblocks:framed_double_stairs[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 226 100 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 226 100 202 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 226 99 202 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-048 framedblocks:framed_double_threeway_corner
execute unless block 229 100 202 framedblocks:framed_double_threeway_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 202 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 229 99 202 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-049 framedblocks:framed_double_threeway_corner_pillar
execute unless block 232 100 202 framedblocks:framed_double_threeway_corner_pillar[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 232 100 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 232 100 202 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 232 99 202 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-050 framedblocks:framed_elev_double_corner_slope_edge
execute unless block 235 100 202 framedblocks:framed_elev_double_corner_slope_edge[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 235 100 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 235 100 202 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 235 99 202 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-051 framedblocks:framed_elev_double_inner_corner_slope_edge
execute unless block 238 100 202 framedblocks:framed_elev_double_inner_corner_slope_edge[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 202 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 238 99 202 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-052 framedblocks:framed_elevated_corner_slope_edge
execute unless block 241 100 202 framedblocks:framed_elevated_corner_slope_edge[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 241 100 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 241 99 202 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-053 framedblocks:framed_elevated_double_slope_edge
execute unless block 244 100 202 framedblocks:framed_elevated_double_slope_edge[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 202 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 244 99 202 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-054 framedblocks:framed_elevated_double_slope_slab
execute unless block 247 100 202 framedblocks:framed_elevated_double_slope_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 247 100 202 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 247 100 202 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 247 99 202 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-055 framedblocks:framed_elevated_inner_corner_slope_edge
execute unless block 196 100 205 framedblocks:framed_elevated_inner_corner_slope_edge[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 196 100 205 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 196 99 205 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-056 framedblocks:framed_elevated_inner_double_prism
execute unless block 199 100 205 framedblocks:framed_elevated_inner_double_prism[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 199 100 205 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 199 100 205 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 199 99 205 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-057 framedblocks:framed_elevated_inner_double_sloped_prism
execute unless block 202 100 205 framedblocks:framed_elevated_inner_double_sloped_prism[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 202 100 205 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 202 100 205 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 202 99 205 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-058 framedblocks:framed_elevated_inner_prism
execute unless block 205 100 205 framedblocks:framed_elevated_inner_prism[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 205 100 205 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 205 99 205 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-059 framedblocks:framed_elevated_inner_sloped_prism
execute unless block 208 100 205 framedblocks:framed_elevated_inner_sloped_prism[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 208 100 205 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 208 99 205 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-060 framedblocks:framed_elevated_pyramid_slab
execute unless block 211 100 205 framedblocks:framed_elevated_pyramid_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 211 100 205 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 211 99 205 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-061 framedblocks:framed_elevated_slope_edge
execute unless block 214 100 205 framedblocks:framed_elevated_slope_edge[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 214 100 205 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 214 99 205 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-062 framedblocks:framed_elevated_slope_slab
execute unless block 217 100 205 framedblocks:framed_elevated_slope_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 217 100 205 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 217 99 205 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-063 framedblocks:framed_ext_corner_slope_panel
execute unless block 220 100 205 framedblocks:framed_ext_corner_slope_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 220 100 205 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 220 99 205 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-064 framedblocks:framed_ext_corner_slope_panel_w
execute unless block 223 100 205 framedblocks:framed_ext_corner_slope_panel_w[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 223 100 205 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 223 99 205 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-065 framedblocks:framed_ext_double_corner_slope_panel
execute unless block 226 100 205 framedblocks:framed_ext_double_corner_slope_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 226 100 205 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 226 100 205 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 226 99 205 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-066 framedblocks:framed_ext_double_corner_slope_panel_w
execute unless block 229 100 205 framedblocks:framed_ext_double_corner_slope_panel_w[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 205 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 205 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 229 99 205 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-067 framedblocks:framed_ext_inner_corner_slope_panel
execute unless block 232 100 205 framedblocks:framed_ext_inner_corner_slope_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 232 100 205 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 232 99 205 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-068 framedblocks:framed_ext_inner_corner_slope_panel_w
execute unless block 235 100 205 framedblocks:framed_ext_inner_corner_slope_panel_w[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 235 100 205 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 235 99 205 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-069 framedblocks:framed_ext_inner_double_corner_slope_panel
execute unless block 238 100 205 framedblocks:framed_ext_inner_double_corner_slope_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 205 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 205 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 238 99 205 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-070 framedblocks:framed_ext_inner_double_corner_slope_panel_w
execute unless block 241 100 205 framedblocks:framed_ext_inner_double_corner_slope_panel_w[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 241 100 205 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 241 100 205 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 241 99 205 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-071 framedblocks:framed_extended_double_slope_panel
execute unless block 244 100 205 framedblocks:framed_extended_double_slope_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 205 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 205 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 244 99 205 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-072 framedblocks:framed_extended_slope_panel
execute unless block 247 100 205 framedblocks:framed_extended_slope_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 247 100 205 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 247 99 205 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-073 framedblocks:framed_fancy_activator_rail
execute unless block 196 100 208 framedblocks:framed_fancy_activator_rail run scoreboard players add #failures fbgv1061 1
execute unless data block 196 100 208 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 196 99 208 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-074 framedblocks:framed_fancy_activator_rail_slope
execute unless block 199 100 208 framedblocks:framed_fancy_activator_rail_slope[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 199 100 208 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 199 100 208 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 199 99 208 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-075 framedblocks:framed_fancy_detector_rail
execute unless block 202 100 208 framedblocks:framed_fancy_detector_rail run scoreboard players add #failures fbgv1061 1
execute unless data block 202 100 208 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 202 99 208 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-076 framedblocks:framed_fancy_detector_rail_slope
execute unless block 205 100 208 framedblocks:framed_fancy_detector_rail_slope[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 205 100 208 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 205 100 208 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 205 99 208 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-077 framedblocks:framed_fancy_powered_rail
execute unless block 208 100 208 framedblocks:framed_fancy_powered_rail run scoreboard players add #failures fbgv1061 1
execute unless data block 208 100 208 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 208 99 208 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-078 framedblocks:framed_fancy_powered_rail_slope
execute unless block 211 100 208 framedblocks:framed_fancy_powered_rail_slope[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 211 100 208 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 211 100 208 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 211 99 208 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-079 framedblocks:framed_fancy_rail
execute unless block 214 100 208 framedblocks:framed_fancy_rail run scoreboard players add #failures fbgv1061 1
execute unless data block 214 100 208 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 214 99 208 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-080 framedblocks:framed_fancy_rail_slope
execute unless block 217 100 208 framedblocks:framed_fancy_rail_slope[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 217 100 208 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 217 100 208 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 217 99 208 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-081 framedblocks:framed_fence
execute unless block 220 100 208 framedblocks:framed_fence run scoreboard players add #failures fbgv1061 1
execute unless data block 220 100 208 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 220 99 208 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-082 framedblocks:framed_fence_gate
execute unless block 223 100 208 framedblocks:framed_fence_gate run scoreboard players add #failures fbgv1061 1
execute unless data block 223 100 208 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 223 99 208 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-083 framedblocks:framed_flat_double_slope_panel_corner
execute unless block 226 100 208 framedblocks:framed_flat_double_slope_panel_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 226 100 208 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 226 100 208 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 226 99 208 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-084 framedblocks:framed_flat_double_slope_slab_corner
execute unless block 229 100 208 framedblocks:framed_flat_double_slope_slab_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 208 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 208 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 229 99 208 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-085 framedblocks:framed_flat_elev_double_slope_slab_corner
execute unless block 232 100 208 framedblocks:framed_flat_elev_double_slope_slab_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 232 100 208 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 232 100 208 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 232 99 208 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-086 framedblocks:framed_flat_elev_inner_double_slope_slab_corner
execute unless block 235 100 208 framedblocks:framed_flat_elev_inner_double_slope_slab_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 235 100 208 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 235 100 208 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 235 99 208 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-087 framedblocks:framed_flat_elev_inner_slope_slab_corner
execute unless block 238 100 208 framedblocks:framed_flat_elev_inner_slope_slab_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 208 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 238 99 208 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-088 framedblocks:framed_flat_elev_slope_slab_corner
execute unless block 241 100 208 framedblocks:framed_flat_elev_slope_slab_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 241 100 208 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 241 99 208 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-089 framedblocks:framed_flat_ext_double_slope_panel_corner
execute unless block 244 100 208 framedblocks:framed_flat_ext_double_slope_panel_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 208 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 208 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 244 99 208 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-090 framedblocks:framed_flat_ext_inner_double_slope_panel_corner
execute unless block 247 100 208 framedblocks:framed_flat_ext_inner_double_slope_panel_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 247 100 208 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 247 100 208 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 247 99 208 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-091 framedblocks:framed_flat_ext_inner_slope_panel_corner
execute unless block 196 100 211 framedblocks:framed_flat_ext_inner_slope_panel_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 196 100 211 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 196 99 211 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-092 framedblocks:framed_flat_ext_slope_panel_corner
execute unless block 199 100 211 framedblocks:framed_flat_ext_slope_panel_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 199 100 211 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 199 99 211 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-093 framedblocks:framed_flat_inner_slope_panel_corner
execute unless block 202 100 211 framedblocks:framed_flat_inner_slope_panel_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 202 100 211 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 202 99 211 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-094 framedblocks:framed_flat_inner_slope_slab_corner
execute unless block 205 100 211 framedblocks:framed_flat_inner_slope_slab_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 205 100 211 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 205 99 211 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-095 framedblocks:framed_flat_inv_double_slope_panel_corner
execute unless block 208 100 211 framedblocks:framed_flat_inv_double_slope_panel_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 208 100 211 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 208 100 211 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 208 99 211 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-096 framedblocks:framed_flat_inv_double_slope_slab_corner
execute unless block 211 100 211 framedblocks:framed_flat_inv_double_slope_slab_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 211 100 211 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 211 100 211 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 211 99 211 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-097 framedblocks:framed_flat_slope_panel_corner
execute unless block 214 100 211 framedblocks:framed_flat_slope_panel_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 214 100 211 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 214 99 211 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-098 framedblocks:framed_flat_slope_slab_corner
execute unless block 217 100 211 framedblocks:framed_flat_slope_slab_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 217 100 211 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 217 99 211 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-099 framedblocks:framed_flat_stacked_inner_slope_panel_corner
execute unless block 220 100 211 framedblocks:framed_flat_stacked_inner_slope_panel_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 220 100 211 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 220 100 211 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 220 99 211 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-100 framedblocks:framed_flat_stacked_inner_slope_slab_corner
execute unless block 223 100 211 framedblocks:framed_flat_stacked_inner_slope_slab_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 223 100 211 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 223 100 211 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 223 99 211 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-101 framedblocks:framed_flat_stacked_slope_panel_corner
execute unless block 226 100 211 framedblocks:framed_flat_stacked_slope_panel_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 226 100 211 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 226 100 211 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 226 99 211 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-102 framedblocks:framed_flat_stacked_slope_slab_corner
execute unless block 229 100 211 framedblocks:framed_flat_stacked_slope_slab_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 211 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 211 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 229 99 211 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-103 framedblocks:framed_floor_board
execute unless block 232 100 211 framedblocks:framed_floor_board[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 232 100 211 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 232 99 211 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-104 framedblocks:framed_flower_pot
execute unless block 235 100 211 framedblocks:framed_flower_pot run scoreboard players add #failures fbgv1061 1
execute unless data block 235 100 211 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 235 99 211 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-105 framedblocks:framed_gate
execute unless block 238 100 211 framedblocks:framed_gate[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 211 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 238 99 211 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-106 framedblocks:framed_glowing_cube
execute unless block 241 100 211 framedblocks:framed_glowing_cube[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 241 100 211 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 241 99 211 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-107 framedblocks:framed_glowing_item_frame
execute unless block 244 100 211 framedblocks:framed_glowing_item_frame[facing=north,map_frame=false] run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 211 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 244 99 211 minecraft:stone run scoreboard players add #failures fbgv1061 1
execute unless block 244 100 210 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-108 framedblocks:framed_gold_pressure_plate
execute unless block 247 100 211 framedblocks:framed_gold_pressure_plate run scoreboard players add #failures fbgv1061 1
execute unless data block 247 100 211 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 247 99 211 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-109 framedblocks:framed_half_pillar
execute unless block 196 100 214 framedblocks:framed_half_pillar run scoreboard players add #failures fbgv1061 1
execute unless data block 196 100 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 196 99 214 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-110 framedblocks:framed_half_slope
execute unless block 199 100 214 framedblocks:framed_half_slope run scoreboard players add #failures fbgv1061 1
execute unless data block 199 100 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 199 99 214 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-111 framedblocks:framed_half_stairs
execute unless block 202 100 214 framedblocks:framed_half_stairs run scoreboard players add #failures fbgv1061 1
execute unless data block 202 100 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 202 99 214 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-112 framedblocks:framed_hanging_sign
execute unless block 205 100 214 framedblocks:framed_hanging_sign[attached=false,rotation=0] run scoreboard players add #failures fbgv1061 1
execute unless data block 205 100 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 205 99 214 minecraft:stone run scoreboard players add #failures fbgv1061 1
execute unless block 205 101 214 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-113 framedblocks:framed_hopper
execute unless block 208 100 214 framedblocks:framed_hopper run scoreboard players add #failures fbgv1061 1
execute unless data block 208 100 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 208 99 214 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-114 framedblocks:framed_horizontal_pane
execute unless block 211 100 214 framedblocks:framed_horizontal_pane[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 211 100 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 211 99 214 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-115 framedblocks:framed_inner_corner_slope
execute unless block 214 100 214 framedblocks:framed_inner_corner_slope[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 214 100 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 214 99 214 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-116 framedblocks:framed_inner_corner_slope_edge
execute unless block 217 100 214 framedblocks:framed_inner_corner_slope_edge run scoreboard players add #failures fbgv1061 1
execute unless data block 217 100 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 217 99 214 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-117 framedblocks:framed_inner_prism_corner
execute unless block 220 100 214 framedblocks:framed_inner_prism_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 220 100 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 220 99 214 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-118 framedblocks:framed_inner_threeway_corner
execute unless block 223 100 214 framedblocks:framed_inner_threeway_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 223 100 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 223 99 214 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-119 framedblocks:framed_inner_threeway_corner_slope_edge
execute unless block 226 100 214 framedblocks:framed_inner_threeway_corner_slope_edge run scoreboard players add #failures fbgv1061 1
execute unless data block 226 100 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 226 99 214 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-120 framedblocks:framed_inv_double_corner_slope_panel
execute unless block 229 100 214 framedblocks:framed_inv_double_corner_slope_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 214 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 229 99 214 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-121 framedblocks:framed_inv_double_corner_slope_panel_w
execute unless block 232 100 214 framedblocks:framed_inv_double_corner_slope_panel_w[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 232 100 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 232 100 214 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 232 99 214 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-122 framedblocks:framed_inv_double_slope_panel
execute unless block 235 100 214 framedblocks:framed_inv_double_slope_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 235 100 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 235 100 214 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 235 99 214 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-123 framedblocks:framed_inv_double_slope_slab
execute unless block 238 100 214 framedblocks:framed_inv_double_slope_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 214 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 238 99 214 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-124 framedblocks:framed_iron_door
execute unless block 241 100 214 framedblocks:framed_iron_door[facing=south,half=lower,hinge=left,open=false,powered=false,solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 241 100 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 241 99 214 minecraft:stone run scoreboard players add #failures fbgv1061 1
execute unless block 241 101 214 framedblocks:framed_iron_door[facing=south,half=upper,hinge=left,open=false,powered=false,solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 241 101 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1

# fb1061-125 framedblocks:framed_iron_gate
execute unless block 244 100 214 framedblocks:framed_iron_gate[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 244 99 214 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-126 framedblocks:framed_iron_pressure_plate
execute unless block 247 100 214 framedblocks:framed_iron_pressure_plate run scoreboard players add #failures fbgv1061 1
execute unless data block 247 100 214 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 247 99 214 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-127 framedblocks:framed_iron_trapdoor
execute unless block 196 100 217 framedblocks:framed_iron_trapdoor[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 196 100 217 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 196 99 217 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-128 framedblocks:framed_item_frame
execute unless block 199 100 217 framedblocks:framed_item_frame[facing=north,map_frame=false] run scoreboard players add #failures fbgv1061 1
execute unless data block 199 100 217 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 199 99 217 minecraft:stone run scoreboard players add #failures fbgv1061 1
execute unless block 199 100 216 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-129 framedblocks:framed_ladder
execute unless block 202 100 217 framedblocks:framed_ladder[facing=north] run scoreboard players add #failures fbgv1061 1
execute unless data block 202 100 217 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 202 99 217 minecraft:stone run scoreboard players add #failures fbgv1061 1
execute unless block 202 100 216 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-130 framedblocks:framed_lantern
execute unless block 205 100 217 framedblocks:framed_lantern run scoreboard players add #failures fbgv1061 1
execute unless data block 205 100 217 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 205 99 217 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-131 framedblocks:framed_large_button
execute unless block 208 100 217 framedblocks:framed_large_button[face=floor,facing=south,powered=false] run scoreboard players add #failures fbgv1061 1
execute unless data block 208 100 217 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 208 99 217 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-132 framedblocks:framed_large_corner_slope_panel
execute unless block 211 100 217 framedblocks:framed_large_corner_slope_panel run scoreboard players add #failures fbgv1061 1
execute unless data block 211 100 217 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 211 99 217 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-133 framedblocks:framed_large_corner_slope_panel_w
execute unless block 214 100 217 framedblocks:framed_large_corner_slope_panel_w run scoreboard players add #failures fbgv1061 1
execute unless data block 214 100 217 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 214 99 217 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-134 framedblocks:framed_large_double_corner_slope_panel
execute unless block 217 100 217 framedblocks:framed_large_double_corner_slope_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 217 100 217 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 217 100 217 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 217 99 217 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-135 framedblocks:framed_large_double_corner_slope_panel_w
execute unless block 220 100 217 framedblocks:framed_large_double_corner_slope_panel_w[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 220 100 217 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 220 100 217 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 220 99 217 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-136 framedblocks:framed_large_inner_corner_slope_panel
execute unless block 223 100 217 framedblocks:framed_large_inner_corner_slope_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 223 100 217 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 223 99 217 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-137 framedblocks:framed_large_inner_corner_slope_panel_w
execute unless block 226 100 217 framedblocks:framed_large_inner_corner_slope_panel_w[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 226 100 217 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 226 99 217 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-138 framedblocks:framed_large_stone_button
execute unless block 229 100 217 framedblocks:framed_large_stone_button[face=floor,facing=south,powered=false] run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 217 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 229 99 217 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-139 framedblocks:framed_lattice_block
execute unless block 232 100 217 framedblocks:framed_lattice_block run scoreboard players add #failures fbgv1061 1
execute unless data block 232 100 217 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 232 99 217 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-140 framedblocks:framed_layered_cube
execute unless block 235 100 217 framedblocks:framed_layered_cube[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 235 100 217 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 235 99 217 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-141 framedblocks:framed_lever
execute unless block 238 100 217 framedblocks:framed_lever[face=floor,facing=south,powered=false] run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 217 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 238 99 217 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-142 framedblocks:framed_lightning_rod
execute unless block 241 100 217 framedblocks:framed_lightning_rod run scoreboard players add #failures fbgv1061 1
execute unless data block 241 100 217 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 241 99 217 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-143 framedblocks:framed_masonry_corner
execute unless block 244 100 217 framedblocks:framed_masonry_corner[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 217 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 217 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 244 99 217 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-144 framedblocks:framed_masonry_corner_segment
execute unless block 247 100 217 framedblocks:framed_masonry_corner_segment run scoreboard players add #failures fbgv1061 1
execute unless data block 247 100 217 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 247 99 217 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-145 framedblocks:framed_mini_cube
execute unless block 196 100 220 framedblocks:framed_mini_cube run scoreboard players add #failures fbgv1061 1
execute unless data block 196 100 220 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 196 99 220 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-146 framedblocks:framed_obsidian_pressure_plate
execute unless block 199 100 220 framedblocks:framed_obsidian_pressure_plate run scoreboard players add #failures fbgv1061 1
execute unless data block 199 100 220 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 199 99 220 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-147 framedblocks:framed_one_way_window
execute unless block 202 100 220 framedblocks:framed_one_way_window run scoreboard players add #failures fbgv1061 1
execute unless data block 202 100 220 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 202 99 220 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-148 framedblocks:framed_pane
execute unless block 205 100 220 framedblocks:framed_pane run scoreboard players add #failures fbgv1061 1
execute unless data block 205 100 220 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 205 99 220 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-149 framedblocks:framed_panel
execute unless block 208 100 220 framedblocks:framed_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 208 100 220 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 208 99 220 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-150 framedblocks:framed_path
execute unless block 211 100 220 framedblocks:framed_path[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 211 100 220 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 211 99 220 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-151 framedblocks:framed_pillar
execute unless block 214 100 220 framedblocks:framed_pillar run scoreboard players add #failures fbgv1061 1
execute unless data block 214 100 220 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 214 99 220 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-152 framedblocks:framed_pillar_socket
execute unless block 217 100 220 framedblocks:framed_pillar_socket[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 217 100 220 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 217 99 220 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-153 framedblocks:framed_post
execute unless block 220 100 220 framedblocks:framed_post run scoreboard players add #failures fbgv1061 1
execute unless data block 220 100 220 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 220 99 220 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-154 framedblocks:framed_powered_rail_slope
execute unless block 223 100 220 framedblocks:framed_powered_rail_slope[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 223 100 220 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 223 99 220 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-155 framedblocks:framed_pressure_plate
execute unless block 226 100 220 framedblocks:framed_pressure_plate run scoreboard players add #failures fbgv1061 1
execute unless data block 226 100 220 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 226 99 220 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-156 framedblocks:framed_prism
execute unless block 229 100 220 framedblocks:framed_prism[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 220 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 229 99 220 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-157 framedblocks:framed_prism_corner
execute unless block 232 100 220 framedblocks:framed_prism_corner run scoreboard players add #failures fbgv1061 1
execute unless data block 232 100 220 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 232 99 220 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-158 framedblocks:framed_pyramid
execute unless block 235 100 220 framedblocks:framed_pyramid[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 235 100 220 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 235 99 220 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-159 framedblocks:framed_pyramid_slab
execute unless block 238 100 220 framedblocks:framed_pyramid_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 220 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 238 99 220 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-160 framedblocks:framed_rail_slope
execute unless block 241 100 220 framedblocks:framed_rail_slope[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 241 100 220 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 241 99 220 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-161 framedblocks:framed_redstone_block
execute unless block 244 100 220 framedblocks:framed_redstone_block[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 220 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 244 99 220 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-162 framedblocks:framed_redstone_torch
execute unless block 247 100 220 framedblocks:framed_redstone_torch run scoreboard players add #failures fbgv1061 1
execute unless data block 247 100 220 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 247 99 220 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-163 framedblocks:framed_redstone_wall_torch
execute unless block 196 100 223 framedblocks:framed_redstone_wall_torch[facing=south,lit=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 196 100 223 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 196 99 223 minecraft:stone run scoreboard players add #failures fbgv1061 1
execute unless block 196 100 222 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-164 framedblocks:framed_secret_storage
execute unless block 199 100 223 framedblocks:framed_secret_storage[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 199 100 223 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 199 99 223 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-165 framedblocks:framed_sign
execute unless block 202 100 223 framedblocks:framed_sign run scoreboard players add #failures fbgv1061 1
execute unless data block 202 100 223 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 202 99 223 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-166 framedblocks:framed_slab
execute unless block 205 100 223 framedblocks:framed_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 205 100 223 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 205 99 223 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-167 framedblocks:framed_slab_corner
execute unless block 208 100 223 framedblocks:framed_slab_corner run scoreboard players add #failures fbgv1061 1
execute unless data block 208 100 223 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 208 99 223 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-168 framedblocks:framed_slab_edge
execute unless block 211 100 223 framedblocks:framed_slab_edge run scoreboard players add #failures fbgv1061 1
execute unless data block 211 100 223 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 211 99 223 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-169 framedblocks:framed_sliced_sloped_stairs_slab
execute unless block 214 100 223 framedblocks:framed_sliced_sloped_stairs_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 214 100 223 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 214 100 223 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 214 99 223 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-170 framedblocks:framed_sliced_sloped_stairs_slope
execute unless block 217 100 223 framedblocks:framed_sliced_sloped_stairs_slope[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 217 100 223 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 217 100 223 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 217 99 223 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-171 framedblocks:framed_sliced_stairs_panel
execute unless block 220 100 223 framedblocks:framed_sliced_stairs_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 220 100 223 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 220 100 223 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 220 99 223 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-172 framedblocks:framed_sliced_stairs_slab
execute unless block 223 100 223 framedblocks:framed_sliced_stairs_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 223 100 223 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 223 100 223 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 223 99 223 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-173 framedblocks:framed_slope
execute unless block 226 100 223 framedblocks:framed_slope[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 226 100 223 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 226 99 223 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-174 framedblocks:framed_slope_edge
execute unless block 229 100 223 framedblocks:framed_slope_edge run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 223 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 229 99 223 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-175 framedblocks:framed_slope_panel
execute unless block 232 100 223 framedblocks:framed_slope_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 232 100 223 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 232 99 223 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-176 framedblocks:framed_slope_slab
execute unless block 235 100 223 framedblocks:framed_slope_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 235 100 223 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 235 99 223 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-177 framedblocks:framed_sloped_double_stairs
execute unless block 238 100 223 framedblocks:framed_sloped_double_stairs[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 223 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 223 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 238 99 223 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-178 framedblocks:framed_sloped_prism
execute unless block 241 100 223 framedblocks:framed_sloped_prism[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 241 100 223 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 241 99 223 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-179 framedblocks:framed_sloped_stairs
execute unless block 244 100 223 framedblocks:framed_sloped_stairs[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 223 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 244 99 223 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-180 framedblocks:framed_small_corner_slope_panel
execute unless block 247 100 223 framedblocks:framed_small_corner_slope_panel run scoreboard players add #failures fbgv1061 1
execute unless data block 247 100 223 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 247 99 223 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-181 framedblocks:framed_small_corner_slope_panel_w
execute unless block 196 100 226 framedblocks:framed_small_corner_slope_panel_w run scoreboard players add #failures fbgv1061 1
execute unless data block 196 100 226 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 196 99 226 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-182 framedblocks:framed_small_double_corner_slope_panel
execute unless block 199 100 226 framedblocks:framed_small_double_corner_slope_panel run scoreboard players add #failures fbgv1061 1
execute unless data block 199 100 226 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 199 100 226 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 199 99 226 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-183 framedblocks:framed_small_double_corner_slope_panel_w
execute unless block 202 100 226 framedblocks:framed_small_double_corner_slope_panel_w run scoreboard players add #failures fbgv1061 1
execute unless data block 202 100 226 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 202 100 226 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 202 99 226 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-184 framedblocks:framed_small_inner_corner_slope_panel
execute unless block 205 100 226 framedblocks:framed_small_inner_corner_slope_panel run scoreboard players add #failures fbgv1061 1
execute unless data block 205 100 226 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 205 99 226 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-185 framedblocks:framed_small_inner_corner_slope_panel_w
execute unless block 208 100 226 framedblocks:framed_small_inner_corner_slope_panel_w run scoreboard players add #failures fbgv1061 1
execute unless data block 208 100 226 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 208 99 226 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-186 framedblocks:framed_soul_lantern
execute unless block 211 100 226 framedblocks:framed_soul_lantern run scoreboard players add #failures fbgv1061 1
execute unless data block 211 100 226 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 211 99 226 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-187 framedblocks:framed_soul_torch
execute unless block 214 100 226 framedblocks:framed_soul_torch run scoreboard players add #failures fbgv1061 1
execute unless data block 214 100 226 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 214 99 226 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-188 framedblocks:framed_soul_wall_torch
execute unless block 217 100 226 framedblocks:framed_soul_wall_torch[facing=south] run scoreboard players add #failures fbgv1061 1
execute unless data block 217 100 226 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 217 99 226 minecraft:stone run scoreboard players add #failures fbgv1061 1
execute unless block 217 100 225 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-189 framedblocks:framed_split_pillar_socket
execute unless block 220 100 226 framedblocks:framed_split_pillar_socket[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 220 100 226 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 220 100 226 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 220 99 226 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-190 framedblocks:framed_stacked_corner_slope_edge
execute unless block 223 100 226 framedblocks:framed_stacked_corner_slope_edge[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 223 100 226 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 223 100 226 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 223 99 226 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-191 framedblocks:framed_stacked_corner_slope_panel
execute unless block 226 100 226 framedblocks:framed_stacked_corner_slope_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 226 100 226 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 226 100 226 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 226 99 226 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-192 framedblocks:framed_stacked_corner_slope_panel_w
execute unless block 229 100 226 framedblocks:framed_stacked_corner_slope_panel_w[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 226 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 226 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 229 99 226 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-193 framedblocks:framed_stacked_inner_corner_slope_edge
execute unless block 232 100 226 framedblocks:framed_stacked_inner_corner_slope_edge[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 232 100 226 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 232 100 226 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 232 99 226 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-194 framedblocks:framed_stacked_inner_corner_slope_panel
execute unless block 235 100 226 framedblocks:framed_stacked_inner_corner_slope_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 235 100 226 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 235 100 226 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 235 99 226 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-195 framedblocks:framed_stacked_inner_corner_slope_panel_w
execute unless block 238 100 226 framedblocks:framed_stacked_inner_corner_slope_panel_w[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 226 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 226 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 238 99 226 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-196 framedblocks:framed_stacked_pyramid_slab
execute unless block 241 100 226 framedblocks:framed_stacked_pyramid_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 241 100 226 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 241 100 226 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 241 99 226 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-197 framedblocks:framed_stacked_slope_edge
execute unless block 244 100 226 framedblocks:framed_stacked_slope_edge[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 226 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 226 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 244 99 226 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-198 framedblocks:framed_stacked_slope_panel
execute unless block 247 100 226 framedblocks:framed_stacked_slope_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 247 100 226 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 247 100 226 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 247 99 226 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-199 framedblocks:framed_stacked_slope_slab
execute unless block 196 100 229 framedblocks:framed_stacked_slope_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 196 100 229 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 196 100 229 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 196 99 229 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-200 framedblocks:framed_stairs
execute unless block 199 100 229 framedblocks:framed_stairs[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 199 100 229 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 199 99 229 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-201 framedblocks:framed_stone_button
execute unless block 202 100 229 framedblocks:framed_stone_button[face=floor,facing=south,powered=false] run scoreboard players add #failures fbgv1061 1
execute unless data block 202 100 229 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 202 99 229 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-202 framedblocks:framed_stone_pressure_plate
execute unless block 205 100 229 framedblocks:framed_stone_pressure_plate run scoreboard players add #failures fbgv1061 1
execute unless data block 205 100 229 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 205 99 229 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-203 framedblocks:framed_tank
execute unless block 208 100 229 framedblocks:framed_tank[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 208 100 229 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 208 99 229 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-204 framedblocks:framed_target
execute unless block 211 100 229 framedblocks:framed_target[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 211 100 229 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 211 99 229 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-205 framedblocks:framed_thick_lattice
execute unless block 214 100 229 framedblocks:framed_thick_lattice run scoreboard players add #failures fbgv1061 1
execute unless data block 214 100 229 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 214 99 229 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-206 framedblocks:framed_threeway_corner
execute unless block 217 100 229 framedblocks:framed_threeway_corner run scoreboard players add #failures fbgv1061 1
execute unless data block 217 100 229 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 217 99 229 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-207 framedblocks:framed_threeway_corner_pillar
execute unless block 220 100 229 framedblocks:framed_threeway_corner_pillar run scoreboard players add #failures fbgv1061 1
execute unless data block 220 100 229 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 220 99 229 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-208 framedblocks:framed_threeway_corner_slope_edge
execute unless block 223 100 229 framedblocks:framed_threeway_corner_slope_edge run scoreboard players add #failures fbgv1061 1
execute unless data block 223 100 229 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 223 99 229 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-209 framedblocks:framed_torch
execute unless block 226 100 229 framedblocks:framed_torch run scoreboard players add #failures fbgv1061 1
execute unless data block 226 100 229 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 226 99 229 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-210 framedblocks:framed_trapdoor
execute unless block 229 100 229 framedblocks:framed_trapdoor[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 229 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 229 99 229 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-211 framedblocks:framed_tube
execute unless block 232 100 229 framedblocks:framed_tube[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 232 100 229 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 232 99 229 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-212 framedblocks:framed_upper_pyramid_slab
execute unless block 235 100 229 framedblocks:framed_upper_pyramid_slab[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 235 100 229 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 235 99 229 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-213 framedblocks:framed_vertical_divided_stairs
execute unless block 238 100 229 framedblocks:framed_vertical_divided_stairs[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 229 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 229 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 238 99 229 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-214 framedblocks:framed_vertical_double_half_slope
execute unless block 241 100 229 framedblocks:framed_vertical_double_half_slope[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 241 100 229 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 241 100 229 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 241 99 229 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-215 framedblocks:framed_vertical_double_half_stairs
execute unless block 244 100 229 framedblocks:framed_vertical_double_half_stairs[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 229 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 229 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 244 99 229 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-216 framedblocks:framed_vertical_double_stairs
execute unless block 247 100 229 framedblocks:framed_vertical_double_stairs[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 247 100 229 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 247 100 229 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 247 99 229 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-217 framedblocks:framed_vertical_half_slope
execute unless block 196 100 232 framedblocks:framed_vertical_half_slope run scoreboard players add #failures fbgv1061 1
execute unless data block 196 100 232 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 196 99 232 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-218 framedblocks:framed_vertical_half_stairs
execute unless block 199 100 232 framedblocks:framed_vertical_half_stairs run scoreboard players add #failures fbgv1061 1
execute unless data block 199 100 232 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 199 99 232 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-219 framedblocks:framed_vertical_sliced_sloped_stairs_panel
execute unless block 202 100 232 framedblocks:framed_vertical_sliced_sloped_stairs_panel[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 202 100 232 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 202 100 232 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 202 99 232 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-220 framedblocks:framed_vertical_sliced_sloped_stairs_slope
execute unless block 205 100 232 framedblocks:framed_vertical_sliced_sloped_stairs_slope[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 205 100 232 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 205 100 232 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 205 99 232 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-221 framedblocks:framed_vertical_sliced_stairs
execute unless block 208 100 232 framedblocks:framed_vertical_sliced_stairs[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 208 100 232 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 208 100 232 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 208 99 232 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-222 framedblocks:framed_vertical_sloped_double_stairs
execute unless block 211 100 232 framedblocks:framed_vertical_sloped_double_stairs[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 211 100 232 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless data block 211 100 232 {camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 211 99 232 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-223 framedblocks:framed_vertical_sloped_stairs
execute unless block 214 100 232 framedblocks:framed_vertical_sloped_stairs[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 214 100 232 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 214 99 232 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-224 framedblocks:framed_vertical_stairs
execute unless block 217 100 232 framedblocks:framed_vertical_stairs[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 217 100 232 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 217 99 232 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-225 framedblocks:framed_wall
execute unless block 220 100 232 framedblocks:framed_wall run scoreboard players add #failures fbgv1061 1
execute unless data block 220 100 232 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 220 99 232 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-226 framedblocks:framed_wall_board
execute unless block 223 100 232 framedblocks:framed_wall_board[solid=true] run scoreboard players add #failures fbgv1061 1
execute unless data block 223 100 232 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 223 99 232 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-227 framedblocks:framed_wall_hanging_sign
execute unless block 226 100 232 framedblocks:framed_wall_hanging_sign[facing=south] run scoreboard players add #failures fbgv1061 1
execute unless data block 226 100 232 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 226 99 232 minecraft:stone run scoreboard players add #failures fbgv1061 1
execute unless block 227 100 232 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-228 framedblocks:framed_wall_sign
execute unless block 229 100 232 framedblocks:framed_wall_sign[facing=south] run scoreboard players add #failures fbgv1061 1
execute unless data block 229 100 232 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 229 99 232 minecraft:stone run scoreboard players add #failures fbgv1061 1
execute unless block 229 100 231 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-229 framedblocks:framed_wall_torch
execute unless block 232 100 232 framedblocks:framed_wall_torch[facing=south] run scoreboard players add #failures fbgv1061 1
execute unless data block 232 100 232 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 232 99 232 minecraft:stone run scoreboard players add #failures fbgv1061 1
execute unless block 232 100 231 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-230 framedblocks:framed_waterloggable_gold_pressure_plate
execute unless block 235 100 232 framedblocks:framed_waterloggable_gold_pressure_plate run scoreboard players add #failures fbgv1061 1
execute unless data block 235 100 232 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 235 99 232 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-231 framedblocks:framed_waterloggable_iron_pressure_plate
execute unless block 238 100 232 framedblocks:framed_waterloggable_iron_pressure_plate run scoreboard players add #failures fbgv1061 1
execute unless data block 238 100 232 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 238 99 232 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-232 framedblocks:framed_waterloggable_obsidian_pressure_plate
execute unless block 241 100 232 framedblocks:framed_waterloggable_obsidian_pressure_plate run scoreboard players add #failures fbgv1061 1
execute unless data block 241 100 232 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 241 99 232 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-233 framedblocks:framed_waterloggable_pressure_plate
execute unless block 244 100 232 framedblocks:framed_waterloggable_pressure_plate run scoreboard players add #failures fbgv1061 1
execute unless data block 244 100 232 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 244 99 232 minecraft:stone run scoreboard players add #failures fbgv1061 1

# fb1061-234 framedblocks:framed_waterloggable_stone_pressure_plate
execute unless block 247 100 232 framedblocks:framed_waterloggable_stone_pressure_plate run scoreboard players add #failures fbgv1061 1
execute unless data block 247 100 232 {camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}} run scoreboard players add #failures fbgv1061 1
execute unless block 247 99 232 minecraft:stone run scoreboard players add #failures fbgv1061 1

function framedblocks_gallery:verify_renderer_paths
execute unless score #failures fbgr1061 matches 0 run scoreboard players add #failures fbgv1061 1

execute if score #failures fbgv1061 matches 0 run tellraw @a [{"text":"FramedBlocks gallery verification passed: 234/234 anchors plus camos and supports.","color":"green"}]
execute unless score #failures fbgv1061 matches 0 run tellraw @a [{"text":"FramedBlocks gallery verification failed with ","color":"red"},{"score":{"name":"#failures","objective":"fbgv1061"}},{"text":" mismatches.","color":"red"}]

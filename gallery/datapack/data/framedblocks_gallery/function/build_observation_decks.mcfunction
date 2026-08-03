# SPDX-License-Identifier: LGPL-3.0-only
# Fixed south and east observation decks for the isolated staging gallery.
function framedblocks_gallery:clear_observation_decks

# south
fill 215 107 252 227 107 256 minecraft:stone_bricks replace
fill 215 108 252 227 108 252 minecraft:iron_bars replace
fill 215 108 256 227 108 256 minecraft:iron_bars replace
fill 215 108 253 215 108 255 minecraft:iron_bars replace
fill 227 108 253 227 108 255 minecraft:iron_bars replace

# east
fill 266 107 208 272 107 220 minecraft:stone_bricks replace
fill 266 108 208 272 108 208 minecraft:iron_bars replace
fill 266 108 220 272 108 220 minecraft:iron_bars replace
fill 266 108 209 266 108 219 minecraft:iron_bars replace
fill 272 108 209 272 108 219 minecraft:iron_bars replace

tellraw @a [{"text":"Built the fixed south and east FramedBlocks observation decks.","color":"aqua"}]
function framedblocks_gallery:verify_observation_decks

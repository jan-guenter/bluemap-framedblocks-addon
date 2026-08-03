# SPDX-License-Identifier: LGPL-3.0-only
# Fixed east observation pose; no teleport if the deck is unsafe.
execute if block 269 107 214 minecraft:stone_bricks if block 269 108 214 minecraft:air if block 269 109 214 minecraft:air run teleport @s 269.5 108 214.5 90 14
execute unless block 269 107 214 minecraft:stone_bricks run tellraw @s [{"text":"Observation pose refused: deck floor is missing.","color":"red"}]
execute unless block 269 108 214 minecraft:air run tellraw @s [{"text":"Observation pose refused: foot space is blocked.","color":"red"}]
execute unless block 269 109 214 minecraft:air run tellraw @s [{"text":"Observation pose refused: head space is blocked.","color":"red"}]

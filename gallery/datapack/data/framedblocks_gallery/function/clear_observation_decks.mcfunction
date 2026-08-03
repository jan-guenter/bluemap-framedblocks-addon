# SPDX-License-Identifier: LGPL-3.0-only
# South and east observation decks: bounded cleanup.
# south
fill 214 106 251 228 110 257 minecraft:air replace
# east
fill 265 106 207 273 110 221 minecraft:air replace
tellraw @a [{"text":"Cleared the FramedBlocks observation decks.","color":"yellow"}]

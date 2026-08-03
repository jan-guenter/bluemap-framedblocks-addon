#!/usr/bin/env python3
# SPDX-License-Identifier: LGPL-3.0-only
"""Generate the exact FramedBlocks 10.6.1 isolated-staging roster datapack."""

from __future__ import annotations

import argparse
from dataclasses import dataclass
import hashlib
import json
import os
from pathlib import Path
import sys
from typing import Iterable


GALLERY_ROOT = Path(__file__).resolve().parent
PROJECT_ROOT = GALLERY_ROOT.parent
BLOCKSTATE_MANIFEST = PROJECT_ROOT / (
    "src/main/resources/bluemap-framedblocks/profiles/10.6.1/blockstate-ids.txt"
)

TARGET_FRAMEDBLOCKS_VERSION = "10.6.1"
TARGET_MINECRAFT_VERSION = "1.21.1"
TARGET_BLOCKSTATE_MANIFEST_SHA256 = (
    "e4aed367abf2f037d92496e5028fc9493ae7fb48c5e8dd6ffb85eeddb13330c9"
)
TARGET_GENERIC_DOUBLE_SHA256 = (
    "88978d117b9a3fd81b40a34d6cf6ac128222bb9fb058fb0765c0c7a2d9bf5bc6"
)
TARGET_SOLID_CAPABLE_SHA256 = (
    "768a9897351f221bda84e6ea048c78d2395251623a94ebbdc94db574c88254bd"
)

SAW_IDS = frozenset(
    {
        "framedblocks:framing_saw",
        "framedblocks:powered_framing_saw",
    }
)

# Exact BlockType entries whose seventh constructor argument (doubleBlock) is
# true at release-correlated FramedBlocks commit
# 99522893fce0c9cd543194be1e8cefd488e0eec8. The generated canonical file is
# sorted through the tracked 236-ID blockstate manifest and hash-locked above.
GENERIC_DOUBLE_IDS = frozenset(
    """
framedblocks:framed_double_slope
framedblocks:framed_divided_slope
framedblocks:framed_double_half_slope
framedblocks:framed_vertical_double_half_slope
framedblocks:framed_double_corner
framedblocks:framed_double_prism_corner
framedblocks:framed_double_threeway_corner
framedblocks:framed_elevated_double_slope_edge
framedblocks:framed_stacked_slope_edge
framedblocks:framed_elev_double_corner_slope_edge
framedblocks:framed_elev_double_inner_corner_slope_edge
framedblocks:framed_stacked_corner_slope_edge
framedblocks:framed_stacked_inner_corner_slope_edge
framedblocks:framed_double_slab
framedblocks:framed_adj_double_slab
framedblocks:framed_adj_double_copycat_slab
framedblocks:framed_divided_slab
framedblocks:framed_double_panel
framedblocks:framed_adj_double_panel
framedblocks:framed_adj_double_copycat_panel
framedblocks:framed_divided_panel_horizontal
framedblocks:framed_divided_panel_vertical
framedblocks:framed_double_stairs
framedblocks:framed_divided_stairs
framedblocks:framed_double_half_stairs
framedblocks:framed_sliced_stairs_slab
framedblocks:framed_sliced_stairs_panel
framedblocks:framed_sloped_double_stairs
framedblocks:framed_sliced_sloped_stairs_slab
framedblocks:framed_sliced_sloped_stairs_slope
framedblocks:framed_vertical_double_stairs
framedblocks:framed_vertical_divided_stairs
framedblocks:framed_vertical_double_half_stairs
framedblocks:framed_vertical_sliced_stairs
framedblocks:framed_vertical_sloped_double_stairs
framedblocks:framed_vertical_sliced_sloped_stairs_panel
framedblocks:framed_vertical_sliced_sloped_stairs_slope
framedblocks:framed_double_threeway_corner_pillar
framedblocks:framed_fancy_rail_slope
framedblocks:framed_fancy_powered_rail_slope
framedblocks:framed_fancy_detector_rail_slope
framedblocks:framed_fancy_activator_rail_slope
framedblocks:framed_split_pillar_socket
framedblocks:framed_elevated_inner_double_prism
framedblocks:framed_elevated_inner_double_sloped_prism
framedblocks:framed_double_slope_slab
framedblocks:framed_inv_double_slope_slab
framedblocks:framed_elevated_double_slope_slab
framedblocks:framed_stacked_slope_slab
framedblocks:framed_flat_double_slope_slab_corner
framedblocks:framed_flat_inv_double_slope_slab_corner
framedblocks:framed_flat_elev_double_slope_slab_corner
framedblocks:framed_flat_elev_inner_double_slope_slab_corner
framedblocks:framed_flat_stacked_slope_slab_corner
framedblocks:framed_flat_stacked_inner_slope_slab_corner
framedblocks:framed_double_slope_panel
framedblocks:framed_inv_double_slope_panel
framedblocks:framed_extended_double_slope_panel
framedblocks:framed_stacked_slope_panel
framedblocks:framed_flat_double_slope_panel_corner
framedblocks:framed_flat_inv_double_slope_panel_corner
framedblocks:framed_flat_ext_double_slope_panel_corner
framedblocks:framed_flat_ext_inner_double_slope_panel_corner
framedblocks:framed_flat_stacked_slope_panel_corner
framedblocks:framed_flat_stacked_inner_slope_panel_corner
framedblocks:framed_small_double_corner_slope_panel
framedblocks:framed_small_double_corner_slope_panel_w
framedblocks:framed_large_double_corner_slope_panel
framedblocks:framed_large_double_corner_slope_panel_w
framedblocks:framed_inv_double_corner_slope_panel
framedblocks:framed_inv_double_corner_slope_panel_w
framedblocks:framed_ext_double_corner_slope_panel
framedblocks:framed_ext_double_corner_slope_panel_w
framedblocks:framed_ext_inner_double_corner_slope_panel
framedblocks:framed_ext_inner_double_corner_slope_panel_w
framedblocks:framed_stacked_corner_slope_panel
framedblocks:framed_stacked_corner_slope_panel_w
framedblocks:framed_stacked_inner_corner_slope_panel
framedblocks:framed_stacked_inner_corner_slope_panel_w
framedblocks:framed_stacked_pyramid_slab
framedblocks:framed_masonry_corner
framedblocks:framed_checkered_cube
framedblocks:framed_checkered_slab
framedblocks:framed_checkered_panel
""".split()
)

# Exact BlockType entries whose first constructor argument
# (canOccludeWithSolidCamo) is true at the same release-correlated commit.
# Direct disk-NBT merging does not call FramedBlockEntity#setCamo, so the
# gallery explicitly selects solid=true for these stone-camouflaged blocks.
SOLID_CAPABLE_IDS = frozenset(
    """
framedblocks:framed_cube
framedblocks:framed_slope
framedblocks:framed_double_slope
framedblocks:framed_divided_slope
framedblocks:framed_double_half_slope
framedblocks:framed_vertical_double_half_slope
framedblocks:framed_corner_slope
framedblocks:framed_inner_corner_slope
framedblocks:framed_double_corner
framedblocks:framed_inner_prism_corner
framedblocks:framed_double_prism_corner
framedblocks:framed_inner_threeway_corner
framedblocks:framed_double_threeway_corner
framedblocks:framed_elevated_slope_edge
framedblocks:framed_elevated_double_slope_edge
framedblocks:framed_stacked_slope_edge
framedblocks:framed_elevated_corner_slope_edge
framedblocks:framed_elevated_inner_corner_slope_edge
framedblocks:framed_elev_double_corner_slope_edge
framedblocks:framed_elev_double_inner_corner_slope_edge
framedblocks:framed_stacked_corner_slope_edge
framedblocks:framed_stacked_inner_corner_slope_edge
framedblocks:framed_slab
framedblocks:framed_double_slab
framedblocks:framed_adj_double_slab
framedblocks:framed_adj_double_copycat_slab
framedblocks:framed_divided_slab
framedblocks:framed_panel
framedblocks:framed_double_panel
framedblocks:framed_adj_double_panel
framedblocks:framed_adj_double_copycat_panel
framedblocks:framed_divided_panel_horizontal
framedblocks:framed_divided_panel_vertical
framedblocks:framed_stairs
framedblocks:framed_double_stairs
framedblocks:framed_divided_stairs
framedblocks:framed_double_half_stairs
framedblocks:framed_sliced_stairs_slab
framedblocks:framed_sliced_stairs_panel
framedblocks:framed_sloped_stairs
framedblocks:framed_sloped_double_stairs
framedblocks:framed_sliced_sloped_stairs_slab
framedblocks:framed_sliced_sloped_stairs_slope
framedblocks:framed_vertical_stairs
framedblocks:framed_vertical_double_stairs
framedblocks:framed_vertical_divided_stairs
framedblocks:framed_vertical_double_half_stairs
framedblocks:framed_vertical_sliced_stairs
framedblocks:framed_vertical_sloped_stairs
framedblocks:framed_vertical_sloped_double_stairs
framedblocks:framed_vertical_sliced_sloped_stairs_panel
framedblocks:framed_vertical_sliced_sloped_stairs_slope
framedblocks:framed_double_threeway_corner_pillar
framedblocks:framed_door
framedblocks:framed_iron_door
framedblocks:framed_trapdoor
framedblocks:framed_iron_trapdoor
framedblocks:framed_floor_board
framedblocks:framed_wall_board
framedblocks:framed_secret_storage
framedblocks:framed_tank
framedblocks:framed_horizontal_pane
framedblocks:framed_rail_slope
framedblocks:framed_powered_rail_slope
framedblocks:framed_detector_rail_slope
framedblocks:framed_activator_rail_slope
framedblocks:framed_fancy_rail_slope
framedblocks:framed_fancy_powered_rail_slope
framedblocks:framed_fancy_detector_rail_slope
framedblocks:framed_fancy_activator_rail_slope
framedblocks:framed_pillar_socket
framedblocks:framed_split_pillar_socket
framedblocks:framed_bouncy_cube
framedblocks:framed_redstone_block
framedblocks:framed_prism
framedblocks:framed_elevated_inner_prism
framedblocks:framed_elevated_inner_double_prism
framedblocks:framed_sloped_prism
framedblocks:framed_elevated_inner_sloped_prism
framedblocks:framed_elevated_inner_double_sloped_prism
framedblocks:framed_slope_slab
framedblocks:framed_elevated_slope_slab
framedblocks:framed_compound_slope_slab
framedblocks:framed_double_slope_slab
framedblocks:framed_inv_double_slope_slab
framedblocks:framed_elevated_double_slope_slab
framedblocks:framed_stacked_slope_slab
framedblocks:framed_flat_slope_slab_corner
framedblocks:framed_flat_inner_slope_slab_corner
framedblocks:framed_flat_elev_slope_slab_corner
framedblocks:framed_flat_elev_inner_slope_slab_corner
framedblocks:framed_flat_double_slope_slab_corner
framedblocks:framed_flat_inv_double_slope_slab_corner
framedblocks:framed_flat_elev_double_slope_slab_corner
framedblocks:framed_flat_elev_inner_double_slope_slab_corner
framedblocks:framed_flat_stacked_slope_slab_corner
framedblocks:framed_flat_stacked_inner_slope_slab_corner
framedblocks:framed_slope_panel
framedblocks:framed_extended_slope_panel
framedblocks:framed_compound_slope_panel
framedblocks:framed_double_slope_panel
framedblocks:framed_inv_double_slope_panel
framedblocks:framed_extended_double_slope_panel
framedblocks:framed_stacked_slope_panel
framedblocks:framed_flat_slope_panel_corner
framedblocks:framed_flat_inner_slope_panel_corner
framedblocks:framed_flat_ext_slope_panel_corner
framedblocks:framed_flat_ext_inner_slope_panel_corner
framedblocks:framed_flat_double_slope_panel_corner
framedblocks:framed_flat_inv_double_slope_panel_corner
framedblocks:framed_flat_ext_double_slope_panel_corner
framedblocks:framed_flat_ext_inner_double_slope_panel_corner
framedblocks:framed_flat_stacked_slope_panel_corner
framedblocks:framed_flat_stacked_inner_slope_panel_corner
framedblocks:framed_large_inner_corner_slope_panel
framedblocks:framed_large_inner_corner_slope_panel_w
framedblocks:framed_ext_corner_slope_panel
framedblocks:framed_ext_corner_slope_panel_w
framedblocks:framed_ext_inner_corner_slope_panel
framedblocks:framed_ext_inner_corner_slope_panel_w
framedblocks:framed_large_double_corner_slope_panel
framedblocks:framed_large_double_corner_slope_panel_w
framedblocks:framed_inv_double_corner_slope_panel
framedblocks:framed_inv_double_corner_slope_panel_w
framedblocks:framed_ext_double_corner_slope_panel
framedblocks:framed_ext_double_corner_slope_panel_w
framedblocks:framed_ext_inner_double_corner_slope_panel
framedblocks:framed_ext_inner_double_corner_slope_panel_w
framedblocks:framed_stacked_corner_slope_panel
framedblocks:framed_stacked_corner_slope_panel_w
framedblocks:framed_stacked_inner_corner_slope_panel
framedblocks:framed_stacked_inner_corner_slope_panel_w
framedblocks:framed_glowing_cube
framedblocks:framed_pyramid
framedblocks:framed_pyramid_slab
framedblocks:framed_elevated_pyramid_slab
framedblocks:framed_upper_pyramid_slab
framedblocks:framed_stacked_pyramid_slab
framedblocks:framed_target
framedblocks:framed_gate
framedblocks:framed_iron_gate
framedblocks:framed_bookshelf
framedblocks:framed_chiseled_bookshelf
framedblocks:framed_centered_slab
framedblocks:framed_centered_panel
framedblocks:framed_masonry_corner
framedblocks:framed_checkered_cube
framedblocks:framed_checkered_slab
framedblocks:framed_checkered_panel
framedblocks:framed_tube
framedblocks:framed_corner_tube
framedblocks:framed_layered_cube
framedblocks:framed_path
""".split()
)

BLOCK_STATE_OVERRIDES = {
    "framedblocks:framed_button": "face=floor,facing=south,powered=false",
    "framedblocks:framed_large_button": "face=floor,facing=south,powered=false",
    "framedblocks:framed_large_stone_button": "face=floor,facing=south,powered=false",
    "framedblocks:framed_stone_button": "face=floor,facing=south,powered=false",
    "framedblocks:framed_lever": "face=floor,facing=south,powered=false",
    "framedblocks:framed_wall_torch": "facing=south",
    "framedblocks:framed_soul_wall_torch": "facing=south",
    "framedblocks:framed_redstone_wall_torch": "facing=south,lit=true",
    "framedblocks:framed_wall_sign": "facing=south",
    "framedblocks:framed_wall_hanging_sign": "facing=south",
    "framedblocks:framed_hanging_sign": "attached=false,rotation=0",
    "framedblocks:framed_ladder": "facing=north",
    "framedblocks:framed_item_frame": "facing=north,map_frame=false",
    "framedblocks:framed_glowing_item_frame": "facing=north,map_frame=false",
}

NORTH_BACKING_IDS = frozenset(
    {
        "framedblocks:framed_wall_torch",
        "framedblocks:framed_soul_wall_torch",
        "framedblocks:framed_redstone_wall_torch",
        "framedblocks:framed_wall_sign",
        "framedblocks:framed_ladder",
        "framedblocks:framed_item_frame",
        "framedblocks:framed_glowing_item_frame",
    }
)
EAST_ARM_IDS = frozenset({"framedblocks:framed_wall_hanging_sign"})
CEILING_SUPPORT_IDS = frozenset({"framedblocks:framed_hanging_sign"})
DOOR_IDS = frozenset(
    {
        "framedblocks:framed_door",
        "framedblocks:framed_iron_door",
    }
)

MAP_MASK_MIN = 192
MAP_MASK_MAX = 320
ROSTER_ORIGIN_X = 196
ROSTER_ORIGIN_Y = 100
ROSTER_ORIGIN_Z = 196
ROSTER_COLUMNS = 18
ROSTER_ROWS = 13
ROSTER_SPACING = 3
RENDERER_MATRIX_COLUMNS = 6
RENDERER_MATRIX_ROWS = 3
RENDERER_MATRIX_SPACING = 4


@dataclass(frozen=True, order=True)
class Position:
    x: int
    y: int
    z: int

    def as_dict(self) -> dict[str, int]:
        return {"x": self.x, "y": self.y, "z": self.z}

    def command(self) -> str:
        return f"{self.x} {self.y} {self.z}"


@dataclass(frozen=True)
class Aabb:
    minimum: Position
    maximum: Position

    def contains(self, position: Position) -> bool:
        return (
            self.minimum.x <= position.x <= self.maximum.x
            and self.minimum.y <= position.y <= self.maximum.y
            and self.minimum.z <= position.z <= self.maximum.z
        )

    def overlaps(self, other: "Aabb") -> bool:
        return not (
            self.maximum.x < other.minimum.x
            or other.maximum.x < self.minimum.x
            or self.maximum.y < other.minimum.y
            or other.maximum.y < self.minimum.y
            or self.maximum.z < other.minimum.z
            or other.maximum.z < self.minimum.z
        )

    def volume(self) -> int:
        return (
            (self.maximum.x - self.minimum.x + 1)
            * (self.maximum.y - self.minimum.y + 1)
            * (self.maximum.z - self.minimum.z + 1)
        )

    def as_dict(self) -> dict[str, dict[str, int]]:
        return {
            "min": self.minimum.as_dict(),
            "max": self.maximum.as_dict(),
        }


@dataclass(frozen=True)
class CameraPose:
    pose_id: str
    x: float
    y: float
    z: float
    yaw: float
    pitch: float

    def as_dict(self) -> dict[str, object]:
        return {
            "pose_id": self.pose_id,
            "position": {"x": self.x, "y": self.y, "z": self.z},
            "yaw": self.yaw,
            "pitch": self.pitch,
        }

    def foot_block(self) -> Position:
        return Position(int(self.x), int(self.y), int(self.z))

    def teleport_command(self) -> str:
        return (
            f"teleport @s {self.x:g} {self.y:g} {self.z:g} "
            f"{self.yaw:g} {self.pitch:g}"
        )


@dataclass(frozen=True)
class ObservationDeck:
    deck_id: str
    clear_aabb: Aabb
    floor_aabb: Aabb
    pose: CameraPose


@dataclass(frozen=True)
class RendererCaseSpec:
    case_id: str
    category: str
    block_id: str
    block_state: str
    expected_path: str
    expected_reason: str
    primary_camo: str
    secondary_camo: str | None = None
    extra_nbt: tuple[str, ...] = ()
    companion_block_state: str | None = None
    glowing_modifier: bool = False
    reinforced_modifier: bool = False


GALLERY_CLEAR_AABB = Aabb(Position(195, 98, 195), Position(248, 102, 233))
ROSTER_ANCHOR_AABB = Aabb(Position(196, 100, 196), Position(247, 100, 232))
M0_PROTECTED_CORRIDOR = Aabb(Position(252, -64, 192), Position(261, 319, 320))
M0_FIXTURE_AABB = Aabb(Position(256, 99, 256), Position(258, 101, 256))
RENDERER_MATRIX_CLEAR_AABB = Aabb(
    Position(196, 98, 238), Position(220, 102, 250)
)
RENDERER_MATRIX_ANCHOR_AABB = Aabb(
    Position(198, 100, 240), Position(218, 100, 248)
)

OBSERVATION_DECKS = (
    ObservationDeck(
        "south",
        Aabb(Position(214, 106, 251), Position(228, 110, 257)),
        Aabb(Position(215, 107, 252), Position(227, 107, 256)),
        CameraPose("gallery_overview_south", 221.5, 108.0, 254.5, 180.0, 14.0),
    ),
    ObservationDeck(
        "east",
        Aabb(Position(265, 106, 207), Position(273, 110, 221)),
        Aabb(Position(266, 107, 208), Position(272, 107, 220)),
        CameraPose("gallery_overview_east", 269.5, 108.0, 214.5, 90.0, 14.0),
    ),
)

RENDERER_CASE_SPECS = (
    RendererCaseSpec(
        "fb1061-rp-01",
        "proven_static_single",
        "framedblocks:framed_cube",
        (
            "framedblocks:framed_cube[alt=false,glowing=false,"
            "propagates_skylight=false,reinforced=false,solid=true,solid_bg=false]"
        ),
        "addon_geometry",
        "state-only-baked-model",
        "minecraft:stone",
    ),
    RendererCaseSpec(
        "fb1061-rp-02",
        "proven_static_double",
        "framedblocks:framed_double_slab",
        (
            "framedblocks:framed_double_slab[glowing=false,"
            "propagates_skylight=false,solid=true]"
        ),
        "addon_geometry",
        "state-only-baked-model",
        "minecraft:stone",
        "minecraft:gold_block",
    ),
    RendererCaseSpec(
        "fb1061-rp-03",
        "proven_static_oriented",
        "framedblocks:framed_slope",
        (
            "framedblocks:framed_slope[facing=east,glowing=false,"
            "propagates_skylight=false,solid=true,type=bottom,"
            "waterlogged=false,yslope=false]"
        ),
        "addon_geometry",
        "state-only-baked-model",
        "minecraft:stone",
    ),
    RendererCaseSpec(
        "fb1061-rp-04",
        "fallback_adjustable",
        "framedblocks:framed_adj_double_slab",
        (
            "framedblocks:framed_adj_double_slab[glowing=false,"
            "propagates_skylight=false,solid=true]"
        ),
        "stock_fallback",
        "adjustable-model-data-required",
        "minecraft:stone",
        "minecraft:gold_block",
        ("first_height:5",),
    ),
    RendererCaseSpec(
        "fb1061-rp-05",
        "fallback_collapsible",
        "framedblocks:framed_collapsible_block",
        (
            "framedblocks:framed_collapsible_block[face=up,glowing=false,"
            "propagates_skylight=false,rot_split_line=false,waterlogged=false]"
        ),
        "stock_fallback",
        "collapsible-model-data-required",
        "minecraft:stone",
        extra_nbt=("offsets:270600",),
    ),
    RendererCaseSpec(
        "fb1061-rp-06",
        "fallback_flower",
        "framedblocks:framed_flower_pot",
        (
            "framedblocks:framed_flower_pot[glowing=false,hanging=false,"
            "propagates_skylight=false]"
        ),
        "stock_fallback",
        "flower-model-data-required",
        "minecraft:stone",
        extra_nbt=('flower:"minecraft:dandelion"',),
    ),
    RendererCaseSpec(
        "fb1061-rp-07",
        "fallback_one_way",
        "framedblocks:framed_one_way_window",
        (
            "framedblocks:framed_one_way_window[face=east,glowing=false,"
            "propagates_skylight=false]"
        ),
        "stock_fallback",
        "one-way-window-model-data-required",
        "minecraft:stone",
    ),
    RendererCaseSpec(
        "fb1061-rp-08",
        "fallback_block_entity_renderer",
        "framedblocks:framed_sign",
        (
            "framedblocks:framed_sign[glowing=false,propagates_skylight=false,"
            "rotation=0,waterlogged=false]"
        ),
        "stock_fallback",
        "block-entity-renderer-required",
        "minecraft:stone",
    ),
    RendererCaseSpec(
        "fb1061-rp-09",
        "fallback_special_camo_overlay",
        "framedblocks:framed_bouncy_cube",
        (
            "framedblocks:framed_bouncy_cube[glowing=false,"
            "propagates_skylight=false,solid=true]"
        ),
        "stock_fallback",
        "camo-overlay-model-data-required",
        "minecraft:stone",
    ),
    RendererCaseSpec(
        "fb1061-rp-10",
        "fallback_waterlogged",
        "framedblocks:framed_slab",
        (
            "framedblocks:framed_slab[glowing=false,propagates_skylight=false,"
            "solid=true,top=false,waterlogged=true]"
        ),
        "stock_fallback",
        "waterlogged-fluid-rendering-required",
        "minecraft:stone",
    ),
    RendererCaseSpec(
        "fb1061-rp-11",
        "fallback_adjacent_framed_neighbor",
        "framedblocks:framed_cube",
        (
            "framedblocks:framed_cube[alt=false,glowing=false,"
            "propagates_skylight=false,reinforced=false,solid=true,solid_bg=false]"
        ),
        "stock_fallback",
        "neighbor-hidden-face-model-data-required",
        "minecraft:stone",
        companion_block_state=(
            "framedblocks:framed_cube[alt=false,glowing=false,"
            "propagates_skylight=false,reinforced=false,solid=true,solid_bg=false]"
        ),
    ),
    RendererCaseSpec(
        "fb1061-rp-12",
        "fallback_non_opaque_camo",
        "framedblocks:framed_cube",
        (
            "framedblocks:framed_cube[alt=false,glowing=false,"
            "propagates_skylight=false,reinforced=false,solid=false,solid_bg=false]"
        ),
        "stock_fallback",
        "primary-camo-material-unresolved",
        "minecraft:glass",
    ),
    RendererCaseSpec(
        "fb1061-rp-13",
        "fallback_glowing",
        "framedblocks:framed_slab",
        (
            "framedblocks:framed_slab[glowing=true,propagates_skylight=false,"
            "solid=true,top=false,waterlogged=false]"
        ),
        "stock_fallback",
        "dynamic-light-camo-required",
        "minecraft:stone",
        glowing_modifier=True,
    ),
    RendererCaseSpec(
        "fb1061-rp-14",
        "fallback_propagates_skylight",
        "framedblocks:framed_slab",
        (
            "framedblocks:framed_slab[glowing=false,propagates_skylight=true,"
            "solid=false,top=false,waterlogged=false]"
        ),
        "stock_fallback",
        "dynamic-skylight-camo-required",
        "minecraft:glass",
    ),
    RendererCaseSpec(
        "fb1061-rp-15",
        "fallback_reinforced",
        "framedblocks:framed_cube",
        (
            "framedblocks:framed_cube[alt=false,glowing=false,"
            "propagates_skylight=false,reinforced=false,solid=true,solid_bg=false]"
        ),
        "stock_fallback",
        "reinforcement-model-data-required",
        "minecraft:stone",
        reinforced_modifier=True,
    ),
)

UNBUILT_RENDERER_CASES = (
    {
        "category": "fallback_missing_camo",
        "status": "not_safely_persistable_by_datapack",
        "reason": (
            "FramedBlocks normalizes an absent disk camo to its empty container and "
            "writes a canonical camo value on save."
        ),
    },
    {
        "category": "fallback_malformed_camo",
        "status": "not_safely_persistable_by_datapack",
        "reason": (
            "FramedBlocks decodes, validates, logs, and normalizes malformed disk "
            "camouflage during block-entity load; raw chunk editing is out of scope."
        ),
    },
)

RENDERER_FUNCTIONS = {
    "build": "framedblocks_gallery:build_renderer_paths",
    "verify": "framedblocks_gallery:verify_renderer_paths",
    "clear": "framedblocks_gallery:clear_renderer_paths",
}
OBSERVATION_DECK_FUNCTIONS = {
    "build": "framedblocks_gallery:build_observation_decks",
    "verify": "framedblocks_gallery:verify_observation_decks",
    "clear": "framedblocks_gallery:clear_observation_decks",
}
OBSERVATION_POSE_FUNCTIONS = {
    "south": "framedblocks_gallery:pose_south",
    "east": "framedblocks_gallery:pose_east",
}
UNROUTED_FAMILY_REASONS = frozenset(
    {
        "adjustable-model-data-required",
        "collapsible-model-data-required",
        "flower-model-data-required",
        "one-way-window-model-data-required",
        "block-entity-renderer-required",
        "camo-overlay-model-data-required",
    }
)

PRIMARY_CAMO = "minecraft:stone"
SECONDARY_CAMO = "minecraft:gold_block"
PRIMARY_CAMO_SNBT = 'camo:{type:"framedblocks:block",state:{Name:"minecraft:stone"}}'
SECONDARY_CAMO_SNBT = (
    'camo_two:{type:"framedblocks:block",state:{Name:"minecraft:gold_block"}}'
)


def sha256_bytes(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def canonical_lines(values: Iterable[str]) -> bytes:
    return ("\n".join(values) + "\n").encode("utf-8")


def read_roster_ids() -> tuple[list[str], list[str]]:
    raw = BLOCKSTATE_MANIFEST.read_bytes()
    actual_hash = sha256_bytes(raw)
    if actual_hash != TARGET_BLOCKSTATE_MANIFEST_SHA256:
        raise ValueError(
            "unexpected blockstate manifest SHA-256: "
            f"{actual_hash}; expected {TARGET_BLOCKSTATE_MANIFEST_SHA256}"
        )

    all_ids = raw.decode("utf-8").splitlines()
    if len(all_ids) != 236 or len(set(all_ids)) != 236:
        raise ValueError("the exact-profile blockstate manifest must contain 236 unique IDs")
    if set(all_ids) & SAW_IDS != SAW_IDS:
        raise ValueError("the exact-profile manifest does not contain both framing saw IDs")

    roster_ids = [block_id for block_id in all_ids if block_id not in SAW_IDS]
    if len(roster_ids) != 234 or len(set(roster_ids)) != 234:
        raise ValueError("the saw-excluded gallery roster must contain 234 unique IDs")
    if not GENERIC_DOUBLE_IDS <= set(roster_ids):
        missing = sorted(GENERIC_DOUBLE_IDS - set(roster_ids))
        raise ValueError(f"generic-double IDs absent from exact roster: {missing}")
    if len(GENERIC_DOUBLE_IDS) != 84:
        raise ValueError("generic-double classification must contain exactly 84 IDs")
    if len(SOLID_CAPABLE_IDS) != 153 or not SOLID_CAPABLE_IDS <= set(roster_ids):
        raise ValueError("solid-capable classification must contain 153 exact roster IDs")

    double_ids = [block_id for block_id in roster_ids if block_id in GENERIC_DOUBLE_IDS]
    double_hash = sha256_bytes(canonical_lines(double_ids))
    if double_hash != TARGET_GENERIC_DOUBLE_SHA256:
        raise ValueError(
            "unexpected generic-double classification SHA-256: "
            f"{double_hash}; expected {TARGET_GENERIC_DOUBLE_SHA256}"
        )
    solid_ids = [block_id for block_id in roster_ids if block_id in SOLID_CAPABLE_IDS]
    solid_hash = sha256_bytes(canonical_lines(solid_ids))
    if solid_hash != TARGET_SOLID_CAPABLE_SHA256:
        raise ValueError(
            "unexpected solid-capable classification SHA-256: "
            f"{solid_hash}; expected {TARGET_SOLID_CAPABLE_SHA256}"
        )
    return roster_ids, double_ids


def block_state(block_id: str, *, upper_door: bool = False) -> str:
    properties: list[str] = []
    if block_id in DOOR_IDS:
        half = "upper" if upper_door else "lower"
        properties.extend(
            ("facing=south", f"half={half}", "hinge=left", "open=false", "powered=false")
        )
    else:
        override = BLOCK_STATE_OVERRIDES.get(block_id)
        if override:
            properties.extend(override.split(","))
    if block_id in SOLID_CAPABLE_IDS:
        properties.append("solid=true")
    return f"{block_id}[{','.join(properties)}]" if properties else block_id


def camo_snbt(generic_double: bool) -> str:
    entries = [PRIMARY_CAMO_SNBT]
    if generic_double:
        entries.append(SECONDARY_CAMO_SNBT)
    entries.extend(("glowing:0b", "intangible:0b", "reinforced:0b", "updated:3b"))
    return "{" + ",".join(entries) + "}"


def block_camo_entry(key: str, block_id: str) -> str:
    return (
        f'{key}:{{type:"framedblocks:block",state:{{Name:"{block_id}"}}}}'
    )


def renderer_case_snbt(spec: RendererCaseSpec) -> str:
    entries = [block_camo_entry("camo", spec.primary_camo)]
    if spec.secondary_camo is not None:
        entries.append(block_camo_entry("camo_two", spec.secondary_camo))
    entries.extend(spec.extra_nbt)
    entries.extend(
        (
            f"glowing:{int(spec.glowing_modifier)}b",
            "intangible:0b",
            f"reinforced:{int(spec.reinforced_modifier)}b",
            "updated:3b",
        )
    )
    return "{" + ",".join(entries) + "}"


def build_renderer_cases(roster_ids: list[str]) -> list[dict[str, object]]:
    cases: list[dict[str, object]] = []
    for offset, spec in enumerate(RENDERER_CASE_SPECS):
        row, column = divmod(offset, RENDERER_MATRIX_COLUMNS)
        anchor = Position(
            198 + column * RENDERER_MATRIX_SPACING,
            100,
            240 + row * RENDERER_MATRIX_SPACING,
        )
        companions: list[dict[str, object]] = []
        if spec.companion_block_state is not None:
            companion_position = Position(anchor.x + 1, anchor.y, anchor.z)
            companions.append(
                {
                    "kind": "east_framed_neighbor",
                    "position": companion_position.as_dict(),
                    "block_state": spec.companion_block_state,
                    "block_entity_snbt": renderer_case_snbt(
                        RendererCaseSpec(
                            spec.case_id + "-neighbor",
                            spec.category,
                            spec.block_id,
                            spec.companion_block_state,
                            spec.expected_path,
                            spec.expected_reason,
                            spec.primary_camo,
                        )
                    ),
                    "supports": [
                        {
                            "kind": "base",
                            "block_id": "minecraft:stone",
                            "position": Position(
                                companion_position.x,
                                companion_position.y - 1,
                                companion_position.z,
                            ).as_dict(),
                        }
                    ],
                }
            )
        cases.append(
            {
                "case_id": spec.case_id,
                "index": offset + 1,
                "category": spec.category,
                "block_id": spec.block_id,
                "block_state": spec.block_state,
                "expected_path": spec.expected_path,
                "expected_reason": spec.expected_reason,
                "routing_stage": (
                    "addon_renderer_geometry"
                    if spec.expected_path == "addon_geometry"
                    else (
                        "resource_extension_unrouted"
                        if spec.expected_reason in UNROUTED_FAMILY_REASONS
                        else "addon_renderer_runtime_fallback"
                    )
                ),
                "anchor": anchor.as_dict(),
                "primary_camo": spec.primary_camo,
                "secondary_camo": spec.secondary_camo,
                "block_entity_snbt": renderer_case_snbt(spec),
                "supports": [
                    {
                        "kind": "base",
                        "block_id": "minecraft:stone",
                        "position": Position(anchor.x, anchor.y - 1, anchor.z).as_dict(),
                    }
                ],
                "companion_blocks": companions,
            }
        )
    if any(case["block_id"] not in roster_ids for case in cases):
        raise ValueError("renderer-path matrix references an ID outside the exact roster")
    return cases


def support_entries(block_id: str, anchor: Position) -> list[dict[str, object]]:
    supports: list[tuple[str, Position]] = [
        ("base", Position(anchor.x, anchor.y - 1, anchor.z))
    ]
    if block_id in NORTH_BACKING_IDS:
        supports.append(
            ("north_backing", Position(anchor.x, anchor.y, anchor.z - 1))
        )
    if block_id in EAST_ARM_IDS:
        supports.append(("east_arm", Position(anchor.x + 1, anchor.y, anchor.z)))
    if block_id in CEILING_SUPPORT_IDS:
        supports.append(("ceiling", Position(anchor.x, anchor.y + 1, anchor.z)))
    return [
        {
            "kind": kind,
            "block_id": "minecraft:stone",
            "position": position.as_dict(),
        }
        for kind, position in supports
    ]


def position_from_dict(value: dict[str, int]) -> Position:
    return Position(value["x"], value["y"], value["z"])


def build_cases(roster_ids: list[str]) -> list[dict[str, object]]:
    cases: list[dict[str, object]] = []
    for offset, block_id in enumerate(roster_ids):
        row, column = divmod(offset, ROSTER_COLUMNS)
        anchor = Position(
            ROSTER_ORIGIN_X + column * ROSTER_SPACING,
            ROSTER_ORIGIN_Y,
            ROSTER_ORIGIN_Z + row * ROSTER_SPACING,
        )
        generic_double = block_id in GENERIC_DOUBLE_IDS
        companions: list[dict[str, object]] = []
        if block_id in DOOR_IDS:
            companion = Position(anchor.x, anchor.y + 1, anchor.z)
            companions.append(
                {
                    "kind": "upper_half",
                    "block_state": block_state(block_id, upper_door=True),
                    "position": companion.as_dict(),
                    "primary_camo": PRIMARY_CAMO,
                }
            )

        supports = support_entries(block_id, anchor)
        cases.append(
            {
                "case_id": f"fb1061-{offset + 1:03d}",
                "index": offset + 1,
                "block_id": block_id,
                "block_state": block_state(block_id),
                "renderer_family": (
                    "generic_double" if generic_double else "primary_only"
                ),
                "anchor": anchor.as_dict(),
                "primary_camo": PRIMARY_CAMO,
                "secondary_camo": SECONDARY_CAMO if generic_double else None,
                "solid_camo_state": block_id in SOLID_CAPABLE_IDS,
                "supports": supports,
                "companion_blocks": companions,
            }
        )
    return cases


def positions_in(aabb: Aabb) -> Iterable[Position]:
    for x in range(aabb.minimum.x, aabb.maximum.x + 1):
        for y in range(aabb.minimum.y, aabb.maximum.y + 1):
            for z in range(aabb.minimum.z, aabb.maximum.z + 1):
                yield Position(x, y, z)


def observation_deck_blocks(deck: ObservationDeck) -> dict[Position, str]:
    blocks = {
        position: "minecraft:stone_bricks"
        for position in positions_in(deck.floor_aabb)
    }
    railing_y = deck.floor_aabb.minimum.y + 1
    for x in range(deck.floor_aabb.minimum.x, deck.floor_aabb.maximum.x + 1):
        blocks[Position(x, railing_y, deck.floor_aabb.minimum.z)] = (
            "minecraft:iron_bars"
        )
        blocks[Position(x, railing_y, deck.floor_aabb.maximum.z)] = (
            "minecraft:iron_bars"
        )
    for z in range(deck.floor_aabb.minimum.z + 1, deck.floor_aabb.maximum.z):
        blocks[Position(deck.floor_aabb.minimum.x, railing_y, z)] = (
            "minecraft:iron_bars"
        )
        blocks[Position(deck.floor_aabb.maximum.x, railing_y, z)] = (
            "minecraft:iron_bars"
        )
    return blocks


def validate_strict_map_bounds(name: str, aabb: Aabb) -> None:
    if not (
        MAP_MASK_MIN < aabb.minimum.x <= aabb.maximum.x < MAP_MASK_MAX
        and MAP_MASK_MIN < aabb.minimum.z <= aabb.maximum.z < MAP_MASK_MAX
    ):
        raise ValueError(f"{name} must remain strictly inside the configured map mask")


def validate_renderer_layout(cases: list[dict[str, object]]) -> None:
    if len(cases) != 15:
        raise ValueError("renderer-path matrix must contain exactly 15 cases")
    if RENDERER_MATRIX_CLEAR_AABB.volume() > 32_768:
        raise ValueError("renderer-path clear AABB exceeds the fill volume limit")
    validate_strict_map_bounds("renderer-path clear AABB", RENDERER_MATRIX_CLEAR_AABB)
    if RENDERER_MATRIX_CLEAR_AABB.overlaps(GALLERY_CLEAR_AABB):
        raise ValueError("renderer-path matrix overlaps the roster gallery")
    if RENDERER_MATRIX_CLEAR_AABB.overlaps(M0_PROTECTED_CORRIDOR):
        raise ValueError("renderer-path matrix overlaps the protected M0 corridor")

    anchors = [position_from_dict(case["anchor"]) for case in cases]
    if len(set(anchors)) != len(cases):
        raise ValueError("renderer-path matrix anchors must be unique")
    if any(not RENDERER_MATRIX_ANCHOR_AABB.contains(anchor) for anchor in anchors):
        raise ValueError("renderer-path anchor escaped its audited AABB")

    expected_categories = {spec.category for spec in RENDERER_CASE_SPECS}
    actual_categories = {case["category"] for case in cases}
    if len(expected_categories) != len(cases) or actual_categories != expected_categories:
        raise ValueError("renderer-path categories must be unique and complete")
    if sum(case["expected_path"] == "addon_geometry" for case in cases) != 3:
        raise ValueError("renderer-path matrix must contain three add-on cases")
    if sum(case["expected_path"] == "stock_fallback" for case in cases) != 12:
        raise ValueError("renderer-path matrix must contain twelve stock fallbacks")
    if sum(
        case["routing_stage"] == "resource_extension_unrouted" for case in cases
    ) != 6:
        raise ValueError("renderer-path matrix must contain six unrouted families")
    if sum(
        case["routing_stage"] == "addon_renderer_runtime_fallback"
        for case in cases
    ) != 6:
        raise ValueError("renderer-path matrix must contain six runtime fallbacks")

    occupied: set[Position] = set(anchors)
    companion_count = 0
    for case in cases:
        for support in case["supports"]:
            position = position_from_dict(support["position"])
            if not RENDERER_MATRIX_CLEAR_AABB.contains(position):
                raise ValueError(f"renderer support escaped clear AABB: {case['case_id']}")
            if position in occupied:
                raise ValueError(f"renderer support collided: {case['case_id']}")
            occupied.add(position)
        for companion in case["companion_blocks"]:
            companion_count += 1
            position = position_from_dict(companion["position"])
            if not RENDERER_MATRIX_CLEAR_AABB.contains(position):
                raise ValueError(
                    f"renderer companion escaped clear AABB: {case['case_id']}"
                )
            if position in occupied:
                raise ValueError(f"renderer companion collided: {case['case_id']}")
            occupied.add(position)
            for support_entry in companion["supports"]:
                support = position_from_dict(support_entry["position"])
                if (
                    not RENDERER_MATRIX_CLEAR_AABB.contains(support)
                    or support in occupied
                ):
                    raise ValueError(
                        f"renderer companion support collided: {case['case_id']}"
                    )
                occupied.add(support)
    if companion_count != 1:
        raise ValueError("renderer-path matrix must have one adjacent framed companion")


def validate_observation_decks() -> None:
    clear_boxes = [deck.clear_aabb for deck in OBSERVATION_DECKS]
    if len({deck.deck_id for deck in OBSERVATION_DECKS}) != len(OBSERVATION_DECKS):
        raise ValueError("observation deck IDs must be unique")
    if len({deck.pose.pose_id for deck in OBSERVATION_DECKS}) != len(
        OBSERVATION_DECKS
    ):
        raise ValueError("observation pose IDs must be unique")
    for index, deck in enumerate(OBSERVATION_DECKS):
        if deck.clear_aabb.volume() > 32_768:
            raise ValueError(f"observation deck clear AABB is too large: {deck.deck_id}")
        validate_strict_map_bounds(
            f"observation deck {deck.deck_id}", deck.clear_aabb
        )
        for protected in (
            GALLERY_CLEAR_AABB,
            RENDERER_MATRIX_CLEAR_AABB,
            M0_PROTECTED_CORRIDOR,
        ):
            if deck.clear_aabb.overlaps(protected):
                raise ValueError(
                    f"observation deck {deck.deck_id} overlaps a protected lane"
                )
        if any(deck.clear_aabb.overlaps(other) for other in clear_boxes[:index]):
            raise ValueError("observation deck clear AABBs overlap")
        if any(
            not deck.clear_aabb.contains(position)
            for position in observation_deck_blocks(deck)
        ):
            raise ValueError(f"observation deck escaped clear AABB: {deck.deck_id}")

        foot = deck.pose.foot_block()
        head = Position(foot.x, foot.y + 1, foot.z)
        floor = Position(foot.x, foot.y - 1, foot.z)
        blocks = observation_deck_blocks(deck)
        if blocks.get(floor) != "minecraft:stone_bricks":
            raise ValueError(f"observation pose lacks a fixed floor: {deck.deck_id}")
        if foot in blocks or head in blocks:
            raise ValueError(f"observation pose is obstructed: {deck.deck_id}")
        if not deck.clear_aabb.contains(foot) or not deck.clear_aabb.contains(head):
            raise ValueError(f"observation pose escaped clear AABB: {deck.deck_id}")


def validate_layout(cases: list[dict[str, object]], roster_ids: list[str]) -> None:
    if GALLERY_CLEAR_AABB.volume() > 32_768:
        raise ValueError("gallery clear AABB exceeds Minecraft's default fill volume limit")
    if GALLERY_CLEAR_AABB.overlaps(M0_PROTECTED_CORRIDOR):
        raise ValueError("gallery clear AABB overlaps the protected M0 corridor")
    if GALLERY_CLEAR_AABB.overlaps(M0_FIXTURE_AABB):
        raise ValueError("gallery clear AABB overlaps the exact M0 fixture")
    if not M0_PROTECTED_CORRIDOR.overlaps(M0_FIXTURE_AABB):
        raise ValueError("the protected corridor must contain the exact M0 fixture")
    if not (
        MAP_MASK_MIN < GALLERY_CLEAR_AABB.minimum.x
        and GALLERY_CLEAR_AABB.maximum.x < MAP_MASK_MAX
        and MAP_MASK_MIN < GALLERY_CLEAR_AABB.minimum.z
        and GALLERY_CLEAR_AABB.maximum.z < MAP_MASK_MAX
    ):
        raise ValueError("gallery clear AABB must remain strictly inside the map mask")

    anchors = [position_from_dict(case["anchor"]) for case in cases]
    if len(anchors) != 234 or len(set(anchors)) != 234:
        raise ValueError("gallery must contain 234 unique anchor coordinates")
    if any(not ROSTER_ANCHOR_AABB.contains(position) for position in anchors):
        raise ValueError("gallery anchor escaped the audited roster AABB")
    if [case["block_id"] for case in cases] != roster_ids:
        raise ValueError("gallery cases no longer match the tracked roster ordering")

    generic = {case["block_id"] for case in cases if case["renderer_family"] == "generic_double"}
    primary = {case["block_id"] for case in cases if case["renderer_family"] == "primary_only"}
    if generic != GENERIC_DOUBLE_IDS:
        raise ValueError("gallery generic-double classification drifted")
    if generic & primary or generic | primary != set(roster_ids):
        raise ValueError("every roster ID must be classified exactly once")

    occupied: set[Position] = set(anchors)
    for case in cases:
        for support in case["supports"]:
            position = position_from_dict(support["position"])
            if not GALLERY_CLEAR_AABB.contains(position):
                raise ValueError(f"support escaped clear AABB: {case['case_id']}")
            if position in anchors:
                raise ValueError(f"support collides with an anchor: {case['case_id']}")
            occupied.add(position)
        for companion in case["companion_blocks"]:
            position = position_from_dict(companion["position"])
            if not GALLERY_CLEAR_AABB.contains(position):
                raise ValueError(f"companion escaped clear AABB: {case['case_id']}")
            if position in occupied:
                raise ValueError(f"companion collides with gallery content: {case['case_id']}")
            occupied.add(position)


def manifest_document(
    cases: list[dict[str, object]], double_ids: list[str]
) -> dict[str, object]:
    return {
        "schema_version": 1,
        "target": {
            "framedblocks": TARGET_FRAMEDBLOCKS_VERSION,
            "minecraft": TARGET_MINECRAFT_VERSION,
            "pack_format": 48,
        },
        "source": {
            "tracked_blockstate_manifest": (
                "../src/main/resources/bluemap-framedblocks/profiles/10.6.1/"
                "blockstate-ids.txt"
            ),
            "tracked_blockstate_manifest_sha256": TARGET_BLOCKSTATE_MANIFEST_SHA256,
            "framedblocks_source_commit": (
                "99522893fce0c9cd543194be1e8cefd488e0eec8"
            ),
            "generic_double_rule": (
                "BlockType seventh constructor argument doubleBlock == true"
            ),
            "generic_double_ids_sha256": TARGET_GENERIC_DOUBLE_SHA256,
            "solid_capable_rule": (
                "BlockType first constructor argument canOcclude == true"
            ),
            "solid_capable_ids_sha256": TARGET_SOLID_CAPABLE_SHA256,
        },
        "layout": {
            "dimension": "minecraft:overworld",
            "columns": ROSTER_COLUMNS,
            "rows": ROSTER_ROWS,
            "spacing_blocks": ROSTER_SPACING,
            "configured_map_mask_xz": {
                "min": MAP_MASK_MIN,
                "max": MAP_MASK_MAX,
            },
            "gallery_clear_aabb": GALLERY_CLEAR_AABB.as_dict(),
            "roster_anchor_aabb": ROSTER_ANCHOR_AABB.as_dict(),
            "protected_m0_corridor_aabb": M0_PROTECTED_CORRIDOR.as_dict(),
            "exact_m0_fixture_aabb": M0_FIXTURE_AABB.as_dict(),
            "reserved": (
                "The roster functions touch only gallery_clear_aabb. The separate "
                "renderer-path and observation-deck functions use their own AABBs. "
                "The full x=252..261, z=192..320 M0 corridor remains untouched."
            ),
        },
        "related_generated_contracts": {
            "renderer_path_and_observation_manifest": "renderer-path-matrix.json",
            "renderer_path_review_table": "renderer-path-matrix.tsv",
            "observation_pose_review_table": "observation-poses.tsv",
        },
        "counts": {
            "tracked_blockstates": 236,
            "excluded_saws": 2,
            "roster_cases": len(cases),
            "generic_double_cases": len(double_ids),
            "primary_only_cases": len(cases) - len(double_ids),
            "solid_camo_state_cases": len(SOLID_CAPABLE_IDS),
        },
        "excluded_block_ids": sorted(SAW_IDS),
        "camo_contract": {
            "primary": PRIMARY_CAMO,
            "secondary_for_generic_double_only": SECONDARY_CAMO,
            "framedblocks_disk_codec_type": "framedblocks:block",
        },
        "cases": cases,
    }


def renderer_manifest_document(cases: list[dict[str, object]]) -> dict[str, object]:
    decks = []
    for deck in OBSERVATION_DECKS:
        decks.append(
            {
                "deck_id": deck.deck_id,
                "clear_aabb": deck.clear_aabb.as_dict(),
                "floor_aabb": deck.floor_aabb.as_dict(),
                "expected_block_count": len(observation_deck_blocks(deck)),
                "pose": deck.pose.as_dict(),
                "pose_function": OBSERVATION_POSE_FUNCTIONS[deck.deck_id],
            }
        )

    companion_count = sum(len(case["companion_blocks"]) for case in cases)
    return {
        "schema_version": 1,
        "target": {
            "framedblocks": TARGET_FRAMEDBLOCKS_VERSION,
            "minecraft": TARGET_MINECRAFT_VERSION,
            "pack_format": 48,
        },
        "source": {
            "framedblocks_source_commit": (
                "99522893fce0c9cd543194be1e8cefd488e0eec8"
            ),
            "tracked_blockstate_manifest_sha256": TARGET_BLOCKSTATE_MANIFEST_SHA256,
            "matrix_policy": (
                "Exact add-on support status/family/reason plus exact 10.6.1 "
                "block-state and block-entity fields"
            ),
        },
        "layout": {
            "dimension": "minecraft:overworld",
            "configured_map_mask_xz": {
                "min": MAP_MASK_MIN,
                "max": MAP_MASK_MAX,
            },
            "renderer_matrix_columns": RENDERER_MATRIX_COLUMNS,
            "renderer_matrix_rows": RENDERER_MATRIX_ROWS,
            "renderer_matrix_spacing_blocks": RENDERER_MATRIX_SPACING,
            "renderer_matrix_clear_aabb": RENDERER_MATRIX_CLEAR_AABB.as_dict(),
            "renderer_matrix_anchor_aabb": RENDERER_MATRIX_ANCHOR_AABB.as_dict(),
            "roster_clear_aabb": GALLERY_CLEAR_AABB.as_dict(),
            "protected_m0_corridor_aabb": M0_PROTECTED_CORRIDOR.as_dict(),
            "exact_m0_fixture_aabb": M0_FIXTURE_AABB.as_dict(),
        },
        "functions": {
            "renderer_paths": RENDERER_FUNCTIONS,
            "observation_decks": OBSERVATION_DECK_FUNCTIONS,
            "observation_poses": OBSERVATION_POSE_FUNCTIONS,
        },
        "counts": {
            "renderer_path_cases": len(cases),
            "addon_geometry_cases": sum(
                case["expected_path"] == "addon_geometry" for case in cases
            ),
            "stock_fallback_cases": sum(
                case["expected_path"] == "stock_fallback" for case in cases
            ),
            "resource_extension_unrouted_cases": sum(
                case["routing_stage"] == "resource_extension_unrouted"
                for case in cases
            ),
            "addon_renderer_runtime_fallback_cases": sum(
                case["routing_stage"] == "addon_renderer_runtime_fallback"
                for case in cases
            ),
            "renderer_anchor_blocks": len(cases) + companion_count,
            "unbuilt_camo_corruption_cases": len(UNBUILT_RENDERER_CASES),
            "observation_decks": len(OBSERVATION_DECKS),
            "observation_deck_blocks": sum(
                len(observation_deck_blocks(deck)) for deck in OBSERVATION_DECKS
            ),
        },
        "camo_contract": {
            "supported_primary": "minecraft:stone",
            "supported_secondary": "minecraft:gold_block",
            "non_opaque_fallback": "minecraft:glass",
            "framedblocks_disk_codec_type": "framedblocks:block",
        },
        "observation_decks": decks,
        "unbuilt_cases": list(UNBUILT_RENDERER_CASES),
        "cases": cases,
    }


def render_renderer_cases_tsv(cases: list[dict[str, object]]) -> bytes:
    rows = [
        "case_id\tindex\tcategory\texpected_path\trouting_stage\t"
        "expected_reason\tblock_id\tx\ty\tz\tblock_state\tprimary_camo\t"
        "secondary_camo\tcompanions\tblock_entity_snbt"
    ]
    for case in cases:
        anchor = case["anchor"]
        rows.append(
            "\t".join(
                (
                    case["case_id"],
                    str(case["index"]),
                    case["category"],
                    case["expected_path"],
                    case["routing_stage"],
                    case["expected_reason"],
                    case["block_id"],
                    str(anchor["x"]),
                    str(anchor["y"]),
                    str(anchor["z"]),
                    case["block_state"],
                    case["primary_camo"],
                    case["secondary_camo"] or "",
                    "+".join(
                        companion["kind"]
                        for companion in case["companion_blocks"]
                    )
                    or "none",
                    case["block_entity_snbt"],
                )
            )
        )
    return canonical_lines(rows)


def render_observation_poses_tsv() -> bytes:
    rows = [
        "deck_id\tpose_id\tx\ty\tz\tyaw\tpitch\tpose_function\t"
        "clear_min\tclear_max\tfloor_min\tfloor_max"
    ]
    for deck in OBSERVATION_DECKS:
        pose = deck.pose
        rows.append(
            "\t".join(
                (
                    deck.deck_id,
                    pose.pose_id,
                    f"{pose.x:g}",
                    f"{pose.y:g}",
                    f"{pose.z:g}",
                    f"{pose.yaw:g}",
                    f"{pose.pitch:g}",
                    OBSERVATION_POSE_FUNCTIONS[deck.deck_id],
                    deck.clear_aabb.minimum.command(),
                    deck.clear_aabb.maximum.command(),
                    deck.floor_aabb.minimum.command(),
                    deck.floor_aabb.maximum.command(),
                )
            )
        )
    return canonical_lines(rows)


def render_cases_tsv(cases: list[dict[str, object]]) -> bytes:
    rows = [
        "case_id\tindex\tblock_id\trenderer_family\tx\ty\tz\tblock_state\t"
        "primary_camo\tsecondary_camo\tsolid_camo_state\tsupport_profile\t"
        "companion_profile"
    ]
    for case in cases:
        anchor = case["anchor"]
        supports = "+".join(support["kind"] for support in case["supports"])
        companions = "+".join(
            companion["kind"] for companion in case["companion_blocks"]
        ) or "none"
        rows.append(
            "\t".join(
                (
                    case["case_id"],
                    str(case["index"]),
                    case["block_id"],
                    case["renderer_family"],
                    str(anchor["x"]),
                    str(anchor["y"]),
                    str(anchor["z"]),
                    case["block_state"],
                    case["primary_camo"],
                    case["secondary_camo"] or "",
                    str(case["solid_camo_state"]).lower(),
                    supports,
                    companions,
                )
            )
        )
    return canonical_lines(rows)


def render_clear_function() -> bytes:
    lines = [
        "# SPDX-License-Identifier: LGPL-3.0-only",
        "# FramedBlocks 10.6.1 roster gallery: bounded, idempotent cleanup.",
        "# This AABB is disjoint from the protected M0 corridor.",
        (
            f"fill {GALLERY_CLEAR_AABB.minimum.command()} "
            f"{GALLERY_CLEAR_AABB.maximum.command()} minecraft:air replace"
        ),
        (
            'tellraw @a [{"text":"Cleared the isolated FramedBlocks 10.6.1 '
            'roster gallery.","color":"yellow"}]'
        ),
    ]
    return canonical_lines(lines)


def render_build_function(cases: list[dict[str, object]]) -> bytes:
    lines = [
        "# SPDX-License-Identifier: LGPL-3.0-only",
        "# FramedBlocks 10.6.1: deterministic 234-case default-state roster.",
        "# Rebuilding is idempotent because the bounded roster AABB is cleared first.",
        "function framedblocks_gallery:clear",
    ]
    for case in cases:
        anchor = position_from_dict(case["anchor"])
        generic_double = case["renderer_family"] == "generic_double"
        lines.extend(("", f"# {case['case_id']} {case['block_id']}"))
        for support in case["supports"]:
            position = position_from_dict(support["position"])
            lines.append(f"setblock {position.command()} minecraft:stone replace")
        lines.append(f"setblock {anchor.command()} {case['block_state']} replace")
        lines.append(f"data merge block {anchor.command()} {camo_snbt(generic_double)}")
        for companion in case["companion_blocks"]:
            position = position_from_dict(companion["position"])
            lines.append(
                f"setblock {position.command()} {companion['block_state']} replace"
            )
            lines.append(f"data merge block {position.command()} {camo_snbt(False)}")
    lines.extend(
        (
            "",
            (
                'tellraw @a [{"text":"Built the isolated FramedBlocks 10.6.1 '
                'roster gallery (234 anchors).","color":"aqua"}]'
            ),
            "function framedblocks_gallery:verify",
        )
    )
    return canonical_lines(lines)


def render_verify_function(cases: list[dict[str, object]]) -> bytes:
    lines = [
        "# SPDX-License-Identifier: LGPL-3.0-only",
        "# Structural verification only; this does not validate client or BlueMap pixels.",
        "scoreboard objectives add fbgv1061 dummy",
        "scoreboard players set #failures fbgv1061 0",
    ]
    for case in cases:
        anchor = position_from_dict(case["anchor"])
        generic_double = case["renderer_family"] == "generic_double"
        lines.extend(("", f"# {case['case_id']} {case['block_id']}"))
        lines.append(
            f"execute unless block {anchor.command()} {case['block_state']} "
            "run scoreboard players add #failures fbgv1061 1"
        )
        lines.append(
            f"execute unless data block {anchor.command()} "
            f"{{{PRIMARY_CAMO_SNBT}}} "
            "run scoreboard players add #failures fbgv1061 1"
        )
        if generic_double:
            lines.append(
                f"execute unless data block {anchor.command()} "
                f"{{{SECONDARY_CAMO_SNBT}}} "
                "run scoreboard players add #failures fbgv1061 1"
            )
        for support in case["supports"]:
            position = position_from_dict(support["position"])
            lines.append(
                f"execute unless block {position.command()} minecraft:stone "
                "run scoreboard players add #failures fbgv1061 1"
            )
        for companion in case["companion_blocks"]:
            position = position_from_dict(companion["position"])
            lines.append(
                f"execute unless block {position.command()} {companion['block_state']} "
                "run scoreboard players add #failures fbgv1061 1"
            )
            lines.append(
                f"execute unless data block {position.command()} "
                f"{{{PRIMARY_CAMO_SNBT}}} "
                "run scoreboard players add #failures fbgv1061 1"
            )
    lines.extend(
        (
            "",
            (
                "execute if score #failures fbgv1061 matches 0 run tellraw @a "
                '[{"text":"FramedBlocks gallery verification passed: 234/234 '
                'anchors plus camos and supports.","color":"green"}]'
            ),
            (
                "execute unless score #failures fbgv1061 matches 0 run tellraw @a "
                '[{"text":"FramedBlocks gallery verification failed with ",'
                '"color":"red"},{"score":{"name":"#failures",'
                '"objective":"fbgv1061"}},{"text":" mismatches.","color":"red"}]'
            ),
        )
    )
    return canonical_lines(lines)


def render_renderer_clear_function() -> bytes:
    lines = [
        "# SPDX-License-Identifier: LGPL-3.0-only",
        "# FramedBlocks 10.6.1 renderer-path matrix: bounded cleanup.",
        (
            f"fill {RENDERER_MATRIX_CLEAR_AABB.minimum.command()} "
            f"{RENDERER_MATRIX_CLEAR_AABB.maximum.command()} minecraft:air replace"
        ),
        (
            'tellraw @a [{"text":"Cleared the FramedBlocks renderer-path '
            'matrix.","color":"yellow"}]'
        ),
    ]
    return canonical_lines(lines)


def render_renderer_build_function(cases: list[dict[str, object]]) -> bytes:
    anchor_count = len(cases) + sum(
        len(case["companion_blocks"]) for case in cases
    )
    lines = [
        "# SPDX-License-Identifier: LGPL-3.0-only",
        (
            f"# Exact 10.6.1 renderer-path matrix: {len(cases)} cases and "
            f"{anchor_count} framed anchors."
        ),
        "# Rebuilding is idempotent because its disjoint AABB is cleared first.",
        f"function {RENDERER_FUNCTIONS['clear']}",
    ]
    for case in cases:
        anchor = position_from_dict(case["anchor"])
        lines.extend(("", f"# {case['case_id']} {case['category']}"))
        for support in case["supports"]:
            position = position_from_dict(support["position"])
            lines.append(
                f"setblock {position.command()} {support['block_id']} replace"
            )
        lines.append(f"setblock {anchor.command()} {case['block_state']} replace")
        lines.append(
            f"data merge block {anchor.command()} {case['block_entity_snbt']}"
        )
        for companion in case["companion_blocks"]:
            for support in companion["supports"]:
                position = position_from_dict(support["position"])
                lines.append(
                    f"setblock {position.command()} {support['block_id']} replace"
                )
            position = position_from_dict(companion["position"])
            lines.append(
                f"setblock {position.command()} {companion['block_state']} replace"
            )
            lines.append(
                f"data merge block {position.command()} "
                f"{companion['block_entity_snbt']}"
            )
    lines.extend(
        (
            "",
            (
                'tellraw @a [{"text":"Built the FramedBlocks renderer-path '
                f'matrix ({len(cases)} cases, {anchor_count} framed anchors).",'
                '"color":"aqua"}]'
            ),
            f"function {RENDERER_FUNCTIONS['verify']}",
        )
    )
    return canonical_lines(lines)


def render_renderer_verify_function(cases: list[dict[str, object]]) -> bytes:
    anchor_count = len(cases) + sum(
        len(case["companion_blocks"]) for case in cases
    )
    lines = [
        "# SPDX-License-Identifier: LGPL-3.0-only",
        "# Structural path-fixture verification; this does not validate rendered pixels.",
        "scoreboard objectives add fbgr1061 dummy",
        "scoreboard players set #failures fbgr1061 0",
    ]
    for case in cases:
        anchor = position_from_dict(case["anchor"])
        lines.extend(("", f"# {case['case_id']} {case['category']}"))
        lines.append(
            f"execute unless block {anchor.command()} {case['block_state']} "
            "run scoreboard players add #failures fbgr1061 1"
        )
        lines.append(
            f"execute unless data block {anchor.command()} "
            f"{case['block_entity_snbt']} "
            "run scoreboard players add #failures fbgr1061 1"
        )
        for support in case["supports"]:
            position = position_from_dict(support["position"])
            lines.append(
                f"execute unless block {position.command()} {support['block_id']} "
                "run scoreboard players add #failures fbgr1061 1"
            )
        for companion in case["companion_blocks"]:
            position = position_from_dict(companion["position"])
            lines.append(
                f"execute unless block {position.command()} "
                f"{companion['block_state']} "
                "run scoreboard players add #failures fbgr1061 1"
            )
            lines.append(
                f"execute unless data block {position.command()} "
                f"{companion['block_entity_snbt']} "
                "run scoreboard players add #failures fbgr1061 1"
            )
            for support in companion["supports"]:
                support_position = position_from_dict(support["position"])
                lines.append(
                    f"execute unless block {support_position.command()} "
                    f"{support['block_id']} "
                    "run scoreboard players add #failures fbgr1061 1"
                )
    lines.extend(
        (
            "",
            (
                "execute if score #failures fbgr1061 matches 0 run tellraw @a "
                f'[{{"text":"Renderer-path matrix verification passed: '
                f'{len(cases)}/{len(cases)} cases and {anchor_count} framed anchors.",'
                '"color":"green"}]'
            ),
            (
                "execute unless score #failures fbgr1061 matches 0 run tellraw @a "
                '[{"text":"Renderer-path matrix verification failed with ",'
                '"color":"red"},{"score":{"name":"#failures",'
                '"objective":"fbgr1061"}},{"text":" mismatches.",'
                '"color":"red"}]'
            ),
        )
    )
    return canonical_lines(lines)


def render_observation_clear_function() -> bytes:
    lines = [
        "# SPDX-License-Identifier: LGPL-3.0-only",
        "# South and east observation decks: bounded cleanup.",
    ]
    for deck in OBSERVATION_DECKS:
        lines.extend(
            (
                f"# {deck.deck_id}",
                (
                    f"fill {deck.clear_aabb.minimum.command()} "
                    f"{deck.clear_aabb.maximum.command()} minecraft:air replace"
                ),
            )
        )
    lines.append(
        'tellraw @a [{"text":"Cleared the FramedBlocks observation decks.",'
        '"color":"yellow"}]'
    )
    return canonical_lines(lines)


def render_observation_build_function() -> bytes:
    lines = [
        "# SPDX-License-Identifier: LGPL-3.0-only",
        "# Fixed south and east observation decks for the isolated staging gallery.",
        f"function {OBSERVATION_DECK_FUNCTIONS['clear']}",
    ]
    for deck in OBSERVATION_DECKS:
        floor = deck.floor_aabb
        railing_y = floor.minimum.y + 1
        lines.extend(
            (
                "",
                f"# {deck.deck_id}",
                (
                    f"fill {floor.minimum.command()} {floor.maximum.command()} "
                    "minecraft:stone_bricks replace"
                ),
                (
                    f"fill {floor.minimum.x} {railing_y} {floor.minimum.z} "
                    f"{floor.maximum.x} {railing_y} {floor.minimum.z} "
                    "minecraft:iron_bars replace"
                ),
                (
                    f"fill {floor.minimum.x} {railing_y} {floor.maximum.z} "
                    f"{floor.maximum.x} {railing_y} {floor.maximum.z} "
                    "minecraft:iron_bars replace"
                ),
                (
                    f"fill {floor.minimum.x} {railing_y} {floor.minimum.z + 1} "
                    f"{floor.minimum.x} {railing_y} {floor.maximum.z - 1} "
                    "minecraft:iron_bars replace"
                ),
                (
                    f"fill {floor.maximum.x} {railing_y} {floor.minimum.z + 1} "
                    f"{floor.maximum.x} {railing_y} {floor.maximum.z - 1} "
                    "minecraft:iron_bars replace"
                ),
            )
        )
    lines.extend(
        (
            "",
            (
                'tellraw @a [{"text":"Built the fixed south and east '
                'FramedBlocks observation decks.","color":"aqua"}]'
            ),
            f"function {OBSERVATION_DECK_FUNCTIONS['verify']}",
        )
    )
    return canonical_lines(lines)


def render_observation_verify_function() -> bytes:
    expected_count = sum(
        len(observation_deck_blocks(deck)) for deck in OBSERVATION_DECKS
    )
    lines = [
        "# SPDX-License-Identifier: LGPL-3.0-only",
        "# Structural verification of both fixed observation decks and pose volumes.",
        "scoreboard objectives add fbgd1061 dummy",
        "scoreboard players set #failures fbgd1061 0",
    ]
    for deck in OBSERVATION_DECKS:
        lines.extend(("", f"# {deck.deck_id}"))
        for position, block_id in sorted(observation_deck_blocks(deck).items()):
            lines.append(
                f"execute unless block {position.command()} {block_id} "
                "run scoreboard players add #failures fbgd1061 1"
            )
        foot = deck.pose.foot_block()
        head = Position(foot.x, foot.y + 1, foot.z)
        lines.append(
            f"execute unless block {foot.command()} minecraft:air "
            "run scoreboard players add #failures fbgd1061 1"
        )
        lines.append(
            f"execute unless block {head.command()} minecraft:air "
            "run scoreboard players add #failures fbgd1061 1"
        )
    lines.extend(
        (
            "",
            (
                "execute if score #failures fbgd1061 matches 0 run tellraw @a "
                f'['
                f'{{"text":"Observation deck verification passed: '
                f'{expected_count} fixed blocks and two clear pose volumes.",'
                f'"color":"green"}}]'
            ),
            (
                "execute unless score #failures fbgd1061 matches 0 run tellraw @a "
                '[{"text":"Observation deck verification failed with ",'
                '"color":"red"},{"score":{"name":"#failures",'
                '"objective":"fbgd1061"}},{"text":" mismatches.",'
                '"color":"red"}]'
            ),
        )
    )
    return canonical_lines(lines)


def render_observation_pose_function(deck: ObservationDeck) -> bytes:
    foot = deck.pose.foot_block()
    head = Position(foot.x, foot.y + 1, foot.z)
    floor = Position(foot.x, foot.y - 1, foot.z)
    safe_conditions = (
        f"if block {floor.command()} minecraft:stone_bricks "
        f"if block {foot.command()} minecraft:air "
        f"if block {head.command()} minecraft:air"
    )
    lines = [
        "# SPDX-License-Identifier: LGPL-3.0-only",
        f"# Fixed {deck.deck_id} observation pose; no teleport if the deck is unsafe.",
        f"execute {safe_conditions} run {deck.pose.teleport_command()}",
        (
            f"execute unless block {floor.command()} minecraft:stone_bricks "
            'run tellraw @s [{"text":"Observation pose refused: deck floor is '
            'missing.","color":"red"}]'
        ),
        (
            f"execute unless block {foot.command()} minecraft:air "
            'run tellraw @s [{"text":"Observation pose refused: foot space is '
            'blocked.","color":"red"}]'
        ),
        (
            f"execute unless block {head.command()} minecraft:air "
            'run tellraw @s [{"text":"Observation pose refused: head space is '
            'blocked.","color":"red"}]'
        ),
    ]
    return canonical_lines(lines)


def validate_commands(outputs: dict[Path, bytes]) -> None:
    allowed_top_level = {
        "data",
        "execute",
        "fill",
        "function",
        "scoreboard",
        "setblock",
        "tellraw",
    }
    allowed_execute_targets = {"scoreboard", "tellraw", "teleport"}
    pose_path_commands = {
        Path(
            "datapack/data/framedblocks_gallery/function/"
            f"pose_{deck.deck_id}.mcfunction"
        ): deck.pose.teleport_command()
        for deck in OBSERVATION_DECKS
    }
    for path, raw in outputs.items():
        if path.suffix != ".mcfunction":
            continue
        text = raw.decode("utf-8")
        if "\r" in text:
            raise ValueError(f"non-LF line ending in {path}")
        for number, line in enumerate(text.splitlines(), start=1):
            if not line or line.startswith("#"):
                continue
            if "~" in line or "^" in line or ";" in line:
                raise ValueError(f"non-absolute or chained command in {path}:{number}")
            command = line.split(maxsplit=1)[0]
            if command not in allowed_top_level:
                raise ValueError(f"unsafe command {command!r} in {path}:{number}")
            if command == "execute":
                if " in " in line or " run " not in line:
                    raise ValueError(f"unsafe execute form in {path}:{number}")
                target = line.rsplit(" run ", maxsplit=1)[1].split(maxsplit=1)[0]
                if target not in allowed_execute_targets:
                    raise ValueError(f"unsafe execute target in {path}:{number}")
                if target == "teleport" and (
                    path not in pose_path_commands
                    or line.rsplit(" run ", maxsplit=1)[1]
                    != pose_path_commands[path]
                ):
                    raise ValueError(f"unexpected teleport in {path}:{number}")
            if "@" in line and not any(
                selector in line
                for selector in ("tellraw @a", "tellraw @s", "teleport @s")
            ):
                raise ValueError(f"unexpected entity selector in {path}:{number}")
            if "tellraw @s" in line and path not in pose_path_commands:
                raise ValueError(f"unexpected executor message in {path}:{number}")
            if len(line.encode("utf-8")) > 32_000:
                raise ValueError(f"oversized command in {path}:{number}")


def generated_outputs() -> dict[Path, bytes]:
    roster_ids, double_ids = read_roster_ids()
    cases = build_cases(roster_ids)
    validate_layout(cases, roster_ids)
    renderer_cases = build_renderer_cases(roster_ids)
    validate_renderer_layout(renderer_cases)
    validate_observation_decks()

    manifest = manifest_document(cases, double_ids)
    renderer_manifest = renderer_manifest_document(renderer_cases)
    outputs = {
        Path("cases.json"): (
            json.dumps(manifest, indent=2, sort_keys=True) + "\n"
        ).encode("utf-8"),
        Path("cases.tsv"): render_cases_tsv(cases),
        Path("renderer-path-matrix.json"): (
            json.dumps(renderer_manifest, indent=2, sort_keys=True) + "\n"
        ).encode("utf-8"),
        Path("renderer-path-matrix.tsv"): render_renderer_cases_tsv(
            renderer_cases
        ),
        Path("observation-poses.tsv"): render_observation_poses_tsv(),
        Path("generic-double-ids.txt"): canonical_lines(double_ids),
        Path("datapack/pack.mcmeta"): (
            json.dumps(
                {
                    "pack": {
                        "description": (
                            "Isolated FramedBlocks 10.6.1 BlueMap roster gallery"
                        ),
                        "pack_format": 48,
                    }
                },
                indent=2,
                sort_keys=True,
            )
            + "\n"
        ).encode("utf-8"),
        Path("datapack/data/framedblocks_gallery/function/build.mcfunction"): (
            render_build_function(cases)
        ),
        Path("datapack/data/framedblocks_gallery/function/clear.mcfunction"): (
            render_clear_function()
        ),
        Path("datapack/data/framedblocks_gallery/function/verify.mcfunction"): (
            render_verify_function(cases)
        ),
        Path(
            "datapack/data/framedblocks_gallery/function/"
            "build_renderer_paths.mcfunction"
        ): render_renderer_build_function(renderer_cases),
        Path(
            "datapack/data/framedblocks_gallery/function/"
            "verify_renderer_paths.mcfunction"
        ): render_renderer_verify_function(renderer_cases),
        Path(
            "datapack/data/framedblocks_gallery/function/"
            "clear_renderer_paths.mcfunction"
        ): render_renderer_clear_function(),
        Path(
            "datapack/data/framedblocks_gallery/function/"
            "build_observation_decks.mcfunction"
        ): render_observation_build_function(),
        Path(
            "datapack/data/framedblocks_gallery/function/"
            "verify_observation_decks.mcfunction"
        ): render_observation_verify_function(),
        Path(
            "datapack/data/framedblocks_gallery/function/"
            "clear_observation_decks.mcfunction"
        ): render_observation_clear_function(),
    }
    for deck in OBSERVATION_DECKS:
        outputs[
            Path(
                "datapack/data/framedblocks_gallery/function/"
                f"pose_{deck.deck_id}.mcfunction"
            )
        ] = render_observation_pose_function(deck)
    validate_commands(outputs)

    checksum_rows = [
        f"{sha256_bytes(raw)}  {path.as_posix()}" for path, raw in sorted(outputs.items())
    ]
    outputs[Path("SHA256SUMS")] = canonical_lines(checksum_rows)
    return outputs


def write_outputs(outputs: dict[Path, bytes]) -> None:
    for relative, raw in sorted(outputs.items()):
        destination = GALLERY_ROOT / relative
        destination.parent.mkdir(parents=True, exist_ok=True)
        if destination.is_symlink():
            raise ValueError(f"refusing to replace symlink: {destination}")
        temporary = destination.with_name(destination.name + ".tmp")
        temporary.write_bytes(raw)
        os.replace(temporary, destination)


def check_outputs(outputs: dict[Path, bytes]) -> None:
    mismatches = []
    for relative, expected in sorted(outputs.items()):
        destination = GALLERY_ROOT / relative
        if not destination.is_file() or destination.read_bytes() != expected:
            mismatches.append(relative.as_posix())
    if mismatches:
        raise ValueError("generated gallery drift: " + ", ".join(mismatches))


def print_hashes(outputs: dict[Path, bytes]) -> None:
    for relative, raw in sorted(outputs.items()):
        print(f"{sha256_bytes(raw)}  gallery/{relative.as_posix()}")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--check",
        action="store_true",
        help="Validate committed generated files without rewriting them.",
    )
    args = parser.parse_args()

    outputs = generated_outputs()
    if args.check:
        check_outputs(outputs)
        print("FramedBlocks 10.6.1 gallery is reproducible and current.")
    else:
        write_outputs(outputs)
        print("Generated FramedBlocks 10.6.1 gallery.")
    print_hashes(outputs)
    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except (OSError, ValueError) as error:
        print(f"gallery generation failed: {error}", file=sys.stderr)
        sys.exit(1)

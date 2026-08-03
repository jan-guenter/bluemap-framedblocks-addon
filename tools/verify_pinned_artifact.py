#!/usr/bin/env python3
# SPDX-License-Identifier: LGPL-3.0-only
"""Verify the exact FramedBlocks 10.6.1 artifact and tracked inventories."""

from __future__ import annotations

import argparse
import hashlib
from pathlib import Path
import re
import sys
import zipfile


EXPECTED_SIZE = 4_306_703
EXPECTED_SHA1 = "3007be0007d09c0225ca33b647461f342eac0503"
EXPECTED_SHA256 = "3337f29e1fa3331e8740eef9c20b0750d81fd86d1057fb81012a5c4792aa3369"
EXPECTED_SHA512 = (
    "051d07cb372bfafd746ff658ef2ad3485606328e445099c17b499b14df6844a0"
    "c75e8c6decaff1267934fbd6dfaa0eb1938906b0dbeaa20500c1789b4175e3b6"
)
EXPECTED_BLOCKSTATE_COUNT = 236
EXPECTED_BLOCKSTATE_IDS_SHA256 = (
    "e4aed367abf2f037d92496e5028fc9493ae7fb48c5e8dd6ffb85eeddb13330c9"
)
EXPECTED_BLOCKSTATE_RESOURCES_SHA256 = (
    "4712e9775a6d9fc0544f369b44f724f4a8377d6ecf8f23c5e2b30d313ec44f84"
)
EXPECTED_BLOCK_ENTITY_COUNT = 51
EXPECTED_BLOCK_ENTITY_IDS_SHA256 = (
    "faef8938e780a6997f59978221ff6fcb52de1e08d01d5d0a4b1c493eb4b0455b"
)
BLOCKSTATE_PREFIX = "assets/framedblocks/blockstates/"
BLOCKSTATE_SUFFIX = ".json"
BLOCKSTATE_ENTRY = re.compile(
    r"assets/framedblocks/blockstates/([a-z0-9_]+)\.json\Z"
)


def digest(path: Path, algorithm: str) -> str:
    value = hashlib.new(algorithm)
    with path.open("rb") as source:
        for chunk in iter(lambda: source.read(64 * 1024), b""):
            value.update(chunk)
    return value.hexdigest()


def canonical_lines(values: list[str]) -> bytes:
    return ("\n".join(values) + "\n").encode("utf-8")


def verify_hash(label: str, actual: str, expected: str) -> None:
    if actual != expected:
        raise ValueError(f"{label}: got {actual}, expected {expected}")


def inventory(jar: Path) -> tuple[bytes, bytes]:
    with zipfile.ZipFile(jar) as archive:
        all_names = [entry.filename for entry in archive.infolist()]
        if len(all_names) != len(set(all_names)):
            raise ValueError("artifact contains duplicate ZIP entry names")

        entries = sorted(
            name
            for name in all_names
            if BLOCKSTATE_ENTRY.fullmatch(name)
        )
        ids = [
            "framedblocks:" + BLOCKSTATE_ENTRY.fullmatch(name).group(1)
            for name in entries
        ]
        if len(ids) != len(set(ids)):
            raise ValueError("artifact contains duplicate blockstate IDs")
        ids.sort(key=str.encode)
        resource_rows = [
            f"{hashlib.sha256(archive.read(name)).hexdigest()}  {name}"
            for name in entries
        ]
    return canonical_lines(ids), canonical_lines(resource_rows)


def verify_manifest(
    path: Path,
    generated: bytes,
    expected_count: int,
    expected_sha256: str,
) -> None:
    tracked = path.read_bytes()
    if tracked != generated:
        raise ValueError(f"{path} does not match the exact artifact inventory")
    lines = tracked.decode("utf-8").splitlines()
    if len(lines) != expected_count:
        raise ValueError(f"{path}: got {len(lines)} lines, expected {expected_count}")
    verify_hash(
        f"{path} SHA-256",
        hashlib.sha256(tracked).hexdigest(),
        expected_sha256,
    )


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--jar", required=True, type=Path)
    parser.add_argument(
        "--refresh-blockstates",
        action="store_true",
        help="Regenerate the tracked blockstate ID manifest after all artifact checks pass.",
    )
    args = parser.parse_args()

    project = Path(__file__).resolve().parents[1]
    blockstates_path = project / (
        "src/main/resources/bluemap-framedblocks/profiles/10.6.1/blockstate-ids.txt"
    )
    block_entities_path = project / (
        "src/main/resources/bluemap-framedblocks/profiles/10.6.1/block-entity-ids.txt"
    )

    if not args.jar.is_file():
        raise ValueError(f"artifact is not a regular file: {args.jar}")
    if args.jar.stat().st_size != EXPECTED_SIZE:
        raise ValueError(
            f"artifact size: got {args.jar.stat().st_size}, expected {EXPECTED_SIZE}"
        )
    verify_hash("artifact SHA-1", digest(args.jar, "sha1"), EXPECTED_SHA1)
    verify_hash("artifact SHA-256", digest(args.jar, "sha256"), EXPECTED_SHA256)
    verify_hash("artifact SHA-512", digest(args.jar, "sha512"), EXPECTED_SHA512)

    blockstates, resource_rows = inventory(args.jar)
    if args.refresh_blockstates:
        blockstates_path.parent.mkdir(parents=True, exist_ok=True)
        blockstates_path.write_bytes(blockstates)

    verify_manifest(
        blockstates_path,
        blockstates,
        EXPECTED_BLOCKSTATE_COUNT,
        EXPECTED_BLOCKSTATE_IDS_SHA256,
    )
    verify_hash(
        "blockstate resource inventory SHA-256",
        hashlib.sha256(resource_rows).hexdigest(),
        EXPECTED_BLOCKSTATE_RESOURCES_SHA256,
    )

    block_entities = block_entities_path.read_bytes()
    block_entity_lines = block_entities.decode("utf-8").splitlines()
    if len(block_entity_lines) != EXPECTED_BLOCK_ENTITY_COUNT:
        raise ValueError(
            f"{block_entities_path}: got {len(block_entity_lines)} lines, "
            f"expected {EXPECTED_BLOCK_ENTITY_COUNT}"
        )
    if "framedblocks:powered_framing_saw" in block_entity_lines:
        raise ValueError("powered_framing_saw must not enter the framed decoder allowlist")
    verify_hash(
        "block-entity ID inventory SHA-256",
        hashlib.sha256(block_entities).hexdigest(),
        EXPECTED_BLOCK_ENTITY_IDS_SHA256,
    )

    print(
        "Verified FramedBlocks 10.6.1: exact artifact, 236 blockstates, "
        "51 framed block-entity IDs."
    )
    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except (OSError, ValueError, zipfile.BadZipFile) as error:
        print(f"verification failed: {error}", file=sys.stderr)
        sys.exit(1)

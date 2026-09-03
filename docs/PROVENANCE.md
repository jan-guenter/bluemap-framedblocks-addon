# Provenance

The implementation uses the adapted-source lane and the repository is
`LGPL-3.0-only`.

The reduced block-entity DTO and bounded camouflage decoder were
modified/adapted for BlueMap interoperability on 2026-08-02. The projected
geometry profile was prepared on 2026-08-03. On 2026-09-01, the adapter added
bounded client-dynamic family renderers. On 2026-09-02, four repeated BlueMap
integration primitives moved to the exact source-compiled Adapter API pin at
commit `e81f08bc4bfbf02d810ec8949a019130e2e61634`, source tree
`2f974c9bb2ba13888d69682f86f30f58922d30eb`. The corresponding local files,
exact upstream inputs, and nature of each modification are listed in
`provenance/upstreams.json` and in the packaged `NOTICE.md`.

The current decoder behavior is based on these exact/release-correlated inputs:

- FramedBlocks runtime `10.6.1`, SHA-1
  `3007be0007d09c0225ca33b647461f342eac0503`, SHA-256
  `3337f29e1fa3331e8740eef9c20b0750d81fd86d1057fb81012a5c4792aa3369`;
- FramedBlocks commit `99522893fce0c9cd543194be1e8cefd488e0eec8`,
  especially its exact `FBContent`, `BlockType`, block-entity, camouflage and
  factory blobs recorded in the machine-readable manifest;
- BlueMap upstream 5.23 commit
  `4c4cbc291b361ceff6ee239448e9f988f9019dbb` and exact Java 21 feature-
  backport build `5.22-feature.backport-5.23-stateless-java-web-server-46`,
  branch `feature/backport-5.23-stateless-java-web-server`, commit
  `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac`, and API commit
  `285c9a60eff3ac2b0cab308ce1058d1565be0971`;
- BlueMap Add-on Adapter API `0.1.0-alpha.2`, commit
  `e81f08bc4bfbf02d810ec8949a019130e2e61634`, source tree
  `2f974c9bb2ba13888d69682f86f30f58922d30eb`, MIT licensed and compiled from
  source into the consumer JAR without a nested module JAR.

The current pack baseline is the exact All the Mons 1.2.0 export with SHA-256
`1d37df201daddecf5454115f5205cca15ca6ab84ed102bcc2e312f4c14876e5d`,
pack commit `c7bb230f21d14d26859d0b92548f089b3a493ad9`, and NeoForge 21.1.248.
It contains the same exact FramedBlocks 10.6.1 artifact and Minecraft 1.21.1
client resources used by the existing profile. Every adapter-facing source
blob recorded in the manifest remains byte-identical at the exact 5.23
feature-backport commit.

The tracked schema-v3 projection deliberately retains its immutable 1.1.1 /
NeoForge 21.1.234 schema-v2 capture fingerprint. Retargeting the host does not
rewrite that header or pretend a new physical capture occurred. The 1.2.0
server-side resource stack still requires deployment control and visual
acceptance, consistent with the existing no-live-resource-attestation
limitation. The `0.1.0-alpha.3` combined integration gallery passed that visual
review on 2026-09-01. The `0.1.0-alpha.5` structure-audit candidate passed its
combined structure rerender and owner visual review on 2026-09-04. The
fingerprint does not attest future resource stacks.

The accepted `0.1.0-alpha.1` artifact (1,326,858 bytes, SHA-256
`4a88fd3a78acf4bcc24ac173db5b9db9efc56e057eec31eeded6432b3bd695c5`)
and its All the Mons 1.1.1 / NeoForge 21.1.234 / BlueMap backport
`5.22-agent.backport-5.22-mc1.21.1-1` row are retained in
`provenance/upstreams.json` as historical accepted evidence. They do not
broaden the current runtime gate.

The camouflage material proof was additionally validated against the exact
Minecraft 1.21.1 client-resource JAR downloaded by BlueMap in the isolated
full-pack lab: 26,836,906 bytes, SHA-256
`499f6897d1837516680f3114072d8106e11c9adcd933fe5cf051b551089b0c99`.
The exact evidence used from that JAR is:

- `assets/minecraft/blockstates/stone.json`, SHA-256
  `34476f2bb98e7abec029c07c3cbfd8ba3a0141c0df3f02ff255018dbec625bbc`;
- `assets/minecraft/models/block/stone.json`, SHA-256
  `c0734082e7f1e2f44809737b2fbe1136fc7304bde7232e45fbe921884a4a327e`;
- `assets/minecraft/models/block/stone_mirrored.json`, SHA-256
  `f6127c3048b1812b664209825271553c71c2b2dae9d6a19933702b6fdbe34370`.

The blockstate contains four weighted default alternatives: normal, mirrored,
and both at a 180-degree Y rotation. Every alternative resolves to one
identical material, so it enters the normalized weighted fast path. Differing
bounded alternatives instead use BlueMap's coordinate-stable selector. The
normalization establishes rendered geometry and material, not pixel-identical
randomized texture orientation. The Minecraft JAR and its assets are evidence
only and are not bundled or redistributed.

The structure audit also established two exact optional-camouflage inputs:

- Crystalix `3.0.0`, 817,004 bytes, SHA-256
  `42f97cf776cff8261bf671e64a333bbec65a8bf28e519d39cd958e0af9848e6c`;
  its custom `crystalix:crystalix_glass` camo stores the 24-bit colour beside
  the exact glass blockstate in FramedBlocks block-entity data;
- Dyenamics and Friends `1.21.1-2.2.2`, 8,361,784 bytes, SHA-256
  `c9797951ec4773d885cad8e15944374d9e33a43102cfafdb883a71d142a3510f`.
  Its NeoForge client hook exposes resources below `compat_packs/`, which
  BlueMap's resource-root scan does not execute. The adapter therefore reads
  only the exact 90-file, 58,086-byte Luminax full-block closure (canonical
  SHA-256 `ed4180e18caa2f31d62453dee79e36197eb5dcf826788a5d3a4365aa8c86190c`)
  and the exact 864-byte Productive Metalworks honey-fire-bricks PNG (SHA-256
  `9880929dba2bae8430658e8cf968b312f5a9dc5a44aad340a1d43d39dc00312d`).

Both artifacts are runtime evidence only. No Crystalix or Dyenamics and
Friends class, binary, JSON, model, texture, or encoded derivative is bundled.

## Physical-capture evidence and production source form

The neutral geometry evidence was captured twice from the exact modded client.
Both schema-v2 gzip files were byte-identical. These private, untracked
captures are pinned as follows:

- gzip: 1,300,572 bytes, SHA-256
  `390e7edb3e4c0bd6dbaefa90bebfee2918306cae876900d90d94fa2cbecd8234`;
- uncompressed JSON: 59,147,042 bytes, SHA-256
  `f261fb5a6c6189ebb5795facb2dc4cd579724a45422b29aba9ef4898f40509a7`;
- inventory: 5,382 templates and 62,746 quads.

The repository and production JAR do not contain those evidence captures. The
tracked schema-v3 gzip is canonical UTF-8 JSON under deterministic compression
and is the preferred form for editing or otherwise modifying the geometry that
is actually shipped. It preserves all raw-state, template, and alias
identities. Geometry is emptied for the 524 templates belonging to 28 client-
dynamic family IDs, removing 4,038 captured quads and leaving 5,382 templates
with 58,708 quads. Those families now use bounded surrogate geometry, manual
compact bodies, or placeholder-only substitution over the stock body model.
The canonical LF-terminated exclusion-ID list has
SHA-256
`f0be4b79e8ee82686414b5745634d9096d272fc16730ef06bb1de7a15de62529`.
The projection references 18 fixed sprites, all in the `minecraft` or
`framedblocks` namespace.

The projected gzip is 1,256,231 bytes with SHA-256
`d5a91b78090116b9223f1e96d6903cfd6261d7076b8cbe8d67d469ea2de44253`.
Its uncompressed canonical JSON is 56,464,720 bytes with SHA-256
`e16b72167389c92b558dcb5a13d2cbae4e56a59da3f7cdb689c81637798cd001`.
Independent projections from both byte-identical captures produced the same
gzip bytes.

The private capture evidence can optionally be validated and projected with
the dedicated, explicit task (it is not part of a normal build and is not
needed to compile, test, inspect, or modify the shipped source form):

```bash
./gradlew --no-daemon projectGeometryProfile \
  -PrawGeometryExport=/path/to/framedblocks-10.6.1-models.json.gz
```

The task rejects anything except the pinned schema-v2 gzip and atomically
writes the tracked schema-v3 resource. Use
`-PprojectedGeometryOutput=/path/to/output.json.gz` for a comparison output.

The projected neutral geometry is derived from FramedBlocks' client rendering
result and is distributed under `LGPL-3.0-only`. No FramedBlocks classes,
binaries, textures, model files, source archive, or JAR are bundled. The
blockstate and block-entity manifests contain registry identifiers and are
generated/checked from the exact artifact and correlated registration roster.
The GitHub-generated commit archive used for review is not a published source
JAR and is neither bundled nor redistributed.

The projection records the exact client/config/resource-pack fingerprint used
for capture. The add-on does not cryptographically inventory BlueMap's actual
runtime resource stack or compare that live stack with the recorded client
fingerprint. Resource-stack equivalence remains a deployment and visual-
acceptance limitation, not an activation guarantee.

Update the machine-readable `provenance/upstreams.json` before changing any
geometry, UV, orientation, culling, tint, or neighbor algorithm.

The Gradle sources JAR is a convenience artifact. For a release, the exact
tagged repository—including the tracked schema-v3 preferred source form,
build scripts, wrapper, tests, licenses, and provenance—is the complete source
distribution for this add-on. The private schema-v2 captures remain optional
provenance and regeneration evidence rather than a build input.

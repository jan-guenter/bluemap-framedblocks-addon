# Compatibility evidence

| Component | Current compatibility target | Evidence |
| --- | --- | --- |
| All the Mons | `1.2.0` | exact export SHA-256 `1d37df201daddecf5454115f5205cca15ca6ab84ed102bcc2e312f4c14876e5d`; pack commit `c7bb230f21d14d26859d0b92548f089b3a493ad9` |
| Minecraft | `1.21.1` | exact pack baseline |
| NeoForge | `21.1.248` | exact pack baseline; not an add-on compile dependency |
| Java | `21` | toolchain and class-file gate |
| FramedBlocks | exactly `10.6.1` | SHA-1 `3007be0007d09c0225ca33b647461f342eac0503`; SHA-256 `3337f29e1fa3331e8740eef9c20b0750d81fd86d1057fb81012a5c4792aa3369` |
| BlueMap | backport `5.22-agent.backport-5.22-mc1.21.1-2` only | tag `v5.22-agent.backport-5.22-mc1.21.1-2`; commit `9be321df995a1103808621d529eb72773e719d4d`; upstream 5.22 commit `fe5115d5548a30d34175b8e0449aaca280af199f` |

The FramedBlocks runtime archive is `T2_ARTIFACT_EXACT`. Commit
`99522893fce0c9cd543194be1e8cefd488e0eec8` declares 10.6.1 and matches the
release timing, so source claims are `T4_SOURCE_CORRELATED`; a reproducible
source-to-JAR build has not been demonstrated.

The All the Mons 1.2.0 server contains the same exact FramedBlocks artifact as
the accepted 1.1.1 baseline. Its exact `config/framedblocks-client.toml` is
7,109 bytes with SHA-256
`02e7e1c004fc6a15247dd0ddb5c5210a9e0cc901f18f85ad689886eae3d3ea83`,
matching the profile's pinned client config. Between BlueMap backport commits
`fe79cf5b9f4d8ca28f4e41c2aeb9ef792e336a8d` and
`9be321df995a1103808621d529eb72773e719d4d`, the only implementation change is
the NeoForge compile dependency pin from 21.1.234 to 21.1.248. Every audited
registry, renderer, resource-extension, add-on-loader, and NBT source blob is
byte-identical. Official upstream 5.22 binaries target Java 25 and are not an
accepted runtime identity.

## Historical accepted row

The `0.1.0-alpha.1` artifact (1,326,858 bytes, SHA-256
`4a88fd3a78acf4bcc24ac173db5b9db9efc56e057eec31eeded6432b3bd695c5`)
is retained as accepted evidence for All the Mons 1.1.1, NeoForge 21.1.234,
and BlueMap backport `5.22-agent.backport-5.22-mc1.21.1-1` at
`fe79cf5b9f4d8ca28f4e41c2aeb9ef792e336a8d`. The current runtime detector does
not accept that old host identity. A new 1.2.0 lifecycle has not yet been
observed for the compatibility candidate.

No later FramedBlocks version is supported or inferred from this profile.

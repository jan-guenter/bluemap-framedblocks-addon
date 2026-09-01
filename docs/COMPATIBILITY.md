# Compatibility evidence

| Component | Current compatibility target | Evidence |
| --- | --- | --- |
| All the Mons | `1.2.0` | exact export SHA-256 `1d37df201daddecf5454115f5205cca15ca6ab84ed102bcc2e312f4c14876e5d`; pack commit `c7bb230f21d14d26859d0b92548f089b3a493ad9` |
| Minecraft | `1.21.1` | exact pack baseline |
| NeoForge | `21.1.248` | exact pack baseline; not an add-on compile dependency |
| Java | `21` | toolchain and class-file gate |
| FramedBlocks | exactly `10.6.1` | SHA-1 `3007be0007d09c0225ca33b647461f342eac0503`; SHA-256 `3337f29e1fa3331e8740eef9c20b0750d81fd86d1057fb81012a5c4792aa3369` |
| BlueMap | feature-backport build `5.22-feature.backport-5.23-stateless-java-web-server-46` only | branch `feature/backport-5.23-stateless-java-web-server`; commit `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac`; API commit `285c9a60eff3ac2b0cab308ce1058d1565be0971`; upstream 5.23 commit `4c4cbc291b361ceff6ee239448e9f988f9019dbb` |

The FramedBlocks runtime archive is `T2_ARTIFACT_EXACT`. Commit
`99522893fce0c9cd543194be1e8cefd488e0eec8` declares 10.6.1 and matches the
release timing, so source claims are `T4_SOURCE_CORRELATED`; a reproducible
source-to-JAR build has not been demonstrated.

The All the Mons 1.2.0 server contains the same exact FramedBlocks artifact as
the accepted 1.1.1 baseline. Its exact `config/framedblocks-client.toml` is
7,109 bytes with SHA-256
`02e7e1c004fc6a15247dd0ddb5c5210a9e0cc901f18f85ad689886eae3d3ea83`,
matching the profile's pinned client config. The active feature-backport
commit keeps every audited registry, renderer, resource-extension, add-on-
loader, and NBT source blob recorded in `provenance/upstreams.json` byte-
identical to the inherited internal ABI. No other BlueMap commit or binary is
an accepted runtime identity.

## Historical accepted row

The `0.1.0-alpha.1` artifact (1,326,858 bytes, SHA-256
`4a88fd3a78acf4bcc24ac173db5b9db9efc56e057eec31eeded6432b3bd695c5`)
is retained as accepted evidence for All the Mons 1.1.1, NeoForge 21.1.234,
and BlueMap backport `5.22-agent.backport-5.22-mc1.21.1-1` at
`fe79cf5b9f4d8ca28f4e41c2aeb9ef792e336a8d`. The current runtime detector does
not accept that old host identity. The current 1.2.0 candidate passed an
exact-host composite render and owner visual review, but it has not repeated
the historical full removal-and-restoration lifecycle.

No later FramedBlocks version is supported or inferred from this profile.

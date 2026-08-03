# Compatibility evidence

| Component | Initial supported identity | Evidence |
| --- | --- | --- |
| All the Mons | `1.1.1` | pack manifest and runtime knowledge snapshot |
| Minecraft | `1.21.1` | exact pack baseline |
| NeoForge | `21.1.234` | exact pack baseline; not a compile dependency |
| Java | `21` | toolchain and class-file gate |
| FramedBlocks | exactly `10.6.1` | SHA-1 `3007be0007d09c0225ca33b647461f342eac0503`; SHA-256 `3337f29e1fa3331e8740eef9c20b0750d81fd86d1057fb81012a5c4792aa3369` |
| BlueMap | upstream `5.22` internal ABI | upstream commit `fe5115d5548a30d34175b8e0449aaca280af199f`; backport commit `fe79cf5b9f4d8ca28f4e41c2aeb9ef792e336a8d` |

The FramedBlocks runtime archive is `T2_ARTIFACT_EXACT`. Commit
`99522893fce0c9cd543194be1e8cefd488e0eec8` declares 10.6.1 and matches the
release timing, so source claims are `T4_SOURCE_CORRELATED`; a reproducible
source-to-JAR build has not been demonstrated.

The audited BlueMap registry, renderer, resource-extension, add-on-loader, and
NBT files are unchanged between upstream 5.22 and the exact workspace
backport. Official upstream 5.22 binaries target Java 25 and are not used as
the Java 21 build input.

No later FramedBlocks version is supported or inferred from this profile.

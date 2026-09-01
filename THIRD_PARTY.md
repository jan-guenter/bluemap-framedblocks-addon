# Third-party software

The production JAR contains an `LGPL-3.0-only` neutral geometry profile derived
from the exact FramedBlocks client rendering result. It does not bundle
third-party code, binaries, source files, textures, model files, or JARs.

| Project | Use | Version/evidence | License | Bundled |
| --- | --- | --- | --- | --- |
| BlueMap | Compile/runtime host through its internal renderer interfaces | upstream `5.23`; exact Java 21 feature backport commit `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac` | MIT | No |
| BlueNBT | Runtime NBT deserialization supplied by BlueMap | `3.5.1` | MIT | No |
| FramedBlocks | Installed resources, persisted-data format, and source of projected neutral geometry | `10.6.1`, SHA-1 `3007be0007d09c0225ca33b647461f342eac0503` | LGPL-3.0-only | Derived geometry data only; no code, binaries, textures, or models |
| JetBrains Java Annotations | Compile-only annotations required by the audited BlueMap class path | `23.0.0` | Apache-2.0 | No |
| JUnit | Test framework | `5.11.4` | EPL-2.0 | No |
| Gson | Optional geometry-projection tooling | `2.8.9` | Apache-2.0 | No |
| Checkstyle | Source-style verification | `10.18.2` | LGPL-2.1-or-later | No |
| Gradle Wrapper | Repository build bootstrap | `9.4.0` | Apache-2.0 | Repository tooling only |

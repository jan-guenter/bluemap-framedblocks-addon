# Third-party software

The production JAR contains an `LGPL-3.0-only` neutral geometry profile derived
from the exact FramedBlocks client rendering result and four exact MIT-licensed
Adapter API classes compiled from pinned source. It contains no nested JAR,
FramedBlocks class, texture, or model file.

| Project | Use | Version/evidence | License | Bundled |
| --- | --- | --- | --- | --- |
| BlueMap | Compile/runtime host through its internal renderer interfaces | upstream `5.23`; exact Java 21 feature backport commit `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac` | MIT | No |
| BlueMap Add-on Adapter API | Runtime identity, registry guard, extension wrapper, and synthetic dispatch | `0.1.0-alpha.2`, commit `e81f08bc4bfbf02d810ec8949a019130e2e61634`, source tree `2f974c9bb2ba13888d69682f86f30f58922d30eb` | MIT | Four source-compiled classes |
| BlueNBT | Runtime NBT deserialization supplied by BlueMap | `3.5.1` | MIT | No |
| FramedBlocks | Installed resources, persisted-data format, and source of projected neutral geometry | `10.6.1`, SHA-1 `3007be0007d09c0225ca33b647461f342eac0503` | LGPL-3.0-only | Derived geometry data only; no code, binaries, textures, or models |
| Crystalix | Exact custom-camouflage persisted RGB and glass-state contract | `3.0.0`, exact JAR SHA-256 `42f97cf776cff8261bf671e64a333bbec65a8bf28e519d39cd958e0af9848e6c` | LGPL-3.0 | No |
| Dyenamics and Friends | Runtime-only source for 90 hidden Luminax full-block resources and one hidden Productive Metalworks texture | `1.21.1-2.2.2`, exact 8,361,784-byte JAR SHA-256 `c9797951ec4773d885cad8e15944374d9e33a43102cfafdb883a71d142a3510f` | All Rights Reserved | No; exact installed JAR is read at runtime |
| JetBrains Java Annotations | Compile-only annotations required by the audited BlueMap class path | `23.0.0` | Apache-2.0 | No |
| JUnit | Test framework | `5.11.4` | EPL-2.0 | No |
| Gson | Optional geometry-projection tooling | `2.8.9` | Apache-2.0 | No |
| Checkstyle | Source-style verification | `10.18.2` | LGPL-2.1-or-later | No |
| Gradle Wrapper | Repository build bootstrap | `9.4.0` | Apache-2.0 | Repository tooling only |

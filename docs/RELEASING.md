# Release procedure

Releases are maintainer operations. The workflow publishes both a GitHub
Release and the same Maven publication to GitHub Packages. GitHub Releases are
the normal installation channel because GitHub's Maven registry requires
authentication even for public packages.

## Version contract

- Use SemVer. Alpha versions such as `0.1.0-alpha.1` are GitHub prereleases.
- Development versions end in `-SNAPSHOT` and cannot be tagged.
- The tag is exactly `v<addon_version>`.
- The tag commit must be reachable from `main`.
- `CHANGELOG.md` must contain `## [<addon_version>] - YYYY-MM-DD`.
- Published Maven versions are immutable. Never reuse a version or move a
  release tag.

## Prepare through a pull request

1. Start from current `main` and create a release-preparation branch.
2. Change `addon_version` from its snapshot value to the release version.
3. Move the relevant changelog entries from `Unreleased` under a dated release
   heading.
4. Run the complete validation contract in `AGENTS.md`, including:

   ```bash
   ./gradlew --no-daemon \
     -PreleaseTag=v<version> \
     verifyReleaseMetadata
   ```

5. Open a pull request and wait for required CI checks.
6. Merge without bypassing failed or pending checks.

## Tag and automated publication

After the release-preparation pull request is on `main`, create and push an
annotated `v<version>` tag at that exact commit. The tag workflow then:

1. verifies tag, version, changelog, and `main` ancestry;
2. verifies the generated gallery, provenance, exact FramedBlocks artifact,
   tests, production JAR, POM, and projected-geometry source set;
3. builds twice and byte-compares the main JAR, sources JAR, POM, and Gradle
   module metadata;
4. creates a draft GitHub Release with checksums;
5. generates build-provenance attestations for the JARs;
6. publishes the Maven coordinates to GitHub Packages; and
7. publishes the draft as a prerelease when the version has a prerelease
   suffix, or as the latest release otherwise.

Verify the completed workflow, downloadable assets, checksums, attestation,
release classification, and Maven package before announcing the release.
Create a follow-up pull request that advances `addon_version` to the next
`-SNAPSHOT` version.

## Failure recovery

The release intentionally becomes public only after package publication. If a
run fails before the draft Release is created, fix the cause and rerun it. If
a draft exists, do not create another release or reuse the version.

If the Maven publication has not succeeded, delete only the incomplete draft
after retaining its logs, fix the cause, and rerun the same immutable tag. If
the Maven publication has succeeded, do not rerun the publish task: verify
that package and its digest, attach or verify the retained draft assets and
checksums, then manually publish that draft with the same prerelease/latest
classification the workflow would have used.

If any published asset or package is wrong, do not overwrite it. Keep the
evidence, mark the release clearly, correct the source, and publish a new
version.

/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExactArtifactDetectorTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void acceptsOnlyTheExpectedHashOfAFramedBlocksJar() throws Exception {
        Path exact = writeJar(
                "renamed-mod.jar",
                "modLoader = \"javafml\"\n"
                        + "[[mods]] # declaration table\n"
                        + "modId = \"framedblocks\" # exact mod id\n",
                "exact-test-artifact"
        );
        ExactArtifactDetector detector = new ExactArtifactDetector(
                ExactArtifactDetector.sha256(exact),
                Files.size(exact)
        );

        ExactArtifactDetector.Detection detection = detector.detect(List.of(exact));

        assertTrue(detection.exact());
        assertEquals("exact-10.6.1", detection.reason());
    }

    @Test
    void distinguishesUnsupportedCandidateFromMissingCandidate() throws Exception {
        Path wrong = writeJar(
                "neutral-name.jar",
                "[[mods]]\nmodId = 'framedblocks'\n",
                "wrong"
        );
        Path addOn = writeJar("bluemap-framedblocks-addon.jar", null, "add-on");
        ExactArtifactDetector detector = new ExactArtifactDetector("00", Files.size(wrong));

        ExactArtifactDetector.Detection unsupported = detector.detect(List.of(wrong));
        ExactArtifactDetector.Detection absent = detector.detect(List.of(
                temporaryDirectory,
                addOn
        ));

        assertFalse(unsupported.exact());
        assertEquals("unsupported-framedblocks-artifact", unsupported.reason());
        assertFalse(absent.exact());
        assertEquals("framedblocks-artifact-not-found", absent.reason());
    }

    @Test
    void rejectsMatchingHashWhenArtifactSizeDoesNotMatch() throws Exception {
        Path candidate = writeJar(
                "framedblocks.jar",
                "[[mods]]\nmodId = \"framedblocks\"\n",
                "candidate"
        );
        ExactArtifactDetector detector = new ExactArtifactDetector(
                ExactArtifactDetector.sha256(candidate),
                Files.size(candidate) + 1
        );

        ExactArtifactDetector.Detection detection = detector.detect(List.of(candidate));

        assertFalse(detection.exact());
        assertEquals("unsupported-framedblocks-artifact", detection.reason());
    }

    @Test
    void ignoresTheBlueMapAddOnJarAlongsideTheExactModJar() throws Exception {
        Path exact = writeJar(
                "FramedBlocks-10.6.1.jar",
                "[[mods]]\nmodId = \"framedblocks\"\n",
                "exact"
        );
        Path addOn = writeJar("bluemap-framedblocks-addon.jar", null, "blue-map-add-on");
        ExactArtifactDetector detector = new ExactArtifactDetector(
                ExactArtifactDetector.sha256(exact),
                Files.size(exact)
        );

        for (List<Path> order : List.of(List.of(exact, addOn), List.of(addOn, exact))) {
            ExactArtifactDetector.Detection detection = detector.detect(order);
            assertTrue(detection.exact());
            assertEquals("exact-10.6.1", detection.reason());
        }
    }

    @Test
    void rejectsTwoDeclaringArtifactsInEitherAdversarialOrder() throws Exception {
        Path exact = writeJar(
                "first.jar",
                "[[mods]]\nmodId = \"framedblocks\"\n",
                "exact"
        );
        Path wrong = writeJar(
                "second.jar",
                "[[mods]]\nmodId = \"framedblocks\"\n",
                "wrong"
        );
        ExactArtifactDetector detector = new ExactArtifactDetector(
                ExactArtifactDetector.sha256(exact),
                Files.size(exact)
        );

        for (List<Path> order : List.of(List.of(exact, wrong), List.of(wrong, exact))) {
            ExactArtifactDetector.Detection detection = detector.detect(order);
            assertFalse(detection.exact());
            assertEquals("multiple-framedblocks-artifacts", detection.reason());
        }
    }

    @Test
    void rejectsDistinctDuplicateArtifactsButNotTheSameRootTwice() throws Exception {
        Path exact = writeJar(
                "one.jar",
                "[[mods]]\nmodId = \"framedblocks\"\n",
                "same-content"
        );
        Path duplicate = temporaryDirectory.resolve("two.jar");
        Files.copy(exact, duplicate);
        ExactArtifactDetector detector = new ExactArtifactDetector(
                ExactArtifactDetector.sha256(exact),
                Files.size(exact)
        );

        ExactArtifactDetector.Detection distinct = detector.detect(List.of(exact, duplicate));
        ExactArtifactDetector.Detection repeated = detector.detect(List.of(exact, exact));

        assertFalse(distinct.exact());
        assertEquals("multiple-framedblocks-artifacts", distinct.reason());
        assertTrue(repeated.exact());
        assertEquals("exact-10.6.1", repeated.reason());
    }

    @Test
    void ignoresModIdTextOutsideAnActualModsTable() throws Exception {
        Path decoy = writeJar(
                "framedblocks-decoy.jar",
                "framedblocksHint = \"modId = 'framedblocks'\"\n"
                        + "[[mods]]\n"
                        + "modId = \"another_mod\"\n"
                        + "description = '''\n"
                        + "[[mods]]\n"
                        + "modId = \"framedblocks\"\n"
                        + "'''\n",
                "decoy"
        );
        ExactArtifactDetector detector = new ExactArtifactDetector(
                ExactArtifactDetector.sha256(decoy),
                Files.size(decoy)
        );

        ExactArtifactDetector.Detection detection = detector.detect(List.of(decoy));

        assertFalse(detection.exact());
        assertEquals("framedblocks-artifact-not-found", detection.reason());
    }

    private Path writeJar(String fileName, String descriptor, String payload) throws Exception {
        Path jar = temporaryDirectory.resolve(fileName);
        try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(jar))) {
            if (descriptor != null) {
                output.putNextEntry(new ZipEntry("META-INF/neoforge.mods.toml"));
                output.write(descriptor.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                output.closeEntry();
            }
            output.putNextEntry(new ZipEntry("test/payload.txt"));
            output.write(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            output.closeEntry();
        }
        return jar;
    }
}

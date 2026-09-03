/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap523;

import de.bluecolored.bluemap.core.resources.adapter.ResourcesGson;
import de.bluecolored.bluemap.core.resources.pack.PackVersion;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Key;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DyenamicsAndFriendsCompatResourcesTest {

    private static final String COMPAT_ROOT =
            "compat_packs/luminax/assets/dyenamicsandfriends/";
    private static final List<String> COLORS = List.of(
            "amber", "aquamarine", "bubblegum", "cherenkov", "conifer", "fluorescent",
            "honey", "icy_blue", "lavender", "maroon", "mint", "navy", "peach",
            "persimmon", "rose", "spring_green", "ultramarine", "wine"
    );

    @TempDir
    Path temporaryDirectory;

    @Test
    void stagesTheCompleteClosureAndPublishesWithoutReplacingExistingResources()
            throws Exception {
        Map<String, byte[]> luminax = createLuminaxClosure(16);
        byte[] honeyFireBricks = png(16, 0xFFCC9900);
        Path jar = writeJar(luminax, honeyFireBricks);
        DyenamicsAndFriendsCompatResources loader = loaderFor(
                jar,
                luminax,
                honeyFireBricks
        );
        ResourcePack resourcePack = new ResourcePack(new PackVersion(34, 0));

        Key blockStateKey = Key.parse("dyenamicsandfriends:luminax_amber_luminax_block");
        Key modelKey = Key.parse(
                "dyenamicsandfriends:block/luminax/amber_luminax_block"
        );
        Key textureKey = Key.parse("dyenamicsandfriends:block/luminax/amber_block");
        BlockState existingBlockState = parseBlockState(
                "{\"variants\":{\"\":{\"model\":\"minecraft:block/stone\"}}}"
        );
        Model existingModel = parseModel(
                "{\"textures\":{\"all\":\"minecraft:block/stone\"}}"
        );
        Texture existingTexture = Texture.from(textureKey, image(16, 0xFF112233));
        resourcePack.getBlockStates().put(blockStateKey, existingBlockState);
        resourcePack.getModels().put(modelKey, existingModel);
        resourcePack.getTextures().put(textureKey, existingTexture);

        DyenamicsAndFriendsCompatResources.LoadResult result = loader.load(
                resourcePack,
                List.of(jar)
        );

        assertTrue(result.active());
        assertEquals("exact-dyenamicsandfriends-1.21.1-2.2.2-resources", result.reason());
        assertEquals(36, result.blockStateKeys().size());
        assertEquals(36, result.modelKeys().size());
        assertEquals(19, result.textureKeys().size());
        assertSame(existingBlockState, resourcePack.getBlockStates().get(blockStateKey));
        assertSame(existingModel, resourcePack.getModels().get(modelKey));
        assertSame(existingTexture, resourcePack.getTextures().get(textureKey));
        assertNotNull(resourcePack.getBlockStates().get(
                Key.parse("dyenamicsandfriends:luminax_dim_wine_luminax_block")
        ));
        assertNotNull(resourcePack.getModels().get(
                Key.parse("dyenamicsandfriends:block/luminax/dim_wine_luminax_block")
        ));
        assertNotNull(resourcePack.getTextures().get(
                DyenamicsAndFriendsCompatResources.HONEY_FIRE_BRICKS_TEXTURE
        ));
    }

    @Test
    void rejectsAnIncompleteClosureBeforePublishingAnything() throws Exception {
        Map<String, byte[]> complete = createLuminaxClosure(16);
        Map<String, byte[]> incomplete = new TreeMap<>(complete);
        incomplete.remove(
                COMPAT_ROOT + "blockstates/luminax_amber_luminax_block.json"
        );
        byte[] honeyFireBricks = png(16, 0xFFCC9900);
        Path jar = writeJar(incomplete, honeyFireBricks);
        DyenamicsAndFriendsCompatResources loader = new DyenamicsAndFriendsCompatResources(
                Files.size(jar),
                sha256(Files.readAllBytes(jar)),
                byteCount(complete),
                closureSha256(complete),
                honeyFireBricks.length,
                sha256(honeyFireBricks)
        );
        ResourcePack resourcePack = new ResourcePack(new PackVersion(34, 0));

        assertThrows(IOException.class, () -> loader.load(resourcePack, List.of(jar)));
        assertTrue(resourcePack.getBlockStates().keySet().isEmpty());
        assertTrue(resourcePack.getModels().keySet().isEmpty());
        assertTrue(resourcePack.getTextures().keySet().isEmpty());
    }

    @Test
    void rejectsWrongTextureDimensionsBeforePublishingAnything() throws Exception {
        Map<String, byte[]> luminax = createLuminaxClosure(16);
        luminax.put(
                COMPAT_ROOT + "textures/block/luminax/amber_block.png",
                png(1, 0xFFCC9900)
        );
        byte[] honeyFireBricks = png(16, 0xFFCC9900);
        Path jar = writeJar(luminax, honeyFireBricks);
        DyenamicsAndFriendsCompatResources loader = loaderFor(
                jar,
                luminax,
                honeyFireBricks
        );
        ResourcePack resourcePack = new ResourcePack(new PackVersion(34, 0));

        assertThrows(IOException.class, () -> loader.load(resourcePack, List.of(jar)));
        assertTrue(resourcePack.getBlockStates().keySet().isEmpty());
        assertTrue(resourcePack.getModels().keySet().isEmpty());
        assertTrue(resourcePack.getTextures().keySet().isEmpty());
    }

    @Test
    void leavesPoolsUntouchedForAnUnsupportedArtifact() throws Exception {
        Map<String, byte[]> luminax = createLuminaxClosure(16);
        byte[] honeyFireBricks = png(16, 0xFFCC9900);
        Path jar = writeJar(luminax, honeyFireBricks);
        DyenamicsAndFriendsCompatResources loader = new DyenamicsAndFriendsCompatResources(
                Files.size(jar),
                "00",
                byteCount(luminax),
                closureSha256(luminax),
                honeyFireBricks.length,
                sha256(honeyFireBricks)
        );
        ResourcePack resourcePack = new ResourcePack(new PackVersion(34, 0));

        DyenamicsAndFriendsCompatResources.LoadResult result = loader.load(
                resourcePack,
                List.of(jar)
        );

        assertFalse(result.active());
        assertEquals("unsupported-dyenamicsandfriends-artifact", result.reason());
        assertTrue(resourcePack.getBlockStates().keySet().isEmpty());
        assertTrue(resourcePack.getModels().keySet().isEmpty());
        assertTrue(resourcePack.getTextures().keySet().isEmpty());
    }

    private DyenamicsAndFriendsCompatResources loaderFor(
            Path jar,
            Map<String, byte[]> luminax,
            byte[] honeyFireBricks
    ) throws IOException {
        return new DyenamicsAndFriendsCompatResources(
                Files.size(jar),
                sha256(Files.readAllBytes(jar)),
                byteCount(luminax),
                closureSha256(luminax),
                honeyFireBricks.length,
                sha256(honeyFireBricks)
        );
    }

    private Path writeJar(Map<String, byte[]> luminax, byte[] honeyFireBricks)
            throws IOException {
        Path jar = temporaryDirectory.resolve("dyenamicsandfriends.jar");
        try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(jar))) {
            putEntry(
                    output,
                    "META-INF/neoforge.mods.toml",
                    "[[mods]]\nmodId=\"dyenamicsandfriends\"\n".getBytes(StandardCharsets.UTF_8)
            );
            for (Map.Entry<String, byte[]> entry : luminax.entrySet()) {
                putEntry(output, entry.getKey(), entry.getValue());
            }
            putEntry(
                    output,
                    DyenamicsAndFriendsCompatResources.HONEY_FIRE_BRICKS_ENTRY,
                    honeyFireBricks
            );
        }
        return jar;
    }

    private static void putEntry(ZipOutputStream output, String path, byte[] bytes)
            throws IOException {
        output.putNextEntry(new ZipEntry(path));
        output.write(bytes);
        output.closeEntry();
    }

    private static Map<String, byte[]> createLuminaxClosure(int textureSize)
            throws IOException {
        Map<String, byte[]> resources = new TreeMap<>();
        for (String color : COLORS) {
            String normalState = "luminax_" + color + "_luminax_block";
            String dimState = "luminax_dim_" + color + "_luminax_block";
            String normalModel = color + "_luminax_block";
            String dimModel = "dim_" + color + "_luminax_block";
            resources.put(
                    COMPAT_ROOT + "blockstates/" + normalState + ".json",
                    blockStateJson(normalModel)
            );
            resources.put(
                    COMPAT_ROOT + "blockstates/" + dimState + ".json",
                    blockStateJson(dimModel)
            );
            resources.put(
                    COMPAT_ROOT + "models/block/luminax/" + normalModel + ".json",
                    modelJson(color)
            );
            resources.put(
                    COMPAT_ROOT + "models/block/luminax/" + dimModel + ".json",
                    modelJson(color)
            );
            resources.put(
                    COMPAT_ROOT + "textures/block/luminax/" + color + "_block.png",
                    png(textureSize, 0xFFAA5500 | color.length())
            );
        }
        return resources;
    }

    private static byte[] blockStateJson(String model) {
        return ("{\"variants\":{\"\":{\"model\":\"dyenamicsandfriends:block/luminax/"
                + model + "\"}}}").getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] modelJson(String color) {
        return ("{\"parent\":\"minecraft:block/cube_all\",\"textures\":{\"all\":"
                + "\"dyenamicsandfriends:block/luminax/" + color + "_block\"}}")
                .getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] png(int size, int color) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image(size, color), "png", output);
        return output.toByteArray();
    }

    private static BufferedImage image(int size, int color) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                image.setRGB(x, y, color);
            }
        }
        return image;
    }

    private static long byteCount(Map<String, byte[]> resources) {
        return resources.values().stream().mapToLong(bytes -> bytes.length).sum();
    }

    private static String closureSha256(Map<String, byte[]> resources) {
        MessageDigest digest = ExactNeoForgeModArtifact.newSha256();
        for (Map.Entry<String, byte[]> resource : resources.entrySet()) {
            digest.update(resource.getKey().getBytes(StandardCharsets.UTF_8));
            digest.update((byte) 0);
            digest.update(resource.getValue());
            digest.update((byte) 0);
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    private static String sha256(byte[] bytes) {
        MessageDigest digest = ExactNeoForgeModArtifact.newSha256();
        digest.update(bytes);
        return HexFormat.of().formatHex(digest.digest());
    }

    private static BlockState parseBlockState(String json) {
        return ResourcesGson.INSTANCE.fromJson(json, BlockState.class);
    }

    private static Model parseModel(String json) {
        return ResourcesGson.INSTANCE.fromJson(json, Model.class);
    }
}

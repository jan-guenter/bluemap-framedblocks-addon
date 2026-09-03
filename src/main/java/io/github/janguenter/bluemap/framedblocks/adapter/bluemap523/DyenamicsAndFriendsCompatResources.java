/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap523;

import de.bluecolored.bluemap.core.resources.adapter.ResourcesGson;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Key;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Loads exact client-resource-pack files hidden inside the installed
 * Dyenamics and Friends artifact. No third-party resources are bundled.
 */
final class DyenamicsAndFriendsCompatResources {

    static final long ARTIFACT_BYTES = 8_361_784L;
    static final String ARTIFACT_SHA256 =
            "c9797951ec4773d885cad8e15944374d9e33a43102cfafdb883a71d142a3510f";
    static final long LUMINAX_CLOSURE_BYTES = 58_086L;
    static final String LUMINAX_CLOSURE_SHA256 =
            "ed4180e18caa2f31d62453dee79e36197eb5dcf826788a5d3a4365aa8c86190c";

    static final String HONEY_FIRE_BRICKS_ENTRY =
            "compat_packs/productivemetalworks/assets/dyenamicsandfriends/"
                    + "textures/block/productivemetalworks/honey_fire_bricks.png";
    static final Key HONEY_FIRE_BRICKS_TEXTURE = Key.parse(
            "dyenamicsandfriends:block/productivemetalworks/honey_fire_bricks"
    );
    static final long HONEY_FIRE_BRICKS_BYTES = 864L;
    static final String HONEY_FIRE_BRICKS_SHA256 =
            "9880929dba2bae8430658e8cf968b312f5a9dc5a44aad340a1d43d39dc00312d";

    private static final int MAX_JSON_BYTES = 16 * 1024;
    private static final int MAX_PNG_BYTES = 16 * 1024;
    private static final String COMPAT_ROOT =
            "compat_packs/luminax/assets/dyenamicsandfriends/";
    private static final List<String> COLORS = List.of(
            "amber",
            "aquamarine",
            "bubblegum",
            "cherenkov",
            "conifer",
            "fluorescent",
            "honey",
            "icy_blue",
            "lavender",
            "maroon",
            "mint",
            "navy",
            "peach",
            "persimmon",
            "rose",
            "spring_green",
            "ultramarine",
            "wine"
    );
    private static final List<ResourceSpec> LUMINAX_RESOURCES = createLuminaxResources();

    private final ExactNeoForgeModArtifact artifactDetector;
    private final long expectedLuminaxBytes;
    private final String expectedLuminaxSha256;
    private final long expectedHoneyFireBricksBytes;
    private final String expectedHoneyFireBricksSha256;

    DyenamicsAndFriendsCompatResources() {
        this(
                ARTIFACT_BYTES,
                ARTIFACT_SHA256,
                LUMINAX_CLOSURE_BYTES,
                LUMINAX_CLOSURE_SHA256,
                HONEY_FIRE_BRICKS_BYTES,
                HONEY_FIRE_BRICKS_SHA256
        );
    }

    DyenamicsAndFriendsCompatResources(
            long expectedArtifactBytes,
            String expectedArtifactSha256,
            long expectedLuminaxBytes,
            String expectedLuminaxSha256,
            long expectedHoneyFireBricksBytes,
            String expectedHoneyFireBricksSha256
    ) {
        this.artifactDetector = new ExactNeoForgeModArtifact(
                "dyenamicsandfriends",
                expectedArtifactSha256,
                expectedArtifactBytes,
                "dyenamicsandfriends"
        );
        if (expectedLuminaxBytes < 0 || expectedHoneyFireBricksBytes < 0) {
            throw new IllegalArgumentException("Expected resource byte counts must not be negative");
        }
        this.expectedLuminaxBytes = expectedLuminaxBytes;
        this.expectedLuminaxSha256 = Objects.requireNonNull(
                expectedLuminaxSha256,
                "expectedLuminaxSha256"
        );
        this.expectedHoneyFireBricksBytes = expectedHoneyFireBricksBytes;
        this.expectedHoneyFireBricksSha256 = Objects.requireNonNull(
                expectedHoneyFireBricksSha256,
                "expectedHoneyFireBricksSha256"
        );
    }

    LoadResult load(ResourcePack resourcePack, Iterable<Path> roots)
            throws IOException, InterruptedException {
        Objects.requireNonNull(resourcePack, "resourcePack");
        ExactNeoForgeModArtifact.Detection detection = artifactDetector.detect(roots);
        if (!detection.exact()) {
            return LoadResult.inactive(detection.reason());
        }

        StagedResources staged = stage(detection.path());
        publishFirstWins(resourcePack, staged);
        return LoadResult.active(staged);
    }

    private StagedResources stage(Path artifact) throws IOException, InterruptedException {
        Map<String, byte[]> luminaxBytes = new TreeMap<>();
        byte[] honeyFireBricksBytes;
        try (ZipFile zip = new ZipFile(artifact.toFile())) {
            for (ResourceSpec spec : LUMINAX_RESOURCES) {
                if (Thread.interrupted()) {
                    throw new InterruptedException("Interrupted while staging optional resources");
                }
                luminaxBytes.put(spec.entry(), readEntry(zip, spec.entry(), spec.maxBytes()));
            }
            honeyFireBricksBytes = readEntry(
                    zip,
                    HONEY_FIRE_BRICKS_ENTRY,
                    MAX_PNG_BYTES
            );
        }

        DigestResult luminaxDigest = digestClosure(luminaxBytes);
        if (luminaxDigest.bytes() != expectedLuminaxBytes
                || !expectedLuminaxSha256.equals(luminaxDigest.sha256())) {
            throw new IOException("Dyenamics and Friends Luminax resource closure changed");
        }
        if (honeyFireBricksBytes.length != expectedHoneyFireBricksBytes
                || !expectedHoneyFireBricksSha256.equals(sha256(honeyFireBricksBytes))) {
            throw new IOException("Dyenamics and Friends Productive Metalworks texture changed");
        }

        Map<Key, BlockState> blockStates = new LinkedHashMap<>();
        Map<Key, Model> models = new LinkedHashMap<>();
        Map<Key, Texture> textures = new LinkedHashMap<>();
        for (ResourceSpec spec : LUMINAX_RESOURCES) {
            byte[] bytes = luminaxBytes.get(spec.entry());
            switch (spec.kind()) {
                case BLOCK_STATE -> blockStates.put(
                        spec.key(),
                        parseJson(bytes, BlockState.class, spec.entry())
                );
                case MODEL -> models.put(
                        spec.key(),
                        parseJson(bytes, Model.class, spec.entry())
                );
                case TEXTURE -> textures.put(
                        spec.key(),
                        parseTexture(spec.key(), bytes, spec.entry())
                );
            }
        }
        textures.put(
                HONEY_FIRE_BRICKS_TEXTURE,
                parseTexture(
                        HONEY_FIRE_BRICKS_TEXTURE,
                        honeyFireBricksBytes,
                        HONEY_FIRE_BRICKS_ENTRY
                )
        );
        if (blockStates.size() != 36 || models.size() != 36 || textures.size() != 19) {
            throw new IOException("Dyenamics and Friends staged resource inventory changed");
        }
        return new StagedResources(blockStates, models, textures);
    }

    private static byte[] readEntry(ZipFile zip, String path, int maxBytes) throws IOException {
        ZipEntry entry = zip.getEntry(path);
        if (entry == null || entry.isDirectory()) {
            throw new IOException("Required Dyenamics and Friends resource is missing: " + path);
        }
        if (entry.getSize() > maxBytes) {
            throw new IOException("Dyenamics and Friends resource exceeds its byte limit: " + path);
        }
        byte[] bytes;
        try (var input = zip.getInputStream(entry)) {
            bytes = input.readNBytes(maxBytes + 1);
        }
        if (bytes.length > maxBytes) {
            throw new IOException("Dyenamics and Friends resource exceeds its byte limit: " + path);
        }
        return bytes;
    }

    private static <T> T parseJson(byte[] bytes, Class<T> type, String path) throws IOException {
        String json = decodeUtf8(bytes, path);
        try {
            T resource = ResourcesGson.INSTANCE.fromJson(json, type);
            if (resource == null) {
                throw new IOException("Dyenamics and Friends resource parsed to null: " + path);
            }
            return resource;
        } catch (RuntimeException exception) {
            throw new IOException("Dyenamics and Friends resource is invalid JSON: " + path, exception);
        }
    }

    private static String decodeUtf8(byte[] bytes, String path) throws IOException {
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes))
                    .toString();
        } catch (CharacterCodingException exception) {
            throw new IOException("Dyenamics and Friends resource is not UTF-8: " + path, exception);
        }
    }

    private static Texture parseTexture(Key key, byte[] bytes, String path) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
        if (image == null || image.getWidth() != 16 || image.getHeight() != 16) {
            throw new IOException("Dyenamics and Friends texture is not a 16x16 PNG: " + path);
        }
        return Texture.from(key, image);
    }

    private static DigestResult digestClosure(Map<String, byte[]> resources) {
        MessageDigest digest = ExactNeoForgeModArtifact.newSha256();
        long bytes = 0;
        for (Map.Entry<String, byte[]> resource : resources.entrySet()) {
            digest.update(resource.getKey().getBytes(StandardCharsets.UTF_8));
            digest.update((byte) 0);
            digest.update(resource.getValue());
            digest.update((byte) 0);
            bytes += resource.getValue().length;
        }
        return new DigestResult(bytes, HexFormat.of().formatHex(digest.digest()));
    }

    private static String sha256(byte[] bytes) {
        MessageDigest digest = ExactNeoForgeModArtifact.newSha256();
        digest.update(bytes);
        return HexFormat.of().formatHex(digest.digest());
    }

    private static void publishFirstWins(ResourcePack resourcePack, StagedResources staged) {
        staged.blockStates().forEach(resourcePack.getBlockStates()::putIfAbsent);
        staged.models().forEach(resourcePack.getModels()::putIfAbsent);
        staged.textures().forEach(resourcePack.getTextures()::putIfAbsent);
    }

    private static List<ResourceSpec> createLuminaxResources() {
        List<ResourceSpec> resources = new ArrayList<>(90);
        for (String color : COLORS) {
            String normalState = "luminax_" + color + "_luminax_block";
            String dimState = "luminax_dim_" + color + "_luminax_block";
            String normalModel = color + "_luminax_block";
            String dimModel = "dim_" + color + "_luminax_block";

            resources.add(new ResourceSpec(
                    COMPAT_ROOT + "blockstates/" + normalState + ".json",
                    Key.parse("dyenamicsandfriends:" + normalState),
                    ResourceKind.BLOCK_STATE,
                    MAX_JSON_BYTES
            ));
            resources.add(new ResourceSpec(
                    COMPAT_ROOT + "blockstates/" + dimState + ".json",
                    Key.parse("dyenamicsandfriends:" + dimState),
                    ResourceKind.BLOCK_STATE,
                    MAX_JSON_BYTES
            ));
            resources.add(new ResourceSpec(
                    COMPAT_ROOT + "models/block/luminax/" + normalModel + ".json",
                    Key.parse("dyenamicsandfriends:block/luminax/" + normalModel),
                    ResourceKind.MODEL,
                    MAX_JSON_BYTES
            ));
            resources.add(new ResourceSpec(
                    COMPAT_ROOT + "models/block/luminax/" + dimModel + ".json",
                    Key.parse("dyenamicsandfriends:block/luminax/" + dimModel),
                    ResourceKind.MODEL,
                    MAX_JSON_BYTES
            ));
            resources.add(new ResourceSpec(
                    COMPAT_ROOT + "textures/block/luminax/" + color + "_block.png",
                    Key.parse("dyenamicsandfriends:block/luminax/" + color + "_block"),
                    ResourceKind.TEXTURE,
                    MAX_PNG_BYTES
            ));
        }
        return List.copyOf(resources);
    }

    record LoadResult(
            boolean active,
            String reason,
            Set<Key> blockStateKeys,
            Set<Key> modelKeys,
            Set<Key> textureKeys
    ) {

        LoadResult {
            reason = Objects.requireNonNull(reason, "reason");
            blockStateKeys = Set.copyOf(blockStateKeys);
            modelKeys = Set.copyOf(modelKeys);
            textureKeys = Set.copyOf(textureKeys);
        }

        static LoadResult inactive(String reason) {
            return new LoadResult(false, reason, Set.of(), Set.of(), Set.of());
        }

        private static LoadResult active(StagedResources staged) {
            return new LoadResult(
                    true,
                    "exact-dyenamicsandfriends-1.21.1-2.2.2-resources",
                    staged.blockStates().keySet(),
                    staged.models().keySet(),
                    staged.textures().keySet()
            );
        }
    }

    private record StagedResources(
            Map<Key, BlockState> blockStates,
            Map<Key, Model> models,
            Map<Key, Texture> textures
    ) {
    }

    private record DigestResult(long bytes, String sha256) {
    }

    private record ResourceSpec(
            String entry,
            Key key,
            ResourceKind kind,
            int maxBytes
    ) {
    }

    private enum ResourceKind {
        BLOCK_STATE,
        MODEL,
        TEXTURE
    }
}

/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap522;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector4f;
import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.ArrayTileModel;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.mask.Mask;
import de.bluecolored.bluemap.core.resources.ResourcePath;
import de.bluecolored.bluemap.core.resources.pack.PackVersion;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.VariantSet;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variants;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Element;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Face;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.TextureVariable;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Direction;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockEntity;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.DimensionType;
import de.bluecolored.bluemap.core.world.LightData;
import de.bluecolored.bluemap.core.world.biome.Biome;
import de.bluecolored.bluemap.core.world.block.BlockAccess;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6.GeometryTemplateProfile;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FramedGeometryRendererEmissionTest {

    private static final int X = 1;
    private static final int Y = 64;
    private static final int Z = 1;
    private static final Key FRAMED_CUBE = Key.parse("framedblocks:framed_cube");
    private static final Key PRIMARY_NORTH = Key.parse("test:block/primary_north");
    private static final Key SECONDARY_EAST = Key.parse("test:block/secondary_east");
    private static final Key FIXED = Key.parse("test:block/fixed");
    private static final Key ORIGINAL = Key.parse("test:block/original");

    @Test
    void substitutesOnlyPlaceholderFacesAndAppliesCamoTintAndEmission() {
        RecordingTileModel model = new RecordingTileModel();
        model.add(2);
        CamoSubstitutionTileModel proxy = new CamoSubstitutionTileModel(
                model,
                Set.of(41),
                73,
                9,
                new Color().set(0.5F, 0.25F, 1F, 1F, true)
        );

        proxy.setMaterialIndex(0, 41)
                .setColor(0, 0.8F, 0.8F, 0.8F)
                .setBlocklight(0, 3);
        proxy.setMaterialIndex(1, 42)
                .setColor(1, 0.8F, 0.8F, 0.8F)
                .setBlocklight(1, 3);

        assertEquals(73, model.face(0).material());
        assertArrayEquals(new float[]{0.4F, 0.2F, 0.8F}, model.face(0).color(), 0F);
        assertEquals(9, model.face(0).blocklight());
        assertEquals(42, model.face(1).material());
        assertArrayEquals(new float[]{0.8F, 0.8F, 0.8F}, model.face(1).color(), 0F);
        assertEquals(3, model.face(1).blocklight());
        assertEquals(true, proxy.substituted());
    }

    @Test
    void emitsProfileTrianglesWithFaceMaterialsUvsTintLightAndAo() throws Exception {
        Fixture fixture = fixture();
        RecordingTileModel model = new RecordingTileModel();

        fixture.renderer().render(
                fixture.neighborhood(),
                null,
                new TileModelView(model),
                new Color()
        );

        assertEquals(6, model.size());
        assertMaterial(model, 0, fixture.gallery().get(PRIMARY_NORTH));
        assertMaterial(model, 1, fixture.gallery().get(PRIMARY_NORTH));
        assertMaterial(model, 2, fixture.gallery().get(SECONDARY_EAST));
        assertMaterial(model, 3, fixture.gallery().get(SECONDARY_EAST));
        assertMaterial(model, 4, fixture.gallery().get(FIXED));
        assertMaterial(model, 5, fixture.gallery().get(FIXED));

        assertArrayEquals(new float[]{
                0F, 1F, 0F,
                1F, 1F, 0F,
                1F, 1F, 1F
        }, model.face(0).positions(), 0F);
        assertArrayEquals(new float[]{
                0F, 1F, 0F,
                1F, 1F, 1F,
                0F, 1F, 1F
        }, model.face(1).positions(), 0F);
        assertArrayEquals(new float[]{
                0F, 0F,
                1F, 0F,
                1F, 1F
        }, model.face(0).uvs(), 0F);
        assertArrayEquals(new float[]{
                0F, 0F,
                1F, 1F,
                0F, 1F
        }, model.face(1).uvs(), 0F);

        assertArrayEquals(new float[]{1F, 1F, 1F}, model.face(0).color(), 0F);
        assertArrayEquals(new float[]{1F, 1F, 1F}, model.face(2).color(), 0F);
        assertArrayEquals(new float[]{1F, 0F, 0F}, model.face(4).color(), 0F);
        assertEquals(9, model.face(0).sunlight());
        assertEquals(8, model.face(0).blocklight());
        assertEquals(11, model.face(1).sunlight());
        assertEquals(10, model.face(1).blocklight());
        assertArrayEquals(new float[]{0.75F, 1F, 1F}, model.face(0).aos(), 0F);
        assertArrayEquals(new float[]{0.75F, 1F, 0.75F}, model.face(1).aos(), 0F);
    }

    @Test
    void rollsBackPartialProfileGeometryBeforeRenderingTheStockResource() throws Exception {
        Fixture fixture = fixture();
        RecordingTileModel model = new RecordingTileModel();
        model.add(1);
        model.setMaterialIndex(0, 777);
        model.failOnAddInvocation(2);

        fixture.renderer().render(
                fixture.neighborhood(),
                null,
                new TileModelView(model),
                new Color()
        );

        int originalMaterial = fixture.gallery().get(ORIGINAL);
        assertEquals(13, model.size());
        assertMaterial(model, 0, 777);
        for (int face = 1; face < model.size(); face++) {
            assertMaterial(model, face, originalMaterial);
            assertNotEquals(fixture.gallery().get(PRIMARY_NORTH), model.face(face).material());
            assertNotEquals(fixture.gallery().get(SECONDARY_EAST), model.face(face).material());
            assertNotEquals(fixture.gallery().get(FIXED), model.face(face).material());
        }
    }

    private static Fixture fixture() throws Exception {
        ResourcePack resourcePack = new ResourcePack(new PackVersion(34, 0));
        Key primary = Key.parse("test:block/primary");
        Key secondary = Key.parse("test:block/secondary");
        Key occluder = Key.parse("test:block/occluder");
        for (Key texture : List.of(
                primary,
                PRIMARY_NORTH,
                secondary,
                SECONDARY_EAST,
                FIXED,
                ORIGINAL,
                occluder
        )) {
            putOpaqueTexture(resourcePack, texture);
        }

        putCube(
                resourcePack,
                Key.parse("test:primary"),
                Key.parse("test:block/primary_model"),
                primary,
                Direction.NORTH,
                PRIMARY_NORTH
        );
        putCube(
                resourcePack,
                Key.parse("test:secondary"),
                Key.parse("test:block/secondary_model"),
                secondary,
                Direction.EAST,
                SECONDARY_EAST
        );
        putCube(
                resourcePack,
                Key.parse("test:occluder"),
                Key.parse("test:block/occluder_model"),
                occluder,
                null,
                null
        );
        putCube(
                resourcePack,
                FRAMED_CUBE,
                Key.parse("test:block/original_model"),
                ORIGINAL,
                null,
                null
        );

        TextureGallery gallery = new TextureGallery();
        gallery.put(resourcePack.getTextures());

        FramedBlockEntityData blockEntity = blockEntity();
        Map<Position, BlockState> states = Map.of(
                new Position(X, Y, Z), BlockState.fromString(FRAMED_CUBE.getFormatted()),
                new Position(X - 1, Y + 1, Z), BlockState.fromString("test:occluder")
        );
        Map<Position, BlockEntity> blockEntities = Map.of(
                new Position(X, Y, Z), blockEntity
        );
        Map<Position, LightData> lights = Map.of(
                new Position(X, Y, Z), new LightData(3, 2),
                new Position(X, Y + 1, Z), new LightData(5, 4)
        );
        BlockNeighborhood neighborhood = new BlockNeighborhood(
                new TestBlockAccess(states, blockEntities, lights),
                resourcePack,
                TEST_RENDER_SETTINGS,
                DimensionType.OVERWORLD
        );
        neighborhood.set(X, Y, Z);

        AdapterActivation activation = new AdapterActivation();
        activation.activate(profile());
        return new Fixture(
                gallery,
                neighborhood,
                new FramedGeometryRenderer(
                        resourcePack,
                        gallery,
                        TEST_RENDER_SETTINGS,
                        activation
                )
        );
    }

    private static void putOpaqueTexture(ResourcePack resourcePack, Key key) throws IOException {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, 0xFFFFFFFF);
        resourcePack.getTextures().put(key, Texture.from(key, image));
    }

    private static void putCube(
            ResourcePack resourcePack,
            Key blockId,
            Key modelId,
            Key defaultTexture,
            Direction specialDirection,
            Key specialTexture
    ) {
        EnumMap<Direction, Face> faces = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            Key texture = direction == specialDirection ? specialTexture : defaultTexture;
            faces.put(direction, new Face(
                    new Vector4f(0F, 0F, 16F, 16F),
                    new TextureVariable(new ResourcePath<Texture>(texture)),
                    direction,
                    0,
                    -1
            ));
        }
        Model model = new Model(new Element(
                Vector3f.ZERO,
                new Vector3f(16F, 16F, 16F),
                faces
        ));
        model.calculateProperties(resourcePack.getTextures());
        resourcePack.getModels().put(modelId, model);

        Variant variant = new Variant(new ResourcePath<Model>(modelId));
        resourcePack.getBlockStates().put(
                blockId,
                new de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState(
                        new Variants(new VariantSet[0], new VariantSet(variant))
                )
        );
    }

    private static FramedBlockEntityData blockEntity() throws ReflectiveOperationException {
        FramedBlockEntityData data = new FramedBlockEntityData();
        setField(data, "camo", blockCamo("test:primary"));
        setField(data, "camoTwo", blockCamo("test:secondary"));
        setField(data, "glowing", false);
        setField(data, "intangible", false);
        setField(data, "reinforced", false);
        setField(data, "updated", (byte) 3);
        setField(data, "overlayColor", 14);
        return data;
    }

    private static Map<String, Object> blockCamo(String blockId) {
        return Map.of(
                "type", "framedblocks:block",
                "state", Map.of("Name", blockId)
        );
    }

    private static void setField(Object target, String fieldName, Object value)
            throws ReflectiveOperationException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static GeometryTemplateProfile profile() throws IOException {
        String template = """
                {"blockId":"framedblocks:framed_cube","properties":{},
                 "quads":[%s,%s,%s]}
                """.formatted(
                quad("primary", "north", "minecraft:block/magenta_concrete", 2),
                quad("secondary", "east", "minecraft:block/lime_concrete", -7),
                quad("fixed", "none", FIXED.getFormatted(), 1_024)
        );
        String document = """
                {"schemaVersion":2,"profileScope":"applied_camo",
                 "sourceModel":"canonical_opaque_full_cube",
                 "modelDataProfile":"diagnostic_applied_camo_no_dynamic_aux",
                 "dynamicModelDataIncluded":false,
                 "blockEntityRendererContentIncluded":false,
                 "uvSpace":"sprite_normalized_baked",
                 "resourcePackOrder":"selected_precedence_including_hidden",
                 "effectiveAmbientOcclusionAssumption":"zero_block_state_emission",
                 "randomSeed":"0x4652414d4544424c","smoothLightingEnabled":true,
                 "framedBlocksVersion":"10.6.1",
                 "framedBlocksJarSha256":
                   "3337f29e1fa3331e8740eef9c20b0750d81fd86d1057fb81012a5c4792aa3369",
                 "framedBlocksJarBytes":4306703,
                 "framedBlocksClientConfigSha256":
                   "02e7e1c004fc6a15247dd0ddb5c5210a9e0cc901f18f85ad689886eae3d3ea83",
                 "minecraftVersion":"1.21.1","neoForgeVersion":"21.1.234",
                 "packFingerprint":
                   "30d715deacda85316240c9a0f67ccc457f7a00667168dbffb82e16c82ddbcf42",
                 "modsSha256":
                   "7f0d771cf2c1dc430fa32153651dd9bec5ae5492f34a6c8a7bef8a067f5d50a7",
                 "resourcePacksOrderedSha256":
                   "b345c2cfa4743c2a46c5a3ddf1817ca601a1eb91ff446e8a58e6cd0da3e8ed3d",
                 "resourcePackIdSetSha256":
                   "1952cce499adb9e79cce0a422418537b3818ef353f100424692bb5dc94958be5",
                 "modCount":430,"resourcePacksOrderedCount":439,
                 "resourcePackIdSetCount":12,"blockCount":1,
                 "blockIdsSha256":"%s",
                 "raw_state_count":1,"raw_state_keys_sha256":"%s",
                 "renderable_state_count":1,
                 "renderable_state_keys_sha256":"%s",
                 "template_count":1,"template_state_keys_sha256":"%s",
                 "alias_pairs_sha256":
                   "00757c3c2b3d98bf083cde975284f0c524ccde27e51e88ecd83a73baaf74f629",
                 "quadCount":3,
                 "nonCamoUtilityBlocks":["framedblocks:framing_saw",
                                         "framedblocks:powered_framing_saw"],
                 "excludedDynamicInputs":["world_block_entity_model_data",
                    "connected_texture_context","geometry_aux_model_data",
                    "flower_pot_contents","collapsible_and_copycat_offsets",
                    "adjustable_double_component_offsets","one_way_window_context",
                    "hidden_face_masks","block_entity_renderer_content"],
                 "templates":[%s],
                 "states":[{"blockId":"framedblocks:framed_cube",
                            "properties":{},"template":0}]}
                """.formatted(
                sha256Lines(FRAMED_CUBE.getFormatted()),
                sha256Lines(FRAMED_CUBE.getFormatted()),
                sha256Lines(FRAMED_CUBE.getFormatted()),
                sha256Lines(FRAMED_CUBE.getFormatted()),
                template
        );
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(compressed)) {
            gzip.write(document.getBytes(StandardCharsets.UTF_8));
        }
        return GeometryTemplateProfile.loadGzip(
                new ByteArrayInputStream(compressed.toByteArray())
        );
    }

    private static String quad(
            String component,
            String sourceFace,
            String sprite,
            int tintIndex
    ) {
        return """
                {"layer":"solid","cullFace":"none","direction":"up",
                 "component":"%s","sourceFace":"%s",
                 "atlas":"minecraft:textures/atlas/blocks.png",
                 "sprite":"%s","tintIndex":%d,"shade":true,
                 "ambientOcclusion":true,"modelAmbientOcclusion":"true",
                 "effectiveAmbientOcclusionUnderZeroEmission":true,
                 "blockLight":0,"skyLight":0,
                 "vertices":[{"x":0,"y":1,"z":0,"u":0,"v":0,
                              "blockLight":0,"skyLight":0},
                             {"x":1,"y":1,"z":0,"u":1,"v":0,
                              "blockLight":8,"skyLight":9},
                             {"x":1,"y":1,"z":1,"u":1,"v":1,
                              "blockLight":0,"skyLight":0},
                             {"x":0,"y":1,"z":1,"u":0,"v":1,
                              "blockLight":10,"skyLight":11}]}
                """.formatted(component, sourceFace, sprite, tintIndex);
    }

    private static String sha256Lines(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(
                    (value + "\n").getBytes(StandardCharsets.UTF_8)
            );
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new AssertionError(exception);
        }
    }

    private static void assertMaterial(RecordingTileModel model, int face, int material) {
        assertEquals(material, model.face(face).material());
    }

    private static final RenderSettings TEST_RENDER_SETTINGS = new RenderSettings() {
        @Override
        public int getRemoveCavesBelowY() {
            return Integer.MIN_VALUE;
        }

        @Override
        public int getCaveDetectionOceanFloor() {
            return 0;
        }

        @Override
        public boolean isCaveDetectionUsesBlockLight() {
            return false;
        }

        @Override
        public float getAmbientLight() {
            return 0F;
        }

        @Override
        public boolean isRenderEdges() {
            return false;
        }

        @Override
        public Mask getRenderMask() {
            return Mask.ALL;
        }

        @Override
        public boolean isSaveHiresLayer() {
            return false;
        }

        @Override
        public boolean isRenderTopOnly() {
            return false;
        }
    };

    private static final class TestBlockAccess implements BlockAccess {
        private final Map<Position, BlockState> states;
        private final Map<Position, BlockEntity> blockEntities;
        private final Map<Position, LightData> lights;
        private int x;
        private int y;
        private int z;

        private TestBlockAccess(
                Map<Position, BlockState> states,
                Map<Position, BlockEntity> blockEntities,
                Map<Position, LightData> lights
        ) {
            this.states = states;
            this.blockEntities = blockEntities;
            this.lights = lights;
        }

        @Override
        public void set(int newX, int newY, int newZ) {
            x = newX;
            y = newY;
            z = newZ;
        }

        @Override
        public BlockAccess copy() {
            return new TestBlockAccess(states, blockEntities, lights);
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public int getZ() {
            return z;
        }

        @Override
        public BlockState getBlockState() {
            return states.getOrDefault(new Position(x, y, z), BlockState.AIR);
        }

        @Override
        public LightData getLightData() {
            LightData light = lights.get(new Position(x, y, z));
            return light == null
                    ? new LightData(0, 0)
                    : new LightData(light.getSkyLight(), light.getBlockLight());
        }

        @Override
        public Biome getBiome() {
            return Biome.DEFAULT;
        }

        @Override
        public BlockEntity getBlockEntity() {
            return blockEntities.get(new Position(x, y, z));
        }

        @Override
        public boolean hasOceanFloorY() {
            return false;
        }

        @Override
        public int getOceanFloorY() {
            return 0;
        }
    }

    private static final class RecordingTileModel extends ArrayTileModel {
        private final List<FaceData> faces = new ArrayList<>();
        private int addInvocation;
        private int failedAddInvocation = -1;

        private RecordingTileModel() {
            super(32);
        }

        void failOnAddInvocation(int invocation) {
            addInvocation = 0;
            failedAddInvocation = invocation;
        }

        FaceData face(int face) {
            return faces.get(face);
        }

        @Override
        public int add(int count) {
            addInvocation++;
            if (addInvocation == failedAddInvocation) {
                failedAddInvocation = -1;
                throw new IllegalStateException("injected mesh emission failure");
            }
            int start = super.add(count);
            while (faces.size() < size()) {
                faces.add(new FaceData());
            }
            return start;
        }

        @Override
        public RecordingTileModel reset(int size) {
            super.reset(size);
            while (faces.size() > size) {
                faces.removeLast();
            }
            return this;
        }

        @Override
        public RecordingTileModel setPositions(
                int face,
                float x1, float y1, float z1,
                float x2, float y2, float z2,
                float x3, float y3, float z3
        ) {
            super.setPositions(face, x1, y1, z1, x2, y2, z2, x3, y3, z3);
            face(face).positions = new float[]{x1, y1, z1, x2, y2, z2, x3, y3, z3};
            return this;
        }

        @Override
        public RecordingTileModel setUvs(
                int face,
                float u1, float v1,
                float u2, float v2,
                float u3, float v3
        ) {
            super.setUvs(face, u1, v1, u2, v2, u3, v3);
            face(face).uvs = new float[]{u1, v1, u2, v2, u3, v3};
            return this;
        }

        @Override
        public RecordingTileModel setAOs(int face, float ao1, float ao2, float ao3) {
            super.setAOs(face, ao1, ao2, ao3);
            face(face).aos = new float[]{ao1, ao2, ao3};
            return this;
        }

        @Override
        public RecordingTileModel setColor(int face, float red, float green, float blue) {
            super.setColor(face, red, green, blue);
            face(face).color = new float[]{red, green, blue};
            return this;
        }

        @Override
        public RecordingTileModel setSunlight(int face, int sunlight) {
            super.setSunlight(face, sunlight);
            face(face).sunlight = sunlight;
            return this;
        }

        @Override
        public RecordingTileModel setBlocklight(int face, int blocklight) {
            super.setBlocklight(face, blocklight);
            face(face).blocklight = blocklight;
            return this;
        }

        @Override
        public RecordingTileModel setMaterialIndex(int face, int material) {
            super.setMaterialIndex(face, material);
            face(face).material = material;
            return this;
        }
    }

    private static final class FaceData {
        private float[] positions;
        private float[] uvs;
        private float[] aos;
        private float[] color;
        private int sunlight;
        private int blocklight;
        private int material;

        float[] positions() {
            return positions;
        }

        float[] uvs() {
            return uvs;
        }

        float[] aos() {
            return aos;
        }

        float[] color() {
            return color;
        }

        int sunlight() {
            return sunlight;
        }

        int blocklight() {
            return blocklight;
        }

        int material() {
            return material;
        }
    }

    private record Fixture(
            TextureGallery gallery,
            BlockNeighborhood neighborhood,
            FramedGeometryRenderer renderer
    ) {
    }

    private record Position(int x, int y, int z) {
    }
}

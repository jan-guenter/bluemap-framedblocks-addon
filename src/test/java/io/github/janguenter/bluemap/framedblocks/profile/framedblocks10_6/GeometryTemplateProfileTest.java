/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockState;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeometryTemplateProfileTest {

    private static final String QUAD = """
            {"layer":"solid","cullFace":"north","direction":"north",
             "component":"primary","sourceFace":"north",
             "atlas":"minecraft:textures/atlas/blocks.png",
             "sprite":"minecraft:block/magenta_concrete","tintIndex":2,"shade":true,
             "ambientOcclusion":true,"modelAmbientOcclusion":"true",
             "effectiveAmbientOcclusionUnderZeroEmission":true,
             "blockLight":2,"skyLight":3,
             "vertices":[{"x":0,"y":0,"z":0,"u":0,"v":0,
                          "blockLight":4,"skyLight":5},
                         {"x":0,"y":1,"z":0,"u":0,"v":1,
                          "blockLight":2,"skyLight":3},
                         {"x":1,"y":1,"z":0,"u":1,"v":1,
                          "blockLight":2,"skyLight":3},
                         {"x":1,"y":0,"z":0,"u":1,"v":0,
                          "blockLight":2,"skyLight":3}]}
            """;

    @Test
    void loadsPinnedSchemaThreeBundledProjection() throws IOException {
        GeometryTemplateProfile profile = GeometryTemplateProfile.loadBundled();

        assertEquals(FramedBlocks1061Profile.CLIENT_RAW_STATE_COUNT, profile.rawStateCount());
        assertEquals(
                FramedBlocks1061Profile.CLIENT_RENDERABLE_STATE_COUNT,
                profile.renderableStateCount()
        );
        assertEquals(FramedBlocks1061Profile.CLIENT_TEMPLATE_COUNT, profile.templateCount());
        assertEquals(FramedBlocks1061Profile.PROJECTED_QUAD_COUNT, profile.quadCount());
        assertEquals(
                FramedBlocks1061Profile.CLIENT_BASE_ROUTED_FAMILY_TEMPLATE_COUNT,
                profile.baseRoutedFamilyTemplateCount()
        );
        assertEquals(
                FramedBlocks1061Profile.PROJECTED_FIXED_SPRITE_COUNT,
                profile.usedSpriteKeys().size()
        );
        assertTrue(profile.usedSpriteKeys().stream().allMatch(key ->
                "minecraft".equals(key.getNamespace())
                        || "framedblocks".equals(key.getNamespace())
        ));
    }

    @Test
    void loadsSchemaTwoAppliedCamoAndLooksUpOnlyExactStateKeys() throws IOException {
        String states = """
                {"blockId":"framedblocks:framed_cube",
                 "properties":{"waterlogged":"false"},"quads":[%s]},
                {"blockId":"framedblocks:framing_saw","properties":{},"quads":[]}
                """.formatted(QUAD);
        GeometryTemplateProfile profile = load(document(2, 1, states));

        assertEquals(2, profile.rawStateCount());
        assertEquals(1, profile.quadCount());
        assertEquals(1, profile.renderedBlockIds().size());
        assertEquals(Set.of(), profile.usedSpriteKeys());
        assertTrue(profile.renderedBlockIds().contains("framedblocks:framed_cube"));
        assertTrue(profile.routedBlockIds().contains("framedblocks:framed_cube"));
        assertFalse(profile.renderedBlockIds().contains("framedblocks:framing_saw"));
        assertTrue(profile.find(new BlockState(
                Key.parse("framedblocks:framed_cube"),
                Map.of("waterlogged", "false")
        )).isPresent());
        assertFalse(profile.find(new BlockState(
                Key.parse("framedblocks:framed_cube"),
                Map.of()
        )).isPresent());

        GeometryTemplateProfile.QuadTemplate quad = profile.find(new BlockState(
                Key.parse("framedblocks:framed_cube"),
                Map.of("waterlogged", "false")
        )).orElseThrow().quads().getFirst();
        assertEquals("primary", quad.component());
        assertEquals("north", quad.sourceFace());
        assertTrue(quad.ambientOcclusion());
        assertTrue(quad.effectiveAmbientOcclusionUnderZeroEmission());
        assertEquals(2, quad.blockLight());
        assertEquals(3, quad.skyLight());
        assertEquals(4, quad.vertices().getFirst().blockLight());
        assertEquals(5, quad.vertices().getFirst().skyLight());
        assertEquals(2, quad.vertices().get(1).blockLight());
        assertEquals(3, quad.vertices().get(1).skyLight());
    }

    @Test
    void tracksOnlyFixedSpritesAsBundledTextureDependencies() throws IOException {
        String fixed = QUAD
                .replace("\"component\":\"primary\"", "\"component\":\"fixed\"")
                .replace("\"sourceFace\":\"north\"", "\"sourceFace\":\"none\"")
                .replace("\"sprite\":\"minecraft:block/magenta_concrete\"",
                        "\"sprite\":\"framedblocks:block/framed_reinforcement\"")
                .replace("\"tintIndex\":2", "\"tintIndex\":-1");
        String state = """
                {"blockId":"framedblocks:framed_cube","properties":{},"quads":[%s]}
                """.formatted(fixed);

        GeometryTemplateProfile profile = load(document(1, 1, state));

        assertEquals(Set.of(Key.parse("framedblocks:block/framed_reinforcement")),
                profile.usedSpriteKeys());
    }

    @Test
    void routesWaterloggedAndLightBearingGeometryButKeepsDynamicModelsOnFallback()
            throws IOException {
        String states = """
                {"blockId":"framedblocks:framed_cube",
                 "properties":{"glowing":"false","propagates_skylight":"false",
                               "waterlogged":"true"},"quads":[%s]},
                {"blockId":"framedblocks:framed_cube",
                 "properties":{"glowing":"false","propagates_skylight":"true",
                               "waterlogged":"false"},"quads":[%s]},
                {"blockId":"framedblocks:framed_cube",
                 "properties":{"glowing":"true","propagates_skylight":"false",
                               "waterlogged":"false"},"quads":[%s]},
                {"blockId":"framedblocks:framed_tank","properties":{},"quads":[]}
                """.formatted(QUAD, QUAD, QUAD);
        GeometryTemplateProfile profile = load(document(4, 3, states));

        FramedBlocks1061Support.Classification waterlogged = profile.support(
                new BlockState(
                        Key.parse("framedblocks:framed_cube"),
                        Map.of(
                                "glowing", "false",
                                "propagates_skylight", "false",
                                "waterlogged", "true"
                        )
                )
        ).orElseThrow();
        assertEquals(FramedBlocks1061Support.Status.SUPPORTED,
                waterlogged.status());
        assertEquals(FramedBlocks1061Support.Family.STATIC_BAKED_MODEL,
                waterlogged.family());
        assertEquals("state-only-baked-model", waterlogged.reason());

        FramedBlocks1061Support.Classification dynamicLight = profile.support(
                new BlockState(
                        Key.parse("framedblocks:framed_cube"),
                        Map.of(
                                "glowing", "true",
                                "propagates_skylight", "false",
                                "waterlogged", "false"
                        )
                )
        ).orElseThrow();
        assertEquals(FramedBlocks1061Support.Status.SUPPORTED,
                dynamicLight.status());
        assertEquals(FramedBlocks1061Support.Family.STATIC_BAKED_MODEL,
                dynamicLight.family());
        assertEquals("state-only-baked-model", dynamicLight.reason());

        FramedBlocks1061Support.Classification dynamicSkylight = profile.support(
                new BlockState(
                        Key.parse("framedblocks:framed_cube"),
                        Map.of(
                                "glowing", "false",
                                "propagates_skylight", "true",
                                "waterlogged", "false"
                        )
                )
        ).orElseThrow();
        assertEquals(FramedBlocks1061Support.Status.SUPPORTED,
                dynamicSkylight.status());
        assertEquals(FramedBlocks1061Support.Family.STATIC_BAKED_MODEL,
                dynamicSkylight.family());
        assertEquals("state-only-baked-model", dynamicSkylight.reason());

        FramedBlocks1061Support.Classification tank = profile.support(
                BlockState.fromString("framedblocks:framed_tank")
        ).orElseThrow();
        assertEquals(FramedBlocks1061Support.Status.STOCK_FALLBACK, tank.status());
        assertEquals(FramedBlocks1061Support.Family.BLOCK_ENTITY_RENDERER, tank.family());
        assertFalse(profile.routedBlockIds().contains("framedblocks:framed_tank"));
    }

    @Test
    void rejectsHeaderSentinelAndCountDrift() {
        String state = """
                {"blockId":"framedblocks:framed_cube","properties":{},"quads":[%s]}
                """.formatted(QUAD);
        String valid = document(1, 1, state);

        assertThrows(IOException.class, () -> load(document(2, 2, state + "," + state)));
        assertThrows(IOException.class, () -> load(document(2, 1, state)));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"schemaVersion\":2", "\"schemaVersion\":1"
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                FramedBlocks1061Profile.CLIENT_CONFIG_SHA256,
                "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                FramedBlocks1061Profile.CLIENT_PACK_FINGERPRINT,
                "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"layer\":\"solid\"",
                "\"layer\":\"RenderType[unstable-debug-description]\""
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"sourceFace\":\"north\"", "\"sourceFace\":\"south\""
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"blockCount\":1", "\"blockCount\":2"
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"raw_state_count\":1", "\"raw_state_count\":100001"
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"renderable_state_count\":1", "\"renderable_state_count\":0"
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"template_count\":1", "\"template_count\":0"
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"template_count\":1", "\"template_count\":20001"
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"quadCount\":1", "\"quadCount\":0"
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"quadCount\":1", "\"quadCount\":10000001"
        )));
    }

    @Test
    void stateDigestUsesCanonicalStateAndPropertyOrdering() throws IOException {
        String firstOrder = """
                {"blockId":"framedblocks:framed_cube",
                 "properties":{"b":"2","a":"1"},"quads":[%s]},
                {"blockId":"framedblocks:framing_saw","properties":{},"quads":[]}
                """.formatted(QUAD);
        String secondOrder = """
                {"blockId":"framedblocks:framing_saw","properties":{},"quads":[]},
                {"blockId":"framedblocks:framed_cube",
                 "properties":{"a":"1","b":"2"},"quads":[%s]}
                """.formatted(QUAD);
        String expected = "8b4534fbb047d8926af4a7b172fd8ac38c0c66f285e166694bb8ed6fbb79a24c";

        assertEquals(expected, stateKeysSha256(firstOrder));
        assertEquals(expected, stateKeysSha256(secondOrder));
        assertEquals(2, load(document(2, 1, firstOrder)).rawStateCount());
        assertEquals(2, load(document(2, 1, secondOrder)).rawStateCount());
    }

    @Test
    void matchesExporterGoldenStateAndAliasByteStreams() {
        assertEquals(
                "ce492aeea6c2be243896cf55b2168eb4a62875320144d0d668703415c6a253ed",
                sha256OrderedLines(List.of(
                        "framedblocks:framed_cube",
                        "framedblocks:framed_cube\0p=first\0p0=second"
                ))
        );
        assertEquals(
                "73978243793357f6a55c6284cf86312b2c5ae871f3105d5708d351fba42eeff1",
                sha256OrderedLines(List.of(
                        "framedblocks:alpha\0x=1",
                        "framedblocks:alpha\0x=0",
                        "framedblocks:beta",
                        "framedblocks:beta"
                ))
        );
        assertEquals(
                "01ba4719c80b6fe911b091a7c05124b64eeece964e09c058ef8f9805daca546b",
                sha256OrderedLines(List.of())
        );
    }

    @Test
    void rejectsMissingMalformedAndMismatchedSchemaDigests() {
        String states = """
                {"blockId":"framedblocks:framed_cube","properties":{},"quads":[%s]}
                """.formatted(QUAD);
        JsonObject valid = JsonParser.parseString(document(1, 1, states)).getAsJsonObject();

        for (String field : List.of(
                "raw_state_keys_sha256",
                "renderable_state_keys_sha256",
                "template_state_keys_sha256",
                "alias_pairs_sha256"
        )) {
            JsonObject missing = valid.deepCopy();
            missing.remove(field);
            assertThrows(IOException.class, () -> load(missing.toString()), field);

            JsonObject malformed = valid.deepCopy();
            malformed.addProperty(field, "not-a-sha256");
            assertThrows(IOException.class, () -> load(malformed.toString()), field);

            JsonObject mismatched = valid.deepCopy();
            mismatched.addProperty(field, "f".repeat(64));
            assertThrows(IOException.class, () -> load(mismatched.toString()), field);
        }

        JsonObject camelCase = valid.deepCopy();
        camelCase.add("rawStateKeysSha256", camelCase.remove("raw_state_keys_sha256"));
        assertThrows(IOException.class, () -> load(camelCase.toString()));
    }

    @Test
    void stateDigestSeparatesInventoriesThatCollideOnBlockInventory() throws IOException {
        String firstStates = """
                {"blockId":"framedblocks:framed_cube",
                 "properties":{"shape":"a"},"quads":[%s]},
                {"blockId":"framedblocks:framed_cube",
                 "properties":{"shape":"b"},"quads":[%s]}
                """.formatted(QUAD, QUAD);
        String secondStates = firstStates.replace("\"shape\":\"b\"", "\"shape\":\"c\"");
        String firstDigest = stateKeysSha256(firstStates);
        String secondDigest = stateKeysSha256(secondStates);

        assertNotEquals(firstDigest, secondDigest);
        assertEquals(2, load(document(2, 2, firstStates)).rawStateCount());
        assertEquals(2, load(document(2, 2, secondStates)).rawStateCount());

        String crossedDigest = document(2, 2, secondStates)
                .replace(secondDigest, firstDigest);
        assertThrows(IOException.class, () -> load(crossedDigest));
    }

    @Test
    void rejectsStateKeyMutationWithoutDigestUpdate() throws IOException {
        String states = """
                {"blockId":"framedblocks:framed_cube",
                 "properties":{"facing":"north"},"quads":[%s]}
                """.formatted(QUAD);
        String mutatedStates = states.replace("\"facing\":\"north\"", "\"facing\":\"south\"");

        assertEquals(1, load(document(1, 1, states)).rawStateCount());
        assertEquals(1, load(document(1, 1, mutatedStates)).rawStateCount());
        assertThrows(IOException.class, () -> load(document(1, 1, states).replace(
                "\"facing\":\"north\"",
                "\"facing\":\"south\""
        )));
    }

    @Test
    void resolvesManyRawStatesToOneRepresentativeByExactKeyOnly() throws IOException {
        String states = """
                {"blockId":"framedblocks:framed_cube",
                 "properties":{"shape":"a"},"quads":[%s]},
                {"blockId":"framedblocks:framed_cube",
                 "properties":{"shape":"b"},"quads":[%s]}
                """.formatted(QUAD, QUAD);
        JsonObject aliased = jsonDocument(2, 2, states);
        aliased.getAsJsonArray("templates").remove(1);
        aliased.getAsJsonArray("states").get(1).getAsJsonObject()
                .addProperty("template", 0);
        recomputeSchemaHeader(aliased);

        GeometryTemplateProfile profile = load(aliased.toString());
        assertEquals(2, profile.rawStateCount());
        assertEquals(2, profile.renderableStateCount());
        assertEquals(1, profile.templateCount());
        assertEquals(
                Map.of("shape", "a"),
                profile.find(new BlockState(
                        Key.parse("framedblocks:framed_cube"),
                        Map.of("shape", "b")
                )).orElseThrow().properties()
        );
        assertFalse(profile.find(BlockState.fromString("framedblocks:framed_cube")).isPresent());
        assertFalse(profile.find(new BlockState(
                Key.parse("framedblocks:framed_cube"),
                Map.of("shape", "unknown")
        )).isPresent());
    }

    @Test
    void rejectsAliasGraphCorruptionWithFreshHeaders() {
        String sameBlockStates = """
                {"blockId":"framedblocks:framed_cube",
                 "properties":{"shape":"a"},"quads":[%s]},
                {"blockId":"framedblocks:framed_cube",
                 "properties":{"shape":"b"},"quads":[%s]}
                """.formatted(QUAD, QUAD);

        JsonObject orphan = jsonDocument(2, 2, sameBlockStates);
        orphan.getAsJsonArray("states").get(1).getAsJsonObject()
                .addProperty("template", 0);
        recomputeSchemaHeader(orphan);
        assertThrows(IOException.class, () -> load(orphan.toString()));

        JsonObject missingSelfAliases = jsonDocument(2, 2, sameBlockStates);
        missingSelfAliases.getAsJsonArray("states").get(0).getAsJsonObject()
                .addProperty("template", 1);
        missingSelfAliases.getAsJsonArray("states").get(1).getAsJsonObject()
                .addProperty("template", 0);
        recomputeSchemaHeader(missingSelfAliases);
        assertThrows(IOException.class, () -> load(missingSelfAliases.toString()));

        String crossBlockStates = """
                {"blockId":"framedblocks:framed_cube","properties":{},"quads":[%s]},
                {"blockId":"framedblocks:framed_tank","properties":{},"quads":[]}
                """.formatted(QUAD);
        JsonObject crossBlock = jsonDocument(2, 1, crossBlockStates);
        crossBlock.getAsJsonArray("states").get(0).getAsJsonObject()
                .addProperty("template", 1);
        recomputeSchemaHeader(crossBlock);
        assertThrows(IOException.class, () -> load(crossBlock.toString()));
    }

    @Test
    void rejectsNonCanonicalAndDuplicatePhysicalOrderingWithFreshHeaders() {
        String states = """
                {"blockId":"framedblocks:framed_cube",
                 "properties":{"shape":"a"},"quads":[%s]},
                {"blockId":"framedblocks:framed_cube",
                 "properties":{"shape":"b"},"quads":[%s]}
                """.formatted(QUAD, QUAD);

        JsonObject templatesReversed = jsonDocument(2, 2, states);
        reverse(templatesReversed.getAsJsonArray("templates"));
        recomputeSchemaHeader(templatesReversed);
        assertThrows(IOException.class, () -> load(templatesReversed.toString()));

        JsonObject statesReversed = jsonDocument(2, 2, states);
        reverse(statesReversed.getAsJsonArray("states"));
        recomputeSchemaHeader(statesReversed);
        assertThrows(IOException.class, () -> load(statesReversed.toString()));

        JsonObject duplicateTemplate = jsonDocument(2, 2, states);
        duplicateTemplate.getAsJsonArray("templates").add(
                duplicateTemplate.getAsJsonArray("templates").get(1).deepCopy()
        );
        recomputeSchemaHeader(duplicateTemplate);
        assertThrows(IOException.class, () -> load(duplicateTemplate.toString()));

        JsonObject duplicateState = jsonDocument(2, 2, states);
        duplicateState.getAsJsonArray("states").add(
                duplicateState.getAsJsonArray("states").get(1).deepCopy()
        );
        recomputeSchemaHeader(duplicateState);
        assertThrows(IOException.class, () -> load(duplicateState.toString()));
    }

    @Test
    void distinguishesExplicitNullFromMissingAndRejectsInvalidTemplateTypes() throws IOException {
        String state = """
                {"blockId":"framedblocks:framed_cube","properties":{},"quads":[%s]}
                """.formatted(QUAD);
        JsonObject valid = jsonDocument(1, 1, state);

        JsonObject omitted = valid.deepCopy();
        omitted.getAsJsonArray("states").get(0).getAsJsonObject().remove("template");
        assertThrows(IOException.class, () -> load(omitted.toString()));

        for (JsonElement invalid : List.of(
                new com.google.gson.JsonPrimitive("0"),
                new com.google.gson.JsonPrimitive(true),
                new com.google.gson.JsonPrimitive(0.5),
                new com.google.gson.JsonPrimitive(-1),
                new com.google.gson.JsonPrimitive(1),
                new com.google.gson.JsonPrimitive(2_147_483_648L)
        )) {
            JsonObject malformed = valid.deepCopy();
            malformed.getAsJsonArray("states").get(0).getAsJsonObject()
                    .add("template", invalid);
            assertThrows(IOException.class, () -> load(malformed.toString()), invalid.toString());
        }

        JsonObject nullNonSaw = valid.deepCopy();
        nullNonSaw.getAsJsonArray("states").get(0).getAsJsonObject()
                .add("template", JsonNull.INSTANCE);
        recomputeSchemaHeader(nullNonSaw);
        assertThrows(IOException.class, () -> load(nullNonSaw.toString()));

        String saw = """
                {"blockId":"framedblocks:framing_saw","properties":{},"quads":[]}
                """;
        GeometryTemplateProfile sawOnly = load(document(1, 0, saw));
        assertEquals(1, sawOnly.rawStateCount());
        assertEquals(0, sawOnly.renderableStateCount());
        assertEquals(0, sawOnly.templateCount());
        assertFalse(sawOnly.find(BlockState.fromString("framedblocks:framing_saw")).isPresent());

        JsonObject sawTemplate = jsonDocument(1, 0, saw);
        JsonObject template = sawTemplate.getAsJsonArray("states").get(0)
                .getAsJsonObject().deepCopy();
        template.remove("template");
        template.add("quads", new JsonArray());
        sawTemplate.getAsJsonArray("templates").add(template);
        sawTemplate.getAsJsonArray("states").get(0).getAsJsonObject()
                .addProperty("template", 0);
        recomputeSchemaHeader(sawTemplate);
        assertThrows(IOException.class, () -> load(sawTemplate.toString()));
    }

    @Test
    void rejectsAmbiguousCanonicalStatePropertyDelimiters() {
        for (String properties : List.of(
                "{\"a=b\":\"c\"}",
                "{\"a\":\"b=c\"}",
                "{\"\":\"value\"}",
                "{\"name\":\"\"}",
                "{\"has space\":\"value\"}",
                "{\"a\":\"b\\u0000c=d\"}",
                "{\"a\":\"line\\nbreak\"}",
                "{\"a\":\"carriage\\rreturn\"}",
                "{\"a\":\"non-ascii-\\u0080\"}"
        )) {
            String state = """
                    {"blockId":"framedblocks:framed_cube",
                     "properties":%s,"quads":[%s]}
                    """.formatted(properties, QUAD);
            assertThrows(IOException.class, () -> load(document(1, 1, state)));
        }
    }

    @Test
    void rejectsOmittedRequiredSchemaTwoFieldsAndContradictoryAo() {
        String state = """
                {"blockId":"framedblocks:framed_cube","properties":{},"quads":[%s]}
                """.formatted(QUAD);
        String valid = document(1, 1, state);

        assertThrows(IOException.class, () -> load(valid.replace(
                "\"dynamicModelDataIncluded\":false,", ""
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"blockEntityRendererContentIncluded\":false,", ""
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"tintIndex\":2,", ""
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"shade\":true,", ""
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"ambientOcclusion\":true,", ""
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"effectiveAmbientOcclusionUnderZeroEmission\":true,", ""
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"blockLight\":4,", ""
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"x\":0,", ""
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"modelAmbientOcclusion\":\"true\"",
                "\"modelAmbientOcclusion\":\"false\""
        )));
        assertThrows(IOException.class, () -> load(valid.replace(
                "\"effectiveAmbientOcclusionUnderZeroEmission\":true",
                "\"effectiveAmbientOcclusionUnderZeroEmission\":false"
        )));
    }

    @Test
    void rejectsUnsupportedFixedTintsAndEmptyRoutedGeometry() throws IOException {
        String untinted = fixedQuad(-1);
        String targetOverlay = fixedQuad(1_024);
        String fixedState = """
                {"blockId":"framedblocks:framed_cube","properties":{},
                 "quads":[%s,%s]}
                """.formatted(untinted, targetOverlay);

        assertEquals(2, load(document(1, 2, fixedState)).quadCount());
        assertThrows(IOException.class, () -> load(document(
                1,
                1,
                """
                        {"blockId":"framedblocks:framed_cube","properties":{},
                         "quads":[%s]}
                        """.formatted(fixedQuad(0))
        )));
        assertThrows(IOException.class, () -> load(document(
                1,
                0,
                """
                        {"blockId":"framedblocks:framed_cube",
                         "properties":{},"quads":[]}
                        """
        )));

        String explicitFallback = """
                {"blockId":"framedblocks:framed_tank","properties":{},"quads":[]}
                """;
        assertEquals(0, load(document(1, 0, explicitFallback)).quadCount());

        for (String property : Set.of("waterlogged", "glowing", "propagates_skylight")) {
            String emptyRoutedState = """
                    {"blockId":"framedblocks:framed_cube",
                     "properties":{"%s":"true"},"quads":[]}
                    """.formatted(property);
            assertThrows(IOException.class, () -> load(document(1, 0, emptyRoutedState)));
            String routedState = emptyRoutedState.replace(
                    "\"quads\":[]",
                    "\"quads\":[" + QUAD + "]"
            );
            assertEquals(1, load(document(1, 1, routedState)).quadCount());
        }
        assertThrows(IOException.class, () -> load(document(
                1,
                0,
                """
                        {"blockId":"framedblocks:framed_glowing_cube",
                         "properties":{"glowing":"true"},"quads":[]}
                        """
        )));
    }

    private static String fixedQuad(int tintIndex) {
        return QUAD
                .replace("\"component\":\"primary\"", "\"component\":\"fixed\"")
                .replace("\"sourceFace\":\"north\"", "\"sourceFace\":\"none\"")
                .replace("\"sprite\":\"minecraft:block/magenta_concrete\"",
                        "\"sprite\":\"framedblocks:block/framed_reinforcement\"")
                .replace("\"tintIndex\":2", "\"tintIndex\":" + tintIndex);
    }

    private static GeometryTemplateProfile load(String json) throws IOException {
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(compressed)) {
            gzip.write(json.getBytes(StandardCharsets.UTF_8));
        }
        return GeometryTemplateProfile.loadGzip(new ByteArrayInputStream(compressed.toByteArray()));
    }

    private static JsonObject jsonDocument(int stateCount, int quadCount, String states) {
        return JsonParser.parseString(document(stateCount, quadCount, states)).getAsJsonObject();
    }

    private static void reverse(JsonArray array) {
        List<JsonElement> original = new ArrayList<>();
        for (JsonElement element : array) {
            original.add(element.deepCopy());
        }
        while (!array.isEmpty()) {
            array.remove(array.size() - 1);
        }
        for (int index = original.size() - 1; index >= 0; index--) {
            array.add(original.get(index));
        }
    }

    private static void recomputeSchemaHeader(JsonObject document) {
        JsonArray templates = document.getAsJsonArray("templates");
        JsonArray states = document.getAsJsonArray("states");
        List<String> templateKeys = new ArrayList<>();
        int quadCount = 0;
        for (JsonElement element : templates) {
            JsonObject template = element.getAsJsonObject();
            templateKeys.add(canonicalStateKey(template));
            quadCount += template.getAsJsonArray("quads").size();
        }

        Set<String> blockIds = new HashSet<>();
        List<String> rawKeys = new ArrayList<>();
        List<String> renderableKeys = new ArrayList<>();
        List<Map.Entry<String, String>> pairs = new ArrayList<>();
        for (JsonElement element : states) {
            JsonObject state = element.getAsJsonObject();
            String rawKey = canonicalStateKey(state);
            blockIds.add(state.get("blockId").getAsString());
            rawKeys.add(rawKey);
            JsonElement template = state.get("template");
            if (template != null && !template.isJsonNull()) {
                String representative = templateKeys.get(template.getAsInt());
                renderableKeys.add(rawKey);
                pairs.add(Map.entry(rawKey, representative));
            }
        }

        rawKeys.sort(String::compareTo);
        renderableKeys.sort(String::compareTo);
        templateKeys.sort(String::compareTo);
        pairs.sort(Map.Entry.comparingByKey());
        List<String> aliasPairLines = new ArrayList<>(pairs.size() * 2);
        for (Map.Entry<String, String> pair : pairs) {
            aliasPairLines.add(pair.getKey());
            aliasPairLines.add(pair.getValue());
        }

        document.addProperty("blockCount", blockIds.size());
        document.addProperty("blockIdsSha256", sha256Lines(blockIds));
        document.addProperty("raw_state_count", states.size());
        document.addProperty("raw_state_keys_sha256", sha256OrderedLines(rawKeys));
        document.addProperty("renderable_state_count", renderableKeys.size());
        document.addProperty(
                "renderable_state_keys_sha256",
                sha256OrderedLines(renderableKeys)
        );
        document.addProperty("template_count", templates.size());
        document.addProperty(
                "template_state_keys_sha256",
                sha256OrderedLines(templateKeys)
        );
        document.addProperty("alias_pairs_sha256", sha256OrderedLines(aliasPairLines));
        document.addProperty("quadCount", quadCount);
    }

    private static String document(int stateCount, int quadCount, String states) {
        List<JsonObject> rawStates = new ArrayList<>();
        for (JsonElement element : JsonParser.parseString("[" + states + "]").getAsJsonArray()) {
            rawStates.add(element.getAsJsonObject());
        }
        rawStates.sort(Comparator.comparing(GeometryTemplateProfileTest::canonicalStateKey));

        JsonArray templates = new JsonArray();
        Map<String, Integer> templateIndices = new java.util.HashMap<>();
        for (JsonObject state : rawStates) {
            String blockId = state.get("blockId").getAsString();
            if (isSaw(blockId)) {
                continue;
            }
            String key = canonicalStateKey(state);
            templateIndices.putIfAbsent(key, templates.size());
            templates.add(state.deepCopy());
        }

        JsonArray aliases = new JsonArray();
        Set<String> blockIds = new HashSet<>();
        List<String> rawStateKeys = new ArrayList<>();
        List<String> renderableStateKeys = new ArrayList<>();
        List<String> templateStateKeys = new ArrayList<>();
        List<String> aliasPairLines = new ArrayList<>();
        for (JsonElement template : templates) {
            templateStateKeys.add(canonicalStateKey(template.getAsJsonObject()));
        }
        for (JsonObject state : rawStates) {
            String blockId = state.get("blockId").getAsString();
            String key = canonicalStateKey(state);
            JsonObject alias = new JsonObject();
            alias.addProperty("blockId", blockId);
            alias.add("properties", state.getAsJsonObject("properties").deepCopy());
            Integer templateIndex = templateIndices.get(key);
            if (templateIndex == null) {
                alias.add("template", JsonNull.INSTANCE);
            } else {
                alias.addProperty("template", templateIndex);
                String representative = templateStateKeys.get(templateIndex);
                renderableStateKeys.add(key);
                aliasPairLines.add(key);
                aliasPairLines.add(representative);
            }
            aliases.add(alias);
            blockIds.add(blockId);
            rawStateKeys.add(key);
        }
        String blockIdsSha256 = sha256Lines(blockIds);
        return """
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
                 "framedBlocksJarSha256":"3337f29e1fa3331e8740eef9c20b0750d81fd86d1057fb81012a5c4792aa3369",
                 "framedBlocksJarBytes":4306703,
                 "framedBlocksClientConfigSha256":"02e7e1c004fc6a15247dd0ddb5c5210a9e0cc901f18f85ad689886eae3d3ea83",
                 "minecraftVersion":"1.21.1","neoForgeVersion":"21.1.234",
                 "packFingerprint":"30d715deacda85316240c9a0f67ccc457f7a00667168dbffb82e16c82ddbcf42",
                 "modsSha256":"7f0d771cf2c1dc430fa32153651dd9bec5ae5492f34a6c8a7bef8a067f5d50a7",
                 "resourcePacksOrderedSha256":"b345c2cfa4743c2a46c5a3ddf1817ca601a1eb91ff446e8a58e6cd0da3e8ed3d",
                 "resourcePackIdSetSha256":"1952cce499adb9e79cce0a422418537b3818ef353f100424692bb5dc94958be5",
                 "modCount":430,"resourcePacksOrderedCount":439,
                 "resourcePackIdSetCount":12,"blockCount":%d,
                 "blockIdsSha256":"%s",
                 "raw_state_count":%d,"raw_state_keys_sha256":"%s",
                 "renderable_state_count":%d,
                 "renderable_state_keys_sha256":"%s",
                 "template_count":%d,"template_state_keys_sha256":"%s",
                 "alias_pairs_sha256":"%s","quadCount":%d,
                 "nonCamoUtilityBlocks":["framedblocks:framing_saw",
                                         "framedblocks:powered_framing_saw"],
                 "excludedDynamicInputs":["world_block_entity_model_data",
                    "connected_texture_context","geometry_aux_model_data",
                    "flower_pot_contents","collapsible_and_copycat_offsets",
                    "adjustable_double_component_offsets","one_way_window_context",
                    "hidden_face_masks","block_entity_renderer_content"],
                 "templates":%s,"states":%s}
                """.formatted(
                blockIds.size(),
                blockIdsSha256,
                stateCount,
                sha256OrderedLines(rawStateKeys),
                renderableStateKeys.size(),
                sha256OrderedLines(renderableStateKeys),
                templates.size(),
                sha256OrderedLines(templateStateKeys),
                sha256OrderedLines(aliasPairLines),
                quadCount,
                templates,
                aliases
        );
    }

    private static String stateKeysSha256(String states) {
        List<String> stateKeys = new ArrayList<>();
        for (JsonElement element : JsonParser.parseString("[" + states + "]").getAsJsonArray()) {
            stateKeys.add(canonicalStateKey(element.getAsJsonObject()));
        }
        stateKeys.sort(String::compareTo);
        return sha256OrderedLines(stateKeys);
    }

    private static String canonicalStateKey(JsonObject state) {
        StringBuilder canonical = new StringBuilder(state.get("blockId").getAsString());
        state.getAsJsonObject("properties").entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(property -> canonical
                        .append('\0')
                        .append(property.getKey())
                        .append('=')
                        .append(property.getValue().getAsString()));
        return canonical.toString();
    }

    private static boolean isSaw(String blockId) {
        return "framedblocks:framing_saw".equals(blockId)
                || "framedblocks:powered_framing_saw".equals(blockId);
    }

    private static String sha256Lines(Set<String> values) {
        return sha256OrderedLines(values.stream().sorted().toList());
    }

    private static String sha256OrderedLines(List<String> values) {
        String canonical = String.join("\n", values) + "\n";
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(
                    canonical.getBytes(StandardCharsets.UTF_8)
            ));
        } catch (NoSuchAlgorithmException exception) {
            throw new AssertionError(exception);
        }
    }
}

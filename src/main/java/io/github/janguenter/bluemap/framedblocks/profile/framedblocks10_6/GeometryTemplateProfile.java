/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockState;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/** Validated neutral mesh profile exported by the exact 10.6.1 client. */
public final class GeometryTemplateProfile {

    public static final String RESOURCE =
            "/bluemap-framedblocks/profiles/10.6.1/geometry-templates.json.gz";

    private static final int RAW_SCHEMA_VERSION = 2;
    private static final int PROJECTED_SCHEMA_VERSION = 3;
    private static final long MAX_COMPRESSED_BYTES = 16L * 1024L * 1024L;
    private static final int MAX_BLOCKS = 1_024;
    private static final int MAX_RAW_STATES = 100_000;
    private static final int MAX_TEMPLATES = 20_000;
    private static final int MAX_QUADS = 10_000_000;
    private static final long MAX_UNCOMPRESSED_BYTES = 256L * 1024L * 1024L;
    private static final Pattern RESOURCE_ID = Pattern.compile(
            "[a-z0-9_.-]+:[a-z0-9_./-]+"
    );
    private static final Pattern SHA256 = Pattern.compile("[0-9a-f]{64}");
    private static final Pattern INTEGER = Pattern.compile("-?(0|[1-9][0-9]*)");
    private static final Set<String> DIRECTIONS = Set.of(
            "down", "up", "north", "south", "west", "east"
    );
    private static final Set<String> LAYERS = Set.of(
            "solid", "cutout_mipped", "cutout", "translucent", "tripwire"
    );
    private static final Set<String> COMPONENTS = Set.of(
            "primary", "secondary", "fixed"
    );
    private static final Set<String> MODEL_AMBIENT_OCCLUSION = Set.of(
            "true", "false", "default"
    );
    private static final String BLOCK_ATLAS = "minecraft:textures/atlas/blocks.png";
    private static final Map<String, String> CAMO_SENTINEL_SPRITES = Map.of(
            "down", "minecraft:block/white_concrete",
            "up", "minecraft:block/orange_concrete",
            "north", "minecraft:block/magenta_concrete",
            "south", "minecraft:block/light_blue_concrete",
            "west", "minecraft:block/yellow_concrete",
            "east", "minecraft:block/lime_concrete"
    );
    private static final Map<String, Integer> CAMO_SENTINEL_ORDINALS = Map.of(
            "down", 0,
            "up", 1,
            "north", 2,
            "south", 3,
            "west", 4,
            "east", 5
    );
    private static final List<String> NON_CAMO_UTILITY_BLOCKS = List.of(
            "framedblocks:framing_saw",
            "framedblocks:powered_framing_saw"
    );
    private static final List<String> EXCLUDED_DYNAMIC_INPUTS = List.of(
            "world_block_entity_model_data",
            "connected_texture_context",
            "geometry_aux_model_data",
            "flower_pot_contents",
            "collapsible_and_copycat_offsets",
            "adjustable_double_component_offsets",
            "one_way_window_context",
            "hidden_face_masks",
            "block_entity_renderer_content"
    );

    private final List<StateTemplate> templates;
    private final Map<StateKey, StateAliasEntry> rawStates;
    private final Set<String> renderedBlockIds;
    private final Set<String> routedBlockIds;
    private final Map<String, FramedBlocks1061Support.Classification> supportByBlockId;
    private final Set<Key> usedSpriteKeys;
    private final Map<String, Integer> nullAliasCounts;
    private final int renderableStateCount;
    private final int baseRoutedFamilyTemplateCount;
    private final int quadCount;

    private GeometryTemplateProfile(
            List<StateTemplate> templates,
            Map<StateKey, StateAliasEntry> rawStates,
            Set<String> renderedBlockIds,
            Set<String> routedBlockIds,
            Map<String, FramedBlocks1061Support.Classification> supportByBlockId,
            Set<Key> usedSpriteKeys,
            Map<String, Integer> nullAliasCounts,
            int renderableStateCount,
            int baseRoutedFamilyTemplateCount,
            int quadCount
    ) {
        this.templates = List.copyOf(templates);
        this.rawStates = Map.copyOf(rawStates);
        this.renderedBlockIds = Set.copyOf(renderedBlockIds);
        this.routedBlockIds = Set.copyOf(routedBlockIds);
        this.supportByBlockId = Map.copyOf(supportByBlockId);
        this.usedSpriteKeys = Set.copyOf(usedSpriteKeys);
        this.nullAliasCounts = Map.copyOf(nullAliasCounts);
        this.renderableStateCount = renderableStateCount;
        this.baseRoutedFamilyTemplateCount = baseRoutedFamilyTemplateCount;
        this.quadCount = quadCount;
    }

    public static GeometryTemplateProfile loadBundled() throws IOException {
        InputStream input = GeometryTemplateProfile.class.getResourceAsStream(RESOURCE);
        if (input == null) {
            throw new IOException("Bundled FramedBlocks geometry profile is missing");
        }
        try (input) {
            byte[] compressed = readBounded(
                    input,
                    FramedBlocks1061Profile.PROJECTED_EXPORT_GZIP_BYTES
            );
            verifyPayload(
                    compressed,
                    FramedBlocks1061Profile.PROJECTED_EXPORT_GZIP_BYTES,
                    FramedBlocks1061Profile.PROJECTED_EXPORT_GZIP_SHA256,
                    "Bundled FramedBlocks geometry profile gzip"
            );
            byte[] uncompressed = decompress(
                    compressed,
                    FramedBlocks1061Profile.PROJECTED_EXPORT_UNCOMPRESSED_BYTES
            );
            verifyPayload(
                    uncompressed,
                    FramedBlocks1061Profile.PROJECTED_EXPORT_UNCOMPRESSED_BYTES,
                    FramedBlocks1061Profile.PROJECTED_EXPORT_UNCOMPRESSED_SHA256,
                    "Bundled FramedBlocks geometry profile JSON"
            );
            return loadJson(uncompressed, ValidationMode.PROJECTED_EXACT);
        }
    }

    public static GeometryTemplateProfile loadGzip(InputStream compressed) throws IOException {
        Objects.requireNonNull(compressed, "compressed");
        try (InputStream boundedCompressed = new BoundedInputStream(
                compressed,
                MAX_COMPRESSED_BYTES
        ); InputStream gzip = new GZIPInputStream(boundedCompressed)) {
            return loadJson(
                    readBounded(gzip, MAX_UNCOMPRESSED_BYTES),
                    ValidationMode.FLEXIBLE
            );
        }
    }

    static GeometryTemplateProfile validateRawExport(byte[] compressed) throws IOException {
        Objects.requireNonNull(compressed, "compressed");
        verifyPayload(
                compressed,
                FramedBlocks1061Profile.SOURCE_EXPORT_GZIP_BYTES,
                FramedBlocks1061Profile.SOURCE_EXPORT_GZIP_SHA256,
                "FramedBlocks source geometry export gzip"
        );
        byte[] uncompressed = decompress(
                compressed,
                FramedBlocks1061Profile.SOURCE_EXPORT_UNCOMPRESSED_BYTES
        );
        verifyPayload(
                uncompressed,
                FramedBlocks1061Profile.SOURCE_EXPORT_UNCOMPRESSED_BYTES,
                FramedBlocks1061Profile.SOURCE_EXPORT_UNCOMPRESSED_SHA256,
                "FramedBlocks source geometry export JSON"
        );
        return loadJson(uncompressed, ValidationMode.RAW_EXACT);
    }

    static GeometryTemplateProfile validateProjectedExport(byte[] compressed) throws IOException {
        Objects.requireNonNull(compressed, "compressed");
        if (compressed.length > MAX_COMPRESSED_BYTES) {
            throw new IOException("Projected FramedBlocks geometry profile exceeds the gzip bound");
        }
        return loadJson(
                decompress(compressed, MAX_UNCOMPRESSED_BYTES),
                ValidationMode.PROJECTED_EXACT
        );
    }

    private static GeometryTemplateProfile loadJson(
            byte[] uncompressed,
            ValidationMode mode
    ) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(
                new ByteArrayInputStream(uncompressed),
                StandardCharsets.UTF_8
        )) {
            RawExportDocument raw = new Gson().fromJson(reader, RawExportDocument.class);
            validateRequiredFields(raw);
            ExportDocument document = convert(raw);
            GeometryTemplateProfile profile = validate(document, mode);
            if (mode == ValidationMode.RAW_EXACT) {
                validateExactInventory(
                        document,
                        profile,
                        FramedBlocks1061Profile.SOURCE_EXPORT_QUAD_COUNT,
                        false
                );
            } else if (mode == ValidationMode.PROJECTED_EXACT) {
                validateExactInventory(
                        document,
                        profile,
                        FramedBlocks1061Profile.PROJECTED_QUAD_COUNT,
                        true
                );
            }
            return profile;
        } catch (JsonParseException | IllegalArgumentException exception) {
            throw new IOException("FramedBlocks geometry profile JSON is invalid", exception);
        }
    }

    private static void validateExactInventory(
            ExportDocument document,
            GeometryTemplateProfile profile,
            int expectedQuadCount,
            boolean projected
    ) throws IOException {
        if (document.blockCount() != FramedBlocks1061Profile.CLIENT_BLOCK_COUNT
                || !FramedBlocks1061Profile.BLOCK_STATE_IDS_SHA256.equals(
                        document.blockIdsSha256()
                )
                || document.rawStateCount()
                        != FramedBlocks1061Profile.CLIENT_RAW_STATE_COUNT
                || !FramedBlocks1061Profile.CLIENT_RAW_STATE_KEYS_SHA256.equals(
                        document.rawStateKeysSha256()
                )
                || document.renderableStateCount()
                        != FramedBlocks1061Profile.CLIENT_RENDERABLE_STATE_COUNT
                || !FramedBlocks1061Profile.CLIENT_RENDERABLE_STATE_KEYS_SHA256.equals(
                        document.renderableStateKeysSha256()
                )
                || document.templateCount()
                        != FramedBlocks1061Profile.CLIENT_TEMPLATE_COUNT
                || !FramedBlocks1061Profile.CLIENT_TEMPLATE_STATE_KEYS_SHA256.equals(
                        document.templateStateKeysSha256()
                )
                || !FramedBlocks1061Profile.CLIENT_ALIAS_PAIRS_SHA256.equals(
                        document.aliasPairsSha256()
                )
                || document.quadCount() != expectedQuadCount
                || profile.baseRoutedFamilyTemplateCount()
                        != FramedBlocks1061Profile.CLIENT_BASE_ROUTED_FAMILY_TEMPLATE_COUNT
                || document.rawStateCount() - document.renderableStateCount()
                        != FramedBlocks1061Profile.CLIENT_NULL_ALIAS_COUNT
                || profile.nullAliasCounts().size() != NON_CAMO_UTILITY_BLOCKS.size()
                || !NON_CAMO_UTILITY_BLOCKS.stream().allMatch(blockId ->
                        profile.nullAliasCounts().getOrDefault(blockId, 0)
                                == FramedBlocks1061Profile.CLIENT_NULL_ALIAS_COUNT_PER_SAW
                )
                || (projected && !hasExactProjectedGeometry(document, profile))) {
            throw new IOException("Bundled FramedBlocks geometry inventory is incompatible");
        }
    }

    private static boolean hasExactProjectedGeometry(
            ExportDocument document,
            GeometryTemplateProfile profile
    ) throws IOException {
        Set<String> excludedBlockIds = FramedBlocks1061Support.stockFallbackBlockIds();
        int excludedTemplates = 0;
        for (StateTemplate template : document.templates()) {
            if (excludedBlockIds.contains(template.blockId())) {
                excludedTemplates++;
                if (!template.quads().isEmpty()) {
                    return false;
                }
            }
        }
        if (excludedTemplates != FramedBlocks1061Profile.EXCLUDED_GEOMETRY_TEMPLATE_COUNT
                || !FramedBlocks1061Profile.EXCLUDED_GEOMETRY_BLOCK_IDS_SHA256.equals(
                        sha256Lines(excludedBlockIds)
                )
                || profile.usedSpriteKeys().size()
                        != FramedBlocks1061Profile.PROJECTED_FIXED_SPRITE_COUNT
                || !FramedBlocks1061Profile.PROJECTED_FIXED_SPRITES_SHA256.equals(
                        sha256Lines(profile.usedSpriteKeys().stream()
                                .map(Key::getFormatted)
                                .collect(java.util.stream.Collectors.toSet()))
                )) {
            return false;
        }
        return profile.usedSpriteKeys().stream().allMatch(key ->
                "minecraft".equals(key.getNamespace())
                        || "framedblocks".equals(key.getNamespace())
        );
    }

    public Optional<StateTemplate> find(BlockState state) {
        return resolve(state)
                .filter(resolution -> resolution.templateIndex() >= 0)
                .map(resolution -> templates.get(resolution.templateIndex()));
    }

    public Set<String> renderedBlockIds() {
        return renderedBlockIds;
    }

    public Set<String> routedBlockIds() {
        return routedBlockIds;
    }

    public Optional<FramedBlocks1061Support.Classification> support(BlockState state) {
        return resolve(state).map(StateResolution::classification);
    }

    private Optional<StateResolution> resolve(BlockState state) {
        String blockId = state.getId().getFormatted();
        StateKey query = new StateKey(blockId, state.getProperties());
        StateAliasEntry exact = rawStates.get(query);
        if (exact == null) {
            return Optional.empty();
        }
        return Optional.of(new StateResolution(
                exact.templateIndex(),
                supportByBlockId.get(blockId)
        ));
    }

    public Map<String, FramedBlocks1061Support.Classification> supportByBlockId() {
        return supportByBlockId;
    }

    public Set<Key> usedSpriteKeys() {
        return usedSpriteKeys;
    }

    public int rawStateCount() {
        return rawStates.size();
    }

    public int renderableStateCount() {
        return renderableStateCount;
    }

    public int templateCount() {
        return templates.size();
    }

    public int baseRoutedFamilyTemplateCount() {
        return baseRoutedFamilyTemplateCount;
    }

    private Map<String, Integer> nullAliasCounts() {
        return nullAliasCounts;
    }

    public int quadCount() {
        return quadCount;
    }

    private static GeometryTemplateProfile validate(
            ExportDocument document,
            ValidationMode mode
    ) throws IOException {
        if (document == null
                || !isCompatibleSchema(document.schemaVersion(), mode)
                || !"applied_camo".equals(document.profileScope())
                || !"canonical_opaque_full_cube".equals(document.sourceModel())
                || !"diagnostic_applied_camo_no_dynamic_aux".equals(
                        document.modelDataProfile()
                )
                || document.dynamicModelDataIncluded()
                || document.blockEntityRendererContentIncluded()
                || !"sprite_normalized_baked".equals(document.uvSpace())
                || !"selected_precedence_including_hidden".equals(
                        document.resourcePackOrder()
                )
                || !"zero_block_state_emission".equals(
                        document.effectiveAmbientOcclusionAssumption()
                )
                || !"0x4652414d4544424c".equals(document.randomSeed())
                || !document.smoothLightingEnabled()
                || !FramedBlocks1061Profile.VERSION.equals(document.framedBlocksVersion())
                || !FramedBlocks1061Profile.JAR_SHA256.equals(
                        document.framedBlocksJarSha256()
                )
                || document.framedBlocksJarBytes() != FramedBlocks1061Profile.JAR_BYTES
                || !FramedBlocks1061Profile.CLIENT_CONFIG_SHA256.equals(
                        document.framedBlocksClientConfigSha256()
                )
                || !"1.21.1".equals(document.minecraftVersion())
                || !"21.1.234".equals(document.neoForgeVersion())
                || !FramedBlocks1061Profile.CLIENT_PACK_FINGERPRINT.equals(
                        document.packFingerprint()
                )
                || !FramedBlocks1061Profile.CLIENT_MODS_SHA256.equals(document.modsSha256())
                || !FramedBlocks1061Profile.CLIENT_RESOURCE_PACKS_ORDERED_SHA256.equals(
                        document.resourcePacksOrderedSha256()
                )
                || !FramedBlocks1061Profile.CLIENT_RESOURCE_PACK_ID_SET_SHA256.equals(
                        document.resourcePackIdSetSha256()
                )
                || document.modCount() != FramedBlocks1061Profile.CLIENT_MOD_COUNT
                || document.resourcePacksOrderedCount()
                        != FramedBlocks1061Profile.CLIENT_RESOURCE_PACKS_ORDERED_COUNT
                || document.resourcePackIdSetCount()
                        != FramedBlocks1061Profile.CLIENT_RESOURCE_PACK_ID_SET_COUNT
                || document.blockCount() <= 0
                || document.blockCount() > MAX_BLOCKS
                || !isSha256(document.blockIdsSha256())
                || document.rawStateCount() <= 0
                || document.rawStateCount() > MAX_RAW_STATES
                || !isSha256(document.rawStateKeysSha256())
                || document.renderableStateCount() < 0
                || document.renderableStateCount() > document.rawStateCount()
                || !isSha256(document.renderableStateKeysSha256())
                || document.templateCount() < 0
                || document.templateCount() > MAX_TEMPLATES
                || !isSha256(document.templateStateKeysSha256())
                || !isSha256(document.aliasPairsSha256())
                || document.quadCount() < 0
                || document.quadCount() > MAX_QUADS
                || !NON_CAMO_UTILITY_BLOCKS.equals(document.nonCamoUtilityBlocks())
                || !EXCLUDED_DYNAMIC_INPUTS.equals(document.excludedDynamicInputs())
                || document.templates() == null
                || document.states() == null
                || document.states().isEmpty()
                || document.templates().size() != document.templateCount()
                || document.states().size() != document.rawStateCount()) {
            throw new IOException("FramedBlocks geometry profile header is incompatible");
        }
        if (document.schemaVersion() == PROJECTED_SCHEMA_VERSION) {
            validateProjectionHeader(document);
        }

        List<StateTemplate> templates = new ArrayList<>(document.templates().size());
        Map<StateKey, Integer> templateIndices = new HashMap<>();
        List<String> templateKeys = new ArrayList<>(document.templates().size());
        Set<Key> spriteKeys = new HashSet<>();
        long quads = 0L;
        String previousTemplateKey = null;
        for (StateTemplate template : document.templates()) {
            validateTemplate(template);
            if (NON_CAMO_UTILITY_BLOCKS.contains(template.blockId())) {
                throw new IOException("FramedBlocks saw state cannot be a geometry template");
            }
            StateTemplate immutable = new StateTemplate(
                    template.blockId(),
                    Map.copyOf(template.properties()),
                    List.copyOf(template.quads())
            );
            StateKey key = new StateKey(immutable.blockId(), immutable.properties());
            String canonicalKey = canonicalStateKey(immutable.blockId(), immutable.properties());
            if (previousTemplateKey != null
                    && previousTemplateKey.compareTo(canonicalKey) >= 0) {
                throw new IOException(
                        "FramedBlocks geometry templates are not in canonical order"
                );
            }
            previousTemplateKey = canonicalKey;
            if (templateIndices.putIfAbsent(key, templates.size()) != null) {
                throw new IOException("FramedBlocks geometry profile contains a duplicate template");
            }
            templates.add(immutable);
            templateKeys.add(canonicalKey);
            immutable.quads().stream()
                    .filter(quad -> "fixed".equals(quad.component()))
                    .forEach(quad -> spriteKeys.add(Key.parse(quad.sprite())));
            quads += immutable.quads().size();
            if (quads > MAX_QUADS) {
                throw new IOException("FramedBlocks geometry profile exceeds the quad bound");
            }
        }

        Map<StateKey, StateAliasEntry> rawStates = new HashMap<>();
        Set<String> allBlockIds = new HashSet<>();
        List<String> rawStateKeys = new ArrayList<>(document.states().size());
        List<String> renderableStateKeys = new ArrayList<>(
                document.renderableStateCount()
        );
        List<String> aliasPairLines = new ArrayList<>(
                document.renderableStateCount() * 2
        );
        Set<String> renderedIds = new HashSet<>();
        Map<String, Integer> nullAliasCounts = new HashMap<>();
        Map<String, Set<String>> propertyNamesByBlockId = new HashMap<>();
        int[] templateUseCounts = new int[templates.size()];
        boolean[] representativeSelfAliases = new boolean[templates.size()];
        String previousRawStateKey = null;
        int renderableStates = 0;
        for (StateAlias state : document.states()) {
            validateStateIdentity(state.blockId(), state.properties());
            Set<String> propertyNames = Set.copyOf(state.properties().keySet());
            Set<String> expectedPropertyNames = propertyNamesByBlockId.putIfAbsent(
                    state.blockId(),
                    propertyNames
            );
            if (expectedPropertyNames != null
                    && !expectedPropertyNames.equals(propertyNames)) {
                throw new IOException(
                        "FramedBlocks raw states disagree on their block property names"
                );
            }
            StateKey key = new StateKey(state.blockId(), state.properties());
            String canonicalKey = canonicalStateKey(state.blockId(), state.properties());
            if (previousRawStateKey != null
                    && previousRawStateKey.compareTo(canonicalKey) >= 0) {
                throw new IOException(
                        "FramedBlocks raw states are not in canonical order"
                );
            }
            previousRawStateKey = canonicalKey;

            if (state.template() != null
                    && (state.template() < 0 || state.template() >= templates.size())) {
                throw new IOException("FramedBlocks state alias references an invalid template");
            }
            int templateIndex = state.template() == null ? -1 : state.template();
            if (templateIndex == -1) {
                if (!NON_CAMO_UTILITY_BLOCKS.contains(state.blockId())) {
                    throw new IOException("FramedBlocks non-saw state has a null template alias");
                }
                nullAliasCounts.merge(state.blockId(), 1, Integer::sum);
            } else {
                if (NON_CAMO_UTILITY_BLOCKS.contains(state.blockId())) {
                    throw new IOException("FramedBlocks saw state has a non-null template alias");
                }
                StateTemplate representative = templates.get(templateIndex);
                if (!state.blockId().equals(representative.blockId())) {
                    throw new IOException("FramedBlocks state alias crosses a block ID");
                }
                renderableStates++;
                renderableStateKeys.add(canonicalKey);
                aliasPairLines.add(canonicalKey);
                aliasPairLines.add(canonicalStateKey(
                        representative.blockId(),
                        representative.properties()
                ));
                templateUseCounts[templateIndex]++;
                if (key.equals(new StateKey(
                        representative.blockId(),
                        representative.properties()
                ))) {
                    representativeSelfAliases[templateIndex] = true;
                }
            }

            StateAliasEntry entry = new StateAliasEntry(key, templateIndex);
            if (rawStates.putIfAbsent(key, entry) != null) {
                throw new IOException("FramedBlocks geometry profile contains a duplicate state");
            }
            rawStateKeys.add(canonicalKey);
            allBlockIds.add(state.blockId());
            if (isRenderedFramedBlock(state.blockId())) {
                renderedIds.add(state.blockId());
            }
        }

        for (int index = 0; index < templates.size(); index++) {
            if (templateUseCounts[index] == 0 || !representativeSelfAliases[index]) {
                throw new IOException(
                        "FramedBlocks template is unused or lacks its representative self-alias"
                );
            }
        }
        if (document.blockCount() != allBlockIds.size()
                || !document.blockIdsSha256().equals(sha256Lines(allBlockIds))) {
            throw new IOException("FramedBlocks geometry profile block inventory does not match");
        }
        if (!document.rawStateKeysSha256().equals(sha256OrderedLines(rawStateKeys))) {
            throw new IOException("FramedBlocks raw-state inventory does not match");
        }
        if (renderableStates != document.renderableStateCount()
                || !document.renderableStateKeysSha256().equals(
                        sha256OrderedLines(renderableStateKeys)
                )) {
            throw new IOException("FramedBlocks renderable-state inventory does not match");
        }
        if (!document.templateStateKeysSha256().equals(
                sha256OrderedLines(templateKeys)
        )) {
            throw new IOException("FramedBlocks template inventory does not match");
        }
        if (!document.aliasPairsSha256().equals(sha256OrderedLines(aliasPairLines))) {
            throw new IOException("FramedBlocks state aliases do not match");
        }
        if (document.quadCount() != quads) {
            throw new IOException("FramedBlocks geometry profile quad count does not match");
        }
        Map<String, FramedBlocks1061Support.Classification> support =
                FramedBlocks1061Support.classifyAll(renderedIds);
        Set<String> routedIds = new HashSet<>();
        support.forEach((blockId, classification) -> {
            if (classification.routed()) {
                routedIds.add(blockId);
            }
        });
        int routedTemplates = 0;
        for (StateTemplate template : templates) {
            FramedBlocks1061Support.Classification classification =
                    support.get(template.blockId());
            if (classification != null && classification.routed()) {
                routedTemplates++;
            }
        }
        for (StateAliasEntry state : rawStates.values()) {
            FramedBlocks1061Support.Classification classification =
                    support.get(state.key().blockId());
            if (classification != null
                    && classification.routed()
                    && state.templateIndex() >= 0
                    && templates.get(state.templateIndex()).quads().isEmpty()) {
                throw new IOException(
                        "FramedBlocks routed geometry state contains no quads"
                );
            }
        }
        return new GeometryTemplateProfile(
                templates,
                rawStates,
                renderedIds,
                routedIds,
                support,
                spriteKeys,
                nullAliasCounts,
                renderableStates,
                routedTemplates,
                Math.toIntExact(quads)
        );
    }

    private static boolean isCompatibleSchema(int schemaVersion, ValidationMode mode) {
        return switch (mode) {
            case FLEXIBLE -> schemaVersion == RAW_SCHEMA_VERSION
                    || schemaVersion == PROJECTED_SCHEMA_VERSION;
            case RAW_EXACT -> schemaVersion == RAW_SCHEMA_VERSION;
            case PROJECTED_EXACT -> schemaVersion == PROJECTED_SCHEMA_VERSION;
        };
    }

    private static void validateProjectionHeader(ExportDocument document) throws IOException {
        if (!FramedBlocks1061Profile.GEOMETRY_PROJECTION.equals(
                document.geometryProjection()
        )
                || !Integer.valueOf(FramedBlocks1061Profile.SOURCE_EXPORT_SCHEMA_VERSION).equals(
                        document.sourceSchemaVersion()
                )
                || !FramedBlocks1061Profile.SOURCE_EXPORT_GZIP_SHA256.equals(
                        document.sourceExportGzipSha256()
                )
                || !Long.valueOf(FramedBlocks1061Profile.SOURCE_EXPORT_GZIP_BYTES).equals(
                        document.sourceExportGzipBytes()
                )
                || !FramedBlocks1061Profile.SOURCE_EXPORT_UNCOMPRESSED_SHA256.equals(
                        document.sourceExportUncompressedSha256()
                )
                || !Long.valueOf(
                        FramedBlocks1061Profile.SOURCE_EXPORT_UNCOMPRESSED_BYTES
                ).equals(document.sourceExportUncompressedBytes())
                || !Integer.valueOf(FramedBlocks1061Profile.SOURCE_EXPORT_QUAD_COUNT).equals(
                        document.sourceQuadCount()
                )
                || !Integer.valueOf(
                        FramedBlocks1061Profile.EXCLUDED_GEOMETRY_BLOCK_COUNT
                ).equals(document.excludedGeometryBlockCount())
                || !FramedBlocks1061Profile.EXCLUDED_GEOMETRY_BLOCK_IDS_SHA256.equals(
                        document.excludedGeometryBlockIdsSha256()
                )
                || !Integer.valueOf(
                        FramedBlocks1061Profile.EXCLUDED_GEOMETRY_TEMPLATE_COUNT
                ).equals(document.excludedGeometryTemplateCount())
                || !Integer.valueOf(
                        FramedBlocks1061Profile.EXCLUDED_GEOMETRY_QUAD_COUNT
                ).equals(document.excludedGeometryQuadCount())) {
            throw new IOException("FramedBlocks geometry projection header is incompatible");
        }
    }

    private static ExportDocument convert(RawExportDocument raw) throws IOException {
        if (raw == null) {
            return null;
        }
        List<StateTemplate> templates = null;
        if (raw.templates != null) {
            templates = raw.templates.stream().map(GeometryTemplateProfile::convert).toList();
        }
        List<StateAlias> states = null;
        if (raw.states != null) {
            states = new ArrayList<>(raw.states.size());
            for (RawStateAlias state : raw.states) {
                states.add(convert(state));
            }
        }
        return new ExportDocument(
                raw.schemaVersion,
                raw.geometryProjection,
                raw.sourceSchemaVersion,
                raw.sourceExportGzipSha256,
                raw.sourceExportGzipBytes,
                raw.sourceExportUncompressedSha256,
                raw.sourceExportUncompressedBytes,
                raw.sourceQuadCount,
                raw.excludedGeometryBlockCount,
                raw.excludedGeometryBlockIdsSha256,
                raw.excludedGeometryTemplateCount,
                raw.excludedGeometryQuadCount,
                raw.profileScope,
                raw.sourceModel,
                raw.modelDataProfile,
                raw.dynamicModelDataIncluded,
                raw.blockEntityRendererContentIncluded,
                raw.uvSpace,
                raw.resourcePackOrder,
                raw.effectiveAmbientOcclusionAssumption,
                raw.randomSeed,
                raw.smoothLightingEnabled,
                raw.framedBlocksVersion,
                raw.framedBlocksJarSha256,
                raw.framedBlocksJarBytes,
                raw.framedBlocksClientConfigSha256,
                raw.minecraftVersion,
                raw.neoForgeVersion,
                raw.packFingerprint,
                raw.modsSha256,
                raw.resourcePacksOrderedSha256,
                raw.resourcePackIdSetSha256,
                raw.modCount,
                raw.resourcePacksOrderedCount,
                raw.resourcePackIdSetCount,
                raw.blockCount,
                raw.blockIdsSha256,
                raw.rawStateCount,
                raw.rawStateKeysSha256,
                raw.renderableStateCount,
                raw.renderableStateKeysSha256,
                raw.templateCount,
                raw.templateStateKeysSha256,
                raw.aliasPairsSha256,
                raw.quadCount,
                raw.nonCamoUtilityBlocks,
                raw.excludedDynamicInputs,
                templates,
                states
        );
    }

    private static void validateRequiredFields(RawExportDocument raw) throws IOException {
        if (raw == null || anyNull(
                raw.schemaVersion,
                raw.profileScope,
                raw.sourceModel,
                raw.modelDataProfile,
                raw.dynamicModelDataIncluded,
                raw.blockEntityRendererContentIncluded,
                raw.uvSpace,
                raw.resourcePackOrder,
                raw.effectiveAmbientOcclusionAssumption,
                raw.randomSeed,
                raw.smoothLightingEnabled,
                raw.framedBlocksVersion,
                raw.framedBlocksJarSha256,
                raw.framedBlocksJarBytes,
                raw.framedBlocksClientConfigSha256,
                raw.minecraftVersion,
                raw.neoForgeVersion,
                raw.packFingerprint,
                raw.modsSha256,
                raw.resourcePacksOrderedSha256,
                raw.resourcePackIdSetSha256,
                raw.modCount,
                raw.resourcePacksOrderedCount,
                raw.resourcePackIdSetCount,
                raw.blockCount,
                raw.blockIdsSha256,
                raw.rawStateCount,
                raw.rawStateKeysSha256,
                raw.renderableStateCount,
                raw.renderableStateKeysSha256,
                raw.templateCount,
                raw.templateStateKeysSha256,
                raw.aliasPairsSha256,
                raw.quadCount,
                raw.nonCamoUtilityBlocks,
                raw.excludedDynamicInputs,
                raw.templates,
                raw.states
        )) {
            throw new IOException("FramedBlocks geometry profile is missing a required header field");
        }
        Object[] projectionFields = {
                raw.geometryProjection,
                raw.sourceSchemaVersion,
                raw.sourceExportGzipSha256,
                raw.sourceExportGzipBytes,
                raw.sourceExportUncompressedSha256,
                raw.sourceExportUncompressedBytes,
                raw.sourceQuadCount,
                raw.excludedGeometryBlockCount,
                raw.excludedGeometryBlockIdsSha256,
                raw.excludedGeometryTemplateCount,
                raw.excludedGeometryQuadCount
        };
        if (raw.schemaVersion == PROJECTED_SCHEMA_VERSION && anyNull(projectionFields)) {
            throw new IOException(
                    "FramedBlocks geometry profile is missing a projection header field"
            );
        }
        if (raw.schemaVersion == RAW_SCHEMA_VERSION && !allNull(projectionFields)) {
            throw new IOException("Raw FramedBlocks geometry export contains projection fields");
        }

        for (RawStateAlias state : raw.states) {
            if (state == null || anyNull(state.blockId, state.properties, state.template)) {
                throw new IOException("FramedBlocks geometry profile is missing an alias field");
            }
        }
        for (RawStateTemplate template : raw.templates) {
            if (template == null
                    || anyNull(template.blockId, template.properties, template.quads)) {
                throw new IOException("FramedBlocks geometry profile is missing a template field");
            }
            for (RawQuadTemplate quad : template.quads) {
                if (quad == null || anyNull(
                        quad.layer,
                        quad.cullFace,
                        quad.direction,
                        quad.component,
                        quad.sourceFace,
                        quad.atlas,
                        quad.sprite,
                        quad.tintIndex,
                        quad.shade,
                        quad.ambientOcclusion,
                        quad.modelAmbientOcclusion,
                        quad.effectiveAmbientOcclusionUnderZeroEmission,
                        quad.blockLight,
                        quad.skyLight,
                        quad.vertices
                )) {
                    throw new IOException("FramedBlocks geometry profile is missing a quad field");
                }
                for (RawVertex vertex : quad.vertices) {
                    if (vertex == null || anyNull(
                            vertex.x,
                            vertex.y,
                            vertex.z,
                            vertex.u,
                            vertex.v,
                            vertex.blockLight,
                            vertex.skyLight
                    )) {
                        throw new IOException(
                                "FramedBlocks geometry profile is missing a vertex field"
                        );
                    }
                }
            }
        }
    }

    private static boolean anyNull(Object... values) {
        for (Object value : values) {
            if (value == null) {
                return true;
            }
        }
        return false;
    }

    private static boolean allNull(Object... values) {
        for (Object value : values) {
            if (value != null) {
                return false;
            }
        }
        return true;
    }

    private static StateTemplate convert(RawStateTemplate raw) {
        if (raw == null) {
            return null;
        }
        List<QuadTemplate> quads = null;
        if (raw.quads != null) {
            quads = raw.quads.stream().map(GeometryTemplateProfile::convert).toList();
        }
        return new StateTemplate(raw.blockId, raw.properties, quads);
    }

    private static StateAlias convert(RawStateAlias raw) throws IOException {
        Integer template = null;
        if (!raw.template.isJsonNull()) {
            if (!raw.template.isJsonPrimitive()
                    || !raw.template.getAsJsonPrimitive().isNumber()
                    || !INTEGER.matcher(raw.template.getAsString()).matches()) {
                throw new IOException("FramedBlocks state alias has a non-integer template");
            }
            try {
                template = Integer.valueOf(raw.template.getAsString());
            } catch (NumberFormatException exception) {
                throw new IOException("FramedBlocks state alias template is out of range", exception);
            }
        }
        return new StateAlias(raw.blockId, raw.properties, template);
    }

    private static QuadTemplate convert(RawQuadTemplate raw) {
        if (raw == null) {
            return null;
        }
        List<Vertex> vertices = null;
        if (raw.vertices != null) {
            vertices = raw.vertices.stream()
                    .map(vertex -> vertex == null
                            ? null
                            : new Vertex(
                                    vertex.x,
                                    vertex.y,
                                    vertex.z,
                                    vertex.u,
                                    vertex.v,
                                    vertex.blockLight,
                                    vertex.skyLight
                            ))
                    .toList();
        }
        return new QuadTemplate(
                raw.layer,
                raw.cullFace,
                raw.direction,
                raw.component,
                raw.sourceFace,
                raw.atlas,
                raw.sprite,
                raw.tintIndex,
                raw.shade,
                raw.ambientOcclusion,
                raw.modelAmbientOcclusion,
                raw.effectiveAmbientOcclusionUnderZeroEmission,
                raw.blockLight,
                raw.skyLight,
                vertices
        );
    }

    private static void validateTemplate(StateTemplate state) throws IOException {
        if (state == null
                || state.quads() == null) {
            throw new IOException("FramedBlocks geometry profile contains an invalid template");
        }
        validateStateIdentity(state.blockId(), state.properties());
        for (QuadTemplate quad : state.quads()) {
            validateQuad(quad);
        }
    }

    private static void validateStateIdentity(
            String blockId,
            Map<String, String> properties
    ) throws IOException {
        if (!isFramedBlocksId(blockId)
                || properties == null
                || properties.size() > 32) {
            throw new IOException("FramedBlocks geometry profile contains an invalid state");
        }
        for (Map.Entry<String, String> property : properties.entrySet()) {
            if (!isCanonicalPropertyName(property.getKey())
                    || !isCanonicalPropertyValue(property.getValue())) {
                throw new IOException("FramedBlocks geometry profile contains an invalid property");
            }
        }
    }

    private static void validateQuad(QuadTemplate quad) throws IOException {
        if (quad == null
                || quad.layer() == null
                || !LAYERS.contains(quad.layer())
                || quad.cullFace() == null
                || !("none".equals(quad.cullFace()) || DIRECTIONS.contains(quad.cullFace()))
                || !DIRECTIONS.contains(quad.direction())
                || quad.component() == null
                || !COMPONENTS.contains(quad.component())
                || quad.sourceFace() == null
                || !("none".equals(quad.sourceFace())
                        || DIRECTIONS.contains(quad.sourceFace()))
                || !BLOCK_ATLAS.equals(quad.atlas())
                || !RESOURCE_ID.matcher(quad.sprite()).matches()
                || quad.tintIndex() < -4_098
                || quad.tintIndex() > 4_096
                || quad.modelAmbientOcclusion() == null
                || !MODEL_AMBIENT_OCCLUSION.contains(quad.modelAmbientOcclusion())
                || quad.blockLight() < 0
                || quad.blockLight() > 15
                || quad.skyLight() < 0
                || quad.skyLight() > 15
                || quad.vertices() == null
                || quad.vertices().size() != 4) {
            throw new IOException("FramedBlocks geometry profile contains an invalid quad");
        }
        if (("fixed".equals(quad.component()) && !"none".equals(quad.sourceFace()))
                || (!"fixed".equals(quad.component())
                        && !DIRECTIONS.contains(quad.sourceFace()))) {
            throw new IOException("FramedBlocks geometry profile has invalid camo identity");
        }
        if ("fixed".equals(quad.component())
                && quad.tintIndex() != -1
                && quad.tintIndex() != 1_024) {
            throw new IOException("FramedBlocks geometry profile has unsupported fixed tint");
        }
        boolean expectedModelAmbientOcclusion =
                !"false".equals(quad.modelAmbientOcclusion());
        if (quad.effectiveAmbientOcclusionUnderZeroEmission()
                != expectedModelAmbientOcclusion) {
            throw new IOException("FramedBlocks geometry profile has contradictory AO metadata");
        }
        if (!"fixed".equals(quad.component())) {
            int ordinal = CAMO_SENTINEL_ORDINALS.get(quad.sourceFace());
            int expectedTint = "primary".equals(quad.component())
                    ? ordinal : -2 - ordinal;
            if (!CAMO_SENTINEL_SPRITES.get(quad.sourceFace()).equals(quad.sprite())
                    || quad.tintIndex() != expectedTint) {
                throw new IOException("FramedBlocks geometry profile camo sentinel changed");
            }
        }
        for (Vertex vertex : quad.vertices()) {
            if (vertex == null
                    || !finiteWithin(vertex.x(), -2F, 3F)
                    || !finiteWithin(vertex.y(), -2F, 3F)
                    || !finiteWithin(vertex.z(), -2F, 3F)
                    || !finiteWithin(vertex.u(), -4F, 4F)
                    || !finiteWithin(vertex.v(), -4F, 4F)
                    || vertex.blockLight() < 0
                    || vertex.blockLight() > 15
                    || vertex.skyLight() < 0
                    || vertex.skyLight() > 15) {
                throw new IOException("FramedBlocks geometry profile contains an invalid vertex");
            }
        }
    }

    private static boolean finiteWithin(float value, float minimum, float maximum) {
        return Float.isFinite(value) && value >= minimum && value <= maximum;
    }

    private static boolean isSha256(String value) {
        return value != null && SHA256.matcher(value).matches();
    }

    private static String canonicalStateKey(
            String blockId,
            Map<String, String> properties
    ) {
        StringBuilder canonical = new StringBuilder(blockId);
        properties.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(property -> canonical
                        .append('\0')
                        .append(property.getKey())
                        .append('=')
                        .append(property.getValue()));
        return canonical.toString();
    }

    private static boolean isCanonicalPropertyName(String value) {
        return isCanonicalPropertyComponent(value);
    }

    private static boolean isCanonicalPropertyValue(String value) {
        return isCanonicalPropertyComponent(value);
    }

    private static boolean isCanonicalPropertyComponent(String value) {
        if (value == null || value.isEmpty() || value.length() > 128) {
            return false;
        }
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (character < 0x21
                    || character > 0x7E
                    || character == '=') {
                return false;
            }
        }
        return true;
    }

    private static String sha256Lines(Set<String> values) throws IOException {
        return sha256OrderedLines(values.stream().sorted().toList());
    }

    private static String sha256OrderedLines(List<String> values) throws IOException {
        try {
            java.security.MessageDigest digest =
                    java.security.MessageDigest.getInstance("SHA-256");
            if (values.isEmpty()) {
                digest.update((byte) '\n');
            }
            for (String value : values) {
                digest.update(value.getBytes(StandardCharsets.UTF_8));
                digest.update((byte) '\n');
            }
            return java.util.HexFormat.of().formatHex(digest.digest());
        } catch (java.security.NoSuchAlgorithmException exception) {
            throw new IOException("SHA-256 is unavailable", exception);
        }
    }

    private static boolean isFramedBlocksId(String value) {
        return value != null
                && value.startsWith("framedblocks:")
                && RESOURCE_ID.matcher(value).matches();
    }

    private static boolean isRenderedFramedBlock(String value) {
        return !("framedblocks:framing_saw".equals(value)
                || "framedblocks:powered_framing_saw".equals(value));
    }

    private record StateKey(String blockId, Map<String, String> properties) {
        private StateKey {
            properties = Map.copyOf(properties);
        }
    }

    private record StateAliasEntry(StateKey key, int templateIndex) {
    }

    private record StateResolution(
            int templateIndex,
            FramedBlocks1061Support.Classification classification
    ) {
    }

    public record ExportDocument(
            int schemaVersion,
            String geometryProjection,
            Integer sourceSchemaVersion,
            String sourceExportGzipSha256,
            Long sourceExportGzipBytes,
            String sourceExportUncompressedSha256,
            Long sourceExportUncompressedBytes,
            Integer sourceQuadCount,
            Integer excludedGeometryBlockCount,
            String excludedGeometryBlockIdsSha256,
            Integer excludedGeometryTemplateCount,
            Integer excludedGeometryQuadCount,
            String profileScope,
            String sourceModel,
            String modelDataProfile,
            boolean dynamicModelDataIncluded,
            boolean blockEntityRendererContentIncluded,
            String uvSpace,
            String resourcePackOrder,
            String effectiveAmbientOcclusionAssumption,
            String randomSeed,
            boolean smoothLightingEnabled,
            String framedBlocksVersion,
            String framedBlocksJarSha256,
            long framedBlocksJarBytes,
            String framedBlocksClientConfigSha256,
            String minecraftVersion,
            String neoForgeVersion,
            String packFingerprint,
            String modsSha256,
            String resourcePacksOrderedSha256,
            String resourcePackIdSetSha256,
            int modCount,
            int resourcePacksOrderedCount,
            int resourcePackIdSetCount,
            int blockCount,
            String blockIdsSha256,
            int rawStateCount,
            String rawStateKeysSha256,
            int renderableStateCount,
            String renderableStateKeysSha256,
            int templateCount,
            String templateStateKeysSha256,
            String aliasPairsSha256,
            int quadCount,
            List<String> nonCamoUtilityBlocks,
            List<String> excludedDynamicInputs,
            List<StateTemplate> templates,
            List<StateAlias> states
    ) {
    }

    public record StateTemplate(
            String blockId,
            Map<String, String> properties,
            List<QuadTemplate> quads
    ) {
    }

    public record StateAlias(
            String blockId,
            Map<String, String> properties,
            Integer template
    ) {
    }

    public record QuadTemplate(
            String layer,
            String cullFace,
            String direction,
            String component,
            String sourceFace,
            String atlas,
            String sprite,
            int tintIndex,
            boolean shade,
            boolean ambientOcclusion,
            String modelAmbientOcclusion,
            boolean effectiveAmbientOcclusionUnderZeroEmission,
            int blockLight,
            int skyLight,
            List<Vertex> vertices
    ) {
    }

    public record Vertex(
            float x,
            float y,
            float z,
            float u,
            float v,
            int blockLight,
            int skyLight
    ) {
        public Vertex(float x, float y, float z, float u, float v) {
            this(x, y, z, u, v, 0, 0);
        }
    }

    @SuppressWarnings("VisibilityModifier")
    private static final class RawExportDocument {
        Integer schemaVersion;
        String geometryProjection;
        Integer sourceSchemaVersion;
        String sourceExportGzipSha256;
        Long sourceExportGzipBytes;
        String sourceExportUncompressedSha256;
        Long sourceExportUncompressedBytes;
        Integer sourceQuadCount;
        Integer excludedGeometryBlockCount;
        String excludedGeometryBlockIdsSha256;
        Integer excludedGeometryTemplateCount;
        Integer excludedGeometryQuadCount;
        String profileScope;
        String sourceModel;
        String modelDataProfile;
        Boolean dynamicModelDataIncluded;
        Boolean blockEntityRendererContentIncluded;
        String uvSpace;
        String resourcePackOrder;
        String effectiveAmbientOcclusionAssumption;
        String randomSeed;
        Boolean smoothLightingEnabled;
        String framedBlocksVersion;
        String framedBlocksJarSha256;
        Long framedBlocksJarBytes;
        String framedBlocksClientConfigSha256;
        String minecraftVersion;
        String neoForgeVersion;
        String packFingerprint;
        String modsSha256;
        String resourcePacksOrderedSha256;
        String resourcePackIdSetSha256;
        Integer modCount;
        Integer resourcePacksOrderedCount;
        Integer resourcePackIdSetCount;
        Integer blockCount;
        String blockIdsSha256;
        @SerializedName("raw_state_count")
        Integer rawStateCount;
        @SerializedName("raw_state_keys_sha256")
        String rawStateKeysSha256;
        @SerializedName("renderable_state_count")
        Integer renderableStateCount;
        @SerializedName("renderable_state_keys_sha256")
        String renderableStateKeysSha256;
        @SerializedName("template_count")
        Integer templateCount;
        @SerializedName("template_state_keys_sha256")
        String templateStateKeysSha256;
        @SerializedName("alias_pairs_sha256")
        String aliasPairsSha256;
        Integer quadCount;
        List<String> nonCamoUtilityBlocks;
        List<String> excludedDynamicInputs;
        List<RawStateTemplate> templates;
        List<RawStateAlias> states;
    }

    @SuppressWarnings("VisibilityModifier")
    private static final class RawStateTemplate {
        String blockId;
        Map<String, String> properties;
        List<RawQuadTemplate> quads;
    }

    @SuppressWarnings("VisibilityModifier")
    private static final class RawStateAlias {
        String blockId;
        Map<String, String> properties;
        JsonElement template;
    }

    @SuppressWarnings("VisibilityModifier")
    private static final class RawQuadTemplate {
        String layer;
        String cullFace;
        String direction;
        String component;
        String sourceFace;
        String atlas;
        String sprite;
        Integer tintIndex;
        Boolean shade;
        Boolean ambientOcclusion;
        String modelAmbientOcclusion;
        Boolean effectiveAmbientOcclusionUnderZeroEmission;
        Integer blockLight;
        Integer skyLight;
        List<RawVertex> vertices;
    }

    @SuppressWarnings("VisibilityModifier")
    private static final class RawVertex {
        Float x;
        Float y;
        Float z;
        Float u;
        Float v;
        Integer blockLight;
        Integer skyLight;
    }

    private static byte[] decompress(byte[] compressed, long maximum) throws IOException {
        try (InputStream gzip = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
            return readBounded(gzip, maximum);
        }
    }

    private static byte[] readBounded(InputStream input, long maximum) throws IOException {
        if (maximum < 0L) {
            throw new IOException("FramedBlocks geometry profile byte bound is not pinned");
        }
        try (InputStream bounded = new BoundedInputStream(input, maximum)) {
            return bounded.readAllBytes();
        }
    }

    private static void verifyPayload(
            byte[] bytes,
            long expectedBytes,
            String expectedSha256,
            String description
    ) throws IOException {
        if (bytes.length != expectedBytes || !expectedSha256.equals(sha256(bytes))) {
            throw new IOException(description + " does not match its exact payload pin");
        }
    }

    private static String sha256(byte[] bytes) throws IOException {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(bytes)
            );
        } catch (NoSuchAlgorithmException exception) {
            throw new IOException("SHA-256 is unavailable", exception);
        }
    }

    private enum ValidationMode {
        FLEXIBLE,
        RAW_EXACT,
        PROJECTED_EXACT
    }

    private static final class BoundedInputStream extends FilterInputStream {
        private final long maximum;
        private long read;

        BoundedInputStream(InputStream input, long maximum) {
            super(input);
            this.maximum = maximum;
        }

        @Override
        public int read() throws IOException {
            int value = super.read();
            if (value >= 0) {
                account(1L);
            }
            return value;
        }

        @Override
        public int read(byte[] target, int offset, int length) throws IOException {
            int count = super.read(target, offset, length);
            if (count > 0) {
                account(count);
            }
            return count;
        }

        private void account(long count) throws IOException {
            read += count;
            if (read > maximum) {
                throw new IOException("FramedBlocks geometry profile exceeds the byte bound");
            }
        }
    }
}

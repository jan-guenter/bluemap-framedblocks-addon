/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.profile.framedblocks10_6;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

/** Decoder for the named FramedBlocks 10.6.1 on-disk block camouflage codec. */
public final class FramedCamoDecoder {

    private static final int MAX_PROPERTIES = 32;
    private static final int MAX_TEXT_LENGTH = 256;
    private static final Pattern RESOURCE_ID = Pattern.compile(
            "[a-z0-9_.-]+:[a-z0-9_./-]+"
    );

    public CamoDecodeResult decode(Object rawCamo) {
        if (rawCamo == null) {
            return CamoDecodeResult.failure("missing-camo");
        }
        if (!(rawCamo instanceof Map<?, ?> camo)) {
            return CamoDecodeResult.failure("invalid-camo");
        }
        if (camo.isEmpty()) {
            return CamoDecodeResult.success(NormalizedCamo.empty());
        }

        String type = stringValue(camo.get("type"));
        if ("framedblocks:empty".equals(type)) {
            return CamoDecodeResult.success(NormalizedCamo.empty());
        }
        if ("framedblocks:fluid".equals(type)) {
            return decodeFluid(camo);
        }
        boolean crystalix = "crystalix:crystalix_glass".equals(type);
        if (!"framedblocks:block".equals(type) && !crystalix) {
            return CamoDecodeResult.failure("unsupported-camo-type");
        }

        if (!(camo.get("state") instanceof Map<?, ?> state)) {
            return CamoDecodeResult.failure("missing-camo-state");
        }

        String id = firstString(state, "Name", "name");
        if (id == null || id.length() > MAX_TEXT_LENGTH || !RESOURCE_ID.matcher(id).matches()) {
            return CamoDecodeResult.failure("invalid-camo-id");
        }
        if (id.startsWith("framedblocks:")) {
            return CamoDecodeResult.failure("recursive-camo");
        }

        Object rawProperties = state.containsKey("Properties")
                ? state.get("Properties")
                : state.get("properties");
        Map<String, String> properties = new TreeMap<>();
        if (rawProperties != null) {
            if (!(rawProperties instanceof Map<?, ?> propertyMap)) {
                return CamoDecodeResult.failure("invalid-camo-properties");
            }
            if (propertyMap.size() > MAX_PROPERTIES) {
                return CamoDecodeResult.failure("too-many-camo-properties");
            }
            for (Map.Entry<?, ?> entry : propertyMap.entrySet()) {
                String key = stringValue(entry.getKey());
                String value = stringValue(entry.getValue());
                if (key == null || value == null
                        || key.length() > MAX_TEXT_LENGTH
                        || value.length() > MAX_TEXT_LENGTH) {
                    return CamoDecodeResult.failure("invalid-camo-property");
                }
                properties.put(key, value);
            }
        }

        NormalizedBlockState blockState = new NormalizedBlockState(id, properties);
        if (crystalix) {
            if (!"crystalix:crystalix_glass".equals(id)) {
                return CamoDecodeResult.failure("invalid-crystalix-camo-state");
            }
            Object rawColor = camo.get("color");
            if (!(rawColor instanceof Integer color)
                    || color < 0
                    || color > 0x00ff_ffff) {
                return CamoDecodeResult.failure("invalid-crystalix-camo-color");
            }
            return CamoDecodeResult.success(NormalizedCamo.fixedTintBlock(
                    blockState,
                    color
            ));
        }
        return CamoDecodeResult.success(NormalizedCamo.block(
                blockState
        ));
    }

    private CamoDecodeResult decodeFluid(Map<?, ?> camo) {
        String fluid = stringValue(camo.get("fluid"));
        if (fluid == null
                || fluid.length() > MAX_TEXT_LENGTH
                || !RESOURCE_ID.matcher(fluid).matches()) {
            return CamoDecodeResult.failure("invalid-fluid-id");
        }

        String flowDirection = stringValue(camo.get("flow_dir"));
        if (flowDirection == null) {
            flowDirection = "down";
        }
        if (!switch (flowDirection) {
            case "down", "up", "north", "south", "west", "east" -> true;
            default -> false;
        }) {
            return CamoDecodeResult.failure("invalid-fluid-flow-direction");
        }
        return CamoDecodeResult.success(NormalizedCamo.fluid(fluid, flowDirection));
    }

    private static String firstString(Map<?, ?> values, String first, String second) {
        String value = stringValue(values.get(first));
        return value == null ? stringValue(values.get(second)) : value;
    }

    private static String stringValue(Object value) {
        return value instanceof String string ? string : null;
    }
}

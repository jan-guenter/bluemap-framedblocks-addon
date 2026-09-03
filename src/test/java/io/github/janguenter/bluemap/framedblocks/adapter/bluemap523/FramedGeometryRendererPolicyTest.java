/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.mask.Mask;
import de.bluecolored.bluemap.core.resources.pack.PackVersion;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockEntity;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.DimensionType;
import de.bluecolored.bluemap.core.world.LightData;
import de.bluecolored.bluemap.core.world.biome.Biome;
import de.bluecolored.bluemap.core.world.block.BlockAccess;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import de.bluecolored.bluemap.core.util.math.Color;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FramedGeometryRendererPolicyTest {

    private static final int X = 1;
    private static final int Y = 64;
    private static final int Z = 1;

    @Test
    void appliedCamoProfileRejectsEmptyAndUnresolvedPalettes() {
        CamoMaterialResolver.Material material = new CamoMaterialResolver.Material(
                Key.parse("minecraft:block/stone"),
                -1,
                0
        );

        assertTrue(FramedGeometryRenderer.isAppliedCamoPalette(
                CamoMaterialResolver.MaterialPalette.uniform(material)
        ));
        assertFalse(FramedGeometryRenderer.isAppliedCamoPalette(
                CamoMaterialResolver.MaterialPalette.emptyPalette()
        ));
        assertFalse(FramedGeometryRenderer.isAppliedCamoPalette(
                CamoMaterialResolver.MaterialPalette.missing(BlockState.MISSING)
        ));
    }

    @Test
    void usesExactMinecraft1211DyeTextColorsAndRejectsInvalidIds() {
        int[] expected = {
                0xFFFFFF, 0xFF681F, 0xFF00FF, 0x9AC0CD,
                0xFFFF00, 0xBFFF00, 0xFF69B4, 0x808080,
                0xD3D3D3, 0x00FFFF, 0xA020F0, 0x0000FF,
                0x8B4513, 0x00FF00, 0xFF0000, 0x000000
        };
        for (int id = 0; id < expected.length; id++) {
            assertEquals(expected[id], FramedGeometryRenderer.dyeTextColor(id));
        }
        assertThrows(IllegalArgumentException.class,
                () -> FramedGeometryRenderer.dyeTextColor(-1));
        assertThrows(IllegalArgumentException.class,
                () -> FramedGeometryRenderer.dyeTextColor(16));
    }

    @Test
    void recognizesBakedBoundaryCoordinatesWithinFloatNoiseOnly() {
        assertEquals(-1, FramedGeometryRenderer.boundaryOffset(0F));
        assertEquals(-1, FramedGeometryRenderer.boundaryOffset(0.000001F));
        assertEquals(-1, FramedGeometryRenderer.boundaryOffset(-0.000001F));
        assertEquals(1, FramedGeometryRenderer.boundaryOffset(1F));
        assertEquals(1, FramedGeometryRenderer.boundaryOffset(0.999999F));
        assertEquals(1, FramedGeometryRenderer.boundaryOffset(1.000001F));
        assertEquals(0, FramedGeometryRenderer.boundaryOffset(0.5F));
        assertEquals(0, FramedGeometryRenderer.boundaryOffset(0.001F));
        assertEquals(0, FramedGeometryRenderer.boundaryOffset(0.999F));
    }

    @Test
    void fixedTintPreservesPremultipliedAlphaForTranslucentTextures() {
        Color tint = FramedGeometryRenderer.fixedTint(0x12_34ab, new Color());
        Color translucent = new Color().set(0x80ff_ffff, true).multiply(tint);

        assertTrue(tint.premultiplied);
        assertTrue(translucent.premultiplied);
        new Color().set(0F, 0F, 0F, 0F, true).add(translucent);
    }

    @Test
    void newBlockEntityDtoDoesNotInventRequiredNbtFields() {
        FramedBlockEntityData data = new FramedBlockEntityData();

        assertFalse(data.hasRequiredBaseFields());
        assertFalse(data.hasOverlayColor());
        assertEquals(Byte.MIN_VALUE, data.getUpdated());
        assertEquals(Integer.MIN_VALUE, data.getOverlayColor());
    }

    private static BlockNeighborhood neighborhood(Map<Position, BlockState> states) {
        BlockNeighborhood neighborhood = new BlockNeighborhood(
                new TestBlockAccess(states),
                new ResourcePack(new PackVersion(34, 0)),
                TEST_RENDER_SETTINGS,
                DimensionType.OVERWORLD
        );
        neighborhood.set(X, Y, Z);
        return neighborhood;
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
        private int x;
        private int y;
        private int z;

        private TestBlockAccess(Map<Position, BlockState> states) {
            this.states = states;
        }

        @Override
        public void set(int newX, int newY, int newZ) {
            x = newX;
            y = newY;
            z = newZ;
        }

        @Override
        public BlockAccess copy() {
            return new TestBlockAccess(states);
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
            return new LightData(15, 0);
        }

        @Override
        public Biome getBiome() {
            return Biome.DEFAULT;
        }

        @Override
        public BlockEntity getBlockEntity() {
            return null;
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

    private record Position(int x, int y, int z) {
    }
}

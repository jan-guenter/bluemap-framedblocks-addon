/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.hires.TileModel;
import de.bluecolored.bluemap.core.util.math.MatrixM3f;
import de.bluecolored.bluemap.core.util.math.MatrixM4f;
import de.bluecolored.bluemap.core.util.math.Color;

import java.util.BitSet;
import java.util.Set;

/** Replaces only known stock-model placeholder faces while preserving fixed materials. */
final class CamoSubstitutionTileModel implements TileModel {

    private final TileModel delegate;
    private final Set<Integer> placeholderMaterials;
    private final int camoMaterial;
    private final int camoLightEmission;
    private final float tintRed;
    private final float tintGreen;
    private final float tintBlue;
    private final BitSet substitutedFaces = new BitSet();
    private boolean substituted;

    CamoSubstitutionTileModel(
            TileModel delegate,
            Set<Integer> placeholderMaterials,
            int camoMaterial,
            int camoLightEmission,
            Color tint
    ) {
        this.delegate = delegate;
        this.placeholderMaterials = Set.copyOf(placeholderMaterials);
        this.camoMaterial = camoMaterial;
        this.camoLightEmission = camoLightEmission;
        this.tintRed = tint.r;
        this.tintGreen = tint.g;
        this.tintBlue = tint.b;
    }

    boolean substituted() {
        return substituted;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public int add(int count) {
        return delegate.add(count);
    }

    @Override
    public TileModel setPositions(
            int face,
            float x1,
            float y1,
            float z1,
            float x2,
            float y2,
            float z2,
            float x3,
            float y3,
            float z3
    ) {
        delegate.setPositions(face, x1, y1, z1, x2, y2, z2, x3, y3, z3);
        return this;
    }

    @Override
    public TileModel setUvs(
            int face,
            float u1,
            float v1,
            float u2,
            float v2,
            float u3,
            float v3
    ) {
        delegate.setUvs(face, u1, v1, u2, v2, u3, v3);
        return this;
    }

    @Override
    public TileModel setAOs(int face, float ao1, float ao2, float ao3) {
        delegate.setAOs(face, ao1, ao2, ao3);
        return this;
    }

    @Override
    public TileModel setColor(int face, float red, float green, float blue) {
        if (substitutedFaces.get(face)) {
            delegate.setColor(
                    face,
                    red * tintRed,
                    green * tintGreen,
                    blue * tintBlue
            );
        } else {
            delegate.setColor(face, red, green, blue);
        }
        return this;
    }

    @Override
    public TileModel setSunlight(int face, int sunlight) {
        delegate.setSunlight(face, sunlight);
        return this;
    }

    @Override
    public TileModel setBlocklight(int face, int blocklight) {
        delegate.setBlocklight(
                face,
                substitutedFaces.get(face)
                        ? Math.max(blocklight, camoLightEmission)
                        : blocklight
        );
        return this;
    }

    @Override
    public TileModel setMaterialIndex(int face, int material) {
        if (placeholderMaterials.contains(material)) {
            substitutedFaces.set(face);
            substituted = true;
            delegate.setMaterialIndex(face, camoMaterial);
        } else {
            substitutedFaces.clear(face);
            delegate.setMaterialIndex(face, material);
        }
        return this;
    }

    @Override
    public TileModel invertOrientation(int face) {
        delegate.invertOrientation(face);
        return this;
    }

    @Override
    public TileModel rotate(
            int start,
            int count,
            float angle,
            float axisX,
            float axisY,
            float axisZ
    ) {
        delegate.rotate(start, count, angle, axisX, axisY, axisZ);
        return this;
    }

    @Override
    public TileModel rotateXYZ(int start, int count, float pitch, float yaw, float roll) {
        delegate.rotateXYZ(start, count, pitch, yaw, roll);
        return this;
    }

    @Override
    public TileModel rotateZYX(int start, int count, float pitch, float yaw, float roll) {
        delegate.rotateZYX(start, count, pitch, yaw, roll);
        return this;
    }

    @Override
    public TileModel rotateYXZ(int start, int count, float pitch, float yaw, float roll) {
        delegate.rotateYXZ(start, count, pitch, yaw, roll);
        return this;
    }

    @Override
    public TileModel rotateByQuaternion(
            int start,
            int count,
            double x,
            double y,
            double z,
            double w
    ) {
        delegate.rotateByQuaternion(start, count, x, y, z, w);
        return this;
    }

    @Override
    public TileModel scale(int start, int count, float x, float y, float z) {
        delegate.scale(start, count, x, y, z);
        return this;
    }

    @Override
    public TileModel translate(int start, int count, float x, float y, float z) {
        delegate.translate(start, count, x, y, z);
        return this;
    }

    @Override
    public TileModel transform(int start, int count, MatrixM3f transform) {
        delegate.transform(start, count, transform);
        return this;
    }

    @Override
    public TileModel transform(
            int start,
            int count,
            float m00,
            float m01,
            float m02,
            float m10,
            float m11,
            float m12,
            float m20,
            float m21,
            float m22
    ) {
        delegate.transform(
                start,
                count,
                m00,
                m01,
                m02,
                m10,
                m11,
                m12,
                m20,
                m21,
                m22
        );
        return this;
    }

    @Override
    public TileModel transform(int start, int count, MatrixM4f transform) {
        delegate.transform(start, count, transform);
        return this;
    }

    @Override
    public TileModel transform(
            int start,
            int count,
            float m00,
            float m01,
            float m02,
            float m03,
            float m10,
            float m11,
            float m12,
            float m13,
            float m20,
            float m21,
            float m22,
            float m23,
            float m30,
            float m31,
            float m32,
            float m33
    ) {
        delegate.transform(
                start,
                count,
                m00,
                m01,
                m02,
                m03,
                m10,
                m11,
                m12,
                m13,
                m20,
                m21,
                m22,
                m23,
                m30,
                m31,
                m32,
                m33
        );
        return this;
    }

    @Override
    public TileModel reset(int size) {
        delegate.reset(size);
        substitutedFaces.clear(size, substitutedFaces.length());
        return this;
    }

    @Override
    public TileModel clear() {
        delegate.clear();
        substitutedFaces.clear();
        substituted = false;
        return this;
    }

    @Override
    public void sort() {
        delegate.sort();
    }
}

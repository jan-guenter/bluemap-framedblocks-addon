/*
 * SPDX-License-Identifier: LGPL-3.0-only
 */
package io.github.janguenter.bluemap.framedblocks.adapter.bluemap522;

import de.bluecolored.bluemap.core.world.mca.blockentity.MCABlockEntity;
import de.bluecolored.bluenbt.NBTName;

/** BlueNBT DTO retaining the FramedBlocks fields needed by exact profiles. */
public final class FramedBlockEntityData extends MCABlockEntity {

    private Object camo;

    @NBTName("camo_two")
    private Object camoTwo;

    private Boolean glowing;
    private Boolean intangible;
    private Boolean reinforced;
    private Byte updated;

    private int offsets;

    @NBTName("first_height")
    private int firstHeight;

    @NBTName("overlay_color")
    private Integer overlayColor;

    private String flower;
    private Object fluid;
    private Object item;
    private byte rotation;

    @NBTName("front_text")
    private Object frontText;

    @NBTName("back_text")
    private Object backText;

    public FramedBlockEntityData() {
    }

    public Object getCamo() {
        return camo;
    }

    public Object getCamoTwo() {
        return camoTwo;
    }

    public boolean isGlowing() {
        return Boolean.TRUE.equals(glowing);
    }

    public boolean isIntangible() {
        return Boolean.TRUE.equals(intangible);
    }

    public boolean isReinforced() {
        return Boolean.TRUE.equals(reinforced);
    }

    public byte getUpdated() {
        return updated == null ? Byte.MIN_VALUE : updated;
    }

    public boolean hasRequiredBaseFields() {
        return glowing != null
                && intangible != null
                && reinforced != null
                && updated != null;
    }

    public int getOffsets() {
        return offsets;
    }

    public int getFirstHeight() {
        return firstHeight;
    }

    public int getOverlayColor() {
        return overlayColor == null ? Integer.MIN_VALUE : overlayColor;
    }

    public boolean hasOverlayColor() {
        return overlayColor != null;
    }

    public String getFlower() {
        return flower;
    }

    public Object getFluid() {
        return fluid;
    }

    public Object getItem() {
        return item;
    }

    public byte getRotation() {
        return rotation;
    }

    public Object getFrontText() {
        return frontText;
    }

    public Object getBackText() {
        return backText;
    }
}

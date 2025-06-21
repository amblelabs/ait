package dev.amble.ait.core.roundels;

import net.minecraft.util.DyeColor;

public record RoundelType(RoundelPattern pattern, int color, boolean emissive) {
    public static final RoundelType EMPTY = new RoundelType(RoundelPatterns.BASE, DyeColor.WHITE.getSignColor(), false);

    public static RoundelType of(RoundelPattern pattern, int color) {
        return new RoundelType(pattern, color, false);
    }

    public static RoundelType of(RoundelPattern pattern, int color, boolean emissive) {
        return new RoundelType(pattern, color, emissive);
    }

    public static RoundelType empty() {
        return EMPTY;
    }
}

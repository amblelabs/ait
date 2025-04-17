package dev.amble.ait.core.roundels;

import net.minecraft.util.DyeColor;

public record RoundelType(RoundelPattern pattern, DyeColor color, boolean emissive) {
    public static final RoundelType EMPTY = new RoundelType(RoundelPatterns.BASE, DyeColor.WHITE, false);

    public static RoundelType of(RoundelPattern pattern, DyeColor color) {
        return new RoundelType(pattern, color, false);
    }

    public static RoundelType of(RoundelPattern pattern, DyeColor color, boolean emissive) {
        return new RoundelType(pattern, color, emissive);
    }

    public static RoundelType empty() {
        return EMPTY;
    }
}

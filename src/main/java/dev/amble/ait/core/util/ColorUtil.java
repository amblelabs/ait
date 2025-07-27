package dev.amble.ait.core.util;

public class ColorUtil {
    public static int blendColorsSoft(int color1, int color2) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = (r1 * 3 + r2) / 4;
        int g = (g1 * 3 + g2) / 4;
        int b = (b1 * 3 + b2) / 4;

        return (r << 16) | (g << 8) | b;
    }

}

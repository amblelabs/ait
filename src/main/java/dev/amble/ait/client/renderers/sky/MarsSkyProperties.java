package dev.amble.ait.client.renderers.sky;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MarsSkyProperties extends DimensionEffects {
    public static final float[] SUNSET_COLORS = {0,0  , 1, 1};

    public MarsSkyProperties() {
        super(Overworld.CLOUDS_HEIGHT, true, SkyType.NORMAL, false, false);
//            this.
    }

    public MarsSkyProperties(float cloudLevel, boolean hasGround, SkyType skyType, boolean forceBrightLightmap,
                             boolean constantAmbientLight) {
        super(cloudLevel, hasGround, skyType, forceBrightLightmap, constantAmbientLight);
    }

    //adjustSkyColor
    @Override
    public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
        return color.multiply(sunHeight * 0.91f + 0.09f, sunHeight * 0.94f + 0.06f, sunHeight * 0.94f + 0.06f);
    }

    //isFoggyAt
    @Override
    public boolean useThickFog(int camX, int camY) {
        return false;
    }

    @Override
    public float @Nullable [] getFogColorOverride(float p_230492_1, float p230492_2) {
        float g = MathHelper.cos(p_230492_1 * ((float)Math.PI * 2)) - 0.0f;
        if (g >= -0.5f && g <= 0.5f) {
            float i = (g - -0.0f) / 0.7f * 0.5f + 0.5f;
            float j = 1.0f - (1.0f - MathHelper.sin(i * (float)Math.PI)) * 0.99f;
            j *= j;
            SUNSET_COLORS[2] = i * 0.1f + 0.7f;
            SUNSET_COLORS[1] = i * i * 0.7f + 0.2f;
            SUNSET_COLORS[0] = i * i * 0.3f + 0.2f;
            SUNSET_COLORS[3] = j;
            return SUNSET_COLORS;
        }
        return null;
    }
}

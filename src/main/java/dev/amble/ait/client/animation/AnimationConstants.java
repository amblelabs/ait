package dev.amble.ait.client.animation;

import org.joml.Vector3f;

import net.minecraft.client.render.entity.animation.Transformation;

public class AnimationConstants {

    // Used to fix the STEP interpolation type for Blockbench's keyframes
    public static final Transformation.Interpolation STEP = (destination, delta, keyframes, start, end, scale) -> {
        Vector3f vector3f = keyframes[start].target();
        Vector3f vector3f2 = keyframes[end].target();
        return vector3f.lerp(vector3f2, 0, destination).mul(scale);
    };
}

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

    // Used to fix the BEZIER interpolation type for Blockbench's keyframes
    public static final Transformation.Interpolation BEZIER = (destination, delta, keyframes, start, end, scale) -> {
        Vector3f p0 = keyframes[start].target();
        Vector3f p1 = keyframes[start + 1].target();
        Vector3f p2 = keyframes[end - 1].target();
        Vector3f p3 = keyframes[end].target();

        float u = 1 - delta;
        float tt = delta * delta;
        float uu = u * u;
        float uuu = uu * u;
        float ttt = tt * delta;

        Vector3f result = new Vector3f();
        result.add(new Vector3f(p0).mul(uuu));
        result.add(new Vector3f(p1).mul(3 * uu * delta));
        result.add(new Vector3f(p2).mul(3 * u * tt));
        result.add(new Vector3f(p3).mul(ttt));
        return result.mul(scale);
    };
}

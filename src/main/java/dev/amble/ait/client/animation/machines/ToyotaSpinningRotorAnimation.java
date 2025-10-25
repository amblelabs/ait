package dev.amble.ait.client.animation.machines;// Save this class in your mod and generate all required imports

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

/**
 * Made with Blockbench 4.12.4
 * Exported for Minecraft version 1.19 or later with Yarn mappings
 * @author Loqor
 */
public class ToyotaSpinningRotorAnimation {
    public static final Animation START_UP = Animation.Builder.create(9.0F).looping()
        .addBoneAnimation("ring1", new Transformation(Transformation.Targets.ROTATE,
            new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(0.875F, AnimationHelper.createRotationalVector(0.0F, -15.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(1.7083F, AnimationHelper.createRotationalVector(0.0F, -35.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(2.5417F, AnimationHelper.createRotationalVector(0.0F, -60.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(3.25F, AnimationHelper.createRotationalVector(0.0F, -90.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(9.0F, AnimationHelper.createRotationalVector(0.0F, -360.0F, 0.0F), Transformation.Interpolations.LINEAR)
        ))
        .addBoneAnimation("ring2", new Transformation(Transformation.Targets.ROTATE,
            new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(0.875F, AnimationHelper.createRotationalVector(0.0F, 15.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(1.7083F, AnimationHelper.createRotationalVector(0.0F, 35.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(2.5417F, AnimationHelper.createRotationalVector(0.0F, 60.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(3.25F, AnimationHelper.createRotationalVector(0.0F, 90.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(9.0F, AnimationHelper.createRotationalVector(0.0F, 360.0F, 0.0F), Transformation.Interpolations.LINEAR)
        ))
        .build();

    public static final Animation FLIGHT = Animation.Builder.create(9.0F).looping()
        .addBoneAnimation("ring1", new Transformation(Transformation.Targets.ROTATE,
            new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(9.0F, AnimationHelper.createRotationalVector(0.0F, -360.0F, 0.0F), Transformation.Interpolations.LINEAR)
        ))
        .addBoneAnimation("ring2", new Transformation(Transformation.Targets.ROTATE,
            new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(9.0F, AnimationHelper.createRotationalVector(0.0F, 360.0F, 0.0F), Transformation.Interpolations.LINEAR)
        ))
        .build();

    public static final Animation STOP = Animation.Builder.create(9.0F)
        .addBoneAnimation("ring1", new Transformation(Transformation.Targets.ROTATE,
            new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 360.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(6.25F, AnimationHelper.createRotationalVector(0.0F, 90.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(6.9583F, AnimationHelper.createRotationalVector(0.0F, 60.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(7.5417F, AnimationHelper.createRotationalVector(0.0F, 35.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(8.125F, AnimationHelper.createRotationalVector(0.0F, 15.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(9.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
        ))
        .addBoneAnimation("ring2", new Transformation(Transformation.Targets.ROTATE,
            new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, -360.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(6.25F, AnimationHelper.createRotationalVector(0.0F, -90.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(6.9583F, AnimationHelper.createRotationalVector(0.0F, -60.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(7.5417F, AnimationHelper.createRotationalVector(0.0F, -35.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(8.125F, AnimationHelper.createRotationalVector(0.0F, -15.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(9.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
        ))
        .build();

    public static final Animation FLIGHT_APERTURE = Animation.Builder.create(9.0F).looping()
        .addBoneAnimation("ring1", new Transformation(Transformation.Targets.ROTATE,
            new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(9.0F, AnimationHelper.createRotationalVector(0.0F, -720.0F, 0.0F), Transformation.Interpolations.LINEAR)
        ))
        .addBoneAnimation("ring3", new Transformation(Transformation.Targets.ROTATE,
            new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(9.0F, AnimationHelper.createRotationalVector(0.0F, -1440.0F, 0.0F), Transformation.Interpolations.LINEAR)
        ))
        .addBoneAnimation("ring2", new Transformation(Transformation.Targets.ROTATE,
            new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(9.0F, AnimationHelper.createRotationalVector(0.0F, -1440.0F, 0.0F), Transformation.Interpolations.LINEAR)
        ))
        .build();
}
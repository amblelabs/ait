package dev.amble.ait.client.animation.decoration.flag;// Save this class in your mod and generate all required imports

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

/**
 * Made with Blockbench 4.11.2
 * Exported for Minecraft version 1.19 or later with Yarn mappings
 * @author Author
 */
public class FlagAnimations {
    public static final Animation ANIMATION_FLAG_PLANT = Animation.Builder.create(0.25F)
        .addBoneAnimation("flag_elements", new Transformation(Transformation.Targets.SCALE,
            new Keyframe(0.0F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(0.1667F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
        ))
        .addBoneAnimation("element2", new Transformation(Transformation.Targets.SCALE,
            new Keyframe(0.0F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(0.25F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
        ))
        .addBoneAnimation("element3", new Transformation(Transformation.Targets.SCALE,
            new Keyframe(0.0F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(0.25F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
        ))
        .addBoneAnimation("clamp", new Transformation(Transformation.Targets.SCALE,
            new Keyframe(0.0F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(0.1667F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
        ))
        .build();

    public static final Animation ANIMATION_FLAG_WAVE = Animation.Builder.create(1.1667F).looping()
        .addBoneAnimation("flag_elements", new Transformation(Transformation.Targets.ROTATE,
            new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
            new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 2.5F, 0.0F), Transformation.Interpolations.CUBIC),
            new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
            new Keyframe(0.8333F, AnimationHelper.createRotationalVector(0.0F, -2.5F, 0.0F), Transformation.Interpolations.CUBIC),
            new Keyframe(1.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
        ))
        .addBoneAnimation("element2", new Transformation(Transformation.Targets.ROTATE,
            new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
            new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 2.5F, 0.0F), Transformation.Interpolations.CUBIC),
            new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
            new Keyframe(0.8333F, AnimationHelper.createRotationalVector(0.0F, -2.5F, 0.0F), Transformation.Interpolations.CUBIC),
            new Keyframe(1.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
        ))
        .addBoneAnimation("element3", new Transformation(Transformation.Targets.ROTATE,
            new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
            new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 2.5F, 0.0F), Transformation.Interpolations.CUBIC),
            new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
            new Keyframe(0.8333F, AnimationHelper.createRotationalVector(0.0F, -2.5F, 0.0F), Transformation.Interpolations.CUBIC),
            new Keyframe(1.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
        ))
        .build();
}
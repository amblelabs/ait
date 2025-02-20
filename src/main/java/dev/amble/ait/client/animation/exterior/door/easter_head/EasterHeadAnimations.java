package dev.amble.ait.client.animation.exterior.door.easter_head;

import static dev.amble.ait.client.animation.AnimationConstants.STEP;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

public class EasterHeadAnimations {
    // EXTERIOR
    public static final Animation EASTER_HEAD_EXTERIOR_OPEN_ANIMATION = Animation.Builder.create(0.88f)
            .addBoneAnimation("door",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.041676664f, AnimationHelper.createTranslationalVector(-0.25f, 0f, -0.25f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.08343333f, AnimationHelper.createTranslationalVector(0.25f, 0f, -0.5f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.125f, AnimationHelper.createTranslationalVector(-0.25f, 0f, -0.25f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.20834334f, AnimationHelper.createTranslationalVector(0.25f, 0f, -0.5f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.2916767f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.3433333f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.375f, AnimationHelper.createTranslationalVector(-0.25f, 0f, -0.25f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.4167667f, AnimationHelper.createTranslationalVector(0.25f, 0f, -0.5f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.4583433f, AnimationHelper.createTranslationalVector(-0.25f, 0f, -0.25f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5416766f, AnimationHelper.createTranslationalVector(0.25f, 0f, -0.5f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.625f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.6766666f, AnimationHelper.createTranslationalVector(0.25f, 0f, -0.5f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.7083434f, AnimationHelper.createTranslationalVector(-0.25f, 0f, -0.25f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.7916766f, AnimationHelper.createTranslationalVector(0.25f, 0f, -0.5f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.875f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("door",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0.375f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.6766666f, AnimationHelper.createRotationalVector(-67.5f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.875f, AnimationHelper.createRotationalVector(-67.5f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();

    public static final Animation EASTER_HEAD_EXTERIOR_CLOSE_ANIMATION = Animation.Builder.create(0.88f)
            .addBoneAnimation("door",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.041676664f, AnimationHelper.createTranslationalVector(-0.25f, 0f, -0.25f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.08343333f, AnimationHelper.createTranslationalVector(0.25f, 0f, -0.5f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.125f, AnimationHelper.createTranslationalVector(-0.25f, 0f, -0.25f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.20834334f, AnimationHelper.createTranslationalVector(0.25f, 0f, -0.5f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.2916767f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.3433333f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.375f, AnimationHelper.createTranslationalVector(-0.25f, 0f, -0.25f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.4167667f, AnimationHelper.createTranslationalVector(0.25f, 0f, -0.5f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.4583433f, AnimationHelper.createTranslationalVector(-0.25f, 0f, -0.25f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5416766f, AnimationHelper.createTranslationalVector(0.25f, 0f, -0.5f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.625f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.6766666f, AnimationHelper.createTranslationalVector(0.25f, 0f, -0.5f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.7083434f, AnimationHelper.createTranslationalVector(-0.25f, 0f, -0.25f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.7916766f, AnimationHelper.createTranslationalVector(0.25f, 0f, -0.5f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.875f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("door",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0.375f, AnimationHelper.createRotationalVector(-67.5f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.6766666f, AnimationHelper.createRotationalVector(-7.5f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.875f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();

    // DOOR
    public static final Animation EASTER_HEAD_INTERIOR_DOOR_OPEN_ANIMATION = Animation.Builder.create(0.88f)
            .addBoneAnimation("door",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(0.875f, AnimationHelper.createScalingVector(0f, 0f, 0f), STEP)))
            .build();

    public static final Animation EASTER_HEAD_INTERIOR_DOOR_CLOSE_ANIMATION = Animation.Builder.create(0.88f)
            .addBoneAnimation("door",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.875f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP)))
            .build();
}

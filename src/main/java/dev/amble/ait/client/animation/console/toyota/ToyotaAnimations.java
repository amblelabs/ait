package dev.amble.ait.client.animation.console.toyota;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

import static dev.amble.ait.client.animation.AnimationConstants.STEP;

public class ToyotaAnimations {

    public static final Animation CONSOLE_TOYOTA_IDLE = Animation.Builder.create(4f).looping()
            .addBoneAnimation("powerlights2",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 1f, 0f), STEP)))
            .addBoneAnimation("doorlights2",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 1f, 0f), STEP)))
            .addBoneAnimation("lights2",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 2f, 0f), STEP)))
            .addBoneAnimation("lights1",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 2f, 0f), STEP)))
            .addBoneAnimation("flightlights2",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 1f, 0f), STEP)))
            .addBoneAnimation("handbrakelights2",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 1f, 0f), STEP)))
            .addBoneAnimation("yellow6",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 1f, 0f), STEP)))
            .addBoneAnimation("yellow5",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 1f, 0f), STEP)))
            .addBoneAnimation("yellow4",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 1f, 0f), STEP)))
            .addBoneAnimation("yellow3",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 1f, 0f), STEP)))
            .addBoneAnimation("gallifreyan2",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4f, AnimationHelper.createRotationalVector(-360f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("gallifreyan3",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4f, AnimationHelper.createRotationalVector(-360f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation CONSOLE_TOYOTA_FLIGHT = Animation.Builder.create(4f).looping()
            .addBoneAnimation("powerlights2",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 1f, 0f), STEP)))
            .addBoneAnimation("doorlights2",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 1f, 0f), STEP)))
            .addBoneAnimation("lights2",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 2f, 0f), STEP)))
            .addBoneAnimation("lights1",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 2f, 0f), STEP)))
            .addBoneAnimation("flightlights2",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 1f, 0f), STEP)))
            .addBoneAnimation("handbrakelights2",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 1f, 0f), STEP)))
            .addBoneAnimation("yellow6",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 1f, 0f), STEP)))
            .addBoneAnimation("yellow5",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 1f, 0f), STEP)))
            .addBoneAnimation("yellow4",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 1f, 0f), STEP)))
            .addBoneAnimation("yellow3",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 1f, 0f), STEP)))
            .addBoneAnimation("gallifreyan2",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4f, AnimationHelper.createRotationalVector(-360f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("gallifreyan3",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4f, AnimationHelper.createRotationalVector(-360f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("uppertimepiece",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.25f, AnimationHelper.createTranslationalVector(0f, 3f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.875f, AnimationHelper.createTranslationalVector(0f, 3.4f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.5f, AnimationHelper.createTranslationalVector(0f, 3f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
}

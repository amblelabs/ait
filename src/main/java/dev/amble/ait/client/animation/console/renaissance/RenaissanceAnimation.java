package dev.amble.ait.client.animation.console.renaissance;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

public class RenaissanceAnimation {
        public static final Animation FLIGHT = Animation.Builder.create(4.0F).looping()
                .addBoneAnimation("rotorlight", new Transformation(Transformation.Targets.TRANSLATE,
                        new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 6.0F, 0.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createTranslationalVector(0.0F, -6.0F, 0.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
                ))
                .addBoneAnimation("rotorrings", new Transformation(Transformation.Targets.TRANSLATE,
                        new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 0.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("bone37", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("bone39", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("undefined", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("undefined", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("undefined", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("undefined", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("undefined", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("undefined", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("undefined", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("undefined", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("undefined", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("undefined", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("bone9", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("bone10", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("bone16", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("bone17", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("bone23", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("bone24", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("bone30", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("bone31", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("bone43", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("bone44", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 20.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("symbol", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(2.0F, AnimationHelper.createRotationalVector(-360.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
                ))
                .addBoneAnimation("lightblinkythingidk", new Transformation(Transformation.Targets.TRANSLATE,
                        new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
                ))
                .addBoneAnimation("lightblinkythingidk", new Transformation(Transformation.Targets.SCALE,
                        new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.1F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.499F, AnimationHelper.createScalingVector(1.0F, 1.1F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.5F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.999F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(3.0F, AnimationHelper.createScalingVector(1.0F, 1.1F, 1.0F), Transformation.Interpolations.LINEAR)
                ))
                .addBoneAnimation("lightblinkythingidk2", new Transformation(Transformation.Targets.TRANSLATE,
                        new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
                ))
                .addBoneAnimation("lightblinkythingidk2", new Transformation(Transformation.Targets.SCALE,
                        new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.1F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.999F, AnimationHelper.createScalingVector(1.0F, 1.1F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.499F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.5F, AnimationHelper.createScalingVector(1.0F, 1.1F, 1.0F), Transformation.Interpolations.LINEAR)
                ))
                .addBoneAnimation("lightblinkythingidk3", new Transformation(Transformation.Targets.TRANSLATE,
                        new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
                ))
                .addBoneAnimation("lightblinkythingidk3", new Transformation(Transformation.Targets.SCALE,
                        new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.1F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.499F, AnimationHelper.createScalingVector(1.0F, 1.1F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.5F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.999F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.0F, AnimationHelper.createScalingVector(1.0F, 1.1F, 1.0F), Transformation.Interpolations.LINEAR)
                ))
                .addBoneAnimation("lamp1", new Transformation(Transformation.Targets.SCALE,
                        new Keyframe(0.0F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.499F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.5F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.749F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.75F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.999F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.249F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.25F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR)
                ))
                .addBoneAnimation("lamp2", new Transformation(Transformation.Targets.SCALE,
                        new Keyframe(0.0F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.749F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.999F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.0F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.249F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.25F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.499F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.5F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR)
                ))
                .addBoneAnimation("lamp3", new Transformation(Transformation.Targets.SCALE,
                        new Keyframe(0.0F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.999F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.249F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.25F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.499F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.5F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.749F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.75F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR)
                ))
                .addBoneAnimation("lamp4", new Transformation(Transformation.Targets.SCALE,
                        new Keyframe(0.0F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.249F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.25F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.499F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.5F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.749F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.999F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(3.0F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR)
                ))
                .addBoneAnimation("lamp5", new Transformation(Transformation.Targets.SCALE,
                        new Keyframe(0.0F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.499F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.5F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.749F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.75F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.999F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(3.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(3.249F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(3.25F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR)
                ))
                .addBoneAnimation("lamp6", new Transformation(Transformation.Targets.SCALE,
                        new Keyframe(0.0F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.749F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(1.999F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(2.0F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(3.249F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR),
                        new Keyframe(3.25F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(3.499F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
                        new Keyframe(3.5F, AnimationHelper.createScalingVector(1.1F, 1.1F, 1.1F), Transformation.Interpolations.LINEAR)
                ))
                .build();

        public static final Animation IDLE = Animation.Builder.create(4.0F).looping()
                .addBoneAnimation("symbol", new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(2.0F, AnimationHelper.createRotationalVector(-360.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                        new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
                ))
                .build();
}
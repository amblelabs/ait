package dev.amble.ait.client.animation.console.hartnell;

import static dev.amble.ait.client.animation.AnimationConstants.STEP;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

public class HartnellAnimations {

    public static final Animation ROTOR = Animation.Builder.create(3.4f).looping()
            .addBoneAnimation("rotor",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.68f, AnimationHelper.createTranslationalVector(0f, -4f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(3.36f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("compass",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.68f, AnimationHelper.createRotationalVector(0f, 180f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(3.36f, AnimationHelper.createRotationalVector(0f, 360f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_INFLIGHT_ANIMATION = Animation.Builder.create(8f).looping()
            .addBoneAnimation("rotor",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2f, AnimationHelper.createTranslationalVector(0f, -4f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6f, AnimationHelper.createTranslationalVector(0f, -4f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(8f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("compass",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(8f, AnimationHelper.createRotationalVector(0f, -360f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone166",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(1.3433333f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(4f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(5.343333f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(8f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP)))
            .addBoneAnimation("bone169",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(1.3433333f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(4f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(5.343333f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(8f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP)))
            .addBoneAnimation("bone167",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(1.3433333f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(2.6766665f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(4f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(5.343333f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(6.676667f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP)))
            .addBoneAnimation("bone170",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(1.3433333f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(2.6766665f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(4f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(5.343333f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(6.676667f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP)))
            .addBoneAnimation("bone168",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(2.6766665f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(4f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(6.676667f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(8f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP)))
            .addBoneAnimation("bone171",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(2.6766665f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(4f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(6.676667f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(8f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP)))
            .addBoneAnimation("bone91",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.3433333f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.5834334f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(2.2083435f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(2.7916765f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(5.041677f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(6.208343f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(6.834333f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(7.416767f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone93",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(2.75f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(3.9167665f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(5.25f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(6.416767f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(7.343333f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(7.676667f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone92",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.75f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(1.2916767f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(2f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(2.6766665f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(3.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(4.208343f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(4.75f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(5.5f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(6.167667f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(7.083433f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(7.375f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(7.416767f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(7.958343f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone94",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.6766666f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(1.2916767f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(2.1676665f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(3.0416765f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(4.083433f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(4.958343f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(5.541677f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(6.041677f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(6.791677f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(7.25f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(7.791677f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone95",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.2916767f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.7916766f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(1.0834333f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(1.9167667f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(2.2083435f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(3.0834335f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(3.375f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(4.208343f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(4.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(5.416767f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(5.708343f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(5.958343f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(6.25f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(6.958343f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(7.25f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(7.375f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(7.676667f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone109",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.4583433f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.9583434f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.4167667f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.6766667f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.0834335f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.3433335f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.7916765f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.2916765f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.75f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.416767f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.834333f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5.375f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5.791677f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6.041677f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6.5f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6.916767f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.167667f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.625f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.958343f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("bone116",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.4583433f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(1.375f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(2.5f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(3.5834335f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(4.791677f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(5.834333f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(6.676667f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(6.958343f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(7.291677f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(7.583433f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(7.834333f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(8f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone126",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5416766f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.125f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.625f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.9167667f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.4167665f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.6766665f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.2083435f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.7916765f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.291677f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.583433f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5.083433f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5.541677f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6.167667f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6.676667f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6.958343f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.458343f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.875f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("bone127",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.3433333f, AnimationHelper.createRotationalVector(0f, 20f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.75f, AnimationHelper.createRotationalVector(0f, 40f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.1676667f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.625f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.0834335f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.5f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.9583435f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.25f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.7083435f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.083433f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.416767f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.541677f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.916767f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5.625f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6.125f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6.708343f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.167667f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.458343f, AnimationHelper.createRotationalVector(0f, 45f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.916767f, AnimationHelper.createRotationalVector(0f, 17.5f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("bone131",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, -0.7f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.4167667f, AnimationHelper.createTranslationalVector(0f, 0f, 0.1f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, -0.3f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0.7f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.125f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.6766665f, AnimationHelper.createTranslationalVector(0f, 0f, 0.6f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.2916765f, AnimationHelper.createTranslationalVector(0f, 0f, -0.6f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.041677f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.5f, AnimationHelper.createTranslationalVector(0f, 0f, -0.6f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.958343f, AnimationHelper.createTranslationalVector(0f, 0f, 0.5f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(5.416767f, AnimationHelper.createTranslationalVector(0f, 0f, -0.7f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6f, AnimationHelper.createTranslationalVector(0f, 0f, -0.3f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(6.583433f, AnimationHelper.createTranslationalVector(0f, 0f, -0.6f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.167667f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.5f, AnimationHelper.createTranslationalVector(0f, 0f, -0.5f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(7.676667f, AnimationHelper.createTranslationalVector(0f, 0f, -0.2f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(8f, AnimationHelper.createTranslationalVector(0f, 0f, -0.7f),
                                    Transformation.Interpolations.CUBIC)))
            .build();

    public static final Animation HARTNELL_IDLE_ANIMATION = Animation.Builder.create(8f).looping()
            .addBoneAnimation("bone33",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.8343333f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone91",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.8343334f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(2.1676665f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(3.5416765f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(5.167667f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone93",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(2.0834335f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(3.0834335f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(4.083433f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(6.291677f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(7.958343f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone92",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(1.4167667f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(2f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(2.3433335f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(2.625f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(4.5f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(5.875f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(7.083433f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(7.676667f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone94",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(6f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(6.5f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(7f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone95",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(2.2083435f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(3.75f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(4.083433f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(5.167667f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(6.583433f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(7.416767f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("m_sensor_1",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone109",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.7083434f, AnimationHelper.createRotationalVector(0f, -17.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.9167666f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.1676667f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.375f, AnimationHelper.createRotationalVector(0f, -17.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.5834333f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(2.25f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(2.4583435f, AnimationHelper.createRotationalVector(0f, -17.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(2.6766665f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(3.2083435f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(3.4167665f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(3.5834335f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(3.625f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(3.7916765f, AnimationHelper.createRotationalVector(0f, -17.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(4f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(4.676667f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(4.875f, AnimationHelper.createRotationalVector(0f, -17.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(5.083433f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(5.75f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(5.958343f, AnimationHelper.createRotationalVector(0f, -17.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(6.167667f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(6.375f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(6.583433f, AnimationHelper.createRotationalVector(0f, -17.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(6.708343f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(6.791677f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(6.916767f, AnimationHelper.createRotationalVector(0f, -17.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(7.125f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(7.375f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(7.583433f, AnimationHelper.createRotationalVector(0f, -17.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(7.791677f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone116",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(2.2083435f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(3.5f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(4.625f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(6.5f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone126",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.2083433f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.4167667f, AnimationHelper.createRotationalVector(0f, -17.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.625f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(4f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(4.208343f, AnimationHelper.createRotationalVector(0f, -17.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(4.416767f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(6.583433f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(6.791677f, AnimationHelper.createRotationalVector(0f, -17.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(7f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone127",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.2916767f, AnimationHelper.createRotationalVector(0f, -8.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5834334f, AnimationHelper.createRotationalVector(0f, -27.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.9167666f, AnimationHelper.createRotationalVector(0f, -7.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.25f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(2f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(2.2916765f, AnimationHelper.createRotationalVector(0f, -17.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(2.5834335f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(3.5834335f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(3.8343335f, AnimationHelper.createRotationalVector(0f, -17.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(4.125f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(4.834333f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(5.125f, AnimationHelper.createRotationalVector(0f, -17.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(5.375f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(5.916767f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(6.167667f, AnimationHelper.createRotationalVector(0f, -17.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(6.416767f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(6.5f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(6.75f, AnimationHelper.createRotationalVector(0f, -17.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(7f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone131",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, -0.75f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5834334f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, -0.75f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.4583433f, AnimationHelper.createTranslationalVector(0f, 0f, -0.75f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(2.0416765f, AnimationHelper.createTranslationalVector(0f, 0f, 0.2f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(2.4583435f, AnimationHelper.createTranslationalVector(0f, 0f, -0.75f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(3.375f, AnimationHelper.createTranslationalVector(0f, 0f, -0.75f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(3.9583435f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(4.375f, AnimationHelper.createTranslationalVector(0f, 0f, -0.75f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(4.708343f, AnimationHelper.createTranslationalVector(0f, 0f, -0.75f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(5.291677f, AnimationHelper.createTranslationalVector(0f, 0f, -0.4f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(5.708343f, AnimationHelper.createTranslationalVector(0f, 0f, -0.75f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(6.041677f, AnimationHelper.createTranslationalVector(0f, 0f, -0.75f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(6.583433f, AnimationHelper.createTranslationalVector(0f, 0f, 0.6f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(6.834333f, AnimationHelper.createTranslationalVector(0f, 0f, 0.4f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(7.125f, AnimationHelper.createTranslationalVector(0f, 0f, 0.6f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(7.5f, AnimationHelper.createTranslationalVector(0f, 0f, -0.75f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("compass",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(8f, AnimationHelper.createRotationalVector(0f, -180f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone166",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(1.3433333f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(4f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(5.343333f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP)))
            .addBoneAnimation("bone169",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(1.3433333f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(4f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(5.343333f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP)))
            .addBoneAnimation("bone167",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(1.3433333f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(2.6766665f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(5.343333f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(6.676667f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP)))
            .addBoneAnimation("bone170",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(1.3433333f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(2.6766665f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(5.343333f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(6.676667f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP)))
            .addBoneAnimation("bone168",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(2.6766665f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(4f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(6.676667f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(8f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP)))
            .addBoneAnimation("bone171",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(2.6766665f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(4f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP),
                            new Keyframe(6.676667f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP),
                            new Keyframe(8f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP)))
            .build();

    public static final Animation HARTNELL_CONTROL_HAILMARY_OFF_ANIMATION = Animation.Builder.create(0.5f)
            .addBoneAnimation("bone61",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("bone97",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .build();
    public static final Animation HARTNELL_CONTROL_HAILMARY_ON_ANIMATION = Animation.Builder.create(0.5f)
            .addBoneAnimation("bone61",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("bone97",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .build();
    public static final Animation HARTNELL_CONTROL_DIMENSION_FIRST_ANIMATION = Animation.Builder.create(1.75f)
            .addBoneAnimation("bone86",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone87",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.16766666f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone88",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone89",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.375f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone90",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.75f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone62",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.375f, AnimationHelper.createRotationalVector(0f, 15f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.6766667f, AnimationHelper.createRotationalVector(0f, 312.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.75f, AnimationHelper.createRotationalVector(0f, 360f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("bone63",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.375f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 70f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone65",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 70f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.16766666f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone64",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 70f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.75f, AnimationHelper.createRotationalVector(0f, 0f, 70f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone80",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 70f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.16766666f, AnimationHelper.createRotationalVector(0f, 0f, 70f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.375f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone66",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.75f, AnimationHelper.createRotationalVector(0f, 0f, 70f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone81",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.25f, AnimationHelper.createRotationalVector(0f, 0f, 70f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_DIMENSION_SECOND_ANIMATION = Animation.Builder.create(1.75f)
            .addBoneAnimation("bone86",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.5416766f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone87",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.16766666f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone88",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone89",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.3433333f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone90",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.75f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone62",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.2083433f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.3433333f, AnimationHelper.createRotationalVector(0f, 15f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.625f, AnimationHelper.createRotationalVector(0f, 312.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.75f, AnimationHelper.createRotationalVector(0f, 360f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .addBoneAnimation("bone63",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 70f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.3433333f, AnimationHelper.createRotationalVector(0f, 0f, 70f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5416766f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone65",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.16766666f, AnimationHelper.createRotationalVector(0f, 0f, 70f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone64",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.75f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 70f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone80",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.16766666f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.3433333f, AnimationHelper.createRotationalVector(0f, 0f, 70f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone66",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 70f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5416766f, AnimationHelper.createRotationalVector(0f, 0f, 70f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.75f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone81",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 70f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 70f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.2083433f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_RANDOMISER_ANIMATION = Animation.Builder.create(1.75f)
            .addBoneAnimation("bone85",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.3433333f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.4167667f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.5834334f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.6766666f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.75f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.8343334f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.9167666f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(1.0834333f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(1.1676667f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(1.25f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(1.3433333f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(1.4167667f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(1.5834333f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(1.6766667f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(1.75f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone79",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.5f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.75f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_DOORCONTROL_OPEN_ANIMATION = Animation.Builder.create(0.375f)
            .addBoneAnimation("bone117",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.375f, AnimationHelper.createTranslationalVector(0f, -2f, 0f), STEP)))
            .addBoneAnimation("bone123",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.375f, AnimationHelper.createRotationalVector(0f, 180f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_DOORCONTROL_CLOSE_ANIMATION = Animation.Builder.create(0.375f)
            .addBoneAnimation("bone117",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.375f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone123",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 180f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.375f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_DOORLOCK_UNLOCKED_ANIMATION = Animation.Builder.create(0.375f)
            .addBoneAnimation("bone118",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.375f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone125",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.375f, AnimationHelper.createRotationalVector(0f, 180f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_DOORLOCK_LOCKED_ANIMATION = Animation.Builder.create(0.375f)
            .addBoneAnimation("bone118",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.375f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone125",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 180f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.375f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_LANDTYPE_ANIMATION = Animation.Builder.create(1.25f)
            .addBoneAnimation("bone129",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.08343333f, AnimationHelper.createTranslationalVector(0f, 0f, -0.15f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.20834334f, AnimationHelper.createTranslationalVector(0f, 0f, -1f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.2916767f, AnimationHelper.createTranslationalVector(-0.15f, 0f, -1f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.4167667f, AnimationHelper.createTranslationalVector(-1f, 0f, -1f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(-0.85f, 0f, -1f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.7083434f, AnimationHelper.createTranslationalVector(0f, 0f, -1f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.7916766f, AnimationHelper.createTranslationalVector(0f, 0f, -1.25f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, -2f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.0834333f, AnimationHelper.createTranslationalVector(0f, 0f, -1.58f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.25f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_FASTRETURN_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone25",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.125f, AnimationHelper.createTranslationalVector(0f, -0.25f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_XINC_ANIMATION = Animation.Builder.create(0.5f)
            .addBoneAnimation("bone70",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone82",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .build();
    public static final Animation HARTNELL_CONTROL_YINC_ANIMATION = Animation.Builder.create(0.5f)
            .addBoneAnimation("bone76",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone83",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .build();
    public static final Animation HARTNELL_CONTROL_ZINC_ANIMATION = Animation.Builder.create(0.5f)
            .addBoneAnimation("bone77",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone84",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .build();
    public static final Animation HARTNELL_CONTROL_XYZINC_1_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone74",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 120f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_XYZINC_10_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone74",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 25f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_XYZINC_100_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone74",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 25f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 75f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_XYZINC_1000_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone74",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 75f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 120f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_THROTTLE_1_FIRST_ANIMATION = Animation.Builder.create(0.5f)
            .addBoneAnimation("bone45",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 52.5f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_THROTTLE_1_SECOND_ANIMATION = Animation.Builder.create(0.5f)
            .addBoneAnimation("bone45",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 52.5f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_AUTOPILOT_ON_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone26",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.08343333f, AnimationHelper.createRotationalVector(0f, 5.42f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 62.5f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone145",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .build();
    public static final Animation HARTNELL_CONTROL_AUTOPILOT_OFF_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone26",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 62.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.08343333f, AnimationHelper.createRotationalVector(0f, 57.08f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone145",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .build();
    public static final Animation HARTNELL_CONTROL_HANDBRAKE_ON_ANIMATION = Animation.Builder.create(0.5f)
            .addBoneAnimation("bone46",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.125f, AnimationHelper.createRotationalVector(0f, 0f, 5f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 52.5f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_HANDBRAKE_OFF_ANIMATION = Animation.Builder.create(0.5f)
            .addBoneAnimation("bone46",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 52.5f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_ANTIGRAV_ON_ANIMATION = Animation.Builder.create(0.125f)
            .addBoneAnimation("bone33",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.125f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_ANTIGRAV_OFF_ANIMATION = Animation.Builder.create(0.125f)
            .addBoneAnimation("bone33",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.125f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_REFUELER_ON_ANIMATION = Animation.Builder.create(0.375f)
            .addBoneAnimation("bone106",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.375f, AnimationHelper.createRotationalVector(0f, 180f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_REFUELER_OFF_ANIMATION = Animation.Builder.create(0.375f)
            .addBoneAnimation("bone106",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 180f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.375f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_DIRECTION_NORTH_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone59",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 27.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 117.5f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_DIRECTION_EAST_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone59",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 117.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 207.5f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_DIRECTION_SOUTH_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone59",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 207.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 297.5f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_DIRECTION_WEST_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone59",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 297.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 387.5f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH1_ON_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone34",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH1_OFF_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone34",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH2_ON_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone35",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH2_OFF_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone35",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH3_ON_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone47",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH3_OFF_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone47",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_LEVER1_ON_ANIMATION = Animation.Builder.create(0.5f)
            .addBoneAnimation("bone138",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone136",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone143",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.125f, AnimationHelper.createRotationalVector(0f, 0f, 5f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_LEVER1_OFF_ANIMATION = Animation.Builder.create(0.5f)
            .addBoneAnimation("bone138",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone136",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone143",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.125f, AnimationHelper.createRotationalVector(0f, 0f, 85f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_LEVER2_ON_ANIMATION = Animation.Builder.create(0.5f)
            .addBoneAnimation("bone135",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone140",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone144",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.125f, AnimationHelper.createRotationalVector(0f, 0f, 5f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_LEVER2_OFF_ANIMATION = Animation.Builder.create(0.5f)
            .addBoneAnimation("bone135",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone140",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone144",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.125f, AnimationHelper.createRotationalVector(0f, 0f, 85f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_LEVER3_ON_ANIMATION = Animation.Builder.create(0.5f)
            .addBoneAnimation("bone139",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone137",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone146",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.125f, AnimationHelper.createRotationalVector(0f, 0f, 5f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_LEVER3_OFF_ANIMATION = Animation.Builder.create(0.5f)
            .addBoneAnimation("bone139",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone137",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone146",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.125f, AnimationHelper.createRotationalVector(0f, 0f, 85f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_LEVER4_ON_ANIMATION = Animation.Builder.create(0.5f)
            .addBoneAnimation("bone142",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 52.5f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_LEVER4_OFF_ANIMATION = Animation.Builder.create(0.5f)
            .addBoneAnimation("bone142",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 52.5f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_CRANK1_ON_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone147",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 55f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_CRANK1_OFF_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone147",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 55f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_CRANK2_ON_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone148",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, -55f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_CRANK2_OFF_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone148",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, -55f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH4_ON_ANIMATION = Animation.Builder.create(0.16766666f)
            .addBoneAnimation("bone124",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.16766666f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH4_OFF_ANIMATION = Animation.Builder.create(0.16766666f)
            .addBoneAnimation("bone124",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.16766666f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH5_ON_ANIMATION = Animation.Builder.create(0.16766666f)
            .addBoneAnimation("bone128",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.16766666f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH5_OFF_ANIMATION = Animation.Builder.create(0.16766666f)
            .addBoneAnimation("bone128",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.16766666f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_TOGGLESWITCH1_ON_ANIMATION = Animation.Builder.create(0.75f)
            .addBoneAnimation("bone120",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone121",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone119",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.75f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_TOGGLESWITCH1_OFF_ANIMATION = Animation.Builder.create(0.75f)
            .addBoneAnimation("bone120",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone121",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone119",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.75f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_TOGGLESWITCH2_ON_ANIMATION = Animation.Builder.create(0.75f)
            .addBoneAnimation("bone111",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone102",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.75f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone115",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone104",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone105",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.75f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_TOGGLESWITCH2_OFF_ANIMATION = Animation.Builder.create(0.75f)
            .addBoneAnimation("bone115",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone104",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone105",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.75f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone111",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone102",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.75f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .build();
    public static final Animation HARTNELL_CONTROL_TURNSWITCH1_ON_ANIMATION = Animation.Builder.create(0.375f)
            .addBoneAnimation("bone101",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.375f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone108",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 180f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.375f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_TURNSWITCH1_OFF_ANIMATION = Animation.Builder.create(0.375f)
            .addBoneAnimation("bone101",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.375f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone108",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.375f, AnimationHelper.createRotationalVector(0f, 180f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_TURNSWITCH2_ON_ANIMATION = Animation.Builder.create(0.375f)
            .addBoneAnimation("bone103",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.375f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone107",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.375f, AnimationHelper.createRotationalVector(0f, 180f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_TURNSWITCH2_OFF_ANIMATION = Animation.Builder.create(0.375f)
            .addBoneAnimation("bone103",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.375f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone107",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 180f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.375f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_CRANK3_ON_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone75",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 77.5f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_CRANK3_OFF_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone75",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 77.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_CRANK4_ON_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone78",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 67.5f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_CRANK4_OFF_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone78",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 67.5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_LEVER5_ON_ANIMATION = Animation.Builder.create(0.375f)
            .addBoneAnimation("bone71",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.375f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone96",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.375f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .build();
    public static final Animation HARTNELL_CONTROL_LEVER5_OFF_ANIMATION = Animation.Builder.create(0.375f)
            .addBoneAnimation("bone71",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 90f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.375f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone96",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.375f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH6_ON_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone72",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH6_OFF_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone72",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH7_ON_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone73",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH7_OFF_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone73",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_CRANK5_ON_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone60",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 135f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_CRANK5_OFF_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone60",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 135f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH8_ON_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone56",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH8_OFF_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone56",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH9_ON_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone57",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH9_OFF_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone57",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH10_ON_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone58",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_CONTROL_SWITCH10_OFF_ANIMATION = Animation.Builder.create(0.25f)
            .addBoneAnimation("bone58",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(1f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();

    public static final Animation HARTNELL_POWER_ON_ANIMATION = Animation.Builder.create(9.291676f)
            .addBoneAnimation("bone33",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone91",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(4.75f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone93",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(3.9583435f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone92",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(3.2083435f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone94",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(3.9583435f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone95",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(4.75f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("m_sensor_1",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone109",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone116",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(5.916767f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone126",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone127",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone131",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, -0.75f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone166",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP)))
            .addBoneAnimation("bone169",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 1f, 1f), STEP)))
            .addBoneAnimation("bone167",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP)))
            .addBoneAnimation("bone170",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP)))
            .addBoneAnimation("bone168",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, -0.7f, 1f), STEP)))
            .addBoneAnimation("bone171",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP)))
            .addBoneAnimation("bone86",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(9.083434f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone87",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(9.083434f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone88",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(9.083434f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone89",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(9.083434f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone90",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(9.083434f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone82",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(4.343333f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone83",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(5.125f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone84",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(5.916767f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone85",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(7.541677f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone111",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(6.291677f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone101",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(5.916767f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone102",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(6.676667f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone103",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(7.167667f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone117",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(5.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone118",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(6.958343f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone135",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.20834334f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone138",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.20834334f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone136",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.7916766f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone139",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(0.7916766f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone137",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(1.5416767f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("bone140",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP),
                            new Keyframe(1.5416767f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP)))
            .addBoneAnimation("rotor",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(2.6766665f, AnimationHelper.createTranslationalVector(0f, -5f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(6.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("light_3",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 1f, 1f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(9.291676f, AnimationHelper.createScalingVector(1f, 1f, 1f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
    public static final Animation HARTNELL_POWER_OFF_ANIMATION = Animation.Builder.create(4f)
            .addBoneAnimation("bone33",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone91",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(2.0416765f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone93",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(1.7083433f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone92",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(1.375f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone94",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(1.7083433f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone95",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(2.0416765f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("m_sensor_1",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone109",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone116",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(2.5416765f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone126",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone127",
                    new Transformation(Transformation.Targets.ROTATE,
                            new Keyframe(0f, AnimationHelper.createRotationalVector(0f, -40f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone131",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, -0.75f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("bone166",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP)))
            .addBoneAnimation("bone169",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, -1.3f, 1f), STEP)))
            .addBoneAnimation("bone167",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP)))
            .addBoneAnimation("bone170",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP)))
            .addBoneAnimation("bone168",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, -0.7f, 1f), STEP)))
            .addBoneAnimation("bone171",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 0f, 1f), STEP)))
            .addBoneAnimation("bone86",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(3.9167665f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone87",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(3.9167665f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone88",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(3.9167665f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone89",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(3.9167665f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone90",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(3.9167665f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone82",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(1.875f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone83",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(2.2083435f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone84",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(2.5416765f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone85",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(3.25f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone111",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(2.7083435f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone101",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(2.5416765f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone102",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(2.875f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone103",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(3.0834335f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone117",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(2.375f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone118",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(3f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone135",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.08343333f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone138",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.08343333f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone136",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.3433333f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone139",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.3433333f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone137",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.6766666f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("bone140",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), STEP),
                            new Keyframe(0.6766666f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), STEP)))
            .addBoneAnimation("rotor",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(1.375f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, -5f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("light_3",
                    new Transformation(Transformation.Targets.SCALE,
                            new Keyframe(0f, AnimationHelper.createScalingVector(1f, 1f, 1f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(4f, AnimationHelper.createScalingVector(1f, 1f, 1f),
                                    Transformation.Interpolations.LINEAR)))
            .build();

    public static List<Animation> listOfControlAnimations() {
        List<Animation> animationList = new ArrayList<>();
        animationList.add(HARTNELL_CONTROL_ANTIGRAV_OFF_ANIMATION); // 0
        animationList.add(HARTNELL_CONTROL_ANTIGRAV_ON_ANIMATION); // 1
        animationList.add(HARTNELL_CONTROL_AUTOPILOT_OFF_ANIMATION); // 2
        animationList.add(HARTNELL_CONTROL_AUTOPILOT_ON_ANIMATION); // 3
        animationList.add(HARTNELL_CONTROL_CRANK1_OFF_ANIMATION); // 4
        animationList.add(HARTNELL_CONTROL_CRANK1_ON_ANIMATION); // 5
        animationList.add(HARTNELL_CONTROL_CRANK2_OFF_ANIMATION); // 6
        animationList.add(HARTNELL_CONTROL_CRANK2_ON_ANIMATION); // 7
        animationList.add(HARTNELL_CONTROL_CRANK3_OFF_ANIMATION); // 8
        animationList.add(HARTNELL_CONTROL_CRANK3_ON_ANIMATION); // 9
        animationList.add(HARTNELL_CONTROL_CRANK4_OFF_ANIMATION); // 10
        animationList.add(HARTNELL_CONTROL_CRANK4_ON_ANIMATION); // 11
        animationList.add(HARTNELL_CONTROL_CRANK5_OFF_ANIMATION); // 12
        animationList.add(HARTNELL_CONTROL_CRANK5_ON_ANIMATION); // 13
        animationList.add(HARTNELL_CONTROL_DIMENSION_FIRST_ANIMATION); // 14
        animationList.add(HARTNELL_CONTROL_DIMENSION_SECOND_ANIMATION); // 15
        animationList.add(HARTNELL_CONTROL_DIRECTION_NORTH_ANIMATION); // 16
        animationList.add(HARTNELL_CONTROL_DIRECTION_EAST_ANIMATION); // 17
        animationList.add(HARTNELL_CONTROL_DIRECTION_SOUTH_ANIMATION); // 18
        animationList.add(HARTNELL_CONTROL_DIRECTION_WEST_ANIMATION); // 19
        animationList.add(HARTNELL_CONTROL_DOORCONTROL_CLOSE_ANIMATION); // 20
        animationList.add(HARTNELL_CONTROL_DOORCONTROL_OPEN_ANIMATION); // 21
        animationList.add(HARTNELL_CONTROL_DOORLOCK_LOCKED_ANIMATION); // 22
        animationList.add(HARTNELL_CONTROL_DOORLOCK_UNLOCKED_ANIMATION); // 23
        animationList.add(HARTNELL_CONTROL_FASTRETURN_ANIMATION); // 24
        animationList.add(HARTNELL_CONTROL_HAILMARY_OFF_ANIMATION); // 25
        animationList.add(HARTNELL_CONTROL_HAILMARY_ON_ANIMATION); // 26
        animationList.add(HARTNELL_CONTROL_HANDBRAKE_OFF_ANIMATION); // 27
        animationList.add(HARTNELL_CONTROL_HANDBRAKE_ON_ANIMATION); // 28
        animationList.add(HARTNELL_CONTROL_LANDTYPE_ANIMATION); // 29
        animationList.add(HARTNELL_CONTROL_LEVER1_OFF_ANIMATION); // 30
        animationList.add(HARTNELL_CONTROL_LEVER1_ON_ANIMATION); // 31
        animationList.add(HARTNELL_CONTROL_LEVER2_OFF_ANIMATION); // 32
        animationList.add(HARTNELL_CONTROL_LEVER1_ON_ANIMATION); // 33
        animationList.add(HARTNELL_CONTROL_LEVER2_OFF_ANIMATION); // 34
        animationList.add(HARTNELL_CONTROL_LEVER2_ON_ANIMATION); // 35
        animationList.add(HARTNELL_CONTROL_LEVER3_OFF_ANIMATION); // 36
        animationList.add(HARTNELL_CONTROL_LEVER3_ON_ANIMATION); // 37
        animationList.add(HARTNELL_CONTROL_LEVER4_OFF_ANIMATION); // 38
        animationList.add(HARTNELL_CONTROL_LEVER4_ON_ANIMATION); // 39
        animationList.add(HARTNELL_CONTROL_RANDOMISER_ANIMATION); // 40
        animationList.add(HARTNELL_CONTROL_REFUELER_OFF_ANIMATION); // 41
        animationList.add(HARTNELL_CONTROL_REFUELER_ON_ANIMATION); // 42
        animationList.add(HARTNELL_CONTROL_SWITCH1_OFF_ANIMATION); // 43
        animationList.add(HARTNELL_CONTROL_SWITCH1_ON_ANIMATION); // 44
        animationList.add(HARTNELL_CONTROL_SWITCH2_OFF_ANIMATION); // 45
        animationList.add(HARTNELL_CONTROL_SWITCH3_OFF_ANIMATION); // 46
        animationList.add(HARTNELL_CONTROL_SWITCH3_ON_ANIMATION); // 47
        animationList.add(HARTNELL_CONTROL_SWITCH4_OFF_ANIMATION); // 48
        animationList.add(HARTNELL_CONTROL_SWITCH4_ON_ANIMATION); // 49
        animationList.add(HARTNELL_CONTROL_SWITCH5_OFF_ANIMATION); // 50
        animationList.add(HARTNELL_CONTROL_SWITCH5_ON_ANIMATION); // 51
        animationList.add(HARTNELL_CONTROL_SWITCH6_OFF_ANIMATION); // 52
        animationList.add(HARTNELL_CONTROL_SWITCH6_ON_ANIMATION); // 53
        animationList.add(HARTNELL_CONTROL_SWITCH7_OFF_ANIMATION); // 54
        animationList.add(HARTNELL_CONTROL_SWITCH7_ON_ANIMATION); // 55
        animationList.add(HARTNELL_CONTROL_SWITCH8_OFF_ANIMATION); // 56
        animationList.add(HARTNELL_CONTROL_SWITCH8_ON_ANIMATION); // 57
        animationList.add(HARTNELL_CONTROL_SWITCH9_OFF_ANIMATION); // 58
        animationList.add(HARTNELL_CONTROL_SWITCH9_ON_ANIMATION); // 59
        animationList.add(HARTNELL_CONTROL_SWITCH10_OFF_ANIMATION); // 60
        animationList.add(HARTNELL_CONTROL_SWITCH10_ON_ANIMATION); // 61
        animationList.add(HARTNELL_CONTROL_THROTTLE_1_FIRST_ANIMATION); // 62
        animationList.add(HARTNELL_CONTROL_THROTTLE_1_SECOND_ANIMATION); // 63
        animationList.add(HARTNELL_CONTROL_TOGGLESWITCH1_OFF_ANIMATION); // 64
        animationList.add(HARTNELL_CONTROL_TOGGLESWITCH1_ON_ANIMATION); // 65
        animationList.add(HARTNELL_CONTROL_TOGGLESWITCH2_OFF_ANIMATION); // 66
        animationList.add(HARTNELL_CONTROL_TOGGLESWITCH2_ON_ANIMATION); // 67
        animationList.add(HARTNELL_CONTROL_TURNSWITCH1_OFF_ANIMATION); // 68
        animationList.add(HARTNELL_CONTROL_TURNSWITCH1_ON_ANIMATION); // 69
        animationList.add(HARTNELL_CONTROL_TURNSWITCH2_OFF_ANIMATION); // 70
        animationList.add(HARTNELL_CONTROL_TURNSWITCH2_ON_ANIMATION); // 71
        animationList.add(HARTNELL_CONTROL_XYZINC_1_ANIMATION); // 72
        animationList.add(HARTNELL_CONTROL_XYZINC_10_ANIMATION); // 73
        animationList.add(HARTNELL_CONTROL_XYZINC_100_ANIMATION); // 74
        animationList.add(HARTNELL_CONTROL_XYZINC_1000_ANIMATION); // 75
        animationList.add(HARTNELL_CONTROL_XINC_ANIMATION); // 76
        animationList.add(HARTNELL_CONTROL_YINC_ANIMATION); // 77
        animationList.add(HARTNELL_CONTROL_ZINC_ANIMATION); // 78
        return animationList;
    }

    /*
     * public static HashMap<Integer, ControlAnimationState>
     * animationStatePerControl(List<Animation> animList) { HashMap<Integer,
     * ControlAnimationState> map = new HashMap<>(); for(int i = 0; i <
     * animList.size(); i++) { Animation animation = animList.get(i);
     * ControlAnimationState animationState = new ControlAnimationState(animation);
     * map.put(i, animationState); } return map; }
     *
     * public static int animationOnIdFromControlName(String id) { return switch(id)
     * { default -> 62; case "randomiser" -> 40;
     */
    /* case "refueler" -> 41; */
    /*
     */
    /* case "handbrake" -> 28; */
    /*
     */
    /* case "increment" -> 75; */
    /*
     * case "x" -> 76; case "y" -> 77; case "z" -> 78; }; }
     *
     * public static int animationFromDirection(TardisTravel travel) { return
     * switch(travel.getDestination().getDirection()) { default -> 16; case EAST ->
     * 17; case SOUTH -> 18; case WEST -> 19; }; }
     */
}

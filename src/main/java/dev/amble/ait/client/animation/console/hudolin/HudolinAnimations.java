package dev.amble.ait.client.animation.console.hudolin;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

public class HudolinAnimations {

    public static final Animation HUDOLIN_FLIGHT = Animation.Builder.create(4.343333f).looping()
            .addBoneAnimation("rotor_bottom_ring",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -3f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.7083434f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.4583433f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.1676665f, AnimationHelper.createTranslationalVector(0f, -3f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(2.875f, AnimationHelper.createTranslationalVector(0f, -5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.5834335f, AnimationHelper.createTranslationalVector(0f, -5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.343333f, AnimationHelper.createTranslationalVector(0f, -3f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("rotor_bottom_spikes",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, -3f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.7083434f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.4583433f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.1676665f, AnimationHelper.createTranslationalVector(0f, -3f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(2.875f, AnimationHelper.createTranslationalVector(0f, -5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.5834335f, AnimationHelper.createTranslationalVector(0f, -5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.343333f, AnimationHelper.createTranslationalVector(0f, -3f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("rotor_top_spikes",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 3f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.7083434f, AnimationHelper.createTranslationalVector(0f, 0.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.4583433f, AnimationHelper.createTranslationalVector(0f, 0.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.1676665f, AnimationHelper.createTranslationalVector(0f, 3f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(2.875f, AnimationHelper.createTranslationalVector(0f, 5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.5834335f, AnimationHelper.createTranslationalVector(0f, 5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.343333f, AnimationHelper.createTranslationalVector(0f, 3f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .addBoneAnimation("rotor_top_ring",
                    new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 3f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(0.7083434f, AnimationHelper.createTranslationalVector(0f, 0.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(1.4583433f, AnimationHelper.createTranslationalVector(0f, 0.5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(2.1676665f, AnimationHelper.createTranslationalVector(0f, 3f, 0f),
                                    Transformation.Interpolations.LINEAR),
                            new Keyframe(2.875f, AnimationHelper.createTranslationalVector(0f, 5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(3.5834335f, AnimationHelper.createTranslationalVector(0f, 5f, 0f),
                                    Transformation.Interpolations.CUBIC),
                            new Keyframe(4.343333f, AnimationHelper.createTranslationalVector(0f, 3f, 0f),
                                    Transformation.Interpolations.LINEAR)))
            .build();
}

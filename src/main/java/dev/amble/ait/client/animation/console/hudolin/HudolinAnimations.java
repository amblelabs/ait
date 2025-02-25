package dev.amble.ait.client.animation.console.hudolin;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

public class HudolinAnimations {

    public static final Animation HUDOLIN_FLIGHT = Animation.Builder.create(4.343333f).looping()
            .addBoneAnimation("bone2", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, -0.9F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.875F, AnimationHelper.createTranslationalVector(0.0F, -1.9F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.2083F, AnimationHelper.createTranslationalVector(0.0F, -2.9F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.5417F, AnimationHelper.createTranslationalVector(0.0F, -2.9F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.875F, AnimationHelper.createTranslationalVector(0.0F, -3.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.1667F, AnimationHelper.createTranslationalVector(0.0F, -3.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.4583F, AnimationHelper.createTranslationalVector(0.0F, -3.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.8333F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.25F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.625F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("bone4", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.4167F, AnimationHelper.createTranslationalVector(0.0F, 0.6F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(0.7917F, AnimationHelper.createTranslationalVector(0.0F, 1.1F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.125F, AnimationHelper.createTranslationalVector(0.0F, 1.6F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.4583F, AnimationHelper.createTranslationalVector(0.0F, 2.1F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.8333F, AnimationHelper.createTranslationalVector(0.0F, 2.6F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.1667F, AnimationHelper.createTranslationalVector(0.0F, 3.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.4583F, AnimationHelper.createTranslationalVector(0.0F, 3.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.8333F, AnimationHelper.createTranslationalVector(0.0F, 2.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.25F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.625F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("bone15", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, -90.0F, 90.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 270.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.0F, AnimationHelper.createRotationalVector(-90.0F, -7.0F, 180.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .addBoneAnimation("bone15", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(1.0F, AnimationHelper.createTranslationalVector(1.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(2.0F, AnimationHelper.createTranslationalVector(-1.0F, -1.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(3.0F, AnimationHelper.createTranslationalVector(0.0F, 1.0F, 0.0F), Transformation.Interpolations.CUBIC),
                    new Keyframe(4.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            ))
            .build();
}

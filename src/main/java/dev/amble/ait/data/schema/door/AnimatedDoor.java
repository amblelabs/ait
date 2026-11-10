package dev.amble.ait.data.schema.door;

import java.util.Optional;

import dev.amble.lib.client.bedrock.BedrockAnimationReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public interface AnimatedDoor {
    default Optional<BedrockAnimationReference> getLeftAnimation() {
        return Optional.empty();
    }

    default Optional<BedrockAnimationReference> getRightAnimation() {
        return Optional.empty();
    }

    default Vec3d getScale() {
        return new Vec3d(1, 1, 1);
    }

    default Vec3d getOffset() {
        return Vec3d.ZERO;
    }

    @Environment(EnvType.CLIENT)
    default void runAnimations(ModelPart root, MatrixStack matrices, float leftProgress, float rightProgress) {
        Vec3d offset = this.getOffset().multiply(-1);
        matrices.translate(offset.x, offset.y, offset.z);

        Vec3d scale = this.getScale();
        matrices.scale((float) scale.x, (float) scale.y, (float) scale.z);

        applyAt(this.getLeftAnimation(), root, leftProgress);
        applyAt(this.getRightAnimation(), root, rightProgress);
    }

    @Environment(EnvType.CLIENT)
    private static void applyAt(Optional<BedrockAnimationReference> reference, ModelPart root, float progress) {
        reference.flatMap(BedrockAnimationReference::get)
                .ifPresent(anim -> anim.apply(root, Math.min(progress * anim.animationLength, Math.nextDown(anim.animationLength))));
    }
}

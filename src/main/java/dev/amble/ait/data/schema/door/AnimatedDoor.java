package dev.amble.ait.data.schema.door;

import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.tardis.handler.DoorHandler;
import dev.amble.ait.data.schema.AnimatedFeature;
import dev.amble.lib.client.bedrock.BedrockAnimationReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public interface AnimatedDoor extends AnimatedFeature {
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
	default void runAnimations(ModelPart root, MatrixStack matrices, float tickDelta, ClientTardis tardis) {
		DoorHandler doors = tardis.door();

		Vec3d offset = this.getOffset().multiply(-1);
		matrices.translate(offset.x, offset.y, offset.z);

		Vec3d scale = this.getScale();
		matrices.scale((float) scale.x, (float) scale.y, (float) scale.z);

		matrices.push();
		float leftProgress = doors.getLeftRot();
		float rightProgress = doors.getRightRot();

		if (!AITModClient.CONFIG.animateDoors) {
			leftProgress = doors.isLeftOpen() ? 1 : 0;
			rightProgress = doors.isRightOpen() ? 1 : 0;
		}

		float leftDelta;
		if (leftProgress == 1 || leftProgress == 0) {
			leftDelta = 0;
		} else {
			leftDelta = tickDelta / 10F;
		}

		float rightDelta;
		if (rightProgress == 1 || rightProgress == 0) {
			rightDelta = 0;
		} else {
			rightDelta = tickDelta / 10F;
		}

		float finalRightProgress = rightProgress - 0.001F;
		float finalLeftProgress = leftProgress - 0.001F;
		this.getLeftAnimation().flatMap(BedrockAnimationReference::get).ifPresent(anim -> anim.apply(root, (int) (finalLeftProgress * anim.animationLength * 20), leftDelta));
		this.getRightAnimation().flatMap(BedrockAnimationReference::get).ifPresent(anim -> anim.apply(root, (int) (finalRightProgress * anim.animationLength * 20), rightDelta));
		matrices.pop();
	}
}

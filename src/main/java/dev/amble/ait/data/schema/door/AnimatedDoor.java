package dev.amble.ait.data.schema.door;

import dev.amble.ait.core.tardis.animation.v2.bedrock.BedrockAnimationRegistry;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public interface AnimatedDoor {
	default Optional<BedrockAnimationRegistry.Reference> getLeftAnimation() {
		return Optional.empty();
	}

	default Optional<BedrockAnimationRegistry.Reference> getRightAnimation() {
		return Optional.empty();
	}

	default Vec3d getScale() {
		return new Vec3d(1, 1, 1);
	}

	default Vec3d getOffset() {
		return Vec3d.ZERO;
	}
}

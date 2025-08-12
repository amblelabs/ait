package dev.amble.ait.data.schema;

import dev.amble.ait.client.tardis.ClientTardis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;

public interface AnimatedFeature {
	@Environment(EnvType.CLIENT)
	void runAnimations(ModelPart root, MatrixStack matrices, float tickDelta, ClientTardis tardis);
}

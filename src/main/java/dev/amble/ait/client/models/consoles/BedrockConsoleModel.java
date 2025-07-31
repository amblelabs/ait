package dev.amble.ait.client.models.consoles;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.models.AnimatedModel;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.animation.v2.bedrock.BedrockAnimation;
import dev.amble.ait.core.tardis.animation.v2.bedrock.BedrockModel;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.datapack.DatapackConsole;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.lib.api.Identifiable;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.AnimationState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class BedrockConsoleModel implements ConsoleModel, Identifiable {
	private final BedrockModel model;
	private ModelPart root;

	public BedrockConsoleModel(BedrockModel model) {
		this.model = model;

		if (this.model == null) throw new IllegalStateException("Bedrock Model is null. Ensure the resource pack is loaded correctly.");

		this.root = this.model.create().createModel();
	}

	@Override
	public Identifier id() {
		return this.model.id();
	}

	@Override
	public ModelPart getPart() {
		return root;
	}

	@Override
	public void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity linkableBlockEntity, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, float tickDelta) {
		matrices.push();

		if (!(linkableBlockEntity instanceof ConsoleBlockEntity console)) {
			throw new IllegalArgumentException("Expected ConsoleBlockEntity, got " + linkableBlockEntity.getClass().getSimpleName());
		}

		ConsoleVariantSchema schema = console.getVariant();

		if (schema instanceof DatapackConsole datapackConsole) {
			Vec3d offset = datapackConsole.getOffset().multiply(1, -1, 1);
			matrices.translate(offset.x, offset.y, offset.z);

			Vec3d scale = datapackConsole.getScale();
			matrices.scale((float) scale.x, (float) scale.y, (float) scale.z);
		}

		getPart().render(matrices, vertices, light, overlay);

		matrices.pop();
	}

	@Override
	public void animateBlockEntity(ConsoleBlockEntity console, TravelHandlerBase.State state, boolean hasPower) {
		if (!(console.getVariant() instanceof DatapackConsole schema)) return;

		DatapackConsole.AnimationMap map = schema.getAnimations();
		if (map == null) {
			throw new IllegalStateException("DatapackConsole " + schema.id() + " has no animations defined.");
		}

		BedrockAnimation anim = map.getAnimation(state);

		if (anim == null) return;

		this.getPart().traverse().forEach(ModelPart::resetTransform);

		anim.apply(this.getPart(), console.ANIM_STATE, console.getAge(), 1F);
	}
}

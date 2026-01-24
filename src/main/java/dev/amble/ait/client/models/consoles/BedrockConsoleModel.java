package dev.amble.ait.client.models.consoles;

import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.entities.ConsoleControlEntity;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.control.ControlTypes;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.datapack.DatapackConsole;
import dev.amble.ait.data.datapack.TravelAnimationMap;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.lib.api.Identifiable;
import dev.amble.lib.bedrock.TargetedAnimationState;
import dev.amble.lib.client.bedrock.BedrockAnimation;
import dev.amble.lib.client.bedrock.BedrockAnimationReference;
import dev.amble.lib.client.bedrock.BedrockModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class BedrockConsoleModel implements ConsoleModel, Identifiable {
    private final BedrockModel model;
    private final ModelPart root;

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
    public void renderWithAnimations(ClientTardis tardis, ConsoleBlockEntity console, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, float tickDelta) {
        matrices.push();

        ConsoleVariantSchema schema = console.getVariant();

	    this.applyOffsets(matrices, schema);

        getPart().render(matrices, vertices, light, overlay);

        matrices.pop();

    }

	public void applyOffsets(MatrixStack matrices, ConsoleVariantSchema schema) {
		if (schema instanceof DatapackConsole datapackConsole) {
			Vec3d offset = datapackConsole.getOffset().multiply(1, -1, 1);
			matrices.translate(offset.x, offset.y, offset.z);

			Vec3d scale = datapackConsole.getScale();
			matrices.scale((float) scale.x, (float) scale.y, (float) scale.z);
		}
	}

	@Override
    public void animateBlockEntity(ConsoleBlockEntity console, TravelHandlerBase.State state, boolean hasPower) {
        if (!(console.getVariant() instanceof TravelAnimationMap.Holder schema)) return;

        TravelAnimationMap map = schema.getAnimations();
        if (map == null) {
            throw new IllegalStateException("DatapackConsole " + console.getVariant().id() + " has no animations defined.");
        }


        this.getPart().traverse().forEach(ModelPart::resetTransform);
		console.getControlEntities().forEach(this::applyControlAnimation);

		BedrockAnimation anim = map.getAnimation(state);
		if (anim == null) return;

		anim.apply(this.getPart(), console.ANIM_STATE, console.getAge(), 1F, null);
    }

	private void applyControlAnimation(ConsoleControlEntity entity) {
		if (entity.tardis().isEmpty()) return;

		Control control = entity.getControl();
		if (control == null) return;

		ControlTypes type = entity.getControlType().orElse(null);
		if (type == null) return;

		BedrockAnimationReference ref = type.getAnimation().orElse(null);
		if (ref == null) return;

		TargetedAnimationState state = entity.getAnimationState();
		state.setTargetProgress(control.getTargetProgress(entity.tardis().get(), entity.isOnDelay(), entity));
		state.tick();
		ref.get().ifPresent(anim -> anim.apply(this.getPart(), state.getAnimationTimeSecs() - 0.01F));
	}
}

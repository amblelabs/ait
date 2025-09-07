package dev.amble.ait.client.models.consoles;

import dev.amble.lib.api.Identifiable;
import dev.amble.lib.client.bedrock.BedrockAnimation;
import dev.amble.lib.client.bedrock.BedrockModel;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.datapack.DatapackConsole;
import dev.amble.ait.data.datapack.TravelAnimationMap;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;

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
        if (!(console.getVariant() instanceof TravelAnimationMap.Holder schema)) return;

        TravelAnimationMap map = schema.getAnimations();
        if (map == null) {
            throw new IllegalStateException("DatapackConsole " + console.getVariant().id() + " has no animations defined.");
        }

        BedrockAnimation anim = map.getAnimation(state);

        if (anim == null) return;

        this.getPart().traverse().forEach(ModelPart::resetTransform);

        anim.apply(this.getPart(), console.ANIM_STATE, console.getAge(), 1F);
    }
}

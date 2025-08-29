package dev.amble.ait.client.renderers.builtin;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.decoration.CoralSeatModel;

public class CoralSeatBuiltInRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    private final CoralSeatModel coralSeatModel;

    public CoralSeatBuiltInRenderer() {
        this.coralSeatModel = new CoralSeatModel(CoralSeatModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(ItemStack itemStack, ModelTransformationMode modelTransformationMode, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int i1) {
        matrixStack.push();

        matrixStack.scale(1.0f, -1.0f, -1.0f);

        this.coralSeatModel.setAngles(matrixStack, modelTransformationMode);

        Identifier texture = new Identifier(
                AITMod.MOD_ID,
                "textures/blockentities/decoration/coral_seat.png"
        );

        VertexConsumer buffer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(texture));
        coralSeatModel.render(matrixStack, buffer, i, i1, 1.0F, 1.0F, 1.0F, 1.0F);

        matrixStack.pop();
    }
}

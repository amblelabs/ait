package dev.amble.ait.client.renderers.machines;

import org.joml.Vector3f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.client.models.machines.GenericSubSystemModel;
import dev.amble.ait.core.engine.block.generic.GenericStructureSystemBlockEntity;

public class GenericSubSystemRenderer<T extends GenericStructureSystemBlockEntity> implements BlockEntityRenderer<T> {
    private final GenericSubSystemModel model;
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public GenericSubSystemRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new GenericSubSystemModel();
    }

    @Override
    public void render(GenericStructureSystemBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrices.translate(0.5f, -1.5f, -0.5f);

        ItemStack stack = entity.getSourceStack().orElse(null);
        boolean hasStack = stack != null && !stack.isEmpty();

        ModelPart wires = this.model.getPart().getChild("wires");
        wires.visible = hasStack;

        if (hasStack) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
            double offset = Math.sin((entity.getWorld().getTime() + tickDelta) / 8.0) / 18.0;

            matrices.translate(0, -1.15f + (offset / 2), 0);

            Vector3f scale = client.getItemRenderer().getModel(stack, entity.getWorld(), null, 0).getTransformation().firstPersonRightHand.scale;
            matrices.scale(0.9f, 0.9f, 0.9f);
            matrices.scale(scale.x, scale.y, scale.z);

            client.getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, light,
                    overlay, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
        }

        matrices.pop();
    }
}

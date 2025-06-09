package dev.amble.ait.client.renderers.machines;

import dev.amble.ait.client.models.machines.GenericSubSystemModel;
import dev.amble.ait.client.renderers.MultiBlockStructureRenderer;
import dev.amble.ait.core.engine.StructureHolder;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.block.generic.GenericStructureSystemBlockEntity;
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
import org.joml.Vector3f;

public class GenericSubSystemRenderer<T extends GenericStructureSystemBlockEntity> implements BlockEntityRenderer<T> {
    private GenericSubSystemModel model;

    public GenericSubSystemRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new GenericSubSystemModel();
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        SubSystem system = entity.system();
        if (entity.hasSystem() && system != null && !system.isUsable() && system instanceof StructureHolder holder) {
            MultiBlockStructureRenderer.instance().render(holder.getStructure(), entity.getPos(), entity.getWorld(), matrices, vertexConsumers, true);
        }

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrices.translate(0.5f, -1.5f, -0.5f);

        ItemStack stack = entity.getSourceStack().orElse(null);
        boolean hasStack = stack != null && !stack.isEmpty();

        ModelPart wires = this.model.getPart().getChild("wires");
        wires.visible = hasStack;

        this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(GenericSubSystemModel.TEXTURE)),
                light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        if (hasStack) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
            double offset = Math.sin((entity.getWorld().getTime() + tickDelta) / 8.0) / 18.0;

            matrices.translate(0, -1.15f + (offset / 2), 0);

            Vector3f scale = MinecraftClient.getInstance().getItemRenderer().getModel(stack, entity.getWorld(), null, 0).getTransformation().firstPersonRightHand.scale;
            matrices.scale(0.9f, 0.9f, 0.9f);
            matrices.scale(scale.x, scale.y, scale.z);

            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, light,
                    overlay, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
        }

        matrices.pop();
    }
}

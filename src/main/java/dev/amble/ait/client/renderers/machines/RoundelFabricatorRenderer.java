package dev.amble.ait.client.renderers.machines;


import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.profiler.Profiler;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.machines.RoundelFabricatorModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.core.blockentities.RoundelFabricatorBlockEntity;
import dev.amble.ait.core.blocks.FabricatorBlock;
import dev.amble.ait.core.blocks.RoundelFabricatorBlock;

public class RoundelFabricatorRenderer<T extends RoundelFabricatorBlockEntity> implements BlockEntityRenderer<T> {

    public static final Identifier FABRICATOR_TEXTURE = AITMod.id("textures/block/roundel_fabricator.png");
    public static final Identifier EMISSIVE_FABRICATOR_TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/block/roundel_fabricator_emission.png");
    private final RoundelFabricatorModel fabricatorModel;

    public RoundelFabricatorRenderer(BlockEntityRendererFactory.Context ctx) {
        this.fabricatorModel = new RoundelFabricatorModel(RoundelFabricatorModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(RoundelFabricatorBlockEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Profiler profiler = entity.getWorld().getProfiler();
        profiler.push("fabricator");

        matrices.push();
        matrices.translate(0.5f, 1.5f, 0.5f);

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrices.multiply(RotationAxis.POSITIVE_Y
                .rotationDegrees(entity.getCachedState().get(RoundelFabricatorBlock.FACING).asRotation()));

        this.fabricatorModel.render(matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(FABRICATOR_TEXTURE)), light, overlay, 1.0F,
                1.0F, 1.0F, 1.0F);

        if (entity.isValid()) {
            this.fabricatorModel.render(matrices,
                    vertexConsumers.getBuffer(AITRenderLayers.tardisEmissiveCullZOffset(EMISSIVE_FABRICATOR_TEXTURE, true)), 0xf000f0, overlay, 1.0F,
                    1.0F, 1.0F, 1.0F);
        }

        matrices.pop();
        matrices.push();
        matrices.translate(0.5, 1.5, 0.5);
        float rotation = entity.getCachedState().get(FabricatorBlock.FACING).asRotation();
        if (entity.getCachedState().get(FabricatorBlock.FACING) == Direction.NORTH ||
                entity.getCachedState().get(FabricatorBlock.FACING) == Direction.SOUTH) {
            rotation += 180;
        }
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        matrices.translate(-0.5, -1.5, -0.5);

        matrices.pop();
        profiler.pop();
    }
}

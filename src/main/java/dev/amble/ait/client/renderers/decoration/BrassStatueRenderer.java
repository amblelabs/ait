package dev.amble.ait.client.renderers.decoration;


import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.decoration.BrassStatueModel;
import dev.amble.ait.client.models.decoration.WoodenSeatModel;
import dev.amble.ait.core.blockentities.decoration.BrassStatueBlockEntity;
import dev.amble.ait.core.blockentities.decoration.WoodenSeatBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationPropertyHelper;

public class BrassStatueRenderer implements BlockEntityRenderer<BrassStatueBlockEntity> {

    private final BrassStatueModel model;

    public BrassStatueRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new BrassStatueModel(BrassStatueModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(BrassStatueBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        BlockState blockState = entity.getCachedState();
        int k = blockState.contains(SkullBlock.ROTATION) ? blockState.get(SkullBlock.ROTATION) : 0;
        float h = 180.0f - RotationPropertyHelper.toDegrees(k);

        String variant = entity.getVariant();
        Identifier texture = new Identifier(AITMod.MOD_ID, "textures/blockentities/decoration/brass_statue/" + variant + ".png");

        matrices.push();
        matrices.scale(1f, 1f, 1f);

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrices.translate(0.5, -1.5f, -0.5);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(h));

        this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(texture)),
                light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        matrices.pop();
    }
}

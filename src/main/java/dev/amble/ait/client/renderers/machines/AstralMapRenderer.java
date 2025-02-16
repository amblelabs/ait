package dev.amble.ait.client.renderers.machines;

import static dev.amble.ait.client.boti.BOTI.AIT_BUF_BUILDER_STORAGE;

import java.util.Optional;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.machines.AstralMapModel;
import dev.amble.ait.client.util.ClientLightUtil;
import dev.amble.ait.core.blockentities.AstralMapBlockEntity;

public class AstralMapRenderer<T extends AstralMapBlockEntity> implements BlockEntityRenderer<T>, ClientLightUtil.Renderable<AstralMapBlockEntity> {

    public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/machines/astral_map.png");

    private final AstralMapModel model;
    private final Optional voidCube;

    public AstralMapRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new AstralMapModel();
        this.voidCube = this.model.getChild("void");
    }

    @Override
    public void render(AstralMapBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState blockState = entity.getCachedState();

        matrices.push();
        matrices.scale(1f, 1f, 1f);
        matrices.translate(0.5, 1.5f, 0.5);

        Direction facing = blockState.get(HorizontalFacingBlock.FACING);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(facing.asRotation()));

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE)),
                light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);


        VertexConsumerProvider.Immediate botiProvider = AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();

        matrices.pop();
    }

    @Override
    public void render(AstralMapBlockEntity entity, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}

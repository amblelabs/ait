package dev.amble.ait.client.renderers.machines;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.machines.RiftRipperModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.core.blockentities.RiftRipperBlockEntity;
import dev.amble.ait.core.blocks.ArtronCollectorBlock;
import dev.amble.ait.core.world.RiftChunkManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class RiftRipperRenderer<T extends RiftRipperBlockEntity> implements BlockEntityRenderer<T> {

    public static final Identifier RIPPER_TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/machines/rift_ripper.png"));
    public static final Identifier EMISSIVE_RIPPER_TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/machines/rift_ripper_emission.png"));
    private final RiftRipperModel riftRipperModel;

    public RiftRipperRenderer(BlockEntityRendererFactory.Context ctx) {
        this.riftRipperModel = new RiftRipperModel(RiftRipperModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(RiftRipperBlockEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {

        BlockState blockState = entity.getCachedState();

        float f = blockState.get(HorizontalFacingBlock.FACING).asRotation();

        if (MinecraftClient.getInstance().world == null)
            return;

        matrices.push();
        matrices.translate(0.5f, 1.5f, 0.5f);


        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(f));

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        ModelPart symbol = this.riftRipperModel.symbol;
        symbol.visible = true;

        matrices.push();

        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(10f * (MinecraftClient.getInstance().getTickDelta() + MinecraftClient.getInstance().player.age)));
        matrices.translate(0, 1.5f, 0);

        symbol.render(matrices, vertexConsumers.getBuffer(AITRenderLayers.getEyes(RIPPER_TEXTURE)), 0xf000f0, overlay, 0.3607843137f,
                0.6450980392f, 1, entity.getWorld().random.nextInt(32) != 6 ? 0.4f : 0.05f);
        matrices.pop();


        this.riftRipperModel.render(matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(RIPPER_TEXTURE)), light, overlay, 1.0F,
                1.0F, 1.0F, 1.0F);
        this.riftRipperModel.render(matrices,
                vertexConsumers.getBuffer(RenderLayer.getEyes(EMISSIVE_RIPPER_TEXTURE)),
                0xF000F00, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        matrices.push();
        matrices.translate(0, 0.7f, -0.5f);
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(20f));
        matrices.scale(0.01f, 0.01f, 0.01f);
        matrices.translate(0, 0, 15);
        String text1 = "-= Rift Ripper =-";
        String text2 = "Systems Version: v1.0-0.1";
        String bar = "[##########] 100%";
        String text3 = "RIFT HELD [10s]!";
        int width1 = MinecraftClient.getInstance().textRenderer.getWidth(text1);
        int width2 = MinecraftClient.getInstance().textRenderer.getWidth(text2);
        int width4 = MinecraftClient.getInstance().textRenderer.getWidth(bar);
        int width3 = MinecraftClient.getInstance().textRenderer.getWidth(text3);
        MinecraftClient.getInstance().textRenderer.draw(text1, 0 - width1 / 2, 0, Colors.WHITE,
                false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL,
                0x0000000, 0xf000f0);
        MinecraftClient.getInstance().textRenderer.draw(text2, 0 - width2 / 2, 10, Colors.WHITE,
                false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL,
                0x0000000, 0xf000f0);
        MinecraftClient.getInstance().textRenderer.draw(bar, 0 - width4 / 2, 20, Colors.WHITE,
                false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL,
                0x0000000, 0xf000f0);
        MinecraftClient.getInstance().textRenderer.draw(text3, 0 - width3 / 2, 30, Colors.WHITE,
                false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL,
                0x0000000, 0xf000f0);
        matrices.pop();

        matrices.pop();
    }
}

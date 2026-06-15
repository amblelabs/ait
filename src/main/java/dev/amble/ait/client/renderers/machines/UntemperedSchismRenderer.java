package dev.amble.ait.client.renderers.machines;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.machines.RiftRipperModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.core.blockentities.UntemperedSchismBlockEntity;
import dev.amble.ait.core.blocks.UntemperedSchismBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class UntemperedSchismRenderer<T extends UntemperedSchismBlockEntity> implements BlockEntityRenderer<T> {

    public static final Identifier RIPPER_TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/machines/rift_ripper.png"));
    public static final Identifier EMISSIVE_RIPPER_TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/machines/rift_ripper_emission.png"));
    private final RiftRipperModel riftRipperModel;

    public UntemperedSchismRenderer(BlockEntityRendererFactory.Context ctx) {
        this.riftRipperModel = new RiftRipperModel(RiftRipperModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(UntemperedSchismBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        int percentage = (int) (entity.getCurrentFuel() / entity.getMaxFuel() * 100);
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

        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((percentage + 10f) * (MinecraftClient.getInstance().getTickDelta() + MinecraftClient.getInstance().player.age)));
        matrices.translate(0, 1.5f, 0);

        symbol.render(matrices, vertexConsumers.getBuffer(AITRenderLayers.getEyes(RIPPER_TEXTURE)), LightmapTextureManager.MAX_LIGHT_COORDINATE, overlay, 0.36f,
                0.65f, 1, entity.getWorld().random.nextInt(32) != 6 ? 0.4f : 0.05f);
        matrices.pop();


        this.riftRipperModel.render(matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(RIPPER_TEXTURE)), light, overlay, 1.0F,
                1.0F, 1.0F, 1.0F);
        this.riftRipperModel.render(matrices,
                vertexConsumers.getBuffer(RenderLayer.getEyes(EMISSIVE_RIPPER_TEXTURE)),
                LightmapTextureManager.MAX_LIGHT_COORDINATE, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        matrices.push();
        matrices.translate(0, 0.7f, -0.5f);
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(20f));
        matrices.scale(0.01f, 0.01f, 0.01f);
        matrices.translate(0, 0, 15);
        this.draw(Text.translatable("rift_ripper.name.renderer").formatted(Formatting.BOLD, Formatting.AQUA), matrices, vertexConsumers, textRenderer, 0);
        this.draw(Text.translatable("rift_ripper.version"), matrices, vertexConsumers, textRenderer, 10);
        this.draw(Text.literal( percentage + "%").formatted(Formatting.GOLD), matrices, vertexConsumers, textRenderer, 20);
        this.draw(Text.translatable("rift_ripper.held", (entity.getCurrentFuel() / UntemperedSchismBlock.ARTRON_PER_TICK / 20f)), matrices, vertexConsumers, textRenderer, 30);
        matrices.pop();

        matrices.pop();
    }

    private void draw(Text text, MatrixStack stack, VertexConsumerProvider vertexConsumers, TextRenderer textRenderer, int yOffset) {
        int width = textRenderer.getWidth(text);
        textRenderer.draw(text, -width / 2f, yOffset, Colors.WHITE,
                false, stack.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL,
                0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
    }
}

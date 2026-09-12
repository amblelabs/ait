package dev.amble.ait.client.renderers.machines;

import dev.amble.lib.client.bedrock.BedrockEntityModel;
import dev.amble.lib.client.bedrock.BedrockModelReference;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.core.blockentities.ArtronCollectorBlockEntity;

public class ArtronCollectorRenderer<T extends ArtronCollectorBlockEntity> implements BlockEntityRenderer<T> {

    private static final int FRAME_COUNT = 8;
    private static final float TICKS_PER_FRAME = 3.0F;

    protected BedrockEntityModel<?> model;

    public ArtronCollectorRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getWorld() == null) return;

        if (this.model == null) {
            this.refreshModel(entity);
        }

        BlockState blockState = entity.getCachedState();
        float f = blockState.get(HorizontalFacingBlock.FACING).asRotation();

        matrices.push();

        matrices.translate(0.5D, 0, 0.5D);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180F));
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(f));

        ModelPart batteryLevels = this.model.getPart().getChild("main").getChild("Meter");

        if (batteryLevels != null) {
            batteryLevels.getChild("Light_1").visible = entity.getCurrentFuel() > 500;
            batteryLevels.getChild("Light_2").visible = entity.getCurrentFuel() > 1000;
            batteryLevels.getChild("Light_3").visible = entity.getCurrentFuel() > 1250;
            batteryLevels.getChild("Light_4").visible = entity.getCurrentFuel() >= 1500;
        }

        this.model.render(
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(entity.getTexture())),
                light,
                overlay,
                1.0f, 1.0f, 1.0f, 1.0f
        );

        Identifier emission = entity.getEmissionTexture();

        if (emission == null) {
            emission = entity.getTexture();
        }

        Identifier animatedTexture = getAnimatedTexture(entity);

        if (animatedTexture == null) {
            animatedTexture = entity.getTexture();
        }

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(emission));

        float alpha = 1f;

        if (entity.getCurrentFuel() > 0) {
            VertexConsumer emissive = vertexConsumers.getBuffer(RenderLayer.getEyes(animatedTexture));

            long worldTime = entity.getWorld().getTime();
            float t = (worldTime + tickDelta) / TICKS_PER_FRAME;
            int frame = Math.floorMod((int) Math.floor(t), FRAME_COUNT);
            int nextFrame = (frame + 1) % FRAME_COUNT;
            alpha = t - MathHelper.floor(t);

            consumer = new FrameOffsetVertexConsumer(emissive, nextFrame, FRAME_COUNT);
        }

        this.model.render(
                matrices,
                consumer,
                LightmapTextureManager.MAX_LIGHT_COORDINATE,
                overlay,
                1.0f, 1.0f, 1.0f, alpha
        );

        matrices.pop();
    }

    protected BedrockEntityModel<?> refreshModel(T entity) {
        BedrockModelReference ref = entity.getModel();
        if (ref == null) {
            throw new IllegalStateException("BlockEntity " + entity + " does not have a BedrockModelReference");
        }
        return this.model = new BedrockEntityModel<>(ref.get().orElseThrow(() ->
                new IllegalStateException("BedrockModel " + ref.id() + " not found for block entity " + entity)));
    }

    protected Identifier getAnimatedTexture(T entity) {
        return entity.getTexture().withPath(s -> s.replace(".png", "_anim.png"));
    }

    private record FrameOffsetVertexConsumer(VertexConsumer delegate, int frame, int frameCount)
            implements VertexConsumer {

        @Override
        public VertexConsumer texture(float u, float v) {
            this.delegate.texture(u, (v + this.frame) / this.frameCount);
            return this;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            this.delegate.vertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            this.delegate.color(red, green, blue, alpha);
            return this;
        }

        @Override
        public VertexConsumer overlay(int u, int v) {
            this.delegate.overlay(u, v);
            return this;
        }

        @Override
        public VertexConsumer light(int u, int v) {
            this.delegate.light(u, v);
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            this.delegate.normal(x, y, z);
            return this;
        }

        @Override
        public void next() {
            this.delegate.next();
        }

        @Override
        public void fixedColor(int red, int green, int blue, int alpha) {
            this.delegate.fixedColor(red, green, blue, alpha);
        }

        @Override
        public void unfixColor() {
            this.delegate.unfixColor();
        }
    }
}
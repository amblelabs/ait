package dev.amble.ait.client.renderers.machines;

import dev.amble.lib.animation.AnimatedBlockEntity;
import dev.amble.lib.client.bedrock.BedrockEntityModel;
import dev.amble.lib.client.bedrock.BedrockModelReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
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
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.machines.ArtronCollectorModel;
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

        if (MinecraftClient.getInstance().world == null)
            return;

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
                vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(this.getTexture(entity))),
                light,
                overlay,
                1.0f, 1.0f, 1.0f, 1.0f
        );

        if (entity.getCurrentFuel() > 0) {
            long worldTime = entity.getWorld().getTime();
            float t = (worldTime + tickDelta) / TICKS_PER_FRAME;
            int frame = Math.floorMod((int) Math.floor(t), FRAME_COUNT);
            int nextFrame = (frame + 1) % FRAME_COUNT;
            float fraction = t - (float) Math.floor(t);

            Identifier animatedTexture = getAnimatedTexture(entity);
            if (animatedTexture == null) {
                animatedTexture = this.getTexture(entity);
            }

            VertexConsumer emissive = vertexConsumers.getBuffer(RenderLayer.getEyes(animatedTexture));

            this.model.render(
                    matrices,
                    new FrameOffsetVertexConsumer(emissive, nextFrame, FRAME_COUNT),
                    0xF000F0,
                    overlay,
                    1.0F, 1.0F, 1.0F,
                    fraction
            );
        } else {
            Identifier emission = entity.getEmissionTexture();
            if (emission != null) {
                this.model.render(
                        matrices,
                        vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(emission)),
                        LightmapTextureManager.MAX_LIGHT_COORDINATE,
                        overlay,
                        1.0f, 1.0f, 1.0f, 1.0f
                );
            }
        }

        matrices.pop();
    }

    public Identifier getTexture(T entity) {
        return entity.getTexture();
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
        return getTexture(entity).withPath(s -> s.replace(".png", "_anim.png"));
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
package dev.amble.ait.client.renderers.machines;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
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

    public static final Identifier COLLECTOR_TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/machines/artron_collector.png"));
    public static final Identifier ANIMATED_COLLECTOR_TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/machines/artron_collector_anim.png"));
    public static final Identifier EMISSIVE_COLLECTOR_TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/machines/artron_collector_emission.png"));
    /** Number of vertically-stacked 128x128 frames in {@link #ANIMATED_COLLECTOR_TEXTURE}. */
    private static final int FRAME_COUNT = 8;
    /** Animation speed: game ticks each frame is held before advancing. Lower is faster. */
    private static final float TICKS_PER_FRAME = 3.0F;

    private final ArtronCollectorModel artronCollectorModel;

    public ArtronCollectorRenderer(BlockEntityRendererFactory.Context ctx) {
        this.artronCollectorModel = new ArtronCollectorModel(ArtronCollectorModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(ArtronCollectorBlockEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {

        BlockState blockState = entity.getCachedState();

        float f = blockState.get(HorizontalFacingBlock.FACING).asRotation();

        if (MinecraftClient.getInstance().world == null)
            return;

        matrices.push();

        matrices.translate(0.5f, 1.5f, 0.5f);

        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(f));

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        ModelPart batteryLevels = artronCollectorModel.getPart().getChild("Meter");

        batteryLevels.getChild("Light_1").visible = entity.getCurrentFuel() > 500;
        batteryLevels.getChild("Light_2").visible = entity.getCurrentFuel() > 1000;
        batteryLevels.getChild("Light_3").visible = entity.getCurrentFuel() > 1250;
        batteryLevels.getChild("Light_4").visible = entity.getCurrentFuel() >= 1500;

        this.artronCollectorModel.render(matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(COLLECTOR_TEXTURE)), light, overlay, 1.0F,
                1.0F, 1.0F, 1.0F);

        if (entity.getCurrentFuel() > 0) {
            // Interpolated animated emissive: cross-fade between the current and next stacked frame.
            long worldTime = MinecraftClient.getInstance().world.getTime();
            float t = (worldTime + tickDelta) / TICKS_PER_FRAME;
            int frame = Math.floorMod((int) Math.floor(t), FRAME_COUNT);
            int nextFrame = (frame + 1) % FRAME_COUNT;
            float fraction = t - (float) Math.floor(t);

            VertexConsumer emissive = vertexConsumers
                    .getBuffer(RenderLayer.getEyes(ANIMATED_COLLECTOR_TEXTURE));

            this.artronCollectorModel.render(matrices, new FrameOffsetVertexConsumer(emissive, nextFrame, FRAME_COUNT),
                    0xF000F0, overlay, 1.0F, 1.0F, 1.0F, fraction);
        } else {
            this.artronCollectorModel.render(matrices,
                    vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(EMISSIVE_COLLECTOR_TEXTURE)),
                    0xF000F0, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        matrices.pop();
    }

    /**
     * Wraps a {@link VertexConsumer} to sample a single frame from a vertically-stacked sprite sheet.
     * The model UVs are baked against one 128x128 frame; this remaps each V coordinate into the
     * selected frame's band of the taller texture: {@code v -> (v + frame) / frameCount}.
     */
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

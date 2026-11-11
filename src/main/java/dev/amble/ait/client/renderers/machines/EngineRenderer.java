package dev.amble.ait.client.renderers.machines;

import java.util.Map;
import java.util.WeakHashMap;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.animation.machines.EngineAnimation;
import dev.amble.ait.client.models.machines.EngineBlockModel;
import dev.amble.ait.core.blockentities.EngineBlockEntity;
import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.impl.EngineSystem;
import dev.amble.ait.core.tardis.Tardis;
import org.joml.Vector3f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;

public class EngineRenderer<T extends EngineBlockEntity> implements BlockEntityRenderer<T> {

    public static final Identifier ENGINE_TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/machines/engine_block.png"));
    public static final Identifier EMISSIVE_ENGINE_TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/machines/engine_block_emission.png"));

    private static final float MIN_HEALTH_SPEED = 0.4f;
    private static final float ERROR_SPEED = 1.35f;
    private static final float THROTTLE_SPEED = 0.15f;
    private static final float SPEED_EASE = 0.05f;

    private static final Map<EngineBlockEntity, Spin> SPINS = new WeakHashMap<>();
    private static final Vector3f TEMP = new Vector3f();

    private final EngineBlockModel engineModel;

    public EngineRenderer(BlockEntityRendererFactory.Context ctx) {
        this.engineModel = new EngineBlockModel(EngineBlockModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(EngineBlockEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {

        if (!entity.isLinked())
            return;

        Tardis tardis = entity.tardis().get();
        EngineSystem engine = tardis.subsystems().engine();
        EngineSystem.Status status = engine.status();
        boolean powered = tardis.fuel().hasPower();
        float health = MathHelper.clamp(engine.durability() / DurableSubSystem.MAX_DURABILITY, 0, 1);

        this.animate(entity, tardis, status, powered, health, entity.getWorld().getTime() + tickDelta);

        matrices.push();
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(180));
        matrices.translate(0.5, -1.5, -0.5);

        this.engineModel.render(tardis, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(ENGINE_TEXTURE)),
                LightmapTextureManager.pack(entity.getWorld().getLightLevel(LightType.BLOCK, entity.getPos().up()), entity.getWorld().getLightLevel(LightType.SKY, entity.getPos())), overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        if (powered && status != EngineSystem.Status.OFF) {
            Vector3f colour = status.colour;
            float glow = 0.35f + 0.65f * health;

            this.engineModel.render(tardis, matrices, vertexConsumers.getBuffer(
                            RenderLayer.getEntityCutoutNoCullZOffset(EMISSIVE_ENGINE_TEXTURE, true)),
                    0xf000f0, overlay, colour.x * glow, colour.y * glow, colour.z * glow, 1.0F);
        }

        matrices.pop();
    }

    private void animate(EngineBlockEntity entity, Tardis tardis, EngineSystem.Status status, boolean powered,
            float health, float worldTicks) {
        MinecraftClient client = MinecraftClient.getInstance();
        Spin spin = SPINS.computeIfAbsent(entity, key -> new Spin());

        float target = targetSpeed(tardis, status, powered, health, worldTicks);

        if (!client.isPaused()) {
            float frame = client.getLastFrameDuration();
            float ease = 1f - (float) Math.pow(1f - SPEED_EASE, frame);

            spin.speed = MathHelper.lerp(ease, spin.speed, target);
            spin.time += frame * 50f * spin.speed;

            float length = EngineAnimation.ENGINE_IDLE.lengthInSeconds() * 1000f;
            if (spin.time > length * 64)
                spin.time %= length;
        }

        for (ModelPart part : this.engineModel.getPart().traverse().toList())
            part.resetTransform();

        AnimationHelper.animate(this.engineModel, EngineAnimation.ENGINE_IDLE, (long) spin.time, 1.0F, TEMP);
    }

    private static float targetSpeed(Tardis tardis, EngineSystem.Status status, boolean powered, float health,
            float worldTicks) {
        if (!powered || status == EngineSystem.Status.OFF)
            return 0;

        float speed = MathHelper.lerp(health, MIN_HEALTH_SPEED, 1f);

        if (tardis.travel().inFlight() || tardis.flight().isFlying())
            speed *= 1f + THROTTLE_SPEED * tardis.travel().speed();

        return switch (status) {
            case ERROR -> speed * ERROR_SPEED;
            case CRITICAL -> speed * sputter(worldTicks);
            default -> speed;
        };
    }

    private static float sputter(float worldTicks) {
        float seconds = worldTicks / 20f;
        float wave = MathHelper.sin(seconds * 2.3f) * MathHelper.sin(seconds * 5.1f + 1.7f);

        return MathHelper.clamp(0.55f + 0.9f * wave, 0.05f, 1.3f);
    }

    private static final class Spin {
        float time;
        float speed;
    }
}

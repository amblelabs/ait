package dev.amble.ait.client.models.consoles;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

@SuppressWarnings("rawtypes")
public abstract class SimpleConsoleModel extends SinglePartEntityModel implements ConsoleModel {

    // Using a map actually is the worst way to do this - DO NOT REPLICATE. - Loqor
    protected static final Map<BlockEntity, Object2FloatMap<String>> ANIMATION_CACHE = new WeakHashMap<>();

    protected static final MinecraftClient client = MinecraftClient.getInstance();

    protected float getAngle(BlockEntity console, String key, float target, float delta) {
        Object2FloatMap<String> state = ANIMATION_CACHE.computeIfAbsent(console, k -> new Object2FloatOpenHashMap<>());
        float current = state.getOrDefault(key, 0f);
        float next = MathHelper.lerp(delta, current, target);
        state.put(key, next);
        return next;
    }

    protected float getLerpedDegrees(BlockEntity console, String key, float targetDegrees, float delta) {
        Object2FloatMap<String> state = ANIMATION_CACHE.computeIfAbsent(console, k -> new Object2FloatOpenHashMap<>());
        float currentRadians = state.getOrDefault(key, 0f);
        float currentDegrees = currentRadians * (180f / (float) Math.PI);
        float nextDegrees = MathHelper.lerpAngleDegrees(delta, currentDegrees, targetDegrees);
        float nextRadians = nextDegrees * ((float) Math.PI / 180f);
        state.put(key, nextRadians);
        return nextRadians;
    }

    public SimpleConsoleModel() {
        this(RenderLayer::getEntityCutoutNoCull);
    }

    public SimpleConsoleModel(Function<Identifier, RenderLayer> function) {
        super(function);
    }

    @Override
    public void animateBlockEntity(ConsoleBlockEntity console, TravelHandlerBase.State state, boolean hasPower) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);

        if (hasPower && AITModClient.CONFIG.animateConsole)
            this.updateAnimation(console.ANIM_STATE, this.getAnimationForState(state), client.getTickDelta() + console.getAge());
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, ConsoleBlockEntity linkableBlockEntity, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, float tickDelta) {;
        renderWithAnimations(linkableBlockEntity, tardis, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
    }

    // Overloaded method for compatibility with older code
    public void renderWithAnimations(ConsoleBlockEntity console, ClientTardis tardis, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        root.render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
            float headPitch) {
    }

    public abstract Animation getAnimationForState(TravelHandlerBase.State state);

    public void renderMonitorText(Tardis tardis, ConsoleBlockEntity entity, MatrixStack matrices,
                                  VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // no op
    }
}

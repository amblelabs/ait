package dev.amble.ait.client.renderers;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;

@Environment(EnvType.CLIENT)
public final class PowerOffEyes {

    private static final int MAX_PAIRS = 2;
    private static final int AMBIENT_CHANCE = 2400;
    private static final int SPAWN_ATTEMPTS = 12;

    private static final double MIN_DISTANCE = 6;
    private static final double MAX_DISTANCE = 20;
    private static final double MIN_ANGLE = 50;
    private static final double MAX_ANGLE = 100;
    private static final double SEEN_ANGLE = 14;
    private static final double VANISH_DISTANCE = 5;

    private static final int MIN_LIFE = 100;
    private static final int MAX_LIFE = 400;
    private static final int APPEAR_TICKS = 15;
    private static final int BLINK_TICKS = 4;
    private static final int STARE_TICKS = 25;
    private static final int FADE_TICKS = 30;

    private static final float PIXEL = 1f / 16f;
    private static final float EYE_WIDTH = PIXEL * 2;
    private static final float EYE_HEIGHT = PIXEL;
    private static final float EYE_CENTER = PIXEL * 3;
    private static final float HALO = PIXEL * 0.75f;

    private enum State {
        WATCHING, BLINKING, STARING, FADING
    }

    private static final class Eyes {
        private final Vec3d pos;
        private final int life;

        private State state = State.WATCHING;
        private int age;
        private int stateTicks;
        private float alpha;
        private float lid = 1f;
        private float prevAlpha;
        private float prevLid = 1f;

        private Eyes(Vec3d pos, int life) {
            this.pos = pos;
            this.life = life;
        }

        private void setState(State state) {
            this.state = state;
            this.stateTicks = 0;
        }
    }

    private static final List<Eyes> EYES = new ArrayList<>();
    private static final Random RANDOM = Random.create();

    private PowerOffEyes() {
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(PowerOffEyes::tick);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(PowerOffEyes::render);
    }

    public static void onCreak() {
        if (!isDark())
            return;

        spawn();
    }

    private static boolean isDark() {
        ClientTardis tardis = ClientTardisUtil.getCurrentTardis();
        return tardis != null && !tardis.fuel().hasPower();
    }

    private static void tick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;

        if (player == null || client.world == null) {
            EYES.clear();
            return;
        }

        boolean dark = isDark();

        if (dark && RANDOM.nextInt(AMBIENT_CHANCE) == 0)
            spawn();

        Vec3d eye = player.getEyePos();
        Vec3d look = player.getRotationVec(1f);

        EYES.removeIf(eyes -> tickEyes(eyes, dark, eye, look));
    }

    private static boolean tickEyes(Eyes eyes, boolean dark, Vec3d eye, Vec3d look) {
        eyes.prevAlpha = eyes.alpha;
        eyes.prevLid = eyes.lid;
        eyes.age++;
        eyes.stateTicks++;

        if (eyes.pos.squaredDistanceTo(eye) < VANISH_DISTANCE * VANISH_DISTANCE)
            return true;

        if (!dark && eyes.state != State.FADING)
            eyes.setState(State.FADING);

        switch (eyes.state) {
            case WATCHING -> {
                eyes.alpha = Math.min(1f, (float) eyes.age / APPEAR_TICKS);

                if (angleTo(eye, look, eyes.pos) < SEEN_ANGLE)
                    eyes.setState(State.BLINKING);
                else if (eyes.age >= eyes.life)
                    eyes.setState(State.FADING);
            }
            case BLINKING -> {
                float half = BLINK_TICKS / 2f;
                eyes.lid = Math.abs(eyes.stateTicks - half) / half;

                if (eyes.stateTicks >= BLINK_TICKS) {
                    eyes.lid = 1f;
                    eyes.setState(State.STARING);
                }
            }
            case STARING -> {
                if (eyes.stateTicks >= STARE_TICKS)
                    eyes.setState(State.FADING);
            }
            case FADING -> {
                eyes.alpha = Math.max(0f, eyes.alpha - 1f / FADE_TICKS);
                eyes.lid = Math.max(0.15f, eyes.lid - 0.5f / FADE_TICKS);

                return eyes.alpha <= 0f;
            }
        }

        return false;
    }

    private static double angleTo(Vec3d eye, Vec3d look, Vec3d target) {
        Vec3d direction = target.subtract(eye).normalize();
        return Math.toDegrees(Math.acos(MathHelper.clamp(look.dotProduct(direction), -1, 1)));
    }

    private static void spawn() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null || client.world == null || EYES.size() >= MAX_PAIRS)
            return;

        Vec3d eye = player.getEyePos();

        for (int attempt = 0; attempt < SPAWN_ATTEMPTS; attempt++) {
            double side = RANDOM.nextBoolean() ? 1 : -1;
            double yaw = player.getYaw() + side * MathHelper.lerp(RANDOM.nextDouble(), MIN_ANGLE, MAX_ANGLE);
            double pitch = MathHelper.lerp(RANDOM.nextDouble(), -12, 18);
            Vec3d direction = Vec3d.fromPolar((float) pitch, (float) yaw);
            double distance = MathHelper.lerp(RANDOM.nextDouble(), MIN_DISTANCE, MAX_DISTANCE);

            BlockHitResult hit = client.world.raycast(new RaycastContext(eye, eye.add(direction.multiply(distance)),
                    RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));

            Vec3d end = hit.getType() == HitResult.Type.MISS ? hit.getPos() : hit.getPos().subtract(direction.multiply(0.6));

            if (end.distanceTo(eye) < MIN_DISTANCE || !client.world.getBlockState(BlockPos.ofFloored(end)).isAir())
                continue;

            EYES.add(new Eyes(end, MathHelper.nextInt(RANDOM, MIN_LIFE, MAX_LIFE)));
            return;
        }
    }

    private static void render(WorldRenderContext context) {
        if (EYES.isEmpty())
            return;

        Vec3d camera = context.camera().getPos();
        float delta = context.tickDelta();
        MatrixStack matrices = context.matrixStack();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA,
                GlStateManager.DstFactor.ONE);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        for (Eyes eyes : EYES) {
            float alpha = MathHelper.lerp(delta, eyes.prevAlpha, eyes.alpha);
            float lid = MathHelper.lerp(delta, eyes.prevLid, eyes.lid);

            if (alpha <= 0)
                continue;

            matrices.push();
            matrices.translate(eyes.pos.x - camera.x, eyes.pos.y - camera.y, eyes.pos.z - camera.z);
            matrices.multiply(context.camera().getRotation());

            Matrix4f matrix = matrices.peek().getPositionMatrix();

            for (int side = -1; side <= 1; side += 2) {
                float x = side * EYE_CENTER;

                eye(buffer, matrix, x, EYE_WIDTH + HALO * 2, (EYE_HEIGHT + HALO * 2) * Math.max(lid, 0.3f), alpha * 0.15f);
                eye(buffer, matrix, x, EYE_WIDTH, EYE_HEIGHT * lid, alpha);
            }

            matrices.pop();
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private static void eye(BufferBuilder buffer, Matrix4f matrix, float x, float width, float height, float alpha) {
        if (height <= 0.001f)
            return;

        float halfWidth = width / 2f;
        float halfHeight = height / 2f;

        buffer.vertex(matrix, x - halfWidth, -halfHeight, 0).color(1f, 1f, 1f, alpha).next();
        buffer.vertex(matrix, x + halfWidth, -halfHeight, 0).color(1f, 1f, 1f, alpha).next();
        buffer.vertex(matrix, x + halfWidth, halfHeight, 0).color(1f, 1f, 1f, alpha).next();
        buffer.vertex(matrix, x - halfWidth, halfHeight, 0).color(1f, 1f, 1f, alpha).next();
    }
}

package dev.amble.ait.client.renderers;

import dev.amble.ait.core.item.PhoneItem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

public final class PhoneCableRenderer {

    private static final int SEGMENTS = 24;
    private static final float THICKNESS = 0.025f;
    private static final float SHADE = 0.06f;

    private static final float FIRST_PERSON_HAND_X = 0.75f;
    private static final float FIRST_PERSON_HAND_Y = -0.8f;
    private static final double HAND_SIDEWAYS = 0.35;
    private static final double HAND_FORWARD = 0.25;
    private static final double HAND_HEIGHT = 0.8;
    private static final double MAX_RENDER_DISTANCE = 64;

    private PhoneCableRenderer() {
    }

    public static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world == null || context.consumers() == null)
            return;

        Vec3d camera = context.camera().getPos();
        float tickDelta = context.tickDelta();

        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            Hand hand = phoneHand(player);

            if (hand == null || player.squaredDistanceTo(camera) > MAX_RENDER_DISTANCE * MAX_RENDER_DISTANCE)
                continue;

            Vec3d anchor = PhoneItem.anchor(player.getStackInHand(hand));

            if (anchor == null)
                continue;

            Vec3d handPos = handPosition(client, player, hand, tickDelta);
            drawCable(context.matrixStack(), context.consumers(), camera, handPos, anchor, client);
        }
    }

    private static Hand phoneHand(PlayerEntity player) {
        for (Hand hand : Hand.values()) {
            ItemStack stack = player.getStackInHand(hand);

            if (stack.getItem() instanceof PhoneItem)
                return hand;
        }

        return null;
    }

    private static Vec3d handPosition(MinecraftClient client, PlayerEntity player, Hand hand, float tickDelta) {
        Arm arm = hand == Hand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
        int side = arm == Arm.RIGHT ? 1 : -1;

        if (player == client.player && client.options.getPerspective().isFirstPerson()) {
            double fovScale = 960.0 / client.options.getFov().getValue();
            Vec3d offset = client.gameRenderer.getCamera().getProjection()
                    .getPosition(side * FIRST_PERSON_HAND_X, FIRST_PERSON_HAND_Y).multiply(fovScale);

            return player.getCameraPosVec(tickDelta).add(offset);
        }

        float bodyYaw = MathHelper.lerp(tickDelta, player.prevBodyYaw, player.bodyYaw) * MathHelper.RADIANS_PER_DEGREE;
        double sin = MathHelper.sin(bodyYaw);
        double cos = MathHelper.cos(bodyYaw);
        float scale = player.getScaleFactor();
        boolean crouching = player.isInSneakingPose();

        double sideways = side * HAND_SIDEWAYS * scale;
        double forward = (crouching ? HAND_FORWARD + 0.2 : HAND_FORWARD) * scale;
        double height = (crouching ? HAND_HEIGHT - 0.3 : HAND_HEIGHT) * scale;

        return player.getLerpedPos(tickDelta).add(-cos * sideways - sin * forward, height,
                -sin * sideways + cos * forward);
    }

    private static void drawCable(MatrixStack matrices, VertexConsumerProvider consumers, Vec3d camera, Vec3d from, Vec3d to,
                                  MinecraftClient client) {
        Vec3d delta = to.subtract(from);
        double length = delta.length();

        if (length < 0.05)
            return;

        float sag = (float) Math.min(0.6, 0.08 + length * 0.05);
        BlockPos middle = BlockPos.ofFloored(from.add(delta.multiply(0.5)));
        int light = LightmapTextureManager.pack(client.world.getLightLevel(LightType.BLOCK, middle),
                client.world.getLightLevel(LightType.SKY, middle));

        Vec3d horizontal = new Vec3d(-delta.z, 0, delta.x);
        horizontal = horizontal.lengthSquared() < 1.0E-6 ? new Vec3d(THICKNESS, 0, 0) : horizontal.normalize().multiply(THICKNESS);
        Vec3d vertical = new Vec3d(0, THICKNESS, 0);

        matrices.push();
        matrices.translate(from.x - camera.x, from.y - camera.y, from.z - camera.z);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        VertexConsumer buffer = consumers.getBuffer(RenderLayer.getLeash());

        strip(buffer, matrix, delta, sag, horizontal, light, false);
        strip(buffer, matrix, delta, sag, vertical, light, true);

        matrices.pop();
    }

    private static void strip(VertexConsumer buffer, Matrix4f matrix, Vec3d delta, float sag, Vec3d width, int light, boolean reverse) {
        for (int step = 0; step <= SEGMENTS; step++) {
            int i = reverse ? SEGMENTS - step : step;
            float t = (float) i / SEGMENTS;
            float shade = (i % 2 == 0) ? SHADE : SHADE * 0.7f;

            float x = (float) (delta.x * t);
            float y = (float) (delta.y * t) - sag * MathHelper.sin(t * MathHelper.PI);
            float z = (float) (delta.z * t);

            buffer.vertex(matrix, x + (float) width.x, y + (float) width.y, z + (float) width.z)
                    .color(shade, shade, shade, 1f).light(light).next();
            buffer.vertex(matrix, x - (float) width.x, y - (float) width.y, z - (float) width.z)
                    .color(shade, shade, shade, 1f).light(light).next();
        }
    }
}

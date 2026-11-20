package dev.amble.ait.client.renderers;

import dev.amble.ait.core.item.PhoneItem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

public final class PhoneCableRenderer {

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final Hand[] HANDS = Hand.values();
    private static final Vec3d UP = new Vec3d(0, 1, 0);

    private static final int SEGMENTS = 24;
    private static final float THICKNESS = 0.025f;
    private static final float SHADE = 0.06f;

    private static final float FIRST_PERSON_HAND_X = 0.75f;
    private static final float FIRST_PERSON_HAND_Y = -0.8f;
    private static final double HAND_SIDEWAYS = 0.35;
    private static final double HAND_FORWARD = 0.25;
    private static final double HAND_HEIGHT = 0.8;
    private static final double DROPPED_HEIGHT = 0.15;

    private PhoneCableRenderer() {
    }

    public static void init() {
        WorldRenderEvents.AFTER_ENTITIES.register(PhoneCableRenderer::renderFirstPerson);
    }

    private static void renderFirstPerson(WorldRenderContext context) {
        ClientPlayerEntity player = CLIENT.player;

        if (player == null || context.consumers() == null || !CLIENT.options.getPerspective().isFirstPerson())
            return;

        renderPlayer(player, context.tickDelta(), context.matrixStack(), context.consumers(), context.camera().getPos());
    }

    public static void renderPlayer(PlayerEntity player, float tickDelta, MatrixStack matrices, VertexConsumerProvider consumers,
                                    Vec3d origin) {
        Hand hand = phoneHand(player);

        if (hand == null)
            return;

        Vec3d anchor = PhoneItem.anchor(player.getStackInHand(hand));

        if (anchor != null)
            drawCable(matrices, consumers, origin, handPosition(player, hand, tickDelta), anchor);
    }

    public static void renderDropped(ItemEntity item, float tickDelta, MatrixStack matrices, VertexConsumerProvider consumers) {
        Vec3d anchor = PhoneItem.anchor(item.getStack());

        if (anchor == null)
            return;

        Vec3d origin = item.getLerpedPos(tickDelta);
        drawCable(matrices, consumers, origin, origin.add(0, DROPPED_HEIGHT, 0), anchor);
    }

    private static Hand phoneHand(PlayerEntity player) {
        for (Hand hand : HANDS) {
            ItemStack stack = player.getStackInHand(hand);

            if (stack.getItem() instanceof PhoneItem)
                return hand;
        }

        return null;
    }

    private static Vec3d handPosition(PlayerEntity player, Hand hand, float tickDelta) {
        Arm arm = hand == Hand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
        int side = arm == Arm.RIGHT ? 1 : -1;

        if (player == CLIENT.player && CLIENT.options.getPerspective().isFirstPerson()) {
            double fovScale = 960.0 / CLIENT.options.getFov().getValue();
            Vec3d offset = CLIENT.gameRenderer.getCamera().getProjection()
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

    private static void drawCable(MatrixStack matrices, VertexConsumerProvider consumers, Vec3d origin, Vec3d from, Vec3d to) {

        Vec3d delta = to.subtract(from);
        double length = delta.length();

        if (length < 0.05)
            return;

        float sag = (float) Math.min(0.6, 0.08 + length * 0.05);
        BlockPos middle = BlockPos.ofFloored(from.add(delta.multiply(0.5)));
        int light = LightmapTextureManager.pack(CLIENT.world.getLightLevel(LightType.BLOCK, middle),
                CLIENT.world.getLightLevel(LightType.SKY, middle));

        Vec3d sideways = delta.crossProduct(UP);
        sideways = sideways.lengthSquared() < 1.0E-6 ? new Vec3d(THICKNESS, 0, 0) : sideways.normalize().multiply(THICKNESS);
        Vec3d vertical = new Vec3d(0, THICKNESS, 0);

        matrices.push();
        matrices.translate(from.x - origin.x, from.y - origin.y, from.z - origin.z);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        VertexConsumer buffer = consumers.getBuffer(RenderLayer.getLeash());

        strip(buffer, matrix, delta, sag, sideways, light, false);
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

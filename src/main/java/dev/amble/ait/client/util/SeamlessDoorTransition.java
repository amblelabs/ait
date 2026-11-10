package dev.amble.ait.client.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.core.tardis.util.TardisUtil;

public final class SeamlessDoorTransition {

    private static final long TIMEOUT_MS = 3000;

    private static boolean pending;
    private static long startedAt;

    private static float yaw;
    private static float pitch;
    private static Vec3d velocity = Vec3d.ZERO;

    private SeamlessDoorTransition() {
    }

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(TardisUtil.DOOR_TRANSITION, (client, handler, buf, responseSender) -> {
            float turn = buf.readFloat();

            client.execute(() -> begin(client, turn));
        });
    }

    private static void begin(MinecraftClient client, float turn) {
        ClientPlayerEntity player = client.player;

        if (player == null)
            return;

        pending = true;
        startedAt = System.currentTimeMillis();

        yaw = player.getYaw() + turn;
        pitch = player.getPitch();
        velocity = player.getVelocity().rotateY(-turn * MathHelper.RADIANS_PER_DEGREE);
    }

    public static boolean isActive() {
        if (pending && System.currentTimeMillis() - startedAt > TIMEOUT_MS)
            pending = false;

        return pending;
    }

    public static void onPositioned(ClientPlayerEntity player) {
        if (!isActive())
            return;

        pending = false;

        player.setYaw(yaw);
        player.setPitch(pitch);
        player.prevYaw = yaw;
        player.prevPitch = pitch;
        player.renderYaw = yaw;
        player.lastRenderYaw = yaw;
        player.renderPitch = pitch;
        player.lastRenderPitch = pitch;

        player.setHeadYaw(yaw);
        player.prevHeadYaw = yaw;
        player.setBodyYaw(yaw);
        player.prevBodyYaw = yaw;

        player.setVelocity(velocity);
    }
}

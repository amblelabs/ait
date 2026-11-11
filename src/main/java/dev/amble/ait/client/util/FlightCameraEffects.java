package dev.amble.ait.client.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import dev.amble.ait.client.AITModClient;
import dev.amble.ait.core.entities.FlightTardisEntity;

public final class FlightCameraEffects {

    private static final float MAX_FOV_BOOST = 0.3f;
    private static final float FOV_SPEED_REF = 1.5f;

    private static final float MAX_ROLL = 12f;
    private static final float MAX_PITCH = 6f;

    private static final float ROLL_PER_STRAFE = 14f;
    private static final float ROLL_PER_TURN = 0.6f;
    private static final float PITCH_PER_CLIMB = 5f;

    private static final float FOV_EASE = 0.15f;
    private static final float TILT_EASE = 0.12f;

    private static final float SHAKE_YAW = 0.6f;
    private static final float SHAKE_PITCH = 0.3f;

    private static final Random RANDOM = Random.create();

    private static float fov = 1f, prevFov = 1f;
    private static float roll, prevRoll;
    private static float pitch, prevPitch;

    private static float pendingShake;
    private static float shakeYaw, prevShakeYaw;
    private static float shakePitch, prevShakePitch;

    private FlightCameraEffects() {
    }

    public static void shake(float scale) {
        pendingShake = Math.max(pendingShake, scale);
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(FlightCameraEffects::tick);
    }

    private static void tick(MinecraftClient client) {
        prevFov = fov;
        prevRoll = roll;
        prevPitch = pitch;
        prevShakeYaw = shakeYaw;
        prevShakePitch = shakePitch;

        float amount = MathHelper.clamp(pendingShake, 0f, 1f) * AITModClient.CONFIG.screenShake;
        pendingShake = 0;

        if (amount > 0 && !client.isPaused()) {
            shakeYaw = (RANDOM.nextFloat() * 2 - 1) * SHAKE_YAW * amount;
            shakePitch = (RANDOM.nextFloat() * 2 - 1) * SHAKE_PITCH * amount;
        } else {
            shakeYaw = 0;
            shakePitch = 0;
        }

        float targetFov = 1f;
        float targetRoll = 0f;
        float targetPitch = 0f;

        ClientPlayerEntity player = client.player;

        if (player != null && !client.isPaused() && player.getVehicle() instanceof FlightTardisEntity entity) {
            Vec3d motion = new Vec3d(entity.getX() - entity.prevX, entity.getY() - entity.prevY,
                    entity.getZ() - entity.prevZ);

            float fovScale = AITModClient.CONFIG.rwfFovEffect
                    * client.options.getFovEffectScale().getValue().floatValue();
            float rush = 1f - (float) Math.exp(-motion.length() / FOV_SPEED_REF);

            targetFov = 1f + MAX_FOV_BOOST * rush * fovScale;

            float tilt = AITModClient.CONFIG.rwfCameraTilt;

            if (tilt > 0 && !entity.isCollidingOnGround()) {
                float yaw = player.getYaw() * MathHelper.RADIANS_PER_DEGREE;

                double strafe = -motion.x * MathHelper.cos(yaw) - motion.z * MathHelper.sin(yaw);
                float turn = MathHelper.wrapDegrees(player.getYaw() - player.prevYaw);

                targetRoll = MathHelper.clamp((float) strafe * ROLL_PER_STRAFE + turn * ROLL_PER_TURN,
                        -MAX_ROLL, MAX_ROLL) * tilt;

                targetPitch = MathHelper.clamp((float) -motion.y * PITCH_PER_CLIMB,
                        -MAX_PITCH, MAX_PITCH) * tilt;
            }
        }

        fov = MathHelper.lerp(FOV_EASE, fov, targetFov);
        roll = MathHelper.lerp(TILT_EASE, roll, targetRoll);
        pitch = MathHelper.lerp(TILT_EASE, pitch, targetPitch);
    }

    public static double fovMultiplier(float tickDelta) {
        return MathHelper.lerp(tickDelta, prevFov, fov);
    }

    public static void applyTilt(MatrixStack matrices, float tickDelta) {
        float r = MathHelper.lerp(tickDelta, prevRoll, roll);
        float p = MathHelper.lerp(tickDelta, prevPitch, pitch) + MathHelper.lerp(tickDelta, prevShakePitch, shakePitch);
        float y = MathHelper.lerp(tickDelta, prevShakeYaw, shakeYaw);

        if (Math.abs(r) > 0.001f)
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(r));

        if (Math.abs(p) > 0.001f)
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(p));

        if (Math.abs(y) > 0.001f)
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(y));
    }
}

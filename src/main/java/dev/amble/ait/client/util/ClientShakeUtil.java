package dev.amble.ait.client.util;


import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

public class ClientShakeUtil {
    private static final float SHAKE_CLAMP = 45.0f; // Adjust this value to set the maximum shake angle
    private static final float SHAKE_INTENSITY = 0.5f; // Adjust this value to control the intensity of the shake
    private static final int MAX_DISTANCE = 16; // The radius from the console where the player will feel the shake

    public static boolean shouldShake(Tardis tardis) {
        if (ClientTardisUtil.getCurrentTardis() != tardis)
            return false;

        if (tardis.flight().falling().get())
            return true;

        return !tardis.travel().autopilot() && tardis.travel().getState() != TravelHandlerBase.State.LANDED;
    }

    /**
     * Shakes based off the distance of the player from the console
     */
    public static void shakeFromConsole() {
        shake(1f - (float) (ClientTardisUtil.distanceFromConsole() / MAX_DISTANCE));
    }

    public static void shakeFromEverywhere() {
        shake(0.1f);
    }

    public static void shake(float scale) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null)
            return;

        float targetPitch = getShakeX(client.player.getPitch(), scale);
        float targetYaw = getShakeY(client.player.getYaw(), scale);

        client.player.setPitch(MathHelper.lerp(SHAKE_INTENSITY, client.player.getPitch(), targetPitch));
        client.player.setYaw(MathHelper.lerp(SHAKE_INTENSITY, client.player.getYaw(), targetYaw));
    }

    private static float getShakeY(float baseYaw, float scale) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null)
            return baseYaw;

        float temp = (client.player.getRandom().nextFloat() * scale);
        float shakeYaw = baseYaw + (client.player.getRandom().nextBoolean() ? temp : -temp);

        return MathHelper.clamp(shakeYaw, baseYaw - SHAKE_CLAMP, baseYaw + SHAKE_CLAMP);
    }

    private static float getShakeX(float basePitch, float scale) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null)
            return basePitch;

        float temp = (client.player.getRandom().nextFloat() * (scale / 2));
        float shakePitch = basePitch + (client.player.getRandom().nextBoolean() ? temp : -temp);

        return MathHelper.clamp(shakePitch, basePitch - SHAKE_CLAMP, basePitch + SHAKE_CLAMP);
    }
}

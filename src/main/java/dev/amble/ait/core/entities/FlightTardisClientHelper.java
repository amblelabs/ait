package dev.amble.ait.core.entities;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;

import dev.amble.ait.client.util.FlightCameraEffects;
import dev.amble.ait.core.tardis.Tardis;

@Environment(EnvType.CLIENT)
public final class FlightTardisClientHelper {

    private static Perspective previousPerspective;

    private FlightTardisClientHelper() {
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (previousPerspective == null)
                return;

            if (client.player != null && client.player.getVehicle() instanceof FlightTardisEntity)
                return;

            client.options.setPerspective(previousPerspective);
            previousPerspective = null;
        });
    }

    public static void clientFlightTick(FlightTardisEntity entity, Tardis tardis) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != entity.getControllingPassenger())
            return;

        if (previousPerspective == null)
            previousPerspective = client.options.getPerspective();

        client.options.setPerspective(Perspective.THIRD_PERSON_BACK);

        boolean horizCollision = !entity.noClip && entity.horizontalCollision;
        boolean vertCollision  = !entity.noClip && entity.verticalCollision && !entity.isOnGround();

        if (horizCollision && !entity.prevHorizontalCollision) {
            FlightCameraEffects.shake(0.8f);
        } else if (vertCollision && !entity.prevUpwardCollision) {
            FlightCameraEffects.shake(0.4f);
        } else if (!entity.isCollidingOnGround()) {
            FlightCameraEffects.shake((float) (tardis.travel().speed() + entity.getVelocity().horizontalLength()) / tardis.travel().maxSpeed().get());
        }

        entity.prevHorizontalCollision = horizCollision;
        entity.prevUpwardCollision = vertCollision;
    }

    public static void finishLandClient() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.options.hudHidden = false;
    }
}

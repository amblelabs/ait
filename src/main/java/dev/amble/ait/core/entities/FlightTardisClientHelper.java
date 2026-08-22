package dev.amble.ait.core.entities;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;

import dev.amble.ait.client.util.ClientShakeUtil;
import dev.amble.ait.core.tardis.Tardis;

@Environment(EnvType.CLIENT)
public final class FlightTardisClientHelper {

    private FlightTardisClientHelper() {
    }

    public static void clientFlightTick(FlightTardisEntity entity, Tardis tardis) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != entity.getControllingPassenger())
            return;

        client.options.setPerspective(Perspective.THIRD_PERSON_BACK);

        boolean horizCollision = !entity.noClip && entity.horizontalCollision;
        boolean vertCollision  = !entity.noClip && entity.verticalCollision && !entity.isOnGround();

        if (horizCollision && !entity.prevHorizontalCollision) {
            ClientShakeUtil.shake(0.8f);
        } else if (vertCollision && !entity.prevUpwardCollision) {
            ClientShakeUtil.shake(0.4f);
        } else if (!entity.isCollidingOnGround()) {
            ClientShakeUtil.shake((float) (tardis.travel().speed() + entity.getVelocity().horizontalLength()) / tardis.travel().maxSpeed().get());
        }

        entity.prevHorizontalCollision = horizCollision;
        entity.prevUpwardCollision = vertCollision;
    }

    public static void finishLandClient() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
        client.options.hudHidden = false;
    }
}

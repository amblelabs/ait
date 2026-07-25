package dev.amble.ait.client.sounds.flight;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

import dev.amble.ait.client.sounds.EntityFollowingLoopingSound;
import dev.amble.ait.client.sounds.LoopingSound;
import dev.amble.ait.client.sounds.PlayerFollowingLoopingSound;
import dev.amble.ait.client.sounds.SoundHandler;
import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.sounds.flight.FlightSound;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;

public class ClientRealFlightSoundHandler extends SoundHandler {
    private static final double HEARING_RANGE = 32;
    private static final double AIR_START = 1;
    private static final double AIR_FULL = 3.5;
    public static LoopingSound AIR;
    public static LoopingSound VWORP;
    private FlightTardisEntity source;
    private FlightSound data;

    public static ClientRealFlightSoundHandler create() {
        return new ClientRealFlightSoundHandler();
    }

    private void generate(FlightTardisEntity entity, FlightSound sfx) {
        this.stopSounds();
        this.source = entity;
        this.data = sfx;

        AIR = new PlayerFollowingLoopingSound(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.AMBIENT, 0.05f);
        VWORP = new EntityFollowingLoopingSound(sfx.sound(), SoundCategory.BLOCKS, entity, 1.5f);

        this.ofSounds(AIR, VWORP);
    }

    public void tick(MinecraftClient client) {
        FlightTardisEntity entity = findNearestFlying(client);

        if (entity == null || !entity.isLinked()) {
            this.stopSounds();
            this.source = null;
            return;
        }

        Tardis tardis = entity.tardis().get();
        FlightSound sfx = tardis.stats().getFlightEffects();

        if (entity != this.source || this.data == null || !this.data.id().equals(sfx.id()))
            this.generate(entity, sfx);

        if (tardis.fuel().hasPower()) {
            VWORP.setPitch(pitch(tardis));
            this.startIfNotPlaying(VWORP);
        } else {
            this.stopSound(VWORP);
        }

        this.tickAir(client, entity);
    }

    private void tickAir(MinecraftClient client, FlightTardisEntity entity) {
        if (client.player == null || client.player.getVehicle() != entity || !hasAir(entity)) {
            this.stopSound(AIR);
            return;
        }

        float rush = (float) MathHelper.clamp((movedLastTick(entity) - AIR_START) / (AIR_FULL - AIR_START), 0, 1);

        if (rush <= 0) {
            this.stopSound(AIR);
            return;
        }

        AIR.setVolume(0.05f + 0.75f * rush);
        AIR.setPitch(0.8f + 0.4f * rush);

        this.startIfNotPlaying(AIR);
    }

    private static boolean hasAir(FlightTardisEntity entity) {
        Planet planet = PlanetRegistry.getInstance().get(entity.getWorld());
        return planet == null || planet.hasOxygen();
    }

    private static float pitch(Tardis tardis) {
        int maxSpeed = tardis.travel().maxSpeed().get();
        float throttle = maxSpeed > 0 ? (float) tardis.travel().speed() / maxSpeed : 0;

        return 0.85f + 0.4f * throttle;
    }

    private static double movedLastTick(FlightTardisEntity entity) {
        double x = entity.getX() - entity.prevX;
        double y = entity.getY() - entity.prevY;
        double z = entity.getZ() - entity.prevZ;

        return Math.sqrt(x * x + y * y + z * z);
    }

    private static FlightTardisEntity findNearestFlying(MinecraftClient client) {
        PlayerEntity player = client.player;

        if (player == null || client.world == null)
            return null;

        if (player.getVehicle() instanceof FlightTardisEntity riding)
            return riding;

        FlightTardisEntity nearest = null;
        double nearestDistance = HEARING_RANGE * HEARING_RANGE;

        for (FlightTardisEntity entity : client.world.getEntitiesByClass(FlightTardisEntity.class,
                player.getBoundingBox().expand(HEARING_RANGE), entity -> true)) {
            double distance = entity.squaredDistanceTo(player);

            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = entity;
            }
        }

        return nearest;
    }
}
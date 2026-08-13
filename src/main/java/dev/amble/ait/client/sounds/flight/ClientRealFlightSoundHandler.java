package dev.amble.ait.client.sounds.flight;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dev.amble.ait.client.sounds.PlayerFollowingLoopingSound;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

import dev.amble.ait.client.sounds.EntityFollowingLoopingSound;
import dev.amble.ait.client.sounds.LoopingSound;
import dev.amble.ait.client.sounds.SoundHandler;
import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.sounds.flight.FlightSound;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;

public class ClientRealFlightSoundHandler extends SoundHandler {

    private static final double AIR_START = 1;
    private static final double AIR_FULL = 3.5;

    /** How far (in blocks) a client will bother tracking/generating a TARDIS's flight sounds. */
    private static final double TRACK_RANGE = 64.0;
    private static final double TRACK_RANGE_SQ = TRACK_RANGE * TRACK_RANGE;

    /** Per-entity sound state, keyed by entity id, so multiple flying TARDISes can be heard at once. */
    private final Map<Integer, EntitySounds> active = new HashMap<>();

    public static ClientRealFlightSoundHandler create() {
        return new ClientRealFlightSoundHandler();
    }

    public void tick(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            this.stopAll();
            return;
        }

        Box searchBox = client.player.getBoundingBox().expand(TRACK_RANGE);
        Set<Integer> seen = new HashSet<>();

        for (FlightTardisEntity entity : client.world.getEntitiesByClass(FlightTardisEntity.class, searchBox, e -> true)) {
            if (!entity.isLinked() || entity.tardis().isEmpty())
                continue;

            if (entity.squaredDistanceTo(client.player) > TRACK_RANGE_SQ)
                continue;

            Tardis tardis = entity.tardis().get();
            FlightSound sfx = tardis.stats().getFlightEffects();

            seen.add(entity.getId());

            EntitySounds state = this.active.get(entity.getId());
            if (state == null || state.data == null || !state.data.id().equals(sfx.id())) {
                if (state != null)
                    state.stopAll(this);

                state = this.generate(entity, sfx);
                this.active.put(entity.getId(), state);
            }

            if (!entity.groundCollision) {
                this.tickVworp(tardis, state);
            } else {
                this.stopSound(state.vworp);
            }
            this.tickAir(entity, state);
        }

        // stop and drop tracking for anything that left range / unloaded / delinked
        this.active.entrySet().removeIf(entry -> {
            if (seen.contains(entry.getKey()))
                return false;

            entry.getValue().stopAll(this);
            return true;
        });
    }

    private EntitySounds generate(FlightTardisEntity entity, FlightSound sfx) {
        EntitySounds state = new EntitySounds();
        state.data = sfx;
        state.air = new EntityFollowingLoopingSound(entity, SoundEvents.ITEM_ELYTRA_FLYING,
                SoundCategory.AMBIENT, 0.05f);
        state.vworp = new EntityFollowingLoopingSound(entity, sfx.sound(), SoundCategory.BLOCKS, 1.5f);
        this.ofSounds(state.air, state.vworp);
        return state;
    }

    private void tickVworp(Tardis tardis, EntitySounds state) {
        if (tardis.fuel().hasPower() && tardis.travel().speed() > 0) {
            state.vworp.setPitch(pitch(tardis));
            this.startIfNotPlaying(state.vworp);
        } else {
            this.stopSound(state.vworp);
        }
    }

    private void tickAir(FlightTardisEntity entity, EntitySounds state) {
        if (!hasAir(entity)) {
            this.stopSound(state.air);
            return;
        }

        float rush = (float) MathHelper.clamp((movedLastTick(entity) - AIR_START) / (AIR_FULL - AIR_START), 0, 1);

        if (rush <= 0) {
            this.stopSound(state.air);
            return;
        }

        state.air.setVolume(0.05f + 0.75f * rush);
        state.air.setPitch(0.8f + 0.4f * rush);
        this.startIfNotPlaying(state.air);
    }

    private void stopAll() {
        this.active.values().forEach(state -> state.stopAll(this));
        this.active.clear();
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

    private static final class EntitySounds {
        FlightSound data;
        LoopingSound air;
        LoopingSound vworp;

        void stopAll(SoundHandler handler) {
            handler.stopSound(this.air);
            handler.stopSound(this.vworp);
        }
    }
}
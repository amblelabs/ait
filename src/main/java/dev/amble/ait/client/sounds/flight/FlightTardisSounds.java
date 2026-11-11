package dev.amble.ait.client.sounds.flight;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.sounds.flight.FlightSound;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;

public final class FlightTardisSounds {

    private static final double AIR_START = 1;
    private static final double AIR_FULL = 3.5;

    private final Identifier flightSound;
    private final Instance vworp;
    private final Instance air;

    private FlightTardisSounds(FlightTardisEntity entity, FlightSound sfx) {
        this.flightSound = sfx.id();
        this.vworp = new Instance(entity, sfx.sound(), SoundCategory.BLOCKS, true);
        this.air = new Instance(entity, SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.AMBIENT, false);
    }

    public static void tick(FlightTardisEntity entity) {
        if (!entity.isLinked() || entity.tardis().isEmpty())
            return;

        FlightSound sfx = entity.tardis().get().stats().getFlightEffects();
        FlightTardisSounds sounds = entity.clientSounds instanceof FlightTardisSounds existing ? existing : null;

        if (sounds != null && !sounds.vworp.isDone() && !sounds.air.isDone() && sounds.flightSound.equals(sfx.id()))
            return;

        if (sounds != null)
            sounds.stop();

        sounds = new FlightTardisSounds(entity, sfx);
        entity.clientSounds = sounds;

        MinecraftClient.getInstance().getSoundManager().play(sounds.vworp);
        MinecraftClient.getInstance().getSoundManager().play(sounds.air);
    }

    private void stop() {
        MinecraftClient.getInstance().getSoundManager().stop(this.vworp);
        MinecraftClient.getInstance().getSoundManager().stop(this.air);
    }

    private static final class Instance extends MovingSoundInstance {

        private final FlightTardisEntity entity;
        private final boolean engine;

        private Instance(FlightTardisEntity entity, SoundEvent sound, SoundCategory category, boolean engine) {
            super(sound, category, Random.create());

            this.entity = entity;
            this.engine = engine;
            this.repeat = true;
            this.repeatDelay = 0;
            this.volume = 0;
            this.follow();
        }

        @Override
        public boolean shouldAlwaysPlay() {
            return true;
        }

        @Override
        public void tick() {
            if (this.entity.isRemoved() || !this.entity.isLinked() || this.entity.tardis().isEmpty()) {
                this.setDone();
                return;
            }

            this.follow();

            if (this.engine) {
                this.tickEngine(this.entity.tardis().get());
            } else {
                this.tickAir();
            }
        }

        private void tickEngine(Tardis tardis) {
            boolean running = !this.entity.groundCollision && tardis.fuel().hasPower()
                    && tardis.travel().speed() > 0 && !tardis.cloak().cloaked().get();

            if (!running) {
                this.volume = 0;
                return;
            }

            int maxSpeed = tardis.travel().maxSpeed().get();
            float throttle = maxSpeed > 0 ? (float) tardis.travel().speed() / maxSpeed : 0;

            this.volume = 1.5f;
            this.pitch = 0.85f + 0.4f * throttle;
        }

        private void tickAir() {
            Planet planet = PlanetRegistry.getInstance().get(this.entity.getWorld());

            if (planet != null && !planet.hasOxygen()) {
                this.volume = 0;
                return;
            }

            double dx = this.entity.getX() - this.entity.prevX;
            double dy = this.entity.getY() - this.entity.prevY;
            double dz = this.entity.getZ() - this.entity.prevZ;
            double moved = Math.sqrt(dx * dx + dy * dy + dz * dz);

            float rush = (float) MathHelper.clamp((moved - AIR_START) / (AIR_FULL - AIR_START), 0, 1);

            this.volume = rush <= 0 ? 0 : 0.05f + 0.75f * rush;
            this.pitch = 0.8f + 0.4f * rush;
        }

        private void follow() {
            this.x = this.entity.getX();
            this.y = this.entity.getY();
            this.z = this.entity.getZ();
        }
    }
}

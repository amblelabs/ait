package dev.amble.ait.client.sounds.flight;

import java.util.Random;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.client.sounds.ClientSoundManager;
import dev.amble.ait.client.sounds.PositionedLoopingSound;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.sounds.flight.FlightSound;

public class InteriorFlightSound extends PositionedLoopingSound {
    private static final Random rnd = new Random();
    private FlightSound data;
    private int ticks = 0;
    private boolean dirty = true;

    public InteriorFlightSound(FlightSound data, SoundCategory soundCategory) {
        super(data.sound(), soundCategory, new BlockPos(0,0,0));
        this.data = data;
    }

    @Override
    public void tick() {
        super.tick();
        this.ticks++;

        if (this.ticks >= (this.getData().length() / this.pitch)) {
            this.refresh();
        }
    }

    public void refresh() {
        this.pitch = getRandomPitch();
        this.setPosition(ClientTardisUtil.getNearestConsole());
        this.ticks = 0;

        if (this.dirty) {
            //AITMod.LOGGER.warn("Interior Flight Sound is still dirty, stopping.."); // this must mean its not being tracked by the flight handler
            MinecraftClient.getInstance().getSoundManager().stop(this);
        }

        this.dirty = true;
    }
    public FlightSound getData() {
        if (this.data == null && ClientTardisUtil.getCurrentTardis() != null)
            this.data = ClientTardisUtil.getCurrentTardis().stats().getFlightEffects();

        return this.data;
    }
    public boolean isDirty() {
        return this.dirty;
    }
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    private static float getRandomPitch() {
        ClientTardis tardis = ClientTardisUtil.getCurrentTardis();

        if (tardis == null)
            return 1f;

        int speed = tardis.travel().speed();
        int maxSpeed = tardis.travel().maxSpeed().get();
        float speedPercentage = (float) speed / maxSpeed;

        if (ClientSoundManager.getFlight().hasThrottleAndHandbrakeDown(tardis)) {
            if (speedPercentage <= 0.33) {
                return 0.5f;
            } else if (speedPercentage <= 0.66) {
                return 0.55f;
            } else {
                return 0.6f;
            }
        }

        if (speedPercentage <= 0.33) {
            return rnd.nextFloat(0.9f, 0.95f);
        } else if (speedPercentage <= 0.66) {
            return rnd.nextFloat(0.95f, 1.0f);
        } else {
            return rnd.nextFloat(1.0f, 1.25f);
        }
    }
}

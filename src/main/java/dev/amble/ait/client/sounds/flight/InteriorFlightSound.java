package dev.amble.ait.client.sounds.flight;

import dev.amble.ait.client.sounds.PositionedLoopingSound;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.sounds.flight.FlightSound;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class InteriorFlightSound extends PositionedLoopingSound implements FlightSoundPlayer {
    private FlightSound data;
    private int ticks = 0;
    private boolean dirty = true;

    public InteriorFlightSound(FlightSound data, SoundCategory soundCategory) {
        super(data.sound(), soundCategory, new BlockPos(0,0,0), 0.25F);
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

    @Override
    public ClientTardis tardis() {
        return ClientTardisUtil.getCurrentTardis();
    }

    @Override
    public float getProgress() {
        if (this.data == null) return 0f;
        return (float) this.ticks / (this.data.length() / this.pitch);
    }

    @Override
    public void refresh() {
        this.pitch = FlightSoundPlayer.getRandomPitch(tardis());
        this.setPosition(ClientTardisUtil.getNearestConsole());
        this.ticks = 0;

        if (this.dirty || tardis() == null) {
            MinecraftClient.getInstance().getSoundManager().stop(this);
        }

        this.dirty = true;
    }

    @Override
    public FlightSound getData() {
        if (this.data == null && ClientTardisUtil.getCurrentTardis() != null)
            this.data = ClientTardisUtil.getCurrentTardis().stats().getFlightEffects();

        return this.data;
    }

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}

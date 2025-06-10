package dev.amble.ait.client.sounds.flight;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.client.sounds.PositionedLoopingSound;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.sounds.flight.FlightSound;

public class ExteriorFlightSound extends PositionedLoopingSound implements FlightSoundPlayer {
    private FlightSound data;
    private int ticks = 0;
    private boolean dirty = true;

    public ExteriorFlightSound(FlightSound data, SoundCategory soundCategory) {
        super(data.sound(), soundCategory, new BlockPos(0,0,0));
        this.data = data;
    }

    @Override
    public ClientTardis tardis() {
        return ClientTardisUtil.getNearestTardis(ClientFlightHandler.MAX_DISTANCE).orElse(null);
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
    public float getProgress() {
        if (this.data == null) return 0f;
        return (float) this.ticks / (this.data.length() / this.pitch);
    }

    @Override
    public void refresh() {
        ClientTardis tardis = tardis();
        this.pitch = FlightSoundPlayer.getRandomPitch(tardis);

        BlockPos pos = tardis != null ? tardis.travel().position().getPos() : BlockPos.ORIGIN;

        this.setPosition(pos);
        this.ticks = 0;

        if (this.dirty || tardis == null) {
            MinecraftClient.getInstance().getSoundManager().stop(this);
        }

        this.dirty = true;
    }

    @Override
    public FlightSound getData() {
        if (this.data == null && tardis() != null)
            this.data = tardis().stats().getFlightEffects();

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

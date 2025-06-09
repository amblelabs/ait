package dev.amble.ait.client.sounds.hum.exterior;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.client.sounds.PositionedLoopingSound;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.data.hum.Hum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.UUID;

public class ExteriorHumSound extends PositionedLoopingSound implements Linkable {
    private Hum data;
    private int ticks = 0;
    private boolean dirty = true;
    private TardisRef tardis;
    private float multiplier = 1f;

    public ExteriorHumSound(Hum data, SoundCategory soundCategory) {
        super(data.sound(), soundCategory, new BlockPos(0,0,0));
        this.data = data;
    }

    @Override
    public void tick() {
        super.tick();
        this.ticks++;

        this.refresh();
    }

    public void refresh() {
        // make quieter if doors closed
        if (this.isLinked()) {
            this.multiplier = tardis.get().door().isOpen() ? 0.25f : 0.05f;

            this.fixDistance();

            // find new nearest tardis every 5 seconds
            if (this.ticks % 100 == 0) {
                this.dirty = true;
                this.ticks = 0;
            }
        }
    }
    private void fixDistance() {
        // adjust volume based off distance
        // ( hum sounds are stereo )
        if (this.isLinked()) {
            BlockPos tPos = tardis.get().travel().position().getPos();

            double distance = Math.sqrt(tPos.getSquaredDistance(MinecraftClient.getInstance().player.getPos()));
            this.volume = (float) Math.max(0, (1 - ((distance / ExteriorHumHandler.MAX_DISTANCE))) * multiplier);

            if (!this.getPosition().equals(tPos)) {
                this.setPosition(tPos);
            }
        }
    }

    public Hum getData() {
        if (this.data == null) {
            Optional<ClientTardis> nearest = ClientTardisUtil.getNearestTardis(ExteriorHumHandler.MAX_DISTANCE);
            nearest.ifPresent(this::link);
        }

        return this.data;
    }
    public boolean isDirty() {
        return this.dirty;
    }
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public void link(Tardis tardis) {
        this.tardis = TardisRef.createAs(MinecraftClient.getInstance().player, tardis);
        this.data = tardis.hum().get();
    }

    @Override
    public void link(UUID id) {
        this.tardis = TardisRef.createAs(MinecraftClient.getInstance().player, id);
    }

    @Override
    public TardisRef tardis() {
        return this.tardis;
    }

}

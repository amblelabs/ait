package dev.amble.ait.tardis.v2.handler;

import dev.amble.ait.api.tardis.v2.handler.THandler;
import dev.amble.ait.tardis.v2.Tardis;
import dev.amble.ait.tardis.v2.data.DoorData;
import dev.amble.ait.tardis.v2.data.RepairData;
import dev.amble.ait.tardis.v2.event.TardisEvents;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;

public class DoorHandler implements THandler, TardisEvents {

    @Override
    public void event$repairTick(Tardis tardis, MinecraftServer server, RepairData repair) {
        tardis.resolve(DoorData.ID).setDoorParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE);
    }

    @Override
    public void event$repaired(Tardis tardis) {
        tardis.resolve(DoorData.ID).setDoorParticle(null);
    }
}

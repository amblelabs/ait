package dev.amble.ait.tardis.v2.handler;

import dev.amble.ait.api.tardis.v2.handler.THandler;
import dev.amble.ait.tardis.v2.Tardis;
import dev.amble.ait.tardis.v2.data.RepairData;
import dev.amble.ait.tardis.v2.event.ServerEvents;
import dev.amble.ait.tardis.v2.event.TardisEvents;
import net.minecraft.server.MinecraftServer;

public class RepairHandler implements THandler, ServerEvents {

    public boolean isToxic(Tardis tardis) {
        return tardis.resolve(RepairData.ID).state().get() == RepairData.State.TOXIC;
    }

    public boolean isUnstable(Tardis tardis) {
        return tardis.resolve(RepairData.ID).state().get() == RepairData.State.UNSTABLE;
    }

    public boolean isNormal(Tardis tardis) {
        return tardis.resolve(RepairData.ID).state().get() == RepairData.State.NORMAL;
    }

    @Override
    public void event$tardisTick(Tardis tardis, MinecraftServer server) {
        RepairData repair = tardis.resolve(RepairData.ID);

        RepairData.State state = repair.state().get();
        int repairTicks = repair.getRepairTicks();

        if (repairTicks <= 0) {
            if (state == RepairData.State.NORMAL)
                return;

            repair.state().set(RepairData.State.NORMAL);
            this.handle(TardisEvents.repaired(tardis));
            return;
        }

        if (repairTicks < RepairData.UNSTABLE_TICK_START_THRESHOLD
                && state != RepairData.State.UNSTABLE) {
            state = RepairData.State.UNSTABLE;
            repair.state().set(state);
        }

        if (state != RepairData.State.NORMAL)
            return;

        int res = this.handle(TardisEvents.repairTick(tardis, server, repair)) + 1;
        repair.setRepairTicks(tardis, repairTicks - res);
    }
}

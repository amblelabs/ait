package dev.amble.ait.core.tardis.handler;

import dev.amble.ait.api.tardis.TardisEvents;
import net.minecraft.server.MinecraftServer;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;

import java.util.Random;

public class CloakHandler extends KeyedTardisComponent implements TardisTickable {
    private static final Random RANDOM = new Random();
    private static final BoolProperty IS_CLOAKED = new BoolProperty("is_cloaked", false);
    private static final BoolProperty IS_SILENT = new BoolProperty("is_silent", false);
    private final BoolValue isCloaked = IS_CLOAKED.create(this);
    private final BoolValue isSilent = IS_SILENT.create(this);

    @Override
    public void onLoaded() {
        isCloaked.of(this, IS_CLOAKED);
        isSilent.of(this, IS_SILENT);
    }

    public CloakHandler() {
        super(Id.CLOAK);
    }

    public BoolValue cloaked() {
        return isCloaked;
    }

    public BoolValue silent() {
        return isSilent;
    }

    static {
        TardisEvents.LOSE_POWER.register(tardis -> tardis.cloak().cloaked().set(false));
    }

    @Override
    public void tick(MinecraftServer server) {
        if (!this.cloaked().get())
            return;

        TravelHandler travel = this.tardis.travel();

        if (travel.inFlight())
            return;

        if (!this.silent().get()) {
            this.tardis.removeFuel(2 * travel.instability()); // idle drain of 2 fuel per tick
        } else {
            this.tardis.removeFuel(4 * travel.instability()); // idle drain of 4 fuel per tick
            if (server.getTicks() % 20 == 0)
                this.tardis.subsystems().chameleon().removeDurability(RANDOM.nextInt(5, 16));
        }
    }
}

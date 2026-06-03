package dev.amble.ait.core.tardis.handler;

import net.minecraft.server.MinecraftServer;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;

import java.util.Random;

public class CloakHandler extends KeyedTardisComponent implements TardisTickable {
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

    @Override
    public void tick(MinecraftServer server) {
        if (!this.cloaked().get())
            return;

        if (!this.tardis.fuel().hasPower())
            this.cloaked().set(false);

        TravelHandler travel = this.tardis.travel();

        if (travel.inFlight())
            return;

        if (!this.silent().get()) {
            this.tardis.removeFuel(2 * travel.instability()); // idle drain of 2 fuel per tick
        } else {
            this.tardis.removeFuel(4 * travel.instability()); // idle drain of 4 fuel per tick
            this.tardis.subsystems().chameleon().removeDurability((float) new Random().nextInt(20, 51));
        }
    }
}

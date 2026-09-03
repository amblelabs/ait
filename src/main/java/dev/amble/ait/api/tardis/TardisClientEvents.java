package dev.amble.ait.api.tardis;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import dev.amble.ait.client.screens.interior.InteriorSettingsScreen;
import dev.amble.ait.client.tardis.ClientTardis;

@Environment(EnvType.CLIENT)
public class TardisClientEvents {

    public static final Event<SettingsSetup> SETTINGS_SETUP = EventFactory.createArrayBacked(SettingsSetup.class,
            callbacks -> screen -> {
                for (SettingsSetup callback : callbacks) {
                    callback.onSetup(screen);
                }
            });

    public static final Event<EnterClientTardis> ENTER_CLIENT_TARDIS = EventFactory.createArrayBacked(EnterClientTardis.class,
            callbacks -> tardis -> {
        for (EnterClientTardis callback : callbacks) {
            callback.enterClientTardis(tardis);
        }
    });

    @FunctionalInterface
    public interface SettingsSetup {
        void onSetup(InteriorSettingsScreen screen);
    }

    @FunctionalInterface
    public interface EnterClientTardis {
        void enterClientTardis(ClientTardis tardis);
    }
}

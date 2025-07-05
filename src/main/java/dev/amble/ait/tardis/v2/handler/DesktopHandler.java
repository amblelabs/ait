package dev.amble.ait.tardis.v2.handler;

import dev.amble.ait.api.tardis.v2.handler.THandler;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.tardis.v2.Tardis;
import dev.amble.ait.tardis.v2.data.DesktopData;
import dev.amble.ait.tardis.v2.event.TardisEvents;
import net.minecraft.sound.SoundCategory;

public class DesktopHandler implements THandler, TardisEvents {

    @Override
    public void event$disablePower(Tardis tardis) {
        this.playSoundAtEveryConsole(AITSounds.SHUTDOWN, SoundCategory.AMBIENT, 10f, 1f);
    }

    @Override
    public void event$enablePower(Tardis tardis) {
        this.playSoundAtEveryConsole(AITSounds.POWERUP, SoundCategory.AMBIENT, 10f, 1f);
        this.playSoundAtEveryConsole(AITSounds.CONSOLE_BOOTUP, SoundCategory.AMBIENT, 0.15f, 1f);
    }
}

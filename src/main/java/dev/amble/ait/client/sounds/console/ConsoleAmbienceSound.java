package dev.amble.ait.client.sounds.console;

import dev.amble.ait.client.sounds.PositionedLoopingSound;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.AITSounds;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;


public class ConsoleAmbienceSound extends PositionedLoopingSound {
    private int ticks = 0;

    public ConsoleAmbienceSound() {
        super(AITSounds.CONSOLE_AMBIENT, SoundCategory.BLOCKS, new BlockPos(0, 0, 0), 0.5f);
    }

    @Override
    public void tick() {
        super.tick();
        this.ticks++;

        if (this.ticks >= (40)) {
            this.refresh();
        }
    }

    public void refresh() {
        this.setPosition(ClientTardisUtil.getNearestConsole());
        this.ticks = 0;
    }
}
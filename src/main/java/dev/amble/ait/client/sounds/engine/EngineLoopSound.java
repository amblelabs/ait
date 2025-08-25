package dev.amble.ait.client.sounds.engine;

import dev.amble.ait.client.sounds.PositionedLoopingSound;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.AITSounds;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class EngineLoopSound extends PositionedLoopingSound {
    private int ticks = 0;

    public EngineLoopSound() {
        super(AITSounds.ENGINE_LOOP, SoundCategory.BLOCKS, new BlockPos(0, 0, 0), 0.35f);
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
        this.setPosition(ClientTardisUtil.getNearestEngine());
        this.ticks = 0;
    }
}

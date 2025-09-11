package dev.amble.ait.client.sounds.engine;

import dev.amble.ait.core.blockentities.EngineBlockEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.client.sounds.PositionedLoopingSound;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.AITSounds;

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
        if (EngineBlockEntity.doesEngineThere()) {
            this.setPosition(ClientTardisUtil.getNearestEngine());
        } else {
            BlockPos g = new BlockPos((29999984 - 30000 - (int) Math.floor(3000 + Math.random() * (2 + 3))), 200 - 95 + 5, 29999984 - 367);
            this.setPosition(g);
        }
        this.ticks = 0;
    }
}

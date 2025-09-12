package dev.amble.ait.client.sounds.engine;

import dev.amble.ait.client.AITModClient;
import dev.amble.ait.core.blockentities.EngineBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.client.sounds.PositionedLoopingSound;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.AITSounds;
import net.minecraft.world.World;

import java.util.UUID;

public class EngineLoopSound extends PositionedLoopingSound {
    private int ticks = 0;

    public EngineLoopSound() {
        super(AITSounds.ENGINE_LOOP, SoundCategory.BLOCKS, new BlockPos(0, 0, 0), AITModClient.CONFIG.engineLoopVolume);
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

        BlockPos nearestEngine = ClientTardisUtil.getNearestEngine();
        World world = MinecraftClient.getInstance().world;
        boolean isEngineBlockEntity = world != null && world.getBlockEntity(nearestEngine) instanceof EngineBlockEntity;

        this.setPosition(nearestEngine);

        if (isEngineBlockEntity) {
            this.setVolume(AITModClient.CONFIG.engineLoopVolume);
        } else {
            this.setVolume(0);
        }
        this.ticks = 0;
    }
}

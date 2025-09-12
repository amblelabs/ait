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
      
        this.setVolume(EngineBlockEntity.doesEngineBlockExist(getTardisUuid()) ? AITModClient.CONFIG.engineLoopVolume : 0);

        if (this.ticks >= (40)) {
            this.refresh();
        }
    }

    public void refresh() {
        if (EngineBlockEntity.doesEngineBlockExist(getTardisUuid())) {
            this.setPosition(ClientTardisUtil.getNearestEngine());
        } else {
            this.setVolume(0);
            
        }
        this.ticks = 0;
    }

    public UUID getTardisUuid() {
        BlockPos pos = ClientTardisUtil.getNearestEngine();
        World world = MinecraftClient.getInstance().world;
        if (world != null && world.getBlockEntity(pos) instanceof EngineBlockEntity engine) {
            return engine.tardis().getId();
        }
        return null;
    }
}

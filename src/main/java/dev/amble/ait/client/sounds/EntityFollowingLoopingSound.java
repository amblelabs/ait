package dev.amble.ait.client.sounds;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class EntityFollowingLoopingSound extends LoopingSound {
    private final Entity entity;

    public EntityFollowingLoopingSound(Entity entity, SoundEvent soundEvent, SoundCategory soundCategory, float volume, float pitch) {
        super(soundEvent, soundCategory);

        this.entity = entity;

        this.setPosition(this.entity == null ? new BlockPos(0,0,0) : this.entity.getBlockPos());
        this.setVolume(volume);
        this.setPitch(pitch);
        this.repeat = true;
    }

    public EntityFollowingLoopingSound(Entity entity, SoundEvent soundEvent, SoundCategory soundCategory, float volume) {
        this(entity, soundEvent, soundCategory, volume, 1);
    }

    public EntityFollowingLoopingSound(Entity entity, SoundEvent soundEvent, SoundCategory soundCategory) {
        this(entity, soundEvent, soundCategory, 1, 1);
    }

    @Override
    public void tick() {
        super.tick();
        this.setCoordsToPlayerCoords();
    }

    private void setCoordsToPlayerCoords() {
        if (this.entity == null || this.entity.getBlockPos() == null)
            return;
        this.setPosition(this.entity.getBlockPos());
    }
}

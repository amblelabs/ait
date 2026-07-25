package dev.amble.ait.client.sounds;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

@Environment(EnvType.CLIENT)
public class EntityFollowingLoopingSound extends LoopingSound {
    private final Entity entity;

    public EntityFollowingLoopingSound(SoundEvent soundEvent, SoundCategory soundCategory, Entity entity, float volume) {
        super(soundEvent, soundCategory);

        this.entity = entity;
        this.setVolume(volume);
        this.follow();
    }

    @Override
    public void tick() {
        if (this.entity.isRemoved()) {
            this.setDone();
            return;
        }

        this.follow();
    }

    private void follow() {
        this.x = this.entity.getX();
        this.y = this.entity.getY();
        this.z = this.entity.getZ();
    }
}
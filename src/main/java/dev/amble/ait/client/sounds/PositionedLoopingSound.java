package dev.amble.ait.client.sounds;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class PositionedLoopingSound extends LoopingSound {
    public PositionedLoopingSound(SoundEvent soundEvent, SoundCategory soundCategory, BlockPos pos, float volume,
            float pitch) {
        super(soundEvent, soundCategory);
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.volume = volume;
        this.pitch = pitch;
        this.repeat = true;
    }

    public PositionedLoopingSound(SoundEvent soundEvent, SoundCategory soundCategory, BlockPos pos, float volume) {
        this(soundEvent, soundCategory, pos, volume, 1);
    }

    public PositionedLoopingSound(SoundEvent soundEvent, SoundCategory soundCategory, BlockPos pos) {
        this(soundEvent, soundCategory, pos, 1, 1);
    }

    public void setPosition(BlockPos pos) {
        if (pos == null)
            return;

        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }
}

package dev.amble.ait.client.sounds;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

@Environment(EnvType.CLIENT)
public class MusicLoopingSound extends LoopingSound {
    public MusicLoopingSound(SoundEvent soundEvent, SoundCategory soundCategory, float volume) {
        super(soundEvent, soundCategory);

        this.relative = true;
        this.attenuationType = AttenuationType.NONE;
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.setVolume(volume);
    }
}

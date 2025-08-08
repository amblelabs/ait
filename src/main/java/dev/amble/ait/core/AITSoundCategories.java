package dev.amble.ait.core;

import net.minecraft.sound.SoundCategory;

public class AITSoundCategories {
    public static SoundCategory HUMS;

    public static SoundCategory getHums() {
        if (HUMS == null) {
            throw new IllegalStateException("HUMS SoundCategory has not been initialized yet.");
        }
        return HUMS;
    }
}

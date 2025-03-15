package dev.amble.ait.data;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class CreakSound {
    private final Identifier id;
    private final SoundEvent sound;

    protected CreakSound(Identifier id, SoundEvent sound) {
        this.id = id;
        this.sound = sound;
    }

    public Identifier id() {
        return this.id;
    }

    public SoundEvent sound() {
        return this.sound;
    }

    public static CreakSound create(String modId, String name, SoundEvent sound) {
        return new CreakSound(createId(modId, name), sound);
    }

    private static Identifier createId(String modid, String name) {
        return new Identifier(modid, "creak/" + name);
    }
}

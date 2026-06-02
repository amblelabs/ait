package dev.amble.ait.data.hum;

import dev.amble.ait.api.Nameable;
import dev.amble.lib.api.Identifiable;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class Hum implements Identifiable, Nameable {
    private final Identifier id;
    private final SoundEvent sound;

	protected Hum(Identifier id, SoundEvent sound) {
        this.id = id;
        this.sound = sound;
    }

    @Override
    public Identifier id() {
        return this.id;
    }

    public SoundEvent sound() {
        return this.sound;
    }

	public static Hum create(String modId, String name, SoundEvent sound) {
		return new Hum(Identifier.of(modId, name), sound);
	}

    @Override
    public String name() {
	    return this.id().toTranslationKey("hum");
    }
}

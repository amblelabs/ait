package dev.amble.ait.data.hum;

import java.util.Optional;

import dev.amble.lib.api.Identifiable;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import dev.amble.ait.api.Nameable;

public class Hum implements Identifiable, Nameable {
    private final Identifier id;
    private final SoundEvent sound;
    private final String name;

    protected Hum(String name, Identifier id, SoundEvent sound) {
        this.name = name;
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

    @Override
    public String name() {
        return this.name;
    }
    public Optional<String> nameOptional() {
        // u shitting me
        return Optional.ofNullable(this.name);
    }

    public static Hum create(String modId, String name, SoundEvent sound) {
        return new Hum(name, new Identifier(modId, name), sound);
    }
}

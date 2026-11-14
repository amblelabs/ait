package dev.amble.ait.data.hum;

import java.util.Optional;

import dev.amble.ait.api.Nameable;
import dev.amble.ait.data.Loyalty;
import dev.amble.lib.register.unlockable.Unlockable;
import org.jetbrains.annotations.Nullable;

import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Hum implements Unlockable, Nameable {
    private final Identifier id;
    private final SoundEvent sound;
    @Nullable
    private final Loyalty loyalty;

    protected Hum(Identifier id, SoundEvent sound, Optional<Loyalty> loyalty) {
        this.id = id;
        this.sound = sound;
        this.loyalty = loyalty.orElse(null);
    }

    protected Hum(Identifier id, SoundEvent sound) {
        this(id, sound, Optional.empty());
    }

    @Override
    public Identifier id() {
        return this.id;
    }

    public SoundEvent sound() {
        return this.sound;
    }

    @Override
    public UnlockType unlockType() {
        return UnlockType.HUM;
    }

    @Override
    public Optional<Loyalty> requirement() {
        return Optional.ofNullable(this.loyalty);
    }

    public static Hum create(String modId, String name, SoundEvent sound) {
        return new Hum(Identifier.of(modId, name), sound);
    }

    public static Hum create(String modId, String name, SoundEvent sound, Loyalty loyalty) {
        return new Hum(Identifier.of(modId, name), sound, Optional.of(loyalty));
    }

    @Override
    public String name() {
        return this.id().getPath();
    }

    @Override
    public Text text() {
        return Text.translatableWithFallback(this.id().toTranslationKey("hum"), this.name());
    }
}

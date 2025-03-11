package dev.amble.lib.register.unlockable;

import java.util.Objects;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.lib.api.Identifiable;

import dev.amble.ait.data.Loyalty;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

public interface Unlockable extends Identifiable {

    static <O extends Unlockable> RecordCodecBuilder<O, Loyalty> optionalRequirement(String name) {
        return Codec.optionalField(name, Loyalty.CODEC).xmap(
                o -> o.orElse(null),
                Optional::ofNullable
        ).forGetter(Unlockable::requirement);
    }

    UnlockType unlockType();

    @Nullable
    Loyalty requirement();

    /**
     * Decides whether this desktop should be auto-unlocked on creation. aka -
     * freebee, freeby
     */
    default boolean freebie() {
        return this.requirement() == null;
    }

    enum UnlockType {
        EXTERIOR, CONSOLE, SONIC, DESKTOP, DIMENSION
    }
}

package dev.amble.ait.core.sounds.landing;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.ait.AITMod;
import dev.amble.ait.api.Nameable;
import dev.amble.ait.data.Loyalty;
import dev.amble.lib.register.unlockable.Unlockable;

import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public record LandingNotification(Identifier id, Identifier soundId, String name, Optional<Loyalty> loyalty)
        implements Unlockable, Nameable {

    public static final Codec<LandingNotification> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(LandingNotification::id),
                    Identifier.CODEC.fieldOf("sound").forGetter(LandingNotification::soundId),
                    Codec.STRING.optionalFieldOf("name", "").forGetter(LandingNotification::name),
                    Loyalty.CODEC.optionalFieldOf("loyalty").forGetter(LandingNotification::loyalty)
            ).apply(instance, LandingNotification::new)
    );

    public LandingNotification {
        if (name.isEmpty()) {
            name = id.getPath();
        }
    }

    public LandingNotification(Identifier id, Identifier soundId, String name) {
        this(id, soundId, name, Optional.empty());
    }

    @Override
    public UnlockType unlockType() {
        return UnlockType.LANDING_NOTIFICATION;
    }

    @Override
    public Optional<Loyalty> requirement() {
        return this.loyalty;
    }

    @Override
    public Text text() {
        return Text.translatableWithFallback(this.id().toTranslationKey("landing"), this.name());
    }

    public SoundEvent sound() {
        SoundEvent sound = Registries.SOUND_EVENT.get(this.soundId());
        return sound != null ? sound : SoundEvent.of(this.soundId());
    }

    public static LandingNotification fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static LandingNotification fromJson(JsonObject json) {
        AtomicReference<LandingNotification> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(var -> created.set(var.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack landing notification: {}", err);
        });

        return created.get();
    }
}

package dev.amble.ait.core.tardis.control.sound;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.lib.api.Identifiable;

import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.data.schema.console.ConsoleTypeSchema;
import dev.amble.ait.registry.impl.ControlRegistry;
import dev.amble.ait.registry.impl.console.ConsoleRegistry;

/**
 * Represents a sound that is played when a control is used
 * @param controlId The identifier of the control that this sound is for
 * @param consoleId The identifier of the console that this sound is for
 * @param successId The identifier of the sound that is played when the control is successful
 * @param altId The identifier of the sound that is played when the control fails OR a value is switched
 * @see Control
 * @see ConsoleTypeSchema
 * @author duzo
 */
public record ControlSound(Identifier controlId, Identifier consoleId, Identifier successId, Identifier altId) implements Identifiable {
    public static final Codec<ControlSound> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("control").forGetter(ControlSound::controlId),
            Identifier.CODEC.fieldOf("console").forGetter(ControlSound::consoleId),
            Identifier.CODEC.fieldOf("success_sound").forGetter(ControlSound::successId),
            Identifier.CODEC.optionalFieldOf("alt_sound", SoundEvents.INTENTIONALLY_EMPTY.getId()).forGetter(ControlSound::altId)
    ).apply(instance, ControlSound::new));

    @Override
    public Identifier id() {
        return mergeIdentifiers(controlId, consoleId);
    }

    public ConsoleTypeSchema console() {
        return ConsoleRegistry.REGISTRY.get(this.consoleId());
    }
    public Control control() {
        return ControlRegistry.REGISTRY.get(this.controlId());
    }
    public SoundEvent sound(Control.Result result) {
        return !result.isAltSound() ? this.successSound() : this.altSound();
    }

    public SoundEvent successSound() {
        SoundEvent sfx = Registries.SOUND_EVENT.get(this.successId());

        if (sfx == null) {
            AITMod.LOGGER.error("Unknown success sound event: {} in control sfx {}", this.successId(), this.id());
            sfx = AITSounds.ERROR;
        }

        return sfx;
    }
    public SoundEvent altSound() {
        SoundEvent sfx = Registries.SOUND_EVENT.get(this.altId());

        if (sfx == null || this.altId() == SoundEvents.INTENTIONALLY_EMPTY.getId()) {
            AITMod.LOGGER.error("Unknown alt sound event: {} in control sfx {}", this.altId(), this.id());
            sfx = successSound();
        }

        return sfx;
    }

    public static ControlSound forFallback(Identifier controlId, SoundEvent success, SoundEvent alt) {
        return new ControlSound(controlId, AITMod.id("fallback"), success.getId(), alt.getId());
    }

    /**
     * Merges the two identifiers into one
     * Example
     * controlId - ait:monitor
     * consoleId - ait:hartnell
     * return - ait:hartnell/ait/monitor
     * @param controlId id of the control
     * @param consoleId id of the console
     * @return Merged identifier
     */
    public static Identifier mergeIdentifiers(Identifier controlId, Identifier consoleId) {
        return Identifier.of(consoleId.getNamespace(), consoleId.getPath() + "/" + controlId.getNamespace() + "/" + controlId.getPath());
    }

    public static ControlSound fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static ControlSound fromJson(JsonObject json) {
        AtomicReference<ControlSound> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(var -> created.set(var.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack console variant: {}", err);
        });

        return created.get();
    }
}

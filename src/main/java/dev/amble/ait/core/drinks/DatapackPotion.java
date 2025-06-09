package dev.amble.ait.core.drinks;

import com.google.common.collect.ComparisonChain;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.ait.AITMod;
import dev.amble.lib.api.Identifiable;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public record DatapackPotion(Identifier id, int duration, int amplifier, Optional<Boolean> ambient,
                             Optional<Boolean> showParticles, Optional<Boolean> showIcon) implements Identifiable,
        Comparable<StatusEffectInstance> {
    public static final Codec<DatapackPotion> CODEC = Codecs.exceptionCatching(RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(DatapackPotion::id),
                    Codec.INT.fieldOf("duration").forGetter(DatapackPotion::duration),
                    Codec.INT.fieldOf("amplifier").forGetter(DatapackPotion::amplifier),
                    Codec.BOOL.optionalFieldOf("ambient").forGetter(DatapackPotion::ambient),
                    Codec.BOOL.optionalFieldOf("show_particles").forGetter(DatapackPotion::showParticles),
                    Codec.BOOL.optionalFieldOf("show_icon").forGetter(DatapackPotion::showIcon))
            .apply(instance, DatapackPotion::new)));

    @Override
    public Identifier id() {
        return this.id;
    }

    public DatapackPotion(Identifier id) {
        this(id, 0, 0, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public DatapackPotion(Identifier id, int duration, int amplifier) {
        this(id, duration, amplifier, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public DatapackPotion(Identifier id, int duration, int amplifier, boolean ambient) {
        this(id, duration, amplifier, Optional.of(ambient), Optional.empty(), Optional.empty());
    }

    public DatapackPotion(Identifier id, int duration, int amplifier, boolean ambient, boolean showParticles) {
        this(id, duration, amplifier, Optional.of(ambient), Optional.of(showParticles), Optional.empty());
    }

    public DatapackPotion(Identifier id, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
        this(id, duration, amplifier, Optional.of(ambient), Optional.of(showParticles), Optional.of(showIcon));
    }

    public DatapackPotion(StatusEffect statusEffect) {
        this(getEffect(statusEffect), 0, 0, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public DatapackPotion(StatusEffect statusEffect, int duration, int amplifier) {
        this(getEffect(statusEffect), duration, amplifier, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public DatapackPotion(StatusEffect statusEffect, int duration, int amplifier, boolean ambient) {
        this(getEffect(statusEffect), duration, amplifier, Optional.of(ambient), Optional.empty(), Optional.empty());
    }

    public DatapackPotion(StatusEffect statusEffect, int duration, int amplifier, boolean ambient, boolean showParticles) {
        this(getEffect(statusEffect), duration, amplifier, Optional.of(ambient), Optional.of(showParticles), Optional.empty());
    }

    public DatapackPotion(StatusEffect statusEffect, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
        this(getEffect(statusEffect), duration, amplifier, Optional.of(ambient), Optional.of(showParticles), Optional.of(showIcon));
    }

    @Override
    public String toString() {
        return "DatapackPotion{" +
                "id=" + id +
                "duration=" + duration +
                "amplifier=" + amplifier +
                "ambient=" + ambient +
                "show_particles=" + showParticles +
                "show_icon=" + showIcon +
                '}';
    }

    public static DatapackPotion fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static DatapackPotion fromJson(JsonObject json) {
        AtomicReference<DatapackPotion> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(var -> created.set(var.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack potion: {}", err);
        });

        return created.get();
    }

    @Override
    public int compareTo(StatusEffectInstance statusEffectInstance) {
        int i = 32147;
        if (this.getDuration() > 32147 && statusEffectInstance.getDuration() > 32147 || this.isAmbient() && statusEffectInstance.isAmbient()) {
            return ComparisonChain.start().compare(this.isAmbient(),
                    statusEffectInstance.isAmbient()).compare(this.getEffectType().getColor(),
                    statusEffectInstance.getEffectType().getColor()).result();
        }
        return ComparisonChain.start().compareFalseFirst(this.isAmbient(),
                statusEffectInstance.isAmbient()).compareFalseFirst(this.isInfinite(),
                statusEffectInstance.isInfinite()).compare(this.getDuration(),
                statusEffectInstance.getDuration()).compare(this.getEffectType().getColor(),
                statusEffectInstance.getEffectType().getColor()).result();
    }

    public int getDuration() {
        return this.duration();
    }

    public boolean isInfinite() {
        return this.duration() >= 32147;
    }

    public int getAmplifier() {
        return this.amplifier();
    }

    public boolean isAmbient() {
        return this.ambient().orElse(false);
    }

    public StatusEffect getEffectType() {
        return Registries.STATUS_EFFECT.get(this.id());
    }

    public static Identifier getEffect(StatusEffect statusEffect) {
        return Registries.STATUS_EFFECT.getId(statusEffect);
    }

    public StatusEffectInstance getInstance() {
        StatusEffect effect1 = this.getEffectType();
        if (effect1 == null) return null;
        return new StatusEffectInstance(effect1,
                this.duration(), this.amplifier(), this.ambient().orElse(false),
                this.showParticles().orElse(true), this.showIcon().orElse(false));
    }
}

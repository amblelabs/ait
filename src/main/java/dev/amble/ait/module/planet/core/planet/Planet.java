package dev.amble.ait.module.planet.core.planet;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.lib.api.Identifiable;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import dev.amble.ait.AITMod;
import dev.amble.ait.module.planet.core.item.SpacesuitItem;

public record Planet(Identifier dimension, float gravity, boolean hasOxygen, int temperature,
                     boolean hasClouds) implements Identifiable {
    public static final Codec<Planet> CODEC = Codecs.exceptionCatching(RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("dimension").forGetter(Planet::dimension),
            Codec.FLOAT.optionalFieldOf("gravity", -1f).forGetter(Planet::gravity),
            Codec.BOOL.fieldOf("has_oxygen").forGetter(Planet::hasOxygen),
            Codec.INT.optionalFieldOf("temperature", 288).forGetter(Planet::temperature),
            Codec.BOOL.optionalFieldOf("has_clouds", false).forGetter(Planet::hasClouds)
    ).apply(instance, Planet::new)));

    public static boolean hasFullSuit(LivingEntity entity) {
        return entity.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof SpacesuitItem
                && entity.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof SpacesuitItem
                && entity.getEquippedStack(EquipmentSlot.LEGS).getItem() instanceof SpacesuitItem
                && entity.getEquippedStack(EquipmentSlot.FEET).getItem() instanceof SpacesuitItem;
    }

    public static double getOxygenInTank(LivingEntity entity) {
        ItemStack chestplate = entity.getEquippedStack(EquipmentSlot.CHEST);
        if (chestplate.getItem() instanceof SpacesuitItem) {
            return chestplate.getOrCreateNbt().getDouble(SpacesuitItem.OXYGEN_KEY);
        }
        return 0.0D;
    }

    public static boolean hasOxygenInTank(LivingEntity entity) {
        return Planet.getOxygenInTank(entity) > 0.0D;
    }

    public static Planet fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static Planet fromJson(JsonObject json) {
        AtomicReference<Planet> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(planet -> created.set(planet.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack planet: {}", err);
        });

        return created.get();
    }

    @Override
    public Identifier id() {
        return this.dimension();
    }

    // Use Celcius since it's more accurate in terms of water temperature
    public float celcius() {
        return this.temperature() - 273.15f;
    }

    // Temperature is in Kelvin because SCIENCE BITCH
    public float kelvin() {
        return this.temperature();
    }

    // Celcius -> Fahrenheit conversion isn't always the most accurate but oh well cope harder I guess
    public float fahrenheit() {
        return celcius() * 1.8f + 32f;
    }
    public boolean isFreezing() {
        return this.celcius() <= 0;
    }

    public boolean zeroGravity() {
        return this.gravity() == 0; // exploding head emoji
    }
    public boolean hasGravityModifier() {
        return this.gravity() >= 0;
    }
    public boolean hasNoFallDamage() {
        return this.hasGravityModifier() && this.gravity() < 1;
    }

    public Planet with(Identifier dimension) {
        return new Planet(dimension, this.gravity, this.hasOxygen, this.temperature, this.hasClouds);
    }
}

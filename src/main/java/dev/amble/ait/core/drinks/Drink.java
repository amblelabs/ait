package dev.amble.ait.core.drinks;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.ait.AITMod;
import dev.amble.ait.data.codec.MoreCodec;
import dev.amble.lib.api.Identifiable;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Vector3f;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public record Drink(Identifier id, Optional<Boolean> hasCustomColor, Optional<Vector3f> custom_color, List<DatapackPotion> potionInstances) implements Identifiable {
    public static final Codec<Drink> CODEC = Codecs.exceptionCatching(RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(Drink::id),
                    Codec.BOOL.optionalFieldOf("has_custom_color").forGetter(Drink::hasCustomColor),
                    MoreCodec.VECTOR3F.optionalFieldOf("custom_color").forGetter(Drink::custom_color),
                    DatapackPotion.CODEC.listOf().fieldOf("potion_instances").forGetter(Drink::potionInstances))
            .apply(instance, Drink::new)));

    @Override
    public Identifier id() {
        return this.id;
    }

    public Vector3f getColor() {
        return this.custom_color().orElse(new Vector3f());
    }

    public boolean getHasColor() {
        return this.hasCustomColor().orElse(false);
    }

    public static Drink fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static Drink fromJson(JsonObject json) {
        AtomicReference<Drink> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(var -> created.set(var.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack drink: {}", err);
        });

        return created.get();
    }

    public Collection<StatusEffectInstance> getEffects() {
        return this.potionInstances().stream()
                .map(DatapackPotion::getInstance)
                .collect(Collectors.toList());
    }
}

package dev.amble.ait.core.drinks;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.lib.api.Identifiable;
import org.joml.Vector3f;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.codec.MoreCodec;

public record BurntPage(Identifier id, Optional<Boolean> hasCustomColor, Optional<Vector3f> custom_color, List<DatapackPotion> potionInstances) implements Identifiable {
    public static final Codec<BurntPage> CODEC = Codecs.exceptionCatching(RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(BurntPage::id),
                    Codec.BOOL.optionalFieldOf("has_custom_color").forGetter(BurntPage::hasCustomColor),
                    MoreCodec.VECTOR3F.optionalFieldOf("custom_color").forGetter(BurntPage::custom_color),
                    DatapackPotion.CODEC.listOf().fieldOf("potion_instances").forGetter(BurntPage::potionInstances))
            .apply(instance, BurntPage::new)));

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

    public static BurntPage fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static BurntPage fromJson(JsonObject json) {
        AtomicReference<BurntPage> created = new AtomicReference<>();

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

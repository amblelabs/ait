package dev.amble.ait.core.likes;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.lib.api.Identifiable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import dev.amble.ait.AITMod;

public record ItemReward(Identifier id, ItemStack stack, int weight) implements Identifiable {
    public static final Codec<ItemReward> CODEC = Codecs.exceptionCatching(RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(ItemReward::id),
                    ItemStack.CODEC.fieldOf("stack").forGetter(ItemReward::stack),
                    Codec.INT.fieldOf("weight").forGetter(ItemReward::weight)
            ).apply(instance, ItemReward::new)
    ));

    @Override
    public Identifier id() {
        return this.id;
    }

    public static ItemReward fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static ItemReward fromJson(JsonObject json) {
        AtomicReference<ItemReward> created = new AtomicReference<>();
        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(var -> created.set(var.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding item reward JSON: {}", err);
        });
        return created.get();
    }

    public static ItemStack fromJsonStack(JsonObject json) {
        String itemId = json.get("id").getAsString();
        int count = json.has("Count") ? json.get("Count").getAsInt() : 1;
        Item item = Registries.ITEM.get(new Identifier(itemId));
        return new ItemStack(item, count);
    }
}

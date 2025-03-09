package dev.amble.ait.data.gson;

import java.lang.reflect.Type;

import com.google.gson.*;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return ItemStack.fromNbt(context.deserialize(json, NbtCompound.class));
    }

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.writeNbt(new NbtCompound()));
    }
}

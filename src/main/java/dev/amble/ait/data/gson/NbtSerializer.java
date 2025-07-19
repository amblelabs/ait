package dev.amble.ait.data.gson;

import java.lang.reflect.Type;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.visitor.StringNbtWriter;

import dev.amble.ait.AITMod;

public class NbtSerializer implements JsonSerializer<NbtCompound>, JsonDeserializer<NbtCompound> {
    @Override
    public NbtCompound deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            return StringNbtReader.parse(json.getAsString());
        } catch (CommandSyntaxException e) {
            AITMod.LOGGER.error("Invalid NBT string: {}", json.getAsString());
            return new NbtCompound();
        }
    }

    @Override
    public JsonElement serialize(NbtCompound src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(new StringNbtWriter().apply(src));
    }
}

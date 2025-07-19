package dev.amble.ait.data.gson;

import java.lang.reflect.Type;

import com.google.gson.*;

import net.minecraft.util.math.BlockPos;

public class BlockPosSerializer implements JsonDeserializer<BlockPos>, JsonSerializer<BlockPos> {

    @Override
    public BlockPos deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        int x = obj.get("x").getAsInt();
        int y = obj.get("y").getAsInt();
        int z = obj.get("z").getAsInt();

        return new BlockPos(x, y, z);
    }

    @Override
    public JsonElement serialize(BlockPos src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        result.addProperty("x", src.getX());
        result.addProperty("y", src.getY());
        result.addProperty("z", src.getZ());

        return result;
    }
}

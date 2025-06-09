package dev.amble.ait.data.gson;

import com.google.gson.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.lang.reflect.Type;

public class GlobalPosSerializer implements JsonDeserializer<GlobalPos>, JsonSerializer<GlobalPos> {

    @Override
    public GlobalPos deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        RegistryKey<World> dimension = context.deserialize(obj.get("dimension"), RegistryKey.class);

        int x = obj.get("x").getAsInt();
        int y = obj.get("y").getAsInt();
        int z = obj.get("z").getAsInt();

        return GlobalPos.create(dimension, new BlockPos(x, y, z));
    }

    @Override
    public JsonElement serialize(GlobalPos src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        result.add("dimension", context.serialize(src.getDimension().getValue()));
        result.addProperty("x", src.getPos().getX());
        result.addProperty("y", src.getPos().getY());
        result.addProperty("z", src.getPos().getZ());

        return result;
    }
}

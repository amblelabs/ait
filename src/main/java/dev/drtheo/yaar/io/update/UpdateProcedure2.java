package dev.drtheo.yaar.io.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.amble.ait.api.tardis.v2.data.TData;
import dev.amble.ait.api.tardis.v2.data.TDataRegistry;
import mock.Identifier;

import java.util.Map;

public class UpdateProcedure2 implements AbstractUpdateProcedure {

    @Override
    public JsonObject update(JsonObject obj) {
        JsonObject handlers = obj.getAsJsonObject("handlers");

        JsonObject doorHandler = handlers.getAsJsonObject("DOOR");
        doorHandler.addProperty("leftDoorRot", doorHandler.getAsJsonObject("leftDoorRot").get("value").getAsInt());
        doorHandler.addProperty("rightDoorRot", doorHandler.getAsJsonObject("rightDoorRot").get("value").getAsInt());

        doorHandler.add("state", doorHandler.get("doorState"));

        JsonArray newHandlers = new JsonArray();

        for (Map.Entry<String, JsonElement> rawHandler : handlers.entrySet()) {
            JsonObject handler = rawHandler.getValue().getAsJsonObject();
            String rawId = rawHandler.getKey();

            Identifier id = new Identifier("ait", rawId.toLowerCase());
            TData.Holder<?> holder = TDataRegistry.get(id);

            if (holder == null) {
                System.err.println("Unknown id " + id);
                continue;
            }

            handler.addProperty("id", id.toString());
            newHandlers.add(handler);
        }

        JsonObject desktop = obj.getAsJsonObject("desktop");
        JsonObject exterior = obj.getAsJsonObject("exterior");

        obj.remove("desktop");
        obj.remove("exterior");

        desktop.addProperty("id", "ait:desktop");
        exterior.addProperty("id", "ait:exterior");

        newHandlers.add(desktop);
        newHandlers.add(exterior);

        JsonObject updated = new JsonObject();
        updated.addProperty("version", 3);
        updated.add("handlers", newHandlers);
        updated.add("id", obj.get("uuid"));

        return updated;
    }
}

package dev.drtheo.yaar.io;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.amble.ait.api.tardis.v2.data.TDataHolder;
import dev.drtheo.yaar.Tardis;
import dev.amble.ait.api.tardis.v2.data.TData;
import dev.amble.ait.api.tardis.v2.data.TDataRegistry;
import dev.drtheo.yaar.io.update.AbstractUpdateProcedure;
import dev.drtheo.yaar.io.update.UpdateProcedure2;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class TardisFileManager {

    public static final int VERSION = 3;

    private static final AbstractUpdateProcedure[] PROCEDURES = new AbstractUpdateProcedure[] {
            null, null, new UpdateProcedure2()
    };

    public void write(Tardis tardis) {

    }

    public Tardis read(Path path) throws IOException {
        JsonObject obj = JsonParser.parseReader(Files.newBufferedReader(path)).getAsJsonObject();
        int version = getVersion(obj);

        for (int i = version; i < VERSION; i++) {
            System.out.println("Updating from v" + version + " to v" + (i + 1));
            obj = PROCEDURES[i].update(obj);
        }

        UUID id = UUID.fromString(obj.get("id").getAsString());
        JsonArray handlers = obj.getAsJsonArray("handlers");

        Tardis tardis = new Tardis(id);

        for (JsonElement rawHandler : handlers) {
            JsonObject handler = rawHandler.getAsJsonObject();
            Identifier handlerId = Identifier.tryParse(handler.get("id").getAsString());

            TDataHolder<?> holder = TDataRegistry.get(handlerId);

            if (holder == null) {
                System.err.println("Failed to deserialize missing handler " + handlerId);
                continue;
            }

            TData<?> data = holder.decode(handler);
            tardis.attach(data);
        }

        return tardis;
    }

    private static int getVersion(JsonObject object) {
        JsonElement raw = object.get("version");
        return raw == null ? 0 : raw.getAsInt();
    }
}

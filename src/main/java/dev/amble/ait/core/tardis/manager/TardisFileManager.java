package dev.amble.ait.core.tardis.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;

public class TardisFileManager {

    private boolean locked = true;

    public TardisFileManager() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            this.locked = false;

            try {
                Files.createDirectories(getRootSavePath(server));
            } catch (IOException ignored) { }
        });
    }

    public void delete(MinecraftServer server, UUID uuid) {
        try {
            Files.deleteIfExists(TardisFileManager.getSavePath(server, uuid, "json"));
        } catch (IOException e) {
            AITMod.LOGGER.error("Failed to delete TARDIS {}", uuid, e);
        }
    }

    public static Path getRootSavePath(MinecraftServer server) {
        return server.getSavePath(WorldSavePath.ROOT).resolve(".ait");
    }

    private static Path getSavePath(MinecraftServer server, UUID uuid, String suffix) throws IOException {
        return TardisFileManager.getRootSavePath(server).resolve(uuid.toString() + "." + suffix);
    }

    public Either<ServerTardis, Exception> loadTardis(MinecraftServer server, ServerTardisManager manager, UUID uuid) {
        if (this.locked)
            return null;

        long start = System.currentTimeMillis();

        try {
            Path file = TardisFileManager.getSavePath(server, uuid, "json");
            String raw = Files.readString(file);

            JsonObject object = JsonParser.parseString(raw).getAsJsonObject();

            /*
             * JsonElement element = JsonParser.parseString(json); JsonObject object =
             * element.getAsJsonObject();
             *
             * int version = object.get("VERSION_SCHEMA").getAsInt();
             *
             * if (version == 0) new JsonObjectTransform(object).transform();
             */
            ServerTardis tardis = manager.getFileGson().fromJson(object, ServerTardis.class);
            Tardis.init(tardis, TardisComponent.InitContext.deserialize());

            AITMod.LOGGER.info("Deserialized {} in {}ms", tardis, System.currentTimeMillis() - start);
            return Either.left(tardis);
        } catch (IOException e) {
            AITMod.LOGGER.warn("Failed to load {}!", uuid);
            e.printStackTrace();

            return Either.right(e);
        }
    }

    public void saveTardis(MinecraftServer server, ServerTardisManager manager, ServerTardis tardis) {
        try {
            Path savePath = TardisFileManager.getSavePath(server, tardis.getUuid(), "json");
            Files.writeString(savePath, manager.getFileGson().toJson(tardis, ServerTardis.class));
        } catch (IOException e) {
            AITMod.LOGGER.warn("Couldn't save TARDIS {}", tardis.getUuid(), e);
        }
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }

    public List<UUID> getTardisList(MinecraftServer server) {
        try {
            return Files.list(TardisFileManager.getRootSavePath(server)).map(path -> {
                String name = path.getFileName().toString();
                return UUID.fromString(name.substring(0, name.indexOf('.')));
            }).toList();
        } catch (IOException e) {
            AITMod.LOGGER.error("Failed to list TARDIS files", e);
        }

        return List.of();
    }

    @FunctionalInterface
    public interface Loader {
        ServerTardis apply(Gson gson, JsonObject object);
    }
}
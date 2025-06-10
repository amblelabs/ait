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
import org.jetbrains.annotations.NotNull;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisManager;

public class TardisFileManager<T extends Tardis> {

    private boolean locked = false;

    public void delete(MinecraftServer server, UUID uuid) {
        try {
            Files.deleteIfExists(TardisFileManager.getSavePath(server, uuid, "json"));
        } catch (IOException e) {
            AITMod.LOGGER.error("Failed to delete TARDIS {}", uuid, e);
        }
    }

    private static Path getRootSavePath(Path root) {
        return root.resolve(".ait");
    }

    public static Path getRootSavePath(MinecraftServer server) {
        return TardisFileManager.getRootSavePath(server.getSavePath(WorldSavePath.ROOT));
    }

    private static Path getSavePath(MinecraftServer server, UUID uuid, String suffix) throws IOException {
        Path result = TardisFileManager.getRootSavePath(server).resolve(uuid.toString() + "." + suffix);
        Files.createDirectories(result.getParent());

        return result;
    }

    public Either<T, Exception> loadTardis(MinecraftServer server, TardisManager<T, ?> manager, UUID uuid, TardisLoader<T> function) {
        long start = System.currentTimeMillis();

        try {
            Path file = TardisFileManager.getSavePath(server, uuid, "json");
            String raw = Files.readString(file);

            JsonObject object = JsonParser.parseString(raw).getAsJsonObject();

            // TODO letting the autistic do it because im not taking my fucking ritalin at 1
            // in the
            // morning to do a
            // dumbass menial task of replacing a bunch of json info
            // this is a dumb way of doing it. do it fucking better.
            // i thought programming was supposed to be simplifying processes not making me
            // do more
            // <3333
            // - Loqor

            // i am not autistic
            // also this is life, should've made it better from the start
            // not your fault tho, i blame duzo
            // - Theo

            /*
             * JsonElement element = JsonParser.parseString(json); JsonObject object =
             * element.getAsJsonObject();
             *
             * int version = object.get("VERSION_SCHEMA").getAsInt();
             *
             * if (version == 0) new JsonObjectTransform(object).transform();
             */

            T tardis = function.readTardis(manager.getFileGson(), object);

            AITMod.LOGGER.info("Deserialized {} in {}ms", tardis, System.currentTimeMillis() - start);
            return Either.left(tardis);
        } catch (Exception e) {
            AITMod.LOGGER.warn("Failed to load {}!", uuid);
            AITMod.LOGGER.warn(e.getMessage());
            return Either.right(e);
        }
    }

    public void saveTardis(MinecraftServer server, TardisManager<T, ?> manager, @NotNull T tardis) {
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
    public interface TardisLoader<T> {
        T readTardis(Gson gson, JsonObject object);
    }
}

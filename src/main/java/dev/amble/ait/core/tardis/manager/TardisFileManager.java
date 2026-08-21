package dev.amble.ait.core.tardis.manager;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisManager;
import org.jetbrains.annotations.NotNull;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;

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
            return this.getTardisListChecked(server);
        } catch (IOException | RuntimeException exception) {
            AITMod.LOGGER.error("Failed to list TARDIS files", exception);
            return List.of();
        }
    }

    public List<UUID> getTardisListChecked(MinecraftServer server) throws IOException {
        Path root = TardisFileManager.getRootSavePath(server);
        if (Files.notExists(root))
            return List.of();
        if (!Files.isDirectory(root))
            throw new IOException("TARDIS save path is not an accessible directory: " + root);

        List<UUID> result = new ArrayList<>();

        try (Stream<Path> paths = Files.list(root)) {
            paths.forEach(path -> {
                String name = path.getFileName().toString();
                if (!name.endsWith(".json"))
                    return;

                try {
                    UUID uuid = UUID.fromString(name.substring(0, name.length() - ".json".length()));
                    if (!Files.isRegularFile(path))
                        throw new UncheckedIOException(new IOException(
                                "TARDIS save is not an accessible regular file: " + path));
                    result.add(uuid);
                } catch (IllegalArgumentException exception) {
                    AITMod.LOGGER.warn("Ignoring invalid TARDIS file name {}", name);
                }
            });
        } catch (UncheckedIOException exception) {
            throw exception.getCause();
        }

        return result;
    }

    public boolean hasTardisFileChecked(MinecraftServer server, UUID uuid) throws IOException {
        Path path = TardisFileManager.getRootSavePath(server).resolve(uuid.toString() + ".json");
        if (Files.notExists(path))
            return false;
        if (!Files.isRegularFile(path))
            throw new IOException("TARDIS save is not an accessible regular file: " + path);
        return true;
    }

    /**
     * Reads only the persisted location needed to arbitrate exact home claims.
     * This deliberately avoids deserializing or loading a dormant TARDIS and its
     * interior world. Old saves without an explicit home use their persisted
     * travel position, matching {@code StatsHandler}'s initialization fallback.
     */
    public StoredHome readStoredHome(MinecraftServer server, UUID uuid) throws IOException {
        Path path = TardisFileManager.getSavePath(server, uuid, "json");
        JsonObject root;

        try (Reader reader = Files.newBufferedReader(path)) {
            root = JsonParser.parseReader(reader).getAsJsonObject();
        }

        JsonObject handlers = requiredChild(root, "handlers", uuid);
        JsonObject stats = requiredChild(handlers, "STATS", uuid);
        JsonElement persistedHome = stats.get("home");
        JsonObject location;

        if (persistedHome == null || persistedHome.isJsonNull()) {
            JsonObject travel = child(handlers, "TRAVEL");
            location = child(travel, "position");
        } else if (persistedHome.isJsonObject()) {
            location = persistedHome.getAsJsonObject();
        } else {
            throw new IllegalStateException("Invalid persisted home for TARDIS " + uuid);
        }

        JsonObject dimension = child(location, "dimension");
        JsonObject position = child(location, "pos");
        if (dimension == null || position == null)
            throw new IllegalStateException("Missing persisted home for TARDIS " + uuid);

        JsonElement dimensionValue = dimension.get("value");
        Identifier dimensionId = dimensionValue == null ? null : Identifier.tryParse(dimensionValue.getAsString());
        if (dimensionId == null)
            throw new IllegalStateException("Invalid persisted home dimension for TARDIS " + uuid);

        return new StoredHome(dimensionId, new BlockPos(
                position.get("x").getAsInt(),
                position.get("y").getAsInt(),
                position.get("z").getAsInt()));
    }

    private static JsonObject child(JsonObject parent, String key) {
        if (parent == null)
            return null;

        JsonElement element = parent.get(key);
        return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
    }

    private static JsonObject requiredChild(JsonObject parent, String key, UUID uuid) {
        JsonObject result = child(parent, key);
        if (result == null)
            throw new IllegalStateException("Invalid persisted " + key + " for TARDIS " + uuid);
        return result;
    }

    public record StoredHome(Identifier dimension, BlockPos position) {
    }

    @FunctionalInterface
    public interface TardisLoader<T> {
        T readTardis(Gson gson, JsonObject object);
    }
}

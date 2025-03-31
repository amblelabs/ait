package dev.amble.ait.core.tardis.animation.v2.blockbench;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.amble.lib.AmbleKit;
import dev.amble.lib.util.ServerLifecycleHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import org.joml.Vector3f;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.animation.v2.keyframe.AnimationKeyframe;
import dev.amble.ait.core.tardis.animation.v2.keyframe.KeyframeTracker;

public class BlockbenchParser implements
        SimpleSynchronousResourceReloadListener {
    private static final Identifier SYNC = AITMod.id("blockbench_sync");

    private static final Lock LOCK = new ReentrantLock();

    private final HashMap<Identifier, Result> lookup = new HashMap<>();
    private final HashMap<Identifier, JsonObject> rawLookup = new HashMap<>();
    private static final BlockbenchParser instance = new BlockbenchParser();

    private BlockbenchParser() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(this);
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> this.sync(player));
    }

    public static BlockbenchParser getInstance() {
        return instance;
    }

    public static void init() {
        if (EnvType.CLIENT == FabricLoader.getInstance().getEnvironmentType()) initClient();
    }

    @Environment(EnvType.CLIENT)
    private static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(SYNC, (client, handler, buf, responseSender) -> {
            BlockbenchParser.getInstance().receive(buf);
        });
    }

    private PacketByteBuf toBuf() {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(this.rawLookup.size());
        for (Map.Entry<Identifier, JsonObject> entry : this.rawLookup.entrySet()) {
            buf.writeIdentifier(entry.getKey());
            buf.writeString(entry.getValue().toString());
        }

        return buf;
    }

    private void sync(ServerPlayerEntity target) {
        if (ServerLifecycleHooks.get() == null) return;

        ServerPlayNetworking.send(target, SYNC, toBuf());
    }

    private void sync() {
        if (ServerLifecycleHooks.get() == null) return;

        PacketByteBuf buf = toBuf();

        for (ServerPlayerEntity player : ServerLifecycleHooks.get().getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, SYNC, buf);
        }
    }

    private void receive(PacketByteBuf buf) {
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            Identifier id = buf.readIdentifier();
            String json = buf.readString();

            this.rawLookup.put(id, JsonParser.parseString(json).getAsJsonObject());
            this.lookup.put(id, parse(this.rawLookup.get(id)));
        }

        AITMod.LOGGER.info("Received {} blockbench animation files", this.rawLookup.size());
    }

    @Override
    public Identifier getFabricId() {
        return AITMod.id("blockbench_parser");
    }

    @Override
    public void reload(ResourceManager manager) {
        for (Identifier id : manager
                .findResources("fx/blockbench", filename -> filename.getPath().endsWith("animation.json")).keySet()) {
            try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                this.rawLookup.put(id, JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());

                Result created = parse(this.rawLookup.get(id));

                this.lookup.put(id, created);
                AmbleKit.LOGGER.info("Loaded blockbench file {}", id.toString());
            } catch (Exception e) {
                AmbleKit.LOGGER.error("Error occurred while loading resource json {}", id.toString(), e);
            }
        }

        this.sync();
    }

    public record Result(KeyframeTracker<Float> alpha,
                         KeyframeTracker<Vector3f> rotation,
                         KeyframeTracker<Vector3f> translation,
                         KeyframeTracker<Vector3f> scale) {
    }

    public static Result getOrThrow(Identifier id) {
        Result result = getInstance().lookup.get(id.withPrefixedPath("fx/blockbench/").withSuffixedPath(".animation.json"));

        if (result == null) {
            throw new IllegalStateException("No blockbench animation found for " + id);
        }

        return result;
    }

    public static Result getOrFallback(Identifier id) {
        try {
            return getOrThrow(id);
        } catch (IllegalStateException e) {
            AITMod.LOGGER.error(String.valueOf(e));
            return getInstance().lookup.values().iterator().next();
        }
    }

    public static Result parse(InputStream stream) {
        return parse(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static Result parse(JsonObject json) {
        // get animations
        JsonObject animations = json.getAsJsonObject("animations");
        JsonObject firstAnimation = animations.getAsJsonObject(animations.keySet().iterator().next()); // get the first found animation

        JsonObject bones = firstAnimation.getAsJsonObject("bones");

        return parseTracker(bones.getAsJsonObject(bones.keySet().iterator().next()), firstAnimation.getAsJsonObject("timeline"));
    }

    private static Result parseTracker(JsonObject main, JsonObject timeline) {
        KeyframeTracker<Vector3f> rotation = parseVectorKeyframe(main.getAsJsonObject("rotation"), 1f);
        KeyframeTracker<Vector3f> translation = parseVectorKeyframe(main.getAsJsonObject("position"), 16f);
        KeyframeTracker<Vector3f> scale = parseVectorKeyframe(main.getAsJsonObject("scale"), 1f);
        KeyframeTracker<Float> alpha = parseAlphaKeyframe(timeline);

        return new Result(alpha, rotation, translation, scale);
    }

    private static KeyframeTracker<Float> parseAlphaKeyframe(JsonObject object) {
        /*
            "timeline": {
                "0.0": "1;",
                "1.0": "0;"
            }
         */

        if (object == null) {
            ArrayList<AnimationKeyframe<Float>> list = new ArrayList<>();

            list.add(new AnimationKeyframe<>(20, AnimationKeyframe.Interpolation.CUBIC, new AnimationKeyframe.InterpolatedFloat(1f, 1f)));

            return new KeyframeTracker<>(list);
        }

        List<AnimationKeyframe<Float>> list = new ArrayList<>();

        TreeMap<Float, Float> alphaMap = new TreeMap<>();


        for (String key : object.keySet()) {
            float time = Float.parseFloat(key);

            String alphaStr = object.get(key).getAsString();
            float alpha = Float.parseFloat(alphaStr.substring(0, alphaStr.length() - 1)); // everything but last character ";"

            alphaMap.put(time, alpha);
        }

        for (Map.Entry<Float, Float> current : alphaMap.entrySet()) {
            Float currentTime = current.getKey();
            Float currentAlpha = current.getValue();
            Map.Entry<Float, Float> nextEntry = alphaMap.higherEntry(currentTime);

            if (nextEntry != null) {
                Float nextTime = nextEntry.getKey();
                Float nextAlpha = nextEntry.getValue();

                AnimationKeyframe<Float> frame = new AnimationKeyframe<>((nextTime - currentTime) * 20, AnimationKeyframe.Interpolation.CUBIC, new AnimationKeyframe.InterpolatedFloat(currentAlpha, nextAlpha));

                list.add(frame);
            }
        }

        return new KeyframeTracker<>(list);
    }

    private static KeyframeTracker<Vector3f> parseVectorKeyframe(JsonObject object, float divider) {
        List<AnimationKeyframe<Vector3f>> list = new ArrayList<>();

        if (object == null) {
            list.add(new AnimationKeyframe<>(20, AnimationKeyframe.Interpolation.LINEAR, new AnimationKeyframe.InterpolatedVector3f(new Vector3f(1f), new Vector3f(1f))));

            return new KeyframeTracker<>(list);
        }

        TreeMap<Float, Pair<Vector3f, AnimationKeyframe.Interpolation>> map = new TreeMap<>();

        for (String key : object.keySet()) {
            float time = Float.parseFloat(key);

            Vector3f vector;
            AnimationKeyframe.Interpolation type;

            if (object.get(key).isJsonObject()) {
                JsonObject data = object.get(key).getAsJsonObject();
                vector = parseVector(data.getAsJsonArray("post")).div(divider);
                type = AnimationKeyframe.Interpolation.CUBIC;
            } else {
                vector = parseVector(object.get(key).getAsJsonArray()).div(divider);
                type = AnimationKeyframe.Interpolation.LINEAR;
            }

            map.put(time, new Pair<>(vector, type));
        }

        for (Map.Entry<Float, Pair<Vector3f, AnimationKeyframe.Interpolation>> current : map.entrySet()) {
            Float currentTime = current.getKey();
            Vector3f currentVector = current.getValue().getLeft();
            AnimationKeyframe.Interpolation currentType = current.getValue().getRight();
            Map.Entry<Float, Pair<Vector3f, AnimationKeyframe.Interpolation>> nextEntry = map.higherEntry(currentTime);

            if (nextEntry != null) {
                Float nextTime = nextEntry.getKey();
                Vector3f nextVector = nextEntry.getValue().getLeft();

                LOCK.lock();
                try {
                    AnimationKeyframe<Vector3f> frame = new AnimationKeyframe<>((nextTime - currentTime) * 20, currentType, new AnimationKeyframe.InterpolatedVector3f(currentVector, nextVector));
                    list.add(frame);
                } finally {
                    LOCK.unlock();
                }
            }
        }

        return new KeyframeTracker<>(list);
    }

    private static Vector3f parseVector(JsonArray json) {
        return new Vector3f(
                json.get(0).getAsFloat(),
                json.get(1).getAsFloat(),
                json.get(2).getAsFloat()
        );
    }
}

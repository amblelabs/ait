package dev.amble.ait.core.tardis.animation.v2.blockbench;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
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


// TODO - replace this with the better BedrockAnimation stuff when i can be bothered.
public class BlockbenchParser implements
        SimpleSynchronousResourceReloadListener {
    private static final Identifier SYNC = AITMod.id("blockbench_sync");

    private final HashMap<Identifier, Result> tardisAnimations = new HashMap<>();
    private final ConcurrentHashMap<String, List<JsonObject>> tardisAnimationsRaw = new ConcurrentHashMap<>();
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

        buf.writeInt(this.tardisAnimationsRaw.size());
        for (Map.Entry<String, List<JsonObject>> entry : this.tardisAnimationsRaw.entrySet()) {
            buf.writeString(entry.getKey());

            buf.writeInt(entry.getValue().size());
            for (JsonObject json : entry.getValue()) {
                buf.writeString(json.toString());
            }
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
        this.tardisAnimationsRaw.clear();
        this.tardisAnimations.clear();

        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            String namespace = buf.readString();

            int jsonSize = buf.readInt();
            List<JsonObject> jsons = new ArrayList<>();

            for (int j = 0; j < jsonSize; j++) {
                String jsonString = buf.readString();
                JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
                jsons.add(json);
            }

            this.tardisAnimationsRaw.put(namespace, jsons);
        }

        this.parseRawLookup();

        AITMod.LOGGER.info("Received {} blockbench animation files", this.tardisAnimationsRaw.size());
    }

    @Override
    public Identifier getFabricId() {
        return AITMod.id("blockbench_parser");
    }

    @Override
    public void reload(ResourceManager manager) {
        this.tardisAnimationsRaw.clear();
        this.tardisAnimations.clear();

        for (Identifier id : manager
                .findResources("fx/animation/keyframes", filename -> filename.getPath().endsWith("animation.json")).keySet()) {
            try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                parseAndStore(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject(), id.getNamespace());
                AmbleKit.LOGGER.info("Loaded blockbench file {}", id);
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
        Result result = getInstance().tardisAnimations.get(id);

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
            return getInstance().tardisAnimations.values().iterator().next();
        }
    }

    private void parseRawLookup() {
        this.tardisAnimations.clear();

        for (Map.Entry<String, List<JsonObject>> entry : this.tardisAnimationsRaw.entrySet()) {
            String namespace = entry.getKey();

            List<JsonObject> animations = entry.getValue();

            for (JsonObject json : animations) {
                HashMap<Identifier, Result> map = parse(json, namespace);
                this.tardisAnimations.putAll(map);
            }
        }
    }

    private void parseAndStore(JsonObject json, String namespace) {
        // store in raw lookup
        // namespace -> raw json source
        this.tardisAnimationsRaw.computeIfAbsent(namespace, k -> new ArrayList<>());
        this.tardisAnimationsRaw.get(namespace).add(json);

        // parse and store in lookup
        HashMap<Identifier, Result> map = parse(json, namespace);
        this.tardisAnimations.putAll(map);
    }

    public static HashMap<Identifier, Result> parse(JsonObject json, String namespace) {
        // get animations
        JsonObject animations = json.getAsJsonObject("animations");

        HashMap<Identifier, Result> map = new HashMap<>();

        for (String key : animations.keySet()) {
            JsonObject anim = animations.getAsJsonObject(key);
            Identifier id = Identifier.of(namespace, key);

            Result result = parseAnimation(anim);
            map.put(id, result);
        }

        return map;
    }

    private static Result parseAnimation(JsonObject anim) {
        JsonObject bones = anim.getAsJsonObject("bones");

        return parseTracker(bones.getAsJsonObject(bones.keySet().iterator().next()), anim.getAsJsonObject("timeline"));
    }

    private static Result parseTracker(JsonObject main, JsonObject timeline) {
        KeyframeTracker<Vector3f> rotation = parseVectorKeyframe(main.get("rotation"), 1f, new Vector3f(0f, 0f, 0f));
        KeyframeTracker<Vector3f> translation = parseVectorKeyframe(main.get("position"), 16f, new Vector3f(0f, 0f, 0f));
        KeyframeTracker<Vector3f> scale = parseVectorKeyframe(main.get("scale"), 1f, new Vector3f(1f, 1f, 1f));
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
            } else {
                if (!list.isEmpty()) continue;

                AnimationKeyframe<Float> frame = new AnimationKeyframe<>(20, AnimationKeyframe.Interpolation.CUBIC, new AnimationKeyframe.InterpolatedFloat(currentAlpha, currentAlpha));
                list.add(frame);
            }
        }

        return new KeyframeTracker<>(list);
    }

    private static KeyframeTracker<Vector3f> parseVectorKeyframe(JsonElement element, float divider, Vector3f fallback) {
        List<AnimationKeyframe<Vector3f>> list = new ArrayList<>();

        if (element == null) {
            list.add(new AnimationKeyframe<>(20, AnimationKeyframe.Interpolation.LINEAR, new AnimationKeyframe.InterpolatedVector3f(fallback, fallback)));

            return new KeyframeTracker<>(list);
        }

        if (element.isJsonArray()) {
            Vector3f vec = parseVector(element.getAsJsonArray());
            list.add(new AnimationKeyframe<>(20, AnimationKeyframe.Interpolation.LINEAR, new AnimationKeyframe.InterpolatedVector3f(vec, vec)));
            return new KeyframeTracker<>(list);
        }

        if (element.isJsonPrimitive()) {
            Vector3f vec = new Vector3f(element.getAsJsonPrimitive().getAsFloat());
            list.add(new AnimationKeyframe<>(20, AnimationKeyframe.Interpolation.LINEAR, new AnimationKeyframe.InterpolatedVector3f(vec, vec)));
            return new KeyframeTracker<>(list);
        }

        JsonObject object = element.getAsJsonObject();

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

                AnimationKeyframe<Vector3f> frame = new AnimationKeyframe<>((nextTime - currentTime) * 20, currentType, new AnimationKeyframe.InterpolatedVector3f(currentVector, nextVector));
                list.add(frame);
            } else {
                if (!list.isEmpty()) continue;

                AnimationKeyframe<Vector3f> frame = new AnimationKeyframe<>(20, currentType, new AnimationKeyframe.InterpolatedVector3f(currentVector, currentVector));
                list.add(frame);
            }
        }

        return new KeyframeTracker<>(list);
    }

    private static Vector3f parseVector(JsonArray json) {
        return new Vector3f(
            parseFloat(json.get(0)),
            parseFloat(json.get(1)),
            parseFloat(json.get(2))
        );
    }

    private static float parseFloat(JsonElement element) {
        // they could be math equations
        try {
            return element.getAsFloat();
        } catch (NumberFormatException ignored) {
        }

        try {
            return parseMath(element.getAsString());
        } catch (Exception e) {
            AITMod.LOGGER.error("Error occurred while parsing float {}", element);
            return 0;
        }
    }

    public static float parseMath(String data) {
        // parses math expressions like "1 + 2 * 3" or "1 - 2 / 3"
        // using net.objecthunter.exp4j
        Expression expression = new ExpressionBuilder(data).build();
        double result = expression.evaluate();
        return (float) result;
    }
}

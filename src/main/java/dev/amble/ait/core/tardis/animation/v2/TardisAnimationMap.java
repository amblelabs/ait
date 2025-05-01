package dev.amble.ait.core.tardis.animation.v2;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

import com.google.gson.*;

import net.minecraft.util.Identifier;

import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.animation.v2.datapack.TardisAnimationRegistry;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.enummap.EnumMap;

public class TardisAnimationMap extends EnumMap.Compliant<TravelHandlerBase.State, TardisAnimation> {
    public TardisAnimationMap() {
        super(TravelHandlerBase.State::values, TardisAnimationMap::createArray);
    }

    private static TardisAnimation[] createArray(int length) {
        TardisAnimation[] array = new TardisAnimation[length];

        TardisAnimation fallback = TardisAnimationRegistry.getInstance().fallback();
        Arrays.fill(array, fallback.instantiate());

        return array;
    }

    public TardisAnimationMap of(TravelHandlerBase.State state, TardisAnimation sound) {
        this.put(state, sound);
        return this;
    }

    public TardisAnimationMap of(TravelHandlerBase.State state, Identifier id) {
        return this.of(state, TardisAnimationRegistry.getInstance().instantiate(id));
    }

    public static TardisAnimationMap forTardis(Tardis tardis) {
        TardisAnimationMap map = new TardisAnimationMap();

        for (TravelHandlerBase.State state : TravelHandlerBase.State.values()) {
            map.of(state, tardis.travel().getAnimationIdFor(state));
        }

        return map;
    }

    public static Object serializer() {
        return new Serializer();
    }

    public static class Serializer implements JsonSerializer<TardisAnimationMap>, JsonDeserializer<TardisAnimationMap> {

        @Override
        public TardisAnimationMap deserialize(JsonElement element, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Map<String, JsonElement> map = element.getAsJsonObject().asMap();

            TardisAnimationMap result = new TardisAnimationMap();

            for (Map.Entry<String, JsonElement> entry : map.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();

                TravelHandlerBase.State state = TravelHandlerBase.State.valueOf(key);
                TardisAnimation animation = jsonDeserializationContext.deserialize(value, TardisAnimation.class);

                result.put(state, animation);
            }

            return result;
        }

        @Override
        public JsonElement serialize(TardisAnimationMap tardisAnimationMap, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result = new JsonObject();

            tardisAnimationMap.forEach((state, animation) -> {
                if (animation == null) return;

                JsonElement element = jsonSerializationContext.serialize(animation);
                result.add(state.name(), element);
            });

            return result;
        }
    }
}

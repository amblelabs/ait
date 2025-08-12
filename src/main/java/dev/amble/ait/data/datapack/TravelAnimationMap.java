package dev.amble.ait.data.datapack;

import java.util.EnumMap;
import java.util.Map;

import com.mojang.serialization.Codec;
import dev.amble.lib.client.bedrock.BedrockAnimation;
import dev.amble.lib.client.bedrock.BedrockAnimationReference;

import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

public class TravelAnimationMap extends EnumMap<TravelHandlerBase.State, BedrockAnimationReference> {
    public static Codec<TravelAnimationMap> CODEC = Codec.unboundedMap(TravelHandlerBase.State.CODEC, BedrockAnimationReference.CODEC)
            .xmap(TravelAnimationMap::new, map -> map);

    public TravelAnimationMap() {
        super(TravelHandlerBase.State.class);
    }

    public TravelAnimationMap(Map<TravelHandlerBase.State, BedrockAnimationReference> map) {
        this();

        this.putAll(map);
    }

    public BedrockAnimation getAnimation(TravelHandlerBase.State state) {
        BedrockAnimationReference ref = this.get(state);
        if (ref == null) {
            return null; // No animation registered for this state
        }

        return ref.get().orElse(null);
    }

    public interface Holder {
        TravelAnimationMap getAnimations();
    }
}

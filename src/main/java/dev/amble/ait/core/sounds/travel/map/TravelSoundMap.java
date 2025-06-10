package dev.amble.ait.core.sounds.travel.map;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.sounds.travel.TravelSound;
import dev.amble.ait.core.sounds.travel.TravelSoundRegistry;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.enummap.EnumMap;

@Deprecated(since = "1.3.0")
public class TravelSoundMap extends EnumMap.Compliant<TravelHandlerBase.State, TravelSound> {

    public static Codec<TravelSoundMap> CODEC = Identifier.CODEC.listOf().flatXmap((l) -> {
        TravelSoundMap map = new TravelSoundMap();
        for (Identifier id : l) {
            TravelSound sound = TravelSoundRegistry.getInstance().get(id);
            if (sound == null) {
                return DataResult.error(() -> "Unknown travel sound: " + id);
            }

            map.put(sound.target(), sound);
        }
        return DataResult.success(map);
    }, (map) -> DataResult.success(map.values().stream().map(TravelSound::id).toList()));

    public TravelSoundMap() {
        super(TravelHandlerBase.State::values, TravelSoundMap::createArray);

        this.put(TravelHandlerBase.State.DEMAT, TravelSoundRegistry.DEFAULT_DEMAT);
        this.put(TravelHandlerBase.State.MAT, TravelSoundRegistry.DEFAULT_MAT);
    }

    private static TravelSound[] createArray(int length) {
        TravelSound[] array = new TravelSound[length];

        Arrays.fill(array, TravelSoundRegistry.EMPTY);

        return array;
    }

    public TravelSoundMap of(TravelHandlerBase.State state, TravelSound sound) {
        this.put(state, sound);
        return this;
    }

    public static TravelSoundMap fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static TravelSoundMap fromJson(JsonObject json) {
        AtomicReference<TravelSoundMap> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(var -> created.set(var.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack travel sfx map: {}", err);
        });

        return created.get();
    }
}

package dev.amble.ait.data.datapack.exterior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.ait.core.tardis.handler.BiomeHandler;
import dev.amble.ait.data.enummap.EnumMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

import java.util.Map;
import java.util.function.Function;

public record BiomeOverrides(EnumMap.Compliant<BiomeHandler.BiomeType, Identifier> lookup) {

    public static BiomeOverrides EMPTY = new BiomeOverrides(createMap());

    private static EnumMap.Compliant<BiomeHandler.BiomeType, Identifier> createMap() {
        return new EnumMap.Compliant<>(() -> BiomeHandler.BiomeType.VALUES, Identifier[]::new);
    }

    private BiomeOverrides(Map<BiomeHandler.BiomeType, Identifier> map) {
        this(createMap());
        this.lookup.putAll(map);
    }

    public Identifier get(BiomeHandler.BiomeType type) {
        return this.lookup.get(type);
    }

    @Environment(EnvType.CLIENT)
    public void validate() {
        ResourceManager manager = MinecraftClient.getInstance().getResourceManager();

        this.lookup.map(id -> {
            if (id == null)
                return null;

            return manager.getResource(id).isPresent() ? id : null;
        });
    }

    public static BiomeOverrides of(Function<BiomeHandler.BiomeType, Identifier> func) {
        EnumMap.Compliant<BiomeHandler.BiomeType, Identifier> map = createMap();

        for (BiomeHandler.BiomeType type : BiomeHandler.BiomeType.VALUES) {
            map.put(type, func.apply(type));
        }

        return new BiomeOverrides(map);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(BiomeOverrides overrides) {
        return new Builder(overrides);
    }

    public static final MapCodec<BiomeOverrides> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.simpleMap(
                            BiomeHandler.BiomeType.CODEC, Identifier.CODEC,
                            StringIdentifiable.toKeyable(BiomeHandler.BiomeType.VALUES)
                    ).forGetter(overrides -> overrides.lookup)
            ).apply(instance, BiomeOverrides::new));

    public static class Builder {

        private final EnumMap.Compliant<BiomeHandler.BiomeType, Identifier> map = createMap();

        private Builder() { }

        private Builder(BiomeOverrides overrides) {
            for (BiomeHandler.BiomeType type : BiomeHandler.BiomeType.VALUES) {
                this.with(type, overrides.lookup().get(type));
            }
        }

        public Builder with(BiomeHandler.BiomeType type, Identifier id) {
            map.put(type, id);
            return this;
        }

        public Builder with(Function<BiomeHandler.BiomeType, Identifier> func, BiomeHandler.BiomeType... types) {
            for (BiomeHandler.BiomeType type : types) {
                this.with(type, func.apply(type));
            }

            return this;
        }

        public BiomeOverrides build() {
            return new BiomeOverrides(map);
        }
    }
}

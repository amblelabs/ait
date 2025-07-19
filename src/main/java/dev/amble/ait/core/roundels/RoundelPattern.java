package dev.amble.ait.core.roundels;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.lib.api.Identifiable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import dev.amble.ait.AITMod;

public record RoundelPattern(Identifier id, Identifier texture, boolean usesDynamicTexture) implements Identifiable {
    public static final Codec<RoundelPattern> CODEC = Codecs.exceptionCatching(RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(RoundelPattern::id),
                    Identifier.CODEC.fieldOf("texture").forGetter(RoundelPattern::texture),
                    Codec.BOOL.optionalFieldOf("uses_dynamic_texture", false).forGetter(RoundelPattern::usesDynamicTexture))
            .apply(instance, RoundelPattern::new)));

    public static RoundelPattern fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static RoundelPattern fromJson(JsonObject json) {
        AtomicReference<RoundelPattern> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(var -> created.set(var.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack roundel pattern: {}", err);
        });

        return created.get();
    }

    @Override
    public Identifier id() {
        return id;
    }

    @Override
    public Identifier texture() {
        return texture;
    }

    @Override
    public boolean usesDynamicTexture() {
        return usesDynamicTexture;
    }

    public static class Patterns {
        private final List<RoundelType> entries = Lists.newArrayList();

        public Patterns add(RoundelPattern pattern, int color) {
            return this.add(new RoundelType(pattern, color, true));
        }

        public Patterns add(RoundelType pattern) {
            this.entries.add(pattern);
            return this;
        }

        public NbtList toNbt() {
            NbtList nbtList = new NbtList();
            for (RoundelType type : this.entries) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putString("Pattern", type.pattern().id.toString());
                nbtCompound.putInt("Color", type.color());
                nbtCompound.putBoolean("Emissive", true);
                nbtList.add(nbtCompound);
            }
            return nbtList;
        }
    }
}

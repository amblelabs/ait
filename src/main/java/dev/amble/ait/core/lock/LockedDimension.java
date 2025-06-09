package dev.amble.ait.core.lock;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.ait.AITMod;
import dev.amble.ait.api.Nameable;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.data.Loyalty;
import dev.amble.lib.register.unlockable.Unlockable;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public record LockedDimension(Identifier dimension, ItemStack stack) implements Unlockable, Nameable {
    public static final Codec<LockedDimension> CODEC = Codecs.exceptionCatching(RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("dimension").forGetter(LockedDimension::dimension),
            ItemStack.CODEC.fieldOf("stack").forGetter(LockedDimension::stack))
            .apply(instance, LockedDimension::new)));

    @Override
    public Identifier id() {
        return this.dimension();
    }

    public static LockedDimension fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static LockedDimension fromJson(JsonObject json) {
        AtomicReference<LockedDimension> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(planet -> created.set(planet.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack locked dim: {}", err);
        });

        return created.get();
    }

    @Override
    public UnlockType unlockType() {
        return UnlockType.DIMENSION;
    }

    @Override
    public Optional<Loyalty> requirement() {
        return Optional.of(Loyalty.fromLevel(100));
    }

    @Override
    public String name() {
        return this.text().getString();
    }

    @Override
    public Text text() {
        return WorldUtil.worldText(RegistryKey.of(RegistryKeys.WORLD, this.dimension()), false);
    }
}

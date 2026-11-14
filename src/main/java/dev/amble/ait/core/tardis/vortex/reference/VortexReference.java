package dev.amble.ait.core.tardis.vortex.reference;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.ait.AITMod;
import dev.amble.ait.api.Nameable;
import dev.amble.ait.client.renderers.VortexRender;
import dev.amble.ait.data.Loyalty;
import dev.amble.lib.register.unlockable.Unlockable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public record VortexReference(Identifier id, Identifier texture, String name, Optional<Loyalty> loyalty) implements Unlockable, Nameable {
    public static final Codec<VortexReference> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(VortexReference::id),
            Identifier.CODEC.fieldOf("texture").forGetter(VortexReference::texture),
            Codec.STRING.optionalFieldOf("name", "").forGetter(VortexReference::name),
            Loyalty.CODEC.optionalFieldOf("loyalty").forGetter(VortexReference::loyalty)
    ).apply(instance, VortexReference::new));

    public VortexReference {
        if (name.isEmpty()) {
            name = id.getPath();
        }
    }

    public VortexReference(Identifier id, Identifier texture) {
        this(id, texture, id.getPath(), Optional.empty());
    }

    @Override
    public UnlockType unlockType() {
        return UnlockType.VORTEX;
    }

    @Override
    public Optional<Loyalty> requirement() {
        return this.loyalty;
    }

    @Override
    public Text text() {
        return Text.translatableWithFallback(this.id().toTranslationKey("vortex"), this.name());
    }

    @Environment(EnvType.CLIENT)
    public VortexRender toRender() {
        return VortexRender.getInstance(this);
    }

    public static VortexReference fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static VortexReference fromJson(JsonObject json) {
        AtomicReference<VortexReference> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(planet -> created.set(planet.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack vortex: {}", err);
        });

        return created.get();
    }
}

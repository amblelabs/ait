package dev.amble.ait.core.tardis.animation.v2.datapack;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.animation.v2.TardisAnimation;
import dev.amble.ait.core.tardis.animation.v2.blockbench.BlockbenchParser;
import dev.amble.ait.core.tardis.animation.v2.keyframe.KeyframeTracker;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

public class DatapackAnimation extends TardisAnimation {
    public static final Codec<TardisAnimation> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    Identifier.CODEC.fieldOf("id").forGetter(TardisAnimation::id),
                    Identifier.CODEC.optionalFieldOf("blockbench_file", new Identifier("")).forGetter(TardisAnimation::getBlockbenchId),
                    TravelHandlerBase.State.CODEC.fieldOf("expected_state").forGetter(TardisAnimation::getExpectedState),
                    Codec.STRING.optionalFieldOf("name", "").forGetter(TardisAnimation::name),
                    Identifier.CODEC.optionalFieldOf("sound", new Identifier("")).forGetter(TardisAnimation::getSoundId)
    ).apply(instance, DatapackAnimation::new));

    private final TravelHandlerBase.State expectedState;
    private final String name;
    private final Identifier blockbenchId;

    protected DatapackAnimation(Identifier id, Identifier blockbench, TravelHandlerBase.State expectedState, String optName, @Nullable Identifier sound) {
        super(id, sound, BlockbenchParser.getOrFallback((blockbench.equals(new Identifier(""))) ? id : blockbench));

        this.blockbenchId = blockbench;

        this.expectedState = expectedState;

        if (optName.isBlank()) {
            optName = id.getPath();
        }

        this.name = optName;
    }

    protected DatapackAnimation(Identifier id, KeyframeTracker<Float> alpha, KeyframeTracker<Vector3f> scale, KeyframeTracker<Vector3f> position, KeyframeTracker<Vector3f> rotation, Identifier blockbench, TravelHandlerBase.State expectedState, String optName, @Nullable Identifier soundId) {
        super(id, soundId, alpha, scale, position, rotation);

        this.blockbenchId = blockbench;

        this.expectedState = expectedState;

        if (optName.isBlank()) {
            optName = id.getPath();
        }

        this.name = optName;
    }

    @Override
    public Identifier getBlockbenchId() {
        return blockbenchId;
    }

    @Override
    public TravelHandlerBase.State getExpectedState() {
        return this.expectedState;
    }

    @Override
    public DatapackAnimation instantiate() {
        return new DatapackAnimation(this.id(), this.alpha.instantiate(), this.scale.instantiate(), this.position.instantiate(), this.rotation.instantiate(), this.blockbenchId, this.expectedState, this.name, this.getSound().getId());
    }

    public static DatapackAnimation fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static DatapackAnimation fromJson(JsonObject json) {
        AtomicReference<DatapackAnimation> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(var -> {
            created.set((DatapackAnimation) var.getFirst());
        }).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack animation: {}", err);
        });

        return created.get();
    }

    @Override
    public String name() {
        return name;
    }
}

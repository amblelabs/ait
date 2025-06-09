package dev.amble.ait.core.tardis.animation.v2.datapack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.animation.v2.TardisAnimation;
import dev.amble.ait.core.tardis.animation.v2.blockbench.BlockbenchParser;
import dev.amble.ait.core.tardis.animation.v2.keyframe.KeyframeTracker;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class DatapackAnimation extends TardisAnimation {
    public static final Codec<TardisAnimation> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    Identifier.CODEC.fieldOf("id").forGetter(TardisAnimation::id),
                    Identifier.CODEC.optionalFieldOf("blockbench_file").forGetter(TardisAnimation::getBlockbenchId),
                    TravelHandlerBase.State.CODEC.fieldOf("expected_state").forGetter(TardisAnimation::getExpectedState),
                    Identifier.CODEC.optionalFieldOf("sound").forGetter(TardisAnimation::getSoundId)
    ).apply(instance, DatapackAnimation::new));

    private final TravelHandlerBase.State expectedState;
    private final String nameKey;
    @Nullable private final Identifier blockbenchId;

    protected DatapackAnimation(Identifier id, Optional<Identifier> blockbench, TravelHandlerBase.State expectedState, Optional<Identifier> sound) {
        super(id, sound.orElse(null), BlockbenchParser.getOrFallback(blockbench.orElse(id)));

        this.blockbenchId = blockbench.orElse(null);
        this.expectedState = expectedState;
        this.nameKey = id.toTranslationKey("animation");
    }

    protected DatapackAnimation(Identifier id, KeyframeTracker<Float> alpha, KeyframeTracker<Vector3f> scale, KeyframeTracker<Vector3f> position, KeyframeTracker<Vector3f> rotation, Identifier blockbench, TravelHandlerBase.State expectedState, String optName, @Nullable Identifier soundId) {
        super(id, soundId, alpha, scale, position, rotation);

        this.blockbenchId = blockbench;
        this.expectedState = expectedState;
        this.nameKey = id.toTranslationKey("animation");
    }

    @Override
    public Optional<Identifier> getBlockbenchId() {
        return Optional.ofNullable(this.blockbenchId);
    }

    @Override
    public TravelHandlerBase.State getExpectedState() {
        return this.expectedState;
    }

    @Override
    public DatapackAnimation instantiate() {
        return new DatapackAnimation(this.id(), this.alpha.instantiate(), this.scale.instantiate(), this.position.instantiate(), this.rotation.instantiate(), this.blockbenchId, this.expectedState, this.nameKey, this.getSound().getId());
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
        return Text.translatable(this.nameKey).getString();
    }
}

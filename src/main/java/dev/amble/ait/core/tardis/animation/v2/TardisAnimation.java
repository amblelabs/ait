package dev.amble.ait.core.tardis.animation.v2;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.UUID;

import com.google.gson.*;
import dev.amble.lib.api.Identifiable;
import dev.amble.lib.util.ServerLifecycleHooks;
import dev.drtheo.queue.api.ActionQueue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.Nameable;
import dev.amble.ait.api.tardis.Disposable;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisManager;
import dev.amble.ait.core.tardis.animation.v2.blockbench.BlockbenchParser;
import dev.amble.ait.core.tardis.animation.v2.datapack.TardisAnimationRegistry;
import dev.amble.ait.core.tardis.animation.v2.keyframe.KeyframeTracker;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.Exclude;

/**
 * Represents an exterior animation for the TARDIS.
 * If you got this from the registry, call {@link TardisAnimation#instantiate()} as to not cause issues.
 */
public abstract class TardisAnimation implements TardisTickable, Disposable, Identifiable, Linkable, Nameable {
    private final Identifier id;
    @Nullable private Identifier soundId;

    private TardisRef ref;
    private boolean isServer = true;

    protected final KeyframeTracker<Float> alpha;
    protected final KeyframeTracker<Vector3f> scale;
    protected final KeyframeTracker<Vector3f> position;
    protected final KeyframeTracker<Vector3f> rotation;

    @Exclude
    private ActionQueue doneQueue;

    protected TardisAnimation(Identifier id, @Nullable Identifier soundId, KeyframeTracker<Float> alpha, KeyframeTracker<Vector3f> scale, KeyframeTracker<Vector3f> position, KeyframeTracker<Vector3f> rotation) {
        this.id = id;
        this.soundId = soundId;

        this.alpha = alpha;
        this.scale = scale;
        this.position = position;
        this.rotation = rotation;
    }

    protected TardisAnimation(Identifier id, @Nullable Identifier soundId, BlockbenchParser.Result result) {
        this(id, soundId, result.alpha().instantiate(), result.scale().instantiate(), result.translation().instantiate(), result.rotation().instantiate());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void tick(MinecraftClient client) {
        this.tickCommon();

        this.alpha.tick(client);
        this.scale.tick(client);
        this.position.tick(client);
        this.rotation.tick(client);

        this.isServer = false;
    }

    @Override
    public void tick(MinecraftServer server) {
        this.tickCommon();
        if (!this.isLinked()) return;

        Tardis tardis = this.tardis().get();

        boolean playSound = this.tryStart(this.alpha, 1f);
        playSound = playSound && this.tryStart(this.scale, new Vector3f(1f));
        playSound = playSound && this.tryStart(this.position, new Vector3f());
        playSound = playSound && this.tryStart(this.rotation, new Vector3f());

        if (playSound) {
            tardis.getExterior().playSound(this.getSoundIdOrDefault(), SoundCategory.BLOCKS);
        }

        this.alpha.tick(server);
        this.scale.tick(server);
        this.position.tick(server);
        this.rotation.tick(server);

        this.isServer = true;
    }

    protected void tickCommon() {
        if (this.isAged() && this.doneQueue != null) {
            this.doneQueue.execute();
            this.doneQueue = null;
        }
    }

    public ActionQueue onDone() {
        if (this.doneQueue == null) {
            this.doneQueue = new ActionQueue();
        }

        return this.doneQueue;
    }

    protected <T> boolean tryStart(KeyframeTracker<T> tracker, T startVal) {
        if (tracker.getCurrent().ticks() != 0) return false;
        if (!tracker.isStarting()) return false;

        tracker.start(startVal);

        return true;
    }

    /**
     * Gets the sound event for this animation.
     * If the sound event is not found, it will log an error and return a default error sound.
     * @return the sound event
     */
    public SoundEvent getSound() {
        SoundEvent sfx = null;

        if (soundId != null) {
            sfx = Registries.SOUND_EVENT.get(this.soundId);
        }

        if (sfx == null) {
            AITMod.LOGGER.error("Unknown sound event: {} in tardis animation {}", this.soundId, this.id());
            sfx = AITSounds.ERROR;
        }

        return sfx;
    }

    public Optional<Identifier> getBlockbenchId() {
        return Optional.of(this.id());
    }

    public Optional<Identifier> getSoundId() {
        return Optional.ofNullable(this.soundId);
    }

    public Identifier getSoundIdOrDefault() {
        return this.getSoundId().orElse(AITSounds.ERROR.getId());
    }

    @Override
    public void dispose() {
        this.alpha.dispose();
        this.scale.dispose();
        this.position.dispose();
        this.rotation.dispose();
    }

    @Override
    public boolean isAged() {
        return this.alpha.isAged() && this.scale.isAged() && this.position.isAged() && this.rotation.isAged();
    }

    @Override
    public void age() {
        this.alpha.age();
        this.scale.age();
        this.position.age();
        this.rotation.age();
    }

    @Override
    public Identifier id() {
        return this.id;
    }

    public float getAlpha(float delta) {
        return this.alpha.getValue(delta);
    }

    public Vector3f getScale(float delta) {
        Vector3f scale = this.scale.getValue(delta);
        if (!this.isLinked()) return scale;

        return scale.mul(this.tardis().get().stats().getScale()); // relative scaling
    }

    public Vector3f getPosition(float delta) {
        return this.position.getValue(delta);
    }

    public Vector3f getRotation(float delta) {
        return this.rotation.getValue(delta);
    }

    @Override
    public void link(UUID uuid) {
        this.ref = new TardisRef(uuid, real -> TardisManager.with(!this.isServer, (o, manager) -> manager.demandTardis(o, real), ServerLifecycleHooks::get));
    }

    @Override
    public void link(Tardis tardis) {
        this.ref = new TardisRef(tardis, real -> TardisManager.with(!this.isServer, (o, manager) -> manager.demandTardis(o, real), ServerLifecycleHooks::get));
    }

    @Override
    public TardisRef tardis() {
        return this.ref;
    }

    public boolean matches(TardisAnimation anim) {
        return this.id().equals(anim.id());
    }

    /**
     * The state the TARDIS is assumed to be in when this animation is played.
     * This is used for the selection screen.
     * @return state
     */
    public abstract TravelHandlerBase.State getExpectedState();

    /**
     * Creates a new instance of this animation
     * @return a new instance
     */
    public abstract TardisAnimation instantiate();

    public static Object serializer() {
        return new Serializer();
    }

    public static class Serializer implements JsonSerializer<TardisAnimation>, JsonDeserializer<TardisAnimation> {

        @Override
        public TardisAnimation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return TardisAnimationRegistry.getInstance().instantiate(jsonDeserializationContext.deserialize(jsonElement, Identifier.class));
        }

        @Override
        public JsonElement serialize(TardisAnimation tardisAnimation, Type type, JsonSerializationContext jsonSerializationContext) {
            return jsonSerializationContext.serialize(tardisAnimation.id());
        }
    }
}

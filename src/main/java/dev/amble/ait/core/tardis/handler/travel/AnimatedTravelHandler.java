package dev.amble.ait.core.tardis.handler.travel;

import java.util.UUID;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import dev.amble.ait.client.tardis.manager.ClientTardisManager;
import dev.amble.ait.core.tardis.animation.v2.AnimationHolder;
import dev.amble.ait.core.tardis.animation.v2.TardisAnimation;
import dev.amble.ait.core.tardis.animation.v2.TardisAnimationMap;
import dev.amble.ait.core.tardis.animation.v2.datapack.TardisAnimationRegistry;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;

public abstract class AnimatedTravelHandler extends ProgressiveTravelHandler {
    private static final Property<Identifier> DEMAT_FX = new Property<>(Property.IDENTIFIER, "demat_fx", TardisAnimationRegistry.DEFAULT_DEMAT);
    private static final Property<Identifier> MAT_FX = new Property<>(Property.IDENTIFIER, "mat_fx", TardisAnimationRegistry.DEFAULT_MAT);
    private final Value<Identifier> dematId = DEMAT_FX.create(this);
    private final Value<Identifier> matId = MAT_FX.create(this);

    @Exclude
    private boolean isAnimationInvalidated;

    static {
        if (EnvType.CLIENT == FabricLoader.getInstance().getEnvironmentType()) initClient();
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(AnimationHolder.UPDATE_PACKET, (client, handler, buf, responseSender) -> {
            State state = buf.readEnumConstant(State.class);
            Identifier id = buf.readIdentifier();
            UUID uuid = buf.readUuid();

            ClientTardisManager.getInstance().getTardis(uuid, tardis -> {
                if (state == State.LANDED) {
                    tardis.travel().setTemporaryAnimation(id);
                    return;
                }

                tardis.travel().getAnimations().onStateChange(state);
            });
        });
    }

    @Exclude
    private AnimationHolder animations;

    public AnimatedTravelHandler(Id id) {
        super(id);
    }

    @Override
    public void postInit(InitContext ctx) {
        super.postInit(ctx);

        // This is necessary, because we need to invalidate on server when the client changes the animation, theres no other way to do this. - duzo
        this.dematId.addListener((id) -> {
            this.invalidateAnimations();
        });

        this.matId.addListener((id) -> {
            this.invalidateAnimations();
        });
    }

    @Override
    public void onLoaded() {
        super.onLoaded();

        dematId.of(this, DEMAT_FX);
        matId.of(this, MAT_FX);
    }

    @Override
    public void tick(MinecraftServer server) {
        super.tick(server);

        State state = this.getState();

        if (this.shouldTickAnimation()) {
            this.tickAnimationProgress(server, state);
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void tick(MinecraftClient client) {
        super.tick(client);

        if (this.shouldTickAnimation()) {
            this.getAnimations().tick(client);
        }
    }

    protected void tickAnimationProgress(MinecraftServer server, State state) {
        if (!this.getAnimations().isRunning()) {
            if (this.isAnimationInvalidated) {
                this.animations = null;
            }
        }

        this.getAnimations().tick(server);

        if (state == State.LANDED) return;

        if (!this.getAnimations().isAged()) return;

        if (this instanceof TravelHandler handler)
            state.finish(handler);
    }

    private void invalidateAnimations() {
        if (this.getState().animated()) {
            if (this.getState() == State.LANDED && !this.getAnimations().isRunning()) {
                this.animations = null;
                return;
            }

            this.isAnimationInvalidated = true;
            return;
        }

        this.animations = null;
    }

    public float getAlpha() {
        return this.getAlpha(0F);
    }

    public Vector3f getScale() {
        return this.getScale(0F);
    }

    public Vector3f getAnimationPosition() {
        return this.getAnimationPosition(0F);
    }

    public Vector3f getAnimationRotation() {
        return this.getAnimationRotation(0F);
    }


    public float getAlpha(float delta) {
        return this.getAnimations().getAlpha(delta);
    }

    public Vector3f getScale(float delta) {
        return this.getAnimations().getScale(delta);
    }

    public Vector3f getAnimationPosition(float delta) {
        return this.getAnimations().getPosition(delta);
    }

    public Vector3f getAnimationRotation(float delta) {
        return this.getAnimations().getRotation(delta);
    }

    public boolean isHitboxShown() {
        return this.getAlpha() > 0.5F && this.getScale().equals(1, 1, 1) && this.getAnimationPosition().equals(0, 0, 0) && this.getAnimationRotation().equals(0, 0, 0);
    }

    protected AnimationHolder getAnimations() {
        if (this.animations == null) {
            if (this.tardis() != null) { // Ask loqor how this happened, because I dont know.
                this.animations = new AnimationHolder(this.tardis());
            } else {
                this.animations = new AnimationHolder(new TardisAnimationMap());
            }
            this.animations.onStateChange(this.getState());
        }

        return this.animations;
    }

    @Nullable public Identifier getAnimationIdFor(State state) {
        return switch (state) {
            case LANDED, FLIGHT -> null;
            case DEMAT -> this.dematId.get();
            case MAT -> this.matId.get();
        };
    }

    protected TardisAnimation getAnimationFor(State state) {
        return TardisAnimationRegistry.getInstance().getOrFallback(this.getAnimationIdFor(state));
    }

    public void setAnimationFor(State state, Identifier id) {
        switch (state) {
            case DEMAT -> this.dematId.set(id);
            case MAT -> this.matId.set(id);
        }

        this.invalidateAnimations();
    }

    public boolean setTemporaryAnimation(Identifier animId) {
        TardisAnimation anim = TardisAnimationRegistry.getInstance().getOrFallback(animId);

        return this.getAnimations().setAnimation(anim);
    }

    @Override
    protected void setState(State state) {
        super.setState(state);

        this.getAnimations().onStateChange(state);
    }

    /**
     * Sets a runnable to be called when the CURRENT animation is complete.
     * This will not be ran on subsequent animations.
     * @param runnable code to run
     */
    public void onAnimationComplete(Runnable runnable) {
        this.getAnimations().onDone().thenRun(runnable);
    }

    public abstract boolean shouldTickAnimation();
}

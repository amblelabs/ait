package dev.amble.ait.core.tardis.animation.v2;

import java.util.UUID;

import dev.amble.lib.util.ServerLifecycleHooks;
import dev.drtheo.queue.api.ActionQueue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import org.joml.Math;
import org.joml.Vector3f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.Disposable;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.client.sounds.ClientSoundManager;
import dev.amble.ait.client.sounds.flight.FlightSoundPlayer;
import dev.amble.ait.core.effects.ZeitonHighEffect;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisManager;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.util.NetworkUtil;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.Loyalty;

public class AnimationHolder implements TardisTickable, Disposable, Linkable {
    public static final Identifier UPDATE_PACKET = AITMod.id("sync/ext_anim");

    protected final TardisAnimationMap map;
    private TardisAnimation current;
    private float alphaOverride = -1;
    public static final double MAX_CLOAK_DISTANCE = 5d;
    @Exclude
    private boolean isServer = true;
    private TardisRef ref;

    public AnimationHolder(TardisAnimationMap map) {
        this.map = map;
    }

    public AnimationHolder(Tardis tardis) {
        this(TardisAnimationMap.forTardis(tardis));

        this.link(tardis);
    }

    protected TardisAnimation getCurrent() {
        return this.current;
    }

    public boolean isRunning() {
        return this.current != null && !this.current.isAged();
    }

    public boolean setAnimation(TardisAnimation anim) {
        if (this.isLinked()) {
            if (anim.getExpectedState() != tardis().get().travel().getState()) {
                AITMod.LOGGER.error("Tried to force animation {} but the tardis is in state {} which is unexpected!", anim.id(), tardis().get().travel().getState());
                return false;
            }
        }

        Tardis tardis = this.tardis().get();

        if (this.current != null) {
            this.current.dispose();
        }

        this.current = anim.instantiate();
        this.alphaOverride = -1F;

        if (this.isLinked()) {
            this.current.link(tardis);
            this.sync(tardis.travel().getState());
        }

        return true;
    }

    /**
     * Allows you to enqueue things to be ran when the current animation is completed.
     * @return The action queue to run when the animation is done. Or null if there is no animation.
     */
    public ActionQueue onDone() {
        if (this.current == null) return null;

        return this.current.onDone();
    }

    @Override
    public void tick(MinecraftServer server) {
        if (this.current == null) return;

        this.current.tick(server);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void tick(MinecraftClient client) {
        this.isServer = false;

        if (this.current == null) return;

        this.current.tick(client);
    }

    @Override
    public boolean isAged() {
        if (this.current == null) return true;

        return this.current.isAged();
    }

    @Override
    public void age() {
        this.current.age();
    }

    @Override
    public void dispose() {
        this.current.dispose();
        this.alphaOverride = -1;
    }

    @Override
    public void link(UUID uuid) {
        this.ref = new TardisRef(uuid, real -> TardisManager.with(!this.isServer, (o, manager) -> manager.demandTardis(o, real), ServerLifecycleHooks::get));
    }

    @Override
    public void link(Tardis tardis) {
        this.ref = new TardisRef(tardis, real -> TardisManager.with(!this.isServer, (o, manager) -> manager.demandTardis(o, real), ServerLifecycleHooks::get));

        this.isServer = tardis instanceof ServerTardis;
    }

    @Override
    public TardisRef tardis() {
        return this.ref;
    }

    public void onStateChange(TravelHandlerBase.State state) {
        TardisAnimation animation = this.map.get(state);

        if (state == TravelHandlerBase.State.LANDED) {
            this.alphaOverride = 1f;
            return;
        } else if (state == TravelHandlerBase.State.FLIGHT) {
            this.alphaOverride = 0f;
            return;
        }

        if (animation == null) {
            switch (state) {
                case DEMAT:
                    this.alphaOverride = 1f;
                case MAT:
                    this.alphaOverride = 0f;
            }
            return;
        }

        this.alphaOverride = -1;

        if (this.current != null) {
            this.current.dispose();
        }

        animation.dispose();;
        this.current = animation.instantiate();

        if (this.isLinked()) {
            this.current.link(this.tardis().get());
        }

        this.sync(state);
    }

    public float getAlpha(float delta) {
        if (this.isLinked()) {
            Tardis tardis = this.tardis().get();
            if (tardis.cloak().cloaked().get())
                return cloakAlpha(tardis);

            if (tardis.travel().isLanded() && tardis.travel().handbrake() && tardis.travel().speed() > 0)
                return handbrakeAlpha(tardis);
        }

        if (this.alphaOverride != -1) {
            return this.alphaOverride;
        }

        if (this.current == null)
             return 1f;

        return this.current.getAlpha(delta);
    }

    private float cloakAlpha(Tardis tardis) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT)
            return 0f;

        return getCloakAlpha(tardis);
    }

    @Environment(EnvType.CLIENT)
    private float getCloakAlpha(Tardis tardis) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player == null)
            return 0f;

        double distance = distanceFromTardis(player, tardis);

        if (distance >= MAX_CLOAK_DISTANCE)
            return 0f;

        boolean companion = tardis.loyalty().get(player).isOf(Loyalty.Type.COMPANION);

        float distanceAlpha = 1f - (float) (distance / MAX_CLOAK_DISTANCE);
        float base = 1f;

        if (!companion)
            base = ZeitonHighEffect.isHigh(player) ? 0.105f : 0f;

        return distanceAlpha * base;
    }

    public static double distanceFromTardis(PlayerEntity player, Tardis tardis) {
        BlockPos pPos = player.getBlockPos();
        BlockPos tPos = tardis.travel().position().getPos();
        return Math.sqrt(tPos.getSquaredDistance(pPos));
    }

    /**
     * The alpha when the handbrake & throttle is down, making the groaning noise and such.
     */
    private float handbrakeAlpha(Tardis tardis) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT)
            return 0.5f;

        return getHandbrakeAlpha(tardis);
    }

    @Environment(EnvType.CLIENT)
    private float getHandbrakeAlpha(Tardis tardis) {
        FlightSoundPlayer sfx = ClientSoundManager.getFlight().getExteriorLoop(tardis.asClient());
        if (sfx == null) {
            return 0.5F;
        }

        float progress = sfx.getProgress();
        /*
            How this works
            0 -> 1
            0.5 -> 0.5
            1 -> 1
            COS function is used to create a smooth transition
         */
        return (float) (Math.cos(2 * (progress * Math.PI)) * 0.15f + 0.85f);
    }

    public Vector3f getScale(float delta) {
        if (this.current == null) {
            if (this.isLinked()) {
                return this.tardis().get().stats().getScale();
            }

            return new Vector3f(1f, 1f, 1f);
        }

        return this.current.getScale(delta);
    }

    public Vector3f getPosition(float delta) {
        if (this.current == null) {
            return new Vector3f(0f, 0f, 0f);
        }

        return this.current.getPosition(delta);
    }

    public Vector3f getRotation(float delta) {
        if (this.current == null) {
            return new Vector3f(0f, 0f, 0f);
        }

        return this.current.getRotation(delta);
    }

    private void sync(TravelHandlerBase.State state) {
        if (!ServerLifecycleHooks.isServer() || !this.isLinked() || !(this.tardis().get() instanceof ServerTardis)) return;

        ServerTardis tardis = this.tardis().get().asServer();

        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeEnumConstant(state);
        buf.writeIdentifier(this.current.id());
        buf.writeUuid(tardis.getUuid());

        NetworkUtil.getSubscribedPlayers(tardis).forEach(player -> {;
            NetworkUtil.send(player, UPDATE_PACKET, buf);
        });
    }
}

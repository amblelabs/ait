package dev.amble.ait.core.tardis.animation.v2;

import java.util.UUID;

import dev.amble.lib.util.ServerLifecycleHooks;
import dev.drtheo.queue.api.ActionQueue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import org.joml.Vector3f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.Disposable;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisManager;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.util.NetworkUtil;
import dev.amble.ait.data.Exclude;

public class AnimationHolder implements TardisTickable, Disposable, Linkable {
    public static final Identifier UPDATE_PACKET = AITMod.id("sync/ext_anim");

    protected final TardisAnimationMap map;
    private TardisAnimation current;
    private float alphaOverride = -1;
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

        if (this.current != null) {
            this.current.dispose();
        }

        this.current = anim.instantiate();
        return true;
    }

    /**
     * Allows you to enqueue things to be ran when the current animation is completed.
     * @return The action queue to run when the animation is done. Or null if there is no animation.
     */
    public ActionQueue onDone() {
        if (this.getCurrent() == null) return null;

        return this.getCurrent().onDone();
    }

    @Override
    public void tick(MinecraftServer server) {
        if (this.getCurrent() == null) return;

        this.getCurrent().tick(server);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void tick(MinecraftClient client) {
        this.isServer = false;

        if (this.getCurrent() == null) return;

        this.getCurrent().tick(client);
    }

    @Override
    public boolean isAged() {
        return this.getCurrent().isAged();
    }

    @Override
    public void age() {
        this.getCurrent().age();
    }

    @Override
    public void dispose() {
        this.getCurrent().dispose();
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

        if (this.getCurrent() != null) {
            this.getCurrent().dispose();
        }

        animation.dispose();;
        this.current = animation.instantiate();

        if (this.isLinked()) {
            this.getCurrent().link(this.tardis().get());
        }

        this.sync(state);
    }

    public float getAlpha(float delta) {
        if (this.alphaOverride != -1) {
            return this.alphaOverride;
        }

/*        if (!this.isServer && ServerLifecycleHooks.get().getTicks() % 20 == 0) {
            System.out.println(this);
            System.out.println(this.getCurrent());
        }*/

        if (this.getCurrent() == null)
             return 1f;

        return this.getCurrent().getAlpha(delta);
    }

    public Vector3f getScale(float delta) {
        if (this.getCurrent() == null) {
            if (this.isLinked()) {
                return this.tardis().get().stats().getScale();
            }

            return new Vector3f(1f, 1f, 1f);
        }

        return this.getCurrent().getScale(delta);
    }

    public Vector3f getPosition(float delta) {
        if (this.getCurrent() == null) {
            return new Vector3f(0f, 0f, 0f);
        }

        return this.getCurrent().getPosition(delta);
    }

    public Vector3f getRotation(float delta) {
        if (this.getCurrent() == null) {
            return new Vector3f(0f, 0f, 0f);
        }

        return this.getCurrent().getRotation(delta);
    }

    private void sync(TravelHandlerBase.State state) {
        if (!ServerLifecycleHooks.isServer() || !this.isLinked() || !(this.tardis().get() instanceof ServerTardis)) return;

        ServerTardis tardis = this.tardis().get().asServer();

        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeEnumConstant(state);
        buf.writeUuid(tardis.getUuid());

        NetworkUtil.getSubscribedPlayers(tardis).forEach(player -> {;
            NetworkUtil.send(player, UPDATE_PACKET, buf);
        });
    }
}

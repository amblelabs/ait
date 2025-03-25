package dev.amble.ait.core.tardis.animation.v2;

import dev.amble.ait.api.tardis.Disposable;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisManager;
import dev.amble.ait.core.tardis.animation.v2.datapack.TardisAnimationRegistry;
import dev.amble.ait.core.tardis.animation.v2.keyframe.KeyframeTracker;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.lib.api.Identifiable;
import dev.amble.lib.util.ServerLifecycleHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.util.UUID;

/**
 * Represents an exterior animation for the TARDIS.
 * If you got this from the registry, call {@link TardisAnimation#instantiate()} as to not cause issues.
 */
public abstract class TardisAnimation implements TardisTickable, Disposable, Identifiable, Linkable {
	private final Identifier id;
	private TardisRef ref;
	private boolean isServer = true;
	protected final KeyframeTracker tracker;

	protected TardisAnimation(Identifier id, KeyframeTracker tracker) {
		this.id = id;
		this.tracker = tracker;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void tick(MinecraftClient client) {
		this.tickCommon();

		this.tracker.tick(client);

		this.isServer = false;
	}

	@Override
	public void tick(MinecraftServer server) {
		this.tickCommon();

		this.tracker.tick(server);
	}

	protected void tickCommon() {
		if (!this.isLinked()) return;
		if (this.tracker.getCurrent().ticks() != 0) return;

		Tardis tardis = this.tardis().get();
		TravelHandlerBase.State state = tardis.travel().getState();

		float alpha = (state == TravelHandlerBase.State.MAT) ? 0f : 1f;
		this.tracker.start(tardis.travel().position(), alpha);
	}

	@Override
	public void dispose() {
		this.tracker.dispose();
	}

	@Override
	public boolean isAged() {
		return this.tracker.isAged();
	}

	@Override
	public void age() {
		this.tracker.age();
	}

	@Override
	public Identifier id() {
		return this.id;
	}

	public float getAlpha() {
		return this.tracker.getAlpha();
	}

	// required for datapacks
	public KeyframeTracker tracker() {
		return this.tracker;
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
	 * Creates a new instance of this animation
	 * @return a new instance
	 */
	public abstract TardisAnimation instantiate();
}

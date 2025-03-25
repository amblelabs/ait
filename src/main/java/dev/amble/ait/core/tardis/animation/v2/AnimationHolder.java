package dev.amble.ait.core.tardis.animation.v2;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.ClientWorldEvents;
import dev.amble.ait.api.tardis.Disposable;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.client.tardis.manager.ClientTardisManager;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisManager;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.util.NetworkUtil;
import dev.amble.lib.util.ServerLifecycleHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class AnimationHolder implements TardisTickable, Disposable, Linkable {
	public static final Identifier UPDATE_PACKET = AITMod.id("sync/ext_anim");

	static {
		if (EnvType.CLIENT == FabricLoader.getInstance().getEnvironmentType()) initClient();
	}

	@Environment(EnvType.CLIENT)
	private static void initClient() {
		ClientPlayNetworking.registerGlobalReceiver(UPDATE_PACKET, (client, handler, buf, responseSender) -> {
			TravelHandlerBase.State state = buf.readEnumConstant(TravelHandlerBase.State.class);
			UUID uuid = buf.readUuid();

			ClientTardisManager.getInstance().getTardis(uuid, tardis -> {
				ClientWorld world = MinecraftClient.getInstance().world;
				BlockPos pos = tardis.travel().position().getPos();

				if (!(world.getBlockEntity(pos) instanceof ExteriorBlockEntity ext)) return;

				ext.getAnimations().onStateChange(state);
			});
		});
	}

	protected final TardisAnimationMap map;
	private TardisAnimation current;
	private float alphaOverride = -1;
	private boolean isServer = true;
	private TardisRef ref;

	public AnimationHolder(TardisAnimationMap map) {
		this.map = map;
	}

	public AnimationHolder(Tardis tardis) {
		this(tardis.stats().getTravelAnimations());
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
	}

	@Override
	public TardisRef tardis() {
		return this.ref;
	}

	public void onStateChange(TravelHandlerBase.State state) {
		TardisAnimation animation = this.map.get(state);
		if (animation == null) {
			switch (state) {
				case LANDED, DEMAT:
					this.alphaOverride = 1f;
				case FLIGHT, MAT:
					this.alphaOverride = 0f;
			}
			return;
		}

		this.alphaOverride = -1;

		if (this.current != null) {
			this.current.dispose();
		}

		animation.dispose();;
		this.current = animation;

		this.sync(state);
	}

	public float getAlpha() {
		if (this.alphaOverride != -1) {
			return this.alphaOverride;
		}

		if (this.current == null) return 1f;

		return this.current.getAlpha();
	}

	private void sync(TravelHandlerBase.State state) {
		if (!ServerLifecycleHooks.isServer() || !this.isLinked()) return;

		ServerTardis tardis = this.tardis().get().asServer();

		PacketByteBuf buf = PacketByteBufs.create();

		buf.writeEnumConstant(state);
		buf.writeUuid(tardis.getUuid());

		for (ServerPlayerEntity player : NetworkUtil.getLinkedPlayers(tardis)) {
			NetworkUtil.send(player, UPDATE_PACKET, buf);
		}
	}
}

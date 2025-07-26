package dev.amble.ait.core.tardis.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.api.tardis.WorldWithTardis;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.manager.old.DeprecatedServerTardisManager;
import dev.amble.ait.core.tardis.util.NetworkUtil;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.registry.impl.TardisComponentRegistry;

public class ServerTardisManager extends DeprecatedServerTardisManager {

    private static ServerTardisManager instance;

    private final Set<ServerTardis> delta = new HashSet<>();

    public static void init() {
        instance = new ServerTardisManager();
    }

    private ServerTardisManager() {
        TardisEvents.SYNC_TARDIS.register(WorldWithTardis.forSync((player, tardisSet) -> {
            if (this.fileManager.isLocked())
                return;

            if (AITMod.CONFIG.sendBulk && tardisSet.size() >= 8) {
                this.sendTardisBulk(player, tardisSet);
                return;
            }

            this.sendTardisAll(player, tardisSet);
        }));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server)
                -> this.sendTardisAll(handler.getPlayer(), NetworkUtil.findLinkedItems(handler.getPlayer())));

        if (DEMENTIA) {
            TardisEvents.UNLOAD_TARDIS.register(WorldWithTardis.forDesync((player, tardisSet) -> {
                for (ServerTardis tardis : tardisSet) {
                    if (isInvalid(tardis))
                        continue;

                    this.sendTardisRemoval(player, tardis);
                }
            }));
        }

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (this.fileManager.isLocked())
                return;

            for (ServerTardis tardis : this.delta) {
                if (isInvalid(tardis))
                    continue;

                if (!tardis.hasDelta())
                    continue;

                PacketByteBuf buf = this.prepareSendDelta(tardis);
                tardis.consumeDelta(component -> this.writeComponent(component, buf));

                NetworkUtil.getSubscribedPlayers(tardis).forEach(
                        watching -> ServerPlayNetworking.send(watching, SEND_COMPONENT, buf)
                );
            }

            this.delta.clear();
        });

        ServerPlayNetworking.registerGlobalReceiver(SEND_PROPERTY, (server, player, networkHandler, buf, response) -> {
            UUID tardisId = buf.readUuid();
            ServerTardis tardis = this.demandTardis(server, tardisId);

            if (tardis == null)
                return;

            TardisComponent.IdLike componentId = TardisComponentRegistry.getInstance().get(buf.readString());

            if (!(tardis.handler(componentId) instanceof KeyedTardisComponent keyed))
                return;

            String propertyId = buf.readString();
            keyed.getPropertyData().get(propertyId).read(buf);
        });
    }

    @Override
    public ServerTardis create(TardisBuilder builder) {
        if (this.isFull()) return null;

        ServerTardis result = super.create(builder);
        this.sendTardisAll(Set.of(result));

        return result;
    }

    private void sendTardis(ServerPlayerEntity player, PacketByteBuf data) {
        ServerPlayNetworking.send(player, SEND, data);
    }

    private void writeSend(ServerTardis tardis, PacketByteBuf buf) {
        buf.writeUuid(tardis.getUuid());
        buf.writeString(this.networkGson.toJson(tardis, ServerTardis.class));
    }

    private void writeComponent(TardisComponent component, PacketByteBuf buf) {
        String rawId = TardisComponentRegistry.getInstance().get(component);

        buf.writeString(rawId);
        buf.writeString(this.networkGson.toJson(component));
    }

    private PacketByteBuf prepareSend(ServerTardis tardis) {
        PacketByteBuf data = PacketByteBufs.create();
        this.writeSend(tardis, data);

        return data;
    }

    private PacketByteBuf prepareSendDelta(ServerTardis tardis) {
        PacketByteBuf data = PacketByteBufs.create();

        data.writeUuid(tardis.getUuid());
        data.writeShort(tardis.getDeltaSize());

        return data;
    }

    protected void sendTardisBulk(ServerPlayerEntity player, Set<ServerTardis> set) {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeInt(set.size());

        for (ServerTardis tardis : set) {
            if (isInvalid(tardis))
                continue;

            this.writeSend(tardis, data);
        }

        ServerPlayNetworking.send(player, SEND_BULK, data);
    }

    protected void sendTardisAll(ServerPlayerEntity player, Set<ServerTardis> set) {
        for (ServerTardis tardis : set) {
            if (isInvalid(tardis))
                continue;

            TardisEvents.SEND_TARDIS.invoker().send(tardis, player);
            this.sendTardis(player, this.prepareSend(tardis));
        }
    }

    protected void sendTardisAll(Set<ServerTardis> set) {
        for (ServerTardis tardis : set) {
            if (isInvalid(tardis))
                continue;

            PacketByteBuf buf = this.prepareSend(tardis);

            NetworkUtil.getSubscribedPlayers(tardis).forEach(
                    watching -> {
                        TardisEvents.SEND_TARDIS.invoker().send(tardis, watching);
                        this.sendTardis(watching, buf);
                    }
            );
        }
    }

    public void mark(ServerWorld world, ServerTardis tardis, ChunkPos chunk) {
        ((WorldWithTardis) world).ait$lookup().put(chunk, tardis);

        NetworkUtil.getSubscribedPlayers(tardis).forEach(player ->
                TardisEvents.SYNC_TARDIS.invoker().sync(player, chunk));
    }

    public void unmark(ServerWorld world, ServerTardis tardis, ChunkPos chunk) {
        ((WorldWithTardis) world).ait$withLookup(lookup -> lookup.remove(chunk, tardis));
    }

    @Override
    public void markComponentDirty(TardisComponent component) {
        if (this.fileManager.isLocked())
            return;

        if (!(component.tardis() instanceof ServerTardis tardis))
            return;

        if (isInvalid(tardis))
            return;

        tardis.markDirty(component);
        this.delta.add(tardis);
    }

    @Override
    public void markPropertyDirty(ServerTardis tardis, Value<?> value) {
        this.markComponentDirty(value.getHolder());
    }

    @Override
    public void reset() {
        this.delta.clear();
        super.reset();
    }

    public boolean isFull() {
        int max = AITMod.CONFIG.maxTardises;
        return max > 0 && this.lookup.size() >= max;
    }

    private static boolean isInvalid(ServerTardis tardis) {
        return tardis == null || tardis.isRemoved();
    }

    public static ServerTardisManager getInstance() {
        return instance;
    }
}

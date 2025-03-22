package dev.amble.ait.core.tardis.manager;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Either;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.util.ServerLifecycleHooks;
import dev.drtheo.multidim.MultiDim;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.api.tardis.WorldWithTardis;
import dev.amble.ait.core.events.ServerCrashEvent;
import dev.amble.ait.core.events.WorldSaveEvent;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisManager;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.util.NetworkUtil;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.util.ForcedChunkUtil;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.OptionalTardisMap;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.amble.ait.data.properties.dbl.DoubleValue;
import dev.amble.ait.data.properties.integer.IntValue;
import dev.amble.ait.data.properties.integer.ranged.RangedIntValue;
import dev.amble.ait.registry.impl.TardisComponentRegistry;

public class ServerTardisManager extends TardisManager<ServerTardis> {

    public static void init() {
        TardisManager.server = new ServerTardisManager();
    }

    protected final Gson fileGson = this.createGson(Exclude.Strategy.FILE, this::getFileGson);

    private final TardisFileManager fileManager = new TardisFileManager();
    private final OptionalTardisMap<ServerTardis> map = new OptionalTardisMap<>();

    private final Set<ServerTardis> dirty = new HashSet<>();

    private ServerTardisManager() {
        super();

        ServerLifecycleEvents.SERVER_STOPPING.register(this::saveAndReset);

        ServerCrashEvent.EVENT.register(((server, report) -> this.reset())); // just panic and reset
        WorldSaveEvent.EVENT.register(world -> this.save(world.getServer(), false));

        TardisEvents.SYNC_TARDIS.register(WorldWithTardis.forSync((player, tardisSet) -> {
            if (this.fileManager.isLocked())
                return;

            if (AITMod.CONFIG.SERVER.BULK_SIZE > 1) {
                this.sendTardisBulk(player, tardisSet);
                return;
            }

            this.sendTardisAll(player, tardisSet);
        }));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server)
                -> this.sendTardisAll(handler.getPlayer(), NetworkUtil.findLinkedItems(handler.getPlayer())));

        ServerPlayNetworking.registerGlobalReceiver(SEND_VALUE, (server, player, handler, buf, sender) -> this.receiveValue(buf));

        ServerTickEvents.END_SERVER_TICK.register(this::endTick);
        ServerTickEvents.START_SERVER_TICK.register(this::startTick);
    }

    @Nullable public ServerTardis create(TardisBuilder builder) {
        if (this.isFull())
            return null;

        ServerTardis tardis = builder.build();
        this.map.put(tardis);

        this.sendTardisAround(tardis);
        return tardis;
    }

    private static boolean isInvalid(Tardis tardis) {
        return !(tardis instanceof ServerTardis server) || isInvalid(server);
    }

    private static boolean isInvalid(ServerTardis tardis) {
        return tardis == null || tardis.isRemoved();
    }

    private void startTick(MinecraftServer server) {
        if (this.fileManager.isLocked())
            return;

        for (ServerTardis tardis : this.dirty) {
            if (isInvalid(tardis))
                continue;

            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeUuid(tardis.getUuid());
            buf.writeShort(tardis.getDeltaSize());

            tardis.consumeDelta(component -> {
                String rawId = TardisComponentRegistry.getInstance().get(component);

                buf.writeString(rawId);
                buf.writeString(this.networkGson.toJson(component));
            });

            NetworkUtil.getSubscribedPlayers(tardis).forEach(
                    watching -> ServerPlayNetworking.send(watching, SEND_DELTA, buf)
            );
        }

        this.dirty.clear();
    }

    private void endTick(MinecraftServer server) {
        this.forEach(tardis -> {
            if (tardis.isRemoved())
                return;

            tardis.tick(server);
        });
    }

    public void markComponentDirty(TardisComponent component) {
        if (this.fileManager.isLocked())
            return;

        Tardis tardis = component.tardis();

        if (isInvalid(tardis))
            return;

        ServerTardis serverTardis = ((ServerTardis) tardis);

        serverTardis.markDirty(component);
        this.dirty.add(serverTardis);
    }

    public void markPropertyDirty(Value<?> value) {
        this.markComponentDirty(value.getHolder());
    }

    @SuppressWarnings("unchecked")
    private <T> void receiveValue(PacketByteBuf buf) {
        UUID id = buf.readUuid();
        String rawComponent = buf.readString();
        String property = buf.readString();
        String data = buf.readString();

        ServerTardis tardis = this.getTardis(id);

        if (tardis == null)
            return;

        TardisComponent.IdLike comp = TardisComponentRegistry.getInstance().get(rawComponent);

        if (!(tardis.handler(comp) instanceof KeyedTardisComponent keyed))
            return;

        Value<T> value = keyed.getPropertyData().getExact(property);
        Class<?> classOfT = value.getProperty().getType().getClazz();

        value.set((T) this.fileGson.fromJson(data, classOfT));
    }

    private PacketByteBuf prepareSend(ServerTardis tardis) {
        PacketByteBuf data = PacketByteBufs.create();
        this.writeSend(tardis, data);

        return data;
    }

    private void writeSend(ServerTardis tardis, PacketByteBuf buf) {
        buf.writeUuid(tardis.getUuid());
        buf.writeString(this.networkGson.toJson(tardis, ServerTardis.class));
    }

    protected void sendTardisAll(ServerPlayerEntity player, Set<ServerTardis> set) {
        for (ServerTardis tardis : set) {
            if (isInvalid(tardis))
                continue;

            TardisEvents.SEND_TARDIS.invoker().send(tardis, player);
            ServerPlayNetworking.send(player, SEND, this.prepareSend(tardis));
        }
    }

    protected void sendTardisAround(ServerTardis tardis) {
        if (isInvalid(tardis))
            return;

        this.sendTardisToAll(tardis, NetworkUtil.getSubscribedPlayers(tardis));
    }

    protected void sendTardisToAll(ServerTardis tardis, Stream<ServerPlayerEntity> iterable) {
        if (isInvalid(tardis))
            return;

        PacketByteBuf buf = this.prepareSend(tardis);
        iterable.forEach(watching -> ServerPlayNetworking.send(watching, SEND, buf));
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

    public boolean isFull() {
        int max = AITMod.CONFIG.SERVER.MAX_TARDISES;

        if (max < 0)
            return false;

        return this.map.size() >= max;
    }

    public void mark(ServerWorld world, ServerTardis tardis, ChunkPos chunk) {
        ((WorldWithTardis) world).ait$lookup().put(chunk, tardis);
    }

    public void unmark(ServerWorld world, ServerTardis tardis, ChunkPos chunk) {
        ((WorldWithTardis) world).ait$withLookup(lookup -> lookup.remove(chunk, tardis));
    }

    @Nullable @Override
    public ServerTardis getTardis(UUID id) {
        Either<ServerTardis, ?> either = this.map.get(id);

        if (either == null)
            either = this.loadTardis(id);

        return either.map(tardis -> tardis, o -> null);
    }

    protected Either<ServerTardis, Exception> loadTardis(UUID id) {
        Either<ServerTardis, Exception> result = this.fileManager.loadTardis(ServerLifecycleHooks.get(), this, id);

        result.ifLeft(this.map::put).ifRight(e -> this.map.empty(id, e));
        return result;
    }

    protected GsonBuilder getFileGson(GsonBuilder builder) {
        if (!AITMod.CONFIG.SERVER.MINIFY_JSON)
            builder.setPrettyPrinting();

        return builder.registerTypeAdapter(Value.class, Value.serializer())
                .registerTypeAdapter(BoolValue.class, BoolValue.serializer())
                .registerTypeAdapter(IntValue.class, IntValue.serializer())
                .registerTypeAdapter(RangedIntValue.class, RangedIntValue.serializer())
                .registerTypeAdapter(DoubleValue.class, DoubleValue.serializer());
    }

    public Gson getFileGson() {
        return fileGson;
    }

    @Override
    public Collection<UUID> ids() {
        return this.map.keySet();
    }

    @Override
    public void forEach(Consumer<ServerTardis> consumer) {
        this.map.forEach((uuid, t) -> t.ifLeft(consumer));
    }

    public void remove(MinecraftServer server, ServerTardis tardis) {
        tardis.setRemoved(true);

        ServerWorld tardisWorld = tardis.getInteriorWorld();
        CachedDirectedGlobalPos exteriorPos = tardis.travel().position();

        if (exteriorPos != null) {
            TardisUtil.getPlayersInsideInterior(tardis).forEach(
                    player -> TardisUtil.teleportOutside(tardis, player));

            World world = exteriorPos.getWorld();
            BlockPos pos = exteriorPos.getPos();

            world.removeBlock(pos, false);
            world.removeBlockEntity(pos);

            ForcedChunkUtil.stopForceLoading(exteriorPos);
        }

        MultiDim.get(server).remove(tardisWorld.getRegistryKey());

        this.sendTardisRemoval(server, tardis);

        this.map.remove(tardis.getUuid());
        this.fileManager.delete(server, tardis.getUuid());
    }

    protected void sendTardisRemoval(MinecraftServer server, ServerTardis tardis) {
        if (tardis == null)
            return;

        PacketByteBuf data = this.prepareTardisRemoval(tardis);

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            this.sendTardisRemoval(player, data);
        }
    }

    protected void sendTardisRemoval(ServerPlayerEntity player, ServerTardis tardis) {
        this.sendTardisRemoval(player, this.prepareTardisRemoval(tardis));
    }

    protected void sendTardisRemoval(ServerPlayerEntity player, PacketByteBuf buf) {
        ServerPlayNetworking.send(player, REMOVE, buf);
    }

    protected PacketByteBuf prepareTardisRemoval(ServerTardis tardis) {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeUuid(tardis.getUuid());

        return data;
    }

    /** Save/reset **/

    public void reset() {
        this.map.clear();
    }

    private void save(MinecraftServer server, boolean clean) {
        if (clean)
            this.fileManager.setLocked(true);

        this.forEach(tardis -> {
            if (clean) {
                if (tardis == null)
                    return;

                // TODO move this into some method like #dispose
                ForcedChunkUtil.stopForceLoading(tardis.travel().position());
                TravelHandlerBase.State state = tardis.travel().getState();

                if (state == TravelHandlerBase.State.DEMAT) {
                    tardis.travel().finishDemat();
                } else if (state == TravelHandlerBase.State.MAT) {
                    tardis.travel().finishRemat();
                }

                tardis.door().closeDoors();
                tardis.interiorChangingHandler().queued().set(false);
                tardis.interiorChangingHandler().regenerating().set(false);
            }

            this.fileManager.saveTardis(server, this, tardis);
        });

        if (!clean)
            return;

        for (ServerWorld world : server.getWorlds()) {
            ((WorldWithTardis) world).ait$withLookup(HashMap::clear);
        }
    }

    private void saveAndReset(MinecraftServer server) {
        this.save(server, true);
        this.reset();
    }

    /** Utility methods **/

    public void loadAll(MinecraftServer server, @Nullable Consumer<ServerTardis> consumer) {
        for (UUID id : this.fileManager.getTardisList(server)) {
            consumer.accept(this.getTardis(id));
        }
    }

    public static ServerPlayNetworking.PlayChannelHandler receiveTardis(Receiver receiver) {
        return (server, player, handler, buf, responseSender) -> {
            receiver.receive(TardisManager.server().getTardis(buf.readUuid()), server, player, handler, buf, responseSender);
        };
    }

    @FunctionalInterface
    public interface Receiver {
        void receive(ServerTardis tardis, MinecraftServer server, ServerPlayerEntity player,
                     ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender);
    }
}

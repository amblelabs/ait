package dev.amble.ait.core.tardis.manager.old;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.drtheo.multidim.MultiDim;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.WorldWithTardis;
import dev.amble.ait.core.events.ServerCrashEvent;
import dev.amble.ait.core.events.WorldSaveEvent;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisManager;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.tardis.manager.TardisBuilder;
import dev.amble.ait.core.tardis.manager.TardisFileManager;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.TardisMap;
import dev.amble.ait.data.properties.Value;

public abstract class DeprecatedServerTardisManager extends TardisManager<ServerTardis, MinecraftServer> implements TardisFileManager.TardisLoader<ServerTardis> {

    protected final TardisMap.Optional<ServerTardis> lookup = new TardisMap.Optional<>();
    protected final TardisFileManager<ServerTardis> fileManager = new TardisFileManager<>();

    public DeprecatedServerTardisManager() {
        this.fileManager.setLocked(true);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> this.fileManager.setLocked(false));
        ServerLifecycleEvents.SERVER_STOPPING.register(this::saveAndReset);

        ServerCrashEvent.EVENT.register(((server, report) -> this.reset())); // just panic and reset

        WorldSaveEvent.EVENT.register(world -> {
            if (world == WorldUtil.getOverworld())
                this.save(world.getServer(), false);
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            this.forEach(tardis -> {
                if (tardis.isRemoved() || !tardis.shouldTick())
                    return;

                tardis.tick(server);
            });
        });
    }

    @Override
    protected GsonBuilder createGsonBuilder(Exclude.Strategy strategy) {
        return super.createGsonBuilder(strategy)
                .registerTypeAdapter(Tardis.class, ServerTardis.creator());
    }

    public ServerTardis create(TardisBuilder builder) {
        Objects.requireNonNull(builder);

        ServerTardis tardis = builder.build();
        this.lookup.put(tardis);

        return tardis;
    }

    protected void sendTardisRemoval(MinecraftServer server, ServerTardis tardis) {
        if (tardis == null)
            return;

        PacketByteBuf data = PacketByteBufs.create();
        data.writeUuid(tardis.getUuid());

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            this.sendTardisRemoval(player, data);
        }
    }

    protected void sendTardisRemoval(ServerPlayerEntity player, ServerTardis tardis) {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeUuid(tardis.getUuid());

        this.sendTardisRemoval(player, data);
    }

    protected void sendTardisRemoval(ServerPlayerEntity player, PacketByteBuf data) {
        ServerPlayNetworking.send(player, REMOVE, data);
    }

    public abstract void markComponentDirty(TardisComponent component);

    public abstract void markPropertyDirty(ServerTardis tardis, Value<?> value);

    @Override
    public @Nullable ServerTardis demandTardis(@NotNull MinecraftServer server, @NotNull UUID uuid) {
        Objects.requireNonNull(uuid);

        if (this.fileManager.isLocked())
            return null;

        Either<ServerTardis, ?> either = this.lookup.get(uuid);

        if (either == null)
            either = this.loadTardis(server, uuid);

        return either.map(tardis -> tardis, o -> null);
    }

    @Override
    public void getTardis(MinecraftServer server, @NotNull UUID uuid, @NotNull Consumer<ServerTardis> consumer) {
        Objects.requireNonNull(uuid);
        Objects.requireNonNull(consumer);

        if (this.fileManager.isLocked())
            return;

        Either<ServerTardis, ?> either = this.lookup.get(uuid);

        if (either == null)
            either = this.loadTardis(server, uuid);

        either.ifLeft(consumer);
    }

    @Override
    protected TardisMap.Optional<ServerTardis> lookup() {
        return lookup;
    }

    @Override
    public void forEach(Consumer<ServerTardis> consumer) {
        this.lookup.forEach((uuid, either) -> either.ifLeft(consumer));
    }

    @NotNull public Either<ServerTardis, Exception> loadTardis(MinecraftServer server, UUID uuid) {
        Either<ServerTardis, Exception> result = this.fileManager.loadTardis(server, this, uuid, this);

        this.lookup.put(uuid, result);
        return result;
    }

    public void loadAll(MinecraftServer server, @Nullable Consumer<ServerTardis> consumer) {
        for (UUID id : this.fileManager.getTardisList(server)) {
            this.getTardis(server, id, consumer);
        }
    }

    public void remove(MinecraftServer server, ServerTardis tardis) {
        Objects.requireNonNull(tardis);

        tardis.setRemoved(true);

        CachedDirectedGlobalPos exteriorPos = tardis.travel().position();

        if (exteriorPos != null) {
            tardis.world().getPlayers().forEach(player
                    -> TardisUtil.teleportOutside(tardis, player));

            World world = exteriorPos.getWorld();
            BlockPos pos = exteriorPos.getPos();

            world.removeBlock(pos, false);
            world.removeBlockEntity(pos);
        }

        MultiDim.get(server).queueRemove(TardisServerWorld.keyForTardis(tardis));

        this.sendTardisRemoval(server, tardis);

        this.lookup.remove(tardis.getUuid());
        this.fileManager.delete(server, tardis.getUuid());
    }

    private void save(MinecraftServer server, boolean close) {
        if (close)
            this.fileManager.setLocked(true);

        this.forEach(tardis -> {
            if (tardis == null)
                return;

            if (close) {
                // TODO move this into some method like #dispose
                TravelHandlerBase.State state = tardis.travel().getState();

                if (state == TravelHandlerBase.State.DEMAT) {
                    tardis.travel().finishDemat();
                } else if (state == TravelHandlerBase.State.MAT) {
                    tardis.travel().finishRemat();
                }
            }

            this.fileManager.saveTardis(server, this, tardis);
        });

        if (!close)
            return;

        for (ServerWorld world : server.getWorlds()) {
            ((WorldWithTardis) world).ait$withLookup(HashMap::clear);
        }
    }

    private void saveAndReset(MinecraftServer server) {
        this.save(server, true);
        this.reset();
    }

    /**
     * @return An initialized {@link ServerTardis} without attachments.
     */
    @Override
    public ServerTardis readTardis(Gson gson, JsonObject json) {
        ServerTardis tardis = gson.fromJson(json, ServerTardis.class);
        Tardis.init(tardis, TardisComponent.InitContext.deserialize());

        return tardis;
    }

    public static ServerPlayNetworking.PlayChannelHandler receiveTardis(Receiver receiver) {
        return (server, player, handler, buf, responseSender) -> {
            ServerTardisManager.getInstance().getTardis(server, buf.readUuid(),
                    tardis -> receiver.receive(tardis, server, player, handler, buf, responseSender));
        };
    }

    @FunctionalInterface
    public interface Receiver {
        void receive(ServerTardis tardis, MinecraftServer server, ServerPlayerEntity player,
                ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender);
    }
}

package dev.loqor.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.lib.data.CachedDirectedGlobalPos;

/**
 * Server-side driver for the cross-dimensional door portal ("real BOTI").
 * <p>
 * For every loaded TARDIS that is landed and currently has player(s) inside its interior dimension, a
 * {@link PacketProxyPlayer fake player} is parked at the TARDIS's <em>real</em> exterior position. The chunk
 * manager streams that surrounding area to the proxy; we intercept those packets and forward them - tagged with
 * the TARDIS UUID - to the players viewing the door from inside, where they are applied to a shadow world and
 * rendered into the doorway.
 * <p>
 * The proxy follows the TARDIS: it is repositioned when the exterior moves and recreated in the new world when
 * the TARDIS changes dimension. Proxies are torn down once a TARDIS has no interior viewers, leaves the LANDED
 * state, or unloads.
 */
public class ExampleMod implements ModInitializer {

    /** One proxy per TARDIS currently being viewed from the inside, keyed by TARDIS UUID. */
    private static final Map<UUID, ProxyEntry> PROXIES = new HashMap<>();

    /** How often (in ticks) to reconcile proxies with the world. Landed TARDISes are stationary, so 1s is ample. */
    private static final long REFRESH_INTERVAL = 20L;

    private long tickCounter = 0;

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
        ServerLifecycleEvents.SERVER_STOPPING.register(ExampleMod::clearAll);
    }

    private void onServerTick(MinecraftServer server) {
        if (this.tickCounter++ % REFRESH_INTERVAL != 0)
            return;

        ServerTardisManager manager = ServerTardisManager.getInstance();
        if (manager == null)
            return;

        Set<UUID> active = new HashSet<>();

        manager.forEach(tardis -> {
            if (ensureProxy(server, tardis))
                active.add(tardis.getUuid());
        });

        // Tear down proxies whose TARDIS no longer wants one (no viewers, took off, or unloaded entirely).
        List<UUID> stale = new ArrayList<>();
        for (UUID id : PROXIES.keySet()) {
            if (!active.contains(id))
                stale.add(id);
        }

        for (UUID id : stale)
            removeProxy(server, id);
    }

    /**
     * Ensures a correctly-positioned proxy exists for the given TARDIS, creating, moving or recreating it as
     * needed.
     *
     * @return {@code true} if a proxy is (now) active for this TARDIS, {@code false} if it should not have one
     */
    private static boolean ensureProxy(MinecraftServer server, ServerTardis tardis) {
        UUID id = tardis.getUuid();

        Set<UUID> viewers = viewerIds(tardis);
        if (!tardis.travel().isLanded() || viewers.isEmpty())
            return false;

        CachedDirectedGlobalPos ext = tardis.travel().position();
        if (ext == null)
            return false;

        ServerWorld extWorld = ext.getWorld();
        if (extWorld == null) {
            ext.init(server);
            extWorld = ext.getWorld();
        }

        if (extWorld == null)
            return false;

        BlockPos extPos = ext.getPos();
        ProxyEntry entry = PROXIES.get(id);

        if (entry == null) {
            PROXIES.put(id, createProxy(tardis, extWorld, extPos, viewers));
            return true;
        }

        // A player started viewing after the surroundings were already streamed - they have none of it yet.
        boolean newViewer = !entry.viewers.containsAll(viewers);
        entry.viewers = viewers;

        // Dimension changed (e.g. a Nether trip) or a new viewer joined - rebuild the proxy from scratch so the
        // whole surrounding area (and the dimension reset) is re-sent to everyone watching this TARDIS.
        if (entry.dimension != extWorld.getRegistryKey() || newViewer) {
            despawn(server, entry);
            PROXIES.put(id, createProxy(tardis, extWorld, extPos, viewers));
            return true;
        }

        // Moved to a different chunk column or vertical slice - reposition and re-stream the surroundings.
        boolean movedChunk = entry.pos.getX() >> 4 != extPos.getX() >> 4
                || entry.pos.getZ() >> 4 != extPos.getZ() >> 4
                || entry.pos.getY() != extPos.getY();

        if (movedChunk) {
            entry.proxy.setPos(extPos.getX(), extPos.getY(), extPos.getZ());
            entry.proxy.onChunkEntered();
            entry.pos = extPos;
            broadcastCenter(tardis, entry.proxy);
        }

        return true;
    }

    private static Set<UUID> viewerIds(ServerTardis tardis) {
        if (!tardis.hasWorld())
            return Set.of();

        Set<UUID> ids = new HashSet<>();
        for (ServerPlayerEntity player : tardis.world().getPlayers())
            ids.add(player.getUuid());

        return ids;
    }

    private static ProxyEntry createProxy(ServerTardis tardis, ServerWorld world, BlockPos pos, Set<UUID> viewers) {
        // Tell viewers which dimension to mirror (and reset any stale shadow world) before streaming chunks.
        broadcastInit(tardis, world);

        PacketProxyPlayer proxy = new PacketProxyPlayer(world);
        proxy.setPos(pos.getX(), pos.getY(), pos.getZ());
        proxy.setPacketListener(packet -> forward(tardis, packet));

        world.spawnEntity(proxy);
        proxy.onChunkEntered();

        broadcastCenter(tardis, proxy);
        return new ProxyEntry(proxy, world.getRegistryKey(), pos, viewers);
    }

    private static void removeProxy(MinecraftServer server, UUID id) {
        ProxyEntry entry = PROXIES.remove(id);
        if (entry != null)
            despawn(server, entry);
    }

    private static void despawn(MinecraftServer server, ProxyEntry entry) {
        ServerWorld world = server.getWorld(entry.dimension);
        if (world != null)
            world.removePlayer(entry.proxy, Entity.RemovalReason.DISCARDED);
    }

    /** Called by the proxy's network handler for every packet the chunk manager streams to it. */
    private static void forward(ServerTardis tardis, Packet<?> packet) {
        if (shouldForward(packet))
            broadcast(tardis, packet);
    }

    /**
     * The subset of the proxy's stream we replay into the shadow world: terrain and the entity-tracking packets
     * the entity tracker sends for everything around the exterior. {@link EntityS2CPacket} is the abstract base
     * for the three relative move/rotate variants, so one check covers all of them.
     */
    private static boolean shouldForward(Packet<?> packet) {
        return packet instanceof ChunkDataS2CPacket
                || packet instanceof ChunkDeltaUpdateS2CPacket
                || packet instanceof BlockUpdateS2CPacket
                || packet instanceof UnloadChunkS2CPacket
                || packet instanceof EntitySpawnS2CPacket
                || packet instanceof EntityPositionS2CPacket
                || packet instanceof EntityS2CPacket
                || packet instanceof EntityVelocityUpdateS2CPacket
                || packet instanceof EntitySetHeadYawS2CPacket
                || packet instanceof EntityTrackerUpdateS2CPacket
                || packet instanceof EntityEquipmentUpdateS2CPacket
                || packet instanceof EntitiesDestroyS2CPacket;
    }

    private static void broadcastInit(ServerTardis tardis, ServerWorld world) {
        RegistryKey<DimensionType> type = world.getDimensionEntry().getKey().orElse(DimensionTypes.OVERWORLD);
        sendToViewers(tardis, new PortalInitS2CPacket(tardis.getUuid(), world.getRegistryKey(), type));
    }

    private static void broadcastCenter(ServerTardis tardis, PacketProxyPlayer proxy) {
        ChunkPos center = proxy.getChunkPos();
        broadcast(tardis, new ChunkRenderDistanceCenterS2CPacket(center.x, center.z));
    }

    private static void broadcast(ServerTardis tardis, Packet<?> packet) {
        sendToViewers(tardis, new WrappedPacketS2CPacket(tardis.getUuid(), packet));
    }

    private static void sendToViewers(ServerTardis tardis, FabricPacket packet) {
        if (!tardis.hasWorld())
            return;

        for (ServerPlayerEntity player : tardis.world().getPlayers()) {
            if (player instanceof PacketProxyPlayer)
                continue;

            ServerPlayNetworking.send(player, packet);
        }
    }

    private static void clearAll(MinecraftServer server) {
        for (ProxyEntry entry : PROXIES.values())
            despawn(server, entry);

        PROXIES.clear();
    }

    private static final class ProxyEntry {
        final PacketProxyPlayer proxy;
        final RegistryKey<World> dimension;
        BlockPos pos;
        Set<UUID> viewers;

        ProxyEntry(PacketProxyPlayer proxy, RegistryKey<World> dimension, BlockPos pos, Set<UUID> viewers) {
            this.proxy = proxy;
            this.dimension = dimension;
            this.pos = pos;
            this.viewers = viewers;
        }
    }
}

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
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
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
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.lib.data.CachedDirectedGlobalPos;

/**
 * Server-side driver for the cross-dimensional door portal ("real BOTI").
 * <p>
 * For every loaded TARDIS that is landed and has player(s) inside its interior dimension, a
 * {@link PacketProxyPlayer fake player} is parked at the TARDIS's real exterior position. Rather than
 * relying on the fake player's inherited server view distance (which can be 10–32 chunks and causes
 * massive chunk-loading pressure), we explicitly force-load exactly {@value #PORTAL_CHUNK_RADIUS}
 * chunks in each direction using a per-TARDIS {@link ChunkTicketType} ticket. The proxy still
 * receives entity-tracking packets naturally by virtue of being in the world; chunk packets from
 * beyond the defined area are filtered before being forwarded to interior viewers.
 * <p>
 * Changes from the original:
 * <ul>
 *   <li><b>Silent-despawn fix:</b> {@link ProxyEntry} stores the {@link ServerWorld} directly.
 *       {@code despawn} can therefore always call {@code removePlayer} — the original "world is null,
 *       skip silently" path that leaked entities and their chunk subscriptions is gone.</li>
 *   <li><b>Controlled chunk area:</b> Force-load tickets replace view-distance-driven loading.
 *       Each TARDIS uses its UUID as the ticket key so two nearby TARDISes never clobber each
 *       other's tickets, and the exact same set of tickets is added and removed symmetrically.</li>
 *   <li><b>Range filter:</b> Chunk packets (data, delta, block-update, unload) that arrive from
 *       outside the defined radius are dropped before they can be wrapped and queued for viewers,
 *       preventing large {@code ChunkDataS2CPacket} objects from accumulating in send queues.</li>
 *   <li><b>Collection reuse:</b> The per-tick active/stale sets are instance fields cleared each
 *       cycle rather than freshly allocated, cutting GC pressure on servers with many TARDISes.</li>
 *   <li><b>Weather caching:</b> Rain/thunder gradient packets are only sent when the value has
 *       actually changed by more than a small threshold.</li>
 *   <li><b>RegistryKey equality:</b> Dimension comparison uses {@code .equals()} instead of
 *       {@code !=} to avoid spurious proxy rebuilds if the registry ever returns distinct instances
 *       for the same key.</li>
 * </ul>
 */
public class BiggerOnTheInside implements ModInitializer {

    /**
     * How many chunks in each axis direction around the exterior position are explicitly
     * force-loaded and forwarded to interior viewers. Value of 3 yields a 7×7 footprint
     * (49 columns) — enough to show immediate surroundings through the doorway.
     * Raise if the portal effect needs more distance; lower to further reduce memory use.
     */
    private static final int PORTAL_CHUNK_RADIUS = 5;

    /**
     * Custom ticket type keyed by TARDIS {@link UUID}. Using a per-TARDIS UUID argument
     * means that each TARDIS's tickets are tracked independently: removing one TARDIS's
     * area never evicts another's, even when two TARDISes land near the same chunks.
     */
    private static final ChunkTicketType<UUID> PORTAL_TICKET =
            ChunkTicketType.create("portal_proxy", UUID::compareTo);

    /** One entry per TARDIS currently being viewed from inside its interior. */
    private static final Map<UUID, ProxyEntry> PROXIES = new HashMap<>();

    /** How often (in ticks) to reconcile proxies with the world state. */
    private static final long REFRESH_INTERVAL = 20L;

    /**
     * Reused across refresh cycles to avoid allocating a new collection on every tick.
     * Both sets are cleared at the top of each refresh, so they never carry stale data.
     */
    private final Set<UUID> activeThisTick = new HashSet<>();
    private final List<UUID> staleIds      = new ArrayList<>();

    private long tickCounter = 0;

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::clearAll);
    }

    // ─── Tick loop ────────────────────────────────────────────────────────────

    private void onServerTick(MinecraftServer server) {
        if (this.tickCounter++ % REFRESH_INTERVAL != 0)
            return;

        ServerTardisManager manager = ServerTardisManager.getInstance();
        if (manager == null)
            return;

        activeThisTick.clear();
        manager.forEach(tardis -> {
            if (ensureProxy(server, tardis))
                activeThisTick.add(tardis.getUuid());
        });

        staleIds.clear();
        for (UUID id : PROXIES.keySet()) {
            if (!activeThisTick.contains(id))
                staleIds.add(id);
        }
        for (UUID id : staleIds)
            removeProxy(id);
    }

    // ─── Proxy lifecycle ──────────────────────────────────────────────────────

    /**
     * Ensures a correctly-positioned proxy exists for {@code tardis}, creating,
     * updating, or rebuilding it as needed.
     *
     * @return {@code true} if a proxy is (now) active for this TARDIS
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

        BlockPos extPos  = ext.getPos();
        ProxyEntry entry = PROXIES.get(id);

        if (entry == null) {
            PROXIES.put(id, createProxy(tardis, extWorld, extPos, viewers));
            return true;
        }

        boolean newViewer = !entry.viewers.containsAll(viewers);
        entry.viewers = viewers;

        // Dimension changed or a new viewer arrived — rebuild from scratch so the whole
        // surrounding area (and any dimension reset) is re-sent to all current viewers.
        if (!entry.world.getRegistryKey().equals(extWorld.getRegistryKey()) || newViewer) {
            despawn(entry);
            PROXIES.put(id, createProxy(tardis, extWorld, extPos, viewers));
            return true;
        }

        // Incremental update: push time every cycle; weather only when it actually changes.
        broadcastTime(tardis, extWorld);
        maybeBroadcastWeather(tardis, extWorld, entry);

        // Moved to a different chunk column — migrate the forced-chunk ticket area to match.
        boolean movedChunk = entry.pos.getX() >> 4 != extPos.getX() >> 4
                || entry.pos.getZ() >> 4 != extPos.getZ() >> 4
                || entry.pos.getY() != extPos.getY();

        if (movedChunk) {
            removeChunkTickets(entry.world, entry.pos, id);

            entry.proxy.setPos(extPos.getX(), extPos.getY(), extPos.getZ());
            entry.proxy.onChunkEntered();

            // Keep the mutable-box reference in sync so the packet listener's range
            // filter uses the new position rather than the stale one.
            entry.posRef[0] = extPos;
            entry.pos = extPos;

            addChunkTickets(extWorld, extPos, id);
            broadcastCenter(tardis, entry.proxy);
        }

        return true;
    }

    private static ProxyEntry createProxy(ServerTardis tardis, ServerWorld world,
                                          BlockPos pos, Set<UUID> viewers) {
        UUID id = tardis.getUuid();

        broadcastInit(tardis, world);
        addChunkTickets(world, pos, id);

        // A single-element array acts as a mutable box so the packet-listener lambda
        // can always read the current exterior position without capturing the ProxyEntry
        // (which doesn't exist yet at this point). ensureProxy writes posRef[0] whenever
        // the TARDIS moves to a new chunk, keeping the range filter correct.
        BlockPos[] posRef = { pos };

        PacketProxyPlayer proxy = new PacketProxyPlayer(world);
        proxy.setPos(pos.getX(), pos.getY(), pos.getZ());
        proxy.setPacketListener(packet -> forwardIfInRange(tardis, posRef[0], packet));

        world.spawnEntity(proxy);
        proxy.onChunkEntered();

        float rain    = world.getRainGradient(1.0f);
        float thunder = world.getThunderGradient(1.0f);

        // Seed viewers with the current exterior state immediately on creation.
        broadcastTime(tardis, world);
        broadcastWeather(tardis, rain, thunder);
        broadcastCenter(tardis, proxy);

        return new ProxyEntry(id, proxy, world, posRef, pos, viewers, rain, thunder);
    }

    private static void removeProxy(UUID id) {
        ProxyEntry entry = PROXIES.remove(id);
        if (entry != null)
            despawn(entry);
    }

    /**
     * Tears down a proxy, releasing its chunk tickets and removing the entity from the world.
     * <p>
     * This is always safe: {@link ProxyEntry} holds the {@link ServerWorld} directly, so there
     * is no "world is null, silently bail" path — the original source of leaked entities and
     * their dangling chunk subscriptions.
     */
    private static void despawn(ProxyEntry entry) {
        removeChunkTickets(entry.world, entry.pos, entry.tardisId);
        entry.world.removePlayer(entry.proxy, Entity.RemovalReason.DISCARDED);
    }

    // ─── Chunk-area management ─────────────────────────────────────────────────

    /**
     * Force-loads every chunk column within {@value #PORTAL_CHUNK_RADIUS} chunks of
     * {@code center} in both axes. Each ticket is keyed by {@code tardisId} so that
     * removing one TARDIS's area never inadvertently evicts a nearby TARDIS's chunks.
     */
    private static void addChunkTickets(ServerWorld world, BlockPos center, UUID tardisId) {
        ChunkPos origin = new ChunkPos(center);
        for (int dx = -PORTAL_CHUNK_RADIUS; dx <= PORTAL_CHUNK_RADIUS; dx++) {
            for (int dz = -PORTAL_CHUNK_RADIUS; dz <= PORTAL_CHUNK_RADIUS; dz++) {
                world.getChunkManager().addTicket(
                        PORTAL_TICKET,
                        new ChunkPos(origin.x + dx, origin.z + dz),
                        0,          // no level propagation beyond the specified chunk
                        tardisId);
            }
        }
    }

    /** Exact mirror of {@link #addChunkTickets} — must always be called with identical arguments. */
    private static void removeChunkTickets(ServerWorld world, BlockPos center, UUID tardisId) {
        ChunkPos origin = new ChunkPos(center);
        for (int dx = -PORTAL_CHUNK_RADIUS; dx <= PORTAL_CHUNK_RADIUS; dx++) {
            for (int dz = -PORTAL_CHUNK_RADIUS; dz <= PORTAL_CHUNK_RADIUS; dz++) {
                world.getChunkManager().removeTicket(
                        PORTAL_TICKET,
                        new ChunkPos(origin.x + dx, origin.z + dz),
                        0,
                        tardisId);
            }
        }
    }

    // ─── Packet forwarding ─────────────────────────────────────────────────────

    /**
     * Called by the proxy for every packet the chunk manager delivers to it. Chunk-category
     * packets are dropped when they fall outside {@value #PORTAL_CHUNK_RADIUS} chunks of the
     * exterior; all other packet types pass through unconditionally.
     */
    private static void forwardIfInRange(ServerTardis tardis, BlockPos extPos, Packet<?> packet) {
        if (isChunkPacketOutOfRange(packet, extPos))
            return;
        if (shouldForward(packet))
            broadcast(tardis, packet);
    }

    /**
     * Returns {@code true} when {@code packet} is a chunk-type packet whose position lies
     * beyond {@value #PORTAL_CHUNK_RADIUS} chunks from {@code extPos}. Non-chunk packets
     * always return {@code false} and are never filtered by distance.
     */
    private static boolean isChunkPacketOutOfRange(Packet<?> packet, BlockPos extPos) {
        int originX = extPos.getX() >> 4;
        int originZ = extPos.getZ() >> 4;

        if (packet instanceof ChunkDataS2CPacket p)
            return outOfRange(p.getX(), p.getZ(), originX, originZ);

        if (packet instanceof ChunkDeltaUpdateS2CPacket p) {
            ChunkSectionPos sec = p.sectionPos;
            return outOfRange(sec.getSectionX(), sec.getSectionZ(), originX, originZ);
        }

        if (packet instanceof BlockUpdateS2CPacket p) {
            BlockPos bp = p.getPos();
            return outOfRange(bp.getX() >> 4, bp.getZ() >> 4, originX, originZ);
        }

        if (packet instanceof UnloadChunkS2CPacket p)
            return outOfRange(p.getX(), p.getZ(), originX, originZ);

        return false; // entity, particle, etc. are never range-filtered
    }

    private static boolean outOfRange(int chunkX, int chunkZ, int originX, int originZ) {
        return Math.abs(chunkX - originX) > PORTAL_CHUNK_RADIUS
                || Math.abs(chunkZ - originZ) > PORTAL_CHUNK_RADIUS;
    }

    /**
     * The subset of the proxy's packet stream that is meaningful to replay in the shadow
     * world. {@link EntityS2CPacket} is the abstract base for all relative-move/rotate
     * variants so one check covers all three.
     */
    private static boolean shouldForward(Packet<?> packet) {
        // The entity-spawn sequence (spawn + tracked data + equipment + passengers) is delivered as a single
        // BundleS2CPacket (see EntityTrackerEntry#startTracking). Without forwarding the bundle, the spawn never
        // reaches the shadow world, so the later move/track packets have no entity to apply to and nothing renders.
        // WrappedPacketS2CPacket already serialises bundles, and the client unwraps them and re-filters each
        // sub-packet through this same whitelist, so forwarding the whole bundle is safe.
        return packet instanceof BundleS2CPacket
                || packet instanceof ChunkDataS2CPacket
                || packet instanceof ChunkDeltaUpdateS2CPacket
                || packet instanceof BlockUpdateS2CPacket
                || packet instanceof UnloadChunkS2CPacket
                || packet instanceof EntitySpawnS2CPacket
                || packet instanceof PlayerSpawnS2CPacket
                || packet instanceof EntityPositionS2CPacket
                || packet instanceof EntityS2CPacket
                || packet instanceof EntityVelocityUpdateS2CPacket
                || packet instanceof EntitySetHeadYawS2CPacket
                || packet instanceof EntityTrackerUpdateS2CPacket
                || packet instanceof EntityEquipmentUpdateS2CPacket
                || packet instanceof EntitiesDestroyS2CPacket
                || packet instanceof ParticleS2CPacket;
    }

    // ─── Broadcast helpers ─────────────────────────────────────────────────────

    private static void broadcastInit(ServerTardis tardis, ServerWorld world) {
        RegistryKey<DimensionType> type = world.getDimensionEntry().getKey().orElse(DimensionTypes.OVERWORLD);
        sendToViewers(tardis, new PortalInitS2CPacket(tardis.getUuid(), world.getRegistryKey(), type));
    }

    private static void broadcastCenter(ServerTardis tardis, PacketProxyPlayer proxy) {
        ChunkPos center = proxy.getChunkPos();
        broadcast(tardis, new ChunkRenderDistanceCenterS2CPacket(center.x, center.z));
    }

    /** Always sent every refresh cycle — world time advances every tick so caching it is pointless. */
    private static void broadcastTime(ServerTardis tardis, ServerWorld world) {
        broadcast(tardis, new WorldTimeUpdateS2CPacket(
                world.getTime(),
                world.getTimeOfDay(),
                world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)));
    }

    /**
     * Sends rain/thunder gradient packets only when the value has moved by more than a
     * small threshold since the last send. Weather transitions are gradual (hundreds of
     * ticks), so most 20-tick cycles produce no change and no packet.
     */
    private static void maybeBroadcastWeather(ServerTardis tardis, ServerWorld world, ProxyEntry entry) {
        float rain    = world.getRainGradient(1.0f);
        float thunder = world.getThunderGradient(1.0f);
        if (Math.abs(rain - entry.lastRain) < 0.01f && Math.abs(thunder - entry.lastThunder) < 0.01f)
            return;
        entry.lastRain    = rain;
        entry.lastThunder = thunder;
        broadcastWeather(tardis, rain, thunder);
    }

    private static void broadcastWeather(ServerTardis tardis, float rain, float thunder) {
        broadcast(tardis, new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED,    rain));
        broadcast(tardis, new GameStateChangeS2CPacket(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED, thunder));
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

    // ─── Misc ──────────────────────────────────────────────────────────────────

    private static Set<UUID> viewerIds(ServerTardis tardis) {
        if (!tardis.hasWorld())
            return Set.of();
        Set<UUID> ids = new HashSet<>();
        for (ServerPlayerEntity player : tardis.world().getPlayers())
            ids.add(player.getUuid());
        return ids;
    }

    private void clearAll(MinecraftServer server) {
        for (ProxyEntry entry : PROXIES.values())
            despawn(entry);
        PROXIES.clear();
    }

    // ─── ProxyEntry ────────────────────────────────────────────────────────────

    private static final class ProxyEntry {

        /** TARDIS identity — needed to key and remove chunk-loading tickets. */
        final UUID tardisId;

        final PacketProxyPlayer proxy;

        /**
         * Stored directly rather than as a {@link RegistryKey}. This means
         * {@link BiggerOnTheInside#despawn} can always call {@code removePlayer} regardless of
         * whether the server is mid-shutdown or the dimension was dynamically unloaded.
         */
        final ServerWorld world;

        /**
         * Single-element mutable box shared with the proxy's packet-listener lambda.
         * Written by {@link BiggerOnTheInside#ensureProxy} when the exterior moves to a new
         * chunk so the in-flight range filter never uses a stale position snapshot.
         */
        final BlockPos[] posRef;

        BlockPos pos;
        Set<UUID> viewers;

        /** Last-sent weather values for change-detection. */
        float lastRain;
        float lastThunder;

        ProxyEntry(UUID tardisId, PacketProxyPlayer proxy, ServerWorld world,
                   BlockPos[] posRef, BlockPos pos, Set<UUID> viewers,
                   float lastRain, float lastThunder) {
            this.tardisId  = tardisId;
            this.proxy     = proxy;
            this.world     = world;
            this.posRef    = posRef;
            this.pos       = pos;
            this.viewers   = viewers;
            this.lastRain  = lastRain;
            this.lastThunder = lastThunder;
        }
    }
}
package dev.loqor.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import dev.amble.ait.AITMod;
import dev.drtheo.portal.PacketProxyPlayer;
import dev.drtheo.portal.PortalInitS2CPacket;
import dev.drtheo.portal.WrappedPacketS2CPacket;
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
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
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

public class BiggerOnTheInside implements ModInitializer {

    private static final ChunkTicketType<UUID> PORTAL_TICKET =
            ChunkTicketType.create("portal_proxy", UUID::compareTo);

    private static final int TICKING_RADIUS = 2;

    private static final Map<UUID, ProxyEntry> PROXIES = new HashMap<>();

    private static final Map<UUID, ProxyEntry> INTERIOR_PROXIES = new HashMap<>();

    private static final long REFRESH_INTERVAL = 5L;

    private static final long INTERIOR_GRACE_MS = 30_000L;

    private final Set<UUID> activeThisTick = new HashSet<>();
    private final List<UUID> staleIds      = new ArrayList<>();

    private long tickCounter = 0;

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::clearAll);
    }

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

        activeThisTick.clear();
        manager.forEach(tardis -> {
            if (ensureInteriorProxy(server, tardis))
                activeThisTick.add(tardis.getUuid());
        });

        staleIds.clear();
        for (UUID id : INTERIOR_PROXIES.keySet()) {
            if (!activeThisTick.contains(id))
                staleIds.add(id);
        }
        for (UUID id : staleIds)
            removeInteriorProxy(id);
    }

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

        if (!entry.world.getRegistryKey().equals(extWorld.getRegistryKey()) || newViewer) {
            despawn(entry);
            PROXIES.put(id, createProxy(tardis, extWorld, extPos, viewers));
            return true;
        }

        broadcastTime(tardis, extWorld);
        maybeBroadcastWeather(tardis, extWorld, entry);

        boolean movedChunk = entry.pos.getX() >> 4 != extPos.getX() >> 4
                || entry.pos.getZ() >> 4 != extPos.getZ() >> 4
                || entry.pos.getY() != extPos.getY();

        if (movedChunk) {
            removeChunkTickets(entry.world, entry.pos, id);

            entry.proxy.setPos(extPos.getX(), extPos.getY(), extPos.getZ());

            if (entry.world.isChunkLoaded(extPos.getX() >> 4, extPos.getZ() >> 4)) {
                entry.proxy.onChunkEntered();
            }

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

        BlockPos[] posRef = { pos };

        PacketProxyPlayer proxy = new PacketProxyPlayer(world);
        proxy.setPos(pos.getX(), pos.getY(), pos.getZ());
        proxy.setPacketListener(packet -> forwardIfInRange(tardis, posRef[0], packet));

        world.spawnEntity(proxy);
        // proxy.onChunkEntered();

        float rain    = world.getRainGradient(1.0f);
        float thunder = world.getThunderGradient(1.0f);

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

    private static void despawn(ProxyEntry entry) {
        removeChunkTickets(entry.world, entry.pos, entry.tardisId);
        entry.world.removePlayer(entry.proxy, Entity.RemovalReason.DISCARDED);
    }

    private static boolean keepDuringGrace(ProxyEntry entry) {
        return entry != null && System.currentTimeMillis() <= entry.graceDeadline;
    }

    private static boolean ensureInteriorProxy(MinecraftServer server, ServerTardis tardis) {
        UUID key = tardis.getUuid();
        ProxyEntry entry = INTERIOR_PROXIES.get(key);

        if (!tardis.travel().isLanded() || !tardis.door().isOpen())
            return keepDuringGrace(entry);

        List<ServerPlayerEntity> viewers = exteriorViewers(tardis);
        if (viewers.isEmpty())
            return keepDuringGrace(entry);

        ServerWorld interior = tardis.world();
        if (interior == null)
            return keepDuringGrace(entry);

        BlockPos doorPos     = tardis.getDesktop().getDoorPos().getPos();
        UUID portalId        = Portals.interiorId(key);
        Set<UUID> viewerIds  = idsOf(viewers);

        if (entry == null) {
            INTERIOR_PROXIES.put(key, createInteriorProxy(tardis, interior, doorPos, viewerIds));
            return true;
        }

        entry.graceDeadline = System.currentTimeMillis() + INTERIOR_GRACE_MS;

        boolean newViewer = !entry.viewers.containsAll(viewerIds);
        boolean dimChanged = !entry.world.getRegistryKey().equals(interior.getRegistryKey());

        boolean dirtyWhileUnviewed = entry.worldDirtyRef != null && entry.worldDirtyRef[0];

        if (dimChanged || newViewer || dirtyWhileUnviewed) {
            entry.viewers = viewerIds;
            despawn(entry);
            ProxyEntry rebuilt = createInteriorProxy(tardis, interior, doorPos, viewerIds);
            INTERIOR_PROXIES.put(key, rebuilt);
            return true;
        }
        entry.viewers = viewerIds;

        broadcastTime(portalId, viewers, interior);
        maybeBroadcastWeather(portalId, viewers, interior, entry);

        boolean movedChunk = entry.pos.getX() >> 4 != doorPos.getX() >> 4
                || entry.pos.getZ() >> 4 != doorPos.getZ() >> 4
                || entry.pos.getY() != doorPos.getY();

        if (movedChunk) {
            removeChunkTickets(entry.world, entry.pos, portalId);

            entry.proxy.setPos(doorPos.getX(), doorPos.getY(), doorPos.getZ());
            entry.proxy.onChunkEntered();
            entry.posRef[0] = doorPos;
            entry.pos = doorPos;

            addChunkTickets(interior, doorPos, portalId);
            broadcastCenter(portalId, viewers, entry.proxy);
        }

        return true;
    }

    private static ProxyEntry createInteriorProxy(ServerTardis tardis, ServerWorld interior,
                                                  BlockPos doorPos, Set<UUID> viewerIds) {
        UUID portalId = Portals.interiorId(tardis.getUuid());
        List<ServerPlayerEntity> viewers = exteriorViewers(tardis);

        broadcastInit(portalId, viewers, interior);
        addChunkTickets(interior, doorPos, portalId);

        BlockPos[] posRef = { doorPos };

        boolean[] dirtyRef = { false };

        PacketProxyPlayer proxy = new PacketProxyPlayer(interior);
        proxy.setPos(doorPos.getX(), doorPos.getY(), doorPos.getZ());
        proxy.setPacketListener(packet -> forwardInteriorIfInRange(portalId, tardis, posRef[0], dirtyRef, packet));

        interior.spawnEntity(proxy);
        // proxy.onChunkEntered();

        float rain    = interior.getRainGradient(1.0f);
        float thunder = interior.getThunderGradient(1.0f);

        broadcastTime(portalId, viewers, interior);
        broadcastWeather(portalId, viewers, rain, thunder);
        broadcastCenter(portalId, viewers, proxy);

        ProxyEntry entry = new ProxyEntry(portalId, proxy, interior, posRef, doorPos, viewerIds, rain, thunder);
        entry.graceDeadline = System.currentTimeMillis() + INTERIOR_GRACE_MS;
        entry.worldDirtyRef = dirtyRef;
        return entry;
    }

    private static void removeInteriorProxy(UUID key) {
        ProxyEntry entry = INTERIOR_PROXIES.remove(key);
        if (entry != null)
            despawn(entry);
    }

    private static List<ServerPlayerEntity> exteriorViewers(ServerTardis tardis) {
        CachedDirectedGlobalPos ext = tardis.travel().position();
        if (ext == null)
            return List.of();

        ServerWorld extWorld = ext.getWorld();
        if (extWorld == null)
            return List.of();

        BlockPos extPos = ext.getPos();
        double range = (AITMod.CONFIG.botiRenderDistance + 1) * 16.0;
        double rangeSq = range * range;

        List<ServerPlayerEntity> result = new ArrayList<>();
        for (ServerPlayerEntity player : extWorld.getPlayers()) {
            if (player instanceof PacketProxyPlayer)
                continue;
            if (player.getBlockPos().getSquaredDistance(extPos) <= rangeSq)
                result.add(player);
        }
        return result;
    }

    private static Set<UUID> idsOf(List<ServerPlayerEntity> players) {
        Set<UUID> ids = new HashSet<>();
        for (ServerPlayerEntity player : players)
            ids.add(player.getUuid());
        return ids;
    }

    private static void addChunkTickets(ServerWorld world, BlockPos center, UUID tardisId) {
        ChunkPos origin = new ChunkPos(center);
        for (int dx = -AITMod.CONFIG.botiRenderDistance; dx <= AITMod.CONFIG.botiRenderDistance; dx++) {
            for (int dz = -AITMod.CONFIG.botiRenderDistance; dz <= AITMod.CONFIG.botiRenderDistance; dz++) {
                world.getChunkManager().addTicket(
                        PORTAL_TICKET,
                        new ChunkPos(origin.x + dx, origin.z + dz),
                        TICKING_RADIUS,
                        tardisId);
            }
        }
    }

    private static void removeChunkTickets(ServerWorld world, BlockPos center, UUID tardisId) {
        ChunkPos origin = new ChunkPos(center);
        for (int dx = -AITMod.CONFIG.botiRenderDistance; dx <= AITMod.CONFIG.botiRenderDistance; dx++) {
            for (int dz = -AITMod.CONFIG.botiRenderDistance; dz <= AITMod.CONFIG.botiRenderDistance; dz++) {
                world.getChunkManager().removeTicket(
                        PORTAL_TICKET,
                        new ChunkPos(origin.x + dx, origin.z + dz),
                        TICKING_RADIUS,
                        tardisId);
            }
        }
    }

    private static void forwardIfInRange(ServerTardis tardis, BlockPos extPos, Packet<?> packet) {
        forwardIfInRange(tardis.getUuid(), () -> interiorViewers(tardis), extPos, packet);
    }

    private static void forwardIfInRange(UUID portalId, Supplier<List<ServerPlayerEntity>> viewers,
                                         BlockPos center, Packet<?> packet) {
        if (isChunkPacketOutOfRange(packet, center))
            return;
        if (shouldForward(packet))
            broadcast(portalId, viewers.get(), packet);
    }

    private static void forwardInteriorIfInRange(UUID portalId, ServerTardis tardis, BlockPos center,
                                                 boolean[] dirtyRef, Packet<?> packet) {
        if (isChunkPacketOutOfRange(packet, center))
            return;
        if (!shouldForward(packet))
            return;

        List<ServerPlayerEntity> viewers = exteriorViewers(tardis);
        if (viewers.isEmpty()) {
            if (isWorldChange(packet))
                dirtyRef[0] = true;
            return;
        }

        broadcast(portalId, viewers, packet);
    }

    private static boolean isWorldChange(Packet<?> packet) {
        return packet instanceof BlockUpdateS2CPacket
                || packet instanceof ChunkDeltaUpdateS2CPacket
                || packet instanceof ChunkDataS2CPacket
                || packet instanceof UnloadChunkS2CPacket;
    }

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

        return false;
    }

    private static boolean outOfRange(int chunkX, int chunkZ, int originX, int originZ) {
        return Math.abs(chunkX - originX) > AITMod.CONFIG.botiRenderDistance
                || Math.abs(chunkZ - originZ) > AITMod.CONFIG.botiRenderDistance;
    }

    private static boolean shouldForward(Packet<?> packet) {
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
                || packet instanceof EntityAnimationS2CPacket
                || packet instanceof EntityTrackerUpdateS2CPacket
                || packet instanceof EntityEquipmentUpdateS2CPacket
                || packet instanceof EntitiesDestroyS2CPacket
                || packet instanceof ParticleS2CPacket;
    }

    private static void broadcastInit(ServerTardis tardis, ServerWorld world) {
        broadcastInit(tardis.getUuid(), interiorViewers(tardis), world);
    }

    private static void broadcastCenter(ServerTardis tardis, PacketProxyPlayer proxy) {
        broadcastCenter(tardis.getUuid(), interiorViewers(tardis), proxy);
    }

    private static void broadcastTime(ServerTardis tardis, ServerWorld world) {
        broadcastTime(tardis.getUuid(), interiorViewers(tardis), world);
    }

    private static void maybeBroadcastWeather(ServerTardis tardis, ServerWorld world, ProxyEntry entry) {
        maybeBroadcastWeather(tardis.getUuid(), interiorViewers(tardis), world, entry);
    }

    private static void broadcastWeather(ServerTardis tardis, float rain, float thunder) {
        broadcastWeather(tardis.getUuid(), interiorViewers(tardis), rain, thunder);
    }

    private static void broadcastInit(UUID portalId, List<ServerPlayerEntity> targets, ServerWorld mirrored) {
        RegistryKey<DimensionType> type = mirrored.getDimensionEntry().getKey().orElse(DimensionTypes.OVERWORLD);
        send(targets, new PortalInitS2CPacket(portalId, mirrored.getRegistryKey(), type));
    }

    private static void broadcastCenter(UUID portalId, List<ServerPlayerEntity> targets, PacketProxyPlayer proxy) {
        ChunkPos center = proxy.getChunkPos();
        broadcast(portalId, targets, new ChunkRenderDistanceCenterS2CPacket(center.x, center.z));
    }

    private static void broadcastTime(UUID portalId, List<ServerPlayerEntity> targets, ServerWorld world) {
        broadcast(portalId, targets, new WorldTimeUpdateS2CPacket(
                world.getTime(),
                world.getTimeOfDay(),
                world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)));
    }

    private static void maybeBroadcastWeather(UUID portalId, List<ServerPlayerEntity> targets, ServerWorld world, ProxyEntry entry) {
        float rain    = world.getRainGradient(1.0f);
        float thunder = world.getThunderGradient(1.0f);
        if (Math.abs(rain - entry.lastRain) < 0.01f && Math.abs(thunder - entry.lastThunder) < 0.01f)
            return;
        entry.lastRain    = rain;
        entry.lastThunder = thunder;
        broadcastWeather(portalId, targets, rain, thunder);
    }

    private static void broadcastWeather(UUID portalId, List<ServerPlayerEntity> targets, float rain, float thunder) {
        broadcast(portalId, targets, new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED,    rain));
        broadcast(portalId, targets, new GameStateChangeS2CPacket(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED, thunder));
    }

    private static void broadcast(UUID portalId, List<ServerPlayerEntity> targets, Packet<?> packet) {
        send(targets, new WrappedPacketS2CPacket(portalId, packet));
    }

    private static void send(List<ServerPlayerEntity> targets, FabricPacket packet) {
        for (ServerPlayerEntity player : targets) {
            if (player instanceof PacketProxyPlayer)
                continue;
            ServerPlayNetworking.send(player, packet);
        }
    }

    private static List<ServerPlayerEntity> interiorViewers(ServerTardis tardis) {
        return tardis.hasWorld() ? tardis.world().getPlayers() : List.of();
    }

    private static Set<UUID> viewerIds(ServerTardis tardis) {
        if (!tardis.hasWorld())
            return Set.of();
        Set<UUID> ids = new HashSet<>();
        for (ServerPlayerEntity player : tardis.world().getPlayers()) {
            if (player instanceof PacketProxyPlayer)
                continue;
            ids.add(player.getUuid());
        }
        return ids;
    }

    private void clearAll(MinecraftServer server) {
        for (ProxyEntry entry : PROXIES.values())
            despawn(entry);
        PROXIES.clear();

        for (ProxyEntry entry : INTERIOR_PROXIES.values())
            despawn(entry);
        INTERIOR_PROXIES.clear();
    }

    private static final class ProxyEntry {

        final UUID tardisId;

        final PacketProxyPlayer proxy;

        final ServerWorld world;

        final BlockPos[] posRef;

        BlockPos pos;
        Set<UUID> viewers;

        float lastRain;
        float lastThunder;

        long graceDeadline;

        boolean[] worldDirtyRef;

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
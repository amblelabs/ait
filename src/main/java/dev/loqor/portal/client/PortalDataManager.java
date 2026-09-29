package dev.loqor.portal.client;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.boti.PortalParticleManager;
import dev.drtheo.portal.PortalInitS2CPacket;
import dev.drtheo.portal.WrappedPacketS2CPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.*;

public class PortalDataManager {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    // TODO: replace with array or intmap maybe
    private static final Map<UUID, PortalData> map = new HashMap<>();

    private static final Map<UUID, PortalParticleManager> particles = new HashMap<>();
    private static final Random random = Random.create();

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(WrappedPacketS2CPacket.TYPE, (wrapped, player, packetSender) -> {
            handle(wrapped);
        });

        ClientPlayNetworking.registerGlobalReceiver(PortalInitS2CPacket.TYPE, (packet, player, packetSender) -> {
            handleInit(packet.id(), packet.dimension(), packet.dimensionType());
        });

        ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, minecraftClient) -> {
            if (minecraftClient.isOnThread())
                reset();
            else
                minecraftClient.execute(PortalDataManager::reset);
        });

        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            long idleReclaimNanos = (long) (dev.amble.ait.client.AITModClient.CONFIG != null
                    ? dev.amble.ait.client.AITModClient.CONFIG.botiIdleReclaimSeconds : 10) * 1_000_000_000L;

            for (PortalData data : new ArrayList<>(map.values())) {
                step(data, "chunk updates", d -> d.world().runQueuedChunkUpdates());

                step(data, "clock", d -> d.world().tickTime());

                PortalParticleManager simManager = particles.computeIfAbsent(data.id(),
                        uuid -> new PortalParticleManager(data.world(), client));
                ParticleManager prevManager = client.particleManager;
                client.particleManager = simManager;
                try {
                    step(data, "block entities", d -> d.world().tickBlockEntities());
                    step(data, "entities", PortalData::tickEntities);
                } finally {
                    client.particleManager = prevManager;
                }

                step(data, "particles", PortalDataManager::spawnDisplayParticles);

                step(data, "geometry reclaim", d -> d.geometry().reclaimIfIdle(idleReclaimNanos));
            }

            for (PortalParticleManager manager : new ArrayList<>(particles.values())) {
                try {
                    manager.tick();
                } catch (Exception e) {
                    AITMod.LOGGER.error("BOTI: failed to tick portal particles", e);
                }
            }
        });
    }

    private static void step(PortalData data, String name, java.util.function.Consumer<PortalData> action) {
        try {
            action.accept(data);
        } catch (Exception e) {
            AITMod.LOGGER.error("BOTI: portal '{}' step failed", name, e);
        }
    }

    private static void spawnDisplayParticles(PortalData data) {
        BlockPos center = data.geometry().centerPos();
        if (center == null || center.equals(BlockPos.ORIGIN))
            return;

        net.minecraft.util.math.Vec3d eye = data.geometry().eyeWorldPos();
        int radius = Math.min(data.geometry().renderDistance(), 24);
        int cx, cy, cz;
        if (eye != null) {
            cx = (int) Math.floor(eye.x);
            cy = (int) Math.floor(eye.y);
            cz = (int) Math.floor(eye.z);
        } else {
            cx = center.getX();
            cy = center.getY();
            cz = center.getZ();
        }

        PortalParticleManager manager = particles.computeIfAbsent(data.id(),
                uuid -> new PortalParticleManager(data.world(), client));

        ParticleManager previous = client.particleManager;
        client.particleManager = manager;
        try {
            data.spawnDisplayParticles(cx, cy, cz, radius);
        } finally {
            client.particleManager = previous;
        }
    }

    public static void handleInit(UUID id, RegistryKey<World> dimension, RegistryKey<DimensionType> dimensionType) {
        if (!client.isOnThread()) {
            client.executeSync(() -> handleInit(id, dimension, dimensionType));
            return;
        }

        free(id);
        map.put(id, PortalData.create(id, dimension, dimensionType));
    }

    public static void reset() {
        for (PortalData data : map.values())
            data.close();

        map.clear();
        particles.clear();
    }

    public static void free(UUID id) {
        PortalData data = map.remove(id);
        if (data != null)
            data.close();

        particles.remove(id);
    }

    public static PortalParticleManager particles(UUID id) {
        return particles.get(id);
    }

    public static PortalData getOrCreate(UUID id) {
        return map.computeIfAbsent(id, uuid -> PortalData.fromCurrent(id));
    }

    public static PortalData get(UUID id) {
        return map.get(id);
    }

    private static void handle(WrappedPacketS2CPacket packet) {
        handle(packet.id(), packet.packet());
    }

    public static void handle(UUID id, Packet<?> packet) {
        if (!client.isOnThread()) {
            client.executeSync(() -> handle(id, packet));
            return;
        }

        try {
            PortalData data = handle0(id, packet);
            PortalEvents.UPDATE.invoker().onPortalUpdate(data);
        } catch (Exception var3) {
            AITMod.LOGGER.error("Failed to handle packet {}, suppressing error", packet, var3);
        }
    }

    private static PortalData handle0(UUID id, Packet<?> packet) {
        PortalData data = getOrCreate(id);

        if (packet instanceof BundleS2CPacket bundle) {
            for (Packet<?> otherPacket : bundle.getPackets()) {
                handle0(data, otherPacket);
            }

            return data;
        }

        handle0(data, packet);
        return data;
    }

    private static void handle0(PortalData data, Packet<?> packet) {
        if (packet instanceof ChunkRenderDistanceCenterS2CPacket render) {
            data.onChunkRenderDistanceCenter(render);
        } else if (packet instanceof WorldTimeUpdateS2CPacket time) {
            data.onWorldTime(time);
        } else if (packet instanceof ChunkDataS2CPacket chunkData) {
            data.onChunkData(chunkData);
        } else if (packet instanceof ChunkDeltaUpdateS2CPacket update) {
            data.onChunkDeltaUpdate(update);
        } else if (packet instanceof BlockUpdateS2CPacket update) {
            data.onBlockUpdate(update);
        } else if (packet instanceof UnloadChunkS2CPacket unload) {
            data.onUnloadChunk(unload);
        } else if (packet instanceof EntitySpawnS2CPacket spawn) {
            data.onEntitySpawn(spawn);
        } else if (packet instanceof PlayerSpawnS2CPacket spawn) {
            data.onPlayerSpawn(spawn);
        } else if (packet instanceof GameStateChangeS2CPacket state) {
            data.onGameStateChange(state);
        } else if (packet instanceof EntityPositionS2CPacket position) {
            data.onEntityPosition(position);
        } else if (packet instanceof EntityS2CPacket move) {
            data.onEntityMove(move);
        } else if (packet instanceof EntityVelocityUpdateS2CPacket velocity) {
            data.onEntityVelocity(velocity);
        } else if (packet instanceof EntitySetHeadYawS2CPacket headYaw) {
            data.onEntitySetHeadYaw(headYaw);
        } else if (packet instanceof EntityAnimationS2CPacket animation) {
            data.onEntityAnimation(animation);
        } else if (packet instanceof EntityTrackerUpdateS2CPacket tracker) {
            data.onEntityTrackerUpdate(tracker);
        } else if (packet instanceof EntityEquipmentUpdateS2CPacket equipment) {
            data.onEntityEquipment(equipment);
        } else if (packet instanceof EntitiesDestroyS2CPacket destroy) {
            data.onEntitiesDestroy(destroy);
        } else if (packet instanceof ParticleS2CPacket particle) {
            onParticle(data, particle);
        } else if (packet instanceof ChunkBiomeDataS2CPacket biome) {
//          this.onChunkBiomeData(biome); // - uncomment if it breaks everything
        }
    }

    private static void onParticle(PortalData data, ParticleS2CPacket packet) {
        PortalParticleManager manager = particles.computeIfAbsent(data.id(),
                uuid -> new PortalParticleManager(data.world(), client));

        if (packet.getCount() == 0) {
            double vx = packet.getSpeed() * packet.getOffsetX();
            double vy = packet.getSpeed() * packet.getOffsetY();
            double vz = packet.getSpeed() * packet.getOffsetZ();
            manager.addParticle(packet.getParameters(),
                    packet.getX(), packet.getY(), packet.getZ(), vx, vy, vz);
        } else {
            for (int i = 0; i < packet.getCount(); i++) {
                double ox = random.nextGaussian() * (double) packet.getOffsetX();
                double oy = random.nextGaussian() * (double) packet.getOffsetY();
                double oz = random.nextGaussian() * (double) packet.getOffsetZ();
                double vx = random.nextGaussian() * (double) packet.getSpeed();
                double vy = random.nextGaussian() * (double) packet.getSpeed();
                double vz = random.nextGaussian() * (double) packet.getSpeed();
                manager.addParticle(packet.getParameters(),
                        packet.getX() + ox, packet.getY() + oy, packet.getZ() + oz, vx, vy, vz);
            }
        }
    }
}
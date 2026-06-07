package dev.loqor.portal.client;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.boti.TardisDoorBOTI;
import dev.loqor.portal.PortalInitS2CPacket;
import dev.loqor.portal.WrappedPacketS2CPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.*;

public class PortalDataManager {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    // TODO: replace with array or intmap maybe
    private static final Map<UUID, PortalData> map = new HashMap<>();

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(WrappedPacketS2CPacket.TYPE, (wrapped, player, packetSender) -> {
            handle(wrapped);
        });

        ClientPlayNetworking.registerGlobalReceiver(PortalInitS2CPacket.TYPE, (packet, player, packetSender) -> {
            handleInit(packet.id(), packet.dimension(), packet.dimensionType());
        });

        ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, minecraftClient) -> {
            reset();
        });

        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            for (PortalData data : new ArrayList<>(map.values()))
                data.tickEntities();
        });
    }

    /** Rebuilds the shadow world for a TARDIS in the dimension the server says its exterior now occupies. */
    public static void handleInit(UUID id, RegistryKey<World> dimension, RegistryKey<DimensionType> dimensionType) {
        if (!client.isOnThread()) {
            client.executeSync(() -> handleInit(id, dimension, dimensionType));
            return;
        }

        free(id);
        map.put(id, PortalData.create(id, dimension, dimensionType));

        WorldGeometryRenderer renderer = TardisDoorBOTI.getInteriorRenderer();
        if (renderer != null)
            renderer.markDirty();
    }

    public static void reset() {
        map.clear();
    }

    public static void free(UUID id) {
        map.remove(id);
    }

    public static PortalData getOrCreate(UUID id) {
        // FIXED: Lambda ensures no signature mismatch if fromCurrent doesn't take a UUID
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
        // FIXED: Prevent NullPointerException when receiving packets for a new portal
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
        } else if (packet instanceof EntityPositionS2CPacket position) {
            data.onEntityPosition(position);
        } else if (packet instanceof EntityS2CPacket move) {
            data.onEntityMove(move);
        } else if (packet instanceof EntityVelocityUpdateS2CPacket velocity) {
            data.onEntityVelocity(velocity);
        } else if (packet instanceof EntitySetHeadYawS2CPacket headYaw) {
            data.onEntitySetHeadYaw(headYaw);
        } else if (packet instanceof EntityTrackerUpdateS2CPacket tracker) {
            data.onEntityTrackerUpdate(tracker);
        } else if (packet instanceof EntityEquipmentUpdateS2CPacket equipment) {
            data.onEntityEquipment(equipment);
        } else if (packet instanceof EntitiesDestroyS2CPacket destroy) {
            data.onEntitiesDestroy(destroy);
        } else if (packet instanceof ChunkBiomeDataS2CPacket biome) {
//          this.onChunkBiomeData(biome); // - uncomment if it breaks everything
        }
    }
}
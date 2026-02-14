package dev.loqor.portal.client;

import dev.amble.ait.AITMod;
import dev.loqor.portal.WrappedPacketS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;

import java.util.*;

public class PortalDataManager {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    // TODO: replace with array or intmap maybe
    private static final Map<UUID, PortalData> map = new HashMap<>();

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(WrappedPacketS2CPacket.TYPE, (wrapped, player, packetSender) -> {
            handle(wrapped);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, minecraftClient) -> {
            reset();
        });
    }

    public static void reset() {
        map.clear();
    }

    public static void free(UUID id) {
        map.remove(id);
    }

    public static PortalData getOrCreate(UUID id) {
        return map.computeIfAbsent(id, PortalData::fromCurrent);
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
        PortalData data = map.get(id);

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
        } else if (packet instanceof ChunkBiomeDataS2CPacket biome) {
//            this.onChunkBiomeData(biome); // - uncomment if it breaks everything
        }
    }
}

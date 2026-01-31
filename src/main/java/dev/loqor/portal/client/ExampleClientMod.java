package dev.loqor.portal.client;

import dev.amble.ait.client.boti.TardisDoorBOTI;
import dev.loqor.portal.WrappedPacketS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;

public class ExampleClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(WrappedPacketS2CPacket.TYPE, (wrapped, player, packetSender) -> {
            Packet<?> packet = wrapped.packet();
            PortalDataManager manager = PortalDataManager.get();

            handlePacket(packet, manager);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, minecraftClient) -> {
            TardisDoorBOTI.cleanup();
            PortalDataManager.get().reset();
        });
    }

    private static void handlePacket(Packet<?> packet, PortalDataManager manager) {
        if (packet instanceof BundleS2CPacket bundle) {
            for (Packet<?> otherPacket : bundle.getPackets()) {
                handlePacket(otherPacket, manager);
            }
        } else if (packet instanceof ChunkRenderDistanceCenterS2CPacket render) {
            manager.onChunkRenderDistanceCenter(render);
        } else if (packet instanceof ChunkDataS2CPacket data) {
            manager.onChunkData(data);
        } else if (packet instanceof ChunkDeltaUpdateS2CPacket update) {
            manager.onChunkDeltaUpdate(update);
        } else if (packet instanceof BlockUpdateS2CPacket update) {
            manager.onBlockUpdate(update);
        } else if (packet instanceof ChunkBiomeDataS2CPacket biome) {
            manager.onChunkBiomeData(biome);
        }

        WorldGeometryRenderer renderer = TardisDoorBOTI.getInteriorRenderer();

        if (renderer != null)
            MinecraftClient.getInstance().executeSync(renderer::markDirty);
    }
}

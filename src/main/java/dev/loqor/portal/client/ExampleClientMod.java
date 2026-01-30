package dev.loqor.portal.client;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.boti.TardisDoorBOTI;
import dev.loqor.portal.WrappedPacketS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionTypes;

public class ExampleClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(WrappedPacketS2CPacket.TYPE, (wrapped, player, packetSender) -> {
            Packet<?> packet = wrapped.packet();
            // AITMod.LOGGER.info("(client) got packet: {}", packet);
            WorldGeometryRenderer renderer = TardisDoorBOTI.getInteriorRenderer();

            if (renderer == null) return;

            if (packet instanceof BundleS2CPacket bundle) {
                for(Packet<?> packets : bundle.getPackets()) {
                    if (packets instanceof ChunkRenderDistanceCenterS2CPacket chunkRenderDistanceCenterS2CPacket) {
                        renderer.onChunkRenderDistanceCenter(chunkRenderDistanceCenterS2CPacket);
                    }

                    if (packets instanceof ChunkDataS2CPacket chunkDataS2CPacket) {

                        renderer.onChunkData(chunkDataS2CPacket);
                    }

                    if (packets instanceof ChunkDeltaUpdateS2CPacket chunkDeltaUpdateS2CPacket) {

                        renderer.onChunkDeltaUpdate(chunkDeltaUpdateS2CPacket);
                    }

                    if (packets instanceof BlockUpdateS2CPacket blockUpdateS2CPacket) {
                        renderer.onBlockUpdate(blockUpdateS2CPacket);
                    }

                    if (packets instanceof ChunkBiomeDataS2CPacket chunkBiomeDataS2CPacket) {
                        renderer.onChunkBiomeData(chunkBiomeDataS2CPacket);
                    }
                }
            }

            if (packet instanceof ChunkRenderDistanceCenterS2CPacket chunkRenderDistanceCenterS2CPacket) {
                renderer.onChunkRenderDistanceCenter(chunkRenderDistanceCenterS2CPacket);
            }

            if (packet instanceof ChunkDataS2CPacket chunkDataS2CPacket) {

                renderer.onChunkData(chunkDataS2CPacket);
            }

            if (packet instanceof ChunkDeltaUpdateS2CPacket chunkDeltaUpdateS2CPacket) {

                renderer.onChunkDeltaUpdate(chunkDeltaUpdateS2CPacket);
            }

            if (packet instanceof BlockUpdateS2CPacket blockUpdateS2CPacket) {
                renderer.onBlockUpdate(blockUpdateS2CPacket);
            }

            if (packet instanceof ChunkBiomeDataS2CPacket chunkBiomeDataS2CPacket) {
                renderer.onChunkBiomeData(chunkBiomeDataS2CPacket);
            }
        });
    }
}

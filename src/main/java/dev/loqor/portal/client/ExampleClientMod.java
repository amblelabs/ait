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
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
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

            WorldGeometryRenderer renderer = TardisDoorBOTI.getInteriorRenderer();

            if (renderer == null) return;

            if (packet instanceof ChunkDataS2CPacket chunkDataS2CPacket) {

                renderer.onChunkData(chunkDataS2CPacket);
            }

            if (packet instanceof ChunkDeltaUpdateS2CPacket chunkDeltaUpdateS2CPacket) {

                renderer.onChunkDeltaUpdate(chunkDeltaUpdateS2CPacket);
            }

            if (packet instanceof BlockUpdateS2CPacket blockUpdateS2CPacket) {
                renderer.onBlockUpdate(blockUpdateS2CPacket);
            }
        });
    }
}

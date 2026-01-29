package dev.loqor.portal.client;

import dev.amble.ait.AITMod;
import dev.loqor.portal.WrappedPacketS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.packet.Packet;

public class ExampleClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(WrappedPacketS2CPacket.TYPE, (wrapped, player, packetSender) -> {
            Packet<?> packet = wrapped.packet();
            AITMod.LOGGER.info("(client) got packet: {}", packet);
        });
    }
}

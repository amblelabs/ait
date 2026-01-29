package dev.loqor.portal;

import dev.amble.ait.AITMod;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class ExampleMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerWorld world = server.getOverworld();

            PacketProxyPlayer proxy = new PacketProxyPlayer(world);
            world.spawnEntity(proxy);

            proxy.setPacketListener(packet -> {
                AITMod.LOGGER.info("Proxied a packet: {}", packet);
                FabricPacket wrapped = new WrappedPacketS2CPacket(packet);

                for (ServerPlayerEntity player : world.getPlayers()) {
                    if (player instanceof PacketProxyPlayer) continue;

                    ServerPlayNetworking.send(player, wrapped);
                }
            });
        });
    }
}

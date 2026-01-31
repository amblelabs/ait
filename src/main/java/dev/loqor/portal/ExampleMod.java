package dev.loqor.portal;

import dev.amble.ait.AITMod;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.atomic.AtomicInteger;

public class ExampleMod implements ModInitializer {

    private static PacketProxyPlayer proxy;

    private static ServerWorld proxyWorld;
    private static ServerWorld playerWorld;

    private static PacketProxyPlayer setupProxy(ServerWorld world, BlockPos pos) {
        PacketProxyPlayer proxy = new PacketProxyPlayer(world);
        proxy.setPos(pos.getX(), pos.getY(), pos.getZ());
        world.spawnEntity(proxy);
//        proxy.onChunkEntered();

        return proxy;
    }

    @Override
    public void onInitialize() {
        ServerPlayConnectionEvents.JOIN.register((handler, packetSender, server) -> {
            playerWorld = handler.player.getServerWorld();
            proxyWorld = server.getOverworld();
            proxy = setupProxy(proxyWorld, new BlockPos(0, 0, 0));

            proxy.setPacketListener(packet -> {
                if (!(packet instanceof ChunkDataS2CPacket)
                        && !(packet instanceof ChunkDeltaUpdateS2CPacket)
                        && !(packet instanceof BlockUpdateS2CPacket))
                    return;

                if (proxyWorld == null) {
                    AITMod.LOGGER.warn("Couldn't proxy packet {} to a null world", packet.getClass());
                    return;
                }

                FabricPacket wrapped = new WrappedPacketS2CPacket(packet);

                for (ServerPlayerEntity player : playerWorld.getPlayers()) {
                    if (player instanceof PacketProxyPlayer) continue;

                    ServerPlayNetworking.send(player, wrapped);
                }
            });
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            proxyWorld.removePlayer(proxy, Entity.RemovalReason.DISCARDED);
            proxyWorld = null;
            proxy = null;

            playerWorld = null;
        });
    }
}

package dev.amble.ait.core.tardis.util.network.c2s;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.util.network.s2c.BOTIDataS2CPacket;

public class BOTIChunkRequestC2SPacket implements FabricPacket {
    public static final PacketType<BOTIChunkRequestC2SPacket> TYPE = PacketType.create(AITMod.id("request_chunk_data"), BOTIChunkRequestC2SPacket::new);
    private final BlockPos botiPos;
    private final RegistryKey<World> targetWorld;
    private final BlockPos targetPos;
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(botiPos);
        buf.writeRegistryKey(targetWorld);
        buf.writeBlockPos(targetPos);
    }

    public BOTIChunkRequestC2SPacket(PacketByteBuf buf) {
        this.botiPos = buf.readBlockPos();
        this.targetWorld = buf.readRegistryKey(RegistryKeys.WORLD);
        this.targetPos = buf.readBlockPos();
    }

    public BOTIChunkRequestC2SPacket(BlockPos botiPos, RegistryKey<World> targetWorld, BlockPos targetPos) {
        this.botiPos = botiPos;
        this.targetWorld = targetWorld;
        this.targetPos = targetPos;
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @SuppressWarnings("unchecked")
    public <T> boolean handle(ServerPlayerEntity source, PacketSender response) {
        if (source == null) return false;

        MinecraftServer server = source.getServer();

        if (server == null) return false;

        ServerWorld world = server.getWorld(this.targetWorld);

        if (world == null) return false;

        ChunkPos chunkPos = new ChunkPos(this.targetPos);
        world.getChunkManager().getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false);
        WorldChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
        ServerPlayNetworking.send(source, new BOTIDataS2CPacket(this.botiPos, chunk, this.targetPos));
        return true;
    }
}

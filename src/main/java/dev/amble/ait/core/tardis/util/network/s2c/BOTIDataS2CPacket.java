package dev.amble.ait.core.tardis.util.network.s2c;

import java.util.HashMap;
import java.util.Map;

import dev.amble.lib.util.ServerLifecycleHooks;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.boti.BOTIChunkVBO;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.Tardis;

public class BOTIDataS2CPacket implements FabricPacket {
    public static final PacketType<BOTIDataS2CPacket> TYPE = PacketType.create(AITMod.id("send_boti_data"), BOTIDataS2CPacket::new);

    private final BlockPos botiPos;
    public Map<BlockPos, BlockState> posStates = new HashMap<>();
    Map<BlockPos, NbtCompound> blockEntities = new HashMap<>();

    public BOTIDataS2CPacket(BlockPos botiPos, RegistryKey<World> key, BlockPos targetPos) {
        this.botiPos = botiPos;
        ServerWorld world = ServerLifecycleHooks.get().getWorld(key);
        try {
            int chunksToRender = BOTIChunkVBO.chunksToRender; // Should be 4
            int r = 16; // Chunk size in blocks
            int targetY = targetPos.getY();
            int baseY = targetY & ~15; // Align to chunk section boundary

            // Assuming chunksToRender = 4 means a 2x2 chunk grid
            int chunkRadius = chunksToRender / 2; // Should be 2 for 4 chunks
            ChunkPos centerChunkPos = new ChunkPos(targetPos);

            for (int chunkXOffset = -chunkRadius / 2; chunkXOffset < chunkRadius / 2; chunkXOffset++) {
                for (int chunkZOffset = -chunkRadius / 2; chunkZOffset < chunkRadius / 2; chunkZOffset++) {
                    // Calculate the chunk position for the current offset
                    ChunkPos chunkPos = new ChunkPos(centerChunkPos.x + chunkXOffset, centerChunkPos.z + chunkZOffset);

                    // Load the chunk
                    assert world != null;
                    world.getChunkManager().getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false);
                    Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
                    if (chunk == null) continue;

                    // Get the chunk section for the target Y level
                    int sectionIndex = chunk.getSectionIndex(targetY);
                    ChunkSection section = chunk.getSection(sectionIndex);
                    if (section == null) continue;

                    // Iterate over blocks within the chunk
                    for (int x = 0; x < r; x++) {
                        for (int y = 0; y < r; y++) {
                            for (int z = 0; z < r; z++) {
                                // Local position within the chunk
                                BlockState state = section.getBlockState(x, y, z);
                                if (state == null || state.isAir()) continue;
                                BlockPos localPos = new BlockPos(x + chunk.getPos().x * 16, y, z + chunk.getPos().z * 16);

                                // Store block state
                                if (!this.posStates.containsKey(localPos)) {

                                    this.posStates.put(localPos, state);
                                }

                                // World position for block entity lookup
                                BlockPos worldPos = new BlockPos(
                                        chunkPos.getStartX() + x,
                                        baseY + y,
                                        chunkPos.getStartZ() + z
                                );
                                BlockEntity be = chunk.getBlockEntity(worldPos);
                                if (be != null) {
                                    blockEntities.put(localPos, be.createNbtWithIdentifyingData());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Exception in packet construction: " + e.getMessage());
            AITMod.LOGGER.atTrace();
            System.out.println("Using fallback stone data due to serialization failure");
        }
    }


    public BOTIDataS2CPacket(PacketByteBuf buf) {
        this.botiPos = buf.readBlockPos();
        this.posStates = buf.readMap(
                PacketByteBuf::readBlockPos,
                buffer -> {
                    NbtCompound nbt = buffer.readNbt();
                    return BlockState.CODEC.parse(NbtOps.INSTANCE, nbt)
                            .result().orElse(Blocks.AIR.getDefaultState());
                }
        );
        this.blockEntities = buf.readMap(
                PacketByteBuf::readBlockPos,
                PacketByteBuf::readNbt
        );
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(botiPos);
        buf.writeMap(
                posStates,
                PacketByteBuf::writeBlockPos,
                (buffer, state) -> {
                    NbtCompound nbt = (NbtCompound) BlockState.CODEC.encodeStart(NbtOps.INSTANCE, state)
                            .result().orElse(new NbtCompound());
                    buffer.writeNbt(nbt);
                }
        );
        buf.writeMap(
                blockEntities,
                PacketByteBuf::writeBlockPos,
                PacketByteBuf::writeNbt
        );
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public <T> boolean handle(ClientPlayerEntity source, PacketSender response) {
        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;

        if (world == null) return false;

        BlockEntity exterior = world.getBlockEntity(this.botiPos);

        if (exterior instanceof ExteriorBlockEntity exteriorBlockEntity) {
            if (exteriorBlockEntity.tardis() == null) return false;
            Tardis tardis = exteriorBlockEntity.tardis().get();
            tardis.stats().updateMap(this.posStates);

            tardis.stats().updateChunkModel(exteriorBlockEntity);
        }
        return true;
    }
}
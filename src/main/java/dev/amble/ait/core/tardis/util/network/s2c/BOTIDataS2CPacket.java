package dev.amble.ait.core.tardis.util.network.s2c;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.Tardis;

public class BOTIDataS2CPacket implements FabricPacket {
    public static final PacketType<BOTIDataS2CPacket> TYPE = PacketType.create(AITMod.id("send_boti_data"), BOTIDataS2CPacket::new);

    private final BlockPos botiPos;
    public final NbtCompound chunkData;

    public BOTIDataS2CPacket(BlockPos botiPos, WorldChunk chunk, BlockPos targetPos) {
        this.botiPos = botiPos;
        this.chunkData = new NbtCompound();
        NbtCompound blockStates = new NbtCompound();
        NbtCompound blockEntities = new NbtCompound();
        World world = chunk.getWorld();
        int targetY = targetPos.getY();
        int baseY = targetY & ~15;
        int sectionIndex = chunk.getSectionIndex(targetY);
        ChunkSection section = chunk.getSection(sectionIndex);
        ChunkPos chunkPos = chunk.getPos();

        try {
            List<BlockState> paletteList = new ArrayList<>();
            Map<BlockState, Integer> stateToIndex = new HashMap<>();
            paletteList.add(Blocks.AIR.getDefaultState()); // Index 0 = air
            stateToIndex.put(Blocks.AIR.getDefaultState(), 0);
            BlockState[][][] sectionStates = new BlockState[16][16][16];
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        BlockState state = section.getBlockState(x, y, z);
                        sectionStates[x][y][z] = state;
                        if (state != null && !state.isAir() && !stateToIndex.containsKey(state)) {
                            stateToIndex.put(state, paletteList.size());
                            paletteList.add(state);
                        }
                    }
                }
            }

            // Build palette NBT
            NbtList palette = new NbtList();
            for (BlockState state : paletteList) {
                NbtCompound stateNbt = (NbtCompound) BlockState.CODEC.encodeStart(NbtOps.INSTANCE, state)
                        .result().orElseThrow(() -> new IllegalStateException("Failed to encode state " + state));
                palette.add(stateNbt);
            }

            int paletteSize = palette.size();
            int bitsPerEntry = Math.max(1, (int) Math.ceil(Math.log(paletteSize) / Math.log(2))); // Allow 1 bit for small palettes
            int entriesPerLong = 64 / bitsPerEntry;
            int dataLength = (int) Math.ceil(4096.0 / entriesPerLong); // 16x16x16 = 4096 entries

            // Build data array
            long[] data = new long[dataLength];
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        int index = y * 256 + z * 16 + x;
                        int longIndex = index / entriesPerLong;
                        int offset = (index % entriesPerLong) * bitsPerEntry;
                        BlockState state = sectionStates[x][y][z];
                        int paletteIndex = stateToIndex.getOrDefault(state, 0); // Default to air
                        data[longIndex] |= ((long) paletteIndex & ((1L << bitsPerEntry) - 1)) << offset;
                    }
                }
            }

            // Collect block entity data
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        BlockPos worldPos = new BlockPos(chunkPos.getStartX() + x, baseY + y, chunkPos.getStartZ() + z);
                        BlockEntity be = chunk.getBlockEntity(worldPos);
                        if (be != null) {
                            NbtCompound blockEntityNbt = be.createNbtWithIdentifyingData();
                            String key = x + "_" + y + "_" + z;
                            blockEntities.put(key, blockEntityNbt);
                        }
                    }
                }
            }

            blockStates.put("palette", palette);
            blockStates.putLongArray("data", data);
            blockStates.putInt("bitsPerEntry", bitsPerEntry);
            this.chunkData.put("block_states", blockStates);
            if (!blockEntities.isEmpty()) {
                this.chunkData.put("block_entities", blockEntities);
            }
        } catch (Exception e) {
            System.out.println("Exception in packet construction: " + e.getMessage());
            AITMod.LOGGER.atTrace();
            NbtList palette = new NbtList();
            palette.add(BlockState.CODEC.encodeStart(NbtOps.INSTANCE, Blocks.STONE.getDefaultState())
                    .result().orElseThrow(() -> new IllegalStateException("Failed to encode stone state")));
            long[] fullData = new long[256];
            java.util.Arrays.fill(fullData, 0);
            blockStates.put("palette", palette);
            blockStates.putLongArray("data", fullData);
            System.out.println("Using fallback stone data due to serialization failure");
            this.chunkData.put("block_states", blockStates);
        }
    }
    public BOTIDataS2CPacket(BlockPos botiPos, NbtCompound chunkData) {
        this.botiPos = botiPos;
        this.chunkData = chunkData;
    }
    public BOTIDataS2CPacket(PacketByteBuf buf) {
        this.botiPos = buf.readBlockPos();
        this.chunkData = buf.readNbt();
    }
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(botiPos);
        buf.writeNbt(chunkData);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @SuppressWarnings("unchecked")
    public <T> boolean handle(ClientPlayerEntity source, PacketSender response) {
        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;

        if (world == null) return false;

        BlockEntity exterior = world.getBlockEntity(this.botiPos);

        if (exterior instanceof ExteriorBlockEntity exteriorBlockEntity) {
            if (!exteriorBlockEntity.isLinked()) return false;
            Tardis tardis = exteriorBlockEntity.tardis().get();
            // tardis.stats().updateChunkModel(exteriorBlockEntity, this.chunkData);
        }
        return true;
    }
}

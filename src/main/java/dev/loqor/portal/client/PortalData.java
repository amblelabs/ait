package dev.loqor.portal.client;

import dev.amble.ait.client.boti.TardisDoorBOTI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.BitSet;
import java.util.Iterator;
import java.util.UUID;

public record PortalData(UUID id, WorldRenderer renderer, ClientWorld world) {

    public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet) {
        packet.visitUpdates(this::handleBlockUpdate);
    }

    public void onBlockUpdate(BlockUpdateS2CPacket packet) {
        handleBlockUpdate(packet.getPos(), packet.getState());
    }

    private void handleBlockUpdate(BlockPos pos, BlockState state) {
        this.world.handleBlockUpdate(pos, state, Block.FORCE_STATE | Block.NOTIFY_LISTENERS | Block.NOTIFY_NEIGHBORS);
    }

    public void onChunkData(ChunkDataS2CPacket chunkDataS2CPacket) {
        int i = chunkDataS2CPacket.getX();
        int j = chunkDataS2CPacket.getZ();

        this.world.getChunkManager().setChunkMapCenter(i, j);

        this.loadChunk(i, j, chunkDataS2CPacket.getChunkData());
        LightData lightData = chunkDataS2CPacket.getLightData();

        this.world.enqueueChunkUpdate(() -> {
            this.readLightData(i, j, lightData);
            WorldChunk worldChunk = this.world.getChunkManager().getWorldChunk(i, j, false);
            if (worldChunk != null) {
                this.scheduleRenderChunk(worldChunk, i, j);
            }
        });
    }

    private void readLightData(int x, int z, LightData data) {
        LightingProvider lightingProvider = this.world.getChunkManager().getLightingProvider();
        BitSet bitSet = data.getInitedSky();
        BitSet bitSet2 = data.getUninitedSky();
        Iterator<byte[]> iterator = data.getSkyNibbles().iterator();
        this.updateLighting(x, z, lightingProvider, LightType.SKY, bitSet, bitSet2, iterator);
        BitSet bitSet3 = data.getInitedBlock();
        BitSet bitSet4 = data.getUninitedBlock();
        Iterator<byte[]> iterator2 = data.getBlockNibbles().iterator();
        this.updateLighting(x, z, lightingProvider, LightType.BLOCK, bitSet3, bitSet4, iterator2);
        lightingProvider.setColumnEnabled(new ChunkPos(x, z), true);
    }

    private void updateLighting(int chunkX, int chunkZ, LightingProvider provider, LightType type, BitSet inited, BitSet uninited, Iterator<byte[]> nibbles) {
        for (int i = 0; i < provider.getHeight(); ++i) {
            int j = provider.getBottomY() + i;
            boolean bl = inited.get(i);
            boolean bl2 = uninited.get(i);
            if (!bl && !bl2) continue;
            provider.enqueueSectionData(type, ChunkSectionPos.from(chunkX, j, chunkZ), bl ? new ChunkNibbleArray(nibbles.next().clone()) : new ChunkNibbleArray());
//            this.world.scheduleBlockRenders(chunkX, j, chunkZ);
            TardisDoorBOTI.getInteriorRenderer().markDirty();
        }
    }

    private void loadChunk(int x, int z, ChunkData chunkData) {
        this.world.getChunkManager().loadChunkFromPacket(x, z, chunkData.getSectionsDataBuf(), chunkData.getHeightmap(), chunkData.getBlockEntities(x, z));
    }

    public void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket packet) {
        this.world.getChunkManager().setChunkMapCenter(packet.getChunkX(), packet.getChunkZ());
    }

    public void onChunkBiomeData(ChunkBiomeDataS2CPacket packet) {
        for (ChunkBiomeDataS2CPacket.Serialized serialized : packet.chunkBiomeData()) {
            this.world.getChunkManager().onChunkBiomeData(serialized.pos().x, serialized.pos().z, serialized.toReadingBuf());
        }
        for (ChunkBiomeDataS2CPacket.Serialized serialized : packet.chunkBiomeData()) {
            this.world.resetChunkColor(new ChunkPos(serialized.pos().x, serialized.pos().z));
        }
        for (ChunkBiomeDataS2CPacket.Serialized serialized : packet.chunkBiomeData()) {
            for (int i = -1; i <= 1; ++i) {
                for (int j = -1; j <= 1; ++j) {
                    for (int k = this.world.getBottomSectionCoord(); k < this.world.getTopSectionCoord(); ++k) {
                        this.renderer.scheduleBlockRender(serialized.pos().x + i, k, serialized.pos().z + j);
                    }
                }
            }
        }
    }

    private void scheduleRenderChunk(WorldChunk chunk, int x, int z) {
        LightingProvider lightingProvider = this.world.getChunkManager().getLightingProvider();
        ChunkSection[] chunkSections = chunk.getSectionArray();
        ChunkPos chunkPos = chunk.getPos();
        for (int i = 0; i < chunkSections.length; ++i) {
            ChunkSection chunkSection = chunkSections[i];
            int j = this.world.sectionIndexToCoord(i);
            lightingProvider.setSectionStatus(ChunkSectionPos.from(chunkPos, j), chunkSection.isEmpty());
//            this.world.scheduleBlockRenders(x, j, z);
            TardisDoorBOTI.getInteriorRenderer().markDirty();
        }
    }

    public void onUnloadChunk(UnloadChunkS2CPacket packet) {
        this.world.getChunkManager().unload(packet.getX(), packet.getZ());
        markRendererDirty();
    }

    private static void markRendererDirty() {
        WorldGeometryRenderer renderer = TardisDoorBOTI.getInteriorRenderer();
        if (renderer != null)
            renderer.markDirty();
    }

    public static PortalData fromCurrent(UUID id) {
        ClientWorld old = MinecraftClient.getInstance().world;
        RegistryKey<DimensionType> type = old.getDimensionEntry().getKey().orElse(DimensionTypes.OVERWORLD);

        return create(id, old.getRegistryKey(), type);
    }

    /**
     * Builds a shadow world mirroring the given dimension. The server tells us which dimension a TARDIS's
     * exterior is in (see {@link dev.loqor.portal.PortalInitS2CPacket}) so the doorway renders with the correct
     * lighting, sky and height limits instead of the interior dimension's.
     */
    public static PortalData create(UUID id, RegistryKey<World> dimension, RegistryKey<DimensionType> dimensionType) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld old = client.world;

        RegistryEntry<DimensionType> typeEntry = old.getRegistryManager()
                .get(RegistryKeys.DIMENSION_TYPE).entryOf(dimensionType);

        WorldRenderer worldRenderer = new WorldRenderer(
                client,
                client.getEntityRenderDispatcher(),
                client.getBlockEntityRenderDispatcher(),
                client.getBufferBuilders()
        );

        ClientWorld world = new ClientWorld(client.getNetworkHandler(), new ClientWorld.Properties(Difficulty.NORMAL,
                false, false), dimension,
                typeEntry,
                12, old.getSimulationDistance(), client::getProfiler, worldRenderer,
                old.isDebugWorld(), old.getBiomeAccess().seed);

        worldRenderer.setWorld(world);

        return new PortalData(id, worldRenderer, world);
    }
}

package dev.loqor.portal.client;

import dev.amble.ait.AITMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;

import java.util.BitSet;
import java.util.Iterator;

public class PortalDataManager {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static PortalDataManager instance;

    private final ClientWorld world;
    public final WorldRenderer worldRenderer;

    public void reset() {
        instance = null;
    }

    private PortalDataManager(ClientWorld world, WorldRenderer worldRenderer) {
        this.world = world;
        this.worldRenderer = worldRenderer;
        // Don't set center here - let subclasses handle it
        // this.onChunkRenderDistanceCenter(new ChunkRenderDistanceCenterS2CPacket(0, 0));
    }

    protected PortalDataManager(ClientWorld world, WorldRenderer worldRenderer, boolean initCenter) {
        this.world = world;
        this.worldRenderer = worldRenderer;
        if (initCenter) {
            this.onChunkRenderDistanceCenter(new ChunkRenderDistanceCenterS2CPacket(0, 0));
        }
    }

    public static PortalDataManager get() {
        if (instance != null) return instance;

        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld oldWorld = client.world;

        if (oldWorld == null) throw new IllegalStateException("Couldn't initialize a fake client world");

        WorldRenderer worldRenderer = new WorldRenderer(
                client,
                client.getEntityRenderDispatcher(),
                client.getBlockEntityRenderDispatcher(),
                client.getBufferBuilders()
        );

        ClientWorld world = new ClientWorld(client.getNetworkHandler(),  new ClientWorld.Properties(Difficulty.NORMAL,
                false, false), oldWorld.getRegistryKey(),
                oldWorld.getDimensionEntry(),
                12, client.world.getSimulationDistance(), client::getProfiler, worldRenderer,
                client.world.isDebugWorld(), client.world.getBiomeAccess().seed);

        worldRenderer.setWorld(world);

        return instance = new PortalDataManager(world, worldRenderer, true);
    }

    public ClientWorld world() {
        return world;
    }

    public void handle(Packet<?> packet) {
        if (!client.isOnThread()) {
            client.executeSync(() -> this.handle1(packet));
            return;
        }

        this.handle1(packet);
    }

    private void handle1(Packet<?> packet) {
        try {
            this.handle0(packet);
        } catch (Exception var3) {
            AITMod.LOGGER.error("Failed to handle packet {}, suppressing error", packet, var3);
        }
    }

    private void handle0(Packet<?> packet) {
        if (packet instanceof BundleS2CPacket bundle) {
            for (Packet<?> otherPacket : bundle.getPackets()) {
                this.handle0(otherPacket);
            }
        } else if (packet instanceof ChunkRenderDistanceCenterS2CPacket render) {
            this.onChunkRenderDistanceCenter(render);
        } else if (packet instanceof ChunkDataS2CPacket data) {
            this.onChunkData(data);
        } else if (packet instanceof ChunkDeltaUpdateS2CPacket update) {
            this.onChunkDeltaUpdate(update);
        } else if (packet instanceof BlockUpdateS2CPacket update) {
            this.onBlockUpdate(update);
        } else if (packet instanceof ChunkBiomeDataS2CPacket biome) {
//            this.onChunkBiomeData(biome); // - uncomment if it breaks everything
        }
    }

    private void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet) {
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
            this.world.scheduleBlockRenders(chunkX, j, chunkZ);
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
                        this.worldRenderer.scheduleBlockRender(serialized.pos().x + i, k, serialized.pos().z + j);
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
            this.world.scheduleBlockRenders(x, j, z);
        }
    }
}

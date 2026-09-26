package dev.loqor.portal.client;

import com.mojang.datafixers.util.Pair;

import dev.amble.ait.AITMod;
import dev.drtheo.portal.PortalInitS2CPacket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TrackedPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public record PortalData(UUID id, WorldRenderer renderer, ClientWorld world, WorldGeometryRenderer geometry) {

    private static int renderDistanceBlocks() {
        return AITMod.CONFIG.botiRenderDistance * 16;
    }

    public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet) {
        packet.visitUpdates(this::handleBlockUpdate);
    }

    public void onBlockUpdate(BlockUpdateS2CPacket packet) {
        handleBlockUpdate(packet.getPos(), packet.getState());
    }

    private void handleBlockUpdate(BlockPos pos, BlockState state) {
        this.world.handleBlockUpdate(pos, state, Block.FORCE_STATE | Block.NOTIFY_LISTENERS | Block.NOTIFY_NEIGHBORS);
        markSectionsDirty(pos);
    }

    private void markSectionsDirty(BlockPos pos) {
        WorldGeometryRenderer renderer = this.geometry;

        int sectionX = pos.getX() >> 4;
        int sectionY = pos.getY() >> 4;
        int sectionZ = pos.getZ() >> 4;

        int localX = pos.getX() & 15;
        int localY = pos.getY() & 15;
        int localZ = pos.getZ() & 15;

        for (int dx = (localX == 0 ? -1 : 0); dx <= (localX == 15 ? 1 : 0); dx++)
            for (int dy = (localY == 0 ? -1 : 0); dy <= (localY == 15 ? 1 : 0); dy++)
                for (int dz = (localZ == 0 ? -1 : 0); dz <= (localZ == 15 ? 1 : 0); dz++)
                    renderer.markSectionDirty(ChunkSectionPos.from(sectionX + dx, sectionY + dy, sectionZ + dz));
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
        WorldGeometryRenderer renderer = this.geometry;

        for (int i = 0; i < provider.getHeight(); ++i) {
            int j = provider.getBottomY() + i;
            boolean bl = inited.get(i);
            boolean bl2 = uninited.get(i);
            if (!bl && !bl2) continue;
            provider.enqueueSectionData(type, ChunkSectionPos.from(chunkX, j, chunkZ), bl ? new ChunkNibbleArray(nibbles.next().clone()) : new ChunkNibbleArray());

            if (renderer != null)
                renderer.markSectionDirty(ChunkSectionPos.from(chunkX, j, chunkZ));
        }
    }

    private void loadChunk(int x, int z, ChunkData chunkData) {
        this.world.getChunkManager().loadChunkFromPacket(x, z, chunkData.getSectionsDataBuf(), chunkData.getHeightmap(), chunkData.getBlockEntities(x, z));
    }

    public void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket packet) {
        this.world.getChunkManager().setChunkMapCenter(packet.getChunkX(), packet.getChunkZ());
    }

    public void onWorldTime(WorldTimeUpdateS2CPacket packet) {
        this.world.setTime(packet.getTime());
        this.world.setTimeOfDay(packet.getTimeOfDay());
    }

    public void onGameStateChange(GameStateChangeS2CPacket packet) {
        GameStateChangeS2CPacket.Reason reason = packet.getReason();
        float value = packet.getValue();

        if (reason == GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED)
            this.world.setRainGradient(value);
        else if (reason == GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED)
            this.world.setThunderGradient(value);
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
        WorldGeometryRenderer renderer = this.geometry;

        for (int i = 0; i < chunkSections.length; ++i) {
            ChunkSection chunkSection = chunkSections[i];
            int j = this.world.sectionIndexToCoord(i);
            lightingProvider.setSectionStatus(ChunkSectionPos.from(chunkPos, j), chunkSection.isEmpty());

            if (renderer != null)
                renderer.markSectionDirty(ChunkSectionPos.from(chunkPos, j));
        }
    }

    public void onUnloadChunk(UnloadChunkS2CPacket packet) {
        this.world.getChunkManager().unload(packet.getX(), packet.getZ());

        WorldGeometryRenderer renderer = this.geometry;
        if (renderer == null)
            return;

        for (int y = this.world.getBottomSectionCoord(); y < this.world.getTopSectionCoord(); y++)
            renderer.dropSection(ChunkSectionPos.from(packet.getX(), y, packet.getZ()));
    }

    public void onEntitySpawn(EntitySpawnS2CPacket packet) {
        EntityType<?> type = packet.getEntityType();
        Entity entity = type.create(this.world);

        if (entity == null)
            return;

        entity.onSpawnPacket(packet);
        this.world.addEntity(packet.getId(), entity);
    }

    public void onPlayerSpawn(PlayerSpawnS2CPacket packet) {
        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
        if (handler == null)
            return;

        PlayerListEntry entry = handler.getPlayerListEntry(packet.getPlayerUuid());
        if (entry == null)
            return;

        OtherClientPlayerEntity player = new OtherClientPlayerEntity(this.world, entry.getProfile());
        int id = packet.getId();
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        float yaw = packet.getYaw() * 360 / 256.0F;
        float pitch = packet.getPitch() * 360 / 256.0F;

        player.setId(id);
        player.updateTrackedPosition(x, y, z);
        player.updatePositionAndAngles(x, y, z, yaw, pitch);
        player.setHeadYaw(yaw);
        player.setBodyYaw(yaw);
        this.world.addEntity(id, player);
    }

    public void onEntityPosition(EntityPositionS2CPacket packet) {
        Entity entity = this.world.getEntityById(packet.getId());
        if (entity == null)
            return;

        Vec3d pos = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
        entity.getTrackedPosition().setPos(pos);
        entity.updateTrackedPositionAndAngles(pos.x, pos.y, pos.z,
                packet.getYaw() * 360 / 256.0F, packet.getPitch() * 360 / 256.0F, 3, true);
        entity.setOnGround(packet.isOnGround());
    }

    public void onEntityMove(EntityS2CPacket packet) {
        Entity entity = packet.getEntity(this.world);
        if (entity == null)
            return;

        if (packet.isPositionChanged()) {
            TrackedPosition tracked = entity.getTrackedPosition();
            Vec3d pos = tracked.withDelta(packet.getDeltaX(), packet.getDeltaY(), packet.getDeltaZ());
            tracked.setPos(pos);

            float yaw = packet.hasRotation() ? packet.getYaw() * 360 / 256.0F : entity.getYaw();
            float pitch = packet.hasRotation() ? packet.getPitch() * 360 / 256.0F : entity.getPitch();
            entity.updateTrackedPositionAndAngles(pos.x, pos.y, pos.z, yaw, pitch, 3, false);
        } else if (packet.hasRotation()) {
            entity.updateTrackedPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(),
                    packet.getYaw() * 360 / 256.0F, packet.getPitch() * 360 / 256.0F, 3, false);
        }

        entity.setOnGround(packet.isOnGround());
    }

    public void onEntityVelocity(EntityVelocityUpdateS2CPacket packet) {
        Entity entity = this.world.getEntityById(packet.getId());
        if (entity == null)
            return;

        entity.setVelocityClient(packet.getVelocityX() / 8000.0,
                packet.getVelocityY() / 8000.0, packet.getVelocityZ() / 8000.0);
    }

    public void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket packet) {
        Entity entity = packet.getEntity(this.world);
        if (entity == null)
            return;

        entity.updateTrackedHeadRotation(packet.getHeadYaw() * 360 / 256.0F, 3);
    }

    public void onEntityAnimation(EntityAnimationS2CPacket packet) {
        if (!(this.world.getEntityById(packet.getId()) instanceof LivingEntity living))
            return;

        if (packet.getAnimationId() == EntityAnimationS2CPacket.SWING_MAIN_HAND)
            living.swingHand(Hand.MAIN_HAND);
        else if (packet.getAnimationId() == EntityAnimationS2CPacket.SWING_OFF_HAND)
            living.swingHand(Hand.OFF_HAND);
        else if (packet.getAnimationId() == EntityAnimationS2CPacket.WAKE_UP && living instanceof PlayerEntity player)
            player.wakeUp(false, false);
    }

    public void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet) {
        Entity entity = this.world.getEntityById(packet.id());

        if (entity != null && packet.trackedValues() != null)
            entity.getDataTracker().writeUpdatedEntries(packet.trackedValues());
    }

    public void onEntityEquipment(EntityEquipmentUpdateS2CPacket packet) {
        if (this.world.getEntityById(packet.getId()) instanceof LivingEntity living) {
            for (Pair<EquipmentSlot, ItemStack> pair : packet.getEquipmentList())
                living.equipStack(pair.getFirst(), pair.getSecond());
        }
    }

    public void onEntitiesDestroy(EntitiesDestroyS2CPacket packet) {
        for (int i = 0; i < packet.getEntityIds().size(); i++) {
            int id = packet.getEntityIds().getInt(i);
            Entity entity = this.world.getEntityById(id);

            if (entity != null)
                this.world.removeEntity(id, Entity.RemovalReason.DISCARDED);
        }
    }

    public void spawnDisplayParticles(int centerX, int centerY, int centerZ, int radius) {
        Random random = this.world.getRandom();
        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int i = 0; i < 667; i++) {
            int x = centerX + random.nextInt(radius) - random.nextInt(radius);
            int y = centerY + random.nextInt(radius) - random.nextInt(radius);
            int z = centerZ + random.nextInt(radius) - random.nextInt(radius);
            pos.set(x, y, z);

            try {
                BlockState state = this.world.getBlockState(pos);
                if (!state.isAir())
                    state.getBlock().randomDisplayTick(state, this.world, pos, random);

                FluidState fluid = state.getFluidState();
                if (!fluid.isEmpty())
                    fluid.randomDisplayTick(this.world, pos, random);
            } catch (Exception ignored) {
            }
        }
    }

    public void tickEntities() {
        List<Entity> snapshot = new ArrayList<>();
        this.world.getEntities().forEach(snapshot::add);

        for (Entity entity : snapshot) {
            if (entity == null || entity.isRemoved())
                continue;

            if (entity.hasVehicle())
                continue;

            try {
                this.world.tickEntity(entity);
            } catch (Throwable t) {
                AITMod.LOGGER.error("BOTI: failed to tick shadow entity {}", entity, t);
            }
        }
    }

    public void close() {
        try {
            this.geometry.close();
            this.renderer.setWorld(null);
            this.renderer.close();
        } catch (Exception e) {
            AITMod.LOGGER.error("Failed to close shadow world for portal {}", this.id, e);
        }
    }

    public static PortalData fromCurrent(UUID id) {
        ClientWorld old = MinecraftClient.getInstance().world;
        RegistryKey<DimensionType> type = old.getDimensionEntry().getKey().orElse(DimensionTypes.OVERWORLD);

        return create(id, old.getRegistryKey(), type);
    }

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

        WorldGeometryRenderer geometry = new WorldGeometryRenderer(renderDistanceBlocks());

        return new PortalData(id, worldRenderer, world, geometry);
    }
}

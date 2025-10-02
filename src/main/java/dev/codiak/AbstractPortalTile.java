/* (C) TAMA Studios 2025 */
package dev.codiak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.tardis.util.TardisUtil;

/**
 * Other tiles implement this to get data for portals
 **/
@Environment(EnvType.CLIENT)
public abstract class AbstractPortalTile extends InteriorLinkableBlockEntity {
    public AbstractPortalTile(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public static <T extends BlockEntity> void tick(World level, BlockPos blockPos, BlockState state, T tile) {
        ((AbstractPortalTile) tile).tick();
    }

    public VertexBuffer MODEL_VBO;

    public Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();

    public Map<BakedModel, Integer> chunkModels = new HashMap<>();

    public List<BotiChunkContainer> containers = new ArrayList<>();

    public long lastRequestTime = 0;

    public long lastUpdateTime = 0;

    public RegistryKey<World> getTargetWorld() {
        return targetWorld;
    }

    public BlockPos getTargetPos() {
        return targetPos;
    }

    public RegistryKey<World> targetWorld;

    public BlockPos targetPos;

    private final List<Integer> recievedPackets = new ArrayList<>();

    @Environment(EnvType.CLIENT)
    public void updateChunkDataFromServer(List<BotiChunkContainer> chunkData, int packetIndex, int totalPackets) {
        if (packetIndex > totalPackets || this.recievedPackets.contains(packetIndex)) {
            AITMod.LOGGER.warn("Portal received packet not meant for it, or it's updating too quickly... ruh roh");
            return;
        } else recievedPackets.add(packetIndex);

        chunkData.forEach(container -> {
            if (container.IsTile) {
                BlockEntity entity =
                        BlockEntity.createFromNbt(container.pos, container.state, container.entityTag);
                blockEntities.put(container.pos, entity);
                containers.remove(container);
            }
        });
        containers.addAll(chunkData);

        if (recievedPackets.size() >= totalPackets) { // If we've got all the packets
            this.recievedPackets.clear();
            this.MODEL_VBO = BOTIUtils.buildModelVBO(this.containers);
        }
    }

    public void setTargetWorld(RegistryKey<World> levelKey, BlockPos targetPos, boolean markDirty) {
        if (this.world == null) return;
        this.targetWorld = levelKey;
        this.targetPos = targetPos;
        chunkModels.clear();
        blockEntities.clear();
        if (markDirty && !world.isClient()) {
            this.markDirty();;
            world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(pos);
            buf.writeIdentifier(targetWorld.getValue());
            buf.writeBlockPos(targetPos);

            for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, this.pos)) {
                ServerPlayNetworking.send(player, TardisUtil.PORTAL_SYNC_S2C, buf);
            }
        }
    }

    public void tick() {
        if (this.targetWorld != null || this.targetPos != null) return;
        assert this.world != null;
        this.setTargetWorld(
                        this.tardis().get().travel().destination().getWorld().getRegistryKey(), this.tardis().get().travel().destination().getPos(), true);
    }


}

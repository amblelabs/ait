/* (C) TAMA Studios 2025 */
package dev.codiak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.amble.lib.data.CachedDirectedGlobalPos;
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
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.util.TardisUtil;

/**
 * Other blockEntities implement this to get data for portals
 * @author Codiak540, Loqor
 **/
@Environment(EnvType.CLIENT)
public abstract class AbstractPortalBlockEntity extends InteriorLinkableBlockEntity {
    public AbstractPortalBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public static <T extends BlockEntity> void tick(World level, BlockPos blockPos, BlockState state, T blockEntity) {
        ((AbstractPortalBlockEntity) blockEntity).tick();
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

    private final List<Integer> receivedPackets = new ArrayList<>();

    @Environment(EnvType.CLIENT)
    public void updateChunkDataFromServer(List<BotiChunkContainer> chunkData, int packetIndex, int totalPackets) {

        if (packetIndex > totalPackets || this.receivedPackets.contains(packetIndex)) {

            AITMod.LOGGER.warn("Portal received packet not meant for it! {}", this.receivedPackets);

            return;
        } else {
            receivedPackets.add(packetIndex);
        }

        chunkData.forEach(container -> {
            if (container.isBlockEntity) {
                BlockEntity entity = BlockEntity.createFromNbt(container.pos, container.state, container.entityTag);

                blockEntities.put(container.pos, entity);
                containers.remove(container);
            }
        });

        containers.addAll(chunkData);

        if (receivedPackets.size() >= totalPackets) { // If we've got all the packets

            this.receivedPackets.clear();
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

            this.markDirty();
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

    // This could cause potential issues during flight. - Loqor
    public void tick() {

        // TODO Stupid fucking packets causing issues. None of the BOTI stuff will do anything until this is fixed.
        // TODO This is what causes a black screen - the rendering doesn't, and I've tested it plenty. This is what causes the problem. - Loqor
        /*if (this.getWorld() != null && this.getWorld().isClient()) {
            MinecraftClient.getInstance().execute(() -> {
                BOTIUtils.updateMe(this);
            });
        }*/
        if (this.targetWorld != null || this.targetPos != null) return;

        assert this.world != null;

        if (this.tardis() == null) return;

        Tardis tardis = this.tardis().get();

        if (tardis == null) return;

        TravelHandler travel = tardis.travel();

        // Unnecessary check, but I'm gonna just do it just in case. - Loqor
        if (travel.inFlight()) return;

        CachedDirectedGlobalPos pos = travel.destination();

        if (pos == null) return;

        if (pos.getDimension() == null) return;

        this.setTargetWorld(pos.getDimension(), pos.getPos(), true);
    }


}

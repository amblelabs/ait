package dev.amble.ait.core.blockentities;

import java.util.UUID;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.world.TardisServerWorld;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CoralBlockEntity extends BlockEntity {

    public UUID creator;

    public CoralBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.CORAL_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);

        if (world instanceof ServerWorld serverWorld && !TardisServerWorld.isTardisDimension(serverWorld)) {
            int delay = 1 + (int) Math.floorMod(this.pos.asLong(), 20L);
            world.scheduleBlockTick(this.pos, this.getCachedState().getBlock(), delay);
        }
    }

    public static void warnNearbyPlayers(ServerWorld world, BlockPos pos) {
        Vec3d center = Vec3d.ofCenter(pos);
        world.getPlayers(player -> player.squaredDistanceTo(center) <= 25.0).forEach(player ->
                player.sendMessage(Text.translatable("message.ait.coral.home_occupied")
                        .formatted(Formatting.RED), true));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.creator = nbt.getUuid("creator");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.creator == null) return;
        nbt.putUuid("creator", this.creator);
    }
}

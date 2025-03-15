package dev.amble.ait.core.blockentities;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.util.StackUtil;
import dev.amble.ait.registry.impl.MachineRecipeRegistry;

public class MachineCasingBlockEntity extends BlockEntity {

    private final Deque<ItemStack> parts = new ArrayDeque<>();

    public MachineCasingBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.MACHINE_CASING_ENTITY_TYPE, pos, state);
    }

    public void onUse(World world, ItemStack stack, PlayerEntity player) {
        if (stack.isEmpty() && player.isSneaking()) {
            if (this.parts.isEmpty())
                return;

            StackUtil.spawn(world, this.pos, parts.pop());
            return;
        }

        if (!(stack.getItem() instanceof SonicItem)) {
            this.parts.push(stack.copyWithCount(1));
            stack.decrement(1);
        }
    }

    public void construct() {
        MachineRecipeRegistry.getInstance().findMatching(this.parts).ifPresent(schema -> {
            StackUtil.spawn(world, this.pos, schema.output());

            world.removeBlock(this.pos, false);
            this.markRemoved();
        });
    }

    public void onBreak(World world) {
        this.parts.add(new ItemStack(AITBlocks.MACHINE_CASING.asItem())); // don't care about the parts now anyway
        StackUtil.scatter(world, pos.up(1), this.parts);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        StackUtil.writeUnordered(nbt, this.parts);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        StackUtil.readUnordered(nbt, this.parts);
    }

    @Nullable @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}

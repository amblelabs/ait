package dev.amble.ait.core.blockentities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.blocks.PottedSonicScrewdriverBlock;

public class PottedSonicScrewdriverBlockEntity extends BlockEntity {

    private final List<ItemStack> sonics = new ArrayList<>();

    public PottedSonicScrewdriverBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.POTTED_SONIC_SCREWDRIVER_BLOCK_ENTITY_TYPE, pos, state);
    }

    public List<ItemStack> getSonics() {
        return this.sonics;
    }

    public int count() {
        return this.sonics.size();
    }

    public boolean isFull() {
        return this.sonics.size() >= PottedSonicScrewdriverBlock.MAX_SONICS;
    }

    public void addSonic(ItemStack stack) {
        if (this.isFull())
            return;

        this.sonics.add(stack.copyWithCount(1));
        this.sync();
    }

    public ItemStack removeLast() {
        if (this.sonics.isEmpty())
            return ItemStack.EMPTY;

        ItemStack removed = this.sonics.remove(this.sonics.size() - 1);
        this.sync();
        return removed;
    }

    private void sync() {
        this.markDirty();
        if (this.world != null && !this.world.isClient)
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), net.minecraft.block.Block.NOTIFY_ALL);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        NbtList list = new NbtList();
        for (ItemStack stack : this.sonics)
            list.add(stack.writeNbt(new NbtCompound()));

        nbt.put("Sonics", list);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.sonics.clear();
        NbtList list = nbt.getList("Sonics", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size() && this.sonics.size() < PottedSonicScrewdriverBlock.MAX_SONICS; i++) {
            ItemStack stack = ItemStack.fromNbt(list.getCompound(i));
            if (!stack.isEmpty())
                this.sonics.add(stack.copyWithCount(1));
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}

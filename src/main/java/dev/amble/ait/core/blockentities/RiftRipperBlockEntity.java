package dev.amble.ait.core.blockentities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import dev.amble.ait.api.ArtronHolder;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.engine.link.IFluidLink;
import dev.amble.ait.core.engine.link.IFluidSource;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;
import dev.amble.ait.core.engine.link.tracker.FluidNetwork;
import dev.amble.ait.core.item.ArtronCollectorItem;
import dev.amble.ait.core.world.RiftChunkManager;

public class RiftRipperBlockEntity extends FluidLinkBlockEntity implements BlockEntityTicker<RiftRipperBlockEntity>, ArtronHolder, IFluidSource {

    private boolean firstTickHandled;
    public double artronAmount = 0;

    public RiftRipperBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.RIFT_RIPPER_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putDouble("artronAmount", this.artronAmount);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("artronAmount"))
            this.setCurrentFuel(nbt.getDouble("artronAmount"));
        super.readNbt(nbt);
    }

    @Override
    public void setCurrentFuel(double artronAmount) {
        this.artronAmount = artronAmount;
        this.updateListeners(this.getCachedState());
    }

    @Override
    public double getMaxFuel() {
        return ArtronCollectorItem.COLLECTOR_MAX_FUEL;
    }

    @Override
    public double getCurrentFuel() {
        return this.artronAmount;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = super.toInitialChunkDataNbt();
        nbtCompound.putDouble("artronAmount", this.artronAmount);
        return nbtCompound;
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, RiftRipperBlockEntity blockEntity) {
        if (!(world instanceof ServerWorld serverWorld))
            return;

        if (!firstTickHandled) {
            firstTickHandled = true;
            FluidNetwork.rebuildFrom(serverWorld, pos);
        }

        if (serverWorld.getServer().getTicks() % 3 == 0)
            return;

        RiftChunkManager manager = RiftChunkManager.getInstance(serverWorld);
        ChunkPos chunk = new ChunkPos(pos);

        if (shouldDrain(manager, chunk)) {
            manager.removeFuel(chunk, 3);
            this.addFuel(3);

            this.updateListeners(state);
        }
    }

    @Override
    public void onBroken(World world, BlockPos pos) {
        this.onLoseFluid(); // always.

        super.onBroken(world, pos);
    }

    private boolean shouldDrain(RiftChunkManager manager, ChunkPos pos) {
        return this.getCurrentFuel() < ArtronCollectorItem.COLLECTOR_MAX_FUEL
                && manager.getArtron(pos) >= 3;
    }

    private void updateListeners(BlockState state) {
        this.markDirty();

        if (!this.hasWorld())
            return;

        this.world.updateListeners(this.getPos(), this.getCachedState(), state, Block.NOTIFY_ALL);
    }

    @Override
    public void onGainFluid() {
        super.onGainFluid();
        this.rebuildOwnNetwork();
    }

    @Override
    public void onLoseFluid() {
        super.onLoseFluid();
        this.rebuildOwnNetwork();
    }

    private void rebuildOwnNetwork() {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            FluidNetwork.rebuildFrom(serverWorld, this.getPos());
        }
    }

    @Override
    public double level() {
        return this.getCurrentFuel();
    }

    @Override
    public void setLevel(double level) {
        this.setCurrentFuel(level);
    }

    @Override
    public double maxLevel() {
        return this.getMaxFuel();
    }

    @Override
    public void setSource(IFluidSource source) {

    }

    @Override
    public void setLast(IFluidLink last) {

    }

    @Override
    public IFluidSource source(boolean search) {
        return this;
    }

    @Override
    public BlockPos getLastPos() {
        return this.getPos();
    }

    @Override
    public IFluidLink last() {
        return this;
    }
}

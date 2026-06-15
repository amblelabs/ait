package dev.amble.ait.core.blockentities;

import dev.amble.ait.api.ArtronHolder;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITEntityTypes;
import dev.amble.ait.core.blocks.UntemperedSchismBlock;
import dev.amble.ait.core.engine.link.IFluidLink;
import dev.amble.ait.core.engine.link.IFluidSource;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;
import dev.amble.ait.core.engine.link.tracker.FluidNetwork;
import dev.amble.ait.core.entities.RiftEntity;
import dev.amble.ait.core.util.EntityRef;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UntemperedSchismBlockEntity extends FluidLinkBlockEntity implements BlockEntityTicker<UntemperedSchismBlockEntity>, ArtronHolder, IFluidSource {

    private boolean firstTickHandled;
    public double artronAmount = 0;
    private EntityRef<RiftEntity> riftRef;

    public UntemperedSchismBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.RIFT_RIPPER_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putDouble("artronAmount", this.artronAmount);
        if (this.riftRef != null) {
            nbt.putUuid("riftId", this.riftRef.getId());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("artronAmount"))
            this.setCurrentFuel(nbt.getDouble("artronAmount"));
        if (nbt.contains("riftId"))
            this.riftRef = new EntityRef<>(null, nbt.getUuid("riftId"));
        super.readNbt(nbt);
    }

    @Override
    public void setCurrentFuel(double artronAmount) {
        this.artronAmount = artronAmount;
        this.updateListeners(this.getCachedState());
    }

    @Override
    public double getMaxFuel() {
        return 10 * 20 * UntemperedSchismBlock.ARTRON_PER_TICK;
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
    public void tick(World world, BlockPos pos, BlockState state, UntemperedSchismBlockEntity blockEntity) {
        if (!(world instanceof ServerWorld serverWorld))
            return;

        if (!firstTickHandled) {
            firstTickHandled = true;
            FluidNetwork.rebuildFrom(serverWorld, pos);
        }

        int centerX = pos.getX();
        int centerZ = pos.getZ();

        double targetY = pos.getY() + 1;

        double endX = centerX + 0.5;
        double endZ = centerZ + 0.5;

        if ((this.getCurrentFuel() >= this.getMaxFuel())) {
            RiftEntity riftEntity = new RiftEntity(AITEntityTypes.RIFT_ENTITY, serverWorld);
            this.riftRef = new EntityRef<>(serverWorld, riftEntity);

            float rotation = this.getCachedState().get(HorizontalFacingBlock.FACING).asRotation();

            float adjustedRotation = rotation + 180.0f;

            riftEntity.updatePositionAndAngles(endX, targetY, endZ, adjustedRotation, 0);

            riftEntity.setYaw(adjustedRotation);
            riftEntity.setHeadYaw(adjustedRotation);
            riftEntity.setBodyYaw(adjustedRotation);

            serverWorld.spawnEntity(riftEntity);

            serverWorld.setBlockState(pos, state.with(UntemperedSchismBlock.ENABLED, true));
            this.updateListeners(state);

            serverWorld.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(),
                    SoundCategory.BLOCKS, 1.5f, 0.5f);
        }

        this.updateListeners(state);

        if (!this.getCachedState().get(UntemperedSchismBlock.ENABLED)) return;

        if (this.getCurrentFuel() <= 0) {
            serverWorld.setBlockState(pos, state.with(UntemperedSchismBlock.ENABLED, false));
            if (this.riftRef != null) {
                this.riftRef.setWorld(serverWorld);
                if (this.riftRef.get() != null)
                    this.riftRef.get().discard();
            }
            this.updateListeners(state);
            return;
        }

        this.removeFuel(UntemperedSchismBlock.ARTRON_PER_TICK);
    }

    @Override
    public void onBroken(World world, BlockPos pos) {
        this.onLoseFluid(); // always.

        if (this.riftRef != null && world instanceof ServerWorld serverWorld) {
            this.riftRef.setWorld(serverWorld);
            if (this.riftRef.get() != null)
                this.riftRef.get().discard();
        }

        super.onBroken(world, pos);
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

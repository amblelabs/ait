package dev.amble.ait.core.blockentities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

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
import dev.amble.ait.core.world.RiftChunkManager;

public class UntemperedSchismBlockEntity extends FluidLinkBlockEntity implements BlockEntityTicker<UntemperedSchismBlockEntity>, ArtronHolder, IFluidSource {

    private boolean firstTickHandled;
    public double artronAmount = 0;
    public boolean hasCreatedRift = false;
    private EntityRef<RiftEntity> riftRef;

    public UntemperedSchismBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.RIFT_RIPPER_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putDouble("artronAmount", this.artronAmount);
        nbt.putBoolean("hasCreatedRift", this.hasCreatedRift);
        if (this.riftRef != null) {
            nbt.putUuid("riftId", this.riftRef.getId());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("artronAmount"))
            this.setCurrentFuel(nbt.getDouble("artronAmount"));
        if (nbt.contains("hasCreatedRift"))
            this.hasCreatedRift = nbt.getBoolean("hasCreatedRift");
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

        if (this.hasCreatedRift)
            return;

        if (!firstTickHandled) {
            firstTickHandled = true;
            FluidNetwork.rebuildFrom(serverWorld, pos);
        }

        int centerX = pos.getX();
        int centerZ = pos.getZ();

        double targetY = pos.getY() + 2.5d;

        double endX = centerX + 0.5;
        double endZ = centerZ + 0.5;

        RiftChunkManager manager = RiftChunkManager.getInstance(serverWorld);
        if (this.getCurrentFuel() >= this.getMaxFuel()) {
            RiftEntity riftEntity = new RiftEntity(serverWorld);
            this.riftRef = new EntityRef<>(serverWorld, riftEntity);

            float rotation = this.getCachedState().get(HorizontalFacingBlock.FACING).asRotation();

            float adjustedRotation = rotation + 180.0f;

            riftEntity.updatePositionAndAngles(endX, targetY, endZ, adjustedRotation, 0);

            riftEntity.setYaw(adjustedRotation);
            riftEntity.setHeadYaw(adjustedRotation);
            riftEntity.setBodyYaw(adjustedRotation);

            serverWorld.spawnEntity(riftEntity);
            this.hasCreatedRift = true;

            serverWorld.setBlockState(pos, state.with(UntemperedSchismBlock.ENABLED, true));
            this.updateListeners(state);

            serverWorld.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(),
                    SoundCategory.BLOCKS, 1.5f, 0.5f);
        } else if (manager.getArtron(new ChunkPos(pos)) > UntemperedSchismBlock.ARTRON_PER_TICK && serverWorld.getServer().getTicks() % 20 == 4 && !state.get(UntemperedSchismBlock.ENABLED)) {
            double percentage = (this.getCurrentFuel() * 100d) / this.getMaxFuel();
            serverWorld.playSound(null, this.getPos(), SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 5.0f, 0.5f + (float) percentage / 40);
        }

        this.updateListeners(state);
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
    public BlockPos getLastPos() {
        return this.getPos();
    }
}

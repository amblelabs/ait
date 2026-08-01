package dev.amble.ait.core.blockentities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import dev.amble.ait.api.ArtronHolder;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.blocks.UntemperedSchismBlock;
import dev.amble.ait.core.engine.link.IFluidLink;
import dev.amble.ait.core.engine.link.IFluidSource;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;
import dev.amble.ait.core.engine.link.tracker.FluidNetwork;
import dev.amble.ait.core.entities.RiftEntity;
import dev.amble.ait.core.util.EntityRef;
import dev.amble.ait.core.world.RiftChunkManager;

public class UntemperedSchismBlockEntity extends FluidLinkBlockEntity implements BlockEntityTicker<UntemperedSchismBlockEntity>, ArtronHolder, IFluidSource {

    private static final String ARTRON_KEY = "artronAmount";
    private static final String CREATED_KEY = "hasCreatedRift";
    private static final String RIFT_ID_KEY = "riftId";

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
        nbt.putDouble(ARTRON_KEY, this.artronAmount);
        nbt.putBoolean(CREATED_KEY, this.hasCreatedRift);

        if (this.riftRef != null)
            nbt.putUuid(RIFT_ID_KEY, this.riftRef.getId());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.setCurrentFuel(nbt.getDouble(ARTRON_KEY));
        this.hasCreatedRift = nbt.getBoolean(CREATED_KEY);
        this.riftRef = nbt.containsUuid(RIFT_ID_KEY) ? new EntityRef<>(null, nbt.getUuid(RIFT_ID_KEY)) : null;
    }

    @Override
    public void setCurrentFuel(double artronAmount) {
        double before = this.artronAmount;
        double normalized = Double.isFinite(artronAmount) ? artronAmount : 0;
        double clamped = MathHelper.clamp(normalized, 0, this.getMaxFuel());

        if (Double.compare(this.artronAmount, clamped) == 0)
            return;

        this.artronAmount = clamped;
        this.onChange(before, clamped);
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
        NbtCompound nbt = super.toInitialChunkDataNbt();
        nbt.putDouble(ARTRON_KEY, this.artronAmount);
        nbt.putBoolean(CREATED_KEY, this.hasCreatedRift);
        return nbt;
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, UntemperedSchismBlockEntity blockEntity) {
        if (!(world instanceof ServerWorld serverWorld))
            return;

        if (!UntemperedSchismBlock.canCreateAt(serverWorld, pos)) {
            serverWorld.setBlockState(pos, Blocks.LODESTONE.getDefaultState(), Block.NOTIFY_ALL);
            return;
        }

        if (!firstTickHandled) {
            firstTickHandled = true;
            FluidNetwork.rebuildFrom(serverWorld, pos);
        }

        if (this.hasCreatedRift)
            return;

        if (this.getCurrentFuel() >= this.getMaxFuel()) {
            this.tryCreateRift(serverWorld, pos);
            return;
        }

        RiftChunkManager manager = RiftChunkManager.getInstance(serverWorld);
        Chunk chunk = serverWorld.getChunk(pos);

        if (manager.getArtron(chunk) > UntemperedSchismBlock.ARTRON_PER_TICK
                && serverWorld.getServer().getTicks() % 20 == 4
                && !state.get(UntemperedSchismBlock.ENABLED)) {
            double percentage = (this.getCurrentFuel() * 100d) / this.getMaxFuel();
            serverWorld.playSound(null, this.getPos(), SoundEvents.BLOCK_END_PORTAL_FRAME_FILL,
                    SoundCategory.BLOCKS, 5.0f, 0.5f + (float) percentage / 40);
        }
    }

    private void tryCreateRift(ServerWorld world, BlockPos pos) {
        RiftEntity rift = new RiftEntity(world);
        float rotation = this.getCachedState().get(HorizontalFacingBlock.FACING).asRotation() + 180.0f;

        rift.updatePositionAndAngles(pos.getX() + 0.5, pos.getY() + 2.5, pos.getZ() + 0.5, rotation, 0);
        rift.setYaw(rotation);
        rift.setHeadYaw(rotation);
        rift.setBodyYaw(rotation);

        if (!world.spawnEntity(rift))
            return;

        double cost = this.getMaxFuel();
        double consumed = this.extractLevel(cost);

        if (consumed < cost) {
            if (consumed > 0)
                this.insertLevel(consumed);
            rift.discard();
            return;
        }

        BlockState current = world.getBlockState(pos);
        if (!current.isOf(AITBlocks.UNTEMPERED_SCHISM)
                || !world.setBlockState(pos, current.with(UntemperedSchismBlock.ENABLED, true), Block.NOTIFY_ALL)) {
            this.insertLevel(consumed);
            rift.discard();
            return;
        }

        this.riftRef = new EntityRef<>(world, rift);
        this.hasCreatedRift = true;
        this.updateListeners(world.getBlockState(pos));

        world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(),
                SoundCategory.BLOCKS, 1.5f, 0.5f);
    }

    @Override
    public void onBroken(World world, BlockPos pos) {
        if (this.riftRef != null && world instanceof ServerWorld serverWorld) {
            this.riftRef.setWorld(serverWorld);
            RiftEntity rift = this.riftRef.get();

            if (rift != null)
                rift.discard();
        }

        this.riftRef = null;
        super.onBroken(world, pos);
    }

    private void updateListeners(BlockState state) {
        this.markDirty();

        if (!this.hasWorld())
            return;

        this.world.updateListeners(this.getPos(), this.getCachedState(), state, Block.NOTIFY_LISTENERS);
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
        if (this.getWorld() instanceof ServerWorld serverWorld)
            FluidNetwork.rebuildFrom(serverWorld, this.getPos());
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

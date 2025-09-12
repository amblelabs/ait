package dev.amble.ait.core.blockentities;


import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.block.SubSystemBlockEntity;
import dev.amble.ait.core.engine.link.IFluidLink;
import dev.amble.ait.core.engine.link.IFluidSource;
import dev.amble.ait.core.engine.link.ITardisSource;
import dev.amble.ait.core.tardis.Tardis;

public class EngineBlockEntity extends SubSystemBlockEntity implements ITardisSource {
    public EngineBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.ENGINE_BLOCK_ENTITY_TYPE, pos, state, SubSystem.Id.ENGINE);

        if (!this.hasWorld()) return;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, @Nullable LivingEntity placer) {
        super.onPlaced(world, pos, placer);
        if (world.isClient())
            return;

        this.tardis().ifPresent(tardis -> tardis.subsystems().engine().setEnabled(true));

        if (tryPlaceFillBlocks())
            return;

        this.onBroken(world, pos);
        world.setBlockState(pos, Blocks.AIR.getDefaultState());

        if (placer == null) return;

        Block.dropStack(world, pos, AITBlocks.ENGINE_BLOCK.asItem().getDefaultStack());

        if (!(placer instanceof ServerPlayerEntity player)) return;

        player.sendMessage(Text.translatable("tardis.message.engine.no_space").formatted(Formatting.RED), true);
    }

    @Override
    public void onBroken(World world, BlockPos pos) {
        this.onLoseFluid(); // always.
        this.tryRemoveFillBlocks();

        super.onBroken(world, pos);
    }

    /**
     * Places cable blocks adjacent and barrier blocks in corners
     * @return true if all blocks were placed
     */
    private boolean tryPlaceFillBlocks() {
        if (this.getWorld().isClient()) return false;

        boolean success = true;

        BlockPos centre = this.getPos();
        ServerWorld world = (ServerWorld) this.getWorld();

        // place cable blocks adjacent
        for (Direction dir : Direction.values()) {
            if (dir == Direction.UP || dir == Direction.DOWN) continue;

            BlockPos offset = centre.offset(dir);
            success = success && tryPlace(world, offset, AITBlocks.CABLE_BLOCK.getDefaultState());
        }

        // place barrier blocks in corners
        BlockPos corner = centre.add(1, 0, 1);
        success = success && tryPlace(world, corner, Blocks.BARRIER.getDefaultState());

        corner = centre.add(-1, 0, 1);
        success = success && tryPlace(world, corner, Blocks.BARRIER.getDefaultState());

        corner = centre.add(1, 0, -1);
        success = success && tryPlace(world, corner, Blocks.BARRIER.getDefaultState());

        corner = centre.add(-1, 0, -1);
        success = success && tryPlace(world, corner, Blocks.BARRIER.getDefaultState());

        return success;
    }

    private boolean tryPlace(ServerWorld world, BlockPos pos, BlockState state) {
        if (world.getBlockState(pos).isReplaceable()) {
            world.setBlockState(pos, state);
            return true;
        }
        return false;
    }

    /**
     * Removes cable blocks adjacent and barrier blocks in corners
     * @return true if all blocks were removed
     */
    private void tryRemoveFillBlocks() {
        if (this.getWorld().isClient())
            return;

        BlockPos centre = this.getPos();
        ServerWorld world = (ServerWorld) this.getWorld();

        // place cable blocks adjacent
        for (Direction dir : Direction.values()) {
            BlockPos offset = centre.offset(dir);
            tryRemoveIfMatches(world, offset, AITBlocks.CABLE_BLOCK);
        }

        // place barrier blocks in corners
        BlockPos corner = centre.add(1, 0, 1);
        tryRemoveIfMatches(world, corner, Blocks.BARRIER);

        corner = centre.add(-1, 0, 1);
        tryRemoveIfMatches(world, corner, Blocks.BARRIER);

        corner = centre.add(1, 0, -1);
        tryRemoveIfMatches(world, corner, Blocks.BARRIER);

        corner = centre.add(-1, 0, -1);
        tryRemoveIfMatches(world, corner, Blocks.BARRIER);
    }

    /**
     * Removes a block if it matches the expected block
     */
    private void tryRemoveIfMatches(ServerWorld world, BlockPos pos, Block expected) {
        BlockState state = world.getBlockState(pos);

        if (!state.isOf(expected))
            return;

        world.removeBlock(pos, false);
    }

    @Override
    public void onLinked() {
        this.tardis().ifPresent(tardis -> tardis.getDesktop().setEnginePos(this));
    }

    @Override
    public Tardis getTardisForFluid() {
        return this.tardis().get();
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
    public IFluidLink last() {
        return this;
    }

    @Override
    public BlockPos getLastPos() {
        return this.getPos();
    }
}

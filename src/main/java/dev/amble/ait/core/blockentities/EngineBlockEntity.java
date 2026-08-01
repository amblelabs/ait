package dev.amble.ait.core.blockentities;


import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import dev.amble.ait.core.engine.link.tracker.FluidNetwork;
import dev.amble.ait.core.tardis.Tardis;

public class EngineBlockEntity extends SubSystemBlockEntity implements ITardisSource {
    private static final Direction[] HORIZONTAL_DIRECTIONS = {
            Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
    };

    private boolean firstTickHandled;
    private boolean suppressFillCleanup;

    private record FillBlock(BlockPos pos, BlockState state) {}

    private record ReplacedBlock(BlockPos pos, BlockState state) {}

    public EngineBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.ENGINE_BLOCK_ENTITY_TYPE, pos, state, SubSystem.Id.ENGINE);

        if (!this.hasWorld()) return;
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        super.tick(world, pos, state);
        if (!firstTickHandled && !world.isClient()) {
            firstTickHandled = true;
            this.registerEnginePosition();
            FluidNetwork.rebuildFrom((ServerWorld) world, pos);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, @Nullable LivingEntity placer) {
        if (world.isClient()) {
            super.onPlaced(world, pos, placer);
            return;
        }

        if (!this.tryPlaceFillBlocks((ServerWorld) world)) {
            this.suppressFillCleanup = true;
            boolean removed = world.setBlockState(pos, Blocks.AIR.getDefaultState());
            if (!removed) this.suppressFillCleanup = false;

            if (!removed || placer == null) return;

            if (!(placer instanceof PlayerEntity player) || !player.isCreative())
                Block.dropStack(world, pos, AITBlocks.ENGINE_BLOCK.asItem().getDefaultStack());

            if (!(placer instanceof ServerPlayerEntity player)) return;

            player.sendMessage(Text.translatable("tardis.message.engine.no_space").formatted(Formatting.RED), true);
            return;
        }

        super.onPlaced(world, pos, placer);
        this.registerEnginePosition();
        this.tardis().ifPresent(tardis -> tardis.subsystems().engine().setEnabled(true));
    }

    @Override
    public void onBroken(World world, BlockPos pos) {
        if (!world.isClient() && !this.suppressFillCleanup) {
            this.onLoseFluid(); // always.
            this.tryRemoveFillBlocks();
            this.tardis().ifPresent(tardis -> tardis.getDesktop().removeEngine(this));
        }

        super.onBroken(world, pos);
    }

    /**
     * Places cable blocks adjacent and barrier blocks in corners
     * @return true if all blocks were placed
     */
    private boolean tryPlaceFillBlocks(ServerWorld world) {
        BlockPos centre = this.getPos();
        List<FillBlock> required = new ArrayList<>(8);

        for (Direction direction : HORIZONTAL_DIRECTIONS) {
            required.add(new FillBlock(centre.offset(direction), AITBlocks.CABLE_BLOCK.getDefaultState()));
        }

        required.add(new FillBlock(centre.add(1, 0, 1), Blocks.BARRIER.getDefaultState()));
        required.add(new FillBlock(centre.add(-1, 0, 1), Blocks.BARRIER.getDefaultState()));
        required.add(new FillBlock(centre.add(1, 0, -1), Blocks.BARRIER.getDefaultState()));
        required.add(new FillBlock(centre.add(-1, 0, -1), Blocks.BARRIER.getDefaultState()));

        List<ReplacedBlock> originals = new ArrayList<>(required.size());
        for (FillBlock fill : required) {
            BlockState original = world.getBlockState(fill.pos());
            if (!original.isReplaceable()) return false;
            originals.add(new ReplacedBlock(fill.pos(), original));
        }

        List<ReplacedBlock> replaced = new ArrayList<>(required.size());

        for (int i = 0; i < required.size(); i++) {
            FillBlock fill = required.get(i);
            ReplacedBlock original = originals.get(i);
            boolean placed = world.setBlockState(fill.pos(), fill.state());
            BlockState current = world.getBlockState(fill.pos());

            // Track only positions this placement actually changed. In particular, do not
            // restore untouched, later preflight positions if an earlier placement fails.
            if (!current.equals(original.state()))
                replaced.add(original);

            if (!placed || !current.equals(fill.state())) {
                this.rollbackFillBlocks(world, replaced);
                return false;
            }
        }

        return true;
    }

    private void rollbackFillBlocks(ServerWorld world, List<ReplacedBlock> originals) {
        for (int i = originals.size() - 1; i >= 0; i--) {
            ReplacedBlock original = originals.get(i);
            if (world.getBlockState(original.pos()).equals(original.state())) continue;
            world.setBlockState(original.pos(), original.state());
        }
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

        // remove only the four horizontal cables created for this engine
        for (Direction direction : HORIZONTAL_DIRECTIONS) {
            tryRemoveIfMatches(world, centre.offset(direction), AITBlocks.CABLE_BLOCK);
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

    private void registerEnginePosition() {
        this.tardis().ifPresent(tardis -> tardis.getDesktop().setEnginePos(this));
    }

    @Override
    public void onGainFluid() {
        super.onGainFluid();
    }

    @Override
    public void onLoseFluid() {
        super.onLoseFluid();
    }

    @Override
    public Tardis getTardisForFluid() {
        if (!this.isLinked() || this.tardis().isEmpty()) return null;

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

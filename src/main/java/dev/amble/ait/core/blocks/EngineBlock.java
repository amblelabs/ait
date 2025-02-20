package dev.amble.ait.core.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.blockentities.EngineBlockEntity;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.block.SubSystemBlock;
import dev.amble.ait.core.engine.block.SubSystemBlockEntity;

public class EngineBlock extends SubSystemBlock implements BlockEntityProvider {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
//    protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(
//            -16.0, 0.0, -16.0,
//            32.0, 48.0, 32.0
//    );
    protected static final VoxelShape Y_SHAPE = VoxelShapes.fullCube();


    public EngineBlock(Settings settings) {
        super(settings, SubSystem.Id.ENGINE);

        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Y_SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Y_SHAPE;
    }

    @Nullable @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EngineBlockEntity(pos, state);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!(world.getBlockEntity(pos) instanceof EngineBlockEntity engine))
            return;

        if (!engine.isLinked()) return;

        world.addParticle(ParticleTypes.SMOKE, true, pos.getX() + -0.9f, pos.getY() + 1.25,
                pos.getZ() + 1.8f, 0, 0.06, 0);
        world.addParticle(ParticleTypes.LARGE_SMOKE, true, pos.getX() + -0.9f, pos.getY() + 1.25,
                pos.getZ() + 1.8f, 0, 0.06, 0);
        world.addParticle(ParticleTypes.FLAME, true, pos.getX() + -0.9f, pos.getY() + 1.25,
                pos.getZ() + 1.8f, 0, 0.06, 0);
        world.addParticle(ParticleTypes.SMALL_FLAME, true, pos.getX() + -0.9f, pos.getY() + 1.25,
                pos.getZ() + 1.8f, 0, 0.06, 0);

        float durability = engine.tardis().get().subsystems().engine().durability();

        if (durability > 10) return;

        // smoke and spark particles & sfx when below 50%
        world.addParticle(ParticleTypes.LARGE_SMOKE, true, pos.getX() + 0.5f, pos.getY() + 1.25,
                pos.getZ() + 0.5f, 0.15, 0, 0);
        world.addParticle(ParticleTypes.CLOUD, pos.getX() + 0.5f, pos.getY() + 1.25, pos.getZ() + 0.5f, 0.1,
                0, 0.05f);

        world.addParticle(ParticleTypes.LARGE_SMOKE, true, pos.getX() + 0.5f, pos.getY() + 1.25,
                pos.getZ() + 0.5f, -0.15, 0, 0);
        world.addParticle(ParticleTypes.CLOUD, pos.getX() + 0.5f, pos.getY() + 1.25, pos.getZ() + 0.5f, -0.1,
                0, -0.05f);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected BlockEntityType<? extends SubSystemBlockEntity> getType() {
        return AITBlockEntityTypes.ENGINE_BLOCK_ENTITY_TYPE;
    }
}

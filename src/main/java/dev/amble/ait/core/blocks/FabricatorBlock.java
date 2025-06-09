package dev.amble.ait.core.blocks;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.FabricatorBlockEntity;
import dev.amble.ait.core.blocks.types.HorizontalDirectionalBlock;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.amble.ait.client.util.TooltipUtil.addShiftHiddenTooltip;

public class FabricatorBlock extends HorizontalDirectionalBlock implements BlockEntityProvider {

    public static final VoxelShape DEFAULT_SHAPE = VoxelShapes.cuboid(0, 0, 0, 1, (double) 2 / 16, 1);

    public FabricatorBlock(Settings settings) {
        super(settings);

        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        this.setDefaultState(state.with(FACING, placer.getHorizontalFacing().getOpposite()));
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        if (world.getBlockEntity(pos) instanceof FabricatorBlockEntity be) {
            be.onBroken();
        }

        super.onDestroyedByExplosion(world, pos, explosion);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if (world.getBlockEntity(pos) instanceof FabricatorBlockEntity be) {
            be.onBroken();
        }

        super.onBroken(world, pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof FabricatorBlockEntity be) {
            be.useOn(state, world, player.isSneaking(), player);
            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return DEFAULT_SHAPE;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!(world.getBlockEntity(pos) instanceof FabricatorBlockEntity be)) return;
        if (!be.isValid()) return;
        if (!be.hasBlueprint()) return;
        if (be.getBlueprint().orElseThrow().isComplete()) return;

        Direction direction = state.get(FACING);
        double d = (double) pos.getX() + 0.55 - (double) (random.nextFloat() * 0.1f);
        double e = (double) pos.getY() + 0.25 - (double) (random.nextFloat() * 0.1f);
        double f = (double) pos.getZ() + 0.55 - (double) (random.nextFloat() * 0.1f);
        double g = 0.4f - (random.nextFloat() + random.nextFloat()) * 0.4f;
        world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d + (double) direction.getOffsetX() * g,
                e + (double) direction.getOffsetY() * g, f + (double) direction.getOffsetZ() * g,
                random.nextGaussian() * 0.005, random.nextGaussian() * 0.005, random.nextGaussian() * 0.005);

        if (random.nextDouble() < 0.05) {
            world.playSound(d, e, f, AITSounds.FABRICATOR_LOOP, SoundCategory.BLOCKS, 0.25f, 1.0f, false);
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return DEFAULT_SHAPE;
    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return DEFAULT_SHAPE;
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FabricatorBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);

        addShiftHiddenTooltip(stack, tooltip, tooltips -> {
            tooltip.add(Text.translatable("block.ait.fabricator.tooltip.use").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        });
    }
}

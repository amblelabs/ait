package dev.amble.ait.core.engine.block;

import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.link.block.FluidLinkBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class SubSystemBlock extends FluidLinkBlock {
    private final SubSystem.IdLike id;

    protected SubSystemBlock(Settings settings, SubSystem.IdLike system) {
        super(settings);

        this.id = system;
    }

    public SubSystem.IdLike getSystemId() {
        return this.id;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock() && !(world.isClient())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof SubSystemBlockEntity be)
                world.updateComparators(pos, this);
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof SubSystemBlockEntity be) {
                be.tick(world1, pos, state1);
            }
        };
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
                              BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        ItemStack stack = player.getStackInHand(hand);
        if (blockEntity instanceof SubSystemBlockEntity be)
            return be.useOn(state, world, player.isSneaking(), player, stack);

        return ActionResult.SUCCESS;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        if (!(world.getBlockEntity(pos) instanceof SubSystemBlockEntity be))
            return;
        if (!be.isLinked()) return;
        if (!(be.system() instanceof DurableSubSystem system)) return;

        float durability = system.durability();

        if (durability > 10) return;

        // smoke and spark particles & sfx when below 10%
        world.addParticle(ParticleTypes.SMOKE, true, pos.getX() + 0.5f, pos.getY() + 1,
                pos.getZ() + 0.5f, 0.15, 0, 0);
        world.addParticle(ParticleTypes.CLOUD, pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f, 0.1,
                0, 0.05f);
        world.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f, 0.1,
                0, 0.05f);
        world.addParticle(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f, 0.1,
                0, 0.05f);

        world.addParticle(ParticleTypes.SMOKE, true, pos.getX() + 0.5f, pos.getY() + 1,
                pos.getZ() + 0.5f, -0.15, 0, 0);
        world.addParticle(ParticleTypes.CLOUD, pos.getX() + 0.5f, pos.getY() + 1.25, pos.getZ() + 0.5f, -0.1,
                0, -0.05f);
        world.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5f, pos.getY() + 1.25, pos.getZ() + 0.5f, -0.1,
                0, -0.05f);
        world.addParticle(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5f, pos.getY() + 1.25, pos.getZ() + 0.5f, -0.1,
                0, -0.05f);
    }
}

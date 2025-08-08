package dev.amble.ait.core.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.ConsoleGeneratorBlockEntity;
import dev.amble.ait.core.engine.link.block.FluidLinkBlock;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;

public class ConsoleGeneratorBlock extends FluidLinkBlock implements BlockEntityProvider {

    public ConsoleGeneratorBlock(Settings settings) {
        super(settings);
    }

    @Nullable @Override
    public FluidLinkBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConsoleGeneratorBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {

        if (world.getBlockEntity(pos) instanceof ConsoleGeneratorBlockEntity be)
            be.useOn(world, player.isSneaking(), false, player);

        return ActionResult.SUCCESS;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 0.5;
        double centerZ = pos.getZ() + 0.5;
        double radius = 1;
        double angle = random.nextDouble() * 2 * Math.PI;
        int side = random.nextInt(4);

        double x = centerX;
        double y = centerY + (random.nextDouble() - 0.5) * 0.2;
        double z = centerZ;

        switch (side) {
            case 0: // +X
                x += radius * Math.cos(angle);
                z += radius * Math.sin(angle);
                break;
            case 1: // -X
                x -= radius * Math.cos(angle);
                z += radius * Math.sin(angle);
                break;
            case 2: // +Z
                z += radius * Math.cos(angle);
                x += radius * Math.sin(angle);
                break;
            case 3: // -Z
                z -= radius * Math.cos(angle);
                x += radius * Math.sin(angle);
                break;
        }

        if (random.nextInt(5) == 0) {
            for (int i = 0; i < 3; i++) {
                world.addParticle(AITMod.CORAL_PARTICLE, x, y, z,
                        random.nextGaussian() * 0.01, random.nextGaussian() * 0.01, random.nextGaussian() * 0.01);
            }
        }
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (world.getBlockEntity(pos) instanceof ConsoleGeneratorBlockEntity be)
            be.useOn(world, player.isSneaking(), true, player);
    }
}

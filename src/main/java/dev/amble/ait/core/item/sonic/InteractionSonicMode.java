package dev.amble.ait.core.item.sonic;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.blockentities.MachineCasingBlockEntity;
import dev.amble.ait.data.schema.sonic.SonicSchema;

public class InteractionSonicMode extends SonicMode {

    protected InteractionSonicMode(int index) {
        super(index);
    }

    @Override
    public Text text() {
        return Text.translatable("sonic.ait.mode.interaction").formatted(Formatting.GREEN,Formatting.BOLD);
    }

    @Override
    public void stopUsing(ItemStack stack, World world, LivingEntity user, int ticks, int ticksLeft) {
        if (!(world instanceof ServerWorld serverWorld))
            return;

        this.process(serverWorld, user, ticks);
    }

    @Override
    public void tick(ItemStack stack, World world, LivingEntity user, int ticks, int ticksLeft) {
        if (!(world instanceof ServerWorld serverWorld) || ticks % 10 != 0)
            return;

        this.process(serverWorld, user, ticks);
    }

    private void process(ServerWorld world, LivingEntity user, int ticks) {
        HitResult hitResult = SonicMode.getHitResult(user);

        if (hitResult instanceof BlockHitResult blockHit)
            this.interactBlock(blockHit.getBlockPos(), world, user, ticks);
    }

    private void interactBlock(BlockPos pos, ServerWorld world, LivingEntity user, int ticks) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (!world.getBlockState(pos).isIn(AITTags.Blocks.SONIC_INTERACTABLE))
            return;

        if (canMakeRedstoneTweak(ticks)
                && state.contains(DaylightDetectorBlock.INVERTED)) {
            world.playSound(null, pos, SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.BLOCKS, 1.0f,
                    world.getRandom().nextFloat() * 0.4f + 0.8f);

            world.setBlockState(pos, state.with(DaylightDetectorBlock.POWER, 15));
            world.emitGameEvent(user, GameEvent.BLOCK_ACTIVATE, pos);
        }

        if (canMakeRedstoneTweak(ticks)
                && block.getDefaultState().contains(RedstoneLampBlock.LIT)) {
            world.playSound(null, pos, SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.BLOCKS, 1.0f,
                    world.getRandom().nextFloat() * 0.4f + 0.8f);

            world.setBlockState(pos, state.cycle(RedstoneLampBlock.LIT));
            world.emitGameEvent(user, GameEvent.BLOCK_CHANGE, pos);
            return;
        }

        if (canMakeRedstoneTweak(ticks)
                && block.getDefaultState().contains(ComparatorBlock.MODE)) {
            world.playSound(null, pos, SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.BLOCKS, 1.0f,
                    world.getRandom().nextFloat() * 0.4f + 0.8f);

            world.setBlockState(pos, state.cycle(ComparatorBlock.POWERED));
            world.emitGameEvent(user, GameEvent.BLOCK_CHANGE, pos);
            return;
        }

        if (canMakeRedstoneTweak(ticks)
                && block.getDefaultState().contains(RepeaterBlock.POWERED)) {
            world.playSound(null, pos, SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.BLOCKS, 1.0f,
                    world.getRandom().nextFloat() * 0.4f + 0.8f);

            world.setBlockState(pos, state.cycle(RepeaterBlock.POWERED));
            world.emitGameEvent(user, GameEvent.BLOCK_CHANGE, pos);
            return;
        }

        if (canInteract3(ticks)
                && block.getDefaultState().contains(BellBlock.FACING)) {
            world.playSound(null, pos, SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 1.0f,
                    world.getRandom().nextFloat() * 0.4f + 0.8f);

            world.setBlockState(pos, state.cycle(BellBlock.FACING));
            world.emitGameEvent(user, GameEvent.ENTITY_INTERACT, pos);
        }


        if (canInteract3(ticks)
                && block == AITBlocks.MACHINE_CASING) {
            ((MachineCasingBlockEntity) world.getBlockEntity(pos)).construct();
            return;
        }

        if (canInteract3(ticks)
                && block instanceof TntBlock) {
            TntBlock.primeTnt(world, pos);

            world.removeBlock(pos, false);
            world.emitGameEvent(user, GameEvent.BLOCK_DESTROY, pos);
        }
    }

    private static boolean canMakeRedstoneTweak(int ticks) {
        return ticks >= 5;
    }

    private static boolean canInteract1(int ticks) {
        return ticks >= 10;
    }

    private static boolean canInteract2(int ticks) {
        return ticks >= 20;
    }

    private static boolean canInteract3(int ticks) {
        return ticks >= 30;
    }

    @Override
    public int maxTime() {
        return 5 * 60 * 20; // 5 minutes
    }

    @Override
    public Identifier model(SonicSchema.Models models) {
        return models.interaction();
    }
}

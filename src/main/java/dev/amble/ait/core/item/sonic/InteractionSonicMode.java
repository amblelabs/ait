package dev.amble.ait.core.item.sonic;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import dev.amble.ait.data.schema.sonic.SonicSchema;

public class InteractionSonicMode extends SonicMode {

    protected InteractionSonicMode(int index) {
        super(index);
    }

    @Override
    public Text text() {
        return Text.translatable("sonic.ait.mode.interaction").formatted(Formatting.GREEN, Formatting.BOLD);
    }

    @Override
    public void tick(ItemStack stack, World world, LivingEntity user, int ticks, int ticksLeft) {
        if (!(world instanceof ServerWorld serverWorld) || ticks % 10 != 0)
            return;

        this.process(serverWorld, user, ticks);
    }

    private void process(ServerWorld world, LivingEntity user, int ticks) {
        HitResult hitResult = SonicMode.getHitResult(user);

        if (hitResult instanceof BlockHitResult blockHit) {
            this.interactBlock(blockHit.getBlockPos(), world, user, ticks);
        }
    }

    private void interactBlock(BlockPos pos, ServerWorld world, LivingEntity user, int ticks) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block == Blocks.IRON_DOOR && state.contains(Properties.OPEN)) {
            boolean isOpen = state.get(Properties.OPEN);
            world.setBlockState(pos, state.with(Properties.OPEN, !isOpen), 3);
            world.emitGameEvent(user, GameEvent.BLOCK_ACTIVATE, pos);
            return;
        }

        if (block == Blocks.IRON_TRAPDOOR && state.contains(Properties.OPEN)) {
            boolean isOpen = state.get(Properties.OPEN);
            world.setBlockState(pos, state.with(Properties.OPEN, !isOpen), 3);
            world.emitGameEvent(user, GameEvent.BLOCK_ACTIVATE, pos);
            return;
        }

        if (block instanceof RepeaterBlock && state.contains(Properties.DELAY)) {
            world.setBlockState(pos, state.cycle(Properties.DELAY), 3);
            world.emitGameEvent(user, GameEvent.BLOCK_CHANGE, pos);
            return;
        }

        if (block instanceof ComparatorBlock && state.contains(Properties.COMPARATOR_MODE)) {
            world.setBlockState(pos, state.cycle(Properties.COMPARATOR_MODE), 3);
            world.emitGameEvent(user, GameEvent.BLOCK_CHANGE, pos);
            return;
        }

        if (block instanceof DaylightDetectorBlock && state.contains(Properties.INVERTED)) {
            world.setBlockState(pos, state.cycle(Properties.INVERTED), 3);
            world.emitGameEvent(user, GameEvent.BLOCK_CHANGE, pos);
            return;
        }
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

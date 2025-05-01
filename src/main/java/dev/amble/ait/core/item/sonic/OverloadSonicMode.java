package dev.amble.ait.core.item.sonic;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.data.schema.sonic.SonicSchema;

public class OverloadSonicMode extends SonicMode {

    protected OverloadSonicMode(int index) {
        super(index);
    }

    @Override
    public Text text() {
        return Text.translatable("sonic.ait.mode.overload").formatted(Formatting.RED, Formatting.BOLD);
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
            this.overloadBlock(blockHit.getBlockPos(), world, user, ticks, blockHit);
    }

    private void overloadBlock(BlockPos pos, ServerWorld world, LivingEntity user, int ticks, BlockHitResult blockHit) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (!state.isIn(AITTags.Blocks.SONIC_INTERACTABLE))
            return;

        if (!canMakeRedstoneTweak(ticks))
            return;

        if (block instanceof DaylightDetectorBlock) {
            activateBlock(world, pos, user, state, blockHit);
            this.playFx(world, pos);
            return;
        }

        if (block instanceof RedstoneLampBlock) {
            world.setBlockState(pos, state.cycle(Properties.LIT));
            this.playFx(world, pos);
            return;
        }

        if (block instanceof RedstoneWireBlock
                || block instanceof AbstractRedstoneGateBlock) {
            forceRedstonePower(world, pos, state, 5 * 20);
            return;
        }

        if (block instanceof GlassBlock
                || block instanceof StainedGlassPaneBlock) {
            breakBlock(world, pos, user, state, blockHit);
            return;
        }

        if (block instanceof ButtonBlock) {
            activateBlock(world, pos, user, state, blockHit);
            return;
        }

        if (!canLit(ticks))
            return;

        if (block instanceof TntBlock) {
            TntBlock.primeTnt(world, pos);
            world.removeBlock(pos, false);
            world.emitGameEvent(user, GameEvent.BLOCK_DESTROY, pos);
            return;
        }

        if (state.isOf(Blocks.OBSIDIAN)) {
            BlockPos blockPos2 = pos.offset(blockHit.getSide());
            if (AbstractFireBlock.canPlaceAt(world, blockPos2, user.getHorizontalFacing())) {
                this.playFx(world, blockPos2);
                BlockState blockState2 = AbstractFireBlock.getState(world, blockPos2);
                world.setBlockState(blockPos2, blockState2, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                world.emitGameEvent(user, GameEvent.BLOCK_PLACE, pos);
            }
        }
    }

    private void activateBlock(ServerWorld world, BlockPos pos, LivingEntity user, BlockState state, BlockHitResult blockHitResult) {
        state.onUse(world, (PlayerEntity) user, user.getActiveHand(), blockHitResult);
        this.playFx(world, pos);
    }

    private void breakBlock(ServerWorld world, BlockPos pos, LivingEntity user, BlockState state, BlockHitResult blockHitResult) {
        world.playSound(null, pos, state.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);

        // Spawn block break particles
        ((ServerWorld) world).spawnParticles(
                new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                10, 0.5, 0.5, 0.5, 0.1
        );
        world.breakBlock(pos, false);
        this.playFx(world, pos);
    }

    private void forceRedstonePower(ServerWorld world, BlockPos pos, BlockState state, int durationTicks) {
        // uhh... womp womp
        this.playFx(world, pos);
        world.updateNeighbors(pos, state.getBlock());
    }

    private void playFx(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos, AITSounds.SONIC_TWEAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
        this.spawnParticles(world, pos);
    }

    private void spawnParticles(ServerWorld world, BlockPos pos) {
        world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 5, 0.2, 0.2, 0.2, 0.01);
        world.spawnParticles(ParticleTypes.LAVA, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 3, 0.1, 0.1, 0.1, 0.01);
    }

    private static boolean canLit(int ticks) {
        return ticks >= 10;
    }

    private static boolean canMakeRedstoneTweak(int ticks) {
        return ticks >= 10;
    }

    @Override
    public int maxTime() {
        return 5 * 60 * 20; // 5 minutes
    }

    @Override
    public Identifier model(SonicSchema.Models models) {
        return models.overload();
    }

    @Override
    public int fuelCost() {
        return 2;
    }
}

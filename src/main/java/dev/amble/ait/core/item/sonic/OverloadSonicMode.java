package dev.amble.ait.core.item.sonic;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.control.impl.HADSControl;
import dev.amble.ait.core.tardis.control.impl.HandBrakeControl;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.Loyalty;
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
        if (!(world instanceof ServerWorld serverWorld) || ticks % 10 != 0) return;
        this.process(serverWorld, user, ticks);
    }

    private void process(ServerWorld world, LivingEntity user, int ticks) {
        HitResult hitResult = SonicMode.getHitResultForOutline(user);

        if (hitResult instanceof BlockHitResult blockHit) {
            this.overloadBlock(blockHit.getBlockPos(), world, user, ticks, blockHit);
        }

        if (!(user instanceof PlayerEntity player)) return;

        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();

        // Dual sonic overload in main and offhand
        /// I ain't never seen more overcomplicated code in my life bruh and I work for fucking DALEK MOD - Loqor
        if (main.getItem() instanceof SonicItem mainSonic &&
                off.getItem() instanceof SonicItem offSonic &&
                SonicItem.mode(off) == Modes.OVERLOAD) {

            double mainFuel = mainSonic.getCurrentFuel(main);
            double offFuel = offSonic.getCurrentFuel(off);

            if (mainFuel > 0 || offFuel > 0) {
                mainSonic.removeFuel(mainFuel, main);
                offSonic.removeFuel(offFuel, off);

                Vec3d dir = player.getRotationVec(1.0F).multiply(-0.5).add(0, 0.3, 0);
                player.addVelocity(dir.x, dir.y, dir.z);
                player.damage(world.getDamageSources().magic(), 2.0F);

                playSparkEffect(world, player);

                player.getItemCooldownManager().set(main.getItem(), 60);
                player.getItemCooldownManager().set(off.getItem(), 60);
            }
        }

        // Overload transfer between two players via targeting
        if (hitResult instanceof EntityHitResult entityHit &&
                entityHit.getEntity() instanceof PlayerEntity other) {

            ItemStack userStack = player.getMainHandStack();
            ItemStack otherStack = other.getMainHandStack();

            if (!(userStack.getItem() instanceof SonicItem userSonic)) return;
            if (!(otherStack.getItem() instanceof SonicItem otherSonic)) return;

            if (SonicItem.mode(otherStack) != Modes.OVERLOAD) return;

            double userFuel = userSonic.getCurrentFuel(userStack);
            double otherFuel = otherSonic.getCurrentFuel(otherStack);

            if (userFuel == otherFuel) return;

            PlayerEntity lowerFuelPlayer = userFuel < otherFuel ? player : other;
            PlayerEntity higherFuelPlayer = userFuel < otherFuel ? other : player;

            ItemStack lowerStack = lowerFuelPlayer.getMainHandStack();
            ItemStack higherStack = higherFuelPlayer.getMainHandStack();

            double transferAmount = 10.0;
            double actualRemoved = Math.min(transferAmount,
                    ((SonicItem) higherStack.getItem()).getCurrentFuel(higherStack));

            ((SonicItem) higherStack.getItem()).removeFuel(actualRemoved, higherStack);
            ((SonicItem) lowerStack.getItem()).addFuel(actualRemoved, lowerStack);

            playSparkEffect(world, lowerFuelPlayer);

            Vec3d pushDir = lowerFuelPlayer.getPos()
                    .subtract(higherFuelPlayer.getPos()).normalize().multiply(0.5);
            lowerFuelPlayer.addVelocity(pushDir.x, 0.1, pushDir.z);
            higherFuelPlayer.addVelocity(-pushDir.x, 0.1, -pushDir.z);

            lowerFuelPlayer.damage(world.getDamageSources().magic(), 1.0F);
            higherFuelPlayer.damage(world.getDamageSources().magic(), 1.0F);

            player.getItemCooldownManager().set(userStack.getItem(), 60);
            other.getItemCooldownManager().set(otherStack.getItem(), 500);

        }
    }

    private void overloadBlock(BlockPos pos, ServerWorld world, LivingEntity user, int ticks, BlockHitResult blockHit) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (!state.isIn(AITTags.Blocks.SONIC_INTERACTABLE) || !canMakeRedstoneTweak(ticks)) return;

        if (world.getBlockEntity(pos) instanceof AbstractLinkableBlockEntity ext && ext.isLinked()) {
            ServerTardis tardis = ext.tardis().get().asServer();
            if (tardis.loyalty().get((PlayerEntity) user).smallerThan(Loyalty.fromLevel(Loyalty.Type.COMPANION.level))) return;

            getExpectedControl(tardis).handleRun(tardis, (ServerPlayerEntity) user, world, pos, false);
            playFx(world, pos);
            return;
        }

        if (block instanceof DaylightDetectorBlock) {
            activateBlock(world, pos, user, state, blockHit);
        }
        else if (block instanceof RedstoneLampBlock) {
            world.setBlockState(pos, state.cycle(Properties.LIT));
        }
        else if (block instanceof RedstoneWireBlock || block instanceof AbstractRedstoneGateBlock) {
            forceRedstonePower(world, pos, state, 5 * 20);
        }
        else if (block instanceof LeverBlock lever) {
            lever.togglePower(state, world, pos);
        }
        else if (block instanceof AbstractGlassBlock || block instanceof PaneBlock) {
            breakBlock(world, pos, user, state, blockHit);
        }
        else if (canLight(ticks) && block instanceof TntBlock) {
            TntBlock.primeTnt(world, pos);
            world.removeBlock(pos, false);
            world.emitGameEvent(user, GameEvent.BLOCK_DESTROY, pos);
        }
        else if (state.getBlock() instanceof AbstractCandleBlock) {
            world.setBlockState(pos, state.with(AbstractCandleBlock.LIT, true), Block.NOTIFY_ALL);
            world.emitGameEvent(user, GameEvent.BLOCK_CHANGE, pos);
        }
        else if (state.isOf(Blocks.OBSIDIAN)) {
            BlockPos blockPos2 = pos.offset(blockHit.getSide());
            if (AbstractFireBlock.canPlaceAt(world, blockPos2, user.getHorizontalFacing())) {
                playFx(world, blockPos2);
                world.setBlockState(blockPos2, AbstractFireBlock.getState(world, blockPos2), Block.NOTIFY_ALL);
                world.emitGameEvent(user, GameEvent.BLOCK_PLACE, pos);
            }
        } else if(state.isOf(Blocks.BRICKS)){
            breakBlock(world, pos, user, state, blockHit);
        }
        playFx(world, pos);
    }

    private void playSparkEffect(ServerWorld world, PlayerEntity player) {
        Vec3d pos = player.getPos().add(0, player.getHeight() / 2.0, 0);
        world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, pos.x, pos.y, pos.z, 10, 0.2, 0.5, 0.2, 0.05);
        world.playSound(null, player.getBlockPos(), AITSounds.SONIC_TWEAK, SoundCategory.PLAYERS, 1.0f, 1.5f);
    }

    private static Control getExpectedControl(ServerTardis tardis) {
        TravelHandler travel = tardis.travel();
        return (travel.getState() == TravelHandlerBase.State.DEMAT || tardis.subsystems().engine().phaser().isPhasing())
                ? new HandBrakeControl()
                : new HADSControl();
    }

    private void activateBlock(ServerWorld world, BlockPos pos, LivingEntity user, BlockState state, BlockHitResult hit) {
        state.onUse(world, (PlayerEntity) user, user.getActiveHand(), hit);
        playFx(world, pos);
    }

    private void breakBlock(ServerWorld world, BlockPos pos, LivingEntity user, BlockState state, BlockHitResult hit) {
        world.playSound(null, pos, state.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                10, 0.5, 0.5, 0.5, 0.1);
        world.breakBlock(pos, false);
    }

    private void forceRedstonePower(ServerWorld world, BlockPos pos, BlockState state, int durationTicks) {
        playFx(world, pos);
        world.updateNeighbors(pos, state.getBlock());
    }

    private void playFx(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos, AITSounds.SONIC_TWEAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
        spawnParticles(world, pos);
    }

    private void spawnParticles(ServerWorld world, BlockPos pos) {
        world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 5, 0.2, 0.2, 0.2, 0.01);
        world.spawnParticles(ParticleTypes.LAVA, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 3, 0.1, 0.1, 0.1, 0.01);
    }

    private static boolean canLight(int ticks) {
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

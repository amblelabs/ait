package dev.amble.ait.core.blockentities;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.api.link.v2.TardisRef;
import dev.amble.ait.api.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blocks.ExteriorBlock;
import dev.amble.ait.core.engine.impl.EngineSystem;
import dev.amble.ait.core.item.KeyItem;
import dev.amble.ait.core.item.SiegeTardisItem;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.animation.ExteriorAnimation;
import dev.amble.ait.core.tardis.handler.SonicHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.util.TardisUtil;

public class ExteriorBlockEntity extends AbstractLinkableBlockEntity implements BlockEntityTicker<ExteriorBlockEntity> {

    private ExteriorAnimation animation;

    public ExteriorBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.EXTERIOR_BLOCK_ENTITY_TYPE, pos, state);
    }

    public ExteriorBlockEntity(BlockPos pos, BlockState state, Tardis tardis) {
        this(pos, state);
        this.link(tardis);
    }

    public void useOn(ServerWorld world, boolean sneaking, PlayerEntity player) {
        if (this.tardis().isEmpty() || player == null)
            return;

        ServerTardis tardis = (ServerTardis) this.tardis().get();

        if (tardis.isGrowth())
            return;

        SonicHandler handler = tardis.sonic();

        ItemStack hand = player.getMainHandStack();
        boolean hasSonic = handler.getExteriorSonic() != null;
        boolean shouldEject = player.isSneaking();

        if (hand.getItem() instanceof KeyItem key && !tardis.siege().isActive()
                && !tardis.interiorChangingHandler().queued().get()) {
            if (hand.isOf(AITItems.SKELETON_KEY) || key.isOf(hand, tardis)) {
                tardis.door().interactToggleLock((ServerPlayerEntity) player);
            } else {
                world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), SoundCategory.BLOCKS, 1F, 0.2F);
                player.sendMessage(Text.translatable("tardis.key.identity_error"), true); // TARDIS does not identify
                                                                                            // with key
            }

            return;
        }

        if (hasSonic) {
            if (shouldEject) {
                player.giveItemStack(handler.takeExteriorSonic());
                world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), SoundCategory.BLOCKS, 1F,
                        0.2F);
                return;
            }

            player.sendMessage(Text.translatable("tardis.exterior.sonic.repairing")
                    .append(Text.literal(": " + tardis.crash().getRepairTicksAsSeconds() + "s")
                            .formatted(Formatting.BOLD, Formatting.GOLD)),
                    true);
            return;
        }

        if (hand.getItem() instanceof SonicItem sonic && sonic.isOf(hand, tardis)) {
            if (!tardis.siege().isActive()
                    && !tardis.interiorChangingHandler().queued().get()
                    && tardis.door().isClosed() && tardis.crash().getRepairTicks() > 0) {
                if (sonic.isOf(hand, tardis)) {
                    handler.insertExteriorSonic(hand);

                    player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                    world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1F, 0.2F);
                } else {
                    world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), SoundCategory.BLOCKS, 1F,
                            0.2F);
                    player.sendMessage(Text.translatable("tardis.tool.cannot_repair"), true); // Unable to repair TARDIS
                    // with current tool!
                }

                return;
            }

            // try to stop phasing
            EngineSystem.Phaser phasing = tardis.subsystems().engine().phaser();

            if (phasing.isPhasing()) {
                world.playSound(null, pos, AITSounds.SONIC_USE, SoundCategory.PLAYERS, 1F, 1F);
                phasing.cancel();
                return;
            }
        }

        if (sneaking && tardis.siege().isActive() && !tardis.isSiegeBeingHeld()) {
            SiegeTardisItem.pickupTardis(tardis, (ServerPlayerEntity) player);
            return;
        }

        if (!tardis.travel().isLanded())
            return;

        tardis.door().interact((ServerWorld) this.getWorld(), this.getPos(), (ServerPlayerEntity) player);
    }

    public void onEntityCollision(Entity entity) {
        TardisRef ref = this.tardis();

        if (ref == null)
            return;

        if (ref.isEmpty())
            return;

        Tardis tardis = ref.get();
        TravelHandler travel = tardis.travel();

        boolean previouslyLocked = tardis.door().previouslyLocked().get();

        if (travel.getState() == TravelHandlerBase.State.DEMAT) return;

        if (!previouslyLocked && travel.getState() == TravelHandlerBase.State.MAT
                && travel.getAnimTicks() >= 0.9 * travel.getMaxAnimTicks())
            TardisUtil.teleportInside(tardis, entity);

        if (!tardis.door().isClosed()
                && (!DependencyChecker.hasPortals() || !tardis.getExterior().getVariant().hasPortals()))
            TardisUtil.teleportInside(tardis, entity);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState blockState, ExteriorBlockEntity blockEntity) {
        TardisRef ref = this.tardis();

        if (ref == null || ref.isEmpty())
            return;

        Tardis tardis = ref.get();

        TravelHandlerBase travel = tardis.travel();
        TravelHandlerBase.State state = travel.getState();

        if (!world.isClient()) {
            if (tardis.travel().isLanded())
                world.scheduleBlockTick(this.getPos(), AITBlocks.EXTERIOR_BLOCK, 2);

            return;
        }

        if (state.animated())
            this.getAnimation().tick(tardis);
        else
            this.getAnimation().reset();

        this.exteriorLightBlockState(blockState, pos, state);
    }

    public void verifyAnimation() {
        TardisRef ref = this.tardis();

        if (this.animation != null || ref == null || ref.isEmpty())
            return;

        Tardis tardis = ref.get();

        this.animation = tardis.getExterior().getVariant().animation(this);
        this.animation.setupAnimation(tardis.travel().getState());

        if (this.getWorld() != null && !this.getWorld().isClient()) {
            this.animation.tellClientsToSetup(tardis.travel().getState());
        }
    }

    public ExteriorAnimation getAnimation() {
        this.verifyAnimation();
        return this.animation;
    }

    @Environment(EnvType.CLIENT)
    public float getAlpha() {
        return this.getAnimation().getAlpha();
    }

    private void exteriorLightBlockState(BlockState blockState, BlockPos pos, TravelHandlerBase.State state) {
        if (!state.animated())
            return;

        if (!blockState.isOf(AITBlocks.EXTERIOR_BLOCK))
            return;

        this.getWorld().setBlockState(pos, blockState.with(ExteriorBlock.LEVEL_4, Math.round(this.getAlpha() * 4)));
    }
}

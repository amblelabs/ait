package dev.amble.ait.core.tardis.control.impl;

import static dev.amble.ait.core.engine.SubSystem.Id.GRAVITATIONAL;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

public class VisualiserControl extends Control {

    public VisualiserControl() {
        super(AITMod.id("visualiser"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean rightClick) {
        super.runServer(tardis, player, world, console, rightClick);

        if (!AITMod.CONFIG.rwfEnabled) {
            player.sendMessage(Text.translatable("tardis.message.control.rwf_disabled"), true);
            return Result.FAILURE;
        }

        if (!player.isCreative()) {
            player.sendMessage(Text.translatable("tardis.message.control.rwf_creative_only"), true);
            return Result.FAILURE;
        }

        if (!player.isSneaking() && tardis.travel().getState() == TravelHandlerBase.State.LANDED && tardis.subsystems().get(GRAVITATIONAL).isEnabled()) {
            if (tardis.door().isOpen()) {
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_CHAIN_FALL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return Result.SUCCESS;
            } else {
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_CLUSTER_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            tardis.flight().enterFlight(player);
            return Result.SUCCESS;
        }

        return Result.FAILURE;
    }

    @Override
    protected SubSystem.IdLike requiredSubSystem() {
        return GRAVITATIONAL;
    }

    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.RENAISSANCE_ANTI_GRAV;
    }
}

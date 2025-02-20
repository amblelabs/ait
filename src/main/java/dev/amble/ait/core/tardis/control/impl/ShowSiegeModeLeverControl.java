package dev.amble.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;

public class ShowSiegeModeLeverControl extends Control {

    public ShowSiegeModeLeverControl() {
        super("toggle_siege_mode_lever");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console) {
        return true;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
    }
}

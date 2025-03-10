package dev.amble.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisDesktop;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

public class RefuelerControl extends Control {

    public RefuelerControl() {
        super(AITMod.id("refueler"));
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        if (tardis.isGrowth())
            return false;

        if (tardis.travel().getState() == TravelHandlerBase.State.LANDED && tardis.travel().handbrake()) {
            tardis.setRefueling(!tardis.isRefueling());

            if (tardis.isRefueling())
                TardisDesktop.playSoundAtConsole(tardis.asServer().getInteriorWorld(), console, AITSounds.ENGINE_REFUEL, SoundCategory.BLOCKS, 10,
                        1);


            return true;
        }

        return false;
    }

    @Override
    public SoundEvent getSound() {
        return AITSounds.ENGINE_REFUEL_CRANK;
    }

    @Override
    public boolean requiresPower() {
        return false;
    }

    @Override
    protected SubSystem.IdLike requiredSubSystem() {
        return null;
    }
}

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
import dev.amble.ait.core.tardis.control.Control;

public class AutoPilotControl extends Control {

    public AutoPilotControl() {
        super(AITMod.id("protocol_116"));
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        boolean autopilot = tardis.travel().autopilot();
        tardis.travel().autopilot(!autopilot);

        SoundEvent toggleSound = autopilot ? AITSounds.PROTOCOL_116_OFF : AITSounds.PROTOCOL_116_ON;
        world.playSound(null, console, toggleSound, SoundCategory.BLOCKS, 1.0F, 1.0F);

        return true;
    }

    @Override
    protected SubSystem.IdLike requiredSubSystem() {
        return SubSystem.Id.STABILISERS;
    }

    @Override
    public SoundEvent getSound() {
        return AITSounds.PROTOCOL_116_ON;
    }
}

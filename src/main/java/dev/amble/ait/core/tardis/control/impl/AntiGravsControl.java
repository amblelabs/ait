package dev.amble.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;

public class AntiGravsControl extends Control {
    public static final Identifier ID = AITMod.id("antigravs");

    public AntiGravsControl() {
        super(ID);
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        tardis.travel().antigravs().toggle();
        tardis.travel().scheduleExteriorUpdate();
        return tardis.travel().antigravs().get() ? Result.SUCCESS : Result.SUCCESS_ALT;
    }

    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.ANTI_GRAVS;
    }

    @Override
    protected SubSystem.IdLike requiredSubSystem() {
        return SubSystem.Id.GRAVITATIONAL;
    }
}

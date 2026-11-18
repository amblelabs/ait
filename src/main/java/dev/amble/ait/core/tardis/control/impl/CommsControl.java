package dev.amble.ait.core.tardis.control.impl;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class CommsControl extends Control {

    public CommsControl() {
        super(AITMod.id("comms"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        AITMod.openScreen(player, 4, tardis.getUuid(), console);
        return Result.SUCCESS;
    }
}

package dev.amble.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.CloakHandler;

public class CloakControl extends Control {
    public static final Identifier ID = AITMod.id("protocol_3");

    public CloakControl() {
        // ⬚ ?
        super(ID);
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        CloakHandler cloak = tardis.handler(TardisComponent.Id.CLOAK);

        if (leftClick && cloak.cloaked().get() && !cloak.silent().get()) {
            cloak.silent().set(true);
            player.sendMessage(Text.translatable("control.ait.protocol_3_silent_activated"), true);
        } else if (leftClick && cloak.cloaked().get() && cloak.silent().get()){
            cloak.silent().set(false);
            player.sendMessage(Text.translatable("control.ait.protocol_3_silent_deactivated"), true);
        } else {
            cloak.cloaked().set(!cloak.cloaked().get());
            if (cloak.silent().get())
                cloak.silent().set(false);
        }

        return cloak.cloaked().get() ? Result.SUCCESS : Result.SUCCESS_ALT;
    }

    @Override
    public SoundEvent getFallbackSound() {
        return SoundEvents.INTENTIONALLY_EMPTY;
    }

    @Override
    protected SubSystem.IdLike requiredSubSystem() {
        return SubSystem.Id.CHAMELEON;
    }
}

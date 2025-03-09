package dev.amble.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;

public class HailMaryControl extends Control {

    public HailMaryControl() {
        // ♡ ?
        super("protocol_813");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console) {
        if (tardis.sequence().hasActiveSequence() && tardis.sequence().controlPartOfSequence(this)) {
            this.addToControlSequence(tardis, player, console);
            return false;
        }

        tardis.stats().hailMary().set(!tardis.stats().hailMary().get());
        tardis.removeFuel(50 * tardis.travel().instability());

        player.sendMessage(tardis.stats().hailMary().get()
                ? Text.translatable("tardis.message.control.hail_mary.engaged")
                : Text.translatable("tardis.message.control.hail_mary.disengaged"), true);

        return true;
    }

    @Override
    protected SubSystem.IdLike requiredSubSystem() {
        return SubSystem.Id.DESPERATION;
    }

    @Override
    public SoundEvent getSound() {
        return AITSounds.HAIL_MARY;
    }
}

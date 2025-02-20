package dev.amble.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;

public class FastReturnControl extends Control {

    public FastReturnControl() {
        super("fast_return");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console) {
        TravelHandler travel = tardis.travel();

        if (tardis.sequence().hasActiveSequence() && tardis.sequence().controlPartOfSequence(this)) {
            this.addToControlSequence(tardis, player, console);
            return false;
        }

        boolean same = travel.destination().equals(travel.previousPosition());

        if (travel.previousPosition() != null) {
            travel.forceDestination(same ? travel.position() : travel.previousPosition());

            this.messagePlayer(player, same);
            return true;
        }

        Text text = Text.translatable("tardis.message.control.fast_return.destination_nonexistent");
        player.sendMessage(text, true);
        return true;
    }

    public void messagePlayer(ServerPlayerEntity player, boolean isLastPosition) {
        Text previousPosition = Text.translatable("tardis.message.control.fast_return.last_position");
        Text currentPosition = Text.translatable("tardis.message.control.fast_return.current_position");
        player.sendMessage((!isLastPosition ? previousPosition : currentPosition), true);
    }

    @Override
    public SoundEvent getSound() {
        return AITSounds.FAST_RETURN;
    }
}

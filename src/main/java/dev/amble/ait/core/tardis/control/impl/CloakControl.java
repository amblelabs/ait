package dev.amble.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.api.TardisComponent;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.CloakHandler;

public class CloakControl extends Control {

    public CloakControl() {
        // ⬚ ?
        super("protocol_3");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console) {
        if (tardis.sequence().hasActiveSequence() && tardis.sequence().controlPartOfSequence(this)) {
            this.addToControlSequence(tardis, player, console);

            world.playSound(null, player.getBlockPos(), AITSounds.PROTOCOL_3, SoundCategory.BLOCKS,
                    1.0F, 1.0F);
            return false;
        }

        CloakHandler cloak = tardis.handler(TardisComponent.Id.CLOAK);

        cloak.cloaked().set(!cloak.cloaked().get());

        if (cloak.cloaked().get()) {
            world.playSound(null, player.getBlockPos(), AITSounds.PROTOCOL_3, SoundCategory.BLOCKS,
                    1.0F, 1.0F);
        } else {
            world.playSound(null, player.getBlockPos(), AITSounds.PROTOCOL_3ALT,
                    SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

        return true;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.INTENTIONALLY_EMPTY;
    }

    @Override
    protected SubSystem.IdLike requiredSubSystem() {
        return SubSystem.Id.CHAMELEON;
    }
}

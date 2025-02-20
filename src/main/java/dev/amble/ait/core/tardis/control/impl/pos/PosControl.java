package dev.amble.ait.core.tardis.control.impl.pos;

import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;

public abstract class PosControl extends Control {

    private final PosType type;

    public PosControl(PosType type, String id) {
        super(id);
        this.type = type;
    }

    public PosControl(PosType type) {
        this(type, type.asString());
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console,
            boolean leftClick) {
        if (tardis.sequence().hasActiveSequence() && tardis.sequence().controlPartOfSequence(this)) {
            this.addToControlSequence(tardis, player, console);
            return false;
        }

        TravelHandler travel = tardis.travel();
        CachedDirectedGlobalPos destination = travel.destination();

        BlockPos pos = this.type.add(destination.getPos(),
                (leftClick) ? -IncrementManager.increment(tardis) : IncrementManager.increment(tardis),
                destination.getWorld());

        travel.destination(destination.pos(pos));
        messagePlayerDestination(player, travel);
        return true;
    }

    private void messagePlayerDestination(ServerPlayerEntity player, TravelHandler travel) {
        CachedDirectedGlobalPos globalPos = travel.destination();
        BlockPos pos = globalPos.getPos();

        Text text = Text.translatable("tardis.message.control.randomiser.poscontrol")
                .append(Text.literal(" " + pos.getX() + " | " + pos.getY() + " | " + pos.getZ()));
        player.sendMessage(text, true);
    }

    @Override
    public boolean shouldHaveDelay() {
        return false;
    }
}

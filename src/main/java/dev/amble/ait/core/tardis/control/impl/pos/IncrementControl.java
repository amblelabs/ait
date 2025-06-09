package dev.amble.ait.core.tardis.control.impl.pos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class IncrementControl extends Control {

    public IncrementControl() {
        super(AITMod.id("increment"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        if (!leftClick) {
            IncrementManager.nextIncrement(tardis);
        } else {
            IncrementManager.prevIncrement(tardis);
        }

        messagePlayerIncrement(player, tardis);

        return leftClick ? Result.SUCCESS_ALT : Result.SUCCESS;
    }

    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.CRANK;
    }

    private void messagePlayerIncrement(ServerPlayerEntity player, Tardis tardis) {
        Text text = Text.translatable("tardis.message.control.increment.info")
                .append(Text.literal("" + IncrementManager.increment(tardis)));
        player.sendMessage(text, true);
    }

    @Override
    public boolean shouldHaveDelay() {
        return false;
    }
}

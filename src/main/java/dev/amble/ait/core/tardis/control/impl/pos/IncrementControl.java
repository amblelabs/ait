package dev.amble.ait.core.tardis.control.impl.pos;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.data.schema.console.variant.coral.*;

public class IncrementControl extends Control {

    private SoundEvent soundEvent = AITSounds.CRANK;

    public IncrementControl() {
        super(AITMod.id("increment"));
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        boolean isCoral = false;

        if (world.getBlockEntity(console) instanceof ConsoleBlockEntity consoleBlockEntity)
            isCoral = isCoralVariant(consoleBlockEntity);

        this.soundEvent = isCoral ? AITSounds.CORAL_INCREMENT_ALT : AITSounds.CRANK;

        if (!leftClick) {
            IncrementManager.nextIncrement(tardis);
        } else {
            IncrementManager.prevIncrement(tardis);
        }

        messagePlayerIncrement(player, tardis);
        return true;
    }

    @Override
    public SoundEvent getSound() {
        return this.soundEvent;
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

    private boolean isCoralVariant(ConsoleBlockEntity consoleBlockEntity) {
        return consoleBlockEntity.getVariant() instanceof CoralVariant ||
                consoleBlockEntity.getVariant() instanceof WhiteCoralVariant ||
                consoleBlockEntity.getVariant() instanceof CoralSithVariant ||
                consoleBlockEntity.getVariant() instanceof BlueCoralVariant ||
                consoleBlockEntity.getVariant() instanceof CoralDecayedVariant;
    }
}

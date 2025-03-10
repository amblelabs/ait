package dev.amble.ait.core.tardis.control.impl;

import java.text.DecimalFormat;

import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.data.schema.console.variant.coral.*;

public class MonitorControl extends Control {

    private SoundEvent soundEvent = AITSounds.MONITOR;

    public MonitorControl() {
        super(AITMod.id("monitor"));
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        CachedDirectedGlobalPos abpd = tardis.travel().destination();
        BlockPos abpdPos = abpd.getPos();

        if (!player.isSneaking()) {
            boolean isCoral = false;

            if (world.getBlockEntity(console) instanceof ConsoleBlockEntity consoleBlockEntity)
                isCoral = isCoralVariant(consoleBlockEntity);

            this.soundEvent = isCoral ? AITSounds.CORAL_MONITOR_ALT : AITSounds.MONITOR;

            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 1.0F);

            if (tardis.sequence().hasActiveSequence() && tardis.sequence().controlPartOfSequence(this)) {
                this.addToControlSequence(tardis, player, console);
                return false;
            }

            AITMod.openScreen(player, 0, tardis.getUuid(), console);
        } else {
            DecimalFormat df = new DecimalFormat("#.##");
            String formattedNumber = df.format(tardis.getFuel());
            player.sendMessage(Text.of("X: " + abpdPos.getX() + " Y: " + abpdPos.getY() + " Z: " + abpdPos.getZ() + " Dim: " + WorldUtil.worldText(abpd.getDimension()).getString() + " Fuel: " + formattedNumber + "/50000"), true);
        }

        return true;
    }

    @Override
    public SoundEvent getSound() {
        return this.soundEvent;
    }

    private boolean isCoralVariant(ConsoleBlockEntity consoleBlockEntity) {
        return consoleBlockEntity.getVariant() instanceof CoralVariant ||
                consoleBlockEntity.getVariant() instanceof WhiteCoralVariant ||
                consoleBlockEntity.getVariant() instanceof CoralSithVariant ||
                consoleBlockEntity.getVariant() instanceof BlueCoralVariant ||
                consoleBlockEntity.getVariant() instanceof CoralDecayedVariant;
    }
}

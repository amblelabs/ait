package dev.amble.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.TardisComponent;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.ShieldHandler;
import dev.amble.ait.data.schema.console.variant.coral.*;
import dev.amble.ait.data.schema.console.variant.renaissance.*;

public class ShieldsControl extends Control {

    private SoundEvent soundEvent = AITSounds.SHIELDS;

    public ShieldsControl() {
        super(AITMod.id("shields"));
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        boolean isRenaissance = false;
        boolean isCoral = false;

        if (world.getBlockEntity(console) instanceof ConsoleBlockEntity consoleBlockEntity) {
            isRenaissance = isRenaissanceVariant(consoleBlockEntity);
            isCoral = isCoralVariant(consoleBlockEntity);
        }

        ShieldHandler shields = tardis.handler(TardisComponent.Id.SHIELDS);

        if (leftClick) {
            if (shields.shielded().get())
                shields.toggleVisuals();
        } else {
            shields.toggle();
            if (shields.visuallyShielded().get())
                shields.disableVisuals();
        }

        if (isRenaissance) {
            this.soundEvent = leftClick ? AITSounds.RENAISSANCE_SHIELDS_ALTALT : AITSounds.RENAISSANCE_SHIELDS_ALT;
        } else if (isCoral) {
            this.soundEvent = leftClick ? AITSounds.CORAL_SHIELDS_ALTALT : AITSounds.CORAL_SHIELDS_ALT;
        } else {
            this.soundEvent = leftClick ? AITSounds.SHIELDS : AITSounds.HANDBRAKE_LEVER_PULL;
        }

        return true;
    }

    @Override
    protected SubSystem.IdLike requiredSubSystem() {
        return SubSystem.Id.SHIELDS;
    }

    @Override
    public SoundEvent getSound() {
        return this.soundEvent;
    }

    private boolean isRenaissanceVariant(ConsoleBlockEntity consoleBlockEntity) {
        return consoleBlockEntity.getVariant() instanceof RenaissanceTokamakVariant ||
                consoleBlockEntity.getVariant() instanceof RenaissanceVariant ||
                consoleBlockEntity.getVariant() instanceof RenaissanceIdentityVariant ||
                consoleBlockEntity.getVariant() instanceof RenaissanceIndustriousVariant ||
                consoleBlockEntity.getVariant() instanceof RenaissanceFireVariant;
    }

    private boolean isCoralVariant(ConsoleBlockEntity consoleBlockEntity) {
        return consoleBlockEntity.getVariant() instanceof CoralVariant ||
                consoleBlockEntity.getVariant() instanceof WhiteCoralVariant ||
                consoleBlockEntity.getVariant() instanceof CoralSithVariant ||
                consoleBlockEntity.getVariant() instanceof BlueCoralVariant ||
                consoleBlockEntity.getVariant() instanceof CoralDecayedVariant;
    }
}

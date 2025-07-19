package dev.amble.ait.client.screens;


import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.client.sounds.PlayerFollowingLoopingSound;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.AITSounds;

/**
 * A screen that is opened from a console.
 * It also plays idle sfx. see {@link #shouldPlayIdleSfx()} and {@link #getIdleSound()}
 */
public abstract class ConsoleScreen extends TardisScreen {

    protected final BlockPos console;
    protected static PlayerFollowingLoopingSound idleSound;

    protected ConsoleScreen(Text title, ClientTardis tardis, BlockPos console) {
        super(title, tardis);

        this.client = MinecraftClient.getInstance();
        this.console = console;

        boolean hasChanged = idleSound == null || idleSound.getId() != this.getIdleSound().getId();

        if (hasChanged) {
            idleSound = (shouldPlayIdleSfx()) ? new PlayerFollowingLoopingSound(this.getIdleSound(), SoundCategory.AMBIENT, 0.25F) : null;
        }

        if (!shouldPlayIdleSfx() || hasChanged)
            this.client.getSoundManager().stop(idleSound);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers))
            return true;

        if (this.canCloseWithKey() && this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }

        return false;
    }

    public BlockPos getConsole() {
        return console;
    }

    public boolean shouldPlayIdleSfx() {
        return this.getIdleSound() != null;
    }

    public @Nullable SoundEvent getIdleSound() {
        return AITSounds.MONITOR_IDLE;
    }

    @Override
    protected void init() {
        super.init();

        if (idleSound != null && !this.client.getSoundManager().isPlaying(idleSound))
            this.client.getSoundManager().play(idleSound);
    }

    @Override
    public void close() {
        this.client.getSoundManager().stop(idleSound);

        super.close();
    }

    public boolean canCloseWithKey() {
        return true;
    }
}

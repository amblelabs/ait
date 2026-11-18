package dev.amble.ait.client.screens.svc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public final class RotaryClicks {

    private int pulses;

    public void dial(char digit) {
        this.pulses += digit == '0' ? 10 : digit - '0';
        click(1.2f);
    }

    public boolean turning() {
        return this.pulses > 0;
    }

    public void tick() {
        if (this.pulses <= 0)
            return;

        this.pulses--;
        click(1.6f + (this.pulses % 2) * 0.1f);
    }

    public static void click(float pitch) {
        MinecraftClient.getInstance().getSoundManager().play(
                PositionedSoundInstance.master(SoundEvents.BLOCK_COMPARATOR_CLICK, pitch, 0.35f));
    }
}

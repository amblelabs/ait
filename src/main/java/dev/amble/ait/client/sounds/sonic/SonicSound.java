package dev.amble.ait.client.sounds.sonic;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;

import dev.amble.ait.client.sounds.PositionedLoopingSound;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;

public class SonicSound extends PositionedLoopingSound {
    private final AbstractClientPlayerEntity player;

    public SonicSound(AbstractClientPlayerEntity player) {
        super(AITSounds.SONIC_USE, SoundCategory.PLAYERS, player.getBlockPos(), 1f, 1f);

        this.player = player;
    }

    @Override
    public void tick() {
        super.tick();

        if (!shouldPlay(this.player)) {
            this.stop();
            return;
        }

        this.updatePosition();
        this.updatePitch();
    }

    public static boolean shouldPlay(PlayerEntity player) {
        return player.isUsingItem() && player.getActiveItem().isOf(AITItems.SONIC_SCREWDRIVER);
    }

    public void play() {
        MinecraftClient.getInstance().getSoundManager().play(this);
    }

    public boolean isPlaying() {
        return MinecraftClient.getInstance().getSoundManager().isPlaying(this);
    }

    public void stop() {
        MinecraftClient.getInstance().getSoundManager().stop(this);
    }

    private void updatePosition() {
        this.setPosition(this.player.getBlockPos());
    }

    private void updatePitch() {
        this.setPitch(-(this.player.getPitch() / 90f) + 1f);
    }

    public void onUse() {
        if (!this.isPlaying()) {
            this.play();
        }
    }
    public void onFinishUse() {
        this.stop();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof SonicSound other))
            return false;

        if (this.player == null || other.player == null) return false; // AHHH

        return this.player.getUuid().equals(other.player.getUuid());
    }
}

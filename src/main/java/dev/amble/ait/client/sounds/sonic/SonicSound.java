package dev.amble.ait.client.sounds.sonic;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

import dev.amble.ait.client.sounds.PositionedLoopingSound;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;

public class SonicSound extends PositionedLoopingSound {
    private final AbstractClientPlayerEntity player;
    private boolean hasPlayedOnSound = false;
    private boolean hasPlayedOffSound = false;
    private float lastYaw;
    private float lastPitch;

    public SonicSound(AbstractClientPlayerEntity player) {
        super(AITSounds.SONIC_USE, SoundCategory.PLAYERS, player.getBlockPos(), 1f, 1f);
        this.player = player;
        this.lastYaw = player.getYaw();
        this.lastPitch = player.getPitch();
    }

    @Override
    public void tick() {
        super.tick();

        if (!shouldPlay(this.player)) {
            this.stop();
            return;
        }

        this.updatePosition();
        this.updatePitchBasedOnCameraMovement();
    }

    public static boolean shouldPlay(PlayerEntity player) {
        return player.isUsingItem() && player.getActiveItem().isOf(AITItems.SONIC_SCREWDRIVER);
    }

    public void play() {
        if (!hasPlayedOnSound) {
            playSoundAtPlayer(AITSounds.SONIC_ON);
            hasPlayedOnSound = true;
            hasPlayedOffSound = false;
        }

        MinecraftClient.getInstance().getSoundManager().play(this);
    }

    public boolean isPlaying() {
        return MinecraftClient.getInstance().getSoundManager().isPlaying(this);
    }

    public void stop() {
        MinecraftClient.getInstance().getSoundManager().stop(this);

        if (!hasPlayedOffSound) {
            playSoundAtPlayer(AITSounds.SONIC_OFF);
            hasPlayedOffSound = true;
            hasPlayedOnSound = false;
        }
    }

    private void updatePosition() {
        this.setPosition(this.player.getBlockPos());
    }

    private void updatePitchBasedOnCameraMovement() {
        float currentYaw = this.player.getYaw();
        float currentPitch = this.player.getPitch();

        float yawChange = Math.abs(currentYaw - lastYaw);
        float pitchChange = Math.abs(currentPitch - lastPitch);

        float totalCameraSpeed = yawChange + pitchChange;

        float newPitch = 1.2f + (totalCameraSpeed * 0.02f);
        newPitch = Math.max(1.2f, Math.min(newPitch, 1.5f));

        this.setPitch(newPitch);

        this.lastYaw = currentYaw;
        this.lastPitch = currentPitch;
    }

    public void onUse() {
        if (!this.isPlaying()) {
            this.play();
        }
    }

    public void onFinishUse() {
        this.stop();
    }

    private void playSoundAtPlayer(SoundEvent sound) {
        World world = MinecraftClient.getInstance().world;
        if (world != null) {
            world.playSound(
                    this.player.getX(), this.player.getY(), this.player.getZ(),
                    sound, SoundCategory.PLAYERS, 1.0f, 1.0f, false
            );
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SonicSound other)) return false;
        return this.player != null && other.player != null && this.player.getUuid().equals(other.player.getUuid());
    }
}

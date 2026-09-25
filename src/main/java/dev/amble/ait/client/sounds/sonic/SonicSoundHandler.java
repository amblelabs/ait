package dev.amble.ait.client.sounds.sonic;

import java.util.HashMap;
import java.util.UUID;

import dev.amble.ait.api.ClientWorldEvents;
import dev.amble.ait.client.sounds.ClientSoundManager;

import net.minecraft.client.network.AbstractClientPlayerEntity;

public class SonicSoundHandler {
    private final HashMap<UUID, SonicSound> sounds;

    static {
        ClientWorldEvents.CHANGE_WORLD.register((client, world) -> {
            SonicSoundHandler handler = ClientSoundManager.getSonicSound();
            handler.stopAll();
        });
    }

    public SonicSoundHandler() {
        this.sounds = new HashMap<>();
    }

    public SonicSound get(AbstractClientPlayerEntity player) {
        return this.sounds.computeIfAbsent(
                player.getUuid(),
                uuid -> new SonicSound(player)
        );
    }

    public void onUse(AbstractClientPlayerEntity user) {
        this.get(user).onUse();
    }

    public void onFinishUse(AbstractClientPlayerEntity user) {
        this.get(user).onFinishUse();
    }

    public void stopAll() {
        this.sounds.values().forEach(SonicSound::stop);
        this.sounds.clear();
    }
}

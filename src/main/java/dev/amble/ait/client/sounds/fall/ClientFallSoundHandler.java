package dev.amble.ait.client.sounds.fall;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import dev.amble.ait.client.sounds.LoopingSound;
import dev.amble.ait.client.sounds.PlayerFollowingLoopingSound;
import dev.amble.ait.client.sounds.SoundHandler;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;

public class ClientFallSoundHandler extends SoundHandler {

    public static LoopingSound FLYING;

    public LoopingSound getFlyingSound() {
        if (FLYING == null)
            FLYING = createFlyingSound();

        return FLYING;
    }

    private LoopingSound createFlyingSound() {
        return new PlayerFollowingLoopingSound(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.AMBIENT, 10f);
    }

    public static ClientFallSoundHandler create() {
        ClientFallSoundHandler handler = new ClientFallSoundHandler();

        handler.generate();
        return handler;
    }

    private void generate() {
        if (FLYING == null)
            FLYING = createFlyingSound();

        this.ofSounds(FLYING);
    }

    private boolean shouldPlaySound(ClientTardis tardis) {
        return tardis != null && tardis.flight().falling().get();
    }

    public void tick(MinecraftClient client) {
        ClientTardis tardis = ClientTardisUtil.getCurrentTardis();

        if (this.sounds == null)
            this.generate();

        if (this.shouldPlaySound(tardis)) {
            this.startIfNotPlaying(this.getFlyingSound());
        } else {
            this.stopSounds();
        }
    }
}

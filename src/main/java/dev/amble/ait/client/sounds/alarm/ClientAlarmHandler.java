package dev.amble.ait.client.sounds.alarm;

import dev.amble.ait.client.sounds.LoopingSound;
import dev.amble.ait.client.sounds.PlayerFollowingLoopingSound;
import dev.amble.ait.client.sounds.SoundHandler;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientShakeUtil;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.AITSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;

// Client only class. One of the last surviving remnants of Duzocode.
public class ClientAlarmHandler extends SoundHandler {

    public static LoopingSound CLOISTER_INTERIOR;

    public LoopingSound getInteriorCloister() {
        if (CLOISTER_INTERIOR == null)
            CLOISTER_INTERIOR = createAlarmSound();

        return CLOISTER_INTERIOR;
    }

    private LoopingSound createAlarmSound() {
        return new PlayerFollowingLoopingSound(AITSounds.CLOISTER, SoundCategory.AMBIENT, 10f);
    }

    public static ClientAlarmHandler create() {
        ClientAlarmHandler handler = new ClientAlarmHandler();

        handler.generate();
        return handler;
    }

    private void generate() {
        if (CLOISTER_INTERIOR == null)
            CLOISTER_INTERIOR = createAlarmSound();

        this.ofSounds(CLOISTER_INTERIOR);
    }

    private boolean shouldPlaySound(ClientTardis tardis) {
        return tardis != null && tardis.alarm().enabled().get();
    }

    public void tick(MinecraftClient client) {
        ClientTardis tardis = ClientTardisUtil.getCurrentTardis();

        if (this.sounds == null)
            this.generate();

        if (this.shouldPlaySound(tardis)) {
            this.startIfNotPlaying(this.getInteriorCloister());
            ClientShakeUtil.shake(0.15f);
        } else {
            this.stopSounds();
        }
    }
}

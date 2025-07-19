package dev.amble.ait.client.sounds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;

public class SoundHandler {

    private static final List<SoundInstance> EMPTY = List.of();

    protected List<SoundInstance> sounds;

    protected void ofSounds(SoundInstance... sound) {
        if (Arrays.stream(sound).anyMatch(Objects::isNull)) {
            this.sounds = EMPTY;
            return;
        }

        this.sounds = List.of(sound);
    }

    public static SoundHandler create(SoundInstance... list) {
        SoundHandler handler = new SoundHandler();

        handler.sounds = new ArrayList<>();
        handler.sounds.addAll(List.of(list));

        return handler;
    }

    public void startIfNotPlaying(SoundEvent event) {
        if (!isPlaying(event))
            startSound(event);
    }

    public void startIfNotPlaying(SoundInstance sound) {
        if (!isPlaying(sound))
            startSound(sound);
    }

    public boolean isPlaying(SoundEvent event) {
        return MinecraftClient.getInstance().getSoundManager().isPlaying(findSoundByEvent(event));
    }

    public boolean isPlaying(SoundInstance sound) {
        return MinecraftClient.getInstance().getSoundManager().isPlaying(sound);
    }

    /**
     * Searches through the sounds in this handler and starts playing whichever one
     * matches the SoundEvent given
     *
     * @param event
     *            the event to search for
     */
    public void startSound(SoundEvent event) {
        SoundInstance sound = findSoundByEvent(event);

        MinecraftClient.getInstance().getSoundManager().play(sound);
    }

    public void startSound(SoundInstance sound) {
        if (sound == null)
            return;
        MinecraftClient.getInstance().getSoundManager().play(sound);
    }

    public void stopSound(SoundEvent event) {
        MinecraftClient.getInstance().getSoundManager().stop(findSoundByEvent(event));
    }

    public void stopSound(SoundInstance sound) {
        MinecraftClient.getInstance().getSoundManager().stop(sound);
    }

    public void stopSounds() {
        if (this.sounds == null)
            return;

        for (SoundInstance sound : this.sounds) {
            this.stopSound(sound);
        }
    }

    /**
     * Finds the first sound instance that matches the event given FIXME i hate this
     * sm and it doesnt work for sounds which are randomised
     */
    public SoundInstance findSoundByEvent(SoundEvent event) {
        return findSoundById(event.getId());
    }

    public SoundInstance findSoundById(Identifier id) {
        Identifier temp;

        for (SoundInstance sound : this.sounds) {
            temp = sound.getId();

            if (temp.equals(id))
                return sound;
        }

        AITMod.LOGGER.error("Could not find sound {} in list, returning empty sound!", id);
        return new PlayerFollowingLoopingSound(SoundEvents.INTENTIONALLY_EMPTY, SoundCategory.NEUTRAL);
    }
}

package dev.amble.ait.core.tardis.control.sound;

import dev.amble.lib.register.AmbleRegistries;
import dev.amble.lib.register.datapack.SimpleDatapackRegistry;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.control.impl.*;
import dev.amble.ait.data.schema.console.ConsoleTypeSchema;

public class ControlSoundRegistry extends SimpleDatapackRegistry<ControlSound> {
    private static final ControlSoundRegistry instance = new ControlSoundRegistry();

    public static final SimpleRegistry<ControlSound> FALLBACKS = FabricRegistryBuilder
            .createSimple(RegistryKey.<ControlSound>ofRegistry(AITMod.id("control_sound_fallback")))
            .buildAndRegister();

    public ControlSoundRegistry() {
        super(ControlSound::fromInputStream, ControlSound.CODEC, "control_sounds", true, AITMod.MOD_ID);
    }

    public static ControlSoundRegistry getInstance() {
        return instance;
    }

    public static ControlSound EMPTY;

    @Override
    public ControlSound fallback() {
        return EMPTY;
    }

    @Override
    protected void defaults() {
        EMPTY = new ControlSound(AITMod.id("empty"), AITMod.id("empty"), AITSounds.ERROR.getId(), AITSounds.ERROR.getId());
    }

    /**
     * Register a fallback sound, which will be used as the default sound when a control or console does not have a specific sound
     * The identifier of the sound will be the same as the control id
     * @param val the sound to register
     * @return the sound that was registered
     */
    private ControlSound registerFallback(ControlSound val) {
        return Registry.register(FALLBACKS, val.controlId(), val);
    }

    /**
     * Get the control sound for a control and console
     * Will return
     * - the sound for the specific control and console
     * OR
     * - the fallback sound for the specific console as defined & registered using {@linkplain ControlSound#forFallback(Identifier, SoundEvent, SoundEvent)}
     * OR
     * - the fallback sound for the specific control as defined from {@linkplain Control#getFallbackSound()}
     * OR
     * - meow, meow meow meow.
     * @param consoleId console id to look for
     * @param controlId control id to look for
     * @return the control sound
     */
    public ControlSound get(Identifier consoleId, Identifier controlId) {
        ControlSound possible = this.get(ControlSound.mergeIdentifiers(controlId, consoleId));
        if (possible != null) {
            return possible;
        }

        // iterate through to find matching
        for (ControlSound sound : this.REGISTRY.values()) {
            if (sound.controlId().equals(controlId) && sound.consoleId().equals(consoleId)) {
                return sound;
            }
        }

        possible = FALLBACKS.get(controlId);

        if (possible != null) {
            return possible;
        }

        return this.fallback();
    }
    public ControlSound get(ConsoleTypeSchema console, Control control) {
        return this.get(console.id(), control.id());
    }

    public static void init() {
        AmbleRegistries.getInstance().register(getInstance());

        instance.registerFallback(ControlSound.forFallback(AutoPilotControl.ID, AITSounds.PROTOCOL_116_ON, AITSounds.PROTOCOL_116_OFF));
        instance.registerFallback(ControlSound.forFallback(CloakControl.ID, AITSounds.PROTOCOL_3, AITSounds.PROTOCOL_3ALT));
        instance.registerFallback(ControlSound.forFallback(DoorControl.ID, AITSounds.DOOR_CONTROL, AITSounds.DOOR_CONTROLALT));
        instance.registerFallback(ControlSound.forFallback(HandBrakeControl.ID, AITSounds.HANDBRAKE_DOWN, AITSounds.HANDBRAKE_UP));
        instance.registerFallback(ControlSound.forFallback(RefuelerControl.ID, AITSounds.ENGINE_REFUEL_CRANK, AITSounds.ENGINE_REFUEL));
        instance.registerFallback(ControlSound.forFallback(ShieldsControl.ID, AITSounds.HANDBRAKE_LEVER_PULL, AITSounds.SHIELDS));
    }
}

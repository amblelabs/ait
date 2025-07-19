package dev.amble.ait.core.sounds.flight;

import dev.amble.lib.register.datapack.SimpleDatapackRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.resource.ResourceType;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;

public class FlightSoundRegistry extends SimpleDatapackRegistry<FlightSound> {
    private static final FlightSoundRegistry instance = new FlightSoundRegistry();

    // just for spotless apply to apply
    public FlightSoundRegistry() {
        super(FlightSound::fromInputStream, FlightSound.CODEC, "fx/flight", true, AITMod.MOD_ID);
    }

    public static FlightSoundRegistry getInstance() {
        return instance;
    }

    public static FlightSound DEFAULT;

    @Override
    public void onCommonInit() {
        super.onCommonInit();
        this.defaults();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
    }

    @Override
    protected void defaults() {
        DEFAULT = register(new FlightSound(AITMod.id("default"), AITSounds.FLIGHT_LOOP.getId(), 80, "default"));
    }

    @Override
    public FlightSound fallback() {
        return DEFAULT;
    }
}

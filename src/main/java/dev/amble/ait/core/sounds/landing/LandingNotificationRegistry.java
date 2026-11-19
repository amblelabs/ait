package dev.amble.ait.core.sounds.landing;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.lib.register.unlockable.UnlockableRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.resource.ResourceType;
import net.minecraft.sound.SoundEvents;

public class LandingNotificationRegistry extends UnlockableRegistry<LandingNotification> {
    private static final LandingNotificationRegistry instance = new LandingNotificationRegistry();

    public LandingNotificationRegistry() {
        super(LandingNotification::fromInputStream, LandingNotification.CODEC, "fx/landing", true, AITMod.MOD_ID);
    }

    public static LandingNotificationRegistry getInstance() {
        return instance;
    }

    public static LandingNotification DEFAULT;
    public static LandingNotification OFF;

    @Override
    public void onCommonInit() {
        super.onCommonInit();
        this.defaults();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
    }

    @Override
    protected void defaults() {
        DEFAULT = register(new LandingNotification(AITMod.id("default"), AITSounds.NAV_NOTIFICATION.getId(), "default"));
        OFF = register(new LandingNotification(AITMod.id("off"), SoundEvents.INTENTIONALLY_EMPTY.getId(), "off"));
    }

    @Override
    public LandingNotification fallback() {
        return DEFAULT;
    }
}

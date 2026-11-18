package dev.amble.ait.compat.svc;

import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import dev.amble.ait.AITMod;

public class AITVoiceChatPlugin implements VoicechatPlugin {
    @Override
    public String getPluginId() {
        return AITMod.MOD_ID + "_plugin";
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
    }

    public void onServerStarted(VoicechatServerStartedEvent event) {
        System.out.println("Hello from AIT SVC Compat! | " + event.getVoicechat());
    }
}

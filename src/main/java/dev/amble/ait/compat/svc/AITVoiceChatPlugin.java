package dev.amble.ait.compat.svc;

import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.VolumeCategory;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.PlayerDisconnectedEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStoppedEvent;
import de.maxhenkel.voicechat.api.packets.StaticSoundPacket;
import dev.amble.ait.AITMod;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

public class AITVoiceChatPlugin implements VoicechatPlugin {

    // Volume Categories
    public static String TARDIS_BROADCAST = "tardis_cast";
    public static String TARDIS_COMMS = "tardis_comms";

    @Override
    public String getPluginId() {
        return AITMod.MOD_ID + "_plugin";
    }

    private static VoicechatServerApi serverApi;

    public static @Nullable VoicechatServerApi getServerApi() {
        return serverApi;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
        registration.registerEvent(VoicechatServerStoppedEvent.class, event -> {
            serverApi = null;
            AITVoiceChatBroadcast.clear();
        });
        registration.registerEvent(PlayerDisconnectedEvent.class, event -> AITVoiceChatBroadcast.stop(event.getPlayerUuid()));
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacket);
    }

    private void onMicrophonePacket(MicrophonePacketEvent event) {
        VoicechatConnection sender = event.getSenderConnection();

        if (sender == null)
            return;

        UUID senderId = sender.getPlayer().getUuid();
        List<UUID> listeners = AITVoiceChatBroadcast.listenersFor(senderId);

        if (listeners.isEmpty())
            return;

        VoicechatServerApi api = event.getVoicechat();
        StaticSoundPacket packet = event.getPacket().staticSoundPacketBuilder()
                .category(TARDIS_BROADCAST)
                .build();

        for (UUID listener : listeners) {
            if (listener.equals(senderId))
                continue;

            VoicechatConnection connection = api.getConnectionOf(listener);

            if (connection != null && connection.isConnected() && !connection.isDisabled())
                api.sendStaticSoundPacketTo(connection, packet);
        }
    }

    public void onServerStarted(VoicechatServerStartedEvent event) {
        VoicechatServerApi api = event.getVoicechat();
        serverApi = api;

        VolumeCategory tardisBroadcast = api.volumeCategoryBuilder()
                .setId(TARDIS_BROADCAST)
                .setName("TARDIS Broadcasts")
                .setDescription("The volume of TARDIS broadcastings.")
                .setIcon(getIcon("category_music_discs.png"))
                .build();

        VolumeCategory tardisComms = api.volumeCategoryBuilder()
                .setId(TARDIS_COMMS)
                .setName("TARDIS Comms")
                .setDescription("The volume of TARDIS communications.")
                .setIcon(getIcon("category_music_discs.png"))
                .build();

        api.registerVolumeCategory(tardisBroadcast);
        api.registerVolumeCategory(tardisComms);
    }

    private int @Nullable [][] getIcon(String path) {
        try {
            Enumeration<URL> resources = AITVoiceChatPlugin.class.getClassLoader().getResources(path);
            while (resources.hasMoreElements()) {
                BufferedImage bufferedImage = ImageIO.read(resources.nextElement().openStream());
                if (bufferedImage.getWidth() != 16) {
                    continue;
                }
                if (bufferedImage.getHeight() != 16) {
                    continue;
                }
                int[][] image = new int[16][16];
                for (int x = 0; x < bufferedImage.getWidth(); x++) {
                    for (int y = 0; y < bufferedImage.getHeight(); y++) {
                        image[x][y] = bufferedImage.getRGB(x, y);
                    }
                }
                return image;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

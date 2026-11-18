package dev.amble.ait.compat.svc;

import de.maxhenkel.voicechat.api.Position;
import de.maxhenkel.voicechat.api.ServerPlayer;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.VolumeCategory;
import de.maxhenkel.voicechat.api.audiochannel.StaticAudioChannel;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.PlayerDisconnectedEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStoppedEvent;
import de.maxhenkel.voicechat.api.packets.LocationalSoundPacket;
import de.maxhenkel.voicechat.api.packets.StaticSoundPacket;
import dev.amble.ait.AITMod;
import dev.amble.ait.compat.portal.PortalsAPI;
import dev.amble.ait.core.tardis.comms.TardisComms;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AITVoiceChatPlugin implements VoicechatPlugin {

    // Volume Categories
    public static String TARDIS_BROADCAST = "tardis_broadcast";
    public static String TARDIS_COMMS = "tardis_comms";

    @Override
    public String getPluginId() {
        return AITMod.MOD_ID + "_plugin";
    }

    private static VoicechatServerApi serverApi;

    private static final ScheduledExecutorService PLAYBACK = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "AIT Comms Playback");
        thread.setDaemon(true);
        return thread;
    });

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
        this.relayThroughPortals(event, sender, senderId);
        this.relayComms(event, senderId);

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

    private void relayComms(MicrophonePacketEvent event, UUID senderId) {
        TardisComms.record(senderId, event.getPacket().getOpusEncodedData());

        List<UUID> listeners = TardisComms.routeFor(senderId);

        if (listeners.isEmpty())
            return;

        VoicechatServerApi api = event.getVoicechat();
        StaticSoundPacket packet = event.getPacket().staticSoundPacketBuilder()
                .category(TARDIS_COMMS)
                .build();

        for (UUID listener : listeners) {
            if (listener.equals(senderId))
                continue;

            VoicechatConnection connection = api.getConnectionOf(listener);

            if (connection != null && connection.isConnected() && !connection.isDisabled())
                api.sendStaticSoundPacketTo(connection, packet);
        }
    }

    private static void playMessage(List<UUID> listeners, List<byte[]> frames) {
        VoicechatServerApi api = serverApi;

        if (api == null || frames.isEmpty())
            return;

        List<StaticAudioChannel> channels = new ArrayList<>();

        for (UUID listener : listeners) {
            VoicechatConnection connection = api.getConnectionOf(listener);

            if (connection == null || !connection.isConnected() || connection.isDisabled())
                continue;

            StaticAudioChannel channel = api.createStaticAudioChannel(UUID.randomUUID(),
                    connection.getPlayer().getServerLevel(), connection);

            if (channel == null)
                continue;

            channel.setCategory(TARDIS_COMMS);
            channels.add(channel);
        }

        if (channels.isEmpty())
            return;

        AtomicInteger index = new AtomicInteger();
        AtomicReference<ScheduledFuture<?>> task = new AtomicReference<>();

        task.set(PLAYBACK.scheduleAtFixedRate(() -> {
            int frame = index.getAndIncrement();

            if (frame >= frames.size()) {
                channels.forEach(StaticAudioChannel::flush);
                ScheduledFuture<?> self = task.get();

                if (self != null)
                    self.cancel(false);

                return;
            }

            for (StaticAudioChannel channel : channels) {
                if (!channel.isClosed())
                    channel.send(frames.get(frame));
            }
        }, 20, 20, TimeUnit.MILLISECONDS));
    }

    private void relayThroughPortals(MicrophonePacketEvent event, VoicechatConnection sender, UUID senderId) {
        List<PortalsAPI.VoiceLink> links = PortalsAPI.VOICE_LINKS.get();

        if (links.isEmpty() || !(sender.getPlayer().getPlayer() instanceof ServerPlayerEntity player))
            return;

        MinecraftServer server = player.getServer();

        if (server == null)
            return;

        VoicechatServerApi api = event.getVoicechat();
        double range = api.getVoiceChatDistance();
        Vec3d mouth = player.getEyePos();
        RegistryKey<World> dimension = player.getWorld().getRegistryKey();
        Set<UUID> reached = new HashSet<>();

        for (PortalsAPI.VoiceLink link : links) {
            if (!link.fromDim().equals(dimension) || mouth.distanceTo(link.from()) > range)
                continue;

            ServerWorld target = server.getWorld(link.toDim());

            if (target == null)
                continue;

            Vec3d through = link.transform().apply(mouth);
            Position position = api.createPosition(through.x, through.y, through.z);
            LocationalSoundPacket packet = event.getPacket().locationalSoundPacketBuilder()
                    .position(position)
                    .distance((float) range)
                    .build();

            for (ServerPlayer listener : api.getPlayersInRange(api.fromServerLevel(target), position, range)) {
                UUID listenerId = listener.getUuid();

                if (listenerId.equals(senderId) || !reached.add(listenerId))
                    continue;

                VoicechatConnection connection = api.getConnectionOf(listenerId);

                if (connection != null && connection.isConnected() && !connection.isDisabled())
                    api.sendLocationalSoundPacketTo(connection, packet);
            }
        }
    }

    public void onServerStarted(VoicechatServerStartedEvent event) {
        VoicechatServerApi api = event.getVoicechat();
        serverApi = api;
        TardisComms.PLAYBACK = AITVoiceChatPlugin::playMessage;

        VolumeCategory tardisBroadcast = api.volumeCategoryBuilder()
                .setId(TARDIS_BROADCAST)
                .setName("TARDIS Broadcast")
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

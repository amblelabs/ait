package dev.amble.ait.compat.svc;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

import javax.imageio.ImageIO;

import de.maxhenkel.voicechat.api.Position;
import de.maxhenkel.voicechat.api.ServerPlayer;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.VolumeCategory;
import de.maxhenkel.voicechat.api.audiochannel.StaticAudioChannel;
import de.maxhenkel.voicechat.api.events.ClientReceiveSoundEvent;
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
import dev.amble.ait.core.tardis.comms.TardisPhones;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
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

    private static final double CLOSED_DOOR_DISTANCE = 1.6;
    private static final Identifier CATEGORY_ICON = AITMod.id("textures/item/phone");
    private static final float EARPIECE_REACH = 4f;

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

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            registration.registerEvent(ClientReceiveSoundEvent.StaticSound.class, AITVoiceFilters::onStaticSound);
            registration.registerEvent(ClientReceiveSoundEvent.LocationalSound.class, AITVoiceFilters::onLocationalSound);
        }
    }

    private void onMicrophonePacket(MicrophonePacketEvent event) {
        VoicechatConnection sender = event.getSenderConnection();

        if (sender == null)
            return;

        UUID senderId = sender.getPlayer().getUuid();
        this.relayThroughLinks(event, sender, senderId);
        this.relayComms(event, senderId);
        this.relayEarpieces(event, sender, senderId);

        List<UUID> listeners = AITVoiceChatBroadcast.listenersFor(senderId);

        if (listeners.isEmpty())
            return;

        VoicechatServerApi api = event.getVoicechat();
        StaticSoundPacket packet = event.getPacket().staticSoundPacketBuilder()
                .channelId(AITVoiceChannels.tag(AITVoiceChannels.Kind.BROADCAST, senderId))
                .category(TARDIS_BROADCAST)
                .build();

        for (UUID listener : listeners) {
            if (listener.equals(senderId))
                continue;

            VoicechatConnection connection = api.getConnectionOf(listener);

            if (connection != null && connection.isConnected() && !connection.isDisabled())
                api.sendStaticSoundPacketTo(connection, packet);
        }

        if (event.isCancellable())
            event.cancel();
    }

    private void relayComms(MicrophonePacketEvent event, UUID senderId) {
        TardisComms.record(senderId, event.getPacket().getOpusEncodedData());

        List<UUID> listeners = TardisComms.routeFor(senderId);

        if (listeners.isEmpty())
            return;

        VoicechatServerApi api = event.getVoicechat();
        StaticSoundPacket packet = event.getPacket().staticSoundPacketBuilder()
                .channelId(AITVoiceChannels.tag(AITVoiceChannels.Kind.COMMS, senderId))
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

    private void relayEarpieces(MicrophonePacketEvent event, VoicechatConnection sender, UUID senderId) {
        List<TardisPhones.Speaker> speakers = TardisComms.speakersFor(senderId);

        if (speakers.isEmpty() || !(sender.getPlayer().getPlayer() instanceof ServerPlayerEntity player))
            return;

        MinecraftServer server = player.getServer();

        if (server == null)
            return;

        VoicechatServerApi api = event.getVoicechat();

        for (TardisPhones.Speaker speaker : speakers) {
            ServerWorld world = server.getWorld(speaker.world());

            if (world == null)
                continue;

            Position position = api.createPosition(speaker.pos().x, speaker.pos().y, speaker.pos().z);
            LocationalSoundPacket packet = event.getPacket().locationalSoundPacketBuilder()
                    .position(position)
                    .distance(EARPIECE_REACH)
                    .channelId(AITVoiceChannels.tag(AITVoiceChannels.Kind.EARPIECE, senderId))
                    .category(TARDIS_COMMS)
                    .build();

            for (ServerPlayer listener : api.getPlayersInRange(api.fromServerLevel(world), position, EARPIECE_REACH)) {
                if (listener.getUuid().equals(senderId))
                    continue;

                VoicechatConnection connection = api.getConnectionOf(listener.getUuid());

                if (connection != null && connection.isConnected() && !connection.isDisabled())
                    api.sendLocationalSoundPacketTo(connection, packet);
            }
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

            StaticAudioChannel channel = api.createStaticAudioChannel(AITVoiceChannels.tag(AITVoiceChannels.Kind.COMMS, UUID.randomUUID()),
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


    private void relayThroughLinks(MicrophonePacketEvent event, VoicechatConnection sender, UUID senderId) {
        List<VoiceLink> links = new ArrayList<>(PortalsAPI.VOICE_LINKS.get());
        links.addAll(AITVoiceDoors.links());

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

        for (VoiceLink link : links) {
            if (!link.fromDim().equals(dimension))
                continue;

            double toDoor = mouth.distanceTo(link.from());

            if (toDoor > range)
                continue;

            ServerWorld target = server.getWorld(link.toDim());

            if (target == null)
                continue;

            if (link.exit() == null) {
                Vec3d source = link.transform().apply(mouth);
                Position position = api.createPosition(source.x, source.y, source.z);
                LocationalSoundPacket packet = event.getPacket().locationalSoundPacketBuilder()
                        .position(position)
                        .distance((float) range)
                        .build();

                for (ServerPlayer listener : api.getPlayersInRange(api.fromServerLevel(target), position, range))
                    this.sendLocational(api, listener.getUuid(), senderId, reached, packet);

                continue;
            }

            this.relayThroughDoor(event, api, link, target, senderId, toDoor, range, reached);
        }
    }

    private void relayThroughDoor(MicrophonePacketEvent event, VoicechatServerApi api, VoiceLink link, ServerWorld target,
                                  UUID senderId, double toDoor, double range, Set<UUID> reached) {
        Vec3d exit = link.exit();
        double factor = link.muffle() == VoiceLink.Muffle.NONE ? 1.0 : CLOSED_DOOR_DISTANCE;
        double listenerReach = range / factor - toDoor;

        if (listenerReach <= 0)
            return;

        Position door = api.createPosition(exit.x, exit.y, exit.z);
        AITVoiceChannels.Kind kind = switch (link.muffle()) {
            case NONE -> null;
            case INSIDE -> AITVoiceChannels.Kind.MUFFLED;
            case OUTSIDE -> AITVoiceChannels.Kind.MUFFLED_OUTSIDE;
        };

        for (ServerPlayer listener : api.getPlayersInRange(api.fromServerLevel(target), door, listenerReach)) {
            Position at = listener.getPosition();
            Vec3d ear = new Vec3d(at.getX(), at.getY(), at.getZ());
            Vec3d toward = exit.subtract(ear);
            double fromDoor = toward.length();
            double total = (toDoor + fromDoor) * factor;

            if (total > range)
                continue;

            Vec3d direction = fromDoor < 1.0E-3 ? Vec3d.ZERO : toward.multiply(1 / fromDoor);
            Vec3d source = exit.add(direction.multiply(total - fromDoor));

            LocationalSoundPacket.Builder<?> builder = event.getPacket().locationalSoundPacketBuilder()
                    .position(api.createPosition(source.x, source.y, source.z))
                    .distance((float) range);

            if (kind != null)
                builder.channelId(AITVoiceChannels.tag(kind, senderId));

            this.sendLocational(api, listener.getUuid(), senderId, reached, builder.build());
        }
    }

    private void sendLocational(VoicechatServerApi api, UUID listenerId, UUID senderId, Set<UUID> reached, LocationalSoundPacket packet) {
        if (listenerId.equals(senderId) || !reached.add(listenerId))
            return;

        VoicechatConnection connection = api.getConnectionOf(listenerId);

        if (connection != null && connection.isConnected() && !connection.isDisabled())
            api.sendLocationalSoundPacketTo(connection, packet);
    }

    public void onServerStarted(VoicechatServerStartedEvent event) {
        VoicechatServerApi api = event.getVoicechat();
        serverApi = api;
        TardisComms.PLAYBACK = AITVoiceChatPlugin::playMessage;

        int[][] icon = icon(CATEGORY_ICON);

        VolumeCategory tardisBroadcast = api.volumeCategoryBuilder()
                .setId(TARDIS_BROADCAST)
                .setName("TARDIS Broadcast")
                .setDescription("The volume of TARDIS broadcastings.")
                .setIcon(icon)
                .build();

        VolumeCategory tardisComms = api.volumeCategoryBuilder()
                .setId(TARDIS_COMMS)
                .setName("TARDIS Comms")
                .setDescription("The volume of TARDIS communications.")
                .setIcon(icon)
                .build();

        api.registerVolumeCategory(tardisBroadcast);
        api.registerVolumeCategory(tardisComms);
    }

    private static int @Nullable [][] icon(Identifier texture) {
        String path = "/assets/" + texture.getNamespace() + "/" + texture.getPath() + ".png";

        try (InputStream stream = AITVoiceChatPlugin.class.getResourceAsStream(path)) {
            if (stream == null)
                return null;

            BufferedImage image = ImageIO.read(stream);

            if (image == null || image.getWidth() != 16 || image.getHeight() != 16)
                return null;

            int[][] pixels = new int[16][16];

            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++)
                    pixels[x][y] = image.getRGB(x, y);
            }

            return pixels;
        } catch (IOException e) {
            AITMod.LOGGER.warn("Failed to read voice chat icon {}", path, e);
            return null;
        }
    }
}

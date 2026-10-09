package dev.drtheo.portal;

import com.mojang.authlib.GameProfile;
import dev.amble.ait.mixin.boti.ProxyChunkStorageAccessor;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PacketProxyPlayer extends FakePlayer {

    private static final String NAME = "[Ptl Packet Proxy]";

    private static final Map<ServerWorld, List<PacketProxyPlayer>> ATTACHED = new HashMap<>();

    // one per portal, the player manager keeps stats and advancements per uuid
    public PacketProxyPlayer(ServerWorld world, UUID portal) {
        super(world, new GameProfile(UUID.nameUUIDFromBytes((NAME + portal).getBytes(StandardCharsets.UTF_8)), NAME));
        this.networkHandler = new ProxyNetworkHandler(this);
    }

    // still watches chunks, as a spectator that doesn't make them tick or spawn mobs
    @Override
    public boolean isSpectator() {
        return true;
    }

    public void setPacketListener(ProxyPacketListener listener) {
        ((ProxyNetworkHandler) this.networkHandler).setListener(listener);
    }

    // never added to the world, so nothing that looks for players finds it: it only watches chunks and tracks entities
    public void attach() {
        ServerWorld world = this.getServerWorld();

        ATTACHED.computeIfAbsent(world, w -> new ArrayList<>()).add(this);
        ((ProxyChunkStorageAccessor) world.getChunkManager().threadedAnvilChunkStorage).invokeHandlePlayerAddedOrRemoved(this, true);
        world.getChunkManager().updatePosition(this);
    }

    public void release() {
        ServerWorld world = this.getServerWorld();
        List<PacketProxyPlayer> attached = ATTACHED.get(world);

        if (attached == null || !attached.remove(this))
            return;

        if (attached.isEmpty())
            ATTACHED.remove(world);

        // a world being deleted is gone from the server before it's closed
        if (world.getServer().getWorld(world.getRegistryKey()) == world)
            world.getChunkManager().unloadEntity(this);
    }

    public static List<PacketProxyPlayer> attached(ServerWorld world) {
        return ATTACHED.getOrDefault(world, List.of());
    }

    public static List<ServerPlayerEntity> withProxies(ServerWorld world, List<ServerPlayerEntity> players) {
        List<PacketProxyPlayer> proxies = ATTACHED.get(world);

        if (proxies == null)
            return players;

        List<ServerPlayerEntity> all = new ArrayList<>(players.size() + proxies.size());
        all.addAll(players);
        all.addAll(proxies);
        return all;
    }

    public void onChunkEntered() {
        this.getServerWorld().getChunkManager().updatePosition(this);
    }
}

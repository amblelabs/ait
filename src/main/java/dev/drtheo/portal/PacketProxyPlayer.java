package dev.drtheo.portal;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.world.ServerWorld;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PacketProxyPlayer extends FakePlayer {

    private static final String NAME = "[Ptl Packet Proxy]";

    // one per portal: a world rejects a second entity with the same uuid, and the player manager keeps stats per uuid
    public PacketProxyPlayer(ServerWorld world, UUID portal) {
        super(world, new GameProfile(UUID.nameUUIDFromBytes((NAME + portal).getBytes(StandardCharsets.UTF_8)), NAME));
        this.networkHandler = new ProxyNetworkHandler(this);
    }

    public void setPacketListener(ProxyPacketListener listener) {
        ((ProxyNetworkHandler) this.networkHandler).setListener(listener);
    }

    public void onChunkEntered() {
        this.getServerWorld().getChunkManager().updatePosition(this);
    }
}

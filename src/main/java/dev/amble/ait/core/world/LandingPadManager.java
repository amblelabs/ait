package dev.amble.ait.core.world;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.util.NetworkUtil;
import dev.amble.ait.data.landing.LandingPadRegion;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class LandingPadManager {

    private static final AttachmentType<LandingPadRegion> PERSISTENT = AttachmentRegistry.createPersistent(
            AITMod.id("landing_pads"), LandingPadRegion.CODEC
    );

    public static void init() {

    }

    private final ServerWorld world;

    public LandingPadManager(ServerWorld world) {
        this.world = world;
    }

    @Nullable public LandingPadRegion getRegion(ChunkPos pos) {
        Chunk chunk = this.world.getChunk(pos.x, pos.z, ChunkStatus.FULL, false);

        if (chunk == null)
            return null;

        return chunk.getAttached(PERSISTENT);
    }

    @Nullable public LandingPadRegion getRegion(long pos) {
        return this.getRegion(new ChunkPos(pos));
    }

    @Nullable public LandingPadRegion getRegionAt(BlockPos pos) {
        return this.getRegion(new ChunkPos(pos));
    }

    private LandingPadRegion claim(ChunkPos pos, int y) {
        WorldChunk chunk = this.world.getChunk(pos.x, pos.z);

        if (chunk.hasAttached(PERSISTENT))
            throw new IllegalStateException("Region already occupied");

        LandingPadRegion created = new LandingPadRegion(pos, y, "");
        chunk.setAttached(PERSISTENT, created);

        Network.syncTracked(Network.Action.ADD, this.world, pos);
        return created;
    }

    public LandingPadRegion claim(BlockPos pos) {
        return this.claim(new ChunkPos(pos), world.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))
                .sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX() & 15, pos.getZ() & 15));
    }

    private @Nullable LandingPadRegion release(ChunkPos pos) {
        LandingPadRegion result = this.world.getChunk(pos.x, pos.z).removeAttached(PERSISTENT);

        Network.syncTracked(Network.Action.REMOVE, this.world, pos);
        return result;
    }

    public @Nullable LandingPadRegion releaseAt(BlockPos pos) {
        return this.release(new ChunkPos(pos));
    }

    public static LandingPadManager getInstance(ServerWorld world) {
        return new LandingPadManager(world);
    }

    public static class Network {

        public static final Identifier SYNC = AITMod.id("landingpad_sync");
        public static final Identifier REQUEST = AITMod.id("landingpad_request");

        public static void syncForPlayer(Action action, ServerPlayerEntity player) {
            ServerWorld world = player.getServerWorld();
            ChunkPos pos = player.getChunkPos();

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeEnumConstant(action);

            if (action != Action.CLEAR)
                buf.writeChunkPos(pos);

            if (action == Action.ADD) {
                LandingPadManager manager = LandingPadManager.getInstance(world);
                LandingPadRegion region = manager.getRegion(pos);

                if (region == null)
                    return;

                NetworkUtil.send(player, buf, SYNC, LandingPadRegion.CODEC, region);
                return;
            }

            NetworkUtil.send(player, SYNC, buf);
        }

        public static void syncTracked(Action action, ServerWorld world, ChunkPos pos) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeEnumConstant(action);

            if (action != Action.CLEAR)
                buf.writeChunkPos(pos);

            if (action == Action.ADD) {
                LandingPadManager manager = LandingPadManager.getInstance(world);
                LandingPadRegion region = manager.getRegion(pos);

                if (region == null)
                    return;

                for (ServerPlayerEntity player : PlayerLookup.tracking(world, pos)) {
                    NetworkUtil.send(player, buf, SYNC, LandingPadRegion.CODEC, region);
                }

                return;
            }

            for (ServerPlayerEntity player : PlayerLookup.tracking(world, pos)) {
                NetworkUtil.send(player, SYNC, buf);
            }
        }

        static {
            ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
                syncForPlayer(Action.ADD, handler.getPlayer());
            });

            ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
                syncForPlayer(Action.CLEAR, player);
                syncForPlayer(Action.ADD, player);
            });

            ServerPlayNetworking.registerGlobalReceiver(LandingPadManager.Network.REQUEST, (server, player, handler, buf, responseSender) -> {
                syncForPlayer(Action.ADD, player);
            });
        }

        public enum Action {
            ADD,
            REMOVE,
            CLEAR
        }
    }
}

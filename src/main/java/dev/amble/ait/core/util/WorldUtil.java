package dev.amble.ait.core.util;

import java.util.ArrayList;
import java.util.List;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.util.ServerLifecycleHooks;
import dev.amble.lib.util.TeleportUtil;
import dev.drtheo.scheduler.api.Scheduler;
import dev.drtheo.scheduler.api.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.chunk.Chunk;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.AITDimensions;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.mixin.server.EnderDragonFightAccessor;

@SuppressWarnings("deprecation")
public class WorldUtil {

    private static final List<Identifier> blacklisted = new ArrayList<>();
    private static List<ServerWorld> worlds;
    private static final int SAFE_RADIUS = 3;

    private static ServerWorld OVERWORLD;
    private static ServerWorld TIME_VORTEX;

    public static void init() {
        for (String id : AITMod.CONFIG.SERVER.WORLDS_BLACKLIST) {
            blacklisted.add(Identifier.tryParse(id));
        }

        ServerLifecycleEvents.SERVER_STARTED.register(server -> worlds = getDimensions(server));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> worlds = null);

        ServerWorldEvents.UNLOAD.register((server, world) -> {
            if (world.getRegistryKey() == World.OVERWORLD)
                OVERWORLD = null;

            if (world.getRegistryKey() == AITDimensions.TIME_VORTEX_WORLD)
                TIME_VORTEX = null;
        });

        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.getRegistryKey() == World.OVERWORLD)
                OVERWORLD = world;

            if (world.getRegistryKey() == AITDimensions.TIME_VORTEX_WORLD)
                TIME_VORTEX = world;
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            OVERWORLD = server.getOverworld();
            TIME_VORTEX = server.getWorld(AITDimensions.TIME_VORTEX_WORLD);

            // blacklist all tardises
            for (ServerWorld world : getDimensions(server)) {
                if (TardisServerWorld.isTardisDimension(world)) blacklist(world);
            }
        });

        ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.register((originalEntity, newEntity, origin, destination) -> {
            if (destination == TIME_VORTEX && newEntity instanceof LivingEntity living)
                scheduleVortexFall(living);
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            if (destination == TIME_VORTEX)
                scheduleVortexFall(player);
        });
    }

    private static void scheduleVortexFall(LivingEntity entity) {
        int worldIndex = TIME_VORTEX.getRandom().nextInt(worlds.size());

        Scheduler.get().runTaskLater(() -> {
            if (entity.getWorld() == TIME_VORTEX)
                TeleportUtil.teleport(entity, worlds.get(worldIndex), entity.getPos(), entity.getYaw());
        }, TimeUnit.SECONDS, 5);
    }

    public static ServerWorld getOverworld() {
        return OVERWORLD;
    }

    public static ServerWorld getTimeVortex() {
        return TIME_VORTEX;
    }

    public static List<ServerWorld> getDimensions(MinecraftServer server) {
        List<ServerWorld> worlds = new ArrayList<>();

        for (ServerWorld world : server.getWorlds()) {
            if (isOpen(world.getRegistryKey()))
                worlds.add(world);
        }

        return worlds;
    }

    public static boolean isOpen(RegistryKey<World> world) {
        for (Identifier blacklisted : blacklisted) {
            if (world.getValue().equals(blacklisted))
                return false;
        }

        return true;
    }
    public static void blacklist(RegistryKey<World> world) {
        blacklisted.add(world.getValue());
    }
    public static void blacklist(World world) {
        blacklist(world.getRegistryKey());
    }

    public static int worldIndex(ServerWorld world) {
        for (int i = 0; i < worlds.size(); i++) {
            if (world == worlds.get(i))
                return i;
        }

        return -1;
    }

    public static List<ServerWorld> getOpenWorlds() {
        return worlds;
    }

    public static CachedDirectedGlobalPos locateSafe(CachedDirectedGlobalPos cached,
                                                     TravelHandlerBase.GroundSearch vSearch, boolean hSearch) {
        ServerWorld world = cached.getWorld();
        BlockPos pos = cached.getPos();

        Chunk chunk = world.getChunk(pos);

        if (isSafe(chunk, pos))
            return cached;

        if (hSearch) {
            BlockPos temp = findSafeXZ(world, pos, SAFE_RADIUS);

            if (temp != null)
                return cached.pos(temp);
        }

        int x = pos.getX();
        int z = pos.getZ();

        int y = switch (vSearch) {
            case CEILING -> chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                    pos.getX() & 15, pos.getZ() & 15) + 1;
            case FLOOR -> findSafeBottomY(chunk, pos);
            case MEDIAN -> findSafeMedianY(chunk, pos);
            case NONE -> pos.getY();
        };

        return cached.pos(x, y, z);
    }

    public static BlockPos findSafeXZ(World world, BlockPos original, int radius) {
        BlockPos.Mutable pos = original.mutableCopy();

        int minX = pos.getX() - radius;
        int maxX = pos.getX() + radius;

        int minZ = pos.getZ() - radius;
        int maxZ = pos.getZ() + radius;

        Chunk chunk = null;
        ChunkPos chunkPos = null;

        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                pos.setX(x).setZ(z);

                ChunkPos tempPos = new ChunkPos(pos);
                if (chunkPos == null || !chunkPos.equals(tempPos)) {
                    chunkPos = tempPos;
                    chunk = world.getChunk(chunkPos.x, chunkPos.z);
                }

                if (isSafe(chunk, pos))
                    return pos;
            }
        }

        return null;
    }

    private static int findSafeMedianY(Chunk chunk, BlockPos pos) {
        BlockPos upCursor = pos;
        BlockState floorUp = chunk.getBlockState(upCursor.down());
        BlockState curUp = chunk.getBlockState(upCursor);
        BlockState aboveUp = chunk.getBlockState(upCursor.up());

        BlockPos downCursor = pos;
        BlockState floorDown = chunk.getBlockState(downCursor.down());
        BlockState curDown = chunk.getBlockState(downCursor);
        BlockState aboveDown = chunk.getBlockState(downCursor.up());

        while (true) {
            boolean canGoUp = upCursor.getY() < chunk.getTopY();
            boolean canGoDown = downCursor.getY() > chunk.getBottomY();

            if (!canGoUp && !canGoDown)
                return pos.getY();

            if (canGoUp) {
                if (isSafe(floorUp, curUp, aboveUp))
                    return upCursor.getY() - 1;

                upCursor = upCursor.up();

                floorUp = curUp;
                curUp = aboveUp;
                aboveUp = chunk.getBlockState(upCursor);
            }

            if (canGoDown) {
                if (isSafe(floorDown, curDown, aboveDown))
                    return downCursor.getY() + 1;

                downCursor = downCursor.down();

                curDown = aboveDown;
                aboveDown = floorDown;
                floorDown = chunk.getBlockState(downCursor);
            }
        }
    }

    private static int findSafeBottomY(Chunk chunk, BlockPos pos) {
        BlockPos cursor = pos.withY(chunk.getBottomY() + 2);

        BlockState floor = chunk.getBlockState(cursor.down());
        BlockState current = chunk.getBlockState(cursor);
        BlockState above = chunk.getBlockState(cursor.up());

        while (true) {
            if (cursor.getY() > chunk.getTopY())
                return pos.getY();

            if (isSafe(floor, current, above))
                return cursor.getY() - 1;

            cursor = cursor.up();

            floor = current;
            current = above;
            above = chunk.getBlockState(cursor);
        }
    }

    private static boolean isSafe(BlockState floor, BlockState block1, BlockState block2) {
        return floor.blocksMovement() && !block1.blocksMovement() && !block2.blocksMovement();
    }

    private static boolean isSafe(Chunk chunk, BlockPos pos) {
        BlockState floor = chunk.getBlockState(pos.down());

        if (!floor.blocksMovement())
            return false;

        BlockState curUp = chunk.getBlockState(pos);
        BlockState aboveUp = chunk.getBlockState(pos.up());

        return !curUp.blocksMovement() && !aboveUp.blocksMovement();
    }

    @Environment(EnvType.CLIENT)
    @SuppressWarnings("DataFlowIssue")
    public static String getName(MinecraftClient client) {
        if (client.isInSingleplayer())
            return client.getServer().getSavePath(WorldSavePath.ROOT).getParent().getFileName().toString();

        return client.getCurrentServerEntry().address;
    }

    public static Text worldText(RegistryKey<World> key) {
        Text translated = Text.translatableWithFallback(key.getValue().toTranslationKey("dimension"), fakeTranslate(key));

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            return hackWorldText(translated);

        return translated;
    }

    @Environment(EnvType.CLIENT)
    private static Text hackWorldText(Text existing) {
        if (ClientTardisUtil.getCurrentTardis() != null &&
                !ClientTardisUtil.getCurrentTardis().flight().isFlying() && ClientTardisUtil.getCurrentTardis().travel().inFlight()) {
            RegistryKey<World> timeVortex = AITDimensions.TIME_VORTEX_WORLD;
            return
                    Text.translatableWithFallback(
                            timeVortex.getValue().toTranslationKey("dimension"),
                            fakeTranslate(timeVortex)).append(" [").append(existing).append( "]");
        }

        return existing;
    }

    public static Text worldText(RegistryKey<World> key, boolean justToSeperate) {
        return Text.translatableWithFallback(key.getValue().toTranslationKey("dimension"), fakeTranslate(key));
    }

    private static String fakeTranslate(RegistryKey<World> id) {
        return fakeTranslate(id.getValue());
    }

    private static String fakeTranslate(Identifier id) {
        return fakeTranslate(id.getPath());
    }

    public static String fakeTranslate(String path) {
        // Split the string into words
        String[] words = path.split("_");

        // Capitalize the first letter of each word
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
        }

        // Join the words back together with spaces
        return String.join(" ", words);
    }

    public static Text rot2Text(int rotation) {
        String key = switch (rotation) {
            case 0 -> "direction.north";
            case 1, 2, 3 -> "direction.north_east";
            case 4 -> "direction.east";
            case 5, 6, 7 -> "direction.south_east";
            case 8 -> "direction.south";
            case 9, 10, 11 -> "direction.south_west";
            case 12 -> "direction.west";
            case 13, 14, 15 -> "direction.north_west";
            default -> null;
        };

        return Text.translatable(key);
    }

    public static void onBreakHalfInCreative(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf doubleBlockHalf = state.get(Properties.DOUBLE_BLOCK_HALF);

        if (doubleBlockHalf != DoubleBlockHalf.UPPER)
            return;

        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);

        if (blockState.isOf(state.getBlock())
                && blockState.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
            BlockState withFluid = blockState.getFluidState().isOf(Fluids.WATER)
                    ? Blocks.WATER.getDefaultState()
                    : Blocks.AIR.getDefaultState();

            world.setBlockState(blockPos, withFluid, 35);
            world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(blockState));
        }
    }

    public static boolean isEndDragonDead() {
        ServerWorld end = ServerLifecycleHooks.get().getWorld(World.END);
        if (end == null) return true;
        return ((EnderDragonFightAccessor) end.getEnderDragonFight()).getDragonKilled();
    }

    public static void teleportToWorld(ServerPlayerEntity player, ServerWorld target, Vec3d pos, float yaw, float pitch) {
        player.teleport(target, pos.x, pos.y, pos.z, yaw, pitch);
        player.addExperience(0);

        player.getStatusEffects().forEach(effect -> player.networkHandler.sendPacket(
                new EntityStatusEffectS2CPacket(player.getId(), effect)));
    }

}

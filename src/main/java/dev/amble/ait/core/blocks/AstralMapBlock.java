package dev.amble.ait.core.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.datafixers.util.Pair;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.Structure;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.blockentities.AstralMapBlockEntity;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.control.impl.TelepathicControl;
import dev.amble.ait.core.tardis.util.AsyncLocatorUtil;
import dev.amble.ait.core.world.TardisServerWorld;

public class AstralMapBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final int MAX_ROTATION_INDEX = RotationPropertyHelper.getMax();
    private static final int MAX_ROTATIONS = MAX_ROTATION_INDEX + 1;
    public static final IntProperty ROTATION = Properties.ROTATION;

    public static final Identifier REQUEST_SEARCH = AITMod.id("c2s/request_search");
    public static final Identifier SYNC_STRUCTURES = AITMod.id("s2c/sync_structures");
    public static final Identifier SYNC_BIOMES = AITMod.id("s2c/sync_biomes");
    public static List<Identifier> structureIds;
    public static List<Identifier> biomeIds;

    static {
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_SEARCH, (server, player, handler, buf, responseSender) -> {
            try {
                Identifier target = buf.readIdentifier();
                if (getBiome(player.getServerWorld(), target).isPresent()) {
                    handleBiomeRequest(player, target);
                    return;
                }
                handleStructureRequest(player, target);
            } catch (Exception e) {
                AITMod.LOGGER.error("Error handling search request", e);
            }
        });
    }

    public AstralMapBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ROTATION, 0));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return AITBlockEntityTypes.ASTRAL_MAP.instantiate(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
                              BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof AstralMapBlockEntity && !world.isClient()) {
            ServerWorld serverWorld = (ServerWorld) world;
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

            sendStructures(serverWorld, serverPlayer);
            sendBiomes(serverWorld, serverPlayer);

            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 1.0F);

            serverWorld.getServer().execute(() -> AITMod.openScreen(serverPlayer, 2));
        }

        return ActionResult.SUCCESS;
    }

    private static Optional<RegistryEntry.Reference<Structure>> getStructure(ServerWorld world, Identifier id) {
        Registry<Structure> registry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);
        RegistryKey<Structure> key = RegistryKey.of(RegistryKeys.STRUCTURE, id);
        return registry.getEntry(key);
    }

    private static Optional<RegistryEntry.Reference<Biome>> getBiome(ServerWorld world, Identifier id) {
        Registry<Biome> registry = world.getRegistryManager().get(RegistryKeys.BIOME);
        RegistryKey<Biome> key = RegistryKey.of(RegistryKeys.BIOME, id);
        return registry.getEntry(key);
    }

    private static void handleStructureRequest(ServerPlayerEntity player, Identifier target) {
        player.sendMessage(Text.translatable("block.ait.astral_map.finder.searching_for_structure"), false);

        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();

        if (TardisServerWorld.isTardisDimension(world)) {
            ServerTardis tardis = ((TardisServerWorld) world).getTardis();
            var tPos = tardis.travel().position();
            world = tPos.getWorld();

            RegistryEntry.Reference<Structure> targetStructure = getStructure(world, target).orElse(null);
            if (targetStructure == null) {
                AITMod.LOGGER.error("Structure not found: {}", target);
                return;
            }

            pos = tPos.getPos();

            AsyncLocatorUtil.locate(world, RegistryEntryList.of(targetStructure), pos, TelepathicControl.RADIUS, false).thenOnServerThread(pPos -> {
                BlockPos newPos = pPos != null ? pPos.getFirst() : null;
                if (newPos != null) {
                    player.sendMessage(Text.translatable(
                            "block.ait.astral_map.finder.found", newPos.getX(), newPos.getY(), newPos.getZ(),
                            Math.round(Math.sqrt(newPos.getSquaredDistance(player.getPos())))), false);
                    tardis.travel().destination(destination -> destination.pos(newPos));
                } else {
                    player.sendMessage(Text.translatable("block.ait.astral_map.finder.not_found"), false);
                }
            });
        }
    }

    private static void handleBiomeRequest(ServerPlayerEntity player, Identifier target) {
        player.sendMessage(Text.translatable("block.ait.astral_map.finder.searching_for_biome"), false);
        player.getServer().execute(() -> {
            ServerWorld world = player.getServerWorld();
            if (!TardisServerWorld.isTardisDimension(world))
                return;

            ServerTardis tardis = ((TardisServerWorld) world).getTardis();
            CachedDirectedGlobalPos curentPos = tardis.travel().position();
            ServerWorld targetWorld = curentPos.getWorld();
            BlockPos start = curentPos.getPos();
            RegistryKey<Biome> biomekey = RegistryKey.of(RegistryKeys.BIOME, target);

            Pair<BlockPos, RegistryEntry<Biome>> r = targetWorld.locateBiome(
                    entry -> entry.getKey().map(key -> key.equals(biomekey)).orElse(false),
                    start, AITMod.CONFIG.astralMapBiomeLocatorRange, 32, 64);

            if (r != null) {
                BlockPos locartedbiome = r.getFirst();
                int distance = (int) Math.round(Math.sqrt(locartedbiome.getSquaredDistance(player.getPos())));
                player.sendMessage(Text.translatable("block.ait.astral_map.finder.found",
                        locartedbiome.getX(), locartedbiome.getY(), locartedbiome.getZ(), distance), false);
                tardis.travel().destination(destination -> destination.pos(locartedbiome));
            } else {
                player.sendMessage(Text.translatable("block.ait.astral_map.finder.not_found"), false);
            }
        });
    }

    private static void sendStructures(ServerWorld world, ServerPlayerEntity target) {
        if (structureIds == null || structureIds.isEmpty()) {
            List<Identifier> ids = new ArrayList<>();
            Registry<Structure> registry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);
            for (Structure entry : registry) {
                ids.add(registry.getId(entry));
            }
            structureIds = ids;
        }

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(structureIds.size());
        for (Identifier id : structureIds) {
            buf.writeIdentifier(id);
        }
        ServerPlayNetworking.send(target, SYNC_STRUCTURES, buf);
    }

    private static void sendBiomes(ServerWorld world, ServerPlayerEntity target) {
        if (biomeIds == null || biomeIds.isEmpty()) {
            List<Identifier> ids = new ArrayList<>();
            Registry<Biome> registry = world.getRegistryManager().get(RegistryKeys.BIOME);
            for (Biome entry : registry) {
                ids.add(registry.getId(entry));
            }
            biomeIds = ids;
        }

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(biomeIds.size());
        for (Identifier id : biomeIds) {
            buf.writeIdentifier(id);
        }
        ServerPlayNetworking.send(target, SYNC_BIOMES, buf);
    }

    @Environment(EnvType.CLIENT)
    public static void registerSyncListener() {
        ClientPlayNetworking.registerGlobalReceiver(SYNC_STRUCTURES, (client, handler, buf, responseSender) -> {
            int size = buf.readInt();
            List<Identifier> ids = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ids.add(buf.readIdentifier());
            }
            client.execute(() -> {
                structureIds = ids;
                if (client.currentScreen instanceof dev.amble.ait.client.screens.AstralMapScreen screen) {
                    screen.reloadData();
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(SYNC_BIOMES, (client, handler, buf, responseSender) -> {
            int size = buf.readInt();
            List<Identifier> ids = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ids.add(buf.readIdentifier());
            }
            client.execute(() -> {
                biomeIds = ids;
                if (client.currentScreen instanceof dev.amble.ait.client.screens.AstralMapScreen screen) {
                    screen.reloadData();
                }
            });
        });
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(ROTATION, RotationPropertyHelper.fromYaw(ctx.getPlayerYaw()));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(ROTATION, rotation.rotate(state.get(ROTATION), MAX_ROTATIONS));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(ROTATION, mirror.mirror(state.get(ROTATION), MAX_ROTATIONS));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }
}
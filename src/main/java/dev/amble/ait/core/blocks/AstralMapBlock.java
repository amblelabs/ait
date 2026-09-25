package dev.amble.ait.core.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.screens.AstralMapScreen;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.blockentities.AstralMapBlockEntity;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.control.impl.SecurityControl;
import dev.amble.ait.core.tardis.control.impl.TelepathicControl;
import dev.amble.ait.core.tardis.util.AsyncLocatorUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import net.minecraft.server.network.ServerPlayNetworkHandler;
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
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

public class AstralMapBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final int MAX_ROTATION_INDEX = RotationPropertyHelper.getMax();
    private static final int MAX_ROTATIONS = MAX_ROTATION_INDEX + 1;
    public static final IntProperty ROTATION = Properties.ROTATION;

    public static final Identifier REQUEST_SEARCH = AITMod.id("c2s/request_search");
    public static final Identifier OPEN_ASTRAL_MAP = AITMod.id("s2c/open_astral_map");
    // Store structure IDs on the client since they aren't synced by default
    public static List<Identifier> structureIds;

    static {
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_SEARCH, (server, player, handler, buf, responseSender) -> {
            Identifier target = buf.readIdentifier();
            AstralMapScreen.Category category = buf.readEnumConstant(AstralMapScreen.Category.class);
            BlockPos pos = buf.readBlockPos();

            server.execute(() -> {
                try {
                    ServerWorld checkWorld = player.getServerWorld();

                    if (checkWorld instanceof TardisServerWorld tardisWorld && SecurityControl.cannotAccess(tardisWorld.getTardis(), player))
                        return;

                    if (player.getEyePos().squaredDistanceTo(pos.toCenterPos()) > ServerPlayNetworkHandler.MAX_BREAK_SQUARED_DISTANCE
                            || !(checkWorld.getBlockState(pos).getBlock() instanceof AstralMapBlock))
                        return;

                    switch(category) {
                        case BIOMES -> handleBiomeRequest(player, target);
                        case STRUCTURES -> handleStructureRequest(player, target);
                    }
                } catch (Exception e) {
                    AITMod.LOGGER.error("Error handling search request", e);
                }
            });
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

            sendStructuresAndOpenScreen(serverWorld, serverPlayer, pos);

            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 1.0F);
        }

        return ActionResult.SUCCESS;
    }

    private static Optional<RegistryEntry.Reference<Structure>> getStructure(ServerWorld world, Identifier id) {
        Registry<Structure> registry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);
        RegistryKey<Structure> key = RegistryKey.of(RegistryKeys.STRUCTURE, id);
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
                            Math.round(Math.sqrt(newPos.getSquaredDistance(tPos.getPos())))), false);
                    tardis.travel().destination(destination -> destination.pos(newPos));
                } else {
                    player.sendMessage(Text.translatable("block.ait.astral_map.finder.structure_not_found"), false);
                }
            });
        }
    }

    private static void handleBiomeRequest(ServerPlayerEntity player, Identifier target) {
        player.sendMessage(Text.translatable("block.ait.astral_map.finder.searching_for_biome"), false);

        if (!(player.getServerWorld() instanceof TardisServerWorld tardisWorld))
            return;

        ServerTardis tardis = tardisWorld.getTardis();
        CachedDirectedGlobalPos currentPos = tardis.travel().position();
        ServerWorld targetWorld = currentPos.getWorld();
        BlockPos start = currentPos.getPos();
        RegistryKey<Biome> biomeKey = RegistryKey.of(RegistryKeys.BIOME, target);

        CompletableFuture.supplyAsync(() -> targetWorld.locateBiome(
                entry -> entry.matchesKey(biomeKey),
                start, AITMod.CONFIG.astralMapBiomeLocatorRange, 32, 64), AsyncLocatorUtil.LOCATING_EXECUTOR_SERVICE).thenAcceptAsync(r -> {
            if (r != null) {
                BlockPos locatedBiome = r.getFirst();
                int distance = (int) Math.round(Math.sqrt(locatedBiome.getSquaredDistance(start)));
                player.sendMessage(Text.translatable("block.ait.astral_map.finder.found",
                        locatedBiome.getX(), locatedBiome.getY(), locatedBiome.getZ(), distance), false);
                tardis.travel().destination(destination -> destination.pos(locatedBiome));
            } else {
                player.sendMessage(Text.translatable("block.ait.astral_map.finder.biome_not_found"), false);
            }
        }, player.getServer()).exceptionally(e -> {
            AITMod.LOGGER.error("Error locating biome {}", target, e);
            return null;
        });
    }

    private static void sendStructuresAndOpenScreen(ServerWorld world, ServerPlayerEntity target, BlockPos pos) {
        if (structureIds == null || structureIds.isEmpty()) {
            Registry<Structure> registry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);
            List<Identifier> ids = new ArrayList<>(registry.size());
            for (Structure entry : registry) {
                ids.add(registry.getId(entry));
            }
            structureIds = ids;
        }

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeCollection(structureIds, PacketByteBuf::writeIdentifier);
        ServerPlayNetworking.send(target, OPEN_ASTRAL_MAP, buf);
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

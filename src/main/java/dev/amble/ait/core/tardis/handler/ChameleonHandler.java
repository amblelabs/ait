package dev.amble.ait.core.tardis.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.core.tardis.util.NetworkUtil;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.drtheo.gaslighter.Gaslighter3000;
import dev.drtheo.gaslighter.api.FakeBlockEvents;

import dev.drtheo.gaslighter.impl.FakeStructureWorldAccess;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.schema.exterior.variant.adaptive.AdaptiveVariant;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.*;
import org.jetbrains.annotations.NotNull;

public class ChameleonHandler extends KeyedTardisComponent {

    @Exclude
    private Gaslighter3000 gaslighter;

    static {
        TardisEvents.ENTER_FLIGHT.register(tardis -> {
            tardis.chameleon().clearDisguise();
        });

        TardisEvents.START_FALLING.register(tardis -> {
            tardis.chameleon().clearDisguise();
        });

        TardisEvents.TOGGLE_SIEGE.register((tardis, active) -> {
            if (shouldNotBeDisguised(tardis)) {
                tardis.chameleon().clearDisguise();
            } else {
                tardis.chameleon().applyDisguise();
            }
        });

        TardisEvents.LANDED.register(tardis -> {
            if (!shouldNotBeDisguised(tardis))
                tardis.chameleon().applyDisguise();
        });

        TardisEvents.SEND_TARDIS.register((tardis, player) -> {
            if (player.isInTeleportationState())
                return;

            if (shouldNotBeDisguised(tardis))
                return;

            CachedDirectedGlobalPos pos = tardis.travel().position();

            if (pos == null || pos.getWorld() != player.getServerWorld())
                return;

            tardis.chameleon().applyDisguise(player);
        });

        TardisEvents.EXTERIOR_CHANGE.register(tardis -> {
            if (shouldNotBeDisguised(tardis)) {
                tardis.chameleon().clearDisguise();
            } else {
                tardis.chameleon().applyDisguise();
            }
        });

        TardisEvents.DOOR_USED.register((tardis,  player) -> {
            if (player == null || !isDisguised(tardis))
                return DoorHandler.InteractionResult.CONTINUE;

            if (!shouldNotBeDisguised(tardis)) {
                tardis.chameleon().applyDisguise(player);
                return DoorHandler.InteractionResult.CONTINUE;
            }

            CachedDirectedGlobalPos cached = tardis.travel().position();
            Optional<ExteriorBlockEntity> blockEntity = tardis.getExterior().findExteriorBlock();

            if (blockEntity.isEmpty())
                return DoorHandler.InteractionResult.CONTINUE;

            player.networkHandler.sendPacket(new BlockUpdateS2CPacket(cached.getWorld(), cached.getPos()));
            player.networkHandler.sendPacket(new BlockUpdateS2CPacket(cached.getWorld(), cached.getPos().up()));
            player.networkHandler.sendPacket(BlockEntityUpdateS2CPacket.create(blockEntity.get()));

            return DoorHandler.InteractionResult.CONTINUE;
        });

        FakeBlockEvents.INTERACT.register((player, hand, pos) -> {
            // allow only main hand clicks!
            if (hand != Hand.MAIN_HAND)
                return FakeBlockEvents.Action.REMOVE;

            return FakeBlockEvents.Action.CONTINUE;
        });

        FakeBlockEvents.CHECK.register((player, hand, state, pos) -> {
            if (state.isOf(AITBlocks.EXTERIOR_BLOCK))
                return FakeBlockEvents.Action.CONTINUE;

            ServerWorld world = player.getServerWorld();

            // should be cheap enough
            if (hand == Hand.MAIN_HAND && world.getBlockEntity(pos.down()) instanceof ExteriorBlockEntity ebe) {
                ebe.useOn(world, player.isSneaking(), player);
                return FakeBlockEvents.Action.CONTINUE;
            }

            shitParticles(world, pos);
            return FakeBlockEvents.Action.REMOVE;
        });

        FakeBlockEvents.PLACED.register((world, state, pos) -> shitParticles(world, pos));
        FakeBlockEvents.REMOVED.register(ChameleonHandler::shitParticles);
    }

    private Identifier lastFeature = null;

    public ChameleonHandler() {
        super(Id.CHAMELEON);
    }

    @Override
    public void postInit(InitContext ctx) {
        if (ctx.created() || !this.isServer()) return;

        if (lastFeature == null)
            return;

        CachedDirectedGlobalPos cached = tardis.travel().position();

        BlockPos pos = cached.getPos();
        ServerWorld world = cached.getWorld();

        Optional<RegistryEntry.Reference<ConfiguredFeature<?, ?>>> feature =
                getRegistry(world).getEntry(asFeature(lastFeature));

        if (feature.isEmpty())
            return;

        this.gaslighter = new Gaslighter3000(world);
        if (!this.generate(world, pos, feature.get()) && !this.applyFallback(world, pos))
            return;

        if (!this.tryFixDisguise(world, pos))
            return;

        this.applyDisguise();
    }

    private static boolean shouldNotBeDisguised(Tardis tardis) {
        return !isDisguised(tardis) || !tardis.travel().isLanded()
                || tardis.siege().isActive() || tardis.door().isOpen()
                || tardis.flight().falling().get()
                || (tardis.travel().antigravs().get() && tardis.flight().shouldFall().get());
    }

    public static boolean isDisguised(Tardis tardis) {
        return tardis.getExterior().getVariant() instanceof AdaptiveVariant;
    }

    public void clearDisguise() {
        if (this.gaslighter == null)
            return;

        gaslighter.touchGrass();
        gaslighter.tweet();

        this.gaslighter = null;
    }

    /**
     * @return Whether the recalculation was successful
     */
    public boolean recalcDisguise() {
        if (this.gaslighter != null)
            return true;

        long start = System.currentTimeMillis();
        CachedDirectedGlobalPos cached = tardis.travel().position();
        ServerWorld world = cached.getWorld();
        BlockPos pos = cached.getPos();

        this.gaslighter = new Gaslighter3000(world);
        boolean success = this.testBiome(world, pos);

        if (!success && !applyFallback(world, pos))
            return false;

        boolean result = this.tryFixDisguise(world, pos);
        AITMod.LOGGER.debug("Recalculated exterior in {}ms", System.currentTimeMillis() - start);

        return result;
    }

    private boolean tryFixDisguise(ServerWorld world, BlockPos pos) {
        // check if the exterior's position is still an exterior
        if (!this.gaslighter.getAgenda(pos).isOf(AITBlocks.EXTERIOR_BLOCK))
            return true;

        // if it is, then try applying fallback
        if (!this.applyFallback(world, pos)) {
            this.gaslighter = null;
            return false;
        }

        return true;
    }

    private boolean applyFallback(ServerWorld world, BlockPos pos) {
        BlockState below = world.getBlockState(pos.down());

        if (!isSafe(below)) {
            below = world.getBlockState(pos.down(2));

            if (!isSafe(below)) {
                this.notifyFailure();
                return false;
            }
        }

        this.gaslighter.spreadLies(pos, below);
        return true;
    }

    private void notifyFailure() {
        Text text = Text.translatable("tardis.message.chameleon.failed")
                .formatted(Formatting.RED);

        NetworkUtil.getSubscribedPlayers(tardis.asServer()).forEach(player ->
                player.sendMessage(text, true));
    }

    private void applyDisguise(ServerPlayerEntity player) {
        if (!this.recalcDisguise())
            return;

        this.gaslighter.tweet(player);
    }

    public void applyDisguise() {
        if (!this.recalcDisguise())
            return;

        this.gaslighter.tweet();
    }

    private boolean testBiome(ServerWorld world, BlockPos pos) {
        RegistryEntry<Biome> biome = world.getBiome(pos);
        List<RegistryEntry<ConfiguredFeature<?, ?>>> trees = this.findTrees(world, biome);

        if (trees.isEmpty())
            return false;

        RegistryEntry<ConfiguredFeature<?, ?>> tree = trees.get(world.random.nextInt(trees.size()));

        if (tree == null)
            return false;

        return this.generate(world, pos, tree);
    }

    private boolean generate(ServerWorld world, BlockPos pos, RegistryEntry<ConfiguredFeature<?, ?>> feature) {
        feature.getKey().ifPresent(k -> this.lastFeature = k.getValue());

        FakeStructureWorldAccess access = new FakeStructureWorldAccess(world, gaslighter);
        return feature.value().generate(access, world.getChunkManager().getChunkGenerator(), world.random, pos);
    }

    private static boolean isSafe(BlockState state) {
        return state.isSolid() && !state.isReplaceable();
    }

    private static final Set<Class<? extends Feature<?>>> TREES = Set.of(
            TreeFeature.class, HugeMushroomFeature.class, HugeFungusFeature.class,
            DesertWellFeature.class, ChorusPlantFeature.class
    );

    private static final RegistryKey<ConfiguredFeature<?, ?>> CACTUS = asFeature(AITMod.id("cactus"));

    private List<RegistryEntry<ConfiguredFeature<?, ?>>> findTrees(ServerWorld world, RegistryEntry<Biome> biome) {
        BiomeHandler biomeHandler = this.tardis.handler(Id.BIOME);
        List<RegistryEntry<ConfiguredFeature<?, ?>>> trees = new ArrayList<>();

        if (biomeHandler.getBiomeKey() == BiomeHandler.BiomeType.SANDY && world.random.nextInt(5) != 0) {
            trees.add(getRegistry(world).getEntry(CACTUS).orElse(null));
            return trees;
        }

        for (RegistryEntryList<PlacedFeature> feature : biome.value().getGenerationSettings().getFeatures()) {
            for (RegistryEntry<PlacedFeature> entry : feature) {
                RegistryEntry<ConfiguredFeature<?, ?>> configured = entry.value().feature();

                if (isTree(configured.value(), biome)) {
                    trees.add(configured);
                    break;
                } else {
                    boolean shouldBreak = false;

                    for (ConfiguredFeature<?, ?> configuredFeature : configured.value()
                            .config().getDecoratedFeatures().toList()) {
                        if (!isTree(configuredFeature, biome))
                            continue;

                        trees.add(configured);
                        shouldBreak = true;
                        break;
                    }

                    if (shouldBreak)
                        break;
                }
            }
        }

        return trees;
    }

    public boolean isApplied() {
        return isDisguised(tardis) && gaslighter != null;
    }

    private static void shitParticles(ServerWorld world, BlockPos pos) {
        Vec3d center = pos.toCenterPos();
        world.spawnParticles(ParticleTypes.END_ROD, center.getX(), center.getY(), center.getZ(),
                12, 0.3, 0.3, 0.3, 0);
    }

    private static boolean isTree(ConfiguredFeature<?, ?> configured, RegistryEntry<Biome> biome) {
        Feature<?> feature = configured.feature();

        for (Class<?> clazz : TREES) {
            if (clazz.isInstance(feature))
                return true;
        }

        return false;
    }

    @NotNull
    private static Registry<ConfiguredFeature<?, ?>> getRegistry(World world) {
        return world.getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE);
    }

    @NotNull
    private static RegistryKey<ConfiguredFeature<?, ?>> asFeature(Identifier id) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, id);
    }
}

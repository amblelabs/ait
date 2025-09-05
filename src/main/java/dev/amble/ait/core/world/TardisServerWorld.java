package dev.amble.ait.core.world;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

import dev.amble.lib.util.ServerLifecycleHooks;
import dev.drtheo.multidim.MultiDim;
import dev.drtheo.multidim.MultiDimMod;
import dev.drtheo.multidim.api.MultiDimServerWorld;
import dev.drtheo.multidim.api.WorldBlueprint;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.Spawner;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITDimensions;
import dev.amble.ait.core.tardis.ServerTardis;

public class TardisServerWorld extends MultiDimServerWorld {

    public static final String NAMESPACE = AITMod.MOD_ID + "-tardis";

    private ServerTardis tardis;
    private RegistryEntry<Biome> cachedBiome;

    public TardisServerWorld(WorldBlueprint blueprint, MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, List<Spawner> spawners, @Nullable RandomSequencesState randomSequencesState, boolean created) {
        super(blueprint, server, workerExecutor, session, properties, worldKey, dimensionOptions, worldGenerationProgressListener, spawners, randomSequencesState, created);
        this.setMobSpawnOptions(false, false);
    }

    @Override
    public void tick(BooleanSupplier shouldKeepTicking) {
        if (this.shouldTick()) {
            super.tick(shouldKeepTicking);
        }
    }

    public boolean shouldTick() {
        return this.tardis != null && (
                !MultiDim.get(this.getServer()).isWorldUnloaded(this)
                || this.tardis.interiorChanging().queued().get()
                || this.tardis.getDesktop().isChanging()
        );
    }

    @Override
    public String toString() {
        return "Tardis" + super.toString();
    }

    @Override
    public RegistryEntry<Biome> getBiome(BlockPos pos) {
        if (this.cachedBiome != null)
            return cachedBiome;

        this.cachedBiome = super.getBiome(pos);
        return cachedBiome;
    }

    public void setTardis(ServerTardis tardis) {
        this.tardis = tardis;
    }

    public ServerTardis getTardis() {
        return tardis;
    }

    public static TardisServerWorld create(ServerTardis tardis) {
        AITMod.LOGGER.info("Creating a dimension for TARDIS {}", tardis.getUuid());
        TardisServerWorld created = (TardisServerWorld) MultiDim.get(ServerLifecycleHooks.get())
                .add(AITDimensions.TARDIS_WORLD_BLUEPRINT, idForTardis(tardis));

        created.setTardis(tardis);
        return created;
    }

    public static TardisServerWorld load(ServerTardis tardis) {
        long start = System.currentTimeMillis();
        MinecraftServer server = ServerLifecycleHooks.get();
        MultiDim multidim = MultiDim.get(server);

        RegistryKey<World> key = keyForTardis(tardis);
        TardisServerWorld result = (TardisServerWorld) server.getWorld(key);

        if (result != null) {
            result.setTardis(tardis);
            return result;
        }

        result = (TardisServerWorld) multidim.load(AITDimensions.TARDIS_WORLD_BLUEPRINT, key);

        if (result == null) {
            MultiDimMod.LOGGER.info("Failed to load the sub-world, creating a new one instead");
            result = create(tardis);
        } else {
            result.setTardis(tardis);
        }

        MultiDimMod.LOGGER.info("Time taken to load sub-world: {}", System.currentTimeMillis() - start);
        return result;
    }

    public static RegistryKey<World> keyForTardis(ServerTardis tardis) {
        return RegistryKey.of(RegistryKeys.WORLD, idForTardis(tardis));
    }

    private static Identifier idForTardis(ServerTardis tardis) {
        return new Identifier(NAMESPACE, tardis.getUuid().toString());
    }

    public static boolean isTardisDimension(RegistryKey<World> key) {
        return NAMESPACE.equals(key.getValue().getNamespace());
    }

    public static boolean isTardisDimension(World world) {
        return world.isClient() ? isTardisDimension((ClientWorld) world) : isTardisDimension((ServerWorld) world);
    }

    public static boolean isTardisDimension(ServerWorld world) {
        return world instanceof TardisServerWorld;
    }

    @Nullable public static UUID getTardisId(@Nullable World world) {
        if (world == null || !isTardisDimension(world))
            return null;

        return getTardisId(world.getRegistryKey());
    }

    public static UUID getTardisId(RegistryKey<World> key) {
        return UUID.fromString(key.getValue().getPath());
    }

    @Environment(EnvType.CLIENT)
    public static boolean isTardisDimension(ClientWorld world) {
        return isTardisDimension(world.getRegistryKey());
    }
}

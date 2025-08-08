package dev.amble.ait;

import static dev.amble.ait.module.planet.core.space.planet.Crater.CRATER_ID;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import dev.amble.lib.container.RegistryContainer;
import dev.amble.lib.register.AmbleRegistries;
import dev.amble.lib.util.ServerLifecycleHooks;
import dev.drtheo.multidim.MultiDim;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

import dev.amble.ait.api.AITModInitializer;
import dev.amble.ait.config.AITServerConfig;
import dev.amble.ait.core.*;
import dev.amble.ait.core.advancement.TardisCriterions;
import dev.amble.ait.core.commands.*;
import dev.amble.ait.core.drinks.DrinkRegistry;
import dev.amble.ait.core.engine.registry.SubSystemRegistry;
import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.entities.RiftEntity;
import dev.amble.ait.core.item.blueprint.BlueprintRegistry;
import dev.amble.ait.core.item.component.AbstractTardisPart;
import dev.amble.ait.core.item.part.MachineItem;
import dev.amble.ait.core.likes.ItemOpinionRegistry;
import dev.amble.ait.core.lock.LockedDimensionRegistry;
import dev.amble.ait.core.loot.SetBlueprintLootFunction;
import dev.amble.ait.core.sounds.flight.FlightSoundRegistry;
import dev.amble.ait.core.sounds.travel.TravelSoundRegistry;
import dev.amble.ait.core.tardis.animation.v2.blockbench.BlockbenchParser;
import dev.amble.ait.core.tardis.animation.v2.datapack.TardisAnimationRegistry;
import dev.amble.ait.core.tardis.control.sound.ControlSoundRegistry;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.tardis.util.AsyncLocatorUtil;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.tardis.vortex.reference.VortexReferenceRegistry;
import dev.amble.ait.core.util.CustomTrades;
import dev.amble.ait.core.util.SpaceUtils;
import dev.amble.ait.core.util.StackUtil;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.core.world.LandingPadManager;
import dev.amble.ait.core.world.RiftChunkManager;
import dev.amble.ait.data.landing.LandingPadRegion;
import dev.amble.ait.data.schema.MachineRecipeSchema;
import dev.amble.ait.module.ModuleRegistry;
import dev.amble.ait.module.planet.core.space.planet.Crater;
import dev.amble.ait.registry.impl.*;
import dev.amble.ait.registry.impl.console.ConsoleRegistry;
import dev.amble.ait.registry.impl.console.variant.ConsoleVariantRegistry;
import dev.amble.ait.registry.impl.door.DoorRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;

public class AITMod implements ModInitializer {

    public static final String MOD_ID = "ait";
    public static final Logger LOGGER = LoggerFactory.getLogger("ait");
    public static final Random RANDOM = new Random();

    public static AITServerConfig CONFIG;
    public static final GameRules.Key<GameRules.BooleanRule> TARDIS_GRIEFING = GameRuleRegistry.register("tardisGriefing",
            GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static final GameRules.Key<GameRules.BooleanRule> TARDIS_FIRE_GRIEFING = GameRuleRegistry.register("tardisFireGriefing",
            GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));


    public static final RegistryKey<PlacedFeature> CUSTOM_GEODE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
            new Identifier(MOD_ID, "zeiton_geode"));

    // This DefaultParticleType gets called when you want to use your particle in code.
    public static final DefaultParticleType CORAL_PARTICLE = FabricParticleTypes.simple();

    // Register our custom particle type in the mod initializer.

    public static final Crater CRATER = new Crater(ProbabilityConfig.CODEC);

    public static final String BRANCH;

    static {
        // ait-1.x.x.xxx-1.20.1-xxxx-xxxx
        String version = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getVersion().getFriendlyString();
        // get the last part of the version string after the -
        BRANCH = version.substring(version.lastIndexOf("-") + 1);
    }

    public static boolean isUnsafeBranch() {
        return !BRANCH.equals("release");
    }

    public void registerParticles() {
        Registry.register(Registries.PARTICLE_TYPE, id("coral_particle"), CORAL_PARTICLE);
    }

    @Override
    public void onInitialize() {
        AITServerConfig.INSTANCE.load();
        CONFIG = AITServerConfig.INSTANCE.instance();

        ServerLifecycleHooks.init();
        AsyncLocatorUtil.init();
        MultiDim.init();

        CreakRegistry.init();
        SequenceRegistry.init();
        MoodEventPoolRegistry.init();
        LandingPadManager.init();
        ControlRegistry.init();
        RiftChunkManager.init();

        AmbleRegistries.getInstance().registerAll(
                ConsoleRegistry.getInstance(),
                SonicRegistry.getInstance(),
                DesktopRegistry.getInstance(),
                ConsoleVariantRegistry.getInstance(),
                MachineRecipeRegistry.getInstance(),
                TravelSoundRegistry.getInstance(),
                FlightSoundRegistry.getInstance(),
                VortexReferenceRegistry.getInstance(),
                BlueprintRegistry.getInstance(),
                ExteriorVariantRegistry.getInstance(),
                CategoryRegistry.getInstance(),
                TardisComponentRegistry.getInstance(),
                LockedDimensionRegistry.getInstance(),
                HumRegistry.getInstance(),
                SubSystemRegistry.getInstance(),
                ItemOpinionRegistry.getInstance(),
                DrinkRegistry.getInstance(),
                TardisAnimationRegistry.getInstance(),
                DoorRegistry.getInstance()
        );
        ControlSoundRegistry.init();
        BlockbenchParser.init();

        registerParticles();

        // For all the addon devs
        FabricLoader.getInstance().invokeEntrypoints("ait-main", AITModInitializer.class,
                AITModInitializer::onInitializeAIT);

        HandlesResponseRegistry.init();

        AITStatusEffects.init();
        AITVillagers.init();
        AITArgumentTypes.register();
        AITSounds.init();
        AITDimensions.init();

        CustomTrades.register();

        RegistryContainer.register(AITItemGroups.class, MOD_ID);
        RegistryContainer.register(AITItems.class, MOD_ID);
        RegistryContainer.register(AITBlocks.class, MOD_ID);
        RegistryContainer.register(AITBlockEntityTypes.class, MOD_ID);
        RegistryContainer.register(AITEntityTypes.class, MOD_ID);
        RegistryContainer.register(AITPaintings.class, MOD_ID);
        ModuleRegistry.instance().onCommonInit();

        BlueprintRegistry.BLUEPRINT_TYPE = Registry.register(Registries.LOOT_FUNCTION_TYPE,
                AITMod.id("set_blueprint"),
                new LootFunctionType(new SetBlueprintLootFunction.Serializer()));

        WorldUtil.init();
        TardisUtil.init();

        ServerTardisManager.init();
        TardisCriterions.init();

        entityAttributeRegister();

        SpaceUtils.init();

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES,
                CUSTOM_GEODE_PLACED_KEY);

        BiomeModifications.addSpawn(
                BiomeSelectors.all(),
                SpawnGroup.AMBIENT,
                AITEntityTypes.RIFT_ENTITY,
                4,
                1,
                1
        );

        Registry.register(net.minecraft.registry.Registries.FEATURE, CRATER_ID, CRATER);

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            TeleportInteriorCommand.register(dispatcher);
            SummonTardisCommand.register(dispatcher);
            SetLockedCommand.register(dispatcher);
            ThisTardisCommand.register(dispatcher);
            FuelCommand.register(dispatcher);
            SetRepairTicksCommand.register(dispatcher);
            RiftChunkCommand.register(dispatcher);
            ScaleCommand.register(dispatcher);
            TriggerMoodRollCommand.register(dispatcher);
            SetNameCommand.register(dispatcher);
            GetNameCommand.register(dispatcher);
            GetCreatorCommand.register(dispatcher);
            SetMaxSpeedCommand.register(dispatcher);
            SetSiegeCommand.register(dispatcher);
            LinkCommand.register(dispatcher);
            RemoveCommand.register(dispatcher);
            PermissionCommand.register(dispatcher);
            LoyaltyCommand.register(dispatcher);
            UnlockCommand.register(dispatcher);
            DataCommand.register(dispatcher);
            TravelDebugCommand.register(dispatcher);
            VersionCommand.register(dispatcher);
            SafePosCommand.register(dispatcher);
            ListCommand.register(dispatcher);
            LoadCommand.register(dispatcher);
            DebugCommand.register(dispatcher);
            EraseChunksCommand.register(dispatcher);
            FlightCommand.register(dispatcher);
            SetDoorParticleCommand.register(dispatcher, registryAccess);
        }));

        ServerPlayNetworking.registerGlobalReceiver(TardisUtil.REGION_LANDING_CODE,
                (server, player, handler, buf, responseSender) -> {
                    BlockPos pos = buf.readBlockPos();
                    String landingCode = buf.readString();

                    server.execute(() -> {
                        LandingPadRegion region = LandingPadManager.getInstance((ServerWorld) player.getWorld()).getRegionAt(pos);

                        if (region == null)
                            return;

                        region.setLandingCode(landingCode);
                        LandingPadManager.Network.syncTracked(LandingPadManager.Network.Action.ADD, player.getServerWorld(),
                                new ChunkPos(player.getBlockPos()));
                    });
                });

        ServerPlayNetworking.registerGlobalReceiver(MachineItem.MACHINE_DISASSEMBLE,
                (server, player, handler, buf, responseSender) -> {
                    ItemStack machine = buf.readItemStack();

                    Optional<MachineRecipeSchema> schema = MachineRecipeRegistry.getInstance().findMatching(machine);

                    if (schema.isEmpty())
                        return;

                    // this should ALWAYS be executed on the main thread
                    server.execute(() -> {
                        MachineItem.disassemble(player, machine, schema.get());

                        StackUtil.playBreak(player);
                    });
                });

        ServerPlayNetworking.registerGlobalReceiver(AbstractTardisPart.DISASSEMBLE,
                (server, player, handler, buf, responseSender) -> {
                    ItemStack machine = buf.readItemStack();

                    Optional<MachineRecipeSchema> schema = MachineRecipeRegistry.getInstance().findMatching(machine);

                    if (schema.isEmpty())
                        return;

                    // this should ALWAYS be executed on the main thread
                    server.execute(() -> {
                        AbstractTardisPart.disassemble(player, machine, schema.get());

                        StackUtil.playBreak(player);
                    });
                });

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (source.isBuiltin()
                    && (id.equals(LootTables.NETHER_BRIDGE_CHEST) || id.equals(LootTables.DESERT_PYRAMID_CHEST)
                    || id.equals(LootTables.VILLAGE_ARMORER_CHEST) || id.equals(LootTables.RUINED_PORTAL_CHEST))
                    || id.equals(LootTables.END_CITY_TREASURE_CHEST) || id.equals(LootTables.SHIPWRECK_MAP_CHEST)
                    || id.equals(LootTables.ABANDONED_MINESHAFT_CHEST) || id.equals(LootTables.VILLAGE_CARTOGRAPHER_CHEST)
                    || id.equals(LootTables.VILLAGE_TOOLSMITH_CHEST) || id.equals(LootTables.SHIPWRECK_TREASURE_CHEST)
                    || id.equals(LootTables.ANCIENT_CITY_CHEST) || id.equals(LootTables.ANCIENT_CITY_ICE_BOX_CHEST)
                    || id.equals(LootTables.BURIED_TREASURE_CHEST) || id.equals(LootTables.DESERT_PYRAMID_ARCHAEOLOGY)
                    || id.equals(LootTables.DESERT_WELL_ARCHAEOLOGY) || id.equals(LootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY)
                    || id.equals(LootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY) || id.equals(LootTables.TRAIL_RUINS_RARE_ARCHAEOLOGY)
                    || id.equals(LootTables.FISHING_TREASURE_GAMEPLAY) || id.equals(LootTables.DESERT_PYRAMID_CHEST)
                    || id.equals(LootTables.SIMPLE_DUNGEON_CHEST) || id.equals(LootTables.STRONGHOLD_LIBRARY_CHEST)) {


                NbtCompound nbt = new NbtCompound();
                LootPool.Builder poolBuilder = LootPool.builder().with(ItemEntry.builder(AITItems.BLUEPRINT).apply(SetNbtLootFunction.builder(nbt)).weight(10));

                tableBuilder.pool(poolBuilder);
            }
        });
    }

    public void entityAttributeRegister() {
        FabricDefaultAttributeRegistry.register(AITEntityTypes.RIFT_ENTITY,
                RiftEntity.createMobAttributes());

        FabricDefaultAttributeRegistry.register(AITEntityTypes.FLIGHT_TARDIS_TYPE,
                FlightTardisEntity.createDummyAttributes());
    }

    public static final Identifier OPEN_SCREEN = AITMod.id("open_screen");
    public static final Identifier OPEN_SCREEN_TARDIS = AITMod.id("open_screen_tardis");
    public static final Identifier OPEN_SCREEN_CONSOLE = AITMod.id("open_screen_console");

    public static void openScreen(ServerPlayerEntity player, int id) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(id);
        ServerPlayNetworking.send(player, OPEN_SCREEN, buf);
    }

    public static void openScreen(ServerPlayerEntity player, int id, UUID tardis) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(id);
        buf.writeUuid(tardis);
        ServerPlayNetworking.send(player, OPEN_SCREEN_TARDIS, buf);
    }

    public static void openScreen(ServerPlayerEntity player, int id, UUID tardis, BlockPos console) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(id);
        buf.writeUuid(tardis);
        buf.writeBlockPos(console);

        ServerPlayNetworking.send(player, OPEN_SCREEN_CONSOLE, buf);
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}

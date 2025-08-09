package dev.amble.ait.client;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.boti.*;
import dev.amble.ait.client.commands.ConfigCommand;
import dev.amble.ait.client.config.AITClientConfig;
import dev.amble.ait.client.data.ClientLandingManager;
import dev.amble.ait.client.models.AnimatedModel;
import dev.amble.ait.client.models.boti.BotiPortalModel;
import dev.amble.ait.client.models.decoration.GallifreyFallsModel;
import dev.amble.ait.client.models.decoration.PaintingFrameModel;
import dev.amble.ait.client.models.decoration.RiftModel;
import dev.amble.ait.client.models.decoration.TrenzalorePaintingModel;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.overlays.ExteriorAxeOverlay;
import dev.amble.ait.client.overlays.FabricatorOverlay;
import dev.amble.ait.client.overlays.RWFOverlay;
import dev.amble.ait.client.overlays.SonicOverlay;
import dev.amble.ait.client.renderers.SonicRendering;
import dev.amble.ait.client.renderers.TardisStar;
import dev.amble.ait.client.renderers.consoles.ConsoleGeneratorRenderer;
import dev.amble.ait.client.renderers.consoles.ConsoleRenderer;
import dev.amble.ait.client.renderers.coral.CoralRenderer;
import dev.amble.ait.client.renderers.decoration.FlagBlockEntityRenderer;
import dev.amble.ait.client.renderers.decoration.PlaqueRenderer;
import dev.amble.ait.client.renderers.decoration.SnowGlobeRenderer;
import dev.amble.ait.client.renderers.doors.DoorRenderer;
import dev.amble.ait.client.renderers.entities.*;
import dev.amble.ait.client.renderers.exteriors.ExteriorRenderer;
import dev.amble.ait.client.renderers.machines.*;
import dev.amble.ait.client.renderers.monitors.MonitorRenderer;
import dev.amble.ait.client.renderers.monitors.WallMonitorRenderer;
import dev.amble.ait.client.renderers.sky.MarsSkyProperties;
import dev.amble.ait.client.screens.AstralMapScreen;
import dev.amble.ait.client.screens.BlueprintFabricatorScreen;
import dev.amble.ait.client.screens.MonitorScreen;
import dev.amble.ait.client.sonic.SonicModelLoader;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.tardis.manager.ClientTardisManager;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.*;
import dev.amble.ait.core.blockentities.ConsoleGeneratorBlockEntity;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.blocks.AstralMapBlock;
import dev.amble.ait.core.blocks.ExteriorBlock;
import dev.amble.ait.core.drinks.DrinkRegistry;
import dev.amble.ait.core.drinks.DrinkUtil;
import dev.amble.ait.core.entities.BOTIPaintingEntity;
import dev.amble.ait.core.entities.RiftEntity;
import dev.amble.ait.core.item.*;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.schema.console.ConsoleTypeSchema;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.amble.ait.module.ModuleRegistry;
import dev.amble.ait.module.gun.core.item.BaseGunItem;
import dev.amble.ait.registry.impl.SonicRegistry;
import dev.amble.ait.registry.impl.console.ConsoleRegistry;
import dev.amble.ait.registry.impl.console.variant.ClientConsoleVariantRegistry;
import dev.amble.ait.registry.impl.door.ClientDoorRegistry;
import dev.amble.ait.registry.impl.exterior.ClientExteriorVariantRegistry;
import dev.amble.lib.register.AmbleRegistries;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.minecraft.block.DoorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import static dev.amble.ait.AITMod.*;
import static dev.amble.ait.core.AITItems.isUnlockedOnThisDay;
import static dev.amble.ait.core.item.PersonalityMatrixItem.colorToInt;

@Environment(value = EnvType.CLIENT)
public class AITModClient implements ClientModInitializer {

    public static AITClientConfig CONFIG;

    @Override
    public void onInitializeClient() {
        AITClientConfig.INSTANCE.load();
        CONFIG = AITClientConfig.INSTANCE.instance();

        // TODO move to Registries
        AmbleRegistries.getInstance().registerAll(
                SonicRegistry.getInstance(),
                DrinkRegistry.getInstance(),
                ClientExteriorVariantRegistry.getInstance(),
                ClientConsoleVariantRegistry.getInstance(),
                ClientDoorRegistry.getInstance()
        );

        ClientTardisManager.init();

        ModuleRegistry.instance().onClientInit();

        setupBlockRendering();
        blockEntityRendererRegister();
        entityRenderRegister();
        chargedZeitonCrystalPredicate();
        waypointPredicate();
        hammerPredicate();
        siegeItemPredicate();
        adventItemPredicates();
        registerItemColors();
        registerParticles();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            ConfigCommand.register(dispatcher);
        });

        AITKeyBinds.init();

        ClientLandingManager.init();

        HudRenderCallback.EVENT.register(new SonicOverlay());
        HudRenderCallback.EVENT.register(new RWFOverlay());
        HudRenderCallback.EVENT.register(new FabricatorOverlay());
        HudRenderCallback.EVENT.register(new ExteriorAxeOverlay());

        ClientPreAttackCallback.EVENT.register((client, player, clickCount) -> (player.getMainHandStack().getItem() instanceof BaseGunItem));
        if (DependencyChecker.hasIris()) {
            WorldRenderEvents.END.register(this::exteriorBOTI);
            WorldRenderEvents.END.register(this::doorBOTI);
            WorldRenderEvents.END.register(this::gallifreyanBOTI);
            WorldRenderEvents.END.register(this::trenzaloreBOTI);
            WorldRenderEvents.END.register(this::riftBOTI);
        } else {
            WorldRenderEvents.AFTER_ENTITIES.register(this::exteriorBOTI);
            WorldRenderEvents.AFTER_ENTITIES.register(this::doorBOTI);
            WorldRenderEvents.AFTER_ENTITIES.register(this::gallifreyanBOTI);
            WorldRenderEvents.AFTER_ENTITIES.register(this::trenzaloreBOTI);
            WorldRenderEvents.AFTER_ENTITIES.register(this::riftBOTI);
        }

        // @TODO idk why but this gets rid of other important stuff, not sure
        DimensionRenderingRegistry.registerDimensionEffects(AITDimensions.MARS.getValue(), new MarsSkyProperties());

        WorldRenderEvents.BEFORE_ENTITIES.register(context -> {
            Tardis tardis = ClientTardisUtil.getCurrentTardis();

            if (tardis == null)
                return;

            TardisStar.render(context, tardis);
        });

        ClientPlayNetworking.registerGlobalReceiver(OPEN_SCREEN, (client, handler, buf, responseSender) -> {
            int id = buf.readInt();
            Screen screen = screenFromId(id);

            if (screen == null)
                return;

            client.execute(() -> client.setScreenAndRender(screen));
        });

        ClientPlayNetworking.registerGlobalReceiver(OPEN_SCREEN_TARDIS, (client, handler, buf, responseSender) -> {
            int id = buf.readInt();
            UUID uuid = buf.readUuid();

            ClientTardisManager.getInstance().getTardis(uuid, tardis -> {
                Screen screen = screenFromId(id, tardis);

                if (screen == null)
                    return;

                client.execute(() -> client.setScreenAndRender(screen));
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(OPEN_SCREEN_CONSOLE, (client, handler, buf, responseSender) -> {
            int id = buf.readInt();
            UUID uuid = buf.readUuid();
            BlockPos console = buf.readBlockPos();

            ClientTardisManager.getInstance().getTardis(uuid, tardis -> {
                Screen screen = screenFromId(id, tardis, console);

                if (screen == null)
                    return;

                client.execute(() -> client.setScreenAndRender(screen));
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ConsoleGeneratorBlockEntity.SYNC_TYPE,
                (client, handler, buf, responseSender) -> {
                    if (client.world == null)
                        return;

                    String id = buf.readString();
                    ConsoleTypeSchema type = ConsoleRegistry.getInstance().get(Identifier.tryParse(id));
                    BlockPos consolePos = buf.readBlockPos();

                    if (client.world.getBlockEntity(consolePos) instanceof ConsoleGeneratorBlockEntity console)
                        console.setConsoleSchema(type.id());
                });

        ClientPlayNetworking.registerGlobalReceiver(ConsoleGeneratorBlockEntity.SYNC_VARIANT,
                (client, handler, buf, responseSender) -> {
                    if (client.world == null)
                        return;

                    Identifier id = Identifier.tryParse(buf.readString());
                    BlockPos consolePos = buf.readBlockPos();

                    if (client.world.getBlockEntity(consolePos) instanceof ConsoleGeneratorBlockEntity console)
                        console.setVariant(id);
                });

        WorldRenderEvents.END.register((context) -> SonicRendering.getInstance().renderWorld(context));
        HudRenderCallback.EVENT.register((context, delta) -> SonicRendering.getInstance().renderGui(context, delta));

        SonicModelLoader.init();

        AstralMapBlock.registerSyncListener();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> BOTI.tryWarn());
    }
    public static Screen screenFromId(int id) {
        return screenFromId(id, null, null);
    }

    public static Screen screenFromId(int id, @Nullable ClientTardis tardis) {
        return screenFromId(id, tardis, null);
    }

    public static Screen screenFromId(int id, @Nullable ClientTardis tardis, @Nullable BlockPos console) {
        return switch (id) {
            case 0 -> new MonitorScreen(tardis, console);
            case 1 -> new BlueprintFabricatorScreen();
            case 2 -> new AstralMapScreen();
            default -> null;
        };
    }

    public void chargedZeitonCrystalPredicate() {
        ModelPredicateProviderRegistry.register(AITItems.CHARGED_ZEITON_CRYSTAL, new Identifier("fuel"),
                (itemStack, clientWorld, livingEntity, integer) -> {
                    if (livingEntity == null)
                        return 0.0F;
                    if (itemStack.getItem() instanceof ChargedZeitonCrystalItem item) {
                        float value = (float) (item.getCurrentFuel(itemStack) / item.getMaxFuel(itemStack));
                        if (value > 0.0f && value < 0.5f) {
                            return 0.5f;
                        } else if (value > 0.5f && value < 1.0f) {
                            return 1.0f;
                        } else {
                            return 0.0f;
                        }
                    }

                    return 0.0F;
                });
    }

    public static void waypointPredicate() {
        ModelPredicateProviderRegistry.register(AITItems.WAYPOINT_CARTRIDGE, new Identifier("type"),
                (stack, clientWorld, livingEntity, integer) ->
                        stack.getOrCreateNbt().contains(WaypointItem.POS_KEY) ? 1 : 0);
    }

    public static void hammerPredicate() {
        ModelPredicateProviderRegistry.register(AITItems.HAMMER, new Identifier("toymakered"),
                (itemStack, clientWorld, livingEntity, integer) -> {
                    if (itemStack.getItem() instanceof HammerItem) {
                        if (itemStack.getName().getString().equalsIgnoreCase("Toymaker Hammer"))
                            return 1.0f;
                        else
                            return 0.0f;
                    }
                    return 0.0F;
                });
    }

    public static void siegeItemPredicate() {
        ModelPredicateProviderRegistry.register(AITItems.HAMMER, new Identifier("bricked"),
                (itemStack, clientWorld, livingEntity, integer) -> {
                    if (itemStack.getOrCreateNbt().contains(SiegeTardisItem.CURRENT_TEXTURE_KEY)) {
                        return itemStack.getOrCreateNbt().getInt(SiegeTardisItem.CURRENT_TEXTURE_KEY);
                    }
                    return 0.0f;
                });
    }

    public static void adventItemPredicates() {
        ModelPredicateProviderRegistry.register(AITItems.HYPERCUBE, new Identifier("advent"),
                (itemStack, clientWorld, livingEntity, integer) -> {
                    if (itemStack.getItem() instanceof HypercubeItem) {
                        return isUnlockedOnThisDay(Calendar.JANUARY, 1) ? 1.0F : 0.0F;
                    }
                    return 0.0F;
                });

        ModelPredicateProviderRegistry.register(AITItems.HAZANDRA, new Identifier("advent"),
                (itemStack, clientWorld, livingEntity, integer) -> {
                    if (itemStack.getItem() instanceof InteriorTeleporterItem) {
                        return isUnlockedOnThisDay(Calendar.DECEMBER, 28) ? 1.0F : 0.0F;
                    }
                    return 0.0F;
                });

        ModelPredicateProviderRegistry.register(AITItems.IRON_KEY, new Identifier("advent"),
                (itemStack, clientWorld, livingEntity, integer) -> {
                    if (itemStack.getItem() instanceof KeyItem) {
                        return isUnlockedOnThisDay(Calendar.DECEMBER, 26) ? 1.0F : 0.0F;
                    }
                    return 0.0F;
                });

        ModelPredicateProviderRegistry.register(AITItems.GOLD_KEY, new Identifier("advent"),
                (itemStack, clientWorld, livingEntity, integer) -> {
                    if (itemStack.getItem() instanceof KeyItem) {
                        return isUnlockedOnThisDay(Calendar.DECEMBER, 26) ? 1.0F : 0.0F;
                    }
                    return 0.0F;
                });

        ModelPredicateProviderRegistry.register(AITItems.NETHERITE_KEY, new Identifier("advent"),
                (itemStack, clientWorld, livingEntity, integer) -> {
                    if (itemStack.getItem() instanceof KeyItem) {
                        return isUnlockedOnThisDay(Calendar.DECEMBER, 26) ? 1.0F : 0.0F;
                    }
                    return 0.0F;
                });

        ModelPredicateProviderRegistry.register(AITItems.CLASSIC_KEY, new Identifier("advent"),
                (itemStack, clientWorld, livingEntity, integer) -> {
                    if (itemStack.getItem() instanceof KeyItem) {
                        return isUnlockedOnThisDay(Calendar.DECEMBER, 26) ? 1.0F : 0.0F;
                    }
                    return 0.0F;
                });
    }

    public static void blockEntityRendererRegister() {
        BlockEntityRendererFactories.register(AITBlockEntityTypes.CONSOLE_BLOCK_ENTITY_TYPE, ConsoleRenderer::new);
        BlockEntityRendererFactories.register(AITBlockEntityTypes.CONSOLE_GENERATOR_ENTITY_TYPE,
                ConsoleGeneratorRenderer::new);
        BlockEntityRendererFactories.register(AITBlockEntityTypes.EXTERIOR_BLOCK_ENTITY_TYPE, ExteriorRenderer::new);
        BlockEntityRendererFactories.register(AITBlockEntityTypes.DOOR_BLOCK_ENTITY_TYPE, DoorRenderer::new);
        BlockEntityRendererFactories.register(AITBlockEntityTypes.CORAL_BLOCK_ENTITY_TYPE, CoralRenderer::new);
        BlockEntityRendererFactories.register(AITBlockEntityTypes.MONITOR_BLOCK_ENTITY_TYPE, MonitorRenderer::new);
        BlockEntityRendererFactories.register(AITBlockEntityTypes.ARTRON_COLLECTOR_BLOCK_ENTITY_TYPE,
                ArtronCollectorRenderer::new);
        BlockEntityRendererFactories.register(AITBlockEntityTypes.PLAQUE_BLOCK_ENTITY_TYPE, PlaqueRenderer::new);
        BlockEntityRendererFactories.register(AITBlockEntityTypes.WALL_MONITOR_BLOCK_ENTITY_TYPE,
                WallMonitorRenderer::new);
        BlockEntityRendererFactories.register(AITBlockEntityTypes.ENGINE_BLOCK_ENTITY_TYPE, EngineRenderer::new);
        BlockEntityRendererFactories.register(AITBlockEntityTypes.FABRICATOR_BLOCK_ENTITY_TYPE,
                FabricatorRenderer::new);
        BlockEntityRendererFactories.register(AITBlockEntityTypes.WAYPOINT_BANK_BLOCK_ENTITY_TYPE,
                WaypointBankBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(AITBlockEntityTypes.FLAG_BLOCK_ENTITY_TYPE, FlagBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(AITBlockEntityTypes.GENERIC_SUBSYSTEM_BLOCK_TYPE,
                GenericSubSystemRenderer::new);
        BlockEntityRendererFactories.register(AITBlockEntityTypes.POWER_CONVERTER_BLOCK_TYPE,
                PowerConverterRenderer::new);
        BlockEntityRendererFactories.register(AITBlockEntityTypes.FOOD_MACHINE_BLOCK_ENTITY_TYPE,
                FoodMachineRenderer::new);
        BlockEntityRendererFactories.register(AITBlockEntityTypes.ASTRAL_MAP, AstralMapRenderer::new);
        if (isUnlockedOnThisDay(Calendar.DECEMBER, 30)) {
            BlockEntityRendererFactories.register(AITBlockEntityTypes.SNOW_GLOBE_BLOCK_ENTITY_TYPE,
                    SnowGlobeRenderer::new);
        }
    }

    public static void entityRenderRegister() {
        EntityRendererRegistry.register(AITEntityTypes.CONTROL_ENTITY_TYPE, ControlEntityRenderer::new);
        EntityRendererRegistry.register(AITEntityTypes.FALLING_TARDIS_TYPE, FallingTardisRenderer::new);
        EntityRendererRegistry.register(AITEntityTypes.FLIGHT_TARDIS_TYPE, FlightTardisRenderer::new);
        EntityRendererRegistry.register(AITEntityTypes.GALLIFREY_FALLS_PAINTING_ENTITY_TYPE, GallifreyanPaintingEntityRenderer::new);
        EntityRendererRegistry.register(AITEntityTypes.TRENZALORE_PAINTING_ENTITY_TYPE, TrenzalorePaintingEntityRenderer::new);
//        if (isUnlockedOnThisDay(Calendar.DECEMBER, 26)) {
//            EntityRendererRegistry.register(AITEntityTypes.COBBLED_SNOWBALL_TYPE, FlyingItemEntityRenderer::new);
//        }
        EntityRendererRegistry.register(AITEntityTypes.RIFT_ENTITY, RiftEntityRenderer::new);
    }

    public static void setupBlockRendering() {
        BlockRenderLayerMap map = BlockRenderLayerMap.INSTANCE;
        map.putBlock(AITBlocks.ZEITON_BLOCK, RenderLayer.getCutout());
        map.putBlock(AITBlocks.BUDDING_ZEITON, RenderLayer.getCutout());
        map.putBlock(AITBlocks.ENGINE_BLOCK, RenderLayer.getCutout());
        map.putBlock(AITBlocks.ZEITON_CLUSTER, RenderLayer.getCutout());
        map.putBlock(AITBlocks.LARGE_ZEITON_BUD, RenderLayer.getCutout());
        map.putBlock(AITBlocks.MEDIUM_ZEITON_BUD, RenderLayer.getCutout());
        map.putBlock(AITBlocks.SMALL_ZEITON_BUD, RenderLayer.getCutout());
        map.putBlock(AITBlocks.MACHINE_CASING, RenderLayer.getCutout());
        map.putBlock(AITBlocks.FABRICATOR, RenderLayer.getTranslucent());
        map.putBlock(AITBlocks.ENVIRONMENT_PROJECTOR, RenderLayer.getTranslucent());
        map.putBlock(AITBlocks.WAYPOINT_BANK, RenderLayer.getCutout());
        if (isUnlockedOnThisDay(Calendar.DECEMBER, 30)) {
            map.putBlock(AITBlocks.SNOW_GLOBE, RenderLayer.getCutout());
        }
        map.putBlock(AITBlocks.TARDIS_CORAL_BLOCK, RenderLayer.getCutout());
        map.putBlock(AITBlocks.TARDIS_CORAL_FAN, RenderLayer.getCutout());
        map.putBlock(AITBlocks.TARDIS_CORAL_WALL_FAN, RenderLayer.getCutout());
        map.putBlock(AITBlocks.MATRIX_ENERGIZER, RenderLayer.getCutout());
    }

    public void registerItemColors() {
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
                    if (tintIndex != 0)
                        return -1;

                    PersonalityMatrixItem personalityMatrixItem = (PersonalityMatrixItem) stack.getItem();
                    int[] integers = personalityMatrixItem.getColor(stack);
                    return colorToInt(integers[0], integers[1], integers[2]);
                }, AITItems.PERSONALITY_MATRIX);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 :
                DrinkUtil.getColor(stack), AITItems.MUG);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (tintIndex != 0)
                return -1;

            WaypointItem waypoint = (WaypointItem) stack.getItem();
            return waypoint.getColor(stack);
        }, AITItems.WAYPOINT_CARTRIDGE);
    }

    public void registerParticles() {
        ParticleFactoryRegistry.getInstance().register(CORAL_PARTICLE, EndRodParticle.Factory::new);
    }

    public void exteriorBOTI(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        ClientWorld world = client.world;
        MatrixStack stack = context.matrixStack();
        var exteriorQueue = new ArrayList<>(BOTI.EXTERIOR_RENDER_QUEUE);
        for (ExteriorBlockEntity exterior : exteriorQueue) {
            if (exterior == null || !exterior.isLinked() || exterior.tardis().isEmpty()) continue;
            Tardis tardis = exterior.tardis().get();
            if (tardis == null || tardis.getExterior() == null) return;
            ClientExteriorVariantSchema variant = tardis.getExterior().getVariant().getClient();
            ExteriorModel model = variant.model();
            BlockPos pos = exterior.getPos();
            stack.push();
            stack.translate(0.5, 0, 0.5);
            stack.translate(pos.getX() - context.camera().getPos().getX(), pos.getY() - context.camera().getPos().getY(), pos.getZ() - context.camera().getPos().getZ());
            stack.scale(1, -1, -1);
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(RotationPropertyHelper.toDegrees(exterior.getCachedState().get(ExteriorBlock.ROTATION))));
            int light = world.getLightLevel(pos);
            if ((tardis.door().getLeftRot() > 0 || variant.hasTransparentDoors()) && !tardis.isGrowth()) {
                light = LightmapTextureManager.pack(world.getLightLevel(LightType.BLOCK, pos), world.getLightLevel(LightType.SKY, pos));
                TardisExteriorBOTI boti = new TardisExteriorBOTI();
                boti.renderExteriorBoti(exterior, variant, stack,
                        AITMod.id("textures/environment/tardis_sky.png"), model,
                        BotiPortalModel.getTexturedModelData().createModel(), light);
            }
            stack.pop();
        }
        BOTI.EXTERIOR_RENDER_QUEUE.clear();
    }

    public void doorBOTI(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        ClientWorld world = client.world;
        MatrixStack stack = context.matrixStack();
        boolean bl = TardisServerWorld.isTardisDimension(world);
        if (bl) {
            ClientTardis tardis = ClientTardisUtil.getCurrentTardis();
            if (tardis == null || tardis.getDesktop() == null) return;
            ClientExteriorVariantSchema variant = tardis.getExterior().getVariant().getClient();
            AnimatedModel model = variant.getDoor().model();
            for (DoorBlockEntity door : BOTI.DOOR_RENDER_QUEUE) {
                if (door == null) continue;
                BlockPos pos = door.getPos();
                stack.push();
                stack.translate(0.5, 0, 0.5);
                stack.translate(pos.getX() - context.camera().getPos().getX(), pos.getY() - context.camera().getPos().getY(), pos.getZ() - context.camera().getPos().getZ());
                stack.scale(1, -1, -1);
                stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(door.getCachedState().get(DoorBlock.FACING).asRotation()));
                int light = world.getLightLevel(pos.up());
                if ((tardis.door().getLeftRot() > 0  || variant.hasTransparentDoors()) && !tardis.isGrowth()) {
                    light = LightmapTextureManager.pack(world.getLightLevel(LightType.BLOCK, pos), world.getLightLevel(LightType.SKY, pos));
                    TardisDoorBOTI.renderInteriorDoorBoti(tardis, door, variant, stack,
                            AITMod.id("textures/environment/tardis_sky.png"), model,
                            BotiPortalModel.getTexturedModelData().createModel(), light, context.tickDelta());
                }
                stack.pop();
            }
            BOTI.DOOR_RENDER_QUEUE.clear();
        }
    }

    public void gallifreyanBOTI(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        SinglePartEntityModel contents = new GallifreyFallsModel(GallifreyFallsModel.getTexturedModelData().createModel());
        Identifier frameTex = GallifreyanPaintingEntityRenderer.GALLIFREY_FRAME_TEXTURE;
        Identifier contentsTex = GallifreyanPaintingEntityRenderer.GALLIFREY_PAINTING_TEXTURE;
        if (client.player == null || client.world == null) return;
        ClientWorld world = client.world;
        MatrixStack stack = context.matrixStack();
        for (BOTIPaintingEntity painting : BOTI.GALLIFREYAN_RENDER_QUEUE) {
            if (painting == null) continue;
            Vec3d pos = painting.getPos();
            stack.push();
            stack.translate(pos.getX() - context.camera().getPos().getX(),
                    pos.getY() - context.camera().getPos().getY(), pos.getZ() - context.camera().getPos().getZ());
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(painting.getBodyYaw()));
            stack.translate(0, -0.5f, 0.5);
            PaintingFrameModel frame = new PaintingFrameModel(PaintingFrameModel.getTexturedModelData().createModel());
            BlockPos blockPos = BlockPos.ofFloored(painting.getClientCameraPosVec(client.getTickDelta()));
            PaintingBOTI.renderBOTIPainting(stack, frame,
                    LightmapTextureManager.pack(world.getLightLevel(LightType.BLOCK, blockPos),
                            world.getLightLevel(LightType.SKY, blockPos)), contents, frameTex, contentsTex);
            stack.pop();
        }
        BOTI.GALLIFREYAN_RENDER_QUEUE.clear();
    }

    public void trenzaloreBOTI(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        SinglePartEntityModel contents = new TrenzalorePaintingModel(TrenzalorePaintingModel.getTexturedModelData().createModel());
        Identifier frameTex = TrenzalorePaintingEntityRenderer.TRENZALORE_FRAME_TEXTURE;
        Identifier contentsTex = TrenzalorePaintingEntityRenderer.TRENZALORE_PAINTING_TEXTURE;
        if (client.player == null || client.world == null) return;
        ClientWorld world = client.world;
        MatrixStack stack = context.matrixStack();
        for (BOTIPaintingEntity painting : BOTI.TRENZALORE_PAINTING_QUEUE) {
            if (painting == null) continue;
            Vec3d pos = painting.getPos();
            stack.push();
            stack.translate(pos.getX() - context.camera().getPos().getX(),
                    pos.getY() - context.camera().getPos().getY(), pos.getZ() - context.camera().getPos().getZ());
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(painting.getBodyYaw()));
            stack.translate(0, -0.5f, 0.5);
            PaintingFrameModel frame = new PaintingFrameModel(PaintingFrameModel.getTexturedModelData().createModel());
            BlockPos blockPos = BlockPos.ofFloored(painting.getClientCameraPosVec(client.getTickDelta()));
            PaintingBOTI.renderBOTIPainting(stack, frame,
                    LightmapTextureManager.pack(world.getLightLevel(LightType.BLOCK, blockPos),
                            world.getLightLevel(LightType.SKY, blockPos)), contents, frameTex, contentsTex);
            stack.pop();
        }
        BOTI.TRENZALORE_PAINTING_QUEUE.clear();
    }

    public void riftBOTI(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        ClientWorld world = client.world;
        MatrixStack stack = context.matrixStack();
        for (RiftEntity rift : BOTI.RIFT_RENDERING_QUEUE) {
            if (rift == null) continue;
            Vec3d pos = rift.getPos();
            stack.push();
            stack.translate(pos.getX() - context.camera().getPos().getX(),
                    pos.getY() - context.camera().getPos().getY(), pos.getZ() - context.camera().getPos().getZ());
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rift.getBodyYaw()));
            stack.translate(0, 1f, 0);
            RiftModel riftModel = new RiftModel(RiftModel.getTexturedModelData().createModel());
            BlockPos blockPos = BlockPos.ofFloored(rift.getClientCameraPosVec(client.getTickDelta()));
            RiftBOTI.renderRiftBoti(stack, riftModel, LightmapTextureManager.pack(world.getLightLevel(LightType.BLOCK, blockPos), world.getLightLevel(LightType.SKY, blockPos)));
            stack.pop();
        }
        BOTI.RIFT_RENDERING_QUEUE.clear();
    }
}

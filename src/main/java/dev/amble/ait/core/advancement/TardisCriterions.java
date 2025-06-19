package dev.amble.ait.core.advancement;

import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.advancement.Advancement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.effects.ZeitonHighEffect;
import dev.amble.ait.core.engine.impl.EngineSystem;
import dev.amble.ait.core.world.TardisServerWorld;

public class TardisCriterions {
    public static SimpleCriterion ROOT = SimpleCriterion.create("root").register();
    public static SimpleCriterion TAKEOFF = SimpleCriterion.create("takeoff").register();
    public static SimpleCriterion CRASH = SimpleCriterion.create("crash").register();
    public static SimpleCriterion VEGETATION = SimpleCriterion.create("break_vegetation").register();
    public static SimpleCriterion PLACE_CORAL = SimpleCriterion.create("place_coral").register();
    public static final SimpleCriterion PLACE_ENERGIZER = SimpleCriterion.create("place_energizer").register();
    public static SimpleCriterion FEED_POWER_CONVERTER = SimpleCriterion.create("feed_power_converter").register();
    public static SimpleCriterion ENTER_TARDIS = SimpleCriterion.create("enter_tardis").register();
    public static SimpleCriterion REDECORATE = SimpleCriterion.create("redecorate").register();
    public static SimpleCriterion FORCED_ENTRY = SimpleCriterion.create("forced_entry").register();
    public static SimpleCriterion SONIC_WOOD = SimpleCriterion.create("sonic_wood").register();
    public static SimpleCriterion PILOT_HIGH = SimpleCriterion.create("pilot_high").register();
    public static SimpleCriterion REACH_PILOT = SimpleCriterion.create("reach_pilot").register();
    public static SimpleCriterion REACH_OWNER = SimpleCriterion.create("reach_owner").register();
    public static SimpleCriterion ENABLE_SUBSYSTEM = SimpleCriterion.create("enable_subsystem").register();
    public static SimpleCriterion REPAIR_SUBSYSTEM = SimpleCriterion.create("repair_subsystem").register();
    public static SimpleCriterion ENGINES_PHASE = SimpleCriterion.create("engines_phase").register();
    public static SimpleCriterion BRAND_NEW = SimpleCriterion.create("brand_new").register();

    public static void init() {
        AITMod.LOGGER.info("Initializing Tardis Criterions");

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ROOT.trigger(handler.getPlayer()));

        TardisEvents.CRASH.register(tardis -> tardis.asServer().world().getPlayers().forEach(
                player -> TardisCriterions.CRASH.trigger(player)));

        TardisEvents.ENTER_FLIGHT.register(tardis -> {
            tardis.asServer().world().getPlayers().forEach(player -> {
                TardisCriterions.TAKEOFF.trigger(player);

                if (ZeitonHighEffect.isHigh(player))
                    TardisCriterions.PILOT_HIGH.trigger(player);
            });
        });

        TardisEvents.ENTER_TARDIS.register((tardis, entity) -> {
            if (!(entity instanceof ServerPlayerEntity player))
                return;

            Advancement advancement = player.getServer().getAdvancementLoader().get(new Identifier("ait/enter_tardis"));

            if (player.getWorld() instanceof TardisServerWorld && !player.getAdvancementTracker().getProgress(advancement).isDone()) {
                Scheduler.get().runTaskLater(() -> tardis.asServer().world().playSound(null, player.getBlockPos(), AITSounds.WONDERFUL_TIME_IN_SPACE,
                        SoundCategory.PLAYERS, 0.6f, 1.0f), TaskStage.END_SERVER_TICK, TimeUnit.TICKS, 400);
            }

            TardisCriterions.ENTER_TARDIS.trigger(player);
        });

        TardisEvents.FORCED_ENTRY.register((tardis, entity) -> {
            if (!(entity instanceof ServerPlayerEntity player))
                return;

            TardisCriterions.FORCED_ENTRY.trigger(player);
        });

        TardisEvents.SUBSYSTEM_ENABLE.register(system -> {
            if (system.isClient() || system instanceof EngineSystem)
                return;

            system.tardis().asServer().world().getPlayers().forEach(player ->
                    TardisCriterions.ENGINES_PHASE.trigger(player));
        });
        TardisEvents.SUBSYSTEM_REPAIR.register(system -> {
            if (system.isClient())
                return;

            system.tardis().asServer().world().getPlayers().forEach(player ->
                    TardisCriterions.REPAIR_SUBSYSTEM.trigger(player));
        });

        TardisEvents.ENGINES_PHASE.register(system -> {
            if (system.isClient())
                return;

            system.tardis().asServer().world().getPlayers().forEach(player ->
                    TardisCriterions.REACH_PILOT.trigger(player));
        });
    }
}

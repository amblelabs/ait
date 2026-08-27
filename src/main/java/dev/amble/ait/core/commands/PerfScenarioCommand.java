package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.compat.permissionapi.PermissionAPICompat;
import dev.amble.ait.core.entities.BOTIPaintingEntity;
import dev.amble.ait.core.entities.RiftEntity;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.tardis.handler.ShieldHandler;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.tardis.manager.TardisBuilder;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.registry.impl.DesktopRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;

/**
 * Scenario setup for profiling runs. Places a deterministic grid of landed TARDISes and flips their
 * doors, so a scenario is a reproducible list of commands rather than something assembled by hand.
 *
 * <p>Coral growth is the only in-game route to a new TARDIS and it needs random ticks, which the
 * profiling world switches off. These build straight through {@link TardisBuilder} instead.
 *
 * <p>The capsule variant is pinned deliberately: its {@code hasTransparentDoors()} is false, so a
 * closed door really does mean no BOTI, which a scenario comparing the two depends on.
 */
public class PerfScenarioCommand {

    private static final int MAX_COUNT = 64;
    private static final String DEFAULT_VARIANT = "exterior/capsule/default";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID)
                .then(literal("perf-spawn")
                        .requires(source -> PermissionAPICompat.hasPermission(source, "ait.command.perf", 2))
                        .then(argument("count", IntegerArgumentType.integer(1, MAX_COUNT))
                                .then(argument("spacing", IntegerArgumentType.integer(2, 64))
                                        .then(argument("pos", BlockPosArgumentType.blockPos())
                                                .executes(context -> spawn(context, DEFAULT_VARIANT))
                                                .then(argument("variant", StringArgumentType.string())
                                                        .executes(context -> spawn(context,
                                                                StringArgumentType.getString(context, "variant")))))))));

        dispatcher.register(literal(AITMod.MOD_ID)
                .then(literal("perf-tp")
                        .requires(source -> PermissionAPICompat.hasPermission(source, "ait.command.perf", 2))
                        .then(literal("interior").executes(context -> teleport(context, true)))
                        .then(literal("exterior").executes(context -> teleport(context, false)))
                        .then(literal("console").executes(PerfScenarioCommand::teleportToConsole))));

        dispatcher.register(literal(AITMod.MOD_ID)
                .then(literal("perf-flight")
                        .requires(source -> PermissionAPICompat.hasPermission(source, "ait.command.perf", 2))
                        .then(argument("on", com.mojang.brigadier.arguments.BoolArgumentType.bool())
                                .executes(PerfScenarioCommand::flight))));

        dispatcher.register(literal(AITMod.MOD_ID)
                .then(literal("perf-state")
                        .requires(source -> PermissionAPICompat.hasPermission(source, "ait.command.perf", 2))
                        .then(argument("what", StringArgumentType.word())
                                .then(argument("on", com.mojang.brigadier.arguments.BoolArgumentType.bool())
                                        .executes(PerfScenarioCommand::state)))));

        dispatcher.register(literal(AITMod.MOD_ID)
                .then(literal("perf-doors")
                        .requires(source -> PermissionAPICompat.hasPermission(source, "ait.command.perf", 2))
                        .then(literal("open").executes(context -> doors(context, true)))
                        .then(literal("closed").executes(context -> doors(context, false)))));

        dispatcher.register(literal(AITMod.MOD_ID)
                .then(literal("perf-verify")
                        .requires(source -> PermissionAPICompat.hasPermission(source, "ait.command.perf", 2))
                        .executes(PerfScenarioCommand::verify)));

        dispatcher.register(literal(AITMod.MOD_ID)
                .then(literal("perf-clear")
                        .requires(source -> PermissionAPICompat.hasPermission(source, "ait.command.perf", 2))
                        .executes(PerfScenarioCommand::clear)));
    }

    private static int spawn(CommandContext<ServerCommandSource> context, String variant) {
        ServerCommandSource source = context.getSource();
        int count = IntegerArgumentType.getInteger(context, "count");
        int spacing = IntegerArgumentType.getInteger(context, "spacing");
        BlockPos origin = BlockPosArgumentType.getBlockPos(context, "pos");
        ServerWorld world = source.getWorld();

        List<ServerPlayerEntity> players = source.getServer().getPlayerManager().getPlayerList();

        if (players.isEmpty()) {
            source.sendFeedback(() -> Text.literal("No player online to own the TARDISes."), false);
            return 0;
        }

        ServerPlayerEntity owner = players.get(0);
        int side = (int) Math.ceil(Math.sqrt(count));
        int made = 0;

        for (int i = 0; i < count; i++) {
            BlockPos pos = origin.add((i % side) * spacing, 0, (i / side) * spacing);

            TardisBuilder builder = new TardisBuilder()
                    .at(CachedDirectedGlobalPos.create(world, pos, (byte) 0))
                    .owner(owner)
                    .<FuelHandler>with(TardisComponent.Id.FUEL, fuel -> fuel.setCurrentFuel(5000))
                    .with(TardisComponent.Id.TRAVEL, travel -> travel.tardis().travel().autopilot(false))
                    .exterior(ExteriorVariantRegistry.getInstance().get(AITMod.id(variant)))
                    .desktop(DesktopRegistry.getInstance().get(AITMod.id("alnico")));

            ServerTardis created = ServerTardisManager.getInstance().create(builder);

            if (created == null)
                continue;

            // A fresh TARDIS is unpowered, and its subsystems all start disabled. enablePower() with
            // the default engine requirement is a silent no-op in that state, so the subsystems are
            // repaired first and the engine switched on before asking for power. Without this every
            // power-gated render path (emission most of all) never runs and reads as free.
            created.subsystems().repairAll();
            created.subsystems().engine().setEnabled(true);
            created.<FuelHandler>handler(TardisComponent.Id.FUEL).enablePower(false);
            created.door().setLocked(false);
            created.door().setDeadlocked(false);

            made++;
        }

        int finalMade = made;
        source.sendFeedback(() -> Text.literal("Spawned " + finalMade + " TARDIS(es) on a " + spacing
                + " block grid from " + origin.toShortString()), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int doors(CommandContext<ServerCommandSource> context, boolean open) {
        List<ServerTardis> all = new ArrayList<>();
        ServerTardisManager.getInstance().forEach(all::add);

        for (ServerTardis tardis : all) {
            if (open)
                tardis.door().openDoors();
            else
                tardis.door().closeDoors();
        }

        int finalTouched = all.size();
        context.getSource().sendFeedback(
                () -> Text.literal((open ? "Opened" : "Closed") + " the doors on " + finalTouched + " TARDIS(es)."),
                true);

        return Command.SINGLE_SUCCESS;
    }

    private static int teleport(CommandContext<ServerCommandSource> context, boolean inside) {
        ServerCommandSource source = context.getSource();
        List<ServerPlayerEntity> players = source.getServer().getPlayerManager().getPlayerList();
        List<ServerTardis> all = new ArrayList<>();
        ServerTardisManager.getInstance().forEach(all::add);

        if (players.isEmpty() || all.isEmpty()) {
            source.sendFeedback(() -> Text.literal("Need a player and at least one TARDIS."), false);
            return 0;
        }

        ServerTardis tardis = all.get(0);

        for (ServerPlayerEntity player : players) {
            if (inside)
                TardisUtil.teleportInside(tardis, player);
            else
                TardisUtil.teleportOutside(tardis, player);
        }

        source.sendFeedback(() -> Text.literal("Teleported to the " + (inside ? "interior" : "exterior") + "."), true);

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Flips one piece of state on every TARDIS, so a scenario can isolate the render path it gates.
     */
    private static int state(CommandContext<ServerCommandSource> context) {
        String what = StringArgumentType.getString(context, "what");
        boolean on = com.mojang.brigadier.arguments.BoolArgumentType.getBool(context, "on");

        List<ServerTardis> all = new ArrayList<>();
        ServerTardisManager.getInstance().forEach(all::add);

        for (ServerTardis tardis : all) {
            switch (what) {
                case "siege" -> tardis.siege().setActive(on);
                case "shields" -> {
                    ShieldHandler shields = tardis.handler(TardisComponent.Id.SHIELDS);

                    if (on) {
                        shields.enable();
                        shields.enableVisuals();
                    } else {
                        shields.disableAll();
                    }
                }
                case "alarm" -> {
                    if (on)
                        tardis.alarm().enable();
                    else
                        tardis.alarm().disable();
                }
                case "power" -> {
                    if (on) {
                        tardis.<FuelHandler>handler(TardisComponent.Id.FUEL).setCurrentFuel(5000);
                        tardis.subsystems().repairAll();
                        tardis.subsystems().engine().setEnabled(true);
                        tardis.<FuelHandler>handler(TardisComponent.Id.FUEL).enablePower(false);
                    } else {
                        tardis.<FuelHandler>handler(TardisComponent.Id.FUEL).disablePower();
                    }
                }
                default -> {
                    context.getSource().sendFeedback(
                            () -> Text.literal("Unknown state '" + what + "'. Use siege, shields, alarm or power."),
                            false);
                    return 0;
                }
            }
        }

        int count = all.size();
        context.getSource().sendFeedback(
                () -> Text.literal("Set " + what + "=" + on + " on " + count + " TARDIS(es)."), true);

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Reports the state every render path is gated on, so a scenario can be checked before it is
     * profiled rather than after. Several of these default to off and fail silently when set, which
     * makes an unpowered TARDIS look like a cheap one.
     */
    private static int verify(CommandContext<ServerCommandSource> context) {
        List<ServerTardis> all = new ArrayList<>();
        ServerTardisManager.getInstance().forEach(all::add);

        int powered = 0, doorsOpen = 0, landed = 0, locked = 0, siege = 0, shields = 0, alarm = 0;

        for (ServerTardis tardis : all) {
            if (tardis.fuel().hasPower()) powered++;
            if (tardis.door().getLeftRot() > 0) doorsOpen++;
            if (tardis.travel().isLanded()) landed++;
            if (tardis.door().locked()) locked++;
            if (tardis.siege().isActive()) siege++;
            if (tardis.areVisualShieldsActive()) shields++;
            if (tardis.alarm().isEnabled()) alarm++;
        }

        String report = String.format(
                "PERF-VERIFY total=%d powered=%d doorsOpen=%d landed=%d locked=%d siege=%d shields=%d alarm=%d",
                all.size(), powered, doorsOpen, landed, locked, siege, shields, alarm);
        context.getSource().sendFeedback(() -> Text.literal(report), false);

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Puts the camera on the nearest console, looking down at it. The plain interior teleport lands on
     * {@code getDoorPos()}, which falls back to the world origin when a desktop has neither door nor
     * console, and drops the camera into the void with nothing in frame.
     */
    private static int teleportToConsole(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        List<ServerPlayerEntity> players = source.getServer().getPlayerManager().getPlayerList();
        List<ServerTardis> all = new ArrayList<>();
        ServerTardisManager.getInstance().forEach(all::add);

        if (players.isEmpty() || all.isEmpty()) {
            source.sendFeedback(() -> Text.literal("Need a player and at least one TARDIS."), false);
            return 0;
        }

        ServerTardis tardis = all.get(0);
        Set<BlockPos> consoles = tardis.getDesktop().getConsolePos();

        if (consoles.isEmpty()) {
            source.sendFeedback(() -> Text.literal("PERF-TP-CONSOLE failed: this desktop has no console."), false);
            return 0;
        }

        BlockPos console = consoles.iterator().next();

        // Three back on +Z and two up, facing -Z and pitched down, so the console fills the frame.
        Vec3d camera = new Vec3d(console.getX() + 0.5, console.getY() + 2, console.getZ() + 3.5);

        for (ServerPlayerEntity player : players) {
            WorldUtil.teleportToWorld(player, tardis.world(), camera, 180f, 35f);
        }

        source.sendFeedback(() -> Text.literal("PERF-TP-CONSOLE ok console=" + console.toShortString()
                + " consoles=" + consoles.size()), false);

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Puts every TARDIS into flight, or lands it. Setting power and shutting the doors does not start
     * a flight, so the vortex scenario was measuring a landed TARDIS.
     */
    private static int flight(CommandContext<ServerCommandSource> context) {
        boolean on = com.mojang.brigadier.arguments.BoolArgumentType.getBool(context, "on");
        List<ServerTardis> all = new ArrayList<>();
        ServerTardisManager.getInstance().forEach(all::add);

        for (ServerTardis tardis : all) {
            if (on) {
                tardis.travel().autopilot(false);
                tardis.travel().handbrake(false);
                tardis.travel().forceDemat();
            } else {
                tardis.travel().stopHere();
            }
        }

        int count = all.size();
        context.getSource().sendFeedback(
                () -> Text.literal("Flight=" + on + " on " + count + " TARDIS(es)."), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int clear(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        List<ServerTardis> all = new ArrayList<>();
        ServerTardisManager.getInstance().forEach(all::add);

        for (ServerTardis tardis : all) {
            ServerTardisManager.getInstance().remove(source.getServer(), tardis);
        }

        // Rifts and paintings are summoned by scenarios and nothing else removes them, so they used to
        // survive into later runs. A single leftover rift is worth about a millisecond a frame.
        int entities = 0;

        for (ServerWorld world : source.getServer().getWorlds()) {
            List<Entity> doomed = new ArrayList<>();

            for (Entity entity : world.iterateEntities()) {
                if (entity instanceof RiftEntity || entity instanceof BOTIPaintingEntity
                        || entity instanceof ItemEntity)
                    doomed.add(entity);
            }

            for (Entity entity : doomed) {
                entity.discard();
                entities++;
            }
        }

        int finalEntities = entities;
        source.sendFeedback(() -> Text.literal("Removed " + all.size() + " TARDIS(es) and "
                + finalEntities + " leftover entities."), true);

        return Command.SINGLE_SUCCESS;
    }
}

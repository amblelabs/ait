package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.Collection;
import java.util.Collections;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.util.TardisUtil;

public final class TeleportInteriorCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("teleport").requires(source -> source.hasPermissionLevel(2))
                .then(argument("tardis", TardisArgumentType.tardis())
                                .then(literal("interior").executes(TeleportInteriorCommand::tpSelfInterior)
                                        .then(argument("entities", EntityArgumentType.players())
                                                .executes(TeleportInteriorCommand::tpToInterior)))
                                .then(literal("exterior").executes(TeleportInteriorCommand::tpSelfExterior)
                                        .then(argument("entities", EntityArgumentType.players())
                                                .executes(TeleportInteriorCommand::tpToExterior)))
                )));
    }

    private static int tpSelfInterior(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Entity source = context.getSource().getEntity();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        return tpToInterior(tardis, Collections.singleton(source));
    }

    private static int tpSelfExterior(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Entity source = context.getSource().getEntity();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        return tpToExterior(tardis, Collections.singleton(source));
    }

    private static int tpToInterior(CommandContext<ServerCommandSource> context)
            throws CommandSyntaxException {
        Entity source = context.getSource().getEntity();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, "entities");

        return tpToInterior(tardis, source, entities);
    }

    private static int tpToExterior(CommandContext<ServerCommandSource> context)
            throws CommandSyntaxException {
        Entity source = context.getSource().getEntity();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, "entities");

        return tpToExterior(tardis, source, entities);
    }

    private static int tpToInterior(ServerTardis tardis, Entity source, Collection<? extends Entity> players) {
        for (Entity player : players) {
            TardisUtil.teleportInside(tardis, player);
        }

        source.sendMessage(Text.translatableWithFallback("tardis.teleport.interior.success",
                "Successful teleport - interior of [" + tardis.getUuid().toString().substring(0, 7) + "]"));

        return Command.SINGLE_SUCCESS;
    }

    private static int tpToExterior(ServerTardis tardis, Entity source, Collection<? extends Entity> players) {
        for (Entity player : players) {
            TardisUtil.teleportOutside(tardis, player);
        }

        source.sendMessage(Text.translatableWithFallback("tardis.teleport.exterior.success",
                "Successful teleport - exterior of [" + tardis.getUuid().toString().substring(0, 7) + "]"));

        return Command.SINGLE_SUCCESS;
    }

    private static int tpToInterior(ServerTardis tardis, Collection<? extends Entity> players) {
        if (players.isEmpty())
            return 0;

        return tpToInterior(tardis, players.stream().findFirst().get(), players);
    }

    private static int tpToExterior(ServerTardis tardis, Collection<? extends Entity> players) {
        if (players.isEmpty())
            return 0;

        return tpToExterior(tardis, players.stream().findFirst().get(), players);
    }
}

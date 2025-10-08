package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.amble.ait.core.tardis.handler.mood.v2.MoodHandler2;
import net.minecraft.server.command.ServerCommandSource;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.compat.permissionapi.PermissionAPICompat;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.handler.mood.MoodHandler;

public class TriggerMoodRollCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("trigger-mood-roll")
                .requires(source -> PermissionAPICompat.hasPermission(source, "ait.command.trigger-mood-roll", 2)).then(argument("tardis", TardisArgumentType.tardis())
                        .executes(TriggerMoodRollCommand::triggerMoodRollCommand))));
    }

    private static int triggerMoodRollCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        System.out.println(tardis.<MoodHandler2>handler(TardisComponent.Id.MOOD).container.toString());
        //tardis.<MoodHandler2>handler(TardisComponent.Id.MOOD).runEvent();

        return Command.SINGLE_SUCCESS;
    }
}

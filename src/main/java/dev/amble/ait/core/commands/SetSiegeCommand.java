package dev.amble.ait.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetSiegeCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("siege")
                .requires(source -> source.hasPermissionLevel(2)).then(argument("tardis", TardisArgumentType.tardis())
                        .then(argument("siege", BoolArgumentType.bool()).executes(SetSiegeCommand::runCommand)))));
    }

    // TODO: improve feedback
    private static int runCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");
        boolean sieged = BoolArgumentType.getBool(context, "siege");

        tardis.siege().setActive(sieged);
        return Command.SINGLE_SUCCESS;
    }
}

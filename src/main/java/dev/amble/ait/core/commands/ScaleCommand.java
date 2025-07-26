package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import dev.amble.ait.AITMod;
import dev.amble.ait.compat.permissionapi.PermissionAPICompat;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.handler.StatsHandler;

public class ScaleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("scale").requires(source -> PermissionAPICompat.hasPermission(source, "ait.command.scale", 2))
                .then(argument("tardis", TardisArgumentType.tardis()).then(argument("x", DoubleArgumentType.doubleArg()))
                        .then(argument("y", DoubleArgumentType.doubleArg()))
                        .then(argument("z", DoubleArgumentType.doubleArg())).executes(ScaleCommand::runCommand))));
    }

    private static int runCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");
        if (tardis == null) {
            context.getSource().sendError(Text.of("Tardis not found."));
            return 0;
        }
        double x = DoubleArgumentType.getDouble(context, "x");
        double y = DoubleArgumentType.getDouble(context, "y");
        double z = DoubleArgumentType.getDouble(context, "z");

        StatsHandler stats = tardis.stats();
        stats.setScale(x, y, z);

        return Command.SINGLE_SUCCESS;
    }
}

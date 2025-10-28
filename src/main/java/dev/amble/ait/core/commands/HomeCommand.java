package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.compat.permissionapi.PermissionAPICompat;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.lib.data.CachedDirectedGlobalPos;

public final class HomeCommand {

    private HomeCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID)
                .then(literal("home")
                        .requires(source -> PermissionAPICompat.hasPermission(source, "ait.command.home", 2))
                        .then(literal("get")
                                .then(argument("tardis", TardisArgumentType.tardis()).executes(HomeCommand::runGet)))
                        .then(literal("set")
                                .then(argument("tardis", TardisArgumentType.tardis()).executes(HomeCommand::runSet)))));
    }

    private static int runGet(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return sendHome(context, false);
    }

    private static int runSet(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return sendHome(context, true);
    }

    private static int sendHome(CommandContext<ServerCommandSource> context, boolean update) throws CommandSyntaxException {
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");
        CachedDirectedGlobalPos homePos = tardis.travel().home();

        if (update) {
            CachedDirectedGlobalPos current = tardis.travel().position();

            if (current == null)
                return -1;

            tardis.travel().setHome(current);
            homePos = tardis.travel().home();
        }

        if (homePos == null)
            return -1;

        BlockPos blockPos = homePos.getPos();
        Identifier dimension = homePos.getDimension().getValue();

        context.getSource().sendMessage(Text.literal(
                blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ() + " " + dimension));
        return Command.SINGLE_SUCCESS;
    }
}

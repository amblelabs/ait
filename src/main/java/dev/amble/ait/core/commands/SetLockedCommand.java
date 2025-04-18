package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;

public class SetLockedCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("lock").requires(source -> source.hasPermissionLevel(2))
                .then(argument("tardis", TardisArgumentType.tardis())
                        .then(argument("locked", BoolArgumentType.bool()).executes(SetLockedCommand::runCommand)))));
    }

    private static int runCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity source = context.getSource().getPlayer();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");
        boolean locked = BoolArgumentType.getBool(context, "locked");

        tardis.door().interactLock(locked, source, true);
        return Command.SINGLE_SUCCESS;
    }
}

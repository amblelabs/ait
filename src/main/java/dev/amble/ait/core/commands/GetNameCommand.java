package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;

public class GetNameCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID)
                .then(literal("name").requires(source -> source.hasPermissionLevel(2)).then(literal("get")
                        .then(argument("tardis", TardisArgumentType.tardis()).executes(GetNameCommand::runCommand)))));
    }

    private static int runCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        source.sendMessage(Text.translatableWithFallback("command.tardis.ait.name", "TARDIS name: %s", tardis.stats().getName()));
        return Command.SINGLE_SUCCESS;
    }
}

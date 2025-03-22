package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.world.TardisServerWorld;

public class SetNameInteriorCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("name").requires(source -> source.hasPermissionLevel(2))
                .then(literal("set")
                        .then(argument("value",
                                StringArgumentType.string()).executes(SetNameInteriorCommand::runCommand)))));
    }

    private static int runCommand(CommandContext<ServerCommandSource> context) {
        ServerTardis tardis = ((TardisServerWorld) context.getSource().getWorld()).getTardis();
        String name = StringArgumentType.getString(context, "value");

        tardis.stats().setName(name);
        return Command.SINGLE_SUCCESS;
    }
}

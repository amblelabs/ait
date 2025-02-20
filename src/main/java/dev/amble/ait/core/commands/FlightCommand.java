package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;

public class FlightCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("flight").requires(source -> source.hasPermissionLevel(2))
                        .then(argument("tardis", TardisArgumentType.tardis())
                                .executes(FlightCommand::execute))));

    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        context.getSource().getServer().executeSync(()
                -> tardis.flight().enterFlight(player));

        return Command.SINGLE_SUCCESS;
    }
}

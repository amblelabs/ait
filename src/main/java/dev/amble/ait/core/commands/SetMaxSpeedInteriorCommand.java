package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.world.TardisServerWorld;

/**
 * temporary command to set max speed until we find a proper way
 */
public class SetMaxSpeedInteriorCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("set-max-speed")
                .requires(source -> source.hasPermissionLevel(2)).then(
                        argument("speed", IntegerArgumentType.integer(0))
                                .executes(SetMaxSpeedInteriorCommand::runCommand))));
    }

    private static int runCommand(CommandContext<ServerCommandSource> context) {
        ServerTardis tardis = ((TardisServerWorld) context.getSource().getWorld()).getTardis();
        int speed = IntegerArgumentType.getInteger(context, "speed");

        tardis.travel().maxSpeed().set(speed);
        return Command.SINGLE_SUCCESS;
    }
}

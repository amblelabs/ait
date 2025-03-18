package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.world.TardisServerWorld;

public class SetLockedInteriorCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("lock").requires(source -> source.hasPermissionLevel(2))
                        .then(argument("locked",
                                BoolArgumentType.bool()).executes(SetLockedInteriorCommand::runCommand))));
    }

    private static int runCommand(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity source = context.getSource().getPlayer();
        if (source == null) return 0;
        ServerTardis tardis = ((TardisServerWorld) source.getServerWorld()).getTardis();
        boolean locked = BoolArgumentType.getBool(context, "locked");

        tardis.door().interactLock(locked, source, true);
        return Command.SINGLE_SUCCESS;
    }
}

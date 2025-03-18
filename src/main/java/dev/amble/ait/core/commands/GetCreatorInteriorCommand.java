package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.world.TardisServerWorld;

public class GetCreatorInteriorCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID)
                .then(literal("creator-name").requires(source -> source.hasPermissionLevel(2))
                        .then(literal("get").executes(GetCreatorInteriorCommand::runCommand))));
    }

    private static int runCommand(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerTardis tardis = ((TardisServerWorld) source.getWorld()).getTardis();

        source.sendMessage(Text.literal(tardis.stats().getPlayerCreatorName()));
        return Command.SINGLE_SUCCESS;
    }
}

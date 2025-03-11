package dev.amble.ait.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.amble.ait.AITMod;
import dev.amble.ait.registry.v2.AITRegistries;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class RegistryCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("registry")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(RegistryCommand::run)));
    }

    private static int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        AITRegistries.EXTERIOR_VARIANT.get(context.getSource().getWorld()).forEach(System.out::println);
        return Command.SINGLE_SUCCESS;
    }
}

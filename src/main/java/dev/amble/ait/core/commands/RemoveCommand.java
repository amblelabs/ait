package dev.amble.ait.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RemoveCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("remove").requires(source -> source.hasPermissionLevel(2))
                .then(argument("tardis", TardisArgumentType.tardis()).executes(RemoveCommand::removeCommand))));
    }

    private static int removeCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        source.sendFeedback(() -> Text.translatableWithFallback("tardis.remove.progress",
                "Removing TARDIS with id [%s]...", tardis.getUuid()), true);

        // Delete the file. File system operations are costly!
        ServerTardisManager.getInstance().remove(context.getSource().getServer(), tardis);

        source.sendFeedback(
                () -> Text.translatableWithFallback("tardis.remove.done", "TARDIS [%s] removed", tardis.getUuid()),
                true);

        return Command.SINGLE_SUCCESS;
    }
}

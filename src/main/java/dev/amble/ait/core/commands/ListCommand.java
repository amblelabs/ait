package dev.amble.ait.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.util.TextUtil;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ListCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("list")
                .requires(source -> source.hasPermissionLevel(2)).executes(ListCommand::list)
                .then(argument("search-args", StringArgumentType.greedyString())
                        .executes(ListCommand::search))
        ));
    }

    public static int list(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        source.sendMessage(Text.literal("TARDIS':"));

        ServerTardisManager.getInstance().forEach(tardis -> sendTardis(source, tardis));
        return Command.SINGLE_SUCCESS;
    }

    public static int search(CommandContext<ServerCommandSource> context) {
        String args = StringArgumentType.getString(context, "search-args");

        ServerCommandSource source = context.getSource();
        source.sendMessage(Text.literal("TARDIS':"));

        ServerTardisManager.getInstance().forEach(tardis -> filterTardis(source, tardis, args));
        return Command.SINGLE_SUCCESS;
    }

    private static void filterTardis(ServerCommandSource source, ServerTardis tardis, String args) {
        if (!tardis.getUuid().toString().contains(args))
            return;

        sendTardis(source, tardis);
    }

    private static void sendTardis(ServerCommandSource source, ServerTardis tardis) {
        source.sendMessage(Text.literal("  - ").append(TextUtil.forTardis(tardis)));
    }
}

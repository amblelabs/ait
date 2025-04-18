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
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.util.TextUtil;

public class LoadCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("load")
                .requires(source -> source.hasPermissionLevel(2)).executes(LoadCommand::load)
                .then(argument("target", TardisArgumentType.tardis())
                        .executes(LoadCommand::search))
        ));
    }

    public static int load(CommandContext<ServerCommandSource> context) {
        ServerTardisManager.getInstance().loadAll(context.getSource().getServer(), (tardis -> sendTardis(context.getSource(), tardis)));

        return Command.SINGLE_SUCCESS;
    }

    public static int search(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerTardis loaded = TardisArgumentType.getTardis(context, "target");
        ServerCommandSource source = context.getSource();
        sendTardis(source, loaded);

        return Command.SINGLE_SUCCESS;
    }

    private static void sendTardis(ServerCommandSource source, ServerTardis loaded) {
        Text message = loaded != null ? Text.literal("Loaded: ").append(TextUtil.forTardis(loaded)) : Text.literal("No TARDIS found with that UUID.");

        source.sendMessage(message); // todo - translatable
    }
}

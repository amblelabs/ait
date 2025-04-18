package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.link.LinkableItem;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;

public class LinkCommand {

    // TODO: add slot argument, like in "/item replace" command
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("link").requires(source -> source.hasPermissionLevel(2))
                .then(argument("tardis", TardisArgumentType.tardis()).executes(LinkCommand::runCommand))));
    }

    private static int runCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity source = context.getSource().getPlayer();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        ItemStack stack = source.getMainHandStack();

        if (!(stack.getItem() instanceof LinkableItem linker))
            return 0;

        linker.link(stack, tardis);
        return Command.SINGLE_SUCCESS;
    }
}

package dev.amble.ait.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.link.LinkableItem;
import dev.amble.ait.compat.permissionapi.PermissionAPICompat;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class UnLinkCommand {

    // TODO: add slot argument, like in "/item replace" command
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("unlink")
                .executes(UnLinkCommand::runCommand)));
    }

    private static int runCommand(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity source = context.getSource().getPlayer();
        ItemStack stack = source.getMainHandStack();

        if (!(stack.getItem() instanceof LinkableItem linker))
            return 0;

        linker.unlink(stack);
        return Command.SINGLE_SUCCESS;
    }
}

package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.util.TextUtil;
import dev.amble.ait.core.world.TardisServerWorld;

public class GetInsideTardisCommand {

    // TODO: add BlockPosition argument type
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("this").requires(source -> source.hasPermissionLevel(2))
                .executes(GetInsideTardisCommand::runCommand)));
    }

    private static int runCommand(CommandContext<ServerCommandSource> context) {
        Entity source = context.getSource().getEntity();

        if (source.getWorld() instanceof TardisServerWorld tardisWorld)
            source.sendMessage(Text.translatable("message.ait.id").append(TextUtil.forTardis(tardisWorld.getTardis())));

        return Command.SINGLE_SUCCESS;
    }
}

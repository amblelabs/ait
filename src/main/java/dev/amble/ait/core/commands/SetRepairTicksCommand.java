package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.handler.TardisCrashHandler;

public class SetRepairTicksCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID)
                .then(literal("repair").requires(source -> source.hasPermissionLevel(2))
                        .then(literal("set").then(argument("tardis", TardisArgumentType.tardis()).then(
                                argument("ticks", IntegerArgumentType.integer(0, TardisCrashHandler.MAX_REPAIR_TICKS))
                                        .executes(SetRepairTicksCommand::runCommand))))));
    }

    private static int runCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        if (tardis.crash().getRepairTicks() >= TardisCrashHandler.MAX_REPAIR_TICKS) {
            source.sendMessage(Text.translatableWithFallback("tardis.repair.max", "TARDIS repair ticks are at max!"));
            return 0;
        }

        int repairTicksAmount = IntegerArgumentType.getInteger(context, "ticks");
        tardis.crash().setRepairTicks(repairTicksAmount);

        source.sendMessage(Text.translatableWithFallback("tardis.repair.set", "Set repair ticks for [%s] to: [%s]",
                tardis.getUuid(), tardis.crash().getRepairTicks()));

        return Command.SINGLE_SUCCESS;
    }
}

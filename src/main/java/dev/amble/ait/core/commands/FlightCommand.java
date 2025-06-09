package dev.amble.ait.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FlightCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("flight").requires(source -> source.hasPermissionLevel(2))
                        .then(argument("tardis", TardisArgumentType.tardis())
                                .executes(FlightCommand::execute))));

    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (player == null)
            return 0;

        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        if (!AITMod.CONFIG.rwfEnabled) {
            player.sendMessage(Text.translatable("tardis.message.control.rwf_disabled"), true);
            return Command.SINGLE_SUCCESS;
        }

        if (!player.isCreative()) {
            player.sendMessage(Text.translatable("tardis.message.control.rwf_creative_only"), true);
            return Command.SINGLE_SUCCESS;
        }

        context.getSource().getServer().executeSync(()
                -> tardis.flight().enterFlight(player));

        return Command.SINGLE_SUCCESS;
    }
}

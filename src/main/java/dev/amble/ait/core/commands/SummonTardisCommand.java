package dev.amble.ait.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.handler.travel.TravelUtil;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationPropertyHelper;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SummonTardisCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher
                .register(literal(AITMod.MOD_ID).then(literal("summon").requires(source -> source.hasPermissionLevel(2))
                        .then(argument("tardis", TardisArgumentType.tardis())
                                .executes(SummonTardisCommand::runCommand)
                                .then(argument("pos", BlockPosArgumentType.blockPos())
                                        .executes(SummonTardisCommand::runCommandWithPos))
                                .then(argument("message", BoolArgumentType.bool())
                                        .executes(SummonTardisCommand::runCommandWithPosAndMessage)))));
    }

    private static int runCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return summonTardis(context, null, true);  // Default to showing the message
    }

    private static int runCommandWithPos(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
        return summonTardis(context, pos, true);
    }

    private static int runCommandWithPosAndMessage(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
        boolean showMessage = BoolArgumentType.getBool(context, "showMessage");
        return summonTardis(context, pos, showMessage);
    }

    private static int summonTardis(CommandContext<ServerCommandSource> context, @Nullable BlockPos pos, boolean showMessage) throws CommandSyntaxException {
        Entity source = context.getSource().getEntity();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        if (pos == null)
            pos = source.getBlockPos();

        CachedDirectedGlobalPos globalPos = CachedDirectedGlobalPos.create((ServerWorld) source.getWorld(), pos,
                (byte) RotationPropertyHelper.fromYaw(source.getBodyYaw()));

        TravelUtil.travelTo(tardis, globalPos);

        if (showMessage) {
            source.sendMessage(Text.translatableWithFallback("tardis.summon", "TARDIS [%s] is on the way!",
                    tardis.getUuid().toString().substring(0, 7)));
        }

        return Command.SINGLE_SUCCESS;
    }
}

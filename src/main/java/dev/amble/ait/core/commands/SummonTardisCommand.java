package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import org.jetbrains.annotations.Nullable;

import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationPropertyHelper;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.handler.travel.TravelUtil;

public class SummonTardisCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher
                .register(literal(AITMod.MOD_ID).then(literal("summon").requires(source -> source.hasPermissionLevel(2))
                        .then(argument("tardis", TardisArgumentType.tardis()).executes(SummonTardisCommand::runCommand)
                                .then(argument("pos", BlockPosArgumentType.blockPos())
                                        .executes(SummonTardisCommand::runCommandWithPos)))));
    }

    private static int runCommand(CommandContext<ServerCommandSource> context) {
        return summonTardis(context, null);
    }

    private static int runCommandWithPos(CommandContext<ServerCommandSource> context) {
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
        return summonTardis(context, pos);
    }

    private static int summonTardis(CommandContext<ServerCommandSource> context, @Nullable BlockPos pos) {
        Entity source = context.getSource().getEntity();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        if (pos == null)
            pos = source.getBlockPos();

        CachedDirectedGlobalPos globalPos = CachedDirectedGlobalPos.create((ServerWorld) source.getWorld(), pos,
                (byte) RotationPropertyHelper.fromYaw(source.getBodyYaw()));

        TravelUtil.travelTo(tardis, globalPos);
        source.sendMessage(Text.translatableWithFallback("tardis.summon", "TARDIS [%s] is on the way!",
                tardis.getUuid().toString().substring(0, 7)));

        return Command.SINGLE_SUCCESS;
    }
}

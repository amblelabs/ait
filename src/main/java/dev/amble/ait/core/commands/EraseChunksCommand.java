package dev.amble.ait.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.amble.ait.AITMod;
import dev.drtheo.queue.api.util.block.ChunkEraser;
import net.minecraft.block.Block;
import net.minecraft.command.argument.ColumnPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.ColumnPos;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EraseChunksCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("erase-chunks").requires(source -> source.hasPermissionLevel(2))
                .then(argument("from", ColumnPosArgumentType.columnPos())
                        .then(argument("to", ColumnPosArgumentType.columnPos())
                                .executes(EraseChunksCommand::execute)))));

    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        ColumnPos from = ColumnPosArgumentType.getColumnPos(context, "from");
        ColumnPos to = ColumnPosArgumentType.getColumnPos(context, "to");

        new ChunkEraser.Builder()
                .withFlags(Block.FORCE_STATE | Block.REDRAW_ON_MAIN_THREAD)
                .build(context.getSource().getWorld(), from.toChunkPos(), to.toChunkPos())
                .execute();

        return Command.SINGLE_SUCCESS;
    }
}

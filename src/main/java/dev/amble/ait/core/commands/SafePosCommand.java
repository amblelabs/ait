package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.commands.argument.GroundSearchArgumentType;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.util.WorldUtil;

public class SafePosCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal(AITMod.MOD_ID).then(literal("safe-pos").requires(source -> source.hasPermissionLevel(2))
                        .then(argument("world", DimensionArgumentType.dimension())
                                .then(argument("pos", BlockPosArgumentType.blockPos())
                                        .then(argument("search-type", GroundSearchArgumentType.groundSearch())
                                                .executes(SafePosCommand::execute))))));
    }

    public static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerWorld world = DimensionArgumentType.getDimensionArgument(context, "world");
        BlockPos posA = BlockPosArgumentType.getBlockPos(context, "pos");

        TravelHandlerBase.GroundSearch search = GroundSearchArgumentType.getGroundSearch(context, "search-type");

        CachedDirectedGlobalPos cached = WorldUtil.locateSafe(CachedDirectedGlobalPos.create(world, posA, (byte) 0),
                search, false);
        BlockPos blockPos = cached.getPos();

        Text text = Texts
                .bracketed(Text.translatable("chat.coordinates", blockPos.getX(), blockPos.getY(), blockPos.getZ()))
                .styled((style) -> style.withColor(Formatting.GREEN)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                "/tp @s " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Text.translatable("chat.coordinates.tooltip"))));

        context.getSource().sendMessage(text);
        return Command.SINGLE_SUCCESS;
    }
}

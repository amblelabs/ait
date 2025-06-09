package dev.amble.ait.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.WorldWithTardis;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.util.NetworkUtil;
import dev.amble.ait.core.world.LandingPadManager;
import dev.amble.ait.data.landing.LandingPadRegion;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DebugCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("debug").executes(DebugCommand::execute)
                .then(argument("tardis", TardisArgumentType.tardis()).executes(DebugCommand::executeTardis)
                        .then(argument("player", EntityArgumentType.player()).executes(DebugCommand::executePlayer)))));

    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        if (!source.isExecutedByPlayer())
            return Command.SINGLE_SUCCESS;

        ServerWorld world = source.getWorld();
        PlayerEntity player = source.getPlayer();

        LandingPadRegion region = LandingPadManager.getInstance(world).getRegion(player.getChunkPos());

        if (region != null)
            source.sendMessage(Text.literal("LP in chunk: " + region));

        ((WorldWithTardis) context.getSource().getWorld()).ait$withLookup(lookup -> {
            source.sendMessage(Text.empty());
            source.sendMessage(Text.literal("TARDIS in chunk: " + lookup.get(source.getPlayer().getChunkPos())));
        });

        return Command.SINGLE_SUCCESS;
    }

    private static int executeTardis(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();

        if (!source.isExecutedByPlayer())
            return 0;

        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        context.getSource().getServer().executeSync(() -> tardis.chameleon().recalcDisguise());
        return Command.SINGLE_SUCCESS;
    }

    private static int executePlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        long start = System.nanoTime();
        NetworkUtil.hasLinkedItem(tardis, player);

        context.getSource().sendFeedback(() -> Text.literal("Checked player in "
                + (System.nanoTime() - start) + "ns"), false);

        return Command.SINGLE_SUCCESS;
    }
}

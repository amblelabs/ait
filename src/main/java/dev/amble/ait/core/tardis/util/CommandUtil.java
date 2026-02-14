package dev.amble.ait.core.tardis.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.amble.ait.core.AITDimensions;
import dev.amble.ait.core.world.TardisServerWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class CommandUtil {

    public static final SuggestionProvider<ServerCommandSource> NON_TARDIS_DIM_SUGGESTIONS =
            (CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) -> {
                MinecraftServer server = ctx.getSource().getServer();

                for (RegistryKey<World> key : server.getWorldRegistryKeys()) {
                    ServerWorld world = server.getWorld(key);
                    if (world == null)
                        continue;

                    if (!TardisServerWorld.isTardisDimension(world)
                            && !key.getValue().equals(AITDimensions.TIME_VORTEX_WORLD.getValue())
                            && !key.getValue().toString().equals("ait:tardis_dimension_type")) {
                        builder.suggest(key.getValue().toString());
                    }
                }
                return builder.buildFuture();
            };

    public static final SuggestionProvider<ServerCommandSource> DIRECTION = (ctx, b) -> {
        String[] opts = {
                "north",
                "north_east",
                "east",
                "south_east",
                "south",
                "south_west",
                "west",
                "north_west"
        };
        for (String o : opts) b.suggest(o);
        return b.buildFuture();
    };

    public static boolean hasArgument(CommandContext<ServerCommandSource> context, String name) {
        for (ParsedCommandNode<ServerCommandSource> node : context.getNodes()) {
            if (node.getNode().getName().equals(name))
                return true;
        }

        return false;
    }
}

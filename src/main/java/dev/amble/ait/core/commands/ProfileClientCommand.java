package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.Collection;
import java.util.List;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import dev.amble.ait.AITMod;
import dev.amble.ait.compat.permissionapi.PermissionAPICompat;

/**
 * Starts the vanilla client profiler on a connected client, so a profiling run can be driven from the
 * server console or rcon instead of someone pressing F3+L at the keyboard.
 *
 * <p>The recorder stops itself after {@code DebugRecorder.MAX_DURATION_IN_SECONDS}, so there is no stop
 * form of this command. The client logs the dump path when it finishes.
 */
public class ProfileClientCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID)
                .then(literal("profile-client")
                        .requires(source -> PermissionAPICompat.hasPermission(source, "ait.command.profile-client", 2))
                        .executes(ProfileClientCommand::profileSelf)
                        .then(argument("players", EntityArgumentType.players())
                                .executes(ProfileClientCommand::profileTargets))));
    }

    private static int profileSelf(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return profile(context, List.of(context.getSource().getPlayerOrThrow()));
    }

    private static int profileTargets(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return profile(context, EntityArgumentType.getPlayers(context, "players"));
    }

    private static int profile(CommandContext<ServerCommandSource> context,
            Collection<ServerPlayerEntity> targets) {
        for (ServerPlayerEntity player : targets) {
            ServerPlayNetworking.send(player, AITMod.PROFILE_CLIENT, PacketByteBufs.create());
        }

        int count = targets.size();
        context.getSource().sendFeedback(() -> Text.literal("Started the client profiler on " + count + " client(s)."),
                true);

        return Command.SINGLE_SUCCESS;
    }
}

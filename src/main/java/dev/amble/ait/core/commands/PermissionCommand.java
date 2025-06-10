package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.function.Predicate;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.commands.argument.PermissionArgumentType;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.handler.permissions.Permission;
import dev.amble.ait.core.tardis.handler.permissions.PermissionHandler;

public class PermissionCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("permission")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("tardis", TardisArgumentType.tardis()).then(argument("player",
                        EntityArgumentType.player())
                        .then(argument("permission", PermissionArgumentType.permission())
                                .executes(PermissionCommand::get)
                                .then(argument("value", BoolArgumentType.bool()).executes(PermissionCommand::set)))))));
    }

    private static int set(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        CommonArgs args = CommonArgs.create(context);
        boolean value = BoolArgumentType.getBool(context, "value");

        args.run("ait.command.permission.set", "Set permission '%s' for player %s to '%s'",
                handler -> handler.set(args.player, args.permission, value));

        return Command.SINGLE_SUCCESS;
    }

    private static int get(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        CommonArgs args = CommonArgs.create(context);
        return args.run("ait.command.permission.get", "Permission check '%s' for player %s: '%s'",
                handler -> handler.check(args.player, args.permission)) ? 1 : 0;
    }

    record CommonArgs(ServerCommandSource source, ServerTardis tardis, ServerPlayerEntity player,
            Permission permission) {

        public static CommonArgs create(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
            ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");
            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
            Permission permission = PermissionArgumentType.getPermission(context, "permission");

            return new CommonArgs(context.getSource(), tardis, player, permission);
        }

        public boolean run(String key, String fallback, Predicate<PermissionHandler> func) {
            boolean result = func.test(this.tardis.permissions());

            this.source.sendFeedback(
                    () -> Text.translatableWithFallback(key, fallback, this.permission, this.player.getName(), result),
                    false);

            return result;
        }
    }
}

package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.amble.lib.api.Identifiable;
import dev.amble.lib.register.unlockable.Unlockable;
import dev.amble.lib.register.unlockable.UnlockableRegistry;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.Nameable;
import dev.amble.ait.compat.permissionapi.PermissionAPICompat;
import dev.amble.ait.core.commands.argument.IdentifierWildcardArgumentType;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.data.Wildcard;
import dev.amble.ait.registry.impl.DesktopRegistry;
import dev.amble.ait.registry.impl.console.variant.ConsoleVariantRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;

public class UnlockCommand {

    public static final SuggestionProvider<ServerCommandSource> CONSOLE_SUGGESTION = (context,
            builder) -> IdentifierWildcardArgumentType.suggestWildcardIds(builder,
                    ConsoleVariantRegistry.getInstance());
    public static final SuggestionProvider<ServerCommandSource> DESKTOP_SUGGESTION = (context,
            builder) -> IdentifierWildcardArgumentType.suggestWildcardIds(builder, DesktopRegistry.getInstance());
    public static final SuggestionProvider<ServerCommandSource> EXTERIOR_SUGGESTION = (context,
            builder) -> IdentifierWildcardArgumentType.suggestWildcardIds(builder,
                    ExteriorVariantRegistry.getInstance());

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("unlock")
                .requires(source -> PermissionAPICompat.hasPermission(source, "ait.command.unlock", 2))
                .then(argument("tardis", TardisArgumentType.tardis())
                        .then(literal("console").then(argument("console", IdentifierWildcardArgumentType.wildcard())
                                .suggests(CONSOLE_SUGGESTION).executes(UnlockCommand::unlockConsole)))
                        .then(literal("desktop").then(argument("desktop", IdentifierWildcardArgumentType.wildcard())
                                .suggests(DESKTOP_SUGGESTION).executes(UnlockCommand::unlockDesktop)))
                        .then(literal("exterior").then(argument("exterior", IdentifierWildcardArgumentType.wildcard())
                                .suggests(EXTERIOR_SUGGESTION).executes(UnlockCommand::unlockExterior))))));
    }

    private static <T extends Identifiable & Unlockable & Nameable> int unlock(
            CommandContext<ServerCommandSource> context, String type, Wildcard<T> wildcard,
            UnlockableRegistry<T> registry) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        if (wildcard.isPresent()) {
            T t = wildcard.get();
            source.getServer().execute(() -> tardis.stats().unlock(t));

            source.sendMessage(Text.translatableWithFallback("command.ait.unlock.some", "Granted [%s] %s %s",
                    tardis.getUuid(), t.name(), type));

            return Command.SINGLE_SUCCESS;
        }

        source.getServer().execute(() -> registry.unlockAll(tardis));
        source.sendMessage(Text.translatableWithFallback("command.ait.unlock.all", "Granted [%s] every %s",
                tardis.getUuid(), type));

        return Command.SINGLE_SUCCESS;
    }

    private static int unlockConsole(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return unlock(context, "console", IdentifierWildcardArgumentType.getConsoleVariantArgument(context, "console"),
                ConsoleVariantRegistry.getInstance());
    }

    private static int unlockDesktop(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return unlock(context, "desktop", IdentifierWildcardArgumentType.getDesktopArgument(context, "desktop"),
                DesktopRegistry.getInstance());
    }

    private static int unlockExterior(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return unlock(context, "exterior variant",
                IdentifierWildcardArgumentType.getExteriorVariantArgument(context, "exterior"),
                ExteriorVariantRegistry.getInstance());
    }
}

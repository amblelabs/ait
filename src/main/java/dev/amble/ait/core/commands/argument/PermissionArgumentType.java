package dev.amble.ait.core.commands.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.handler.permissions.Permission;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PermissionArgumentType implements ArgumentType<Permission> {

    public static final SimpleCommandExceptionType INVALID_PERMISSION = new SimpleCommandExceptionType(
            Text.translatable(AITMod.MOD_ID, "argument.permission.invalid"));

    private static final Collection<String> EXAMPLES = List.of("tardis.use.console", "tardis.special.cloak",
            "tardis.modify.exterior");

    public static Permission getPermission(CommandContext<ServerCommandSource> context, String name) {
        return context.getArgument(name, Permission.class);
    }

    public static PermissionArgumentType permission() {
        return new PermissionArgumentType();
    }

    @Override
    public Permission parse(StringReader reader) throws CommandSyntaxException {
        Permission permission = Permission.from(reader.readString());

        if (permission == null)
            throw INVALID_PERMISSION.create();

        return permission;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Permission.collect(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

package dev.amble.ait.core.commands.argument;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.TardisManager;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;

public class TardisArgumentType implements ArgumentType<TardisArgumentType.ServerTardisAccessor> {

    public static final SimpleCommandExceptionType INVALID_UUID = new SimpleCommandExceptionType(
            Text.translatable("argument.uuid.invalid"));

    private static final Collection<String> EXAMPLES = List.of("dd12be42-52a9-4a91-a8a1-11c01849e498");
    private static final Pattern VALID_CHARACTERS = Pattern.compile("^([-A-Fa-f0-9]+)");

    public static ServerTardis getTardis(CommandContext<ServerCommandSource> context, String name) {
        return context.getArgument(name, ServerTardisAccessor.class).get(context);
    }

    public static TardisArgumentType tardis() {
        return new TardisArgumentType();
    }

    @Override
    public ServerTardisAccessor parse(StringReader reader) throws CommandSyntaxException {
        String string = reader.getRemaining();
        Matcher matcher = VALID_CHARACTERS.matcher(string);

        if (!matcher.find())
            throw INVALID_UUID.create();

        String raw = matcher.group(1);

        UUID uuid = UUID.fromString(raw);
        reader.setCursor(reader.getCursor() + raw.length());

        return context -> ServerTardisManager.getInstance().demandTardis(context.getSource().getServer(), uuid);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        boolean isServer = context.getSource() instanceof ServerCommandSource;
        TardisManager<?, ?> manager = TardisManager.getInstance(isServer);

        return CommandSource.suggestMatching(manager.ids().stream().map(UUID::toString), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @FunctionalInterface
    public interface ServerTardisAccessor {
        ServerTardis get(CommandContext<ServerCommandSource> context);
    }
}

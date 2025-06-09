package dev.amble.ait.core.commands.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.TardisManager;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.world.TardisServerWorld;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TardisArgumentType implements ArgumentType<TardisArgumentType.ServerTardisAccessor> {

    public static final SimpleCommandExceptionType INVALID_UUID = new SimpleCommandExceptionType(
            Text.translatable("argument.uuid.invalid"));

    private static final Collection<String> EXAMPLES = List.of("~", "^", "dd12be42-52a9-4a91-a8a1-11c01849e498");
    private static final Pattern VALID_CHARACTERS = Pattern.compile("^([-A-Fa-f0-9]+)");

    public static ServerTardis getTardis(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return context.getArgument(name, ServerTardisAccessor.class).get(context);
    }

    public static TardisArgumentType tardis() {
        return new TardisArgumentType();
    }

    @Override
    public ServerTardisAccessor parse(StringReader reader) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '~') {
            reader.skip();

            return context -> {
                if (!(context.getSource().getWorld() instanceof TardisServerWorld tardisWorld))
                    throw INVALID_UUID.create();

                return tardisWorld.getTardis();
            };
        }

        if (reader.canRead() && reader.peek() == '^') {
            reader.skip();

            return context -> {
                HitResult hit = context.getSource().getEntity().raycast(16, 0, false);

                if (!(hit instanceof BlockHitResult blockHit))
                    throw INVALID_UUID.create();

                BlockEntity blockEntity = context.getSource().getWorld().getBlockEntity(blockHit.getBlockPos());

                if (!(blockEntity instanceof AbstractLinkableBlockEntity linkable))
                    throw INVALID_UUID.create();

                return linkable.tardis().get().asServer();
            };
        }

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

        return CommandSource.suggestMatching(manager.ids().stream().map(UUID::toString),
                builder.suggest("~").suggest("^"));
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @FunctionalInterface
    public interface ServerTardisAccessor {
        ServerTardis get(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;
    }
}

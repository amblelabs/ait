package dev.amble.ait.core.commands.argument;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.amble.lib.api.Identifiable;
import dev.amble.lib.register.datapack.DatapackRegistry;

import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Wildcard;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.DesktopRegistry;
import dev.amble.ait.registry.impl.console.variant.ConsoleVariantRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;

public class IdentifierWildcardArgumentType implements ArgumentType<Wildcard<Identifier>> {

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "*");

    private static final DynamicCommandExceptionType UNKNOWN_DESKTOP_EXCEPTION = new DynamicCommandExceptionType(
            id -> Text.translatable(AITMod.MOD_ID, "desktop.notFound", id));
    private static final DynamicCommandExceptionType UNKNOWN_EXTERIOR_VARIANT_EXCEPTION = new DynamicCommandExceptionType(
            id -> Text.translatable(AITMod.MOD_ID, "desktop.notFound", id));
    private static final DynamicCommandExceptionType UNKNOWN_CONSOLE_VARIANT_EXCEPTION = new DynamicCommandExceptionType(
            id -> Text.translatable(AITMod.MOD_ID, "desktop.notFound", id));

    public static IdentifierWildcardArgumentType wildcard() {
        return new IdentifierWildcardArgumentType();
    }

    private static <T extends Identifiable> Wildcard<T> getRegistryArgument(CommandContext<ServerCommandSource> context,
            String name, DynamicCommandExceptionType type, DatapackRegistry<T> registry) throws CommandSyntaxException {
        Wildcard<Identifier> wildcard = IdentifierWildcardArgumentType.getIdentifier(context, name);

        if (wildcard.isPresent()) {
            Identifier id = wildcard.get();
            T schema = registry.get(id);

            if (schema == null)
                throw type.create(id);

            return Wildcard.of(schema);
        }

        return Wildcard.wildcard();
    }

    public static Wildcard<ConsoleVariantSchema> getConsoleVariantArgument(CommandContext<ServerCommandSource> context,
            String argumentName) throws CommandSyntaxException {
        return IdentifierWildcardArgumentType.getRegistryArgument(context, argumentName,
                UNKNOWN_CONSOLE_VARIANT_EXCEPTION, ConsoleVariantRegistry.getInstance());
    }

    public static Wildcard<TardisDesktopSchema> getDesktopArgument(CommandContext<ServerCommandSource> context,
            String argumentName) throws CommandSyntaxException {
        return IdentifierWildcardArgumentType.getRegistryArgument(context, argumentName, UNKNOWN_DESKTOP_EXCEPTION,
                DesktopRegistry.getInstance());
    }

    public static Wildcard<ExteriorVariantSchema> getExteriorVariantArgument(
            CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
        return IdentifierWildcardArgumentType.getRegistryArgument(context, argumentName,
                UNKNOWN_EXTERIOR_VARIANT_EXCEPTION, ExteriorVariantRegistry.getInstance());
    }

    public static Wildcard<Identifier> getIdentifier(CommandContext<ServerCommandSource> context, String name) {
        return context.getArgument(name, Wildcard.class);
    }

    public static <T extends Identifiable> CompletableFuture<Suggestions> suggestWildcardIds(SuggestionsBuilder builder,
            DatapackRegistry<T> registry) {
        return CommandSource.suggestMatching(registry.toList().stream().map(schema -> schema.id().toString()),
                builder.suggest("*"));
    }

    @Override
    public Wildcard<Identifier> parse(StringReader reader) throws CommandSyntaxException {
        if (reader.peek() == '*') {
            reader.skip();

            return Wildcard.wildcard();
        }

        return Wildcard.of(Identifier.fromCommandInput(reader));
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

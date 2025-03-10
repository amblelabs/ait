package dev.amble.ait.core.commands.argument;

import java.util.Arrays;
import java.util.Collection;

import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.amble.ait.core.commands.argument.json.StringJsonReader;

public class JsonElementArgumentType implements ArgumentType<JsonElement> {

    private static final Collection<String> EXAMPLES = Arrays.asList("0", "0b", "0l", "0.0", "\"foo\"", "{foo=bar}",
            "[0]");

    private JsonElementArgumentType() {
    }

    public static JsonElementArgumentType jsonElement() {
        return new JsonElementArgumentType();
    }

    public static <S> JsonElement getJsonElement(CommandContext<S> context, String name) {
        return context.getArgument(name, JsonElement.class);
    }

    @Override
    public JsonElement parse(StringReader stringReader) throws CommandSyntaxException {
        return new StringJsonReader(stringReader).parseElement();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

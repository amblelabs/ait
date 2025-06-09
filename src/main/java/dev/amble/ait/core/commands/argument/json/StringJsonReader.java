package dev.amble.ait.core.commands.argument.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.Text;

import java.util.regex.Pattern;

public class StringJsonReader {
    public static final SimpleCommandExceptionType TRAILING = new SimpleCommandExceptionType(
            Text.translatable("argument.nbt.trailing"));
    public static final SimpleCommandExceptionType EXPECTED_KEY = new SimpleCommandExceptionType(
            Text.translatable("argument.nbt.expected.key"));
    public static final SimpleCommandExceptionType EXPECTED_VALUE = new SimpleCommandExceptionType(
            Text.translatable("argument.nbt.expected.value"));
    public static final char COMMA = ',';
    public static final char COLON = ':';
    public static final char SEMICOLON = ';';
    private static final char SQUARE_OPEN_BRACKET = '[';
    private static final char SQUARE_CLOSE_BRACKET = ']';
    private static final char RIGHT_CURLY_BRACKET = '}';
    private static final char LEFT_CURLY_BRACKET = '{';
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?",
            2);
    private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
    private final StringReader reader;

    /**
     * {@return the NBT compound parsed from the {@code string}}
     *
     * @throws CommandSyntaxException
     *             if the reader detects a syntax error (including
     *             {@linkplain #TRAILING trailing strings})
     */
    public static JsonObject parse(String string) throws CommandSyntaxException {
        return new StringJsonReader(new StringReader(string)).readCompound();
    }

    JsonObject readCompound() throws CommandSyntaxException {
        JsonObject nbtCompound = this.parseObject();
        this.reader.skipWhitespace();

        if (this.reader.canRead())
            throw TRAILING.createWithContext(this.reader);

        return nbtCompound;
    }

    public StringJsonReader(StringReader reader) {
        this.reader = reader;
    }

    protected String readString() throws CommandSyntaxException {
        this.reader.skipWhitespace();

        if (!this.reader.canRead())
            throw EXPECTED_KEY.createWithContext(this.reader);

        return this.reader.readString();
    }

    protected JsonPrimitive parseElementPrimitive() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        int i = this.reader.getCursor();

        if (StringReader.isQuotedStringStart(this.reader.peek()))
            return new JsonPrimitive(this.reader.readQuotedString());

        String string = this.reader.readUnquotedString();

        if (string.isEmpty()) {
            this.reader.setCursor(i);
            throw EXPECTED_VALUE.createWithContext(this.reader);
        }

        return this.parsePrimitive(string);
    }

    private JsonPrimitive parsePrimitive(String input) {
        try {
            if (FLOAT_PATTERN.matcher(input).matches())
                return new JsonPrimitive(Float.parseFloat(input.substring(0, input.length() - 1)));

            if (INT_PATTERN.matcher(input).matches())
                return new JsonPrimitive(Integer.parseInt(input));

            if ("true".equalsIgnoreCase(input))
                return new JsonPrimitive(true);

            if ("false".equalsIgnoreCase(input))
                return new JsonPrimitive(false);
        } catch (NumberFormatException numberFormatException) {
            // empty catch block
        }

        return new JsonPrimitive(input);
    }

    /**
     * {@return the parsed NBT element}
     *
     * @throws CommandSyntaxException
     *             if the reader detects a syntax error
     */
    public JsonElement parseElement() throws CommandSyntaxException {
        this.reader.skipWhitespace();

        if (!this.reader.canRead()) {
            throw EXPECTED_VALUE.createWithContext(this.reader);
        }

        char c = this.reader.peek();
        if (c == LEFT_CURLY_BRACKET)
            return this.parseObject();

        if (c == SQUARE_OPEN_BRACKET)
            return this.parseArray();

        return this.parseElementPrimitive();
    }

    protected JsonArray parseArray() throws CommandSyntaxException {
        if (this.reader.canRead(3) && !StringReader.isQuotedStringStart(this.reader.peek(1))
                && this.reader.peek(2) == SEMICOLON)
            return this.parseElementPrimitiveArray();

        return this.parseList();
    }

    /**
     * {@return the parsed NBT compound}
     *
     * @throws CommandSyntaxException
     *             if the reader detects a syntax error
     */
    public JsonObject parseObject() throws CommandSyntaxException {
        this.expect(LEFT_CURLY_BRACKET);
        JsonObject jsonObject = new JsonObject();

        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != RIGHT_CURLY_BRACKET) {
            int i = this.reader.getCursor();
            String string = this.readString();

            if (string.isEmpty()) {
                this.reader.setCursor(i);
                throw EXPECTED_KEY.createWithContext(this.reader);
            }

            this.expect(COLON);
            jsonObject.add(string, this.parseElement());

            if (!this.readComma())
                break;

            if (this.reader.canRead())
                continue;

            throw EXPECTED_KEY.createWithContext(this.reader);
        }

        this.expect(RIGHT_CURLY_BRACKET);
        return jsonObject;
    }

    private JsonArray parseList() throws CommandSyntaxException {
        this.expect(SQUARE_OPEN_BRACKET);
        this.reader.skipWhitespace();

        if (!this.reader.canRead())
            throw EXPECTED_VALUE.createWithContext(this.reader);

        JsonArray nbtList = new JsonArray();

        while (this.reader.peek() != SQUARE_CLOSE_BRACKET) {
            nbtList.add(this.parseElement());

            if (!this.readComma())
                break;

            if (this.reader.canRead())
                continue;

            throw EXPECTED_VALUE.createWithContext(this.reader);
        }

        this.expect(SQUARE_CLOSE_BRACKET);
        return nbtList;
    }

    private JsonArray parseElementPrimitiveArray() throws CommandSyntaxException {
        this.expect(SQUARE_OPEN_BRACKET);

        this.reader.read();
        this.reader.skipWhitespace();

        if (!this.reader.canRead())
            throw EXPECTED_VALUE.createWithContext(this.reader);

        return this.readNumberArray();
    }

    private JsonArray readNumberArray() throws CommandSyntaxException {
        JsonArray list = new JsonArray();

        while (this.reader.peek() != SQUARE_CLOSE_BRACKET) {
            JsonElement jsonElement = this.parseElement();

            if (jsonElement instanceof JsonPrimitive primitive)
                list.add(primitive.getAsNumber());

            if (!this.readComma())
                break;

            if (this.reader.canRead())
                continue;

            throw EXPECTED_VALUE.createWithContext(this.reader);
        }

        this.expect(SQUARE_CLOSE_BRACKET);
        return list;
    }

    private boolean readComma() {
        this.reader.skipWhitespace();

        if (this.reader.canRead() && this.reader.peek() == COMMA) {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        }

        return false;
    }

    private void expect(char c) throws CommandSyntaxException {
        this.reader.skipWhitespace();
        this.reader.expect(c);
    }
}

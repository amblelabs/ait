package dev.drtheo.autojson.adapter.string;

import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import dev.drtheo.autojson.ast.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Deque;

public class JsonParser {

    public static JsonElement parse(String str) throws IOException {
        return parse(new StringReader(str));
    }

    public static JsonElement parse(StringReader reader) throws IOException {
        return parse(new JsonReader(reader));
    }

    public static JsonElement parse(JsonReader in) {
        boolean isEmpty = true;
        try {
            in.peek();
            isEmpty = false;

            JsonElement current;
            JsonToken peeked = in.peek();

            current = tryBeginNesting(in, peeked);
            if (current == null) {
                return readTerminal(in, peeked);
            }

            Deque<JsonElement> stack = new ArrayDeque<>();

            while (true) {
                while (in.hasNext()) {
                    String name = null;
                    // Name is only used for JSON object members
                    if (current instanceof JsonObject) {
                        name = in.nextName();
                    }

                    peeked = in.peek();
                    JsonElement value = tryBeginNesting(in, peeked);
                    boolean isNesting = value != null;

                    if (value == null) {
                        value = readTerminal(in, peeked);
                    }

                    if (current instanceof JsonArray) {
                        ((JsonArray) current).add(value);
                    } else {
                        ((JsonObject) current).put(name, value);
                    }

                    if (isNesting) {
                        stack.addLast(current);
                        current = value;
                    }
                }

                // End current element
                if (current instanceof JsonArray) {
                    in.endArray();
                } else {
                    in.endObject();
                }

                if (stack.isEmpty()) {
                    return current;
                } else {
                    // Continue with enclosing element
                    current = stack.removeLast();
                }
            }
        } catch (EOFException e) {
            if (isEmpty)
                return JsonNull.INSTANCE;

            throw new JsonSyntaxException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static JsonElement readTerminal(JsonReader in, JsonToken peeked) throws IOException {
        return switch (peeked) {
            case STRING -> new JsonPrimitive(in.nextString());
            case NUMBER -> {
                String number = in.nextString();
                yield new JsonPrimitive.Lazy(new LazilyParsedNumber(number));
            }
            case BOOLEAN -> new JsonPrimitive(in.nextBoolean());
            case NULL -> {
                in.nextNull();
                yield JsonNull.INSTANCE;
            }
            default -> throw new IllegalStateException("Unexpected token: " + peeked);
        };
    }

    private static JsonElement tryBeginNesting(JsonReader in, JsonToken peeked) throws IOException {
        return switch (peeked) {
            case BEGIN_ARRAY -> {
                in.beginArray();
                yield new JsonArray();
            }
            case BEGIN_OBJECT -> {
                in.beginObject();
                yield JsonObject.create();
            }
            default -> null;
        };
    }
}

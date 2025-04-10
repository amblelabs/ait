package dev.drtheo.autojson.ast;

import java.util.ArrayList;
import java.util.List;

public class JsonArray implements JsonElement {

    private final List<JsonElement> elements = new ArrayList<>();

    public void add(JsonElement element) {
        this.elements.add(element);
    }
}

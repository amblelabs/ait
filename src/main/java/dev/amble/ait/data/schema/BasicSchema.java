package dev.amble.ait.data.schema;

import dev.amble.ait.api.Nameable;
import dev.amble.lib.api.Identifiable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class BasicSchema implements Identifiable, Nameable {

    private final String prefix;
    private Text text;

    protected BasicSchema(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Text text() {
        if (this.text == null) {
            Identifier id = this.id();

            // turn stuff like ait:exterior/police_box into ait:police_box
            String[] parts = id.getPath().split("/");
            String last = parts[parts.length - 1];

            this.text = Text.translatable(this.prefix + "." + id.getNamespace() + "." + last);
        }

        return text;
    }

    @Override
    public String name() {
        return this.text().getString();
    }
}

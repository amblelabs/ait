package dev.amble.ait.api;

import net.minecraft.text.Text;

// TODO: change the String to Text
// TODO: make it so if the object is Nameable AND Identifiable, use Identifier#toTranslationKey
public interface Nameable {
    String name();

    default Text text() {
        return Text.literal(this.name());
    }
}

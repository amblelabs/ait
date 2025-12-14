package dev.amble.ait.api;

import dev.amble.lib.api.Identifiable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.NotImplementedException;

// TODO: change the String to Text
// TODO: make it so if the object is Nameable AND Identifiable, use Identifier#toTranslationKey
public interface Nameable {
	default String toTranslationKey() {
		if (this instanceof Identifiable id) {
			Identifier identifier = id.id();
			if (prefix() == null) return identifier.toTranslationKey();
			if (suffix() != null) return identifier.toTranslationKey(prefix(), suffix());
			else return identifier.toTranslationKey(prefix());
		}

		throw new NotImplementedException("toTranslationKey not implemented for " + this.getClass().getName());
	}

	// todo - move into identifiable but i cba opening up amblekit
	default String prefix() {
		return null;
	}

	// todo - move into identifiable but i cba opening up amblekit
	default String suffix() {
		return null;
	}

    default Text text() {
	    return Text.translatable(this.toTranslationKey());
    }
}

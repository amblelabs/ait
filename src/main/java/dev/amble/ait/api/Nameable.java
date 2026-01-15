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
			if (idPrefix() == null) return identifier.toTranslationKey();
			if (idSuffix() != null) return identifier.toTranslationKey(idPrefix(), idSuffix());
			else return identifier.toTranslationKey(idPrefix());
		}

		throw new NotImplementedException("toTranslationKey not implemented for " + this.getClass().getName());
	}

	// todo - move into identifiable but i cba opening up amblekit
	default String idPrefix() {
		return null;
	}

	// todo - move into identifiable but i cba opening up amblekit
	default String idSuffix() {
		return null;
	}

	default Text nameText() {
	    return Text.translatable(this.toTranslationKey());
    }
}

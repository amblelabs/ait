package dev.amble.ait.data.schema.door.impl.exclusive;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DoorSchema;
import net.minecraft.util.Identifier;

public class PersonaDoorVariant extends DoorSchema {
	public static final Identifier REFERENCE = AITMod.id("door/persona");

	public PersonaDoorVariant() {
		super(REFERENCE);
	}

	@Override
	public boolean isDouble() {
		return false;
	}
}

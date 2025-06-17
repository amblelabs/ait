package dev.amble.ait.data.schema.door.impl.exclusive.client;

import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.models.doors.exclusive.PersonaDoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;
import dev.amble.ait.data.schema.door.impl.exclusive.BlueBoxDoorVariant;
import dev.amble.ait.data.schema.door.impl.exclusive.PersonaDoorVariant;
import net.minecraft.util.Identifier;

public class ClientPersonaDoorVariant extends ClientDoorSchema {
	public ClientPersonaDoorVariant() {
		super(PersonaDoorVariant.REFERENCE);
	}


	@Override
	public DoorModel model() {
		return new PersonaDoorModel();
	}
}

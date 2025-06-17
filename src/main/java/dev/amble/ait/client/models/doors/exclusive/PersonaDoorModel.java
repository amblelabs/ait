package dev.amble.ait.client.models.doors.exclusive;

import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.data.schema.exterior.variant.exclusive.persona.client.PersonaExteriorModel;
import net.minecraft.client.model.ModelPart;

public class PersonaDoorModel extends DoorModel {
	private final ModelPart root;

	public PersonaDoorModel(ModelPart root) {
		this.root = root;
	}

	public PersonaDoorModel() {
		this(PersonaExteriorModel.getTexturedModelData().createModel());
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}
}

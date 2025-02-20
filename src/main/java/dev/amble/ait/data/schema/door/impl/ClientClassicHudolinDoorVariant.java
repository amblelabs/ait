package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.ClassicHudolinDoorModel;
import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientClassicHudolinDoorVariant extends ClientDoorSchema {
    public ClientClassicHudolinDoorVariant() {
        super(ClassicHudolinDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new ClassicHudolinDoorModel(ClassicHudolinDoorModel.getTexturedModelData().createModel());
    }
}

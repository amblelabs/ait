package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.models.doors.TardimDoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientTardimDoorVariant extends ClientDoorSchema {
    public ClientTardimDoorVariant() {
        super(TardimDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new TardimDoorModel(TardimDoorModel.getTexturedModelData().createModel());
    }
}

package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.models.doors.GeometricDoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientGeometricDoorVariant extends ClientDoorSchema {
    public ClientGeometricDoorVariant() {
        super(GeometricDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new GeometricDoorModel(GeometricDoorModel.getTexturedModelData().createModel());
    }
}

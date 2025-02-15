package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.models.doors.EasterHeadDoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientEasterHeadDoorVariant extends ClientDoorSchema {
    public ClientEasterHeadDoorVariant() {
        super(EasterHeadDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new EasterHeadDoorModel(EasterHeadDoorModel.getTexturedModelData().createModel());
    }
}

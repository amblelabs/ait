package dev.amble.ait.data.schema.door.impl.exclusive;

import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.models.doors.exclusive.BlueBoxDoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientBlueBoxDoorVariant extends ClientDoorSchema {

    public ClientBlueBoxDoorVariant() {
        super(BlueBoxDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new BlueBoxDoorModel(BlueBoxDoorModel.getTexturedModelData().createModel());
    }
}

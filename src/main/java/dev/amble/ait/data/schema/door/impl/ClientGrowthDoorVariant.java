package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.coral.CoralGrowthDoorModel;
import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientGrowthDoorVariant extends ClientDoorSchema {
    public ClientGrowthDoorVariant() {
        super(CoralGrowthDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new CoralGrowthDoorModel(CoralGrowthDoorModel.getTexturedModelData().createModel());
    }
}

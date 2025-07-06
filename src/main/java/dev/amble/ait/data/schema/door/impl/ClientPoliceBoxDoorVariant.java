package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.models.doors.PoliceBoxDoorModel;
import dev.amble.ait.client.models.doors.pixelconsistant.ConsistantPoliceBoxDoorModel;
import dev.amble.ait.client.models.exteriors.PoliceBoxModel;
import dev.amble.ait.client.models.exteriors.pixelconsistant.ConsistantPoliceBoxModel;
import dev.amble.ait.client.util.ResourcePackUtil;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientPoliceBoxDoorVariant extends ClientDoorSchema {
    public ClientPoliceBoxDoorVariant() {
        super(PoliceBoxDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        if (ResourcePackUtil.isPixelConsistentPackActive()) {
            return new ConsistantPoliceBoxDoorModel(ConsistantPoliceBoxDoorModel.getTexturedModelData().createModel());
        } else {
            return new PoliceBoxDoorModel(PoliceBoxDoorModel.getTexturedModelData().createModel());
        }
    }
}

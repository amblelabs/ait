package dev.amble.ait.data.schema.door.impl;

import dev.amble.ait.client.models.doors.BookshelfDoorModel;
import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.data.schema.door.ClientDoorSchema;

public class ClientBookshelfDoorVariant extends ClientDoorSchema {
    public ClientBookshelfDoorVariant() {
        super(BookshelfDoorVariant.REFERENCE);
    }

    @Override
    public DoorModel model() {
        return new BookshelfDoorModel(BookshelfDoorModel.getTexturedModelData().createModel());
    }
}

package dev.amble.ait.client.models.consoles;

import dev.amble.ait.client.models.AnimatedModel;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

public interface ConsoleModel extends AnimatedModel {
	void animateBlockEntity(ConsoleBlockEntity console, TravelHandlerBase.State state, boolean hasPower);
}

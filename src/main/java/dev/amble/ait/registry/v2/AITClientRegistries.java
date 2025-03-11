package dev.amble.ait.registry.v2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AITClientRegistries {

    public static final ClientExteriorModelRegistry EXTERIOR_MODEL = new ClientExteriorModelRegistry();
    public static DoorRegistry DOOR = new DoorRegistry();

    public static void init() {

    }
}

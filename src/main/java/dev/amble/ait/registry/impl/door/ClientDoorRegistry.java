package dev.amble.ait.registry.impl.door;


import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.ClientDoorSchema;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.*;

public class ClientDoorRegistry {
    public static final SimpleRegistry<ClientDoorSchema> REGISTRY = FabricRegistryBuilder
            .createSimple(RegistryKey.<ClientDoorSchema>ofRegistry(AITMod.id("client_door")))
            .buildAndRegister();

    public static ClientDoorSchema register(ClientDoorSchema schema) {
        return Registry.register(REGISTRY, schema.id(), schema);
    }

    /**
     * Will return the clients version of a servers door variant
     *
     * @param parent
     * @return the first variant found as there should only be one client version
     */
    public static ClientDoorSchema withParent(DoorSchema parent) {
        for (ClientDoorSchema schema : REGISTRY) {
            if (schema.parent().equals(parent))
                return schema;
        }

        return null;
    }

    public static ClientDoorSchema TARDIM;
    public static ClientDoorSchema CLASSIC;
    public static ClientDoorSchema CLASSIC_HUDOLIN;
    public static ClientDoorSchema BOOTH;
    public static ClientDoorSchema CAPSULE;
    public static ClientDoorSchema BOX;
    public static ClientDoorSchema BOX_CORAL;
    public static ClientDoorSchema BOX_RENAISSANCE;
    public static ClientDoorSchema HEAD;
    public static ClientDoorSchema GROWTH;
    public static ClientDoorSchema DOOM;
    public static ClientDoorSchema PLINTH;
    public static ClientDoorSchema RENEGADE;
    public static ClientDoorSchema BOOKSHELF;
    public static ClientDoorSchema GEOMETRIC;
    public static ClientDoorSchema STALLION;
    public static ClientDoorSchema ADAPTIVE;
    public static ClientDoorSchema DALEK_MOD;
    //public static ClientDoorSchema JAKE;
    public static ClientDoorSchema PRESENT;
    public static ClientDoorSchema PIPE;

    public static void init() {
        TARDIM = register(new ClientTardimDoorVariant());
        CLASSIC = register(new ClientClassicDoorVariant());
        CLASSIC_HUDOLIN = register(new ClientClassicHudolinDoorVariant());
        BOOTH = register(new ClientBoothDoorVariant());
        CAPSULE = register(new ClientCapsuleDoorVariant());
        BOX = register(new ClientPoliceBoxDoorVariant());
        BOX_CORAL = register(new ClientPoliceBoxCoralDoorVariant());
        BOX_RENAISSANCE = register(new ClientPoliceBoxRenaissanceDoorVariant());
        HEAD = register(new ClientEasterHeadDoorVariant());
        GROWTH = register(new ClientGrowthDoorVariant());
        DOOM = register(new ClientDoomDoorVariant());
        PLINTH = register(new ClientPlinthDoorVariant());
        RENEGADE = register(new ClientRenegadeDoorVariant());
        BOOKSHELF = register(new ClientBookshelfDoorVariant());
        GEOMETRIC = register(new ClientGeometricDoorVariant());
        STALLION = register(new ClientStallionDoorVariant());
        ADAPTIVE = register(new ClientAdaptiveDoorVariant());
        DALEK_MOD = register(new ClientDalekModDoorVariant());
        //JAKE = register(new ClientJakeDoorVariant());
        PRESENT = register(new ClientPresentDoorVariant());
        PIPE = register(new ClientPipeDoorVariant());
    }
}

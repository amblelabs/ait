package dev.amble.ait.registry.impl.door;


import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.*;
import dev.amble.ait.data.schema.door.impl.exclusive.BlueBoxDoorVariant;
import dev.amble.ait.data.schema.door.impl.exclusive.DoomDoorVariant;

public class DoorRegistry {
    public static final SimpleRegistry<DoorSchema> REGISTRY = FabricRegistryBuilder
            .createSimple(RegistryKey.<DoorSchema>ofRegistry(AITMod.id("door"))).buildAndRegister();

    public static DoorSchema register(DoorSchema schema) {
        return Registry.register(REGISTRY, schema.id(), schema);
    }

    public static DoorSchema TARDIM;
    public static DoorSchema CLASSIC;
    public static DoorSchema CLASSIC_HUDOLIN;
    public static DoorSchema BOOTH;
    public static DoorSchema CAPSULE;
    public static DoorSchema BOX;
    public static DoorSchema BOX_CORAL;
    public static DoorSchema BOX_RENAISSANCE;
    public static DoorSchema HEAD;
    public static DoorSchema GROWTH;
    public static DoorSchema PLINTH;
    public static DoorSchema RENEGADE;
    public static DoorSchema BOOKSHELF;
    public static DoorSchema GEOMETRIC;
    public static DoorSchema STALLION;
    public static DoorSchema ADAPTIVE;
    public static DoorSchema DALEK_MOD;
    public static DoorSchema PRESENT;
    public static DoorSchema PIPE;

    public static DoorSchema DOOM;
    public static DoorSchema BLUE_BOX;

    public static void init() {
        TARDIM = register(new TardimDoorVariant());
        CLASSIC = register(new ClassicDoorVariant());
        CLASSIC_HUDOLIN = register(new ClassicHudolinDoorVariant());
        BOOTH = register(new BoothDoorVariant());
        CAPSULE = register(new CapsuleDoorVariant());
        BOX = register(new PoliceBoxDoorVariant());
        BOX_CORAL = register(new PoliceBoxCoralDoorVariant());
        BOX_RENAISSANCE = register(new PoliceBoxRenaissanceDoorVariant());
        HEAD = register(new EasterHeadDoorVariant());
        GROWTH = register(new CoralGrowthDoorVariant());
        PLINTH = register(new PlinthDoorVariant());
        RENEGADE = register(new RenegadeDoorVariant());
        BOOKSHELF = register(new BookshelfDoorVariant());
        GEOMETRIC = register(new GeometricDoorVariant());
        STALLION = register(new StallionDoorVariant());
        ADAPTIVE = register(new AdaptiveDoorVariant());
        DALEK_MOD = register(new DalekModDoorVariant());
        PRESENT = register(new PresentDoorVariant());
        PIPE = register(new PipeDoorVariant());

        // Exclusives
        DOOM = register(new DoomDoorVariant());
        BLUE_BOX = register(new BlueBoxDoorVariant());
    }
}

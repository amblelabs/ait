package dev.amble.ait.registry.impl.door;


import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DatapackDoor;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.*;
import dev.amble.ait.data.schema.door.impl.exclusive.BlueBoxDoorVariant;
import dev.amble.ait.data.schema.door.impl.exclusive.DoomDoorVariant;
import dev.amble.lib.register.datapack.SimpleDatapackRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class DoorRegistry extends SimpleDatapackRegistry<DoorSchema> {
    private static DoorRegistry INSTANCE;

    private DoorRegistry() {
        super(DatapackDoor::fromInputStream, null, "door", true, AITMod.MOD_ID);
    }

    public static DoorRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DoorRegistry();
        }
        return INSTANCE;
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

    @Override
    public DoorSchema fallback() {
        return CAPSULE;
    }

    @Override
    public void syncToClient(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        PacketByteBuf secondary = PacketByteBufs.create();

        int counter = 0;
        for (DoorSchema schema : this.toList()) {
            if (!(schema instanceof DatapackDoor type)) continue;

            counter++;
            secondary.encodeAsJson(DatapackDoor.CODEC, type);
        }

        buf.writeInt(counter);
        buf.writeBytes(secondary);

        ServerPlayNetworking.send(player, this.packet, buf);
    }

    @Override
    public void readFromServer(PacketByteBuf buf) {
        PacketByteBuf copy = PacketByteBufs.copy(buf);

        for (DoorSchema schema : this.toList()) {
            if (!(schema instanceof DatapackDoor type)) continue;

            this.REGISTRY.remove(type.id());
        }

        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            DatapackDoor type = buf.decodeAsJson(DatapackDoor.CODEC);
            this.register(type);
        }

        ClientDoorRegistry.getInstance().readFromServer(copy);
    }

    @Override
    protected void defaults() {
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

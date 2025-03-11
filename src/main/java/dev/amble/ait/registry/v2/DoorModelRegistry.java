package dev.amble.ait.registry.v2;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.TardimDoorVariant;
import dev.amble.lib.registry.SimpleAmbleRegistry;
import dev.amble.lib.registry.SimpleRegistryElementCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class DoorModelRegistry extends SimpleAmbleRegistry<DoorModel> {

    public static final Identifier FALLBACK = AITMod.id("tardim");

    private final SimpleRegistryElementCodec<DoorSchema> entry = SimpleRegistryElementCodec.of(this);

    public DoorModelRegistry() {
        super(AITMod.id("door"));

        Registry.register(this.get(), AITMod.id("tardim"), new TardimDoorVariant());
    }

    public SimpleRegistryElementCodec<DoorSchema> entry() {
        return entry;
    }
}

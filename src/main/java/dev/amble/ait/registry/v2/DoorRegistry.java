package dev.amble.ait.registry.v2;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.TardimDoorVariant;
import dev.amble.lib.registry.SimpleAmbleRegistry;
import dev.amble.lib.registry.SimpleRegistryElementCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public class DoorRegistry extends SimpleAmbleRegistry<DoorSchema> {

    public static final Identifier FALLBACK = AITMod.id("tardim");

    private final SimpleRegistryElementCodec<DoorSchema> entry = SimpleRegistryElementCodec.of(this);

    public DoorRegistry() {
        super(AITMod.id("door"));

        Registry.register(this.get(), AITMod.id("tardim"), new TardimDoorVariant());
    }

    public SimpleRegistryElementCodec<DoorSchema> entry() {
        return entry;
    }
}

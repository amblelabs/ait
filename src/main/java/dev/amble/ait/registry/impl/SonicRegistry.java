package dev.amble.ait.registry.impl;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.datapack.DatapackSonic;
import dev.amble.ait.data.schema.sonic.BuiltinSonic;
import dev.amble.ait.data.schema.sonic.SonicSchema;
import dev.amble.lib.register.unlockable.UnlockableRegistry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static dev.amble.ait.AITMod.LOGGER;

public class SonicRegistry extends UnlockableRegistry<SonicSchema> {

    private static SonicRegistry INSTANCE;

    protected SonicRegistry() {
        super(DatapackSonic::fromInputStream, DatapackSonic.CODEC, "sonic", true);
    }

    @Override
    public SonicSchema fallback() {
        return SonicRegistry.DEFAULT;
    }

    @Override
    protected void defaults() {
        DEFAULT = register(BuiltinSonic.create("prime"));

        register(BuiltinSonic.create("copper"));
        register(BuiltinSonic.create("mechanical"));
        register(BuiltinSonic.create("fob"));
        register(BuiltinSonic.create("coral"));
        register(BuiltinSonic.create("renaissance"));
        register(BuiltinSonic.create("crystalline"));
        register(BuiltinSonic.create("song"));
        register(BuiltinSonic.create("singularity"));
        register(BuiltinSonic.create("candy_cane"));
        register(BuiltinSonic.create("type_100"));
    }

    public static SonicRegistry getInstance() {
        if (INSTANCE == null) {
            AITMod.LOGGER.debug("SonicRegistry was not initialized, creating a new instance");
            INSTANCE = new SonicRegistry();
        }

        return INSTANCE;
    }

    public static SonicSchema DEFAULT;

    public void populateModels(Consumer<Identifier> consumer) {
        for (SonicSchema schema : REGISTRY.values()) {
            SonicSchema.Models models = schema.models();
            models.load(consumer);

            LOGGER.debug("Loading sonic '{}' with models: {}", schema.id(), models);
        }
    }

    public Collection<Identifier> models() {
        List<Identifier> result = new ArrayList<>();

        for (SonicSchema schema : REGISTRY.values()) {
            SonicSchema.Models models = schema.models();
            models.load(result::add);

            LOGGER.debug("Loading sonic '{}' with models: {}", schema.id(), models);
        }

        return result;
    }
}

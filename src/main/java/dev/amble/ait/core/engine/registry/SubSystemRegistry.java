package dev.amble.ait.core.engine.registry;

import com.google.gson.*;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.lib.register.Registry;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SubSystemRegistry implements Registry {

    private static final SubSystemRegistry instance = new SubSystemRegistry();

    private final Map<String, SubSystem.IdLike> REGISTRY = new HashMap<>();
    private SubSystem.IdLike[] LOOKUP;

    private boolean frozen = false;

    public void register(SubSystem.IdLike id) {
        if (!id.creatable())
            return;

        id.index(REGISTRY.size());
        REGISTRY.put(id.name(), id);

        if (frozen)
            AITMod.LOGGER.error("Tried to init a component id after the registry got frozen: {}", id);
    }

    public void register(SubSystem.IdLike[] idLikes) {
        for (SubSystem.IdLike idLike : idLikes) {
            register(idLike);
        }
    }

    @Override
    public void onCommonInit() {
        register(SubSystem.Id.ids());

        LOOKUP = new SubSystem.IdLike[REGISTRY.size()];
        REGISTRY.forEach((name, idLike) -> LOOKUP[idLike.index()] = idLike);

        this.frozen = true;
    }

    public void fill(Consumer<SubSystem> consumer) {
        for (SubSystem.IdLike id : LOOKUP) {
            consumer.accept(id.create());
        }
    }

    public SubSystem.IdLike get(String name) {
        return REGISTRY.get(name);
    }

    public String get(SubSystem component) {
        return component.getId().name();
    }

    public SubSystem.IdLike get(int index) {
        return LOOKUP[index];
    }

    public static SubSystem.IdLike[] values() {
        return instance.LOOKUP;
    }

    public Collection<SubSystem.IdLike> getValues() {
        return REGISTRY.values();
    }

    public static SubSystemRegistry getInstance() {
        return instance;
    }

    public static Object idSerializer() {
        return new Serializer();
    }

    private static class Serializer
            implements
            JsonSerializer<SubSystem.IdLike>,
            JsonDeserializer<SubSystem.IdLike> {

        @Override
        public SubSystem.IdLike deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return SubSystemRegistry.getInstance().get(json.getAsString());
        }

        @Override
        public JsonElement serialize(SubSystem.IdLike src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.name());
        }
    }
}

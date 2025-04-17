package dev.amble.ait.core.tardis.manager.autojson;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.core.tardis.TardisHandlersManager;
import dev.amble.ait.registry.impl.TardisComponentRegistry;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.ObjectSchema;

public class HandlersManagerSchema implements ObjectSchema<TardisHandlersManager> {

    @Override
    public <To> void serialize(JsonAdapter<Object, To> adapter, JsonSerializationContext.Obj obj, TardisHandlersManager manager) {
        manager.forEach(component -> {
            TardisComponent.IdLike idLike = component.getId();

            if (idLike == null) {
                AITMod.LOGGER.error("Id was null for {}", component.getClass());
                return;
            }

            obj.obj$put(idLike.name(), component);
        });
    }

    @Override
    public TardisHandlersManager instantiate() {
        return new TardisHandlersManager();
    }

    @Override
    public <To> void deserialize(JsonAdapter<Object, To> jsonAdapter, JsonDeserializationContext ctx, TardisHandlersManager manager, String s) {
        TardisComponentRegistry registry = TardisComponentRegistry.getInstance();
        TardisComponent.IdLike id = registry.get(s);

        if (id == null) {
            AITMod.LOGGER.error("Can't find a subsystem id with name '{}'!", s);
            return;
        }

        try {
            manager.set(ctx.decode(id.clazz()));
        } catch (Throwable e) {
            AITMod.LOGGER.error("Failed to deserialize subsystem {}", id, e);
        }
    }
}

package dev.amble.ait.core.tardis.manager.autojson;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.registry.SubSystemRegistry;
import dev.amble.ait.core.tardis.handler.SubSystemHandler;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.base.ObjectSchema;

public class SubSystemHandlerSchema implements ObjectSchema<SubSystemHandler> {

    @Override
    public <To> void serialize(JsonAdapter<Object, To> adapter, JsonSerializationContext.Obj obj, SubSystemHandler subSystems) {
        subSystems.forEach(component -> {
            SubSystem.IdLike idLike = component.getId();

            if (idLike == null) {
                AITMod.LOGGER.error("Id was null for {}", component.getClass());
                return;
            }

            obj.obj$put(idLike.name(), component);
        });
    }

    @Override
    public SubSystemHandler instantiate() {
        return new SubSystemHandler();
    }

    @Override
    public <To> void deserialize(JsonAdapter<Object, To> jsonAdapter, JsonDeserializationContext ctx, SubSystemHandler subSystems, String s) {
        SubSystemRegistry registry = SubSystemRegistry.getInstance();
        SubSystem.IdLike id = registry.get(s);

        if (id == null) {
            AITMod.LOGGER.error("Can't find a subsystem id with name '{}'!", s);
            return;
        }

        try {
            subSystems.set(ctx.decode(id.clazz()));
        } catch (Throwable e) {
            AITMod.LOGGER.error("Failed to deserialize subsystem {}", id, e);
        }
    }
}

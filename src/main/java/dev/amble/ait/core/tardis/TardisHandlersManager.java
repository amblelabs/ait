package dev.amble.ait.core.tardis;

import com.google.gson.*;
import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.enummap.ConcurrentEnumMap;
import dev.amble.ait.registry.impl.TardisComponentRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.function.Consumer;

public class TardisHandlersManager extends TardisComponent implements TardisTickable {

    @Exclude
    private final ConcurrentEnumMap<IdLike, TardisComponent> handlers = new ConcurrentEnumMap<>(TardisComponentRegistry::values,
            TardisComponent[]::new);

    public TardisHandlersManager() {
        super(Id.HANDLERS);
    }

    @Override
    public void onCreate() {
        TardisComponentRegistry.getInstance().fill(this::createHandler);
    }

    @Override
    protected void onInit(InitContext ctx) {
        this.forEach(component -> TardisComponent.init(component, this.tardis, ctx));
    }

    @Override
    public void postInit(InitContext ctx) {
        this.forEach(component -> component.postInit(ctx));
    }

    private void forEach(Consumer<TardisComponent> consumer) {
        for (TardisComponent component : this.handlers.getValues()) {
            if (component == null)
                continue;

            consumer.accept(component);
        }
    }

    private void createHandler(TardisComponent component) {
        this.handlers.put(component.getId(), component);
    }

    /**
     * Called on the END of a servers tick
     *
     * @param server
     *            the current server
     */
    public void tick(MinecraftServer server) {
        this.forEach(component -> {
            if (!(component instanceof TardisTickable tickable))
                return;

            try {
                tickable.tick(server);
            } catch (Exception e) {
                AITMod.LOGGER.error("Ticking failed for {} | {}", component.getId().name(), component.tardis().getUuid().toString(), e);
            }
        });
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void tick(MinecraftClient client) {
        this.forEach(component -> {
            if (!(component instanceof TardisTickable tickable))
                return;

            try {
                tickable.tick(client);
            } catch (Exception e) {
                AITMod.LOGGER.error("Ticking failed for {} | {}", component.getId().name(), component.tardis().getUuid().toString(), e);
            }
        });
    }

    /**
     * @deprecated Use {@link Tardis#handler(IdLike)}
     */
    @Deprecated
    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    public <T extends TardisComponent> T get(IdLike id) {
        return (T) this.handlers.get(id);
    }

    @Override
    public void dispose() {
        super.dispose();

        this.forEach(TardisComponent::dispose);
        this.handlers.clear();
    }

    @ApiStatus.Internal
    public <T extends TardisComponent> void set(T t) {
        this.handlers.put(t.getId(), t);
    }

    public static Object serializer() {
        return new Serializer();
    }

    static class Serializer implements JsonSerializer<TardisHandlersManager>, JsonDeserializer<TardisHandlersManager> {

        @Override
        public TardisHandlersManager deserialize(JsonElement json, java.lang.reflect.Type type,
                JsonDeserializationContext context) throws JsonParseException {
            TardisHandlersManager manager = new TardisHandlersManager();
            Map<String, JsonElement> map = json.getAsJsonObject().asMap();

            TardisComponentRegistry registry = TardisComponentRegistry.getInstance();

            for (Map.Entry<String, JsonElement> entry : map.entrySet()) {
                String key = entry.getKey();
                JsonElement element = entry.getValue();

                IdLike id = registry.get(key);

                if (id == null) {
                    AITMod.LOGGER.error("Can't find a component id with name '{}'!", key);
                    continue;
                }

                manager.set(context.deserialize(element, id.clazz()));
            }

            for (int i = 0; i < manager.handlers.size(); i++) {
                if (manager.handlers.get(i) != null)
                    continue;

                IdLike id = registry.get(i);
                AITMod.LOGGER.debug("Appending new component {}", id);

                manager.set(id.create());
            }

            return manager;
        }

        @Override
        public JsonElement serialize(TardisHandlersManager manager, java.lang.reflect.Type type,
                JsonSerializationContext context) {
            JsonObject result = new JsonObject();

            manager.forEach(component -> {
                IdLike idLike = component.getId();

                if (idLike == null) {
                    AITMod.LOGGER.error("Id was null for {}", component.getClass());
                    return;
                }

                result.add(idLike.name(), context.serialize(component));
            });

            return result;
        }
    }
}

package dev.amble.ait.data.properties;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.gson.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.network.PacketByteBuf;

import dev.amble.ait.api.Disposable;
import dev.amble.ait.api.KeyedTardisComponent;
import dev.amble.ait.api.TardisComponent;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.tardis.util.network.c2s.SyncPropertyC2SPacket;
import dev.amble.ait.data.Exclude;

public class Value<T> implements Disposable {

    /**
     * Due to a circular-dependency between a component and a property, it should be
     * excluded.
     */
    @Exclude
    private TardisComponent holder;

    @Exclude
    protected Property<T> property;

    @Exclude
    private List<Consumer<T>> listeners;

    private T value;

    protected Value(T value) {
        this.value = value;
        this.listeners = new ArrayList<>();
    }

    public void of(KeyedTardisComponent holder, Property<T> property) {
        this.holder = holder;
        this.property = property;

        holder.register(this);
    }

    public void addListener(Consumer<T> listener) {
        if (this.listeners == null)
            this.listeners = new ArrayList<>();

        this.listeners.add(listener);
    }

    public Property<T> getProperty() {
        return property;
    }

    public TardisComponent getHolder() {
        return holder;
    }

    public T get() {
        return value;
    }

    public void set(Value<T> value) {
        this.set(value.get(), true);
    }

    public void set(T value) {
        this.set(value, true);
    }

    public void set(T value, boolean sync) {
        if (this.value == value)
            return;

        this.value = value;

        if (this.listeners != null) {
            for (Consumer<T> listener : this.listeners)
                listener.accept(value);
        }

        if (sync)
            this.sync();
    }

    protected void sync() {
        if (this.holder == null || !(this.holder.tardis() instanceof ServerTardis tardis)) {
            this.syncToServer();
            return;
        }

        ServerTardisManager.getInstance().markPropertyDirty(tardis, this);
    }
    @Environment(EnvType.CLIENT)
    protected void syncToServer() { // todo - flags
        ClientPlayNetworking.send(new SyncPropertyC2SPacket(this.holder.tardis().getUuid(), this));
    }

    public void flatMap(Function<T, T> func) {
        this.set(func.apply(this.value), true);
    }

    public void flatMap(Function<T, T> func, boolean sync) {
        this.set(func.apply(this.value), sync);
    }

    public void ifPresent(Consumer<T> consumer) {
        this.ifPresent(consumer, true);
    }

    public void ifPresent(Consumer<T> consumer, boolean sync) {
        if (this.value == null)
            return;

        consumer.accept(this.value);

        if (sync)
            this.sync();
    }

    public void read(PacketByteBuf buf, byte mode) {
        if (this.property == null)
            throw new IllegalStateException(
                    "Couldn't get the parent property value! Maybe you forgot to initialize the value field on load?");

        T value = mode == Property.Mode.UPDATE ? this.property.getType().decode(buf) : null;

        this.set(value, false);
    }

    public void write(PacketByteBuf buf) {
        this.property.getType().encode(buf, this.value);
    }

    @Override
    public void dispose() {
        this.holder = null;
    }

    public static Object serializer() {
        return new Serializer<>(Value::new);
    }

    protected static class Serializer<V, T extends Value<V>> implements JsonSerializer<T>, JsonDeserializer<T> {

        private final Class<?> clazz;
        private final Function<V, T> creator;

        public Serializer(Property.Type<?> type, Function<V, T> creator) {
            this(type.getClazz(), creator);
        }

        public Serializer(Class<?> clazz, Function<V, T> creator) {
            this.clazz = clazz;
            this.creator = creator;
        }

        protected Serializer(Function<V, T> creator) {
            this((Class<?>) null, creator);
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            Type type = clazz;

            if (clazz == null && typeOfT instanceof ParameterizedType parameter)
                type = parameter.getActualTypeArguments()[0];

            return this.creator.apply(context.deserialize(json, type));
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.get());
        }
    }
}

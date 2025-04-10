package dev.drtheo.autojson;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.ast.JsonElement;
import dev.drtheo.autojson.ast.JsonObject;
import dev.drtheo.autojson.ast.JsonPrimitive;
import dev.drtheo.autojson.bake.ClassAdapter;
import dev.drtheo.autojson.bake.UnsafeUtil;

import java.lang.reflect.Field;

public class BakedAutoSchema<T> implements Schema<T> {

    @SuppressWarnings("unchecked")
    public static <T> BakedAutoSchema<T> bake(Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        FieldType<T, ?>[] types = new FieldType[fields.length];

        for (int i = 0; i < fields.length; i++) {
            types[i] = FieldType.from(fields[i]);
        }

        return new BakedAutoSchema<>(clazz, types);
    }

    record FieldType<T, E>(Class<E> type, ClassAdapter<E> adapter, String name, long offset) {

        public static <T, E> FieldType<T, E> from(Field field) {
            Class<E> type = (Class<E>) field.getType();
            ClassAdapter<E> adapter = (ClassAdapter<E>) ClassAdapter.match(type);

            return new FieldType<>(type, adapter, field.getName(),
                    UnsafeUtil.UNSAFE.objectFieldOffset(field));
        }

        public E get(T obj) {
            return this.adapter.get(UnsafeUtil.UNSAFE, obj, offset);
        }

        public void set(T obj, E value) {
            this.adapter.set(UnsafeUtil.UNSAFE, obj, offset, value);
        }
    }

    private final Class<T> clazz;
    private final FieldType<T, ?>[] fields;

    private BakedAutoSchema(Class<T> clazz, FieldType<T, ?>[] fields) {
        this.clazz = clazz;
        this.fields = fields;
    }

    @Override
    public <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext c, T t) {
        if (!this.clazz.isInstance(t))
            throw new IllegalArgumentException();

        for (FieldType<T, ?> type : this.fields) {
            c.put(type.name(), type.get(t));
        }
    }

    @Override
    public <To> T deserialize(JsonAdapter<Object, To> auto, JsonElement element) {
        JsonObject object = element.getAsJsonObject();

        try {
            T t = (T) UnsafeUtil.UNSAFE.allocateInstance(this.clazz);

            for (FieldType<T, ?> field : this.fields) {
                deserialize(auto, field, t, object);
            }

            return t;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }



    private static <T, E, To> void deserialize(JsonAdapter<Object, To> auto, FieldType<T, E> field, T t, JsonObject object) {
        JsonElement element = object.get(field.name());

        Class<E> clazz = field.type();

        /*if (UnsafeUtil.isChar(clazz)) {
            element = new JsonPrimitive(element.getAsJsonPrimitive().getAsCharacter());
        } else if(UnsafeUtil.isNumber(clazz)) {
            Number wrapped = element.getAsJsonPrimitive().getAsNumber();

            if (clazz == double.class || clazz == Double.class)
                element = new JsonPrimitive(wrapped.doubleValue());

            if (clazz == float.class || clazz == Float.class)
                element = new JsonPrimitive(wrapped.floatValue());

            if (clazz == long.class || clazz == Long.class)
                element = new JsonPrimitive(wrapped.longValue());

            if (clazz == short.class || clazz == Short.class)
                element = new JsonPrimitive(wrapped.shortValue());

            if (clazz == byte.class || clazz == Byte.class)
                element = new JsonPrimitive(wrapped.byteValue());

            if (clazz == int.class || clazz == Integer.class)
                element = new JsonPrimitive(wrapped.intValue());
        }*/

        field.set(t, auto.fromJson(element, field.type()));
    }
}

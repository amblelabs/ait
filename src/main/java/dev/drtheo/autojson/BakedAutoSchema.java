package dev.drtheo.autojson;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.ast.JsonElement;
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

    record FieldType<T, E>(ClassAdapter<E> adapter, String name, long offset) {

        public static <T, E> FieldType<T, E> from(Field field) {
            Class<?> type = field.getType();
            ClassAdapter<E> adapter = (ClassAdapter<E>) ClassAdapter.match(type);

            return new FieldType<>(adapter, field.getName(),
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
    public <To> void serialize(JsonAdapter<? super T, To> auto, JsonSerializationContext c, T t) {
        if (!this.clazz.isInstance(t))
            throw new IllegalArgumentException();

        for (FieldType<T, ?> type : this.fields) {
            c.put(type.name(), type.get(t));
        }
    }

    @Override
    public <To> T deserialize(JsonAdapter<T, To> auto, JsonElement element) {
        return null;
    }

    /*private static <T, E, To extends JsonObject> void deserialize(JsonAdapter<E, To> auto, FieldType<T, E> field, T o, To obj) {
        field.set(o, field.adapter().fromJson(auto, obj.get(field.name())));
    }*/
}

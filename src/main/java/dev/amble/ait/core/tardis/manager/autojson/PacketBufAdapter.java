package dev.amble.ait.core.tardis.manager.autojson;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.schema.base.Schema;
import net.minecraft.network.PacketByteBuf;

import java.lang.reflect.Type;

public class PacketBufAdapter implements JsonAdapter<Object, PacketByteBuf> {

    public static final byte BEGIN_OBJECT = 0x01;
    public static final byte END_OBJECT = 0x02;
    public static final byte BEGIN_ARRAY = 0x03;
    public static final byte END_ARRAY = 0x04;

    private final AutoJSON auto;

    public PacketBufAdapter(AutoJSON auto) {
        this.auto = auto;
    }

    @Override
    public <T> PacketByteBuf toJson(T t, Type type) {
        PacketBufSerial ctx = new PacketBufSerial(this);

        this.toJson(ctx, t, type, this.auto.schema(type));
        return ctx.buf();
    }

    protected <T> void toJson(PacketBufSerial ctx, T obj, Type type, Schema<T> s) {
        try {
            s.serialize(this, ctx, obj);
        } catch (Exception e) {
            System.err.println("Failed to serialize " + type);
            throw e;
        }
    }

    @Override
    public <R> R fromJson(PacketByteBuf buf, Type type) {
        return new PacketBufParser(this, buf).deserialize(type);
    }

    @Override
    public <R> Schema<R> schema(Type type) {
        return auto.schema(type);
    }

    public AutoJSON auto() {
        return auto;
    }

    public static boolean canBeNull(Type type) {
        if (type instanceof Class<?> c)
            return !c.isPrimitive() || c.isArray();

        return true;
    }
}

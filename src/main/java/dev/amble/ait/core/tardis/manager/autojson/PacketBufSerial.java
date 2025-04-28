package dev.amble.ait.core.tardis.manager.autojson;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.PrimitiveSchema;
import dev.drtheo.autojson.schema.Schema;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.lang.reflect.Type;
import java.util.*;

public class PacketBufSerial implements JsonSerializationContext, JsonSerializationContext.Obj, JsonSerializationContext.Array, JsonSerializationContext.Primitive, JsonSerializationContext.Built {

    private final PacketBufAdapter adapter;
    private final PacketByteBuf buf = PacketByteBufs.create();

    private final List<ArrayEntry<?>> listBuffer = new ArrayList<>();
    private final List<ObjEntry<?>> objBuffer = new ArrayList<>();

    public PacketBufSerial(PacketBufAdapter adapter) {
        this.adapter = adapter;
    }

    public <T> void put(String key, T t, Type type, Schema<T> schema) {
        buf.writeString(key);
        this.put(t, type, schema);
    }

    <T> void put(T t, Type type, Schema<T> schema) {
        if (PacketBufAdapter.canBeNull(type)) {
            buf.writeNullable(t, (packetByteBuf, t1) -> this.put0(t1, type, schema));
        } else {
            this.put0(t, type, schema);
        }
    }

    private <T> void put0(T t, Type type, Schema<T> schema) {
        if (t instanceof String s)
            buf.writeString(s);
        else if (t instanceof Integer i)
            buf.writeInt(i);
        else if (t instanceof Character c)
            buf.writeChar(c);
        else if (t instanceof Short s)
            buf.writeShort(s);
        else if (t instanceof Double d)
            buf.writeDouble(d);
        else if (t instanceof Float f)
            buf.writeFloat(f);
        else if (t instanceof Byte b)
            buf.writeByte(b);
        else if (t instanceof Boolean b)
            buf.writeBoolean(b);
        else if (t instanceof UUID id)
            buf.writeUuid(id);
        else if (t instanceof Long l)
            buf.writeLong(l);
        else if (t instanceof BlockPos b)
            buf.writeBlockPos(b);
        else if (t instanceof ChunkPos c)
            buf.writeChunkPos(c);
        else if (t instanceof ItemStack i)
            buf.writeItemStack(i);
        else {

            if (schema != null) {
                this.adapter.toJson(this, t, type, schema);
            } else {
                throw new IllegalArgumentException("No schema for type " + type);
            }

            return;
        }
    }

    public JsonSerializationContext.Array array$element(Object value, Type type) {
        return this.array$element(value, type, this.adapter.schema(type));
    }

    @Override
    public <T> JsonSerializationContext.Array array$element(T t, Type type, Schema<T> schema) {
        listBuffer.add(new ArrayEntry<>(t, type, schema));
        return this;
    }

    @Override
    public JsonSerializationContext.Built array$build() {
        List<ArrayEntry<?>> buffer = new ArrayList<>(listBuffer);
        listBuffer.clear();

        int listSize = buffer.size();
        buf.writeVarInt(listSize);

        Iterator<ArrayEntry<?>> iter = buffer.iterator();

        while (iter.hasNext()) {
            ArrayEntry<?> entry = iter.next();
            iter.remove();

            entry.put(this);
        }

        buf.writeByte(PacketBufAdapter.END_ARRAY);
        return this;
    }

    @Override
    public <T> JsonSerializationContext.Obj obj$put(String s, T t, Type type, Schema<T> schema) {
        this.objBuffer.add(new ObjEntry<>(s, t, type, schema));
        return this;
    }

    @Override
    public JsonSerializationContext.Built obj$build() {
        List<ObjEntry<?>> buffer = new ArrayList<>(objBuffer);
        objBuffer.clear();

        int listSize = buffer.size();
        buf.writeVarInt(listSize);

        Iterator<ObjEntry<?>> iter = buffer.iterator();

        while (iter.hasNext()) {
            ObjEntry<?> entry = iter.next();
            iter.remove();

            entry.put(this);
        }

        this.buf.writeByte(PacketBufAdapter.END_OBJECT);
        return this;
    }

    @Override
    public Obj object() {
        this.buf.writeByte(PacketBufAdapter.BEGIN_OBJECT);
        return this;
    }

    @Override
    public Array array() {
        this.buf.writeByte(PacketBufAdapter.BEGIN_ARRAY);
        return this;
    }

    @Override
    public Primitive primitive() {
        return this;
    }

    @Override
    public AutoJSON auto() {
        return adapter.auto();
    }

    @Override
    public <T> Schema<T> schema(Type type) {
        return adapter.schema(type);
    }

    @Override
    public <T> void primitive$value(T t, Type type, PrimitiveSchema<T> schema) {
        this.put(t, type, schema);
    }

    @Override
    public Built primitive$build() {
        return this;
    }

    public PacketByteBuf buf() {
        return buf;
    }

    record ArrayEntry<T>(T o, Type t, Schema<T> schema) {

        public void put(PacketBufSerial serial) {
            serial.put(o, t, schema);
        }
    }

    record ObjEntry<T>(String field, T o, Type t, Schema<T> schema) {

        public void put(PacketBufSerial serial) {
            serial.put(field, o, t, schema);
        }
    }
}

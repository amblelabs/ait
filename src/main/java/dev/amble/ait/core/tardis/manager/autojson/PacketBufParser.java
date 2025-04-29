package dev.amble.ait.core.tardis.manager.autojson;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.schema.base.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.UUID;

public class PacketBufParser implements JsonDeserializationContext {

    private final PacketBufAdapter adapter;
    private final PacketByteBuf buf;

    private Object currentValue;

    public PacketBufParser(PacketBufAdapter adapter, PacketByteBuf buf) {
        this.adapter = adapter;
        this.buf = buf;
    }

    public <T> T deserialize(Type type) {
        return decode0(type, this.adapter.schema(type), false);
    }

    @Override
    public <T> T decodeBuiltIn(Class<T> t) {
        return decode0(t, null, true);
    }

    @Override
    public <T> T decodeCustom(Type type, @NotNull Schema<T> schema) {
        return decode0(type, schema, true);
    }

    private <T> T decode0(Type type, Schema<T> schema, boolean canBeNull) {
        if (canBeNull && PacketBufAdapter.canBeNull(type)) {
            return (T) buf.readNullable(b -> read(this, b, type, schema));
        } else {
            return (T) read(this, buf, type, schema);
        }
    }

    private static <T> Object read(PacketBufParser self, PacketByteBuf buf, Type type, Schema<T> s) {
        if (s == null) {
            self.currentValue = null;
            return self.readPrimitive(type);
        } else if (s.type() == SchemaType.PRIMITIVE) {
            self.currentValue = self.readPrimitive(type);
            return self.readPrimitive(s.asPrimitive());
        } else if (s instanceof ObjectSchema<T> os) {
            return self.readObject(os);
        } else if (s instanceof ArraySchema<T,?> as) {
            // TODO: handle array schema
            return null;
        }

        throw new IllegalArgumentException("Unhandled");
    }

    private Object readPrimitive(Type type) {
        if (type == String.class)
            return buf.readString();
        else if (type == int.class || type == Integer.class)
            return buf.readInt();
        else if (type == char.class || type == Character.class)
            return buf.readChar();
        else if (type == short.class || type == Short.class)
            return buf.readShort();
        else if (type == double.class || type == Double.class)
            return buf.readDouble();
        else if (type == float.class || type == Float.class)
            return buf.readFloat();
        else if (type == byte.class || type == Byte.class)
            return buf.readByte();
        else if (type == boolean.class || type == Boolean.class)
            return buf.readBoolean();
        else if (type == UUID.class)
            return buf.readUuid();
        else if (type == long.class || type == Long.class)
            return buf.readLong();
        else if (type == BlockPos.class)
            return buf.readBlockPos();
        else if (type == ChunkPos.class)
            return buf.readChunkPos();
        else if (type == ItemStack.class)
            return buf.readItemStack();

        return null;
    }

    private <T> T readPrimitive(PrimitiveSchema<T> ps) {
        return ps.deserialize(this.adapter, this);
    }

    private <T> T readObject(ObjectSchema<T> os) {
        byte begin = buf.readByte();

        if (begin != PacketBufAdapter.BEGIN_OBJECT)
            throw new IllegalStateException("Expected object start, got " + begin);

        int len = buf.readVarInt();
        T t = os.instantiate();

        for (int i = 0; i < len; i++) {
            String key = buf.readString();
            boolean notNull = buf.readBoolean();

            if (notNull)
                os.deserialize(this.adapter, this, t, key);
        }

        byte end = buf.readByte();

        if (end != PacketBufAdapter.END_OBJECT)
            throw new IllegalStateException("Expected object end, got " + begin);

        return t;
    }

    @Override
    public AutoJSON auto() {
        return adapter.auto();
    }

    @Override
    public <T> Schema<T> schema(Type type) {
        return adapter.schema(type);
    }
}

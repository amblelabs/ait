package dev.amble.ait.data.properties;

import java.util.HashSet;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedGlobalPos;
import org.joml.Vector2i;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import dev.amble.ait.api.KeyedTardisComponent;

public class Property<T> {

    private final Type<T> type;
    private final String name;

    protected final Function<KeyedTardisComponent, T> def;

    public Property(Type<T> type, String name, Function<KeyedTardisComponent, T> def) {
        this.type = type;
        this.name = name;
        this.def = def;
    }

    public Property(Type<T> type, String name, T def) {
        this(type, name, o -> def);
    }

    public Value<T> create(KeyedTardisComponent holder) {
        T t = this.def == null ? null : this.def.apply(holder);
        Value<T> result = this.create(t);

        result.of(holder, this);
        return result;
    }

    protected Value<T> create(T t) {
        return new Value<>(t);
    }

    public String getName() {
        return name;
    }

    public Type<T> getType() {
        return type;
    }

    public Property<T> copy(String name) {
        return new Property<>(this.type, name, this.def);
    }

    public Property<T> copy(String name, T def) {
        return new Property<>(this.type, name, def);
    }

    public static <T extends Enum<T>> Property<T> forEnum(String name, Class<T> clazz, T def) {
        return new Property<>(Type.forEnum(clazz), name, def);
    }

    public static class Type<T> {

        public static final Type<DirectedGlobalPos> DIRECTED_GLOBAL_POS = new Type<>(DirectedGlobalPos.class,
                (buf, pos) -> pos.write(buf), DirectedGlobalPos::read);

        public static final Type<BlockPos> BLOCK_POS = new Type<>(BlockPos.class, PacketByteBuf::writeBlockPos,
                PacketByteBuf::readBlockPos);
        public static final Type<CachedDirectedGlobalPos> CDIRECTED_GLOBAL_POS = new Type<>(
                CachedDirectedGlobalPos.class, (buf, pos) -> pos.write(buf), CachedDirectedGlobalPos::read);
        public static final Type<Identifier> IDENTIFIER = new Type<>(Identifier.class, PacketByteBuf::writeIdentifier,
                PacketByteBuf::readIdentifier);

        public static final Type<RegistryKey<World>> WORLD_KEY = new Type<>(RegistryKey.class,
                PacketByteBuf::writeRegistryKey, buf -> buf.readRegistryKey(RegistryKeys.WORLD));
        public static final Type<Direction> DIRECTION = Type.forEnum(Direction.class);

        public static final Type<Vector2i> VEC2I = new Type<>(Vector2i.class, (buf, vector2i) -> {
            buf.writeInt(vector2i.x);
            buf.writeInt(vector2i.y);
        }, buf -> new Vector2i(buf.readInt(), buf.readInt()));

        public static final Type<String> STR = new Type<>(String.class, PacketByteBuf::writeString,
                PacketByteBuf::readString);

        public static final Type<UUID> UUID = new Type<>(UUID.class, PacketByteBuf::writeUuid, PacketByteBuf::readUuid);

        public static final Type<Double> DOUBLE = new Type<>(Double.class, PacketByteBuf::writeDouble,
                PacketByteBuf::readDouble);

        public static final Type<HashSet<String>> STR_SET = new Type<>(HashSet.class, (buf, strings) -> {
            buf.writeVarInt(strings.size());

            for (String str : strings)
                buf.writeString(str);
        }, buf -> {
            HashSet<String> result = new HashSet<>();
            int size = buf.readVarInt();

            for (int i = 0; i < size; i++) {
                result.add(buf.readString());
            }

            return result;
        });

        public static final Type<ItemStack> ITEM_STACK = new Type<>(ItemStack.class, PacketByteBuf::writeItemStack,
                PacketByteBuf::readItemStack);

        private final Class<?> clazz;
        private final BiConsumer<PacketByteBuf, T> encoder;
        private final Function<PacketByteBuf, T> decoder;

        public Type(Class<?> clazz, BiConsumer<PacketByteBuf, T> encoder, Function<PacketByteBuf, T> decoder) {
            this.clazz = clazz;
            this.encoder = encoder;
            this.decoder = decoder;
        }

        public void encode(PacketByteBuf buf, T value) {
            this.encoder.accept(buf, value);
        }

        public T decode(PacketByteBuf buf) {
            return this.decoder.apply(buf);
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public static <T extends Enum<T>> Type<T> forEnum(Class<T> clazz) {
            return new Type<>(clazz, PacketByteBuf::writeEnumConstant, buf -> buf.readEnumConstant(clazz));
        }
    }

    public interface Mode {
        byte UPDATE = 0;
        byte NULL = 1;
    }
}

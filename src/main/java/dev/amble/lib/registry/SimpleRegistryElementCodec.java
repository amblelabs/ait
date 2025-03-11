package dev.amble.lib.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.Optional;

public class SimpleRegistryElementCodec<E> implements Codec<RegistryEntry<E>> {

    private final AmbleRegistry<E> registry;

    public static <E> SimpleRegistryElementCodec<E> of(AmbleRegistry<E> registry) {
        return new SimpleRegistryElementCodec<>(registry);
    }

    private SimpleRegistryElementCodec(AmbleRegistry<E> registry) {
        this.registry = registry;
    }

    @Override
    public <T> DataResult<T> encode(RegistryEntry<E> registryEntry, DynamicOps<T> dynamicOps, T object) {
        Optional<RegistryKey<E>> key = registryEntry.getKey();

        if (key.isEmpty())
            return DataResult.error(() -> "No key for value");

        return Identifier.CODEC.encode(key.get().getValue(), dynamicOps, object);
    }

    @Override
    public <T> DataResult<Pair<RegistryEntry<E>, T>> decode(DynamicOps<T> ops, T input) {
        DataResult<Pair<Identifier, T>> dataResult = Identifier.CODEC.decode(ops, input);

        if (dataResult.result().isEmpty())
            return DataResult.error(() -> "Inline definitions not allowed here");

        Pair<Identifier, T> pair = dataResult.result().get();
        RegistryKey<E> registryKey = RegistryKey.of(this.registry.getKey(), pair.getFirst());

        return registry.getEntry(registryKey)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(() -> "Failed to get element " + registryKey))
                .<Pair<RegistryEntry<E>, T>>map(reference -> Pair.of(reference, pair.getSecond()))
                .setLifecycle(Lifecycle.stable());
    }

    public String toString() {
        return "RegistryFileCodec[" + this.registry.getKey() + "]";
    }

    public static <E> MapCodec<RegistryEntry<E>> or(String name, SimpleRegistryElementCodec<E> codec, Identifier id) {
        return Codec.optionalField(name, codec).xmap(
                o -> o.orElse(codec.registry.getEntry(RegistryKey.of(codec.registry.getKey(), id)).get()),
                a -> Objects.equals(a.getKey().map(RegistryKey::getValue).orElse(id), id) ? Optional.empty() : Optional.of(a)
        );
    }
}

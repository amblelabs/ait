package dev.amble.lib.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public abstract class AmbleRegistry<T> implements Registry<T> {

    private final Identifier id;
    private final RegistryKey<Registry<T>> key;

    public AmbleRegistry(Identifier id) {
        this.id = id;
        this.key = RegistryKey.ofRegistry(id);
    }

    public T tryGetOr(Identifier id, Identifier def) {
        return this.getOrEmpty(id).orElse(this.get(def));
    }

    public Identifier getId() {
        return id;
    }

    public RegistryKey<Registry<T>> getKey() {
        return key;
    }

    @Override
    public @Nullable Identifier getId(T value) {
        return this.get().getId(value);
    }

    @Override
    public Optional<RegistryKey<T>> getKey(T entry) {
        return this.get().getKey(entry);
    }

    @Override
    public int getRawId(@Nullable T value) {
        return this.get().getRawId(value);
    }

    @Override
    public @Nullable T get(int index) {
        return this.get().get(index);
    }

    @Override
    public int size() {
        return this.get().size();
    }

    @Override
    public @Nullable T get(@Nullable RegistryKey<T> key) {
        return this.get().get(key);
    }

    @Override
    public @Nullable T get(@Nullable Identifier id) {
        return this.get().get(id);
    }

    @Override
    public Lifecycle getEntryLifecycle(T entry) {
        return this.get().getEntryLifecycle(entry);
    }

    @Override
    public Lifecycle getLifecycle() {
        return this.get().getLifecycle();
    }

    @Override
    public Set<Identifier> getIds() {
        return this.get().getIds();
    }

    @Override
    public Set<Map.Entry<RegistryKey<T>, T>> getEntrySet() {
        return this.get().getEntrySet();
    }

    @Override
    public Set<RegistryKey<T>> getKeys() {
        return this.get().getKeys();
    }

    @Override
    public Optional<RegistryEntry.Reference<T>> getRandom(Random random) {
        return this.get().getRandom(random);
    }

    @Override
    public boolean containsId(Identifier id) {
        return this.get().containsId(id);
    }

    @Override
    public boolean contains(RegistryKey<T> key) {
        return this.get().contains(key);
    }

    @Override
    public Registry<T> freeze() {
        return this.get().freeze();
    }

    @Override
    public RegistryEntry.Reference<T> createEntry(T value) {
        return this.get().createEntry(value);
    }

    @Override
    public Optional<RegistryEntry.Reference<T>> getEntry(int rawId) {
        return this.get().getEntry(rawId);
    }

    @Override
    public Optional<RegistryEntry.Reference<T>> getEntry(RegistryKey<T> key) {
        return this.get().getEntry(key);
    }

    @Override
    public RegistryEntry<T> getEntry(T value) {
        return this.get().getEntry(value);
    }

    @Override
    public Stream<RegistryEntry.Reference<T>> streamEntries() {
        return this.get().streamEntries();
    }

    @Override
    public Optional<RegistryEntryList.Named<T>> getEntryList(TagKey<T> tag) {
        return this.get().getEntryList(tag);
    }

    @Override
    public RegistryEntryList.Named<T> getOrCreateEntryList(TagKey<T> tag) {
        return this.get().getOrCreateEntryList(tag);
    }

    @Override
    public Stream<Pair<TagKey<T>, RegistryEntryList.Named<T>>> streamTagsAndEntries() {
        return this.get().streamTagsAndEntries();
    }

    @Override
    public Stream<TagKey<T>> streamTags() {
        return this.get().streamTags();
    }

    @Override
    public void clearTags() {
        this.get().clearTags();
    }

    @Override
    public void populateTags(Map<TagKey<T>, List<RegistryEntry<T>>> tagEntries) {
        this.get().populateTags(tagEntries);
    }

    @Override
    public RegistryEntryOwner<T> getEntryOwner() {
        return this.get().getEntryOwner();
    }

    @Override
    public RegistryWrapper.Impl<T> getReadOnlyWrapper() {
        return this.get().getReadOnlyWrapper();
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return this.get().iterator();
    }

    public abstract Registry<T> get();
}

package dev.amble.ait.module;

import dev.amble.ait.AITMod;
import dev.amble.ait.datagen.datagen_providers.AITBlockTagProvider;
import dev.amble.ait.datagen.datagen_providers.AITItemTagProvider;
import dev.amble.ait.datagen.datagen_providers.AITRecipeProvider;
import dev.amble.lib.api.Identifiable;
import dev.amble.lib.container.impl.BlockContainer;
import dev.amble.lib.container.impl.ItemContainer;
import dev.amble.lib.datagen.lang.AmbleLanguageProvider;
import dev.amble.lib.datagen.model.AmbleModelProvider;
import dev.amble.lib.itemgroup.AItemGroup;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class Module implements Identifiable {
    private AItemGroup group;

    public abstract void init();

    @Environment(EnvType.CLIENT)
    public abstract void initClient();

    protected Item register(Item item, Identifier id) {
        Registry.register(Registries.ITEM, id, item);
        return item;
    }
    protected SoundEvent register(SoundEvent sound, Identifier id) {
        Registry.register(Registries.SOUND_EVENT, id, sound);
        return sound;
    }
    protected SoundEvent registerSound(String name) {
        return register(SoundEvent.of(AITMod.id(name)), AITMod.id(name));
    }
    protected <T extends BlockEntity> BlockEntityType<T> register(BlockEntityType<T> type, Identifier id) {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, id, type);
        return type;
    }

    public Optional<Class<? extends BlockContainer>> getBlockRegistry() {
        return Optional.empty();
    }
    public Optional<Class<? extends ItemContainer>> getItemRegistry() {
        return Optional.empty();
    }

    public boolean shouldRegister() {
        return true;
    }

    protected AItemGroup.Builder buildItemGroup() {
        return null;
    }

    public AItemGroup getItemGroup() {
        AItemGroup.Builder builder = buildItemGroup();

        if (builder == null) throw new UnsupportedOperationException("Item Group for module " + this + " is not defined");
        if (!(this.shouldRegister())) throw new UnsupportedOperationException("Tried to access item group for module " + this + " but it is not registered");

        if (group == null) {
            group = builder.build();

            Registry.register(Registries.ITEM_GROUP, group.id(), group);
        }

        return group;
    }

    @Override
    public String toString() {
        return "Module{" +
                "id=" + id() +
                '}';
    }

    public Optional<DataGenerator> getDataGenerator() {
        return Optional.empty();
    }

    public interface DataGenerator {
        /**
         * Called when the ENGLISH language provider is generating
         */
        void lang(AmbleLanguageProvider provider);
        void recipes(AITRecipeProvider provider);
        void blockTags(AITBlockTagProvider provider);
        void itemTags(AITItemTagProvider provider);

        void generateItemModels(AmbleModelProvider provider, ItemModelGenerator generator);

        void models(AmbleModelProvider provider, BlockStateModelGenerator generator);

        void advancements(Consumer<Advancement> consumer);
    }
}

package dev.amble.ait.module.decoration;

import java.util.Optional;
import java.util.function.Consumer;

import dev.amble.lib.container.RegistryContainer;
import dev.amble.lib.container.impl.BlockContainer;
import dev.amble.lib.container.impl.ItemContainer;
import dev.amble.lib.datagen.lang.AmbleLanguageProvider;
import dev.amble.lib.datagen.model.AmbleModelProvider;
import dev.amble.lib.itemgroup.AItemGroup;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.advancement.Advancement;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.datagen.datagen_providers.AITBlockTagProvider;
import dev.amble.ait.datagen.datagen_providers.AITItemTagProvider;
import dev.amble.ait.datagen.datagen_providers.AITRecipeProvider;
import dev.amble.ait.module.Module;
import dev.amble.ait.module.decoration.core.DecorationBlocks;
import dev.amble.ait.module.decoration.core.DecorationItems;



public class DecorationModule extends Module {
    private static final DecorationModule INSTANCE = new DecorationModule();

    public static final Identifier ID = AITMod.id("decoration");

    @Override
    public void init() {
        RegistryContainer.register(DecorationItems.class, AITMod.MOD_ID);
        RegistryContainer.register(DecorationBlocks.class, AITMod.MOD_ID);
    }

    @Override
    protected AItemGroup.Builder buildItemGroup() {
        return AItemGroup.builder(id()).icon(() -> new ItemStack(Blocks.BARRIER));
    }



    @Environment(EnvType.CLIENT)
    @Override
    public void initClient() {
    }

    @Override
    public Identifier id() {
        return ID;
    }


    @Override
    public Optional<Class<? extends BlockContainer>> getBlockRegistry() {
        return Optional.of(DecorationBlocks.class);
    }

    @Override
    public Optional<Class<? extends ItemContainer>> getItemRegistry() {
        return Optional.of(DecorationItems.class);
    }

    @Override
    public Optional<DataGenerator> getDataGenerator() {
        return Optional.of(new DataGenerator() {
            @Override
            public void lang(AmbleLanguageProvider provider) {
                provider.addTranslation(getItemGroup(), "AIT: Decoration");
                provider.addTranslation("itemGroup.ait.decoration", "AIT: Decoration");

            }

            @Override
            public void recipes(AITRecipeProvider provider) {

            }


            @Override
            public void blockTags(AITBlockTagProvider provider) {
            }

            @Override
            public void itemTags(AITItemTagProvider provider) {

            }

            @Override
            public void generateItemModels(AmbleModelProvider provider, ItemModelGenerator generator) {

            }

            @Override
            public void models(AmbleModelProvider provider, BlockStateModelGenerator generator) {

            }

            @Override
            public void advancements(Consumer<Advancement> consumer) {

            }
        });
    }

    public static DecorationModule instance() {
        return INSTANCE;
    }
}

package dev.amble.ait.module.gun;

import java.util.Optional;
import java.util.function.Consumer;

import dev.amble.lib.container.RegistryContainer;
import dev.amble.lib.container.impl.ItemContainer;
import dev.amble.lib.datagen.lang.AmbleLanguageProvider;
import dev.amble.lib.datagen.model.AmbleModelProvider;
import dev.amble.lib.itemgroup.AItemGroup;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.advancement.Advancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.datagen.datagen_providers.AITBlockTagProvider;
import dev.amble.ait.datagen.datagen_providers.AITItemTagProvider;
import dev.amble.ait.datagen.datagen_providers.AITRecipeProvider;
import dev.amble.ait.module.Module;
import dev.amble.ait.module.gun.client.ScopeOverlay;
import dev.amble.ait.module.gun.client.render.StaserBoltEntityRenderer;
import dev.amble.ait.module.gun.core.entity.GunEntityTypes;
import dev.amble.ait.module.gun.core.item.GunItems;

public class GunModule extends Module {
    private static final GunModule INSTANCE = new GunModule();

    public static final Identifier ID = AITMod.id("gun");


    @Override
    public void init() {
        RegistryContainer.register(GunItems.class, AITMod.MOD_ID);
        RegistryContainer.register(GunEntityTypes.class, AITMod.MOD_ID);
    }

    @Override
    public void initClient() {
        HudRenderCallback.EVENT.register(new ScopeOverlay());
        EntityRendererRegistry.register(GunEntityTypes.STASER_BOLT_ENTITY_TYPE, StaserBoltEntityRenderer::new);

        ModelPredicateProviderRegistry.register(GunItems.CULT_STASER_RIFLE, new Identifier("ads"),
                (itemStack, clientWorld, livingEntity, integer) -> {
                    if (livingEntity == null) return 0.0f;
                    if (itemStack.getItem() == GunItems.CULT_STASER_RIFLE && livingEntity.getMainHandStack().getItem() == GunItems.CULT_STASER_RIFLE) {
                        if (livingEntity instanceof PlayerEntity) {
                            boolean bl = MinecraftClient.getInstance().options.useKey.isPressed();
                            return bl ? 1.0f : 0.0f;
                        }
                    }
                    return 0.0F;
                });
        ModelPredicateProviderRegistry.register(GunItems.CULT_STASER, new Identifier("ads"),
                (itemStack, clientWorld, livingEntity, integer) -> {
                    if (livingEntity == null) return 0.0f;
                    if (itemStack.getItem() == GunItems.CULT_STASER && livingEntity.getMainHandStack().getItem() == GunItems.CULT_STASER) {
                        if (livingEntity instanceof PlayerEntity) {
                            boolean bl = MinecraftClient.getInstance().options.useKey.isPressed();
                            return bl ? 1.0f : 0.0f;
                        }
                    }
                    return 0.0F;
                });
    }

    @Override
    public Optional<Class<? extends ItemContainer>> getItemRegistry() {
        return Optional.of(GunItems.class);
    }

    @Override
    protected AItemGroup.Builder buildItemGroup() {
        return AItemGroup.builder(id()).icon(() -> new ItemStack(GunItems.CULT_STASER));
    }

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public Optional<DataGenerator> getDataGenerator() {
        return Optional.of(new DataGenerator() {
            @Override
            public void lang(AmbleLanguageProvider provider) {
                // provider.addTranslation(getItemGroup(), "AIT: Combat");
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

    public static GunModule instance() {
        return INSTANCE;
    }
}

package dev.amble.ait.datagen.datagen_providers;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryWrapper;

import dev.amble.ait.core.AITEntityTypes;
import dev.amble.ait.core.AITTags;


public class AITEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {
    public AITEntityTypeTagProvider(FabricDataOutput output,
                                    CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(AITTags.EntityTypes.NON_DISMOUNTABLE)
                .add(AITEntityTypes.FLIGHT_TARDIS_TYPE);

        getOrCreateTagBuilder(AITTags.EntityTypes.BOSS)
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.WITHER)
                .add(EntityType.WARDEN)
                .add(EntityType.ELDER_GUARDIAN);

        getOrCreateTagBuilder(AITTags.EntityTypes.HADS_TRIGGERER)
                .add(EntityType.WITHER_SKULL)
                .add(EntityType.FIREBALL)
                .add(EntityType.DRAGON_FIREBALL)
                .add(EntityType.SMALL_FIREBALL)
                .add(EntityType.END_CRYSTAL)
                .add(EntityType.EVOKER_FANGS)
                .add(EntityType.LIGHTNING_BOLT)
                .add(EntityType.SHULKER_BULLET)
                .add(EntityType.TNT)
                .add(EntityType.TNT_MINECART)
                .add(EntityType.CREEPER);
    }
}

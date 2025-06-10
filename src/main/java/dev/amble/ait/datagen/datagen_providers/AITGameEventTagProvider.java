package dev.amble.ait.datagen.datagen_providers;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.event.GameEvent;

import dev.amble.ait.core.AITTags;

public class AITGameEventTagProvider extends FabricTagProvider.GameEventTagProvider {
    public AITGameEventTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        this.getOrCreateTagBuilder(AITTags.GameEvents.MATRIX_CAN_LISTEN).add(GameEvent.SHRIEK);
    }
}

package dev.amble.ait.client.boti;

import java.util.SortedMap;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Util;

import dev.amble.ait.client.renderers.AITRenderLayers;

@Environment(value=EnvType.CLIENT)
public class AITBufferBuilderStorage extends BufferBuilderStorage {
    private final SortedMap<RenderLayer, BufferBuilder> botiBuilder = Util.make(new Object2ObjectLinkedOpenHashMap(), map -> {
        AITBufferBuilderStorage.assignBufferBuilder(map, AITRenderLayers.getBoti());
    });
    private final VertexConsumerProvider.Immediate botiVertexConsumer = VertexConsumerProvider.immediate(this.botiBuilder, new BufferBuilder(256));

    private static void assignBufferBuilder(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> builderStorage, RenderLayer layer) {
        builderStorage.put(layer, new BufferBuilder(layer.getExpectedBufferSize()));
    }

    public VertexConsumerProvider.Immediate getBotiVertexConsumer() {
        return this.botiVertexConsumer;
    }
}

package dev.amble.ait.client.renderers.entities;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.boti.BOTI;
import dev.amble.ait.core.entities.BOTIPaintingEntity;

@Environment(value=EnvType.CLIENT)
public class BOTIPaintingEntityRenderer
        extends EntityRenderer<BOTIPaintingEntity> {
    public static final Identifier GALLIFREY_PAINTING_TEXTURE = AITMod.id("textures/painting/gallifrey_falls/gallifrey_falls.png");
    public static final Identifier GALLIFREY_FRAME_TEXTURE = AITMod.id("textures/painting/gallifrey_falls/gallifrey_falls_frame.png");
    public static final Identifier TRENZALORE_PAINTING_TEXTURE = AITMod.id("textures/painting/trenzalore/trenzalore.png");
    public static final Identifier TRENZALORE_FRAME_TEXTURE = AITMod.id("textures/painting/trenzalore/trenzalore_frame.png");
    public BOTIPaintingEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(BOTIPaintingEntity paintingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        BOTI.PAINTING_RENDER_QUEUE.add(paintingEntity);
    }

    @Override
    public Identifier getTexture(BOTIPaintingEntity entity) {
        return null;
    }

}

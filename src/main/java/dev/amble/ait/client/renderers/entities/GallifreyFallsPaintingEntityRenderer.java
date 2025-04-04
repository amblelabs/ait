package dev.amble.ait.client.renderers.entities;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.boti.BOTI;
import dev.amble.ait.client.models.decoration.GallifreyFallsFrameModel;
import dev.amble.ait.client.models.decoration.GallifreyFallsModel;
import dev.amble.ait.core.entities.GallifreyFallsPaintingEntity;

@Environment(value=EnvType.CLIENT)
public class GallifreyFallsPaintingEntityRenderer
        extends EntityRenderer<GallifreyFallsPaintingEntity> {
    private Framebuffer framebuffer;
    public static final Identifier PAINTING_TEXTURE = AITMod.id("textures/painting/gallifrey_falls/gallifrey_falls.png");
    public static final Identifier FRAME_TEXTURE = AITMod.id("textures/painting/gallifrey_falls/frame.png");
    GallifreyFallsFrameModel frame;
    GallifreyFallsModel painting;
    public GallifreyFallsPaintingEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        frame = new GallifreyFallsFrameModel(GallifreyFallsFrameModel.getTexturedModelData().createModel());
        painting = new GallifreyFallsModel(GallifreyFallsModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(GallifreyFallsPaintingEntity paintingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        BOTI.PAINTING_RENDER_QUEUE.add(paintingEntity);
    }

    @Override
    public Identifier getTexture(GallifreyFallsPaintingEntity entity) {
        return null;
    }

}

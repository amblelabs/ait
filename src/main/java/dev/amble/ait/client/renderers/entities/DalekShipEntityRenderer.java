package dev.amble.ait.client.renderers.entities;


import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.entities.transport.DalekShipModel;
import dev.amble.ait.core.entities.DalekShipEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class DalekShipEntityRenderer
        extends EntityRenderer<DalekShipEntity> {
    public static final Identifier SHIP_TEXTURE = AITMod.id("textures/entity/dalek_ship/dalek_ship.png");
    public static final Identifier EMISSION_TEXTURE = AITMod.id("textures/entity/dalek_ship/dalek_ship_emission.png");
    private final DalekShipModel model;
    public DalekShipEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new DalekShipModel(DalekShipModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(DalekShipEntity dalekShipEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        if(!dalekShipEntity.isAiDisabled())
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((MinecraftClient.getInstance().player.age / 200f * 220f)));
        matrixStack.translate(0, 1.5f, 0);
        this.model.render(matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(SHIP_TEXTURE)), i,
                OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        this.model.render(matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getEyes(EMISSION_TEXTURE)), i,
                OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(DalekShipEntity entity) {
        return null;
    }

}

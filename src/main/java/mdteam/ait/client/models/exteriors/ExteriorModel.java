package mdteam.ait.client.models.exteriors;

import mdteam.ait.AITMod;
import mdteam.ait.client.renderers.entities.FallingTardisRenderer;
import mdteam.ait.client.renderers.exteriors.ExteriorEnum;
import mdteam.ait.client.renderers.exteriors.VariantEnum;
import mdteam.ait.core.blockentities.ExteriorBlockEntity;
import mdteam.ait.core.entities.FallingTardisEntity;
import mdteam.ait.tardis.TardisTravel;
import mdteam.ait.tardis.handler.DoorHandler;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@SuppressWarnings("rawtypes")
public abstract class ExteriorModel extends SinglePartEntityModel {
    public static int MAX_TICK_COUNT = 2 * 20;

    public static String TEXTURE_PATH = "textures/blockentities/exteriors/";

    public ExteriorModel() {
        this(RenderLayer::getEntityCutoutNoCull);
    }

    public ExteriorModel(Function<Identifier, RenderLayer> function) {
        super(function);
    }

    // Thanks craig for help w animation code @TODO more craig thank yous
    public void animateTile(ExteriorBlockEntity exterior) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        if (exterior.tardis() == null)
            return;
        DoorHandler.DoorStateEnum state = exterior.tardis().getDoor().getDoorState();
        //System.out.println(state);
        this.updateAnimation(exterior.DOOR_STATE, getAnimationForDoorState(state), exterior.animationTimer);
    }

    public void renderWithAnimations(ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        if (exterior.tardis() == null) return;

        root.render(matrices, vertices, light, overlay, red, green, blue, exterior.getAlpha());
    }

    public void renderFalling(FallingTardisEntity falling, ModelPart root, MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices,vertexConsumer,light,overlay,red,green,blue,alpha);
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    public Identifier getVariousTextures(ExteriorEnum exterior, VariantEnum variant) {
        /*new Identifier(AITMod.MOD_ID, TEXTURE_PATH + exterior.toString().toLowerCase() + "/" + exterior.toString().toLowerCase() + ".png");
        new Identifier(AITMod.MOD_ID, TEXTURE_PATH + exterior.toString().toLowerCase() + "/" + exterior.toString().toLowerCase() + "_emission" + ".png");*/
        return new Identifier(AITMod.MOD_ID, TEXTURE_PATH + exterior.toString().toLowerCase() + "/" + exterior.toString().toLowerCase() + "_" + variant.toString().toLowerCase() + ".png");
    }

    public Identifier getVariousEmission(Identifier id, ExteriorEnum exterior) {
        String originalPathNoPng = id.getPath().substring(0, id.getPath().length() - 4);
        String addedEmission = originalPathNoPng + "_emission.png";
        return new Identifier(AITMod.MOD_ID, addedEmission);
    }

    public abstract Animation getAnimationForDoorState(DoorHandler.DoorStateEnum state);
}

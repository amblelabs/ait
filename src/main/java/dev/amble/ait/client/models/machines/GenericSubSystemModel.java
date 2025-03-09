package dev.amble.ait.client.models.machines;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;

public class GenericSubSystemModel extends SinglePartEntityModel {
    public static final Identifier TEXTURE = AITMod.id("textures/blockentities/machines/subsystem.png");

    private final ModelPart box;

    public GenericSubSystemModel(ModelPart root) {
        this.box = root.getChild("box");
    }
    public GenericSubSystemModel() {
        this(getTexturedModelData().createModel());
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData box = modelPartData.addChild("box", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -4.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(-0.001F)), ModelTransform.pivot(0.0F, 12.0F, 0.0F));

        ModelPartData wires = box.addChild("wires", ModelPartBuilder.create().uv(33, 33).cuboid(-8.0F, -16.0F, 0.0F, 16.0F, 16.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 33).cuboid(0.0F, -16.0F, -8.0F, 0.0F, 16.0F, 16.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 12.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public ModelPart getPart() {
        return box;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}

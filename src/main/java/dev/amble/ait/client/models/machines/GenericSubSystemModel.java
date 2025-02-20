package dev.amble.ait.client.models.machines;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;

public class GenericSubSystemModel extends SinglePartEntityModel {
    public static final Identifier TEXTURE = AITMod.id("textures/blockentities/machines/subsystem.png");

    private final ModelPart main;

    public GenericSubSystemModel(ModelPart root) {
        this.main = root.getChild("bb_main");
    }
    public GenericSubSystemModel() {
        this(getTexturedModelData().createModel());
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData cube_r1 = bb_main.addChild("cube_r1", ModelPartBuilder.create().uv(40, 32).cuboid(0.0F, -8.0F, -11.0F, 0.0F, 16.0F, 22.0F, new Dilation(0.0F)), ModelTransform.of(-0.2211F, -8.0F, -0.2225F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r2 = bb_main.addChild("cube_r2", ModelPartBuilder.create().uv(40, 32).cuboid(0.0F, -8.0F, -11.0F, 0.0F, 16.0F, 22.0F, new Dilation(0.0F)), ModelTransform.of(0.2218F, -8.0F, -0.2218F, 0.0F, 2.3562F, 0.0F));

        ModelPartData cube_r3 = bb_main.addChild("cube_r3", ModelPartBuilder.create().uv(0, 32).cuboid(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(-0.5F)), ModelTransform.of(0.0F, -8.0F, 0.0F, 0.6155F, -0.5236F, -0.1699F));

        ModelPartData cube_r4 = bb_main.addChild("cube_r4", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0071F, -0.1F, 0.0429F, -1.5708F, 0.7854F, 3.1416F));

        ModelPartData cube_r5 = bb_main.addChild("cube_r5", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0071F, -0.1F, 0.0429F, 1.5708F, 0.7854F, 0.0F));

        ModelPartData cube_r6 = bb_main.addChild("cube_r6", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0071F, -0.1F, 0.0429F, 1.5708F, -0.7854F, 0.0F));

        ModelPartData cube_r7 = bb_main.addChild("cube_r7", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0071F, -0.1F, 0.0429F, -1.5708F, -0.7854F, 3.1416F));

        ModelPartData cube_r8 = bb_main.addChild("cube_r8", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0071F, -15.9F, 0.0429F, -1.5708F, -0.7854F, 0.0F));

        ModelPartData cube_r9 = bb_main.addChild("cube_r9", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0071F, -15.9F, 0.0429F, -1.5708F, 0.7854F, 0.0F));

        ModelPartData cube_r10 = bb_main.addChild("cube_r10", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0071F, -15.9F, 0.0429F, 1.5708F, 0.7854F, -3.1416F));

        ModelPartData cube_r11 = bb_main.addChild("cube_r11", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0071F, -15.9F, 0.0429F, 1.5708F, -0.7854F, -3.1416F));

        ModelPartData cube_r12 = bb_main.addChild("cube_r12", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(7.9F, -8.0429F, 0.0071F, -1.5708F, -0.7854F, 1.5708F));

        ModelPartData cube_r13 = bb_main.addChild("cube_r13", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(7.9F, -8.0429F, 0.0071F, -1.5708F, 0.7854F, 1.5708F));

        ModelPartData cube_r14 = bb_main.addChild("cube_r14", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(7.9F, -8.0429F, 0.0071F, 1.5708F, -0.7854F, -1.5708F));

        ModelPartData cube_r15 = bb_main.addChild("cube_r15", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(7.9F, -8.0429F, 0.0071F, 1.5708F, 0.7854F, -1.5708F));

        ModelPartData cube_r16 = bb_main.addChild("cube_r16", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.0071F, -8.0429F, 7.9F, 3.1416F, 0.0F, 2.3562F));

        ModelPartData cube_r17 = bb_main.addChild("cube_r17", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.0071F, -8.0429F, 7.9F, 3.1416F, 0.0F, -0.7854F));

        ModelPartData cube_r18 = bb_main.addChild("cube_r18", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.0071F, -8.0429F, 7.9F, -3.1416F, 0.0F, 0.7854F));

        ModelPartData cube_r19 = bb_main.addChild("cube_r19", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.0071F, -8.0429F, 7.9F, -3.1416F, 0.0F, -2.3562F));

        ModelPartData cube_r20 = bb_main.addChild("cube_r20", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-7.9F, -8.0429F, -0.0071F, -1.5708F, 0.7854F, -1.5708F));

        ModelPartData cube_r21 = bb_main.addChild("cube_r21", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-7.9F, -8.0429F, -0.0071F, 1.5708F, -0.7854F, 1.5708F));

        ModelPartData cube_r22 = bb_main.addChild("cube_r22", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-7.9F, -8.0429F, -0.0071F, -1.5708F, -0.7854F, -1.5708F));

        ModelPartData cube_r23 = bb_main.addChild("cube_r23", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-7.9F, -8.0429F, -0.0071F, 1.5708F, 0.7854F, 1.5708F));

        ModelPartData cube_r24 = bb_main.addChild("cube_r24", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.0929F, -8.0429F, -7.9F, 0.0F, 0.0F, 2.3562F));

        ModelPartData cube_r25 = bb_main.addChild("cube_r25", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.0929F, -8.0429F, -7.9F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r26 = bb_main.addChild("cube_r26", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.0929F, -8.0429F, -7.9F, 0.0F, 0.0F, -2.3562F));

        ModelPartData cube_r27 = bb_main.addChild("cube_r27", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0354F, -10.2177F, 0.0F, 2.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.0929F, -8.0429F, -7.9F, 0.0F, 0.0F, 0.7854F));

        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public ModelPart getPart() {
        return main;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}

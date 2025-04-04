package dev.amble.ait.client.models.entities.hostile.daleks;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import dev.amble.ait.client.animation.entities.daleks.imperial.ImperialDalekAnimations;
import dev.amble.ait.core.entities.DalekEntity;

public class DalekModel<T extends Entity> extends SinglePartEntityModel<T> {
    private final ModelPart body;
    private final ModelPart dome;
    private final ModelPart eyestalk;
    private final ModelPart neck;
    private final ModelPart torso;
    private final ModelPart plunger;
    private final ModelPart gun;
    public DalekModel(ModelPart root) {
        this.body = root.getChild("body");
        this.dome = this.body.getChild("dome");
        this.eyestalk = this.dome.getChild("eyestalk");
        this.neck = this.body.getChild("neck");
        this.torso = this.body.getChild("torso");
        this.plunger = this.torso.getChild("plunger");
        this.gun = this.torso.getChild("gun");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(77, 82).cuboid(6.75F, -9.2071F, -9.325F, -1.0F, 14.0F, 19.0F, new Dilation(0.5F))
                .uv(62, 83).cuboid(-5.75F, -9.2071F, -9.375F, -1.0F, 14.0F, 18.0F, new Dilation(0.5F))
                .uv(0, 80).cuboid(-6.0F, -7.9571F, -10.825F, 12.0F, 14.0F, 19.0F, new Dilation(0.0F))
                .uv(50, 37).cuboid(-7.0F, 7.0429F, -8.325F, 14.0F, 1.0F, 19.0F, new Dilation(0.0F))
                .uv(58, 43).cuboid(-6.0F, -8.9571F, -4.075F, 12.0F, 1.0F, 13.0F, new Dilation(0.1F))
                .uv(50, 37).cuboid(-7.0F, 6.0429F, -8.325F, 14.0F, 1.0F, 19.0F, new Dilation(0.1F))
                .uv(87, 17).cuboid(-7.0F, 7.6429F, -8.325F, 14.0F, 0.0F, 19.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.9571F, -1.125F));

        ModelPartData cube_r1 = body.addChild("cube_r1", ModelPartBuilder.create().uv(3, 34).cuboid(-5.5F, -4.5F, -1.0F, 11.0F, 7.0F, -1.0F, new Dilation(0.5F)), ModelTransform.of(0.0F, -3.6971F, -3.9395F, -0.1309F, 0.0F, 0.0F));

        ModelPartData cube_r2 = body.addChild("cube_r2", ModelPartBuilder.create().uv(3, 34).cuboid(-5.5F, -4.5F, -1.0F, 11.0F, 7.0F, -1.0F, new Dilation(0.5F)), ModelTransform.of(0.0F, 2.8029F, -5.7895F, -0.1309F, 0.0F, 0.0F));

        ModelPartData cube_r3 = body.addChild("cube_r3", ModelPartBuilder.create().uv(15, 3).cuboid(-6.0F, -0.5F, -1.0F, 12.0F, 6.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -7.4971F, -3.7895F, -0.1309F, 0.0F, 0.0F));

        ModelPartData cube_r4 = body.addChild("cube_r4", ModelPartBuilder.create().uv(15, 10).cuboid(-6.0F, -4.5F, -1.0F, 12.0F, 9.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 2.3029F, -6.0895F, -0.1309F, 0.0F, 0.0F));

        ModelPartData cube_r5 = body.addChild("cube_r5", ModelPartBuilder.create().uv(19, 34).cuboid(-5.5F, -10.5F, 2.0F, 11.0F, 17.0F, -1.0F, new Dilation(0.5F)), ModelTransform.of(0.0F, 1.6019F, 8.4198F, 0.0873F, 0.0F, 0.0F));

        ModelPartData cube_r6 = body.addChild("cube_r6", ModelPartBuilder.create().uv(43, 116).cuboid(-6.0F, -8.5F, -1.0F, 12.0F, 15.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -0.3981F, 8.5698F, 0.0873F, 0.0F, 0.0F));

        ModelPartData dome = body.addChild("dome", ModelPartBuilder.create().uv(124, 83).cuboid(-5.5F, 0.4947F, -5.5F, 11.0F, 4.0F, 11.0F, new Dilation(-0.1F))
                .uv(47, 17).cuboid(-5.0F, -1.7053F, -5.0F, 10.0F, 3.0F, 10.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.0F, -23.9268F, 2.575F));

        ModelPartData bulbleft = dome.addChild("bulbleft", ModelPartBuilder.create(), ModelTransform.pivot(4.6464F, -1.1447F, 0.0F));

        ModelPartData cube_r7 = bulbleft.addChild("cube_r7", ModelPartBuilder.create().uv(0, 11).mirrored().cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData leftlight = bulbleft.addChild("leftlight", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r8 = leftlight.addChild("cube_r8", ModelPartBuilder.create().uv(0, 47).mirrored().cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.01F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData bulbright = dome.addChild("bulbright", ModelPartBuilder.create(), ModelTransform.pivot(-4.6464F, -1.1447F, 0.0F));

        ModelPartData cube_r9 = bulbright.addChild("cube_r9", ModelPartBuilder.create().uv(0, 11).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

        ModelPartData rightlight = bulbright.addChild("rightlight", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r10 = rightlight.addChild("cube_r10", ModelPartBuilder.create().uv(0, 47).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

        ModelPartData eyestalk = dome.addChild("eyestalk", ModelPartBuilder.create().uv(39, 30).cuboid(-0.5F, -0.5F, -7.4056F, 1.0F, 1.0F, 7.0F, new Dilation(-0.001F))
                .uv(5, 6).cuboid(-1.0F, -1.0F, -2.3056F, 2.0F, 2.0F, 0.0F, new Dilation(-0.001F))
                .uv(5, 6).cuboid(-1.0F, -1.0F, -3.0556F, 2.0F, 2.0F, 0.0F, new Dilation(-0.01F))
                .uv(5, 6).cuboid(-1.0F, -1.0F, -3.7056F, 2.0F, 2.0F, 0.0F, new Dilation(-0.01F))
                .uv(5, 6).cuboid(-1.0F, -1.0F, -4.4556F, 2.0F, 2.0F, 0.0F, new Dilation(-0.001F))
                .uv(34, 23).cuboid(-1.0F, -1.0F, -7.5556F, 2.0F, 2.0F, 2.0F, new Dilation(0.1F)), ModelTransform.pivot(0.0F, 0.8947F, -5.1086F));

        ModelPartData cube_r11 = eyestalk.addChild("cube_r11", ModelPartBuilder.create().uv(0, 6).cuboid(-0.5F, -4.2577F, 2.2577F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -4.5985F, -0.7854F, 0.0F, 0.0F));

        ModelPartData pupil = eyestalk.addChild("pupil", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, -0.075F, -7.6556F));

        ModelPartData neck = body.addChild("neck", ModelPartBuilder.create().uv(95, 116).cuboid(-5.5F, -2.8614F, -5.5773F, 11.0F, 5.0F, 11.0F, new Dilation(-0.2F))
                .uv(8, 8).cuboid(2.0F, -2.7114F, -5.2773F, 1.0F, 5.0F, 0.0F, new Dilation(0.0F))
                .uv(8, 8).cuboid(-3.0F, -2.7114F, -5.2773F, 1.0F, 5.0F, 0.0F, new Dilation(0.0F))
                .uv(8, 8).cuboid(-3.0F, -2.7114F, 5.1227F, 1.0F, 5.0F, 0.0F, new Dilation(0.0F))
                .uv(8, 8).cuboid(2.0F, -2.7114F, 5.1227F, 1.0F, 5.0F, 0.0F, new Dilation(0.0F))
                .uv(43, 0).cuboid(-5.5F, -3.3364F, -5.5773F, 11.0F, 6.0F, 11.0F, new Dilation(-0.7F))
                .uv(59, 44).cuboid(-6.0F, 1.8886F, -6.0273F, 12.0F, 1.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -16.8458F, 2.6523F));

        ModelPartData cube_r12 = neck.addChild("cube_r12", ModelPartBuilder.create().uv(8, 8).cuboid(2.0F, -2.5F, 0.0F, 1.0F, 5.0F, 0.0F, new Dilation(0.0F))
                .uv(8, 8).cuboid(-3.0F, -2.5F, 0.0F, 1.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-5.2F, -0.2114F, 0.1227F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r13 = neck.addChild("cube_r13", ModelPartBuilder.create().uv(8, 8).cuboid(2.0F, -2.5F, 0.0F, 1.0F, 5.0F, 0.0F, new Dilation(0.0F))
                .uv(8, 8).cuboid(-3.0F, -2.5F, 0.0F, 1.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(5.2F, -0.2114F, 0.1227F, 0.0F, -1.5708F, 0.0F));

        ModelPartData torso = body.addChild("torso", ModelPartBuilder.create().uv(36, 58).cuboid(-6.02F, 9.9697F, -6.05F, 12.0F, 5.0F, 12.0F, new Dilation(0.002F))
                .uv(0, 0).mirrored().cuboid(-5.02F, 10.9697F, -7.35F, 3.0F, 3.0F, 3.0F, new Dilation(0.002F)).mirrored(false)
                .uv(0, 0).cuboid(1.98F, 10.9697F, -7.35F, 3.0F, 3.0F, 3.0F, new Dilation(0.002F)), ModelTransform.pivot(0.02F, -23.9268F, 2.625F));

        ModelPartData cube_r14 = torso.addChild("cube_r14", ModelPartBuilder.create().uv(155, 40).mirrored().cuboid(-6.5F, -22.0F, -3.55F, 1.0F, 2.0F, 2.0F, new Dilation(0.002F)).mirrored(false), ModelTransform.of(0.98F, 33.4697F, -1.05F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r15 = torso.addChild("cube_r15", ModelPartBuilder.create().uv(155, 40).mirrored().cuboid(-6.5F, -22.0F, -3.55F, 1.0F, 2.0F, 2.0F, new Dilation(0.002F)).mirrored(false), ModelTransform.of(-6.02F, 33.4697F, -1.05F, 0.0F, -1.5708F, 0.0F));

        ModelPartData plunger = torso.addChild("plunger", ModelPartBuilder.create().uv(83, 58).cuboid(-0.5F, -0.5009F, -10.3936F, 1.0F, 1.0F, 11.0F, new Dilation(0.0F))
                .uv(26, 23).cuboid(-1.5F, -1.5009F, -11.3936F, 3.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.52F, 12.4697F, -7.0F));

        ModelPartData gun = torso.addChild("gun", ModelPartBuilder.create().uv(38, 38).cuboid(-0.5019F, -0.5009F, -6.3436F, 1.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(3.48F, 12.4697F, -7.05F));

        ModelPartData cube_r16 = gun.addChild("cube_r16", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, -1.0F, -2.1F, 2.0F, 2.0F, 5.0F, new Dilation(-0.1F)), ModelTransform.of(-0.0019F, -0.0009F, -3.7936F, 0.0F, 0.0F, 0.7854F));

        ModelPartData torsopipes = torso.addChild("torsopipes", ModelPartBuilder.create().uv(155, 40).cuboid(4.75F, -5.0F, -1.5F, 1.0F, 4.0F, 2.0F, new Dilation(-0.002F))
                .uv(155, 40).cuboid(4.75F, -5.0F, 1.5F, 1.0F, 4.0F, 2.0F, new Dilation(-0.002F))
                .uv(155, 40).cuboid(4.75F, -5.0F, 4.5F, 1.0F, 4.0F, 2.0F, new Dilation(-0.002F))
                .uv(155, 40).cuboid(4.75F, -5.0F, 7.5F, 1.0F, 4.0F, 2.0F, new Dilation(-0.002F))
                .uv(155, 40).mirrored().cuboid(-6.75F, -5.0F, -1.5F, 1.0F, 4.0F, 2.0F, new Dilation(-0.002F)).mirrored(false)
                .uv(155, 40).mirrored().cuboid(-6.75F, -5.0F, 4.5F, 1.0F, 4.0F, 2.0F, new Dilation(-0.002F)).mirrored(false)
                .uv(155, 40).mirrored().cuboid(-6.75F, -5.0F, 1.5F, 1.0F, 4.0F, 2.0F, new Dilation(-0.002F)).mirrored(false)
                .uv(155, 40).mirrored().cuboid(-6.75F, -5.0F, 7.5F, 1.0F, 4.0F, 2.0F, new Dilation(-0.002F)).mirrored(false), ModelTransform.pivot(0.48F, 14.9697F, -4.05F));

        ModelPartData cube_r17 = torsopipes.addChild("cube_r17", ModelPartBuilder.create().uv(155, 40).mirrored().cuboid(-6.5F, -22.0F, 1.55F, 1.0F, 4.0F, 2.0F, new Dilation(-0.002F)).mirrored(false), ModelTransform.of(1.5F, 17.0F, 3.8F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r18 = torsopipes.addChild("cube_r18", ModelPartBuilder.create().uv(155, 40).mirrored().cuboid(-6.5F, -22.0F, 1.55F, 1.0F, 4.0F, 2.0F, new Dilation(-0.002F)).mirrored(false), ModelTransform.of(-1.5F, 17.0F, 3.8F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r19 = torsopipes.addChild("cube_r19", ModelPartBuilder.create().uv(155, 40).mirrored().cuboid(-6.5F, -22.0F, 1.55F, 1.0F, 4.0F, 2.0F, new Dilation(-0.002F)).mirrored(false), ModelTransform.of(-4.5F, 17.0F, 3.8F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r20 = torsopipes.addChild("cube_r20", ModelPartBuilder.create().uv(155, 40).mirrored().cuboid(-6.5F, -22.0F, 1.55F, 1.0F, 4.0F, 2.0F, new Dilation(-0.002F)).mirrored(false), ModelTransform.of(-7.5F, 17.0F, 3.8F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r21 = torsopipes.addChild("cube_r21", ModelPartBuilder.create().uv(155, 40).mirrored().cuboid(-6.5F, -22.0F, -3.55F, 1.0F, 2.0F, 2.0F, new Dilation(-0.002F)).mirrored(false), ModelTransform.of(1.5F, 17.0F, 4.3F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r22 = torsopipes.addChild("cube_r22", ModelPartBuilder.create().uv(155, 40).mirrored().cuboid(-6.5F, -22.0F, -3.55F, 1.0F, 2.0F, 2.0F, new Dilation(-0.002F)).mirrored(false), ModelTransform.of(-1.5F, 17.0F, 4.3F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r23 = torsopipes.addChild("cube_r23", ModelPartBuilder.create().uv(155, 40).mirrored().cuboid(-6.5F, -22.0F, -3.55F, 1.0F, 2.0F, 2.0F, new Dilation(-0.002F)).mirrored(false), ModelTransform.of(-4.5F, 17.0F, 4.3F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r24 = torsopipes.addChild("cube_r24", ModelPartBuilder.create().uv(155, 40).mirrored().cuboid(-6.5F, -22.0F, -3.55F, 1.0F, 2.0F, 2.0F, new Dilation(-0.002F)).mirrored(false), ModelTransform.of(-7.5F, 17.0F, 4.3F, 0.0F, -1.5708F, 0.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.getPart().render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return body;
    }

    @Override
    public void setAngles(T dalekEntity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.setHeadAndArmsAngles(headYaw, headPitch, limbAngle, limbDistance);
        this.lightInfo(dalekEntity);
        torso.yaw = MathHelper.cos(limbAngle * 0.6662f) * 0.4f * limbDistance;
        this.animateMovement(ImperialDalekAnimations.MOVING, limbAngle, limbDistance, ImperialDalekAnimations.MOVING.lengthInSeconds(), 1);
        this.updateAnimation(((DalekEntity)dalekEntity).startMovingTransitionState, ImperialDalekAnimations.START_MOVING_TRANSITION, animationProgress, 1.0f);
        this.updateAnimation(((DalekEntity)dalekEntity).stopMovingTransitionState, ImperialDalekAnimations.STOP_MOVING_TRANSITION, animationProgress, 1.0f);
        this.updateAnimation(((DalekEntity)dalekEntity).exterminateAnimationState, ImperialDalekAnimations.IMPERIAL_EXTERMINATE, animationProgress, 1.0f);
        this.updateAnimation(((DalekEntity)dalekEntity).exterminateAltAnimationState, ImperialDalekAnimations.IMPERIAL_EXTERMINATE_ALT, animationProgress, 1.0f);
        this.updateAnimation(((DalekEntity)dalekEntity).aimAnimationState, ImperialDalekAnimations.AIM, animationProgress, 1.0f);
        this.updateAnimation(((DalekEntity)dalekEntity).yellStayAnimationState, ImperialDalekAnimations.STAY_COMMAND, animationProgress, 1.0f);
        this.updateAnimation(((DalekEntity)dalekEntity).yellDoNotMoveAnimationState, ImperialDalekAnimations.DONT_MOVE_COMMAND, animationProgress, 1.0f);
    }

    private void setHeadAndArmsAngles(float headYaw, float headPitch, float limbAngle, float limbDistance) {
        dome.yaw = headYaw * (float) (Math.PI / 180);
        eyestalk.pitch = headPitch * (float) (Math.PI / 180);
        gun.pitch = MathHelper.cos(limbAngle * 0.6662f) * 0.4f * limbDistance;
        plunger.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 0.4f * limbDistance;
    }

    private void lightInfo(T dalekEntity) {
        boolean bl = ((DalekEntity) dalekEntity).isSpeaking();
        dome.getChild("bulbleft").getChild("leftlight").visible = bl;
        dome.getChild("bulbright").getChild("rightlight").visible = bl;
    }
}
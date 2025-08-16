package dev.amble.ait.client.models.machines;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class FoodMachineModel extends SinglePartEntityModel {
    private final ModelPart root;
    private final ModelPart pannel;
    private final ModelPart bone;
    private final ModelPart dial2;
    private final ModelPart dial;
    private final ModelPart water;
    private final ModelPart milk;
    private final ModelPart select_dial;
    private final ModelPart spiny_thing2;
    private final ModelPart water2;
    private final ModelPart spiny_thing;
    private final ModelPart door_slider;
    private final ModelPart bb_main;
    public FoodMachineModel(ModelPart root) {
        this.root = root;
        this.pannel = root.getChild("pannel");
        this.bone = this.pannel.getChild("bone");
        this.dial2 = this.bone.getChild("dial2");
        this.dial = this.bone.getChild("dial");
        this.water = this.bone.getChild("water");
        this.milk = this.bone.getChild("milk");
        this.select_dial = this.bone.getChild("select_dial");
        this.spiny_thing2 = this.bone.getChild("spiny_thing2");
        this.water2 = this.bone.getChild("water2");
        this.spiny_thing = this.bone.getChild("spiny_thing");
        this.door_slider = this.pannel.getChild("door_slider");
        this.bb_main = root.getChild("bb_main");
    }

    public FoodMachineModel(ModelPart root, ModelPart pannel, ModelPart bone, ModelPart dial2, ModelPart dial, ModelPart water, ModelPart milk, ModelPart selectDial, ModelPart spinyThing2, ModelPart water2, ModelPart spinyThing, ModelPart doorSlider, ModelPart bbMain) {
        this.root = root;
        this.pannel = pannel;
        this.bone = bone;
        this.dial2 = dial2;
        this.dial = dial;
        this.water = water;
        this.milk = milk;
        select_dial = selectDial;
        spiny_thing2 = spinyThing2;
        this.water2 = water2;
        spiny_thing = spinyThing;
        door_slider = doorSlider;
        bb_main = bbMain;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData pannel = modelPartData.addChild("pannel", ModelPartBuilder.create().uv(62, 2).cuboid(-0.5F, 3.5F, -43.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-6.0F, -2.7F, -42.9F, 12.0F, 13.0F, 12.0F, new Dilation(0.0F))
                .uv(0, 26).cuboid(-6.0F, -14.7F, -41.9F, 0.0F, 12.0F, 11.0F, new Dilation(0.0F))
                .uv(0, 26).mirrored().cuboid(6.0F, -14.7F, -41.9F, 0.0F, 12.0F, 11.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 77).cuboid(-5.997F, -4.7F, -41.9F, 0.0F, 2.0F, 11.0F, new Dilation(0.0F))
                .uv(78, 49).cuboid(-4.0F, -1.7F, -42.9F, 4.0F, 3.0F, 3.0F, new Dilation(0.0F))
                .uv(36, 49).cuboid(-6.0F, -14.7F, -30.9F, 12.0F, 12.0F, 0.0F, new Dilation(0.0F))
                .uv(1, 50).cuboid(-6.0F, -14.7F, -35.9F, 12.0F, 0.0F, 5.0F, new Dilation(0.0F))
                .uv(0, 77).cuboid(5.998F, -4.7F, -41.9F, 0.0F, 2.0F, 11.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 13.7F, 36.9F));

        ModelPartData cube_r1 = pannel.addChild("cube_r1", ModelPartBuilder.create().uv(0, 55).cuboid(-6.0F, 0.0F, 0.0F, 12.0F, 14.0F, 3.0F, new Dilation(-0.001F)), ModelTransform.of(0.0F, -14.701F, -35.9F, -0.5236F, 0.0F, 0.0F));

        ModelPartData bone = pannel.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -14.7067F, -35.8873F));

        ModelPartData cube_r2 = bone.addChild("cube_r2", ModelPartBuilder.create().uv(64, 26).cuboid(-5.0F, -14.0F, 0.0F, 10.0F, 14.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 12.0567F, -7.0127F, -0.5236F, 0.0F, 0.0F));

        ModelPartData cube_r3 = bone.addChild("cube_r3", ModelPartBuilder.create().uv(16, 25).cuboid(-4.0F, -11.2F, -0.6F, 8.0F, 4.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 25).cuboid(-4.0F, -11.2F, -0.4F, 8.0F, 4.0F, 0.0F, new Dilation(0.0F))
                .uv(64, 17).cuboid(-4.0F, -11.2F, -0.3F, 8.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 12.0567F, -6.7627F, -0.5236F, 0.0F, 0.0F));

        ModelPartData dial2 = bone.addChild("dial2", ModelPartBuilder.create(), ModelTransform.pivot(2.5F, 11.7567F, -4.0127F));

        ModelPartData cube_r4 = dial2.addChild("cube_r4", ModelPartBuilder.create().uv(0, 0).mirrored().cuboid(2.0F, -7.0F, -0.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
                .uv(48, 44).mirrored().cuboid(2.0F, -6.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-2.5F, 2.7F, -4.1F, -0.5236F, 0.0F, 0.0F));

        ModelPartData dial = bone.addChild("dial", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 16.4567F, -8.1127F));

        ModelPartData cube_r5 = dial.addChild("cube_r5", ModelPartBuilder.create().uv(48, 44).cuboid(-3.0F, -6.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        ModelPartData cube_r6 = dial.addChild("cube_r6", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, -7.4461F, 3.567F, -0.5236F, 0.0F, 0.0F));

        ModelPartData water = bone.addChild("water", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 16.4567F, -8.1127F));

        ModelPartData cube_r7 = water.addChild("cube_r7", ModelPartBuilder.create().uv(54, 44).cuboid(-0.5F, -7.5F, -0.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, -2.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        ModelPartData milk = bone.addChild("milk", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 16.4567F, -8.1127F));

        ModelPartData cube_r8 = milk.addChild("cube_r8", ModelPartBuilder.create().uv(0, 3).cuboid(-0.5F, -4.3F, -0.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, -2.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        ModelPartData select_dial = bone.addChild("select_dial", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 8.9321F, -4.3457F));

        ModelPartData cube_r9 = select_dial.addChild("cube_r9", ModelPartBuilder.create().uv(3, 0).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.2F))
                .uv(48, 44).cuboid(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, -2.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        ModelPartData spiny_thing2 = bone.addChild("spiny_thing2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 16.4567F, -8.1127F));

        ModelPartData cube_r10 = spiny_thing2.addChild("cube_r10", ModelPartBuilder.create().uv(48, 40).cuboid(-4.5F, -7.5F, -0.4F, 4.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        ModelPartData water2 = bone.addChild("water2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 16.4567F, -8.1127F));

        ModelPartData cube_r11 = water2.addChild("cube_r11", ModelPartBuilder.create().uv(0, 1).cuboid(-4.2F, -8.8F, -0.4F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        ModelPartData spiny_thing = bone.addChild("spiny_thing", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 16.4567F, -8.1127F));

        ModelPartData cube_r12 = spiny_thing.addChild("cube_r12", ModelPartBuilder.create().uv(48, 34).mirrored().cuboid(0.5F, -7.5F, -0.4F, 4.0F, 4.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -2.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        ModelPartData door_slider = pannel.addChild("door_slider", ModelPartBuilder.create().uv(63, 2).cuboid(-4.7F, -11.1F, -50.1F, 4.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 10.3F, 7.0F));

        ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(71, 0).cuboid(-6.0F, -13.1F, -6.1F, 12.0F, 13.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        pannel.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        bb_main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}
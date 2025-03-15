package dev.amble.ait.client.models.consoles;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class ConsoleGeneratorModel extends SinglePartEntityModel {
    private final ModelPart bone7;

    public ConsoleGeneratorModel(ModelPart root) {
        this.bone7 = root.getChild("bone7");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone7 = modelPartData.addChild("bone7", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData bone = bone7.addChild("bone",
                ModelPartBuilder.create().uv(20, 16).cuboid(9.25F, -3.0F, -6.5F, 2.0F, 3.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone11 = bone.addChild("bone11",
                ModelPartBuilder.create().uv(20, 16).cuboid(9.25F, -3.0F, -6.5F, 2.0F, 3.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone12 = bone11.addChild("bone12",
                ModelPartBuilder.create().uv(20, 16).cuboid(9.25F, -3.0F, -6.5F, 2.0F, 3.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone13 = bone12.addChild("bone13",
                ModelPartBuilder.create().uv(20, 16).cuboid(9.25F, -3.0F, -6.5F, 2.0F, 3.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone15 = bone13.addChild("bone15",
                ModelPartBuilder.create().uv(20, 16).cuboid(9.25F, -3.0F, -6.5F, 2.0F, 3.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone16 = bone15.addChild("bone16",
                ModelPartBuilder.create().uv(20, 16).cuboid(9.25F, -3.0F, -6.5F, 2.0F, 3.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone8 = bone7.addChild("bone8",
                ModelPartBuilder.create().uv(0, 0).cuboid(-0.45F, -1.0F, -5.5F, 10.0F, 1.0F, 11.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -0.1F, 0.0F));

        ModelPartData bone18 = bone8.addChild("bone18", ModelPartBuilder.create().uv(0, 0).cuboid(-0.45F, -1.0F, -5.5F,
                10.0F, 1.0F, 11.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone19 = bone18.addChild("bone19", ModelPartBuilder.create().uv(0, 0).cuboid(-0.45F, -1.0F, -5.5F,
                10.0F, 1.0F, 11.0F, new Dilation(0.002F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone23 = bone19.addChild("bone23", ModelPartBuilder.create().uv(0, 0).cuboid(-0.45F, -1.0F, -5.5F,
                10.0F, 1.0F, 11.0F, new Dilation(0.003F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone24 = bone23.addChild("bone24", ModelPartBuilder.create().uv(0, 0).cuboid(-0.45F, -1.0F, -5.5F,
                10.0F, 1.0F, 11.0F, new Dilation(0.004F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone25 = bone24.addChild("bone25", ModelPartBuilder.create().uv(0, 0).cuboid(-0.45F, -1.0F, -5.5F,
                10.0F, 1.0F, 11.0F, new Dilation(0.005F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone14 = bone7.addChild("bone14",
                ModelPartBuilder.create().uv(0, 13).cuboid(11.95F, 0.0F, -7.5F, 1.0F, 0.0F, 15.0F, new Dilation(0.01F)),
                ModelTransform.pivot(0.0F, -0.1F, 0.0F));

        ModelPartData bone3 = bone14.addChild("bone3",
                ModelPartBuilder.create().uv(0, 13).cuboid(11.95F, 0.0F, -7.5F, 1.0F, 0.0F, 15.0F, new Dilation(0.01F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone4 = bone3.addChild("bone4",
                ModelPartBuilder.create().uv(0, 13).cuboid(11.95F, 0.0F, -7.5F, 1.0F, 0.0F, 15.0F, new Dilation(0.01F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone5 = bone4.addChild("bone5",
                ModelPartBuilder.create().uv(0, 13).cuboid(11.95F, 0.0F, -7.5F, 1.0F, 0.0F, 15.0F, new Dilation(0.01F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone6 = bone5.addChild("bone6",
                ModelPartBuilder.create().uv(0, 13).cuboid(11.95F, 0.0F, -7.5F, 1.0F, 0.0F, 15.0F, new Dilation(0.01F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone9 = bone6.addChild("bone9",
                ModelPartBuilder.create().uv(0, 13).cuboid(11.95F, 0.0F, -7.5F, 1.0F, 0.0F, 15.0F, new Dilation(0.01F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone84 = bone7.addChild("bone84", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone20 = bone84.addChild("bone20", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData bone21 = bone20.addChild("bone21",
                ModelPartBuilder.create().uv(32, 0).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                        .uv(0, 0).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.of(8.4F, -1.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

        ModelPartData bone22 = bone21.addChild("bone22",
                ModelPartBuilder.create().uv(32, 33).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                        .uv(23, 33).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.of(0.0F, -7.0F, 0.0F, 0.0F, 0.0F, -1.1345F));

        ModelPartData bone69 = bone84.addChild("bone69", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone70 = bone69.addChild("bone70",
                ModelPartBuilder.create().uv(32, 0).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                        .uv(0, 0).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.of(8.4F, -1.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

        ModelPartData bone71 = bone70.addChild("bone71",
                ModelPartBuilder.create().uv(32, 33).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                        .uv(23, 33).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.of(0.0F, -7.0F, 0.0F, 0.0F, 0.0F, -1.1345F));

        ModelPartData bone72 = bone84.addChild("bone72", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData bone73 = bone72.addChild("bone73",
                ModelPartBuilder.create().uv(32, 0).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                        .uv(0, 0).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.of(8.4F, -1.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

        ModelPartData bone74 = bone73.addChild("bone74",
                ModelPartBuilder.create().uv(32, 33).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                        .uv(23, 33).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.of(0.0F, -7.0F, 0.0F, 0.0F, 0.0F, -1.1345F));

        ModelPartData bone75 = bone84.addChild("bone75", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.618F, 0.0F));

        ModelPartData bone76 = bone75.addChild("bone76",
                ModelPartBuilder.create().uv(32, 0).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                        .uv(0, 0).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.of(8.4F, -1.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

        ModelPartData bone77 = bone76.addChild("bone77",
                ModelPartBuilder.create().uv(32, 33).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                        .uv(23, 33).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.of(0.0F, -7.0F, 0.0F, 0.0F, 0.0F, -1.1345F));

        ModelPartData bone78 = bone84.addChild("bone78", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.618F, 0.0F));

        ModelPartData bone79 = bone78.addChild("bone79",
                ModelPartBuilder.create().uv(32, 0).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                        .uv(0, 0).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.of(8.4F, -1.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

        ModelPartData bone80 = bone79.addChild("bone80",
                ModelPartBuilder.create().uv(32, 33).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                        .uv(23, 33).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.of(0.0F, -7.0F, 0.0F, 0.0F, 0.0F, -1.1345F));

        ModelPartData bone81 = bone84.addChild("bone81", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData bone82 = bone81.addChild("bone82",
                ModelPartBuilder.create().uv(32, 0).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                        .uv(0, 0).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.of(8.4F, -1.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

        ModelPartData bone83 = bone82.addChild("bone83",
                ModelPartBuilder.create().uv(32, 33).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F))
                        .uv(23, 33).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.of(0.0F, -7.0F, 0.0F, 0.0F, 0.0F, -1.1345F));

        ModelPartData bone2 = bone7.addChild("bone2",
                ModelPartBuilder.create().uv(0, 29).cuboid(5.8F, -2.0F, -4.5F, 2.0F, 1.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone27 = bone2.addChild("bone27",
                ModelPartBuilder.create().uv(0, 29).cuboid(5.8F, -2.0F, -4.5F, 2.0F, 1.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone28 = bone27.addChild("bone28",
                ModelPartBuilder.create().uv(0, 29).cuboid(5.8F, -2.0F, -4.5F, 2.0F, 1.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone29 = bone28.addChild("bone29",
                ModelPartBuilder.create().uv(0, 29).cuboid(5.8F, -2.0F, -4.5F, 2.0F, 1.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone30 = bone29.addChild("bone30",
                ModelPartBuilder.create().uv(0, 29).cuboid(5.8F, -2.0F, -4.5F, 2.0F, 1.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone31 = bone30.addChild("bone31",
                ModelPartBuilder.create().uv(0, 29).cuboid(5.8F, -2.0F, -4.5F, 2.0F, 1.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone10 = bone7.addChild("bone10",
                ModelPartBuilder.create().uv(0, 13).cuboid(6.05F, -3.0F, -3.5F, 0.0F, 1.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone33 = bone10.addChild("bone33",
                ModelPartBuilder.create().uv(0, 13).cuboid(6.05F, -3.0F, -3.5F, 0.0F, 1.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone34 = bone33.addChild("bone34",
                ModelPartBuilder.create().uv(0, 13).cuboid(6.05F, -3.0F, -3.5F, 0.0F, 1.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone35 = bone34.addChild("bone35",
                ModelPartBuilder.create().uv(0, 13).cuboid(6.05F, -3.0F, -3.5F, 0.0F, 1.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone36 = bone35.addChild("bone36",
                ModelPartBuilder.create().uv(0, 13).cuboid(6.05F, -3.0F, -3.5F, 0.0F, 1.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone37 = bone36.addChild("bone37",
                ModelPartBuilder.create().uv(0, 13).cuboid(6.05F, -3.0F, -3.5F, 0.0F, 1.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone50 = bone7.addChild("bone50", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone17 = bone50.addChild("bone17",
                ModelPartBuilder.create().uv(18, 15).cuboid(5.2F, -3.0F, -3.0F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone39 = bone17.addChild("bone39",
                ModelPartBuilder.create().uv(18, 15).cuboid(5.2F, -3.0F, -3.0F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone40 = bone39.addChild("bone40",
                ModelPartBuilder.create().uv(18, 15).cuboid(5.2F, -3.0F, -3.0F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone41 = bone40.addChild("bone41",
                ModelPartBuilder.create().uv(18, 15).cuboid(5.2F, -3.0F, -3.0F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone42 = bone41.addChild("bone42",
                ModelPartBuilder.create().uv(18, 15).cuboid(5.2F, -3.0F, -3.0F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone43 = bone42.addChild("bone43",
                ModelPartBuilder.create().uv(18, 15).cuboid(5.2F, -3.0F, -3.0F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone26 = bone50.addChild("bone26",
                ModelPartBuilder.create().uv(38, 15).cuboid(4.3F, -3.0F, -2.5F, 0.0F, 1.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone45 = bone26.addChild("bone45",
                ModelPartBuilder.create().uv(38, 15).cuboid(4.3F, -3.0F, -2.5F, 0.0F, 1.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone46 = bone45.addChild("bone46",
                ModelPartBuilder.create().uv(38, 15).cuboid(4.3F, -3.0F, -2.5F, 0.0F, 1.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone47 = bone46.addChild("bone47",
                ModelPartBuilder.create().uv(38, 15).cuboid(4.3F, -3.0F, -2.5F, 0.0F, 1.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone48 = bone47.addChild("bone48",
                ModelPartBuilder.create().uv(38, 15).cuboid(4.3F, -3.0F, -2.5F, 0.0F, 1.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone49 = bone48.addChild("bone49",
                ModelPartBuilder.create().uv(38, 15).cuboid(4.3F, -3.0F, -2.5F, 0.0F, 1.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone38 = bone50.addChild("bone38",
                ModelPartBuilder.create().uv(0, 22).cuboid(3.45F, -3.0F, -2.0F, 0.0F, 1.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone52 = bone38.addChild("bone52",
                ModelPartBuilder.create().uv(0, 22).cuboid(3.45F, -3.0F, -2.0F, 0.0F, 1.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone53 = bone52.addChild("bone53",
                ModelPartBuilder.create().uv(0, 22).cuboid(3.45F, -3.0F, -2.0F, 0.0F, 1.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone54 = bone53.addChild("bone54",
                ModelPartBuilder.create().uv(0, 22).cuboid(3.45F, -3.0F, -2.0F, 0.0F, 1.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone55 = bone54.addChild("bone55",
                ModelPartBuilder.create().uv(0, 22).cuboid(3.45F, -3.0F, -2.0F, 0.0F, 1.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone56 = bone55.addChild("bone56",
                ModelPartBuilder.create().uv(0, 22).cuboid(3.45F, -3.0F, -2.0F, 0.0F, 1.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone44 = bone50.addChild("bone44",
                ModelPartBuilder.create().uv(0, 13).cuboid(2.6F, -3.0F, -1.5F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone57 = bone44.addChild("bone57",
                ModelPartBuilder.create().uv(0, 13).cuboid(2.6F, -3.0F, -1.5F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone58 = bone57.addChild("bone58",
                ModelPartBuilder.create().uv(0, 13).cuboid(2.6F, -3.0F, -1.5F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone59 = bone58.addChild("bone59",
                ModelPartBuilder.create().uv(0, 13).cuboid(2.6F, -3.0F, -1.5F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone60 = bone59.addChild("bone60",
                ModelPartBuilder.create().uv(0, 13).cuboid(2.6F, -3.0F, -1.5F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone61 = bone60.addChild("bone61",
                ModelPartBuilder.create().uv(0, 13).cuboid(2.6F, -3.0F, -1.5F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone32 = bone50.addChild("bone32",
                ModelPartBuilder.create().uv(5, 22).cuboid(-0.3F, -3.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone62 = bone32.addChild("bone62",
                ModelPartBuilder.create().uv(5, 22).cuboid(-0.3F, -3.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone63 = bone62.addChild("bone63",
                ModelPartBuilder.create().uv(5, 22).cuboid(-0.3F, -3.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone64 = bone63.addChild("bone64",
                ModelPartBuilder.create().uv(5, 22).cuboid(-0.3F, -3.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone65 = bone64.addChild("bone65",
                ModelPartBuilder.create().uv(5, 22).cuboid(-0.3F, -3.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone66 = bone65.addChild("bone66",
                ModelPartBuilder.create().uv(5, 22).cuboid(-0.3F, -3.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone51 = bone50.addChild("bone51",
                ModelPartBuilder.create().uv(18, 13).cuboid(-7.0F, -3.0F, 0.0F, 14.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData bone67 = bone51.addChild("bone67",
                ModelPartBuilder.create().uv(18, 13).cuboid(-7.0F, -3.0F, 0.0F, 14.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone68 = bone67.addChild("bone68",
                ModelPartBuilder.create().uv(18, 13).cuboid(-7.0F, -3.0F, 0.0F, 14.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        matrices.push();

        bone7.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);

        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return bone7;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
            float headPitch) {
    }
}

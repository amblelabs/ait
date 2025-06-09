package dev.amble.ait.client.models.items;

import dev.amble.ait.AITMod;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;

public class HandlesModel extends Model {
    public static final Identifier TEXTURE = AITMod.id("textures/blockentities/items/handles.png");
    public static final Identifier EMISSION = AITMod.id("textures/blockentities/items/handles_emission.png");
    public static final Identifier MOUTH = AITMod.id("textures/blockentities/items/handles_mouth.png");
    public final ModelPart handles;
    public HandlesModel(ModelPart root) {
        super(RenderLayer::getEntityCutout);
        this.handles = root.getChild("handles");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData handles = modelPartData.addChild("handles", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData stalk = handles.addChild("stalk", ModelPartBuilder.create().uv(32, 10).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = stalk.addChild("cube_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -0.5F, -1.0F, 4.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(2.5355F, -8.0F, -1.1213F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r2 = stalk.addChild("cube_r2", ModelPartBuilder.create().uv(0, 16).cuboid(-1.8F, -4.7F, 1.2F, 4.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.7041F, -4.0F, 2.6749F, 0.0F, -1.7453F, 0.0F));

        ModelPartData cube_r3 = stalk.addChild("cube_r3", ModelPartBuilder.create().uv(0, 16).cuboid(-1.8F, -4.7F, 1.2F, 4.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-2.281F, -3.8F, 2.7433F, 0.0F, -2.7925F, 0.0F));

        ModelPartData cube_r4 = stalk.addChild("cube_r4", ModelPartBuilder.create().uv(0, 16).cuboid(-1.7F, -4.0F, 0.0F, 4.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(2.8478F, -4.0F, 0.2346F, 0.0F, 0.3927F, 0.0F));

        ModelPartData cube_r5 = stalk.addChild("cube_r5", ModelPartBuilder.create().uv(0, 16).cuboid(6.4F, 0.0F, -0.1F, 4.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-2.3722F, -8.0F, 4.926F, 0.0F, 1.1781F, 0.0F));

        ModelPartData cube_r6 = stalk.addChild("cube_r6", ModelPartBuilder.create().uv(0, 0).mirrored().cuboid(6.0F, 0.0F, -1.0F, 4.0F, 8.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-2.5362F, -8.0F, 3.949F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r7 = stalk.addChild("cube_r7", ModelPartBuilder.create().uv(0, 0).mirrored().cuboid(6.0F, 0.0F, -1.0F, 4.0F, 8.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-3.9497F, -8.0F, -2.5355F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r8 = stalk.addChild("cube_r8", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, 0.0F, -1.0F, 4.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-2.4142F, -8.0F, -1.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData head = stalk.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 16).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.01F))
                .uv(0, 32).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.11F))
                .uv(32, 0).cuboid(-6.0F, -10.0F, 0.0F, 12.0F, 10.0F, 0.0F, new Dilation(0.0F))
                .uv(24, 16).cuboid(-1.0F, -10.0F, -2.0F, 2.0F, 3.0F, 4.0F, new Dilation(0.0F))
                .uv(32, 23).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.5F)), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

        ModelPartData cube_r9 = head.addChild("cube_r9", ModelPartBuilder.create().uv(24, 0).cuboid(-1.0F, -0.3F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.4F, -7.5F, -4.6F, 0.0F, 0.0F, 0.829F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    public void setAngles(MatrixStack matrices, ModelTransformationMode renderMode, boolean left) {
        if (renderMode == ModelTransformationMode.FIXED)
            return;
        matrices.translate(0.5, -1.25f, -0.5);
        matrices.scale(0.6f, 0.6f, 0.6f);

        if (renderMode == ModelTransformationMode.GUI) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(22.5f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45f));
            matrices.translate(0, 0.3f, 0);
            matrices.scale(1.2f, 1.2f, 1.2f);
        }

        if (renderMode == ModelTransformationMode.HEAD) {
            matrices.translate(0, -0.725f, 0);
            matrices.scale(2.725f, 2.725f, 2.725f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        handles.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    public void render(@Nullable ClientWorld world, @Nullable LivingEntity entity, ItemStack stack, MatrixStack matrices, VertexConsumerProvider provider, int light, int overlay, int seed) {
        this.render(matrices, provider.getBuffer(RenderLayer.getEntityCutout(TEXTURE)), light, overlay, 1, 1, 1, 1);
        this.render(matrices, provider.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(EMISSION)), 0xf000f0, overlay, 1, 1, 1, 1);
        if (entity instanceof PlayerEntity player && player.isSneaking())
            this.render(matrices, provider.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(MOUTH)), 0xf000f0, overlay, 1, 1, 1, 1);
    }
}
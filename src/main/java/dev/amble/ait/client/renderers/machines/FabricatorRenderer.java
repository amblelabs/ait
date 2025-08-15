package dev.amble.ait.client.renderers.machines;

import org.joml.Vector3f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.profiler.Profiler;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.machines.FabricatorModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.core.blockentities.FabricatorBlockEntity;
import dev.amble.ait.core.blocks.FabricatorBlock;
import dev.amble.ait.core.item.blueprint.Blueprint;

public class FabricatorRenderer<T extends FabricatorBlockEntity> implements BlockEntityRenderer<T> {

    public static final Identifier FABRICATOR_TEXTURE = AITMod.id("textures/block/fabricator.png");
    public static final Identifier EMISSIVE_FABRICATOR_TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/block/fabricator_emission.png");
    private final FabricatorModel fabricatorModel;

    public FabricatorRenderer(BlockEntityRendererFactory.Context ctx) {
        this.fabricatorModel = new FabricatorModel(FabricatorModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(FabricatorBlockEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Profiler profiler = entity.getWorld().getProfiler();
        profiler.push("fabricator");

        matrices.push();
        matrices.translate(0.5f, 1.5f, 0.5f);

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrices.multiply(RotationAxis.POSITIVE_Y
                .rotationDegrees(entity.getCachedState().get(FabricatorBlock.FACING).asRotation()));

        this.fabricatorModel.render(matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(FABRICATOR_TEXTURE)), light, overlay, 1.0F,
                1.0F, 1.0F, 1.0F);

        if (entity.isValid()) {
            this.fabricatorModel.render(matrices,
                    vertexConsumers.getBuffer(AITRenderLayers.tardisEmissiveCullZOffset(EMISSIVE_FABRICATOR_TEXTURE, true)), 0xf000f0, overlay, 1.0F,
                    1.0F, 1.0F, 1.0F);
        }

        matrices.pop();
        matrices.push();

        ItemStack stack = entity.getShowcaseStack();

        // Apply the same rotation as the block
        matrices.translate(0.5, 1.5, 0.5);
        float rotation = entity.getCachedState().get(FabricatorBlock.FACING).asRotation();
        if (entity.getCachedState().get(FabricatorBlock.FACING) == Direction.NORTH ||
                entity.getCachedState().get(FabricatorBlock.FACING) == Direction.SOUTH) {
            rotation += 180;
        }
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        matrices.translate(-0.5, -1.5, -0.5);

        if (!stack.isEmpty()) {
            matrices.push();
            double offset = Math.sin((entity.getWorld().getTime() + tickDelta) / 8.0) / 18.0;

            matrices.translate(0.5f, 0.35f + (offset / 2), 0.5f);

            Vector3f scale = MinecraftClient.getInstance().getItemRenderer().getModel(stack, entity.getWorld(), null, 0).getTransformation().firstPersonRightHand.scale;
            matrices.scale(0.7f, 0.7f, 0.7f);
            matrices.scale(scale.x, scale.y, scale.z);

            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, 0xf000f0,
                    overlay, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
        }
        renderText(entity, tickDelta, matrices, vertexConsumers, light, overlay);

        matrices.pop();
        profiler.pop();
    }

    private void renderText(FabricatorBlockEntity entity, float tickDelta, MatrixStack matrices,
                            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
        matrices.push();

        matrices.translate(0.93, 0.1255, 0.315);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));
        matrices.scale(0.005f, 0.005f, 0.005f);

        // display "PRESS TO CRAFT" if enough materials
        Text text = Text.literal("COLLECT OUTPUT");

        // if does not have blueprint, text is "INSERT BLUEPRINT"
        if (!entity.hasBlueprint()) {
            text = Text.literal("INSERT BLUEPRINT");
        }

        Blueprint print = entity.getBlueprint().orElse(null);
        ItemStack stack = entity.getShowcaseStack();
        // display "INSERT (COUNT) MATERIAL" if not enough materials
        if (print != null && !print.isComplete()) {
            text = Text.literal("INSERT " + print.getCountLeftFor(stack) + " " + Text.translatable(stack.getTranslationKey()).getString().toUpperCase());
        }

        renderer.drawWithOutline(text.asOrderedText(), 0, 40, 0x60eaf0, 0x108fb3,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        matrices.pop();
    }
}

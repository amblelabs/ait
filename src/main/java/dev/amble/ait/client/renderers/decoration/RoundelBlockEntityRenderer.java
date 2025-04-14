package dev.amble.ait.client.renderers.decoration;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;

import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.core.blockentities.RoundelBlockEntity;
import dev.amble.ait.core.blocks.RoundelBlock;
import dev.amble.ait.core.roundels.RoundelPattern;
import dev.amble.ait.core.roundels.RoundelPatterns;

@Environment(value=EnvType.CLIENT)
public class RoundelBlockEntityRenderer
        implements BlockEntityRenderer<RoundelBlockEntity> {
    private static final Random dummy = Random.create();
    private final ModelPart cube;
    BakedModel model;
    public RoundelBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.cube = getTexturedModelData().createModel();
        this.model = getBlockModel(Blocks.STONE.getDefaultState());
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData cube = modelPartData.addChild("cube", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.0F, 0.0F));
        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public void render(RoundelBlockEntity roundelBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        Profiler profiler = roundelBlockEntity.getWorld().getProfiler();
        profiler.push("roundel");

        List<Pair<RoundelPattern, DyeColor>> list = roundelBlockEntity.getPatterns();
        matrixStack.push();
        matrixStack.translate(0.5f, 1, 0.5f);

        matrixStack.translate(0, 0, 0.5f);

        profiler.swap("render");
        this.renderBlock(roundelBlockEntity, this.cube, matrixStack, vertexConsumerProvider, i, j, list);
        matrixStack.pop();
        profiler.pop();
    }

    public void renderBlock(RoundelBlockEntity roundelBlockEntity, ModelPart modelPart, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, List<Pair<RoundelPattern, DyeColor>> patterns) {
        //modelPart.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(patterns.get(0).getFirst().texture())), light, overlay, 1f, 1f, 1f, 1.0f);
        BlockState stateOf = roundelBlockEntity.getDynamicTextureBlockState();
        /*if (this.model == null || !this.model.equals(getBlockModel(stateOf))) {
            this.model = getBlockModel(stateOf);
            return;
        }*/



        for (int i = 0; i < 17 && i < patterns.size(); ++i) {
            Pair<RoundelPattern, DyeColor> pair = patterns.get(i);
            float[] fs = pair.getSecond().getColorComponents();
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(pair.getFirst().texture()));
            if (pair.getFirst().equals(RoundelPatterns.BASE)) {
                /*Identifier dynamicTex = pair.getFirst().usesDynamicTexture() ?
                        Registries.BLOCK.getId(Blocks.BLACK_STAINED_GLASS).withPrefixedPath("textures/block/").withSuffixedPath(".png") :
                        pair.getFirst().texture();*/

                matrices.push();
                matrices.translate(0, 0.001, 0.001);
                matrices.scale(1.004f, 1.005f, 1.005f);
                matrices.translate(0, 0, -0.5f);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                        roundelBlockEntity.getCachedState().get(RoundelBlock.FACING).asRotation()));
                if (pair.getSecond() != DyeColor.WHITE) {
                    modelPart.render(matrices, vertexConsumers.getBuffer(
                                    RenderLayer.getEntityTranslucent(patterns.get(0).getFirst().texture())), pair.getFirst().emissive() ? 0xf000f0 : light,
                            overlay, fs[0], fs[1], fs[2], 0.75f);
                }
                matrices.pop();
                RoundelBlockEntityRenderer.renderBakedModel(roundelBlockEntity, vertexConsumers, stateOf,
                        matrices, getBlockModel(stateOf), light, overlay);


                continue;
            }



            matrices.push();
            matrices.translate(0, 0.001, 0.001);
            matrices.scale(1.001f, 1.002f, 1.002f);
            matrices.translate(0, 0, -0.5f);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                    roundelBlockEntity.getCachedState().get(RoundelBlock.FACING).asRotation()));

            modelPart.render(matrices, vertexConsumer, pair.getFirst().emissive() ? 0xf000f0 : light, overlay, fs[0], fs[1], fs[2], 1.0f);
            matrices.pop();
        }
    }

    private static BakedModel getBlockModel(BlockState state) {
        return MinecraftClient.getInstance().getBlockRenderManager().getModel(state);
    }

    public static void renderBakedModel(RoundelBlockEntity roundelBlockEntity, VertexConsumerProvider vertexConsumers, BlockState state, MatrixStack matrices, BakedModel model, int light, int overlay) {
        VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayers.getBlockLayer(state));
        matrices.push();
        matrices.translate(-0.5, -1, -1);
        MinecraftClient.getInstance().getBlockRenderManager()
                .renderBlock(state, roundelBlockEntity.getPos(), roundelBlockEntity.getWorld(),
                        matrices, vertices, true,
                        MinecraftClient.getInstance().world.random);
        /*for (int i = 0; i < 7; i++) {
            for (BakedQuad q : model.getQuads(state, ModelHelper.faceFromIndex(i), dummy)) {
                vertices.quad(matrices.peek(), q, 1, 1, 1, light, overlay);
            }
        }*/
        matrices.pop();
    }
}

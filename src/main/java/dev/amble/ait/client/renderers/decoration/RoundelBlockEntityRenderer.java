package dev.amble.ait.client.renderers.decoration;

import java.util.BitSet;
import java.util.List;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;

import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.core.blockentities.RoundelBlockEntity;
import dev.amble.ait.core.roundels.RoundelPattern;
import dev.amble.ait.core.roundels.RoundelPatterns;
@Environment(value=EnvType.CLIENT)
public class RoundelBlockEntityRenderer
        implements BlockEntityRenderer<RoundelBlockEntity> {
    private static final Random dummy = Random.create();
    static final Direction[] DIRECTIONS = Direction.values();
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.NeighborGroup>> FACE_CULL_MAP = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<>(2048, 0.25f) {

            @Override
            protected void rehash(int newN) {
            }
        };
        object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
        return object2ByteLinkedOpenHashMap;
    });
    private final ModelPart cube;
    BakedModel model;
    public RoundelBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.cube = getTexturedModelData().createModel();
        this.model = getBlockModel(Blocks.WHITE_CONCRETE.getDefaultState());
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
        BlockState stateOf = roundelBlockEntity.getDynamicTextureBlockState();

        for (int i = 0; i < 17 && i < patterns.size(); ++i) {
            Pair<RoundelPattern, DyeColor> pair = patterns.get(i);
            float[] fs = pair.getSecond().getColorComponents();
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(AITRenderLayers.getEntityNoOutline(pair.getFirst().texture()));
            if (pair.getFirst().equals(RoundelPatterns.BASE)) {
                matrices.push();
                matrices.translate(0, 0.001, 0.001);
                matrices.scale(1.004f, 1.005f, 1.005f);
                matrices.translate(0, 0, -0.5f);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
                matrices.pop();
                RoundelBlockEntityRenderer.renderBakedModel(fs[0], fs[1], fs[2], roundelBlockEntity,
                        RenderLayers.getBlockLayer(stateOf), stateOf,
                        matrices, getBlockModel(stateOf), light, overlay);
                continue;
            }

            matrices.push();
            matrices.translate(0, 0.001, 0.001);
            matrices.scale(1.001f, 1.002f, 1.002f);
            matrices.translate(0, 0, -0.5f);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));

            modelPart.render(matrices, vertexConsumer, pair.getFirst().emissive() ? 0xf000f0 : light,
                    overlay, fs[0], fs[1], fs[2], 1.0f);
            //RoundelBlockEntityRenderer.renderBakedModel(fs[0], fs[1], fs[2], roundelBlockEntity, AITRenderLayers.
            //                getEyes(pair.getFirst().texture()), Blocks.STONE.getDefaultState(),
            //        matrices, getBlockModel(Blocks.STONE.getDefaultState()), pair.getFirst().emissive() ? 0xf000f0 : light, overlay);
            matrices.pop();
        }
    }

    private static BakedModel getBlockModel(BlockState state) {
        return MinecraftClient.getInstance().getBlockRenderManager().getModel(state);
    }

    public static void renderBakedModel(float red, float green, float blue, RoundelBlockEntity roundelBlockEntity, RenderLayer layer, BlockState state, MatrixStack matrices, BakedModel model, int light, int overlay) {
        matrices.push();
        matrices.translate(-0.5, -1, -1);
        RoundelBlockEntityRenderer.renderSmooth(red, green, blue, roundelBlockEntity.getWorld(), model, state, roundelBlockEntity.getPos(),
                        matrices, layer, true,
                        MinecraftClient.getInstance().world.random, 0, overlay);
        /*for (int i = 0; i < 7; i++) {
            for (BakedQuad q : model.getQuads(state, ModelHelper.faceFromIndex(i), dummy)) {
                vertices.quad(matrices.peek(), q, 1, 1, 1, light, overlay);
            }
        }*/
        matrices.pop();
    }

    public static void renderSmooth(float red, float green, float blue, BlockRenderView world, BakedModel model, BlockState state, BlockPos pos, MatrixStack matrices, RenderLayer layer, boolean cull, Random random, long seed, int overlay) {
        BlockModelRenderer bmR = MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer();
        float[] fs = new float[DIRECTIONS.length * 2];
        BitSet bitSet = new BitSet(3);
        BlockModelRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator = new BlockModelRenderer.AmbientOcclusionCalculator();
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (Direction direction : DIRECTIONS) {
            random.setSeed(seed);
            List<BakedQuad> list = model.getQuads(state, direction, random);
            if (list.isEmpty()) continue;
            mutable.set(pos, direction);
            if (cull && !RoundelBlockEntityRenderer.shouldDrawSide(state, world, pos, direction, mutable)) continue;
            RoundelBlockEntityRenderer.renderQuadsSmooth(red, green, blue, bmR, world, state, pos, matrices, layer, list, fs, bitSet, ambientOcclusionCalculator, overlay);
        }
        random.setSeed(seed);
        List<BakedQuad> list2 = model.getQuads(state, null, random);
        if (!list2.isEmpty()) {
            RoundelBlockEntityRenderer.renderQuadsSmooth(red, green, blue, bmR, world, state, pos, matrices, layer, list2, fs, bitSet, ambientOcclusionCalculator, overlay);
        }
    }

    public static boolean shouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos otherPos) {
        BlockState blockState = world.getBlockState(otherPos);
        if (state.isSideInvisible(blockState, side)) {
            return false;
        }
        Block.NeighborGroup neighborGroup = new Block.NeighborGroup(state, blockState, side);
        Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = FACE_CULL_MAP.get();
        byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(neighborGroup);
        if (b != 127) {
            return b != 0;
        }
        VoxelShape voxelShape = state.getCullingFace(world, pos, side);
        if (voxelShape.isEmpty()) {
            return true;
        }
        VoxelShape voxelShape2 = blockState.getCullingFace(world, otherPos, side.getOpposite());
        boolean bl = VoxelShapes.matchesAnywhere(voxelShape, voxelShape2, BooleanBiFunction.ONLY_FIRST);
        if (object2ByteLinkedOpenHashMap.size() == 2048) {
            object2ByteLinkedOpenHashMap.removeLastByte();
        }
        object2ByteLinkedOpenHashMap.putAndMoveToFirst(neighborGroup, (byte)(bl ? 1 : 0));
        return bl;
    }

    private static void renderQuadsSmooth(float red, float green, float blue, BlockModelRenderer bmr, BlockRenderView world, BlockState state, BlockPos pos, MatrixStack matrices, RenderLayer layer, List<BakedQuad> quads, float[] box, BitSet flags, BlockModelRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator, int overlay) {
        for (BakedQuad bakedQuad : quads) {
            bmr.getQuadDimensions(world, state, pos, bakedQuad.getVertexData(), bakedQuad.getFace(), box, flags);
            ambientOcclusionCalculator.apply(world, state, pos, bakedQuad.getFace(), box, flags, bakedQuad.hasShade());
            RoundelBlockEntityRenderer.renderQuad(red, green, blue, layer, matrices.peek(), bakedQuad, ambientOcclusionCalculator.brightness[0], ambientOcclusionCalculator.brightness[1], ambientOcclusionCalculator.brightness[2], ambientOcclusionCalculator.brightness[3], ambientOcclusionCalculator.light[0], ambientOcclusionCalculator.light[1], ambientOcclusionCalculator.light[2], ambientOcclusionCalculator.light[3], overlay);
        }
    }

    private static void renderQuad(float red, float green, float blue, RenderLayer layer, MatrixStack.Entry matrixEntry, BakedQuad quad, float brightness0, float brightness1, float brightness2, float brightness3, int light0, int light1, int light2, int light3, int overlay) {
        VertexConsumer consumer = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().getBuffer(layer);
        consumer.quad(matrixEntry, quad, new float[]{brightness0, brightness1, brightness2, brightness3},
                red, green, blue, new int[]{light0, light1, light2, light3}, overlay, false);
    }
}

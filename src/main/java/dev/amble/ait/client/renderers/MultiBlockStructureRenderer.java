package dev.amble.ait.client.renderers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.BlockRenderView;

import dev.amble.ait.core.engine.block.multi.MultiBlockStructure;


public class MultiBlockStructureRenderer {

    private final MinecraftClient client;
    private final Profiler profiler;

    protected MultiBlockStructureRenderer(MinecraftClient client) {
        this.client = client;
        this.profiler = client.getProfiler();
    }

    private MultiBlockStructureRenderer() {
        this(MinecraftClient.getInstance());
    }

    public void render(MultiBlockStructure structure, BlockPos centre, BlockRenderView view, MatrixStack matrices, VertexConsumerProvider provider, boolean holographic) {
        profiler.push("multi_block_structure");
        profiler.push("iterate_offsets");
        structure.forEach(offset -> renderOffset(offset, centre, view, matrices, provider, holographic));
    }

    public void renderForInterior(MultiBlockStructure structure, BlockPos centre, BlockRenderView view, MatrixStack matrices, VertexConsumerProvider provider, boolean holographic) {
        profiler.push("multi_block_structure");
        profiler.push("iterate_offsets");
        structure.forEach(offset -> renderOffsetInterior(offset, centre, view, matrices, provider, holographic));
        profiler.pop();
        profiler.pop();
    }

    public void renderOffsetInterior(MultiBlockStructure.BlockOffset offset, BlockPos centre, BlockRenderView view, MatrixStack matrices, VertexConsumerProvider provider, boolean holographic) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;

        BlockPos pos = centre.add(offset.offset());
        BlockPos diff = pos.subtract(centre);
        BlockState state = this.getBlock(offset.block()).getDefaultState();
        BlockEntity entity = world.getBlockEntity(offset.offset());

        matrices.push();
        matrices.translate(diff.getX(), diff.getY(), diff.getZ());

        if (holographic) {
            matrices.scale(0.35f, 0.35f, 0.35f);
            matrices.translate(0.95, 1, 1);
        }

        renderCulledBlocks(state, pos, view, matrices, provider);
        if (entity != null) {
            renderBlockEntities(entity, matrices, provider);
        }
        matrices.pop();
    }

    private void renderCulledBlocks(BlockState state, BlockPos pos, BlockRenderView view, MatrixStack matrices, VertexConsumerProvider provider) {
        client.getBlockRenderManager().renderBlock(state, pos, view, matrices, provider.getBuffer(RenderLayers.getBlockLayer(state)), true, client.world.random);
    }

    private void renderBlockEntities(BlockEntity entity,  MatrixStack matrices, VertexConsumerProvider provider) {
        client.getBlockEntityRenderDispatcher().render(entity, MinecraftClient.getInstance().getTickDelta(), matrices, provider);
    }

    public void renderOffset(MultiBlockStructure.BlockOffset offset, BlockPos centre, BlockRenderView view, MatrixStack matrices, VertexConsumerProvider provider, boolean holographic) {
        BlockPos pos = centre.add(offset.offset());

        BlockPos diff = pos.subtract(centre);
        BlockState state = this.getBlock(offset.block()).getDefaultState();

        matrices.push();
        matrices.translate(diff.getX(), diff.getY(), diff.getZ());

        if (holographic) {
            matrices.scale(0.35f, 0.35f, 0.35f);
            matrices.translate(0.95, 1, 1);
        }

        renderBlock(state, pos, view, matrices, provider);
        matrices.pop();
    }

    private void renderBlock(BlockState state, BlockPos pos, BlockRenderView view, MatrixStack matrices, VertexConsumerProvider provider) {
        client.getBlockRenderManager().getModelRenderer().render(view, client.getBlockRenderManager().getModel(state), state, pos, matrices, provider.getBuffer(RenderLayers.getBlockLayer(state)), false, client.world.random, state.getRenderingSeed(pos), OverlayTexture.DEFAULT_UV);
    }

    private Block getBlock(MultiBlockStructure.AllowedBlocks block) {
        return (Block) block.toArray()[0];
    }

    private static MultiBlockStructureRenderer INSTANCE;

    public static MultiBlockStructureRenderer instance() {
        if (INSTANCE == null) {
            INSTANCE = new MultiBlockStructureRenderer();
        }
        return INSTANCE;
    }
}

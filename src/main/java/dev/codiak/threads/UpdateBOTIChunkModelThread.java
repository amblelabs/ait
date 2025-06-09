package dev.codiak.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import dev.amble.ait.client.boti.BOTIChunkVBO;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;

//TODO: If I don't ever re-implement the baked model re-use this thread for the posState map
@SuppressWarnings("deprecation")
public class UpdateBOTIChunkModelThread extends Thread {
    ExteriorBlockEntity exteriorBlockEntity;
    public UpdateBOTIChunkModelThread(ExteriorBlockEntity exteriorBlockEntity) {
        this.exteriorBlockEntity = exteriorBlockEntity;
    }

    @Override
    public void run() {
        updateChunkModel();
        super.run();
    }

    public void updateChunkModel() {
        if (exteriorBlockEntity == null || exteriorBlockEntity.getWorld() == null || !exteriorBlockEntity.getWorld().isClient())
            return;

        BOTIChunkVBO botiChunkVBO = exteriorBlockEntity.tardis().get().stats().botiChunkVBO;

        BlockPos targetPos = exteriorBlockEntity.tardis().get().stats().targetPos();

        Map<BlockPos, BlockState> posState = exteriorBlockEntity.tardis().get().stats().posState;

        if (botiChunkVBO == null) botiChunkVBO = new BOTIChunkVBO();

        botiChunkVBO.markWorkingInThread();
        botiChunkVBO.markDirty();

        if(botiChunkVBO.bufferBuilder == null) botiChunkVBO.bufferBuilder = new BufferBuilder(4096 * 6 * 4);
        if(botiChunkVBO.bufferBuilder.isBuilding()) return;
        botiChunkVBO.bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
        AtomicInteger vertexCounter = new AtomicInteger();

        botiChunkVBO.setTargetPos(targetPos);
        botiChunkVBO.updateBlockMap(posState);

        MinecraftClient mc = MinecraftClient.getInstance();
        BlockRenderManager blockRenderManager = mc.getBlockRenderManager();
        List<BakedQuad> quads = new ArrayList<>();

        BOTIChunkVBO finalBotiChunkVBO = botiChunkVBO;

        botiChunkVBO.blocks.forEach((pos, state) -> {
            if (state != null && !state.isAir() && !state.hasBlockEntity()) {
                if(BOTIChunkVBO.shouldGenerateQuads) {
                    BakedModel model = blockRenderManager.getModel(state);
                    if (model != null) {
                        List<BakedQuad> blockQuads = new ArrayList<>();

                        // Add general quads
                        List<BakedQuad> generalQuads = model.getQuads(state, null, net.minecraft.util.math.random.Random.create());
                        if (generalQuads != null) {
                            blockQuads.addAll(generalQuads);
                        }

                        // Add side quads
                        List<BakedQuad> sideQuads = model.getQuads(state, Direction.NORTH, net.minecraft.util.math.random.Random.create());
                        sideQuads.addAll(model.getQuads(state, Direction.SOUTH, net.minecraft.util.math.random.Random.create()));
                        sideQuads.addAll(model.getQuads(state, Direction.EAST, net.minecraft.util.math.random.Random.create()));
                        sideQuads.addAll(model.getQuads(state, Direction.WEST, net.minecraft.util.math.random.Random.create()));
                        blockQuads.addAll(sideQuads);

                        Random random = Random.create();
                        int x = pos.getX();
                        int y = pos.getY();
                        int z = pos.getZ();
                        vertexCounter.addAndGet(finalBotiChunkVBO.addQuadsToBuffer(model.getQuads(state, null, random), finalBotiChunkVBO.bufferBuilder, x, y, z));
                        vertexCounter.addAndGet(finalBotiChunkVBO.addQuadsToBuffer(model.getQuads(state, Direction.UP, random), finalBotiChunkVBO.bufferBuilder, x, y, z));
                        vertexCounter.addAndGet(finalBotiChunkVBO.addQuadsToBuffer(model.getQuads(state, Direction.DOWN, random), finalBotiChunkVBO.bufferBuilder, x, y, z));
                        vertexCounter.addAndGet(finalBotiChunkVBO.addQuadsToBuffer(model.getQuads(state, Direction.NORTH, random), finalBotiChunkVBO.bufferBuilder, x, y, z));
                        vertexCounter.addAndGet(finalBotiChunkVBO.addQuadsToBuffer(model.getQuads(state, Direction.SOUTH, random), finalBotiChunkVBO.bufferBuilder, x, y, z));
                        vertexCounter.addAndGet(finalBotiChunkVBO.addQuadsToBuffer(model.getQuads(state, Direction.EAST, random), finalBotiChunkVBO.bufferBuilder, x, y, z));
                        vertexCounter.addAndGet(finalBotiChunkVBO.addQuadsToBuffer(model.getQuads(state, Direction.WEST, random), finalBotiChunkVBO.bufferBuilder, x, y, z));


                        // Translate and add valid quads
                        if (!blockQuads.isEmpty()) {
                            if (exteriorBlockEntity.tardis().get().stats() != null) {
                                List<BakedQuad> translatedQuads = exteriorBlockEntity.tardis().get().stats().translateQuads(blockQuads, pos.getX(), pos.getY(), pos.getZ());
                                if (!translatedQuads.isEmpty()) {
                                    quads.addAll(translatedQuads);
                                }
                            }
                        }
                    }
                }
            }
        });

        if (quads.isEmpty()) {
            //System.out.println("No quads or block entities generated for chunk at " + targetPos);
        } else {
            exteriorBlockEntity.tardis().get().stats().chunkModel = new BakedModel() {
                @Override
                public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, net.minecraft.util.math.random.Random random) {
                    return quads;
                }

                @Override
                public boolean useAmbientOcclusion() {
                    return true;
                }

                @Override
                public boolean hasDepth() {
                    return true;
                }

                @Override
                public boolean isSideLit() {
                    return true;
                }

                @Override
                public boolean isBuiltin() {
                    return false;
                }

                @Override
                public Sprite getParticleSprite() {
                    return mc.getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
                            .apply(new Identifier("minecraft", "stone"));
                }

                @Override
                public ModelTransformation getTransformation() {
                    return ModelTransformation.NONE;
                }

                @Override
                public ModelOverrideList getOverrides() {
                    return ModelOverrideList.EMPTY;
                }
            };
        }
        botiChunkVBO.unmarkWorkingInThread();
    }
}

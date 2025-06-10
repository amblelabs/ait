package dev.amble.ait.client.boti;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.*;

import dev.amble.ait.core.blockentities.ExteriorBlockEntity;


public class BOTIChunkVBO {
    /** if this is false, it won't even attempt to render/create quads and instead fall back to the {@link  net.minecraft.client.render.block.BlockModelRenderer} render method **/
    public static boolean shouldGenerateQuads = false;
    public VertexBuffer vertexBuffer;
    public BufferBuilder bufferBuilder;
    public int vertexCount = 0;
    public static int chunksToRender = 8;
    private final Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();
    public Map<BlockPos, BlockState> blocks = new HashMap<>();
    private BlockPos targetPos;
    private boolean workingInThread = true;
    private boolean dirty = true;

    public BOTIChunkVBO() {
        this.vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
    }

    public static int blocksToRender() {
        return chunksToRender >> 2;
    }

    public static int blocksToRender(int chunkIndex) {
        return chunkIndex >> 2;
    }

    public void setTargetPos(BlockPos targetPos) {
        this.targetPos = targetPos;
    }

    public void markWorkingInThread() {
        this.workingInThread = true;
    }

    public void unmarkWorkingInThread() {
        this.workingInThread = false;
    }

    public boolean isWorkingInThread() {
        return this.workingInThread;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void unmarkDirty() {
        this.dirty = false;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void updateBlockMap(Map<BlockPos, BlockState> map) {
        this.blocks = map;
    }

    public void updateChunkModel(ExteriorBlockEntity exteriorBlockEntity) {
        if (exteriorBlockEntity == null || exteriorBlockEntity.getWorld() == null || !exteriorBlockEntity.getWorld().isClient())
            return;
        MinecraftClient mc = MinecraftClient.getInstance();
        BlockRenderManager blockRenderManager = mc.getBlockRenderManager();
        ChunkPos chunkPos = new ChunkPos(targetPos);
        int baseY = targetPos.getY() & ~15;
        this.blockEntities.clear();

        if (!this.isWorkingInThread() && this.isDirty()) {
            if (bufferBuilder != null) {
                BufferBuilder.BuiltBuffer builtBuffer = bufferBuilder.end();
                vertexBuffer.bind();
                vertexBuffer.upload(builtBuffer);
                VertexBuffer.unbind();
                unmarkDirty();
                System.out.println("VBO updated with " + vertexCount + " vertices for position " + targetPos);
            }
        }
    }

    public static int addQuadsToBuffer(List<BakedQuad> quads, BufferBuilder buffer, int xOffset, int yOffset, int zOffset) {
        if (quads == null || quads.isEmpty()) return 0;

        int verticesAdded = 0;
        for (BakedQuad quad : quads) {
            if (quad == null) continue;

            int[] vertexData = quad.getVertexData();
            if (vertexData.length < 32) continue;

            for (int i = 0; i < 4; i++) {
                int baseIndex = i * 8;
                float x = Float.intBitsToFloat(vertexData[baseIndex]) + xOffset;
                float y = Float.intBitsToFloat(vertexData[baseIndex + 1]) + yOffset;
                float z = Float.intBitsToFloat(vertexData[baseIndex + 2]) + zOffset;
                int packedColor = vertexData[baseIndex + 3];
                float r = ((packedColor >> 16) & 0xFF) / 255.0f;
                float g = ((packedColor >> 8) & 0xFF) / 255.0f;
                float b = (packedColor & 0xFF) / 255.0f;
                float a = ((packedColor >> 24) & 0xFF) / 255.0f;
                float u = Float.intBitsToFloat(vertexData[baseIndex + 4]);
                float v = Float.intBitsToFloat(vertexData[baseIndex + 5]);
                int light = vertexData[baseIndex + 6];
                int normal = vertexData[baseIndex + 7];
                float nx = ((normal >> 16) & 0xFF) / 255.0f * 2.0f - 1.0f;
                float ny = ((normal >> 8) & 0xFF) / 255.0f * 2.0f - 1.0f;
                float nz = (normal & 0xFF) / 255.0f * 2.0f - 1.0f;

                buffer.vertex(x, y, z)
                        .color(r, g, b, a)
                        .texture(u, v)
                        .light(light)
                        .normal(nx, ny, nz)
                        .next();
                verticesAdded++;
                if (verticesAdded <= 4) { // Log first quad only
                    System.out.println("Vertex: (" + x + ", " + y + ", " + z + "), Color: (" + r + ", " + g + ", " + b + ", " + a + ")");
                }
            }
        }
        System.out.println("Added " + verticesAdded + " vertices to buffer");
        return verticesAdded;
    }
}
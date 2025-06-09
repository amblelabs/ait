package dev.amble.ait.client.renderers;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.data.ClientLandingManager;
import dev.amble.ait.data.landing.LandingPadRegion;
import dev.amble.ait.data.landing.LandingPadSpot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;

import java.util.List;

public class LandingRegionRenderer {

    private static final int DARK_CYAN = ColorHelper.Argb.getArgb(255, 0, 155, 155);

    private static final Identifier AVAILABLE = AITMod.id("textures/marker/available.png");
    private static final Identifier OCCUPIED = AITMod.id("textures/marker/occupied.png");

    private final MinecraftClient client;
    private Identifier previous;

    public LandingRegionRenderer(MinecraftClient client) {
        this.client = client;
    }

    private static Identifier getTexture(LandingPadSpot spot) {
        return spot.isOccupied() ? OCCUPIED : AVAILABLE;
    }

    public boolean shouldRender() {
        return SonicRendering.isPlayerHoldingScanningSonic() && ClientLandingManager.getInstance().getRegion(client.player.getChunkPos()) != null;
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        Profiler profiler = client.world.getProfiler();

        profiler.swap("landing_pad");

        profiler.push("region");
        renderRegion();
        profiler.swap("chunk");
        renderChunk(matrices, vertexConsumers, cameraX, cameraY, cameraZ);

        profiler.pop();
    }

    private void renderRegion() {
        Profiler profiler = client.world.getProfiler();

        profiler.push("get");
        LandingPadRegion region = ClientLandingManager.getInstance().getRegion(client.player.getChunkPos());

        if (region == null)
            return;

        profiler.swap("iterate");
        List<LandingPadSpot> spots = region.getSpots();

        for (int i = 0; i < spots.size(); i++) {
            boolean isLast = i == spots.size() - 1;
            renderSpot(spots.get(i), isLast);
        }

        this.previous = null;

        profiler.pop();
    }

    private void renderSpot(LandingPadSpot spot, boolean forceRender) {
        Identifier text = getTexture(spot);
        SonicRendering.renderFloorTexture(spot.getPos().add(1, 0, 1), text, forceRender ? null : this.previous, true);

        forceRender = forceRender || !text.equals(this.previous);

        this.previous = forceRender ? null : text;
    }

    private void renderChunk(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        int k = DARK_CYAN;
        int j;
        Entity entity = this.client.gameRenderer.getCamera().getFocusedEntity();
        float f = (float)((double)this.client.world.getBottomY() - cameraY);
        float g = (float)((double)this.client.world.getTopY() - cameraY);
        ChunkPos chunkPos = entity.getChunkPos();
        float h = (float)((double)chunkPos.getStartX() - cameraX);
        float i = (float)((double)chunkPos.getStartZ() - cameraZ);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(1.0));
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        for (j = 2; j < 16; j += 2) {
            vertexConsumer.vertex(matrix4f, h + (float)j, f, i).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, h + (float)j, f, i).color(k).next();
            vertexConsumer.vertex(matrix4f, h + (float)j, g, i).color(k).next();
            vertexConsumer.vertex(matrix4f, h + (float)j, g, i).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, h + (float)j, f, i + 16.0f).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, h + (float)j, f, i + 16.0f).color(k).next();
            vertexConsumer.vertex(matrix4f, h + (float)j, g, i + 16.0f).color(k).next();
            vertexConsumer.vertex(matrix4f, h + (float)j, g, i + 16.0f).color(1.0f, 1.0f, 0.0f, 0.0f).next();
        }
        for (j = 2; j < 16; j += 2) {
            vertexConsumer.vertex(matrix4f, h, f, i + (float)j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, h, f, i + (float)j).color(k).next();
            vertexConsumer.vertex(matrix4f, h, g, i + (float)j).color(k).next();
            vertexConsumer.vertex(matrix4f, h, g, i + (float)j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, h + 16.0f, f, i + (float)j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, h + 16.0f, f, i + (float)j).color(k).next();
            vertexConsumer.vertex(matrix4f, h + 16.0f, g, i + (float)j).color(k).next();
            vertexConsumer.vertex(matrix4f, h + 16.0f, g, i + (float)j).color(1.0f, 1.0f, 0.0f, 0.0f).next();
        }
        for (j = this.client.world.getBottomY(); j <= this.client.world.getTopY(); j += 2) {
            float l = (float)((double)j - cameraY);
            int m = DARK_CYAN;
            vertexConsumer.vertex(matrix4f, h, l, i).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, h, l, i).color(m).next();
            vertexConsumer.vertex(matrix4f, h, l, i + 16.0f).color(m).next();
            vertexConsumer.vertex(matrix4f, h + 16.0f, l, i + 16.0f).color(m).next();
            vertexConsumer.vertex(matrix4f, h + 16.0f, l, i).color(m).next();
            vertexConsumer.vertex(matrix4f, h, l, i).color(m).next();
            vertexConsumer.vertex(matrix4f, h, l, i).color(1.0f, 1.0f, 0.0f, 0.0f).next();
        }
        vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(2.0));
        for (j = 0; j <= 16; j += 16) {
            for (int k2 = 0; k2 <= 16; k2 += 16) {
                vertexConsumer.vertex(matrix4f, h + (float)j, f, i + (float)k2).color(0.25f, 0.25f, 1.0f, 0.0f).next();
                vertexConsumer.vertex(matrix4f, h + (float)j, f, i + (float)k2).color(0.25f, 0.25f, 1.0f, 1.0f).next();
                vertexConsumer.vertex(matrix4f, h + (float)j, g, i + (float)k2).color(0.25f, 0.25f, 1.0f, 1.0f).next();
                vertexConsumer.vertex(matrix4f, h + (float)j, g, i + (float)k2).color(0.25f, 0.25f, 1.0f, 0.0f).next();
            }
        }
        for (j = this.client.world.getBottomY(); j <= this.client.world.getTopY(); j += 16) {
            float l = (float)((double)j - cameraY);
            vertexConsumer.vertex(matrix4f, h, l, i).color(0.25f, 0.25f, 1.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, h, l, i).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            vertexConsumer.vertex(matrix4f, h, l, i + 16.0f).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            vertexConsumer.vertex(matrix4f, h + 16.0f, l, i + 16.0f).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            vertexConsumer.vertex(matrix4f, h + 16.0f, l, i).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            vertexConsumer.vertex(matrix4f, h, l, i).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            vertexConsumer.vertex(matrix4f, h, l, i).color(0.25f, 0.25f, 1.0f, 0.0f).next();
        }
    }

    public void tryRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        if (!this.shouldRender())
            return;

        this.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
    }

    private static LandingRegionRenderer INSTANCE;

    public static LandingRegionRenderer getInstance() {
        if (INSTANCE == null)
            INSTANCE = new LandingRegionRenderer(MinecraftClient.getInstance());

        return INSTANCE;
    }
}

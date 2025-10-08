package dev.amble.ait.client.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.vortex.reference.VortexReference;
import dev.amble.ait.core.tardis.vortex.reference.VortexReferenceRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class VortexRender {
    private static VortexRender INSTANCE;

    public Identifier texture;
    public Identifier secondLayer;
    public Identifier thirdLayer;
    private final float distortionSpeed;
    private final float distortionSeparationFactor;
    private final float distortionFactor;
    private final float scale;
    private final float speed;
    private float time = 0;

    public VortexRender(Identifier texture) {
        replaceWith(texture);
        this.distortionSpeed = 0.5f;
        this.distortionSeparationFactor = 32f;
        this.distortionFactor = 2;
        this.scale = 32f;
        this.speed = 4f;
    }
    @ApiStatus.Internal
    //@Deprecated(forRemoval = true)
    public VortexRender(String name) {
        this(AITMod.id("textures/vortex/" + name + ".png"));
    }

    /**
     * Get the singleton instance of the VortexRender updated for the given reference.
     */
    public static VortexRender getInstance(VortexReference ref) {
        if (INSTANCE == null) {
            INSTANCE = new VortexRender(ref.texture());
        } else if (!INSTANCE.isFor(ref.texture())) {
            INSTANCE.replaceWith(ref.texture());
        }
        return INSTANCE;
    }

    /**
     * Get the instance of the renderer with the last reference seen.
     */
    public static VortexRender getCurrentInstance() {
        if (INSTANCE == null) INSTANCE = new VortexRender(VortexReferenceRegistry.getInstance().getRandom().texture());
        return INSTANCE;
    }

    public boolean isFor(Identifier texture) {
        return this.texture.equals(texture);
    }

    public void replaceWith(Identifier texture) {
        this.texture = texture;
        secondLayer = new Identifier(texture.getNamespace(), texture.getPath().substring(0, texture.getPath().length() - 4) +
                "_second" + ".png");
        thirdLayer = new Identifier(texture.getNamespace(), texture.getPath().substring(0, texture.getPath().length() - 4) +
                "_third" + ".png");
    }

    public void render(MatrixStack matrixStack) {

        time += MinecraftClient.getInstance().getTickDelta() / 360F;

        this.renderLayer(matrixStack, 1.0F, texture);
        this.renderLayer(matrixStack, 1.5f);
        this.renderLayer(matrixStack, 2.5f);
    }

    public void renderLayer(MatrixStack matrixStack, float scaleFactor) {
        Identifier currentTexture = scaleFactor == 1.5f ? secondLayer : thirdLayer;
        if (MinecraftClient.getInstance().getResourceManager().getResource(currentTexture).isEmpty()) return;
        this.renderLayer(matrixStack, scaleFactor, currentTexture);
    }

    private void renderLayer(MatrixStack matrixStack, float scaleFactor, Identifier layer) {

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getRenderTypeBeaconBeamProgram);
        RenderSystem.setShaderTexture(0, layer);

        matrixStack.push();

        matrixStack.scale(scale / scaleFactor, scale / scaleFactor, scale);

        MinecraftClient.getInstance().getTextureManager().bindTexture(layer);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);

        for (int i = 0; i < 32; ++i) {
            this.renderSection(buffer, i, (((MinecraftClient.getInstance().player.age) * 0.005F) * -this.speed), (float) Math.sin(i * Math.PI / 32),
                    (float) Math.sin((i + 1) * Math.PI / 32), matrixStack.peek().getNormalMatrix(), matrixStack.peek().getPositionMatrix());
        }

        tessellator.draw();
        matrixStack.pop();

        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public void renderSection(VertexConsumer builder, int zOffset, float textureDistanceOffset, float startScale,
            float endScale, Matrix3f matrix3f, Matrix4f matrix4f) {
        float panel = 1 / 6f;
        float sqrt = (float) Math.sqrt(3) / 2.0f;
        int vOffset = (zOffset * panel + textureDistanceOffset > 1.0) ? zOffset - 6 : zOffset;
        float distortion = this.computeDistortionFactor(time, zOffset);
        float distortionPlusOne = this.computeDistortionFactor(time, zOffset + 1);
        float panelDistanceOffset = panel + textureDistanceOffset;
        float vPanelOffset = (vOffset * panel) + textureDistanceOffset;

        int uOffset = 0;

        float uPanelOffset = uOffset * panel;

        addVertex(builder, matrix3f, matrix4f, 0f, -startScale + distortion, -zOffset, uPanelOffset, vPanelOffset);

        addVertex(builder, matrix3f, matrix4f, 0f, -endScale + distortionPlusOne, -zOffset - 1, uPanelOffset,
                vOffset * panel + panelDistanceOffset);

        addVertex(builder, matrix3f, matrix4f, endScale * -sqrt, endScale / -2f + distortionPlusOne, -zOffset - 1,
                uPanelOffset + panel, vOffset * panel + panelDistanceOffset);

        addVertex(builder, matrix3f, matrix4f, startScale * -sqrt, startScale / -2f + distortion, -zOffset, uPanelOffset + panel,
                vPanelOffset);

        uOffset = 1;

        uPanelOffset = uOffset * panel;

        addVertex(builder, matrix3f, matrix4f, startScale * -sqrt, startScale / -2f + distortion, -zOffset, uPanelOffset,
                vPanelOffset);

        addVertex(builder, matrix3f, matrix4f, endScale * -sqrt, endScale / -2f + distortionPlusOne, -zOffset - 1, uPanelOffset,
                vOffset * panel + panelDistanceOffset);

        addVertex(builder, matrix3f, matrix4f, endScale * -sqrt, endScale / 2f + distortionPlusOne, -zOffset - 1,
                uPanelOffset + panel, vOffset * panel + panelDistanceOffset);

        addVertex(builder, matrix3f, matrix4f, startScale * -sqrt, startScale / 2f + distortion, -zOffset, uPanelOffset + panel,
                vPanelOffset);

        uOffset = 2;

        uPanelOffset = uOffset * panel;

        addVertex(builder, matrix3f, matrix4f, 0f, endScale + distortionPlusOne, -zOffset - 1, uPanelOffset + panel,
                vOffset * panel + panelDistanceOffset);

        addVertex(builder, matrix3f, matrix4f, 0f, startScale + distortion, -zOffset, uPanelOffset + panel, vPanelOffset);

        addVertex(builder, matrix3f, matrix4f, startScale * -sqrt, startScale / 2f + distortion, -zOffset, uPanelOffset,
                vPanelOffset);

        addVertex(builder, matrix3f, matrix4f, endScale * -sqrt, endScale / 2f + distortionPlusOne, -zOffset - 1, uPanelOffset,
                vOffset * panel + panelDistanceOffset);

        uOffset = 3;

        uPanelOffset = uOffset * panel;

        addVertex(builder, matrix3f, matrix4f, 0f, startScale + distortion, -zOffset, uPanelOffset, vPanelOffset);

        addVertex(builder, matrix3f, matrix4f, 0f, endScale + distortionPlusOne, -zOffset - 1, uPanelOffset,
                vOffset * panel + panelDistanceOffset);

        addVertex(builder, matrix3f, matrix4f, endScale * sqrt, (endScale / 2f + distortionPlusOne), -zOffset - 1,
                uPanelOffset + panel, vOffset * panel + panelDistanceOffset);

        addVertex(builder, matrix3f, matrix4f, startScale * sqrt, (startScale / 2f + distortion), -zOffset, uPanelOffset + panel,
                vPanelOffset);

        uOffset = 4;

        uPanelOffset = uOffset * panel;

        addVertex(builder, matrix3f, matrix4f, startScale * sqrt, (startScale / 2f + distortion), -zOffset, uPanelOffset,
                vPanelOffset);

        addVertex(builder, matrix3f, matrix4f, endScale * sqrt, endScale / 2f + distortionPlusOne, -zOffset - 1, uPanelOffset,
                vOffset * panel + panelDistanceOffset);

        addVertex(builder, matrix3f, matrix4f, endScale * sqrt, endScale / -2f + distortionPlusOne, -zOffset - 1,
                uPanelOffset + panel, vOffset * panel + panelDistanceOffset);

        addVertex(builder, matrix3f, matrix4f, startScale * sqrt, startScale / -2f + distortion, -zOffset, uPanelOffset + panel,
                vPanelOffset);

        uOffset = 5;

        uPanelOffset = uOffset * panel;

        addVertex(builder, matrix3f, matrix4f, 0f, -endScale + distortionPlusOne, -zOffset - 1, uPanelOffset + panel,
                vOffset * panel + panelDistanceOffset);

        addVertex(builder, matrix3f, matrix4f, 0f, -startScale + distortion, -zOffset, uPanelOffset + panel, vPanelOffset);

        addVertex(builder, matrix3f, matrix4f, startScale * sqrt, startScale / -2f + distortion, -zOffset, uPanelOffset,
                vPanelOffset);

        addVertex(builder, matrix3f, matrix4f, endScale * sqrt, endScale / -2f + distortionPlusOne, -zOffset - 1, uPanelOffset,
                vOffset * panel + panelDistanceOffset);
    }

    private void addVertex(VertexConsumer builder, Matrix3f normalMatrix, Matrix4f matrix, float x, float y, float z, float u, float v) {
        builder.vertex(matrix, x, y, z).color(1, 1, 1, 1f).texture(u, v).light(0xF000F0).normal(normalMatrix,0, 0.0f, 0).next();
    }

    private float computeDistortionFactor(float time, int t) {
        return (float) (Math.sin(time * this.distortionSpeed * 2.0 * Math.PI + (13 - t) *
        this.distortionSeparationFactor) * this.distortionFactor) / 8;
    }
}

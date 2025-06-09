package dev.amble.ait.client.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.ait.AITMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class VortexUtil {
    public Identifier TEXTURE_LOCATION;
    public Identifier SECOND_LAYER_LOCATION;
    public Identifier THIRD_LAYER_LOCATION;
    private final float distortionSpeed;
    private final float distortionSeparationFactor;
    private final float distortionFactor;
    private final float scale;
    private final float speed;
    private float time = 0;

    public VortexUtil(Identifier texture) {
        TEXTURE_LOCATION = texture;
        SECOND_LAYER_LOCATION = new Identifier(texture.getNamespace(), texture.getPath().substring(0, texture.getPath().length() - 4) +
                "_second" + ".png");
        THIRD_LAYER_LOCATION =new Identifier(texture.getNamespace(), texture.getPath().substring(0, texture.getPath().length() - 4) +
                "_third" + ".png");
        this.distortionSpeed = 0.5f;
        this.distortionSeparationFactor = 32f;
        this.distortionFactor = 2;
        this.scale = 32f;
        this.speed = 4f;
    }
    @ApiStatus.Internal
    //@Deprecated(forRemoval = true)
    public VortexUtil(String name) {
        this(AITMod.id("textures/vortex/" + name + ".png"));
    }

    public void renderVortex(MatrixStack matrixStack) {

        time += MinecraftClient.getInstance().getTickDelta() / 360f;

        matrixStack.push();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);

        matrixStack.scale(scale, scale, scale);

        MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE_LOCATION);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        for (int i = 0; i < 32; ++i) {
            this.renderSection(buffer, i, (MinecraftClient.getInstance().player.age / 200.0f) * -this.speed, (float) Math.sin(i * Math.PI / 32),
                    (float) Math.sin((i + 1) * Math.PI / 32), matrixStack.peek().getNormalMatrix(), matrixStack.peek().getPositionMatrix());
        }

        tessellator.draw();
        matrixStack.pop();
    }

    public void renderVortexLayer(MatrixStack matrixStack, float scaleFactor) {
        Identifier currentTexture = scaleFactor == 1.5f ? SECOND_LAYER_LOCATION : THIRD_LAYER_LOCATION;
        if (MinecraftClient.getInstance().getResourceManager().getResource(currentTexture).isEmpty()) return;
        this.renderVortexLayer(matrixStack, scaleFactor, currentTexture);
    }

    private void renderVortexLayer(MatrixStack matrixStack, float scaleFactor, Identifier layer) {

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getRenderTypeBeaconBeamProgram);
        RenderSystem.setShaderTexture(0, layer);

        time += MinecraftClient.getInstance().getTickDelta() / 360f;

        matrixStack.push();

        matrixStack.scale(scale / scaleFactor, scale / scaleFactor, scale);

        MinecraftClient.getInstance().getTextureManager().bindTexture(layer);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);

        for (int i = 0; i < 32; ++i) {
            this.renderSection(buffer, i, (MinecraftClient.getInstance().player.age / 200.0f) * -this.speed, (float) Math.sin(i * Math.PI / 32),
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

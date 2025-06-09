package dev.amble.ait.client.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class TriangleTestingUtil {

    public static void renderTriangle(WorldRenderContext context) {
        Camera camera = context.camera();

        Vec3d targetPosition = new Vec3d(-67, 67, 108);
        Vec3d transformedPosition = targetPosition.subtract(camera.getPos());

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
        matrixStack.translate(transformedPosition.x, transformedPosition.y, transformedPosition.z);
        matrixStack.scale(1f, 8f, 1f);

        Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        for (int i = 0; i < 6; ++i) {
            matrixStack.multiply(
                    RotationAxis.POSITIVE_Y.rotationDegrees(i * ((i > 1f && i < 3f) || i == 4 ? 120f : 60F)), 0, 0, 0);
            buffer.vertex(positionMatrix, -0.5f, 1, -0.865625f).color(1f, 1f, 1f, 1f).texture(0f, 0f).next();
            buffer.vertex(positionMatrix, 0, 0, /*-0.865625f*/ 0).color(1f, 0f, 0f, 1f).texture(0f, 1f).next();
            buffer.vertex(positionMatrix, 0.5f, 1, -0.865625f).color(0f, 0f, 1f, 1f).texture(1f, 0f).next();
        }

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableCull();

        tessellator.draw();

        RenderSystem.enableCull();
    }
}

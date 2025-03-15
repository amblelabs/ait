/*
package dev.amble.ait.client.renderers;


public class InteriorRendering {

    public static void renderInterior(MatrixStack stack, ExteriorBlockEntity exterior) {
        stack.push();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        // Create VBOs for block render layers
        VertexBuffer solidVbo = new VertexBuffer();
        VertexBuffer translucentVbo = new VertexBuffer();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);

        MultiBlockStructureRenderer.instance().renderForInterior(MultiBlockStructure.testInteriorRendering(AITMod.id("interiors/office")), exterior.getPos(), exterior.getWorld(), stack, */
/*should have a provider but im building a buffer here*//*
, false);

        // Upload vertices to VBOs
        solidVbo.upload(buffer.end());
        translucentVbo.upload(buffer.end());

        // Render the buffers
        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        solidVbo.bind();
        solidVbo.draw(stack.peek().getPositionMatrix(), RenderSystem.getProjectionMatrix(), GameRenderer.getPositionColorTexProgram());
        VertexBuffer.unbind();

        // Sort translucent buffer if the camera moves
        if (MinecraftClient.getInstance().worldRenderer.needsTranslucentSorting()) {
            translucentVbo.sortQuads(MinecraftClient.getInstance().gameRenderer.getCamera().getPos());
        }

        RenderSystem.setShader(GameRenderer::getRenderTypeTranslucentProgram);
        translucentVbo.bind();
        translucentVbo.draw(stack.peek().getPositionMatrix(), RenderSystem.getProjectionMatrix(), GameRenderer.getRenderTypeTranslucentProgram());
        VertexBuffer.unbind();

        tessellator.draw();
        stack.pop();
    }
}
*/

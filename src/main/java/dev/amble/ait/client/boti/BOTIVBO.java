package dev.amble.ait.client.boti;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.ait.mixin.client.rendering.VertexBufferWrapper;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;

import java.util.Map;
import java.util.stream.Collectors;

public class BOTIVBO {
    public static final VertexFormat format = VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
    public final Map<RenderLayer, BufferBuilder> bufferBuilders = RenderLayer.getBlockLayers().stream().collect(Collectors.toMap(renderLayer -> renderLayer, renderLayer -> new BufferBuilder(renderLayer.getExpectedBufferSize())));
    public final Map<RenderLayer, VertexBuffer> vbo = RenderLayer.getBlockLayers().stream().collect(Collectors.toMap(renderLayer -> renderLayer, renderLayer -> new VertexBuffer(VertexBuffer.Usage.STATIC)));

    public BOTIVBO() {
        this.init();
    }
    public VertexBuffer get(RenderLayer layer) {
        return this.vbo.get(layer);
    }

    public VertexBuffer getVBO(RenderLayer layer) {
        return this.vbo.getOrDefault(layer, this.vbo.get(RenderLayer.getSolid()));
    }

    public BufferBuilder getBufferBuilder(RenderLayer layer) {
        return this.bufferBuilders.get(layer);
    }

    public void init() {
        for (RenderLayer layer : RenderLayer.getBlockLayers()) {
            this.bufferBuilders.put(layer, new BufferBuilder(layer.getExpectedBufferSize()));
            this.vbo.put(layer, new VertexBuffer(VertexBuffer.Usage.STATIC));
        }
    }

    public void begin(RenderLayer layer) {
        this.getVBO(layer).bind();
    }

    public void reset(RenderLayer layer) {
        this.bufferBuilders.get(layer).clear();
        this.bufferBuilders.get(layer).reset();
    }

    public void upload(RenderLayer layer) {
        BufferBuilder bufferBuilder = this.getBufferBuilder(layer);
        if (bufferBuilder.isBuilding()) {
            bufferBuilder.end();
            return;
        }
        BufferBuilder.BuiltBuffer builtBuffer = bufferBuilder.end();
        this.getVBO(layer).upload(builtBuffer);
    }

    public void unbind(RenderLayer layer) {
        this.getBufferBuilder(layer).reset();
        this.getBufferBuilder(layer).clear();
        VertexBuffer.unbind();
    }

    public void draw() {
        this.vbo.forEach((layer, vbo) -> {
            /*if (((VertexBufferWrapper) this.getVBO(layer)).getIndexType() == null) {
                return; // Skip drawing if there's no index type
            }*/
            this.begin(layer);
            format.setupState();
            layer.startDrawing();
            ((VertexBufferWrapper) this.getVBO(layer)).setDrawMode(layer.getDrawMode());
            ((VertexBufferWrapper) this.getVBO(layer)).setIndexType(VertexFormat.IndexType.SHORT);
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapProgram);
            this.getVBO(layer).draw();
            layer.endDrawing();
            format.clearState();
            VertexBuffer.unbind();
        });
    }
}

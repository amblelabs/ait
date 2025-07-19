package dev.amble.ait.mixin.client.rendering;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.VertexFormat;

@Mixin(VertexBuffer.class)
public interface VertexBufferWrapper {
    @Accessor
    void setDrawMode(VertexFormat.DrawMode drawMode);

    @Accessor
    void setIndexType(VertexFormat.IndexType indexType);

    @Accessor
    VertexFormat.IndexType getIndexType();
}

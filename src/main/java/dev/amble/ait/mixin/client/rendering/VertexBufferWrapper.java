package dev.amble.ait.mixin.client.rendering;

import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VertexBuffer.class)
public interface VertexBufferWrapper {
    @Accessor
    void setDrawMode(VertexFormat.DrawMode drawMode);

    @Accessor
    void setIndexType(VertexFormat.IndexType indexType);

    @Accessor
    VertexFormat.IndexType getIndexType();
}

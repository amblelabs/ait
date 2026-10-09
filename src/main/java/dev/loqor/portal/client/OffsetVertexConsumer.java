package dev.loqor.portal.client;

import net.minecraft.client.render.VertexConsumer;

public class OffsetVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;

    public OffsetVertexConsumer(VertexConsumer delegate, double offsetX, double offsetY, double offsetZ) {
        this.delegate = delegate;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        this.delegate.vertex(x + this.offsetX, y + this.offsetY, z + this.offsetZ);
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        this.delegate.color(red, green, blue, alpha);
        return this;
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        this.delegate.texture(u, v);
        return this;
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        this.delegate.overlay(u, v);
        return this;
    }

    @Override
    public VertexConsumer light(int u, int v) {
        this.delegate.light(u, v);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        this.delegate.normal(x, y, z);
        return this;
    }

    @Override
    public void next() {
        this.delegate.next();
    }

    @Override
    public void fixedColor(int red, int green, int blue, int alpha) {
        this.delegate.fixedColor(red, green, blue, alpha);
    }

    @Override
    public void unfixColor() {
        this.delegate.unfixColor();
    }
}

/* (C) TAMA Studios 2025 */
package dev.codiak;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.render.VertexConsumer;

public class FluidQuadCollector implements VertexConsumer {

    public static class FluidVertex {
        public float x, y, z;
        public float r, g, b, a;
        public float u, v;
        public int light;
    }

    public List<FluidVertex> getVertices() {
        return vertices;
    }

    private final List<FluidVertex> vertices = new ArrayList<>();

    private FluidVertex current;

    // ---- VertexConsumer methods ---- //

    @Override
    public @NotNull VertexConsumer vertex(double x, double y, double z) {
        current = new FluidVertex();
        current.x = (float) x;
        current.y = (float) y;
        current.z = (float) z;
        return this;
    }

    @Override
    public @NotNull VertexConsumer color(int r, int g, int b, int a) {
        if (current != null) {
            current.r = r / 255f;
            current.g = g / 255f;
            current.b = b / 255f;
            current.a = a / 255f;
        }
        return this;
    }

    @Override
    public @NotNull VertexConsumer texture(float u, float v) {
        if (current != null) {
            current.u = u;
            current.v = v;
        }
        return this;
    }

    @Override
    public @NotNull VertexConsumer overlay(int u, int v) {
        // Fluids don’t use overlay
        return this;
    }

    @Override
    public @NotNull VertexConsumer light(int u, int v) {
        // Convert 2 ints into packed light
        if (current != null) {
            current.light = (v << 16) | (u & 0xFFFF);
        }
        return this;
    }

    @Override
    public @NotNull VertexConsumer light(int packedLight) {
        if (current != null) {
            current.light = packedLight;
        }
        return this;
    }

    @Override
    public @NotNull VertexConsumer normal(float nx, float ny, float nz) {
        // Fluids don’t provide normals
        return this;
    }

    @Override
    public void next() {
        if (current != null) {
            vertices.add(current);
            current = null;
        }
    }

    @Override
    public void fixedColor(int r, int g, int b, int a) {}

    @Override
    public void unfixColor() {}
}

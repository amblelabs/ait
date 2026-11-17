package dev.amble.ait.client.util;

import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.compat.iris.IrisCompat;
import dev.amble.ait.compat.portal.PortalsAPI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public final class PortalClip {

    public static VertexConsumerProvider wrap(VertexConsumerProvider provider, BlockPos pos, MatrixStack matrices) {
        if (!DependencyChecker.hasIris() || IrisCompat.isRenderingShadowPass())
            return provider;

        double[] plane = PortalsAPI.CLIP_PLANE.get();

        if (plane == null)
            return provider;

        double px = plane[0] - pos.getX();
        double py = plane[1] - pos.getY();
        double pz = plane[2] - pos.getZ();

        Vector4f equation = new Vector4f((float) plane[3], (float) plane[4], (float) plane[5],
                (float) -(plane[3] * px + plane[4] * py + plane[5] * pz));

        new Matrix4f(matrices.peek().getPositionMatrix()).invert().transpose().transform(equation);

        return new Provider(provider, equation);
    }

    public static boolean isClipping(VertexConsumerProvider provider) {
        return provider instanceof Provider;
    }

    private record Provider(VertexConsumerProvider parent, Vector4f equation) implements VertexConsumerProvider {

        @Override
        public VertexConsumer getBuffer(RenderLayer layer) {
            VertexConsumer buffer = this.parent.getBuffer(layer);

            if (layer.getDrawMode() != VertexFormat.DrawMode.QUADS
                    || layer.getVertexFormat() != VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL)
                return buffer;

            return new Clipper(buffer, this.equation);
        }
    }

    private static final class Clipper implements VertexConsumer {

        private static final int STRIDE = 12;

        private final VertexConsumer parent;
        private final Vector4f equation;

        private final float[] in = new float[4 * STRIDE];
        private final int[] inOverlay = new int[4];
        private final int[] inLight = new int[4];
        private final float[] distance = new float[4];

        private final float[] out = new float[8 * STRIDE];
        private final int[] outOverlay = new int[8];
        private final int[] outLight = new int[8];

        private int count;

        private Clipper(VertexConsumer parent, Vector4f equation) {
            this.parent = parent;
            this.equation = equation;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            int base = this.count * STRIDE;
            this.in[base] = (float) x;
            this.in[base + 1] = (float) y;
            this.in[base + 2] = (float) z;
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            int base = this.count * STRIDE;
            this.in[base + 3] = red / 255f;
            this.in[base + 4] = green / 255f;
            this.in[base + 5] = blue / 255f;
            this.in[base + 6] = alpha / 255f;
            return this;
        }

        @Override
        public VertexConsumer texture(float u, float v) {
            int base = this.count * STRIDE;
            this.in[base + 7] = u;
            this.in[base + 8] = v;
            return this;
        }

        @Override
        public VertexConsumer overlay(int u, int v) {
            this.inOverlay[this.count] = (u & 0xFFFF) | (v << 16);
            return this;
        }

        @Override
        public VertexConsumer light(int u, int v) {
            this.inLight[this.count] = (u & 0xFFFF) | (v << 16);
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            int base = this.count * STRIDE;
            this.in[base + 9] = x;
            this.in[base + 10] = y;
            this.in[base + 11] = z;
            return this;
        }

        @Override
        public void next() {
            if (++this.count < 4)
                return;

            this.count = 0;
            this.flush();
        }

        @Override
        public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float u, float v,
                           int overlay, int light, float normalX, float normalY, float normalZ) {
            int base = this.count * STRIDE;
            this.in[base] = x;
            this.in[base + 1] = y;
            this.in[base + 2] = z;
            this.in[base + 3] = red;
            this.in[base + 4] = green;
            this.in[base + 5] = blue;
            this.in[base + 6] = alpha;
            this.in[base + 7] = u;
            this.in[base + 8] = v;
            this.in[base + 9] = normalX;
            this.in[base + 10] = normalY;
            this.in[base + 11] = normalZ;
            this.inOverlay[this.count] = overlay;
            this.inLight[this.count] = light;
            this.next();
        }

        @Override
        public void fixedColor(int red, int green, int blue, int alpha) {
            this.parent.fixedColor(red, green, blue, alpha);
        }

        @Override
        public void unfixColor() {
            this.parent.unfixColor();
        }

        private void flush() {
            boolean anyIn = false;
            boolean anyOut = false;

            for (int i = 0; i < 4; i++) {
                int base = i * STRIDE;
                float d = this.equation.x() * this.in[base] + this.equation.y() * this.in[base + 1]
                        + this.equation.z() * this.in[base + 2] + this.equation.w();

                this.distance[i] = d;

                if (d >= 0) anyIn = true;
                else anyOut = true;
            }

            if (!anyIn)
                return;

            if (!anyOut) {
                for (int i = 0; i < 4; i++)
                    this.emit(this.in, this.inOverlay, this.inLight, i);

                return;
            }

            int n = 0;

            for (int i = 0; i < 4; i++) {
                int j = (i + 1) & 3;
                float di = this.distance[i];
                float dj = this.distance[j];

                if (di >= 0)
                    this.copy(i, n++);

                if ((di >= 0) != (dj >= 0))
                    this.lerp(i, j, di / (di - dj), n++);
            }

            for (int k = 1; k + 1 < n; k += 2) {
                this.emit(this.out, this.outOverlay, this.outLight, 0);
                this.emit(this.out, this.outOverlay, this.outLight, k);
                this.emit(this.out, this.outOverlay, this.outLight, k + 1);
                this.emit(this.out, this.outOverlay, this.outLight, Math.min(k + 2, n - 1));
            }
        }

        private void copy(int from, int to) {
            System.arraycopy(this.in, from * STRIDE, this.out, to * STRIDE, STRIDE);
            this.outOverlay[to] = this.inOverlay[from];
            this.outLight[to] = this.inLight[from];
        }

        private void lerp(int a, int b, float t, int to) {
            int baseA = a * STRIDE;
            int baseB = b * STRIDE;
            int baseOut = to * STRIDE;

            for (int e = 0; e < STRIDE; e++)
                this.out[baseOut + e] = this.in[baseA + e] + (this.in[baseB + e] - this.in[baseA + e]) * t;

            this.outOverlay[to] = this.inOverlay[a];
            this.outLight[to] = this.inLight[a];
        }

        private void emit(float[] data, int[] overlay, int[] light, int index) {
            int base = index * STRIDE;
            this.parent.vertex(data[base], data[base + 1], data[base + 2], data[base + 3], data[base + 4],
                    data[base + 5], data[base + 6], data[base + 7], data[base + 8], overlay[index], light[index],
                    data[base + 9], data[base + 10], data[base + 11]);
        }
    }
}

package dev.amble.ait.client.overlays;

import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Matrix4f;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.tardis.Tardis;

public class RWFOverlay implements HudRenderCallback {

    private static final Identifier TEXTURE = AITMod.id("textures/gui/overlay/rwf_overlay.png");

    private static final int TEXT = 0xFFD7E9F7;
    private static final int TEXT_DIM = 0xFF6D93AD;
    private static final int GOOD = 0xFF63D68A;
    private static final int WARN = 0xFFE8B44A;
    private static final int ALERT = 0xFFE5484D;

    private static final int BEZEL_W = 256;
    private static final int BEZEL_H = 143;
    private static final int BEZEL_SLICE = 6;
    private static final int INSET = 4;

    private static final int QUARTER_U = 96, QUARTER_V = 182, QUARTER_W = 67, QUARTER_H = 56;
    private static final int BAR_U = 96, BAR_V = 238, BAR_W = 75, BAR_H = 17;
    private static final int TAPE_U = 172, TAPE_V = 146, TAPE_W = 17, TAPE_H = 109;
    private static final int TAPE_TOP = 6, TAPE_BOTTOM = 103;
    private static final int DIAL_U = 190, DIAL_V = 214, DIAL_W = 65, DIAL_H = 41;
    private static final float DIAL_CX = 32.5f, DIAL_CY = 29f;
    private static final int RING_INNER = 21;
    private static final int RING_LABEL = 35;
    private static final float SPEED_EMPTY = 165f, SPEED_FULL = 255f;
    private static final float FUEL_EMPTY = 15f, FUEL_FULL = -75f;
    private static final float CUBE_SIZE = 8f;

    private static final int SKY = 0xFF3FA8E0;
    private static final int GROUND = 0xFF8A5A2B;
    private static final int NORTH_MARK = 0xFFE5484D;
    private static final float NEEDLE_EASE = 0.25f;

    private static final int MINOR_U = 19, MINOR_W = 3, MINOR_H = 4;
    private static final int MAJOR_U = 10, MAJOR_W = 3, MAJOR_H = 6;
    private static final int TICK_V = 145;

    private static final int DOT_U = 4, DOT_V = 200, DOT_W = 3, DOT_H = 3;
    private static final int CURSOR_U = 15, CURSOR_V = 200, CURSOR_W = 6, CURSOR_H = 4;
    private static final int DASH_U = 29, DASH_V = 200, DASH_W = 6, DASH_H = 2;

    private static final int ICON_LIT_V = 161;
    private static final int ICON_DIM_V = 175;
    private static final int ICON_H = 11;
    private static final int[] ICON_U = {4, 19, 31, 44, 59};
    private static final int[] ICON_W = {7, 7, 8, 8, 10};
    private static final int ICON_PITCH = 13;

    private static final int HEADING_SPAN = 120;
    private static final String[] CARDINALS = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
    private static final int ALT_SPAN = 64;

    private static float velocity;
    private static float shownThrottle;
    private static float shownFuel;

    private static float scale = 1f;
    private static int screenWidth;
    private static int screenHeight;

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.world == null)
            return;

        if (!(client.player.getVehicle() instanceof FlightTardisEntity entity) || !entity.isLinked())
            return;

        AbstractClientPlayerEntity player = client.player;
        Tardis tardis = entity.tardis().get();

        velocity = MathHelper.lerp(0.15f, velocity, (float) instantVelocity(player));

        float ease = 1f - (float) Math.pow(1f - NEEDLE_EASE, client.getLastFrameDuration());
        int maxSpeed = Math.max(1, tardis.travel().maxSpeed().get());

        shownThrottle = MathHelper.lerp(ease, shownThrottle, MathHelper.clamp(tardis.travel().speed() / (float) maxSpeed, 0, 1));
        shownFuel = MathHelper.lerp(ease, shownFuel, (float) MathHelper.clamp(tardis.fuel().getCurrentFuel() / tardis.fuel().getMaxFuel(), 0, 1));

        scale = MathHelper.clamp(AITModClient.CONFIG.rwfHudScale, 0.25f, 4f);
        screenWidth = Math.round(context.getScaledWindowWidth() / scale);
        screenHeight = Math.round(context.getScaledWindowHeight() / scale);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1);

        bezel(context);
        quarter(context, client.textRenderer, player);
        heading(context, client.textRenderer, player);
        altitude(context, client.textRenderer, player);
        dial(context, client.textRenderer, player, tardis, tickDelta);
        statusLights(context, entity, tardis, player);

        context.getMatrices().pop();

        RenderSystem.disableBlend();
    }

    private static double instantVelocity(AbstractClientPlayerEntity player) {
        double dx = player.getX() - player.prevX;
        double dy = player.getY() - player.prevY;
        double dz = player.getZ() - player.prevZ;

        return Math.sqrt(dx * dx + dy * dy + dz * dz) * 20;
    }

    private static void bezel(DrawContext context) {
        int s = BEZEL_SLICE;
        int w = screenWidth;
        int h = screenHeight;
        int midW = BEZEL_W - 2 * s;
        int midH = BEZEL_H - 2 * s;

        sprite(context, 0, 0, 0, 0, s, s);
        sprite(context, w - s, 0, BEZEL_W - s, 0, s, s);
        sprite(context, 0, h - s, 0, BEZEL_H - s, s, s);
        sprite(context, w - s, h - s, BEZEL_W - s, BEZEL_H - s, s, s);

        stretch(context, s, 0, w - 2 * s, s, s, 0, midW, s);
        stretch(context, s, h - s, w - 2 * s, s, s, BEZEL_H - s, midW, s);
        stretch(context, 0, s, s, h - 2 * s, 0, s, s, midH);
        stretch(context, w - s, s, s, h - 2 * s, BEZEL_W - s, s, s, midH);
    }

    private static void quarter(DrawContext context, TextRenderer font, AbstractClientPlayerEntity player) {
        int x = INSET;
        int y = INSET;

        sprite(context, x, y, QUARTER_U, QUARTER_V, QUARTER_W, QUARTER_H);

        String[] lines = {
                dimensionName(player.getWorld().getRegistryKey().getValue().getPath()),
                "X " + player.getBlockX(),
                "Y " + player.getBlockY(),
                "Z " + player.getBlockZ()
        };

        for (int i = 0; i < lines.length; i++) {
            int lineY = y + 5 + i * 10;
            float depth = (lineY - y + 8f) / QUARTER_H;
            float room = QUARTER_W * MathHelper.sqrt(Math.max(0, 1 - depth * depth)) - 8;
            int width = font.getWidth(lines[i]);
            float fit = width > room ? Math.max(0.5f, room / width) : 1f;

            MatrixStack matrices = context.getMatrices();
            matrices.push();
            matrices.translate(x + 5, lineY, 0);
            matrices.scale(fit, fit, 1);
            context.drawText(font, lines[i], 0, 0, i == 0 ? TEXT : TEXT_DIM, true);
            matrices.pop();
        }
    }

    private static String dimensionName(String path) {
        StringBuilder name = new StringBuilder();

        for (String word : path.split("_")) {
            if (word.isEmpty())
                continue;

            if (!name.isEmpty())
                name.append(' ');

            name.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }

        return name.toString();
    }

    private static void heading(DrawContext context, TextRenderer font, AbstractClientPlayerEntity player) {
        int width = MathHelper.clamp(screenWidth - 2 * (INSET + QUARTER_W + 8), 100, 200);
        int x = (screenWidth - width) / 2;
        int centre = x + width / 2;
        int y = INSET + 1;

        float bearing = MathHelper.wrapDegrees(player.getYaw() - 180);
        float perDegree = width / (float) HEADING_SPAN;

        scissor(context, x, y, x + width, y + 18);

        for (int tick = 0; tick < 360; tick += 15) {
            int tx = centre + Math.round(MathHelper.wrapDegrees(tick - bearing) * perDegree);

            if (tick % 45 != 0) {
                sprite(context, tx - 1, y, MINOR_U, TICK_V, MINOR_W, MINOR_H);
                continue;
            }

            sprite(context, tx - 1, y, MAJOR_U, TICK_V, MAJOR_W, MAJOR_H);

            String label = CARDINALS[tick / 45];
            int color = tick == 0 ? ALERT : tick % 90 == 0 ? TEXT : TEXT_DIM;
            context.drawText(font, label, tx - font.getWidth(label) / 2, y + 8, color, true);
        }

        context.disableScissor();

        sprite(context, centre - 1, y + 18, DOT_U, DOT_V, DOT_W, DOT_H);

        String exact = String.format("%03d°", Math.floorMod(Math.round(bearing), 360));
        context.drawText(font, exact, centre - font.getWidth(exact) / 2, y + 23, TEXT, true);
    }

    private static void altitude(DrawContext context, TextRenderer font, AbstractClientPlayerEntity player) {
        int x = screenWidth - INSET - 2 - TAPE_W;
        int y = (screenHeight - TAPE_H) / 2;

        sprite(context, x, y, TAPE_U, TAPE_V, TAPE_W, TAPE_H);

        int altitude = MathHelper.floor(player.getY());
        int ground = player.getWorld().getTopY(Heightmap.Type.MOTION_BLOCKING, player.getBlockX(), player.getBlockZ());
        int agl = Math.max(0, altitude - ground);

        float clearance = MathHelper.clamp(agl / (float) ALT_SPAN, 0, 1);
        int cursorY = y + TAPE_BOTTOM - Math.round(clearance * (TAPE_BOTTOM - TAPE_TOP)) - CURSOR_H / 2;

        sprite(context, x + 5, cursorY, CURSOR_U, CURSOR_V, CURSOR_W, CURSOR_H);

        String aglText = String.valueOf(agl);
        int aglColor = agl < 6 ? ALERT : agl < 20 ? WARN : GOOD;
        context.drawText(font, aglText, x - 3 - font.getWidth(aglText), cursorY - 2, aglColor, true);

        String altText = String.valueOf(altitude);
        context.drawText(font, altText, x - 3 - font.getWidth(altText), y, TEXT, true);
    }

    private static void dial(DrawContext context, TextRenderer font, AbstractClientPlayerEntity player, Tardis tardis,
            float tickDelta) {
        int x = (screenWidth - DIAL_W) / 2;
        int y = screenHeight - INSET - DIAL_H;
        float cx = x + DIAL_CX;
        float cy = y + DIAL_CY;

        sprite(context, x, y, DIAL_U, DIAL_V, DIAL_W, DIAL_H);

        attitudeCube(context, cx, cy, player, tickDelta);

        context.fill(Math.round(cx) - 3, Math.round(cy), Math.round(cx) + 4, Math.round(cy) + 1, WARN);
        context.fill(Math.round(cx), Math.round(cy) - 2, Math.round(cx) + 1, Math.round(cy) + 1, WARN);

        ringMarker(context, cx, cy, MathHelper.lerp(shownThrottle, SPEED_EMPTY, SPEED_FULL), 0xFFFFFFFF);
        ringMarker(context, cx, cy, MathHelper.lerp(shownFuel, FUEL_EMPTY, FUEL_FULL), gaugeColor(shownFuel));

        ringLabel(context, font, cx, cy, FUEL_FULL - 8, "F", TEXT);
        ringLabel(context, font, cx, cy, FUEL_EMPTY + 8, "E", ALERT);

        String velocityText = Math.round(velocity) + " m/s";
        context.drawText(font, velocityText, Math.round(cx) - font.getWidth(velocityText) / 2, y - 10, TEXT, true);

        int speed = tardis.travel().speed();
        int maxSpeed = Math.max(1, tardis.travel().maxSpeed().get());
        String reading = speed + "/" + maxSpeed;
        context.drawText(font, reading, x - 3 - font.getWidth(reading), y + DIAL_H - 9,
                throttleColor(speed / (float) maxSpeed), true);
    }

    private static void ringMarker(DrawContext context, float cx, float cy, float angle, int tint) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(cx, cy, 0);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(angle));

        RenderSystem.setShaderColor(((tint >> 16) & 0xFF) / 255f, ((tint >> 8) & 0xFF) / 255f, (tint & 0xFF) / 255f, 1f);
        sprite(context, RING_INNER, -1, DASH_U, DASH_V, DASH_W, DASH_H);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        matrices.pop();
    }

    private static void ringLabel(DrawContext context, TextRenderer font, float cx, float cy, float angle, String text,
            int color) {
        float rad = angle * MathHelper.RADIANS_PER_DEGREE;
        int lx = Math.round(cx + MathHelper.cos(rad) * RING_LABEL) - font.getWidth(text) / 2;
        int ly = Math.round(cy + MathHelper.sin(rad) * RING_LABEL) - 4;

        context.drawText(font, text, lx, ly, color, true);
    }

    private static void attitudeCube(DrawContext context, float cx, float cy, AbstractClientPlayerEntity player,
            float tickDelta) {
        float yaw = player.getYaw(tickDelta) * MathHelper.RADIANS_PER_DEGREE;
        float pitch = player.getPitch(tickDelta) * MathHelper.RADIANS_PER_DEGREE;

        float sinYaw = MathHelper.sin(yaw), cosYaw = MathHelper.cos(yaw);
        float sinPitch = MathHelper.sin(pitch), cosPitch = MathHelper.cos(pitch);

        Vec3d forward = new Vec3d(-sinYaw * cosPitch, -sinPitch, cosYaw * cosPitch);
        Vec3d right = new Vec3d(-cosYaw, 0, -sinYaw);
        Vec3d up = new Vec3d(-sinYaw * sinPitch, cosPitch, cosYaw * sinPitch);

        context.draw();

        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        for (Direction direction : Direction.values()) {
            Vec3d normal = Vec3d.of(direction.getVector());
            float facing = (float) normal.dotProduct(forward);

            if (facing <= 0.01f)
                continue;

            float shade = 0.55f + 0.45f * facing;
            CubeView view = new CubeView(matrix, buffer, cx, cy, right, up);

            if (direction.getAxis() == Direction.Axis.Y) {
                view.quad(normal, new Vec3d(1, 0, 0), new Vec3d(0, 0, 1), -1, 1, -1, 1,
                        direction == Direction.UP ? SKY : GROUND, shade);
                continue;
            }

            Vec3d across = new Vec3d(-normal.z, 0, normal.x);
            Vec3d vertical = new Vec3d(0, 1, 0);

            view.quad(normal, across, vertical, -1, 1, 0, 1, SKY, shade);
            view.quad(normal, across, vertical, -1, 1, -1, 0, GROUND, shade);

            if (direction == Direction.NORTH)
                view.quad(normal, across, vertical, -0.3f, 0.3f, 0.3f, 0.8f, NORTH_MARK, shade);
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
    }

    private record CubeView(Matrix4f matrix, BufferBuilder buffer, float cx, float cy, Vec3d right, Vec3d up) {

        void quad(Vec3d normal, Vec3d u, Vec3d v, float u0, float u1, float v0, float v1, int color, float shade) {
            int r = Math.round(((color >> 16) & 0xFF) * shade);
            int g = Math.round(((color >> 8) & 0xFF) * shade);
            int b = Math.round((color & 0xFF) * shade);

            this.vertex(normal, u, v, u0, v0, r, g, b);
            this.vertex(normal, u, v, u1, v0, r, g, b);
            this.vertex(normal, u, v, u1, v1, r, g, b);
            this.vertex(normal, u, v, u0, v1, r, g, b);
        }

        private void vertex(Vec3d normal, Vec3d u, Vec3d v, float a, float b, int r, int g, int bl) {
            Vec3d corner = normal.add(u.multiply(a)).add(v.multiply(b));

            float sx = this.cx + (float) corner.dotProduct(this.right) * CUBE_SIZE;
            float sy = this.cy - (float) corner.dotProduct(this.up) * CUBE_SIZE;

            this.buffer.vertex(this.matrix, sx, sy, 0).color(r, g, bl, 255).next();
        }
    }

    private static void statusLights(DrawContext context, FlightTardisEntity entity, Tardis tardis,
            AbstractClientPlayerEntity player) {
        int x = INSET;
        int y = screenHeight - INSET - BAR_H;

        sprite(context, x, y, BAR_U, BAR_V, BAR_W, BAR_H);

        boolean gravUsable = tardis.subsystems().get(SubSystem.Id.GRAVITATIONAL) instanceof DurableSubSystem circuit
                && circuit.isUsable();

        boolean[] lit = {
                player.getY() - player.prevY > 0.01,
                tardis.travel().antigravs().get(),
                gravUsable,
                entity.isPhasing(),
                tardis.fuel().hasPower() && tardis.travel().speed() > 0
        };

        for (int i = 0; i < lit.length; i++) {
            int iconX = x + 4 + i * ICON_PITCH + (ICON_PITCH - 3 - ICON_W[i]) / 2;
            sprite(context, iconX, y + 5, ICON_U[i], lit[i] ? ICON_LIT_V : ICON_DIM_V, ICON_W[i], ICON_H);
        }
    }

    private static void sprite(DrawContext context, int x, int y, int u, int v, int width, int height) {
        context.drawTexture(TEXTURE, x, y, u, v, width, height);
    }

    private static void stretch(DrawContext context, int x, int y, int width, int height, int u, int v,
            int regionWidth, int regionHeight) {
        if (width <= 0 || height <= 0)
            return;

        context.drawTexture(TEXTURE, x, y, width, height, u, v, regionWidth, regionHeight, 256, 256);
    }

    private static void scissor(DrawContext context, int x1, int y1, int x2, int y2) {
        context.enableScissor(MathHelper.floor(x1 * scale), MathHelper.floor(y1 * scale),
                MathHelper.ceil(x2 * scale), MathHelper.ceil(y2 * scale));
    }

    private static int gaugeColor(float percent) {
        if (percent > 0.5f)
            return GOOD;

        return percent > 0.2f ? WARN : ALERT;
    }

    private static int throttleColor(float percent) {
        if (percent > 0.75f)
            return ALERT;

        return percent > 0.4f ? WARN : GOOD;
    }
}

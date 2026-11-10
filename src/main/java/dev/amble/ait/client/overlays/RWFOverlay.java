package dev.amble.ait.client.overlays;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
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
    private static final int TRACK = 0x80000000;

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
    private static final int NEEDLE_LEN = 17;

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

        scale = MathHelper.clamp(AITModClient.CONFIG.rwfHudScale, 0.25f, 4f);
        screenWidth = Math.round(context.getScaledWindowWidth() / scale);
        screenHeight = Math.round(context.getScaledWindowHeight() / scale);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1);

        bezel(context);
        quarter(context, client.textRenderer, tardis);
        heading(context, client.textRenderer, player);
        altitude(context, client.textRenderer, player);
        dial(context, client.textRenderer, tardis);
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

    private static void quarter(DrawContext context, TextRenderer font, Tardis tardis) {
        int x = INSET;
        int y = INSET;

        sprite(context, x, y, QUARTER_U, QUARTER_V, QUARTER_W, QUARTER_H);

        String reading = String.valueOf(Math.round(velocity));

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x + 5, y + 6, 0);
        matrices.scale(2, 2, 1);
        context.drawText(font, reading, 0, 0, TEXT, false);
        matrices.pop();

        context.drawText(font, "m/s", x + 5 + font.getWidth(reading) * 2 + 3, y + 14, TEXT_DIM, false);

        float artron = (float) (tardis.fuel().getCurrentFuel() / tardis.fuel().getMaxFuel());
        bar(context, x + 5, y + 27, 34, 3, artron, gaugeColor(artron));
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

        String coords = Math.round(player.getX()) + " / " + Math.round(player.getZ());
        context.drawText(font, coords, centre - font.getWidth(coords) / 2, y + 33, TEXT_DIM, true);
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

    private static void dial(DrawContext context, TextRenderer font, Tardis tardis) {
        int x = (screenWidth - DIAL_W) / 2;
        int y = screenHeight - INSET - DIAL_H;

        sprite(context, x, y, DIAL_U, DIAL_V, DIAL_W, DIAL_H);

        int speed = tardis.travel().speed();
        int maxSpeed = Math.max(1, tardis.travel().maxSpeed().get());
        float throttle = MathHelper.clamp(speed / (float) maxSpeed, 0, 1);

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x + DIAL_CX, y + DIAL_CY, 0);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f + 180f * throttle));

        context.fill(0, 0, NEEDLE_LEN - DASH_W, 1, ALERT);
        sprite(context, NEEDLE_LEN - DASH_W, 0, DASH_U, DASH_V, DASH_W, DASH_H);

        matrices.pop();

        String reading = speed + "/" + maxSpeed;
        context.drawText(font, reading, Math.round(x + DIAL_CX) - font.getWidth(reading) / 2,
                Math.round(y + DIAL_CY) + 2, throttleColor(throttle), true);
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

    private static void bar(DrawContext context, int x, int y, int width, int height, float percent, int color) {
        context.fill(x, y, x + width, y + height, TRACK);
        context.fill(x, y, x + Math.round(width * MathHelper.clamp(percent, 0, 1)), y + height, color);
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

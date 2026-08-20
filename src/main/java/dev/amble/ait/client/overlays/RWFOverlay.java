package dev.amble.ait.client.overlays;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;

import dev.amble.ait.core.AITKeyBinds;
import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.tardis.Tardis;

public class RWFOverlay implements HudRenderCallback {

    private static final int PANEL = 0xB4061323; // console room shadow
    private static final int TRACK = 0xFF0B1B2B;
    private static final int EDGE = 0xFF1B4C77;
    private static final int BRACKET = 0xFFE0A94A; // police box lamp
    private static final int ROTOR = 0xFF6FD8F5;
    private static final int ROTOR_DIM = 0xFF1E4A63;

    private static final int TEXT = 0xFFD7E9F7;
    private static final int TEXT_DIM = 0xFF6D93AD;
    private static final int GOOD = 0xFF63D68A;
    private static final int WARN = 0xFFE8B44A;
    private static final int ALERT = 0xFFE5484D;

    private static final int MARGIN = 6;
    private static final int GUTTER = 4;
    private static final int BRACKET_LEN = 5;
    private static final int BAR_H = 4;

    private static final int PANEL_W = 112;
    private static final int HEADER_H = 12;
    private static final int VELOCITY_H = 26;
    private static final int GAUGE_H = 20;
    private static final int STATUS_H = 14;

    private static final int ROUNDEL = 7;
    private static final int ROUNDEL_PITCH = 11;

    private static final int TAPE_H = 16;
    private static final int HEADING_SPAN = 120; // degrees visible across the heading tape
    private static final String[] CARDINALS = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};

    private static final int ALT_TAPE_W = 36;
    private static final int ALT_SPAN = 64; // blocks visible across the altitude tape

    private static float velocity;

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

        heading(context, client, player);
        systems(context, client, tardis, player.age + tickDelta);
        position(context, client, player);
        controls(context, client);
    }

    private static double instantVelocity(AbstractClientPlayerEntity player) {
        double dx = player.getX() - player.prevX;
        double dy = player.getY() - player.prevY;
        double dz = player.getZ() - player.prevZ;

        return Math.sqrt(dx * dx + dy * dy + dz * dz) * 20;
    }

    private static void heading(DrawContext context, MinecraftClient client, AbstractClientPlayerEntity player) {
        TextRenderer font = client.textRenderer;

        int screen = context.getScaledWindowWidth();
        int width = MathHelper.clamp(screen - 2 * (MARGIN + PANEL_W + GUTTER), 120, 240);
        int x = (screen - width) / 2;
        int centre = x + width / 2;

        panel(context, x, MARGIN, width, TAPE_H);

        float bearing = MathHelper.wrapDegrees(player.getYaw() - 180);
        float perDegree = width / (float) HEADING_SPAN;

        context.enableScissor(x + 1, MARGIN + 1, x + width - 1, MARGIN + TAPE_H - 1);

        for (int tick = 0; tick < 360; tick += 15) {
            int tx = centre + Math.round(MathHelper.wrapDegrees(tick - bearing) * perDegree);

            if (tick % 45 != 0) {
                vline(context, tx, MARGIN + 2, 3, alpha(ROTOR, 0.4f));
                continue;
            }

            vline(context, tx, MARGIN + 1, 4, ROTOR);

            String label = CARDINALS[tick / 45];
            int color = tick == 0 ? BRACKET : tick % 90 == 0 ? TEXT : TEXT_DIM;

            context.drawText(font, label, tx - font.getWidth(label) / 2, MARGIN + 6, color, false);
        }

        context.disableScissor();
        vline(context, centre, MARGIN + 1, TAPE_H - 2, BRACKET);

        // exact bearing, in a notch hanging off the bottom of the tape
        String exact = String.format("%03d°", Math.floorMod(Math.round(bearing), 360));
        int notchW = font.getWidth(exact) + 10;
        int notchX = centre - notchW / 2;
        int notchY = MARGIN + TAPE_H - 1;

        context.fill(notchX, notchY, notchX + notchW, notchY + 12, PANEL);
        hline(context, notchX, notchY + 11, notchW, BRACKET);
        vline(context, notchX, notchY, 12, EDGE);
        vline(context, notchX + notchW - 1, notchY, 12, EDGE);
        context.drawText(font, exact, notchX + 5, notchY + 2, BRACKET, false);
    }

    private static void systems(DrawContext context, MinecraftClient client, Tardis tardis, float time) {
        TextRenderer font = client.textRenderer;

        int speed = tardis.travel().speed();
        int maxSpeed = Math.max(1, tardis.travel().maxSpeed().get());
        float throttle = speed / (float) maxSpeed;

        int height = HEADER_H + VELOCITY_H + 3 * GAUGE_H + STATUS_H + 4;
        int left = MARGIN + 3 + ROUNDEL + 4;
        int content = PANEL_W - (left - MARGIN) - 5;

        panel(context, MARGIN, MARGIN, PANEL_W, height);
        header(context, font, MARGIN, MARGIN, PANEL_W, Text.translatable("overlay.ait.rwf.hud.title"));

        // the rotor runs faster the harder she's pushed
        rail(context, MARGIN + 3, MARGIN + HEADER_H + 2, height - HEADER_H - 5, time * (0.04f + 0.14f * throttle));

        int y = MARGIN + HEADER_H + 3;

        context.drawText(font, Text.translatable("overlay.ait.rwf.hud.velocity"), left, y, TEXT_DIM, false);

        String reading = String.valueOf(Math.round(velocity));
        context.getMatrices().push();
        context.getMatrices().translate(left, y + 9, 0);
        context.getMatrices().scale(2, 2, 1);
        context.drawText(font, reading, 0, 0, TEXT, false);
        context.getMatrices().pop();
        context.drawText(font, "m/s", left + font.getWidth(reading) * 2 + 4, y + 17, TEXT_DIM, false);

        y += VELOCITY_H;

        gauge(context, font, left, y, content, Text.translatable("overlay.ait.rwf.hud.throttle"),
                speed + "/" + maxSpeed, throttleColor(throttle));
        throttleBar(context, left, y + 10, content, speed, maxSpeed);

        y = gravCircuit(context, font, left, y + GAUGE_H, content, tardis);

        float artron = (float) (tardis.fuel().getCurrentFuel() / tardis.fuel().getMaxFuel());
        gauge(context, font, left, y, content, Text.translatable("overlay.ait.rwf.hud.artron"),
                Math.round(artron * 100) + "%", gaugeColor(artron));
        bar(context, left, y + 10, content, artron, gaugeColor(artron));

        y += GAUGE_H;

        boolean antigravs = tardis.travel().antigravs().get();
        context.drawText(font, Text.translatable("overlay.ait.rwf.hud.antigravs"), left, y, TEXT_DIM, false);
        pill(context, font, left + content, y, Text.translatable(antigravs
                ? "overlay.ait.rwf.hud.engaged" : "overlay.ait.rwf.hud.idle"), antigravs ? GOOD : TEXT_DIM);
    }

    private static int gravCircuit(DrawContext context, TextRenderer font, int x, int y, int width, Tardis tardis) {
        if (!(tardis.subsystems().get(SubSystem.Id.GRAVITATIONAL) instanceof DurableSubSystem circuit))
            return y;

        float percent = circuit.durability() / DurableSubSystem.MAX_DURABILITY;
        Text label = Text.translatable("overlay.ait.rwf.hud.grav");

        if (circuit.isUsable()) {
            gauge(context, font, x, y, width, label, Math.round(percent * 100) + "%", gaugeColor(percent));
            bar(context, x, y + 10, width, percent, gaugeColor(percent));
        } else {
            gauge(context, font, x, y, width, label,
                    Text.translatable("overlay.ait.rwf.hud.offline").getString(), ALERT);
            bar(context, x, y + 10, width, percent, alpha(ALERT, 0.5f));
        }

        return y + GAUGE_H;
    }

    private static void position(DrawContext context, MinecraftClient client, AbstractClientPlayerEntity player) {
        TextRenderer font = client.textRenderer;

        int screen = context.getScaledWindowWidth();
        int tapeX = screen - MARGIN - ALT_TAPE_W;
        int tapeH = MathHelper.clamp(context.getScaledWindowHeight() - 2 * MARGIN - 62, 80, 150);
        int centreY = MARGIN + tapeH / 2;

        String coords = Math.round(player.getX()) + " / " + Math.round(player.getZ());
        int coordW = Math.max(76, font.getWidth(coords) + 10);
        int coordX = tapeX - GUTTER - coordW;

        panel(context, coordX, MARGIN, coordW, 24);
        context.drawText(font, Text.translatable("overlay.ait.rwf.hud.position"), coordX + 5, MARGIN + 4, TEXT_DIM,
                false);
        context.drawText(font, coords, coordX + 5, MARGIN + 13, TEXT, false);

        panel(context, tapeX, MARGIN, ALT_TAPE_W, tapeH);

        double altitude = player.getY();
        float perBlock = tapeH / (float) ALT_SPAN;
        int first = MathHelper.floor((altitude - ALT_SPAN / 2f) / 8) * 8;

        context.enableScissor(tapeX + 1, MARGIN + 1, tapeX + ALT_TAPE_W - 1, MARGIN + tapeH - 1);

        for (int level = first; level <= altitude + ALT_SPAN / 2f; level += 8) {
            int ly = centreY - Math.round((float) (level - altitude) * perBlock);
            boolean major = level % 32 == 0;
            int length = major ? 7 : 4;

            hline(context, tapeX + ALT_TAPE_W - 1 - length, ly, length, major ? ROTOR : alpha(ROTOR, 0.4f));

            if (major)
                context.drawText(font, String.valueOf(level), tapeX + 4, ly - 3, TEXT_DIM, false);
        }

        context.disableScissor();
        altimeter(context, font, tapeX, centreY, MathHelper.floor(altitude));

        // ground clearance, for landing
        int ground = player.getWorld().getTopY(Heightmap.Type.MOTION_BLOCKING, player.getBlockX(), player.getBlockZ());
        int agl = Math.max(0, MathHelper.floor(altitude) - ground);
        int aglY = MARGIN + tapeH + GUTTER;

        panel(context, tapeX, aglY, ALT_TAPE_W, 22);
        context.drawText(font, Text.translatable("overlay.ait.rwf.hud.agl"), tapeX + 5, aglY + 4, TEXT_DIM, false);
        context.drawText(font, String.valueOf(agl), tapeX + 5, aglY + 13, agl < 6 ? ALERT : agl < 20 ? WARN : TEXT,
                false);
    }

    private static void altimeter(DrawContext context, TextRenderer font, int tapeX, int centreY, int altitude) {
        String reading = String.valueOf(altitude);
        int width = Math.max(32, font.getWidth(reading) + 12);
        int x = tapeX - width;
        int y = centreY - 7;

        context.fill(x, y, x + width, y + 14, PANEL);
        hline(context, x, y, width, BRACKET);
        hline(context, x, y + 13, width, BRACKET);
        vline(context, x, y, 14, BRACKET);
        context.drawText(font, reading, x + width - 6 - font.getWidth(reading), y + 3, BRACKET, false);

        for (int i = 0; i < 4; i++)
            context.fill(tapeX + i, centreY - 3 + i, tapeX + i + 1, centreY + 4 - i, BRACKET);
    }

    private static void controls(DrawContext context, MinecraftClient client) {
        TextRenderer font = client.textRenderer;

        KeyBinding[] binds = {client.options.sneakKey, client.options.jumpKey,
                AITKeyBinds.TOGGLE_ANTIGRAVS.binding(), AITKeyBinds.DECREASE_SPEED.binding(),
                AITKeyBinds.INCREASE_SPEED.binding(), AITKeyBinds.PHASE.binding()};
        String[] actions = {"land", "ascend", "antigravs", "decrease_speed", "increase_speed", "phase"};

        int capW = 0;
        int labelW = 0;

        for (int i = 0; i < binds.length; i++) {
            capW = Math.max(capW, font.getWidth(binds[i].getBoundKeyLocalizedText()) + 8);
            labelW = Math.max(labelW, font.getWidth(Text.translatable("overlay.ait.rwf.hud.action." + actions[i])));
        }

        int width = capW + labelW + 15;
        int height = HEADER_H + binds.length * 12 + 4;
        int y = context.getScaledWindowHeight() - 30 - height;

        panel(context, MARGIN, y, width, height);
        header(context, font, MARGIN, y, width, Text.translatable("overlay.ait.rwf.hud.controls"));

        y += HEADER_H + 3;

        for (int i = 0; i < binds.length; i++) {
            keycap(context, font, MARGIN + 5, y - 2, capW, binds[i].getBoundKeyLocalizedText());
            context.drawText(font, Text.translatable("overlay.ait.rwf.hud.action." + actions[i]),
                    MARGIN + 10 + capW, y, TEXT_DIM, false);

            y += 12;
        }
    }

    private static void panel(DrawContext context, int x, int y, int width, int height) {
        context.fill(x, y, x + width, y + height, PANEL);

        hline(context, x, y, width, EDGE);
        hline(context, x, y + height - 1, width, EDGE);
        vline(context, x, y, height, EDGE);
        vline(context, x + width - 1, y, height, EDGE);

        int right = x + width - BRACKET_LEN;
        int bottom = y + height - BRACKET_LEN;

        hline(context, x, y, BRACKET_LEN, BRACKET);
        hline(context, right, y, BRACKET_LEN, BRACKET);
        hline(context, x, y + height - 1, BRACKET_LEN, BRACKET);
        hline(context, right, y + height - 1, BRACKET_LEN, BRACKET);

        vline(context, x, y, BRACKET_LEN, BRACKET);
        vline(context, x, bottom, BRACKET_LEN, BRACKET);
        vline(context, x + width - 1, y, BRACKET_LEN, BRACKET);
        vline(context, x + width - 1, bottom, BRACKET_LEN, BRACKET);
    }

    private static void header(DrawContext context, TextRenderer font, int x, int y, int width, Text title) {
        context.drawText(font, title, x + 5, y + 3, BRACKET, false);
        hline(context, x + 1, y + HEADER_H - 1, width - 2, EDGE);
    }

    private static void rail(DrawContext context, int x, int y, int height, float phase) {
        for (int i = 0; i * ROUNDEL_PITCH + ROUNDEL <= height; i++) {
            float glow = Math.max(0, MathHelper.sin(phase - i * 0.7f));

            roundel(context, x, y + i * ROUNDEL_PITCH, mix(ROTOR_DIM, ROTOR, glow * glow));
        }
    }

    private static void roundel(DrawContext context, int x, int y, int core) {
        context.fill(x + 1, y, x + ROUNDEL - 1, y + 1, ROTOR_DIM);
        context.fill(x + 1, y + ROUNDEL - 1, x + ROUNDEL - 1, y + ROUNDEL, ROTOR_DIM);
        context.fill(x, y + 1, x + 1, y + ROUNDEL - 1, ROTOR_DIM);
        context.fill(x + ROUNDEL - 1, y + 1, x + ROUNDEL, y + ROUNDEL - 1, ROTOR_DIM);
        context.fill(x + 2, y + 2, x + ROUNDEL - 2, y + ROUNDEL - 2, core);
    }

    private static void gauge(DrawContext context, TextRenderer font, int x, int y, int width, Text label,
            String value, int color) {
        context.drawText(font, label, x, y, TEXT_DIM, false);
        context.drawText(font, value, x + width - font.getWidth(value), y, color, false);
    }

    private static void bar(DrawContext context, int x, int y, int width, float percent, int color) {
        context.fill(x, y, x + width, y + BAR_H, TRACK);
        context.fill(x, y, x + Math.round(width * MathHelper.clamp(percent, 0, 1)), y + BAR_H, color);

        for (int i = 1; i < 4; i++)
            vline(context, x + width * i / 4, y, BAR_H, TRACK);
    }

    private static void throttleBar(DrawContext context, int x, int y, int width, int speed, int max) {
        if (max * 3 > width) {
            bar(context, x, y, width, speed / (float) max, throttleColor(speed / (float) max));
            return;
        }

        context.fill(x, y, x + width, y + BAR_H, TRACK);

        for (int i = 0; i < max; i++) {
            int start = x + Math.round(width * (i / (float) max));
            int end = x + Math.round(width * ((i + 1) / (float) max));

            context.fill(start, y, end - 1, y + BAR_H, i < speed ? throttleColor((i + 1f) / max) : alpha(ROTOR, 0.15f));
        }
    }

    private static void pill(DrawContext context, TextRenderer font, int right, int y, Text text, int color) {
        int width = font.getWidth(text) + 8;
        int x = right - width;

        context.fill(x, y - 2, x + width, y + 10, TRACK);
        hline(context, x, y - 2, width, color);
        hline(context, x, y + 9, width, color);
        context.drawText(font, text, x + 4, y, color, false);
    }

    private static void keycap(DrawContext context, TextRenderer font, int x, int y, int width, Text key) {
        context.fill(x, y, x + width, y + 12, TRACK);

        hline(context, x, y, width, EDGE);
        hline(context, x, y + 11, width, EDGE);
        vline(context, x, y, 12, EDGE);
        vline(context, x + width - 1, y, 12, EDGE);

        context.drawText(font, key, x + (width - font.getWidth(key)) / 2, y + 2, BRACKET, false);
    }

    private static void hline(DrawContext context, int x, int y, int width, int color) {
        context.fill(x, y, x + width, y + 1, color);
    }

    private static void vline(DrawContext context, int x, int y, int height, int color) {
        context.fill(x, y, x + 1, y + height, color);
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

    private static int alpha(int color, float percent) {
        return (color & 0xFFFFFF) | Math.round(255 * percent) << 24;
    }

    private static int mix(int from, int to, float delta) {
        return ColorHelper.Argb.getArgb(channel(from, to, delta, 24), channel(from, to, delta, 16),
                channel(from, to, delta, 8), channel(from, to, delta, 0));
    }

    private static int channel(int from, int to, float delta, int shift) {
        return Math.round(MathHelper.lerp(delta, (from >> shift) & 0xFF, (to >> shift) & 0xFF));
    }
}

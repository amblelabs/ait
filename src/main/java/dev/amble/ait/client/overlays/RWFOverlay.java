package dev.amble.ait.client.overlays;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.tardis.Tardis;

public class RWFOverlay implements HudRenderCallback {
    private static final int ALPHA_GRAY = ColorHelper.Argb.getArgb(125, 255, 255, 255);
    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null || mc.world == null)
            return;

        if (mc.player.hasVehicle() &&   mc.player.getVehicle() instanceof FlightTardisEntity entity) {
            if (!entity.isLinked()) return;
            Tardis tardis = entity.tardis().get();
            Yaw.render(drawContext, mc.player);
            Position.render(drawContext, mc.player, mc);
            Position.Y.render(drawContext, mc.player, mc);
            Speed.render(drawContext, mc.player, mc);
            //this.renderOverlay(drawContext, AITMod.id("textures/gui/tardis/rwf_gui.png"));
        }
    }

    private void renderOverlay(DrawContext context, Identifier texture) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.drawTexture(texture, (context.getScaledWindowWidth() / 2) - 8,
                (context.getScaledWindowHeight() / 2) - 8, 0, 0.0F, 0.0F, 16, 16, 16, 16);
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static int width() {
        return MinecraftClient.getInstance().getWindow().getScaledWidth();
    }
    private static int height() {
        return MinecraftClient.getInstance().getWindow().getScaledHeight() - 8;
    }


    private static void renderIncrementedLine(DrawContext context, int x, int gap) {
        context.drawVerticalLine(x, 0, height(), ALPHA_GRAY);

        // horizontal line every gap
        /*
        for (int i = 0; i < getScreenHeight(); i += gap) {
            context.drawHorizontalLine(x, x + 3, i, ALPHA_GRAY);
        }*/
    }

    private static class Yaw {
        private static void render(DrawContext context, AbstractClientPlayerEntity player) {
            context.drawHorizontalLine(60, width() - 60, 20, ALPHA_GRAY);
            context.drawVerticalLine(60, 16, 24, Colors.WHITE);
            context.drawVerticalLine(width() - 60, 16, 24, Colors.WHITE);

            float current = player.getYaw();

            line(context, -180, current, false);
            line(context, -90, current, false);
            line(context, 0, current, false);
            line(context, 90, current, false);
            line(context, 180, current, false);

            line(context, player.getYaw(), 0, true);
        }

        private static void line(DrawContext context, float yaw, float current, boolean isPrimary) {
            int x = isPrimary ? width() / 2 : position(yaw, current);

            int middle = width() / 2;
            if (!isPrimary && (middle - 10 <= x && x <= middle + 10)) return;

            int color = isPrimary ? Colors.WHITE : ALPHA_GRAY;

            context.drawVerticalLine(x, 16, 24, color);
            context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, Math.round(MathHelper.wrapDegrees(yaw)) + "", x, 26, color);

            Direction dir = Direction.fromRotation(yaw);
            context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, dir.asString().toUpperCase().charAt(0) + "", x, 35, color);
        }
        private static int position(float yaw, float current) {
            return (int) ((width() - 120) * (((MathHelper.wrapDegrees(yaw - current) + 180) / 360))) + 60;
        }
    }
    private static class Pitch {
        private static void render(DrawContext context, AbstractClientPlayerEntity player) {
            renderIncrementedLine(context, 10, 5);

            line(context, 0, false);
            line(context, 45, false);
            line(context, -45, false);

            line(context, player.getPitch(), true);
        }

        private static void line(DrawContext context, float pitch, boolean isPrimary) {
            int y = position(pitch);
            int color = isPrimary ? Colors.WHITE : ALPHA_GRAY;

            context.drawHorizontalLine(10, 15, y + 3, color);
            context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, Math.round(pitch) + "", 27, y, color);
        }

        private static int position(float pitch) {
            return (int) (height() * (((pitch + 90) / 180)));
        }
    }
    private static class Position {
        private static class Y {
            private static void render(DrawContext context, AbstractClientPlayerEntity player, MinecraftClient client) {
                int bottom = Math.abs(player.getWorld().getBottomY());
                int range = player.getWorld().getTopY() + (Math.min(bottom, 0));

                renderIncrementedLine(context, width() - 10, 8);

                line(context, range / 2d, bottom, range, false);
                line(context, range / 4d, bottom, range, false);
                line(context, 0d, 128, range, false);

                line(context, player.getY(), bottom, range, true);
            }

            private static void line(DrawContext context, double y, int bottom, int range, boolean isPrimary) {
                int yPosition = position(y, bottom, range);
                int color = isPrimary ? Colors.WHITE : ALPHA_GRAY;

                context.drawHorizontalLine(width() - 15, width() - 10, yPosition + 3, color);

                TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
                String text = Math.round(y) + "";
                context.drawTextWithShadow(renderer, text, width() - renderer.getWidth(text) - 17 , yPosition, color);
            }

            private static int position(double y, int bottom, int range) {
                return (int) (height() * (1f - ((float) (y + bottom) / range)));
            }
        }

        private static void render(DrawContext context, AbstractClientPlayerEntity player, MinecraftClient client) {
            String i = Math.round(player.getX()) + ", " + Math.round(player.getZ());
            context.fill(width() - 62 - client.textRenderer.getWidth(i), 48, width() - 58, 61, ALPHA_GRAY);
            context.drawTextWithShadow(client.textRenderer, i, width() - 60 - client.textRenderer.getWidth(i), 50, 0xFFFFFF);
        }
    }
    private static class Speed {
        private static void render(DrawContext context, AbstractClientPlayerEntity player, MinecraftClient client) {
            double deltaX = player.getX() - player.prevX;
            double deltaZ = player.getZ() - player.prevZ;
            double deltaY = player.getY() - player.prevY;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            String s = Math.round(distance * 20) + " m/s";

            context.fill(58, 49, 62 + client.textRenderer.getWidth(s), 60, ALPHA_GRAY);
            context.drawTextWithShadow(client.textRenderer, s, 60, 50, 0xFFFFFF);
        }
    }
}

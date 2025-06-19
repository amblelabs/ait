package dev.amble.ait.module.planet.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.config.AITClientConfig;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.module.planet.core.item.SpacesuitItem;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;

public class SpaceSuitOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, float v) {
        MinecraftClient mc = MinecraftClient.getInstance();
        MatrixStack stack = drawContext.getMatrices();

        if (mc.player == null || mc.world == null)
            return;

        Planet planet = PlanetRegistry.getInstance().get(mc.world);

        boolean isPlanetOrTARDIS = planet != null || TardisServerWorld.isTardisDimension(mc.world);

        if (!mc.options.getPerspective().isFirstPerson())
            return;

        TextRenderer textRenderer = mc.textRenderer;

        if (isPlanetOrTARDIS && mc.player.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof SpacesuitItem) {
            stack.push();
            stack.scale(1.5f, 1.5f, 1.5f);

            drawContext.drawTextWithShadow(textRenderer,
                    TardisServerWorld.isTardisDimension(mc.world) ? Text.literal("??????").formatted(Formatting.OBFUSCATED) :
                            Text.literal(this.getTemperatureType(AITModClient.CONFIG, planet)),
                    0, 0, 0xFFFFFF);

            stack.pop();
            stack.push();
            stack.scale(1.5f, 1.5f, 1.5f);
            String oxygen = "" + Planet.getOxygenInTank(mc.player);
            drawContext.drawTextWithShadow(textRenderer, Text.literal(
                    oxygen.substring(0, 3) + "L / " + SpacesuitItem.MAX_OXYGEN + "L"), 0, 50, 0xFFFFFF);
            stack.pop();
        }
    }

    public String getTemperatureType(AITClientConfig config, Planet planet) {
        return switch(config.temperatureType) {
            case CELSIUS -> ("" + planet.fahrenheit()).substring(0, 5) + "°C";
            case FAHRENHEIT -> ("" + planet.fahrenheit()).substring(0, 5) + "°F";
            case KELVIN -> planet.kelvin() + "K";
        };
    }
}

package dev.amble.ait.client.renderers.monitors;

import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationPropertyHelper;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.monitors.CRTMonitorModel;
import dev.amble.ait.core.blockentities.MonitorBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.util.MonitorUtil;
import dev.amble.ait.core.util.WorldUtil;

public class MonitorRenderer<T extends MonitorBlockEntity> implements BlockEntityRenderer<T> {

    public static final Identifier MONITOR_TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/monitors/crt_monitor.png"));
    public static final Identifier EMISSIVE_MONITOR_TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/monitors/crt_monitor_emission.png"));
    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    private final CRTMonitorModel crtMonitorModel;

    public MonitorRenderer(BlockEntityRendererFactory.Context ctx) {
        this.crtMonitorModel = new CRTMonitorModel(CRTMonitorModel.getTexturedModelData().createModel());
    }


    @Override
    public void render(MonitorBlockEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState blockState = entity.getCachedState();

        int k = blockState.get(SkullBlock.ROTATION);
        float h = RotationPropertyHelper.toDegrees(k);

        matrices.push();
        matrices.translate(0.5f, 1.5f, 0.5f);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(h));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        this.crtMonitorModel.render(matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(MONITOR_TEXTURE)), light, overlay, 1.0F,
                1.0F, 1.0F, 1.0F);
        this.crtMonitorModel.render(matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(EMISSIVE_MONITOR_TEXTURE)),
                0xF000F00, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        matrices.pop();

        if (!entity.isLinked())
            return;

        Tardis tardis = entity.tardis().get();

        if (!tardis.fuel().hasPower())
            return;

        matrices.push();
        matrices.translate(0.5, 0.75, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180 - h));
        matrices.scale(0.005f, 0.005f, 0.005f);
        matrices.translate(-50f, 0, -80);

        TravelHandler travel = tardis.travel();
        CachedDirectedGlobalPos abpp = travel.isLanded() || travel.getState() == TravelHandlerBase.State.MAT
                ? travel.position()
                : travel.getProgress();

        BlockPos abppPos = abpp.getPos();

        CachedDirectedGlobalPos abpd = tardis.travel().destination();
        BlockPos abpdPos = abpd.getPos();

        String positionPosText = " " + abppPos.getX() + ", " + abppPos.getY() + ", " + abppPos.getZ();
        Text positionDimensionText = Text.of(MonitorUtil.truncateDimensionName(WorldUtil.worldText(abpp.getDimension()).getString(), 20));

        this.textRenderer.drawWithOutline(Text.of("❌").asOrderedText(), 0, 0, 0xF00F00, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(Text.of(positionPosText).asOrderedText(), 0, 8, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(positionDimensionText.asOrderedText(), 0, 16, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(WorldUtil.rot2Text(abpp.getRotation()).asOrderedText(), 0, 24, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);

        String destinationPosText = " " + abpdPos.getX() + ", " + abpdPos.getY() + ", " + abpdPos.getZ();
        Text destinationDimensionText = Text.of(MonitorUtil.truncateDimensionName(WorldUtil.worldText(abpd.getDimension(), false).getString(), 20));


        this.textRenderer.drawWithOutline(Text.of("✛").asOrderedText(), 0, 40, 0x00F0FF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(Text.of(destinationPosText).asOrderedText(), 0, 48, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(destinationDimensionText.asOrderedText(), 0, 56, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(WorldUtil.rot2Text(abpd.getRotation()).asOrderedText(), 0, 64, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);

        String fuelText = Math.round((tardis.getFuel() / FuelHandler.TARDIS_MAX_FUEL) * 100) + "%";
        this.textRenderer.drawWithOutline(Text.of("⛽").asOrderedText(), 0, 78, 0xFAF000, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(Text.of(fuelText).asOrderedText(), 8, 78, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);

        String flightTimeText = tardis.travel().getState() == TravelHandlerBase.State.LANDED
                ? "0%"
                : tardis.travel().getDurationAsPercentage() + "%";
        this.textRenderer.drawWithOutline(Text.of("⏳").asOrderedText(), 0, 92, 0x00FF0F, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(Text.of(flightTimeText).asOrderedText(), 8, 92, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);

        String name = tardis.stats().getName();
        this.textRenderer.drawWithOutline(Text.of(name).asOrderedText(), 98 - (this.textRenderer.getWidth(name)), 90,
                0xFFFFFF, 0x000000, matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);

        if (tardis.alarm().enabled().get())
            this.textRenderer.drawWithOutline(Text.of("⚠").asOrderedText(), 84, 0, 0xFE0000, 0x000000,
                    matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);

        matrices.pop();
    }
}

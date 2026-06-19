package dev.amble.ait.client.renderers.monitors;

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
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.models.monitors.CRTMonitorModel;
import dev.amble.ait.core.blockentities.MonitorBlockEntity;
import dev.amble.ait.core.blocks.MonitorBlock;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.util.MonitorStateUtil;
import dev.amble.ait.core.util.MonitorUtil;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.lib.data.CachedDirectedGlobalPos;

public class MonitorRenderer<T extends MonitorBlockEntity> implements BlockEntityRenderer<T> {


    private static final Identifier MONITOR_TEXTURE_DEFAULT = new Identifier(AITMod.MOD_ID, "textures/blockentities/monitors/crt_monitor.png");
    private static final Identifier MONITOR_TEXTURE_BLAZE = new Identifier(AITMod.MOD_ID, "textures/blockentities/monitors/crt_monitor/blaze.png");
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

        MonitorStateUtil state = entity.getCachedState().get(MonitorBlock.TEXTURE);

        Identifier texture = switch (state) {
            case BLAZE -> MONITOR_TEXTURE_BLAZE;
            default -> MONITOR_TEXTURE_DEFAULT;
        };

        BlockState blockState = entity.getCachedState();

        int k = blockState.get(SkullBlock.ROTATION);
        float h = RotationPropertyHelper.toDegrees(k);

        matrices.push();
        matrices.translate(0.5f, 1.5f, 0.5f);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(h));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        this.crtMonitorModel.render(matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(texture)), light, overlay, 1.0F,
                1.0F, 1.0F, 1.0F);
        if (state == MonitorStateUtil.DEFAULT) {
            this.crtMonitorModel.render(matrices,
                    vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(EMISSIVE_MONITOR_TEXTURE)),
                    0xF000F00, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        matrices.pop();

        if (!entity.isLinked())
            return;

        Tardis tardis = entity.tardis().get();

        if (!tardis.fuel().hasPower())
            return;

        if (!AITModClient.CONFIG.showCRTMonitorText)
            return;

        matrices.push();
        matrices.translate(0.5, 0.75, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180 - h));
        matrices.scale(0.005f, 0.005f, 0.005f);
        matrices.translate(-50f, 0, -77);

        TravelHandler travel = tardis.travel();
        CachedDirectedGlobalPos abpp = travel.isLanded() || travel.getState() == TravelHandlerBase.State.MAT
                ? travel.position()
                : travel.getProgress();

        BlockPos abppPos = abpp.getPos();

        CachedDirectedGlobalPos abpd = tardis.travel().destination();
        BlockPos abpdPos = abpd.getPos();

        String positionPosText = abppPos.getX() + ", " + abppPos.getY() + ", " + abppPos.getZ();
        Text positionDimensionText = Text.of(MonitorUtil.truncateDimensionName(WorldUtil.worldText(abpp.getDimension()).getString(), 20));

        this.textRenderer.drawWithOutline(Text.of("\uD83D\uDCCD").asOrderedText(), 4, 4, 0x00EEFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(Text.of(positionPosText).asOrderedText(), 12, 4, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(positionDimensionText.asOrderedText(), 12, 12, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(WorldUtil.rot2Text(abpp.getRotation()).asOrderedText(), 12, 20, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);

        String destinationPosText = abpdPos.getX() + ", " + abpdPos.getY() + ", " + abpdPos.getZ();
        Text destinationDimensionText = Text.of(MonitorUtil.truncateDimensionName(WorldUtil.worldText(abpd.getDimension(), false).getString(), 20));


        this.textRenderer.drawWithOutline(Text.of("\uD83E\uDC97").asOrderedText(), 4, 40, 0xFF0000, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(Text.of(destinationPosText).asOrderedText(), 12, 40, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(destinationDimensionText.asOrderedText(), 12, 48, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(WorldUtil.rot2Text(abpd.getRotation()).asOrderedText(), 12, 56, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);

        String fuelText = Math.round((tardis.getFuel() / FuelHandler.TARDIS_MAX_FUEL) * 100) + "%";
        this.textRenderer.drawWithOutline(Text.translatable("ait.monitor.fuel_with_text", fuelText).asOrderedText(), 12, 78, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);

        String flightTimeText = tardis.travel().getState() == TravelHandlerBase.State.LANDED
                ? "0%"
                : tardis.travel().getDurationAsPercentage() + "%";
        this.textRenderer.drawWithOutline(Text.of("⏳: " + flightTimeText).asOrderedText(), 12, 88, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);

        String name = tardis.stats().getName();
        this.textRenderer.drawWithOutline(Text.of(name).asOrderedText(),  50-(this.textRenderer.getWidth(name) / 2), 102,
                0xFFFFFF, 0x000000, matrices.peek().getPositionMatrix(), vertexConsumers, light);

        if (tardis.alarm().isEnabled())
            this.textRenderer.drawWithOutline(Text.of("⚠").asOrderedText(), 84, 0, 0xFE0000, 0x000000,
                    matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);

        matrices.pop();
    }
}
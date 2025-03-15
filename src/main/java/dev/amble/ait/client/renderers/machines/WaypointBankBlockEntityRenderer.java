package dev.amble.ait.client.renderers.machines;

import dev.amble.lib.data.DirectedGlobalPos;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.util.ClientItemUtil;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.blockentities.WaypointBankBlockEntity;
import dev.amble.ait.core.blocks.WaypointBankBlock;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.util.WorldUtil;

public class WaypointBankBlockEntityRenderer<T extends WaypointBankBlockEntity> implements BlockEntityRenderer<T> {

    private static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    private static final String SEPARATOR = "------------";

    private static final ModelIdentifier WAYPOINT = new ModelIdentifier(AITMod.MOD_ID,
            Registries.ITEM.getId(AITItems.WAYPOINT_CARTRIDGE).getPath(), "inventory");

    public WaypointBankBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertices, int light,
            int overlay) {
        if (!entity.isLinked())
            return;

        Tardis tardis = entity.tardis().get();

        if (!tardis.fuel().hasPower())
            return;

        float facing = entity.getCachedState().get(WaypointBankBlock.FACING).asRotation();

        WaypointBankBlockEntity.WaypointData[] waypoints = entity.getWaypoints();
        int selectedIndex = entity.getSelected();

        if (selectedIndex != -1)
            renderMonitor(matrices, vertices, facing, waypoints, selectedIndex);

        BakedModel cartridgeModel = WaypointBankBlockEntityRenderer.cartridgeModel();
        int i = 0;

        matrices.push();

        matrices.translate(0.5f, 2, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));
        matrices.translate(-0.0625f * 11.5, 0.0625 * 5, 0.0625f * 5.5);

        matrices.push();
        for (; i < WaypointBankBlock.MAX_COUNT / 2; i++) {
            renderCartridge(matrices, vertices, light, overlay, cartridgeModel, waypoints, selectedIndex, i);
        }
        matrices.pop();

        matrices.push();
        matrices.translate(0.0625f * 7, 0, 0);

        for (; i < WaypointBankBlock.MAX_COUNT; i++) {
            renderCartridge(matrices, vertices, light, overlay, cartridgeModel, waypoints, selectedIndex, i);
        }

        matrices.pop();

        matrices.pop();
    }

    private static void renderLabel(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String text, int y) {
        textRenderer.draw(text, 0 - ((float) textRenderer.getWidth(text) / 2), y, 0x00F0FF, false,
                matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0xF000F0,
                0xF000F0);
    }

    private static void renderLabel(MatrixStack matrices, VertexConsumerProvider vertexConsumers, OrderedText text,
            int y) {
        textRenderer.draw(text, 0 - ((float) textRenderer.getWidth(text) / 2), y, 0x00F0FF, false,
                matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0xF000F0,
                0xF000F0);
    }

    private static void renderMonitor(MatrixStack matrices, VertexConsumerProvider vertices, float facing,
            WaypointBankBlockEntity.WaypointData[] data, int selectedIndex) {
        WaypointBankBlockEntity.WaypointData selected = data[selectedIndex];

        if (selected == null)
            return;

        DirectedGlobalPos abpd = selected.pos();

        int rotation = abpd.getRotation();
        BlockPos abpdPos = abpd.getPos();

        String destPos = abpdPos.getX() + ", " + abpdPos.getY() + ", " + abpdPos.getZ();
        String destDim = WorldUtil.worldText(abpd.getDimension(), false).getString();

        matrices.push();
        matrices.translate(0.5, 0.75, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(facing));

        matrices.scale(0.01f, 0.01f, 0.01f);
        matrices.translate(0f, -142f, -51f);

        renderLabel(matrices, vertices, selected.name(), 35);

        renderLabel(matrices, vertices, SEPARATOR, 46);
        renderLabel(matrices, vertices, destPos, 55);

        renderLabel(matrices, vertices, WorldUtil.rot2Text(rotation).asOrderedText(), 67);
        renderLabel(matrices, vertices, destDim, 78);

        String which = (selectedIndex + 1) + "/" + WaypointBankBlock.MAX_COUNT;
        renderLabel(matrices, vertices, which, 96);
        matrices.pop();
    }

    private static void renderCartridge(MatrixStack matrices, VertexConsumerProvider vertices, int light, int overlay,
            BakedModel model, WaypointBankBlockEntity.WaypointData[] data, int selected, int current) {
        matrices.translate(0, 0, 0.0625f * 2);
        WaypointBankBlockEntity.WaypointData waypoint = data[current];

        if (waypoint == null)
            return;

        if (current == selected) {
            matrices.push();
            matrices.translate(0, 0.0625f * 2, 0);
            ClientItemUtil.renderBakedItemModel(model, waypoint.color(), 0x0F000F0, overlay, matrices, vertices);
            matrices.pop();
        } else {
            ClientItemUtil.renderBakedItemModel(model, waypoint.color(), light, overlay, matrices, vertices);
        }
    }

    private static BakedModel cartridgeModel() {
        return MinecraftClient.getInstance().getBakedModelManager().getModel(WAYPOINT);
    }
}

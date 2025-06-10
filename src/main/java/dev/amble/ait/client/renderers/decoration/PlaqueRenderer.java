package dev.amble.ait.client.renderers.decoration;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.decoration.PlaqueModel;
import dev.amble.ait.core.blockentities.PlaqueBlockEntity;
import dev.amble.ait.core.blocks.PlaqueBlock;
import dev.amble.ait.core.tardis.Tardis;

public class PlaqueRenderer<T extends PlaqueBlockEntity> implements BlockEntityRenderer<T> {

    public static final Identifier PLAQUE_TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/decoration/plaque.png"));
    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    private final PlaqueModel plaqueModel;

    public PlaqueRenderer(BlockEntityRendererFactory.Context ctx) {
        this.plaqueModel = new PlaqueModel(PlaqueModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(PlaqueBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        BlockState blockState = entity.getCachedState();

        Direction k = blockState.get(PlaqueBlock.FACING);

        matrices.push();

        matrices.translate(0.5f, 1.5f, 0.5f);

        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(k.asRotation()));

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        this.plaqueModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(PLAQUE_TEXTURE)),
                light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        matrices.pop();

        if (!entity.isLinked() || entity.tardis().isEmpty())
            return;

        Tardis tardis = entity.tardis().get();

        matrices.push();
        matrices.translate(0.5, 0.75, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(k.asRotation()));
        matrices.scale(0.01f, 0.01f, 0.01f);
        float xVal = 0;
        matrices.translate(xVal, -35f, 35f);

        this.textRenderer.drawWithOutline(Text.of(tardis.stats().getCreationString()).asOrderedText(),
                xVal - ((float) this.textRenderer.getWidth(tardis.stats().getCreationString()) / 2), 35, 0xFFFFFF,
                0x000000, matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(Text.of(entity.getPlaqueText()).asOrderedText(),
                xVal - ((float) this.textRenderer.getWidth(entity.getPlaqueText()) / 2), 55, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(Text.of(tardis.stats().getName()).asOrderedText(),
                xVal - ((float) this.textRenderer.getWidth(tardis.stats().getName()) / 2), 75, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);

        matrices.pop();
    }
}

package dev.amble.ait.client.renderers.consoles;

import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.consoles.ConsoleGeneratorModel;
import dev.amble.ait.client.models.consoles.ConsoleModel;
import dev.amble.ait.core.blockentities.ConsoleGeneratorBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.registry.impl.console.variant.ClientConsoleVariantRegistry;

public class ConsoleGeneratorRenderer<T extends ConsoleGeneratorBlockEntity> implements BlockEntityRenderer<T> {

    private final ConsoleGeneratorModel generator;
    private final EntityRenderDispatcher dispatcher;

    public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/consoles/console_generator/console_generator.png");

    public ConsoleGeneratorRenderer(BlockEntityRendererFactory.Context ctx) {
        this.dispatcher = ctx.getEntityRenderDispatcher();
        this.generator = new ConsoleGeneratorModel(ConsoleGeneratorModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            int light, int overlay) {

        if (ClientConsoleVariantRegistry.getInstance().get(entity.getConsoleVariant().id()) == null)
            return;

        if (entity.getWorld() == null)
            return;

        if (!entity.isLinked())
            return;

        ConsoleModel console = ClientConsoleVariantRegistry.getInstance().get(entity.getConsoleVariant().id()).model();
        Identifier consoleTexture = ClientConsoleVariantRegistry.getInstance().get(entity.getConsoleVariant().id())
                .texture();
        Identifier consoleEmission = ClientConsoleVariantRegistry.getInstance().get(entity.getConsoleVariant().id()).emission();

        //boolean powered = entity.isPowered();

        Tardis tardis = entity.tardis().get();
        if (/*powered && */!tardis.isUnlocked(entity.getConsoleVariant())) {
            matrices.push();

            matrices.translate(0.5F, 2.75F, 0.5F);
            matrices.multiply(this.dispatcher.getRotation());
            matrices.scale(-0.1F, -0.1F, 0.1F);

            Text text = Text.literal("\uD83D\uDD12");
            Text type = Text.literal("Console Type: " + entity.getConsoleVariant().id().getPath().replace("console/", "").replace("_", " ").toUpperCase());
            Text requirement = Text.literal("Requires Loyalty Level: " + (entity.getConsoleVariant().requirement().isPresent() ?
                            entity.getConsoleVariant().requirement().get().type() : "None"));
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            float h = (float) (-textRenderer.getWidth(text) / 2);
            float p = (float) (-textRenderer.getWidth(requirement) / 2);
            float l = (float) (-textRenderer.getWidth(type) / 2);

            Matrix4f matrix4f = matrices.peek().getPositionMatrix();

            textRenderer.draw(text, h + 0.35f, 0.0F, 0xFFFFFFFF, false, matrix4f, vertexConsumers,
                    TextRenderer.TextLayerType.NORMAL, 0x000000, 0xf000f0);
            matrices.push();
            matrices.scale(0.2f, 0.2f, 0.2f);
            Matrix4f matrixcf = matrices.peek().getPositionMatrix();
            textRenderer.draw(type, l - 0.35f, 42.5F, ColorHelper.Argb.getArgb(1, 0, 175, 235), false, matrixcf, vertexConsumers,
                    TextRenderer.TextLayerType.NORMAL, 0x000000, 0xf000f0);
            matrices.pop();
            matrices.push();
            matrices.scale(0.2f, 0.2f, 0.2f);
            Matrix4f matrixdf = matrices.peek().getPositionMatrix();
            textRenderer.draw(requirement, p - 0.35f, 55F, ColorHelper.Argb.getArgb(1, 255, 205, 0), false, matrixdf, vertexConsumers,
                    TextRenderer.TextLayerType.NORMAL, 0x000000, 0xf000f0);
            matrices.pop();
            matrices.pop();
        }

        matrices.push();

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.translate(0.5f, -1.5f, -0.5f);

        this.generator.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE)), light,
                overlay, 1, 1, 1, 1);

        matrices.pop();

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));

        matrices.translate(0.5f, -1.5f + entity.getWorld().random.nextFloat() * 0.02, -0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MinecraftClient.getInstance().getTickDelta() % 180));

        //if (powered) {
            if (tardis.isUnlocked(entity.getConsoleVariant())) {
                console.render(matrices,
                        vertexConsumers.getBuffer(entity.getConsoleVariant().getClient().equals(ClientConsoleVariantRegistry.COPPER) ? RenderLayer.getEntityTranslucent(consoleTexture) :
                                RenderLayer.getEntityTranslucentCull(consoleTexture)), 0xf000f0, overlay, 0.3607843137f,
                        0.9450980392f, 1, entity.getWorld().random.nextInt(32) != 6 ? 0.4f : 0.05f);
                console.render(matrices,
                        vertexConsumers.getBuffer(entity.getConsoleVariant().getClient().equals(ClientConsoleVariantRegistry.COPPER) ? RenderLayer.getEntityTranslucent(consoleTexture) :
                                RenderLayer.getEntityTranslucentCull(consoleEmission)), 0xf000f0, overlay, 0.3607843137f,
                        0.9450980392f, 1, entity.getWorld().random.nextInt(32) != 6 ? 0.4f : 0.05f);
            } else {
                console.render(matrices,
                        vertexConsumers.getBuffer(entity.getConsoleVariant().getClient().equals(ClientConsoleVariantRegistry.COPPER) ? RenderLayer.getEntityTranslucent(consoleTexture) :
                                RenderLayer.getEntityTranslucentCull(consoleTexture)), light,
                        OverlayTexture.DEFAULT_UV, 0.2f, 0.2f, 0.2f,
                        entity.getWorld().random.nextInt(32) != 6 ? 0.4f : 0.05f);
            }
        //}
        matrices.pop();
    }
}

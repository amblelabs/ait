package dev.amble.ait.client.renderers.entities;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.consoles.ControlModel;
import dev.amble.ait.client.renderers.SonicRendering;
import dev.amble.ait.core.entities.ConsoleControlEntity;
import dev.amble.ait.core.tardis.Tardis;

@Environment(value = EnvType.CLIENT)
public class ControlEntityRenderer extends LivingEntityRenderer<ConsoleControlEntity, ControlModel> {

    private static final Identifier TEXTURE = AITMod.id("textures/entity/control/sequenced.png");

    ControlModel model = new ControlModel(ControlModel.getTexturedModelData().createModel());

    public ControlEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ControlModel(ControlModel.getNotModelData().createModel()), 0f);
    }

    @Override
    public void render(ConsoleControlEntity livingEntity, float yaw, float tickDelta, MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider, int light) {
        super.render(livingEntity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);

        if (SonicRendering.isPlayerHoldingScanningSonic() && AITMod.CONFIG.CLIENT.SHOW_CONTROL_HITBOXES) {
            renderOutline(livingEntity, matrixStack, vertexConsumerProvider);
        }
    }

    @Override
    protected void renderLabelIfPresent(ConsoleControlEntity entity, Text text, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light) {
        double d = this.dispatcher.getSquaredDistanceToCamera(entity);

        if (d > 4096.0)
            return;

        Text name = Text.translatable(entity.control.getId()
                .toTranslationKey("control"));

        TextRenderer textRenderer = this.getTextRenderer();
        float h = (float) -textRenderer.getWidth(name) / 2;
        float f = entity.getNameLabelHeight() - 0.3f;

        Tardis tardis = entity.tardis().get();

        if (tardis == null)
            return;

        matrices.push();
        matrices.translate(0.0f, f, 0.0f);
        matrices.multiply(this.dispatcher.getRotation());
        matrices.scale(-0.0075f, -0.0075f, 0.0075f);

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        HitResult hitresult = MinecraftClient.getInstance().crosshairTarget;

        if (hitresult != null) {
            boolean isPlayerLookingWithSonic = isPlayerLookingAtControlWithSonic(hitresult, entity);
            OrderedText orderedText = name.asOrderedText();

            if (isPlayerLookingWithSonic) {
                textRenderer.drawWithOutline(orderedText, h, (float) name.getString().length(), 0xF0F0F0, 0x000000,
                        matrix4f, vertexConsumers, 0xFF);
            }
        }

        matrices.pop();

        if (hitresult == null)
            return;

        boolean sonicInConsole = isScanningSonicInConsole(tardis);
        PlayerEntity player = MinecraftClient.getInstance().player;

        if (!sonicInConsole || !entity.isPartOfSequence()/* || tardis.loyalty().get(player).isOf(Loyalty.Type.PILOT)*/)
            return;

        matrices.push();
        matrices.scale(0.4f, 0.4f, 0.4f);
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(180f));
        matrices.translate(0, (-2 - entity.getControlHeight() / 2) + entity.getWorld().random.nextFloat() * 0.02, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MinecraftClient.getInstance().getTickDelta() % 180));

        float alpha = entity.getWorld().random.nextInt(32) != 6 ? 0.4f : 0.05f;
        float red = entity.wasSequenced() ? 0.0f : 1.0f;
        float green = (entity.wasSequenced()) ? 1.0f : 1 - (entity.getSequencePercentage());

        this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE)), light,
                OverlayTexture.DEFAULT_UV,
                red,
                green,
                0,
                alpha);

        this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEyes(TEXTURE)), 0xFF00F0,
                OverlayTexture.DEFAULT_UV,
                red,
                green,
                0,
                alpha);

        matrices.pop();
    }

    private static void renderOutline(LivingEntity entity, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers) {
        VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.LINES);

        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        WorldRenderer.drawBox(matrices, vertices, box, 0.0f, 0.8f, 1.0f, 1.0f);
    }

    private static boolean isPlayerLookingAtControlWithSonic(HitResult hitResult, ConsoleControlEntity entity) {
        PlayerEntity player = MinecraftClient.getInstance().player;

        if (player == null || !(hitResult instanceof EntityHitResult entityHit))
            return false;

        Entity hitEntity = entityHit.getEntity();

        if (hitEntity == null)
            return false;

        ItemStack sonic = SonicRendering.getSonicStack(player);

        if (sonic == null)
            return false;

        return hitEntity.equals(entity) && SonicRendering.isScanningSonic(sonic);
    }

    private static boolean isScanningSonicInConsole(Tardis tardis) {
        if (tardis.sonic() == null) return false;
        ItemStack sonic = tardis.sonic().getConsoleSonic();

        if (sonic == null)
            return false;

        return SonicRendering.isScanningSonic(sonic);
    }

    @Override
    public Identifier getTexture(ConsoleControlEntity controlEntity) {
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(ConsoleControlEntity controlEntity, MatrixStack matrixStack, float f, float g,
            float h) {
    }
}

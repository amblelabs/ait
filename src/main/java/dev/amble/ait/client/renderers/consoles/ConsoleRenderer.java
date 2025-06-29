package dev.amble.ait.client.renderers.consoles;

import dev.amble.ait.client.AITModClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;

import dev.amble.ait.client.models.consoles.ConsoleModel;
import dev.amble.ait.client.models.consoles.HartnellConsoleModel;
import dev.amble.ait.client.models.items.HandlesModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientLightUtil;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.item.HandlesItem;
import dev.amble.ait.data.datapack.DatapackConsole;
import dev.amble.ait.data.schema.console.ClientConsoleVariantSchema;
import dev.amble.ait.data.schema.console.variant.crystalline.client.ClientCrystallineVariant;
import dev.amble.ait.registry.impl.console.variant.ClientConsoleVariantRegistry;

public class ConsoleRenderer<T extends ConsoleBlockEntity> implements BlockEntityRenderer<T> {

    private ClientConsoleVariantSchema variant;
    private ConsoleModel model;

    public ConsoleRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                       int light, int overlay) {

        if (entity.getWorld() == null) return;

        if (!entity.isLinked()) {
            matrices.push();
            matrices.translate(0.5, 1.5, 0.5);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
            HartnellConsoleModel model = new HartnellConsoleModel(HartnellConsoleModel.getTexturedModelData().createModel());
            model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(ClientConsoleVariantRegistry.HARTNELL.texture())),
                    light, overlay, 1, 1, 1, 1);
            RenderLayer layer = DependencyChecker.hasIris()
                    ? AITRenderLayers.tardisEmissiveCullZOffset(ClientConsoleVariantRegistry.HARTNELL.emission(), true)
                    : AITRenderLayers.getBeaconBeam(ClientConsoleVariantRegistry.HARTNELL.emission(), true);
            model.render(matrices, vertexConsumers.getBuffer(layer),
                    0xf000f0, overlay, 1, 1, 1, 1);
            matrices.pop();
            return;
        }

        ClientTardis tardis = entity.tardis().get().asClient();
        Profiler profiler = entity.getWorld().getProfiler();

        this.renderConsole(profiler, tardis, entity, matrices, vertexConsumers, light, overlay);

        if (variant instanceof ClientCrystallineVariant)
            this.renderPanes(tardis, entity, matrices, vertexConsumers, light, overlay);
    }

    private void renderPanes(ClientTardis tardis, T entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!tardis.fuel().hasPower()) return;
        matrices.push();
        matrices.translate(1, 2 + entity.getWorld().random.nextFloat() * 0.02, 0.5);
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(180f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(30f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MinecraftClient.getInstance().getTickDelta() % 180));
        matrices.translate(0.58, 0.1, -0.25);
        matrices.scale(0.9f, 0.9f, 0.9f);

        MinecraftClient.getInstance().getItemRenderer().
                renderItem(new ItemStack(Items.ORANGE_STAINED_GLASS_PANE),
                        ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
        matrices.translate(0 + entity.getWorld().random.nextFloat() * 0.02, 0 + entity.getWorld().random.nextFloat() * 0.02, 0 + entity.getWorld().random.nextFloat() * 0.02);
        MinecraftClient.getInstance().getItemRenderer().
                renderItem(new ItemStack(Items.ORANGE_STAINED_GLASS_PANE),
                        ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
        matrices.pop();

        matrices.push();
        matrices.translate(-1, 2 + entity.getWorld().random.nextFloat() * 0.02, -0.5);
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(180f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(30f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MinecraftClient.getInstance().getTickDelta() % 180));
        matrices.translate(0.78, 0.15, -0.11);
        matrices.scale(0.9f, 0.9f, 0.9f);

        MinecraftClient.getInstance().getItemRenderer().
                renderItem(new ItemStack(Items.ORANGE_STAINED_GLASS_PANE),
                        ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
        matrices.translate(0 - entity.getWorld().random.nextFloat() * 0.02, 0 + entity.getWorld().random.nextFloat() * 0.02, 0 - entity.getWorld().random.nextFloat() * 0.02);
        MinecraftClient.getInstance().getItemRenderer().
                renderItem(new ItemStack(Items.ORANGE_STAINED_GLASS_PANE),
                        ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
        matrices.pop();
    }

    private void renderConsole(Profiler profiler, ClientTardis tardis, T entity, MatrixStack matrices,
                               VertexConsumerProvider vertexConsumers, int light, int overlay) {
        profiler.push("model");

        this.updateModel(entity);

        boolean hasPower = tardis.fuel().hasPower();

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));

        profiler.swap("animate");
        model.animateBlockEntity(entity, tardis.travel().getState(), hasPower);

        profiler.swap("render");
        model.renderWithAnimations(entity, tardis, model.getPart(),
                matrices, vertexConsumers.getBuffer(variant.equals(ClientConsoleVariantRegistry.COPPER) ? RenderLayer.getEntityTranslucent(variant.texture()) :
                        RenderLayer.getEntityTranslucentCull(variant.texture())), light, overlay,
                1, 1, 1, 1);

        matrices.pop();
        matrices.push();

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));

        if (hasPower) {
            profiler.swap("emission"); // emission {

            if (variant.emission() != null && !variant.emission().equals(DatapackConsole.EMPTY)) {
                ClientLightUtil.renderEmissive((vertices, l) -> model.renderWithAnimations(
                        entity, tardis, model.getPart(), matrices, vertices, l, overlay, 1, 1, 1,
                        1), variant.emission(), vertexConsumers);
            }
        }

        matrices.pop();

        profiler.swap("monitor");

        if (hasPower && AITModClient.CONFIG.showConsoleMonitorText) {
            model.renderMonitorText(tardis, entity, matrices, vertexConsumers, light, overlay);
        }

        profiler.swap("sonic_port"); // } emission / sonic {

        ItemStack stack = tardis.sonic().getConsoleSonic() == null ?
                tardis.butler().getHandles() : tardis.sonic().getConsoleSonic();

        if (stack == null) {
            profiler.pop(); // } sonic
            return;
        }

        int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());

        if (stack.getItem() instanceof HandlesItem) {
            matrices.push();
            matrices.translate(variant.handlesTranslations().x(), variant.handlesTranslations().y(),
                    variant.handlesTranslations().z());
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(variant.handlesRotations()[0]));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(variant.handlesRotations()[1]));
            matrices.scale(0.6f, 0.6f, 0.6f);
            HandlesModel handlesModel = new HandlesModel(HandlesModel.getTexturedModelData().createModel());
            //handlesModel.setAngles(matrices, ModelTransformationMode.GROUND, false);
            handlesModel.handles.getChild("stalk").pitch = 45f;
            handlesModel.handles.getChild("stalk").getChild("head").pitch = -0.25f;
            handlesModel.render(null, MinecraftClient.getInstance().player, stack, matrices, vertexConsumers, light, overlay, 0);
            matrices.pop();
        } else {
            matrices.push();
            matrices.translate(variant.sonicItemTranslations().x(), variant.sonicItemTranslations().y(),
                    variant.sonicItemTranslations().z());
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(variant.sonicItemRotations()[0]));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(variant.sonicItemRotations()[1]));
            matrices.scale(0.9f, 0.9f, 0.9f);
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, lightAbove,
                    overlay, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
        }

        profiler.pop(); // } sonic
    }

    private void updateModel(T entity) {
        ClientConsoleVariantSchema variant = entity.getVariant().getClient();

        if (this.variant != variant) {
            this.variant = variant;
            this.model = variant.model();
        }
    }

    @Override
    public boolean rendersOutsideBoundingBox(ConsoleBlockEntity consoleBlockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }

    @Override
    public boolean isInRenderDistance(ConsoleBlockEntity consoleBlockEntity, Vec3d vec3d) {
        return Vec3d.ofCenter(consoleBlockEntity.getPos()).multiply(1.0, 0.0, 1.0).isInRange(vec3d.multiply(1.0, 0.0, 1.0), this.getRenderDistance());
    }
}

package dev.amble.ait.client.renderers.doors;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.profiler.Profiler;

import dev.amble.ait.api.TardisComponent;
import dev.amble.ait.client.boti.BOTI;
import dev.amble.ait.client.models.doors.DoomDoorModel;
import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.client.util.ClientLightUtil;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.amble.ait.core.blocks.DoorBlock;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.BiomeHandler;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.amble.ait.registry.impl.exterior.ClientExteriorVariantRegistry;

public class DoorRenderer<T extends DoorBlockEntity> implements BlockEntityRenderer<T> {

    private ClientExteriorVariantSchema variant;
    private DoorModel model;

    public DoorRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            int light, int overlay) {
        if (!entity.isLinked() || entity.getWorld() == null)
            return;

        Profiler profiler = entity.getWorld().getProfiler();
        profiler.push("door");

        profiler.push("render");

        Tardis tardis = entity.tardis().get();

        if (tardis.siege() != null && tardis.siege().isActive())
            return;

        this.renderDoor(profiler, tardis, entity, matrices, vertexConsumers, light, overlay);
        profiler.pop();

        profiler.pop();
    }

    private void renderDoor(Profiler profiler, Tardis tardis, T entity, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        this.updateModel(tardis);

        BlockState blockState = entity.getCachedState();
        float k = blockState.get(DoorBlock.FACING).asRotation();

        Identifier texture = this.variant.texture();

        if (this.variant.equals(ClientExteriorVariantRegistry.DOOM))
            texture = tardis.door().isOpen() ? DoomDoorModel.DOOM_DOOR_OPEN : DoomDoorModel.DOOM_DOOR;

        matrices.push();
        matrices.translate(0.5, 0, 0.5);
        matrices.scale(tardis.stats().getXScale(), tardis.stats().getYScale(), tardis.stats().getZScale());
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(k));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));

        model.renderWithAnimations(entity, model.getPart(), matrices,
                vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(texture)), light, overlay, 1, 1,
                1, 1);

        /*if (tardis.overgrown().overgrown().get())
            model.renderWithAnimations(entity, model.getPart(), matrices,
                    vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(
                            tardis.overgrown().getOvergrownTexture())),
                    light, overlay, 1, 1, 1, 1);*/

        profiler.push("emission");

        boolean alarms = tardis.alarm().enabled().get();

        if (!variant.equals(ClientExteriorVariantRegistry.DOOM))
            ClientLightUtil.renderEmissivable(tardis.fuel().hasPower(), model::renderWithAnimations,
                this.variant.emission(), entity, model.getPart(), matrices, vertexConsumers, 0xf000f0, overlay, alarms ? !tardis.fuel().hasPower() ? 0.25f : 1f : 1f, alarms ? !tardis.fuel().hasPower() ? 0.01f : 0.3f : 1f,
                    alarms ? !tardis.fuel().hasPower() ? 0.01f : 0.3f : 1f, 1f);

        profiler.swap("biome");

        if (this.variant != ClientExteriorVariantRegistry.CORAL_GROWTH) {
            BiomeHandler biome = tardis.handler(TardisComponent.Id.BIOME);
            Identifier biomeTexture = biome.getBiomeKey().get(this.variant.overrides());

            if (biomeTexture != null && !texture.equals(biomeTexture)) {
                model.renderWithAnimations(entity, model.getPart(), matrices,
                        vertexConsumers.getBuffer(AITRenderLayers.getEntityCutoutNoCullZOffset(biomeTexture)), light,
                        overlay, 1, 1, 1, 1);
            }
        }

        if ((tardis.door().getLeftRot() > 0 || this.variant.hasTransparentDoors()) && !tardis.isGrowth())
            BOTI.DOOR_RENDER_QUEUE.add(entity);

        matrices.pop();
        profiler.pop();
    }

    private void updateModel(Tardis tardis) {
        if (tardis.getExterior().getVariant() == null) return;
        ClientExteriorVariantSchema variant = tardis.getExterior().getVariant().getClient();

        if (this.variant != variant) {
            this.variant = variant;
            this.model = variant.getDoor().model();
        }
    }
}

package dev.amble.ait.client.renderers.doors;

import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;

import dev.amble.ait.api.TardisComponent;
import dev.amble.ait.client.boti.BOTI;
import dev.amble.ait.client.models.doors.DoomDoorModel;
import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.client.util.ClientLightUtil;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.amble.ait.core.blocks.DoorBlock;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.BiomeHandler;
import dev.amble.ait.core.tardis.handler.DoorHandler;
import dev.amble.ait.core.tardis.handler.OvergrownHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
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
        if (entity.tardis() == null && entity.getWorld() == null) return;
        Profiler profiler = entity.getWorld().getProfiler();
        profiler.push("door");

        profiler.push("render");

        /*if (entity.getWorld().getRegistryKey().equals(World.OVERWORLD)) {
            BlockState blockState = entity.getCachedState();
            float k = blockState.get(DoorBlock.FACING).asRotation();
            matrices.push();
            matrices.translate(0.5, 1.5, 0.5);
            matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(180f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(k + 180f));
            ClientDoorRegistry.CAPSULE.model().render(matrices, vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucent(ClientExteriorVariantRegistry.CAPSULE_DEFAULT.texture())), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
            matrices.pop();
            profiler.pop();
            return;
        }*/

        if (!entity.isLinked())
            return;

        Tardis tardis = entity.tardis().get();

        this.renderDoor(profiler, tardis, entity, matrices, vertexConsumers, light, overlay);
        profiler.pop();

        profiler.pop();
    }

    private void renderDoor(Profiler profiler, Tardis tardis, T entity, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (tardis.siege().isActive())
            return;

        this.updateModel(tardis);

        BlockState blockState = entity.getCachedState();
        float k = blockState.get(DoorBlock.FACING).asRotation();

        Identifier texture = this.variant.texture();

        if (this.variant.equals(ClientExteriorVariantRegistry.DOOM))
            texture = tardis.door().isOpen() ? DoomDoorModel.DOOM_DOOR_OPEN : DoomDoorModel.DOOM_DOOR;

        if (DependencyChecker.hasPortals() && tardis.travel().getState() == TravelHandlerBase.State.LANDED
                && tardis.door().getDoorState() != DoorHandler.DoorState.CLOSED) {
            CachedDirectedGlobalPos globalPos = tardis.travel().position();

            BlockPos pos = globalPos.getPos();
            World world = globalPos.getWorld();

            /*if (world != null) {
                int lightConst = 524296;
                int i = world.getLightLevel(LightType.SKY, pos);
                int j = world.getLightLevel(LightType.BLOCK, pos);

                light = (i + j > 15
                        ? (15 * 2) + (j > 0 ? 0 : -5)
                        : world.isNight()
                                ? (i / 15) + j > 0 ? j + 13 : j
                                : i + (world.getRegistryKey().equals(World.NETHER) ? j * 2 : j))
                        * lightConst;
            }*/
        }

        matrices.push();
        matrices.translate(0.5, 0, 0.5);
        matrices.scale(tardis.stats().getXScale(), tardis.stats().getYScale(), tardis.stats().getZScale());
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(k));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));

        model.renderWithAnimations(entity, model.getPart(), matrices,
                vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(texture)), light, overlay, 1, 1,
                1 /* 0.5f */, 1);

        if (tardis.<OvergrownHandler>handler(TardisComponent.Id.OVERGROWN).overgrown().get())
            model.renderWithAnimations(entity, model.getPart(), matrices,
                    vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(
                            tardis.<OvergrownHandler>handler(TardisComponent.Id.OVERGROWN).getOvergrownTexture())),
                    light, overlay, 1, 1, 1, 1);

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

        if (tardis.door().getLeftRot() > 0 && !tardis.isGrowth())
            BOTI.DOOR_RENDER_QUEUE.add(entity);
        //    this.renderDoorBoti(entity, variant, null,
        //                    profiler, tardis, entity, matrices, vertexConsumers, light, overlay);


        matrices.pop();
        profiler.pop();
    }

    private void updateModel(Tardis tardis) {
        ClientExteriorVariantSchema variant = tardis.getExterior().getVariant().getClient();

        if (this.variant != variant) {
            this.variant = variant;
            this.model = variant.getDoor().model();
        }
    }
}

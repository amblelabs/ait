package dev.amble.ait.client.renderers.machines;


import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.machines.ToyotaSpinningRotorModel;
import dev.amble.ait.core.blockentities.ToyotaSpinningRotorBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.world.TardisServerWorld;

public class ToyotaSpinningRotorRenderer<T extends ToyotaSpinningRotorBlockEntity>
        implements BlockEntityRenderer<T> {

    public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/machines/toyota_spinning_rotor.png");

    public static final Identifier EMISSIVE_TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/machines/toyota_spinning_rotor_emission.png"));

    private final ToyotaSpinningRotorModel model;

    public ToyotaSpinningRotorRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new ToyotaSpinningRotorModel(ToyotaSpinningRotorModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(ToyotaSpinningRotorBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!entity.isLinked() && TardisServerWorld.isTardisDimension(entity.getWorld())) return;
        Tardis tardis = entity.tardis().get();
        TravelHandler travel = tardis.travel();
        matrices.push();
        matrices.scale(1f, 1f, 1f);
        matrices.translate(0.5f, 1.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));

        this.model.animateBlockEntity(entity, travel.getState(), tardis.fuel().hasPower());

        this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE)),
                light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEyes(EMISSIVE_TEXTURE)),
                light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        matrices.pop();

    }
}

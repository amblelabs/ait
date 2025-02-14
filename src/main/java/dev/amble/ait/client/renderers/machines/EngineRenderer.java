package dev.amble.ait.client.renderers.machines;

import org.joml.Vector3f;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.machines.EngineModel;
import dev.amble.ait.client.util.ClientLightUtil;
import dev.amble.ait.core.blockentities.EngineBlockEntity;
import dev.amble.ait.core.engine.impl.EngineSystem;
import dev.amble.ait.core.tardis.Tardis;

// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class EngineRenderer<T extends EngineBlockEntity> implements BlockEntityRenderer<T>, ClientLightUtil.Renderable<EngineBlockEntity> {

    public static final Identifier ENGINE_TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/machines/engine.png"));
    public static final Identifier EMISSIVE_ENGINE_TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/machines/engine_emission.png"));
    private final EngineModel engineModel;

    public EngineRenderer(BlockEntityRendererFactory.Context ctx) {
        this.engineModel = new EngineModel(EngineModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(EngineBlockEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {

        if (entity.tardis() == null || entity.tardis().isEmpty())
            return;

        Tardis tardis = entity.tardis().get();
        matrices.push();
        matrices.translate(0.5f, 1.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        matrices.scale(0.25f, 0.25f, 0.25f);
        this.engineModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(ENGINE_TEXTURE)),
                light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        if (tardis.fuel().hasPower()) {
            EngineSystem.Status status = tardis.subsystems().engine().status();
            Vector3f colours = status.colour;
            ClientLightUtil.renderEmissive(this, EMISSIVE_ENGINE_TEXTURE, entity, this.engineModel.getPart(), matrices, vertexConsumers, light, overlay, colours.x, colours.y, colours.z, (status != EngineSystem.Status.OFF) ? 1.0F : 0.0F);
        }

        matrices.pop();
    }

    @Override
    public void render(EngineBlockEntity entity, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}

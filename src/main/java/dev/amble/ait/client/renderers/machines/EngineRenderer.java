package dev.amble.ait.client.renderers.machines;

import org.joml.Vector3f;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.machines.EngineModel;
import dev.amble.ait.core.blockentities.EngineBlockEntity;
import dev.amble.ait.core.engine.impl.EngineSystem;
import dev.amble.ait.core.tardis.Tardis;

// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class EngineRenderer<T extends EngineBlockEntity> implements BlockEntityRenderer<T> {

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

        if (!entity.isLinked())
            return;

        Tardis tardis = entity.tardis().get();
        matrices.push();
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(180));
        matrices.translate(0.5, -0.867f, -0.5);

        this.engineModel.render(tardis, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(ENGINE_TEXTURE)),
                LightmapTextureManager.pack(entity.getWorld().getLightLevel(LightType.BLOCK, entity.getPos().up()), entity.getWorld().getLightLevel(LightType.SKY, entity.getPos())), overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        if (tardis.fuel().hasPower()) {
            EngineSystem.Status status = tardis.subsystems().engine().status();
            Vector3f colours = status.colour;
            this.engineModel.render(tardis, matrices, vertexConsumers.getBuffer
                            (RenderLayer.getEntityCutoutNoCullZOffset(EMISSIVE_ENGINE_TEXTURE, true)),
                    0xf000f0,
                    overlay, colours.x, colours.y, colours.z, (status !=
                            EngineSystem.Status.OFF) ? 1.0F : 0.0F);
        }

        matrices.pop();
    }
}

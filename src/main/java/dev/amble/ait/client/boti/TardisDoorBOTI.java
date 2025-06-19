package dev.amble.ait.client.boti;

import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.client.renderers.VortexUtil;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.amble.ait.core.tardis.handler.StatsHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.CategoryRegistry;

public class TardisDoorBOTI extends BOTI {
    public static void renderInteriorDoorBoti(ClientTardis tardis, DoorBlockEntity door, ClientExteriorVariantSchema variant, MatrixStack stack, Identifier frameTex, SinglePartEntityModel frame, ModelPart mask, int light) {
        ExteriorVariantSchema parent = variant.parent();
        if (!parent.hasPortals()) return;

        if (!AITModClient.CONFIG.enableTardisBOTI)
            return;

        if (MinecraftClient.getInstance().world == null
                || MinecraftClient.getInstance().player == null) return;

        stack.push();
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        MinecraftClient.getInstance().getFramebuffer().endWrite();

        BOTI_HANDLER.setupFramebuffer();

        Vec3d skyColor = MinecraftClient.getInstance().world.getSkyColor(MinecraftClient.getInstance().player.getPos(), MinecraftClient.getInstance().getTickDelta());
        if (AITModClient.CONFIG.greenScreenBOTI)
            BOTI.setFramebufferColor(BOTI_HANDLER.afbo, 0, 1, 0, 1);
        else
            BOTI.setFramebufferColor(BOTI_HANDLER.afbo, (float) skyColor.x, (float) skyColor.y, (float) skyColor.z, 1);

        BOTI.copyFramebuffer(MinecraftClient.getInstance().getFramebuffer(), BOTI_HANDLER.afbo);

        VertexConsumerProvider.Immediate botiProvider = AIT_BUF_BUILDER_STORAGE.getBotiVertexConsumer();

        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        RenderSystem.depthMask(true);
        stack.push();
        StatsHandler stats = tardis.stats();
        Vector3f scale = tardis.travel().getScale();

        stack.scale((float) parent.portalWidth() * scale.x(),
                (float) parent.portalHeight() * scale.y(), scale.z());
        Vec3d vec = parent.door().adjustPortalPos(new Vec3d(0, -0.55f, 0), Direction.NORTH);
        stack.translate(vec.x, vec.y, vec.z);
        if (tardis.travel().getState() == TravelHandlerBase.State.LANDED) {
            RenderLayer whichOne = AITModClient.CONFIG.shouldRenderBOTIInterior || AITModClient.CONFIG.greenScreenBOTI ?
                    RenderLayer.getDebugFilledBox() : RenderLayer.getEndGateway();
            float[] colorsForGreenScreen = AITModClient.CONFIG.greenScreenBOTI ? new float[]{0, 1, 0, 1} : new float[] {(float) skyColor.x, (float) skyColor.y, (float) skyColor.z};
            mask.render(stack, botiProvider.getBuffer(whichOne), 0xf000f0, OverlayTexture.DEFAULT_UV, colorsForGreenScreen[0], colorsForGreenScreen[1], colorsForGreenScreen[2], 1);
        } else {
            mask.render(stack, botiProvider.getBuffer(RenderLayer.getEntityTranslucentCull(frameTex)), 0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        }
        botiProvider.draw();
        stack.pop();
        copyDepth(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        BOTI_HANDLER.afbo.beginWrite(false);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        stack.push();
        if (!tardis.travel().autopilot() && tardis.travel().getState() != TravelHandlerBase.State.LANDED)
            stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float) MinecraftClient.getInstance().player.age / ((float) 200 / tardis.travel().speed()) * 360f));
        if (!tardis.crash().isNormal())
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) MinecraftClient.getInstance().player.age / 100 * 360f));
        stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) MinecraftClient.getInstance().player.age / 100 * 360f));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        stack.translate(0, 0, 500);
        stack.scale(1.5f, 1.5f, 1.5f);
        VortexUtil util = stats.getVortexEffects().toUtil();
        if (!tardis.travel().isLanded() /*&& !tardis.flight().isFlying()*/) {
            util.renderVortex(stack);
            util.renderVortexLayer(stack, 1.5f);
            util.renderVortexLayer(stack, 2.5f);
            /*// TODO not a clue if this will work but oh well - Loqor
            stack.push();
            stack.scale(0.9f, 0.9f, 0.9f);
            util.renderVortex(stack);
            stack.pop();*/
        }
        botiProvider.draw();
        stack.pop();

        if (!tardis.getExterior().getCategory().equals(CategoryRegistry.GEOMETRIC)) {
            stack.push();
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            stack.scale(scale.x, scale.y, scale.z);

            // TODO: use DoorRenderer/ClientLightUtil instead.
            ((DoorModel) frame).renderWithAnimations(tardis, door, frame.getPart(), stack, botiProvider.getBuffer(AITRenderLayers.getBotiInterior(variant.texture())), light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F);
            //((DoorModel) frame).render(stack, botiProvider.getBuffer(AITRenderLayers.getBotiInterior(variant.texture())), light, OverlayTexture.DEFAULT_UV, 1, 1F, 1.0F, 1.0F);
            botiProvider.draw();
            stack.pop();

            stack.push();
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            stack.scale(scale.x, scale.y, scale.z);
            if (variant.emission() != null) {
                float u;
                float t;
                float s;

                if ((stats.getName() != null && "partytardis".equals(stats.getName().toLowerCase()) || (!tardis.extra().getInsertedDisc().isEmpty()))) {
                    int m = 25;
                    int n = MinecraftClient.getInstance().player.age / m + MinecraftClient.getInstance().player.getId();
                    int o = DyeColor.values().length;
                    int p = n % o;
                    int q = (n + 1) % o;
                    float r = ((float) (MinecraftClient.getInstance().player.age % m)) / m;
                    float[] fs = SheepEntity.getRgbColor(DyeColor.byId(p));
                    float[] gs = SheepEntity.getRgbColor(DyeColor.byId(q));
                    s = fs[0] * (1f - r) + gs[0] * r;
                    t = fs[1] * (1f - r) + gs[1] * r;
                    u = fs[2] * (1f - r) + gs[2] * r;
                } else {
                    float[] hs = new float[]{1.0f, 1.0f, 1.0f};
                    s = hs[0];
                    t = hs[1];
                    u = hs[2];
                }

                boolean power = tardis.fuel().hasPower();
                boolean alarm = tardis.alarm().enabled().get();

                float red = power ? s : 0;
                float green = power ? alarm ? 0.3f : t : 0;
                float blue = power ? alarm ? 0.3f : u:  0;

                ((DoorModel) frame).renderWithAnimations(tardis, door, frame.getPart(), stack, botiProvider.getBuffer((DependencyChecker.hasIris() ? AITRenderLayers.tardisEmissiveCullZOffset(variant.emission(), true) : AITRenderLayers.getTextPolygonOffset(variant.emission()))), 0xf000f0, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
                botiProvider.draw();
            }
            stack.pop();
        }

        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

        BOTI.copyColor(BOTI_HANDLER.afbo, MinecraftClient.getInstance().getFramebuffer());

        GL11.glDisable(GL11.GL_STENCIL_TEST);

        RenderSystem.depthMask(true);

        stack.pop();
    }
}

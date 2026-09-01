package dev.amble.ait.client.boti.iris;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import org.joml.Matrix4f;

import dev.amble.ait.AITMod;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.loqor.portal.client.PortalData;
import dev.loqor.portal.client.PortalDataManager;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.PipelineManager;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;

/**
 * THROWAWAY-GRADE Phase B M1 feasibility probe: render ONE shadow world nested under a swapped Iris pipeline,
 * unclipped, and confirm the outer render survives + the interior is shaded. Rewritten wholesale for M2+.
 */
public final class IrisNestedRenderProbe {
    private static boolean loggedOnce = false;

    private IrisNestedRenderProbe() {}

    public static void run(WorldRenderContext ctx) {
        if (!DependencyChecker.isIrisShaderPackInUse()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        ClientTardis tardis = ClientTardisUtil.getCurrentTardis();
        if (tardis == null) return; // M1: only when the player is in/near a TARDIS we have data for

        PortalData data = PortalDataManager.get(tardis.getUuid());
        if (data == null || data.world() == null || data.renderer() == null) return;

        try {
            nestedRender(client, data, ctx);
        } catch (Throwable t) {
            if (!loggedOnce) {
                AITMod.LOGGER.error("Phase B M1: nested render threw (feasibility probe)", t);
                loggedOnce = true;
            }
        }
    }

    private static void nestedRender(MinecraftClient client, PortalData data, WorldRenderContext ctx) {
        PipelineManager pm = Iris.getPipelineManager();
        WorldRenderingPipeline outerPipeline = pm.getPipelineNullable();

        ClientWorld outerWorld = client.world;
        float tickDelta = ctx.tickDelta();

        try {
            // Swap the client to the shadow world so Iris.getCurrentDimension() resolves to the exterior dimension
            // and the nested renderLevel prepares that dimension's pipeline.
            client.world = data.world();

            WorldRenderer shadowRenderer = data.renderer();
            Camera shadowCamera = new Camera();
            // M1 rough camera: sit at the shadow world's spawn-ish origin looking north. Parallax/portal transform
            // is M3; M1 only asks "does it render shaded and not corrupt the outer render".
            // Camera.update(BlockView, Entity, boolean thirdPerson, boolean inverseView, float tickDelta)
            // ClientWorld implements BlockView so data.world() is the correct first arg.
            shadowCamera.update(data.world(), client.player, false, false, tickDelta);

            GameRenderer gameRenderer = client.gameRenderer;
            LightmapTextureManager lightmap = gameRenderer.getLightmapTextureManager();
            // Capture the live projection matrix (includes FOV and view-bobbing from the outer frame).
            Matrix4f projection = new Matrix4f(RenderSystem.getProjectionMatrix());
            MatrixStack matrices = new MatrixStack();

            // WorldRenderer.render(MatrixStack, float tickDelta, long limitTime, boolean renderBlockOutline,
            //                      Camera, GameRenderer, LightmapTextureManager, Matrix4f positionMatrix)
            shadowRenderer.render(matrices, tickDelta, 0L, false, shadowCamera, gameRenderer, lightmap, projection);
        } finally {
            client.world = outerWorld;
            // Restore the outer pipeline reference so the rest of the outer frame has a live pipeline again.
            if (outerWorld != null)
                pm.preparePipeline(Iris.getCurrentDimension());
            if (!loggedOnce) {
                AITMod.LOGGER.info("Phase B M1: nested render completed; outerPipeline={} restoredTo={}",
                        outerPipeline, pm.getPipelineNullable());
                loggedOnce = true;
            }
        }
    }
}

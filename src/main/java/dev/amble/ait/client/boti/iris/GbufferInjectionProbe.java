package dev.amble.ait.client.boti.iris;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.compat.DependencyChecker;
import dev.loqor.portal.client.PortalData;
import dev.loqor.portal.client.PortalDataManager;

/**
 * THROWAWAY gbuffer-injection probe. Fires at {@code WorldRenderEvents.AFTER_ENTITIES} - which runs while Iris's
 * gbuffer is bound and BEFORE its deferred pass (unlike AFTER_TRANSLUCENT, which is post-deferred/composite and
 * showed nothing). Draws the current TARDIS's baked interior terrain into the live gbuffer with Iris's terrain
 * phase set, so Iris's own deferred+composite should light it as part of the scene - with no double-composite,
 * because we are only adding draws to the existing opaque pass, not running a nested finalizeLevelRendering.
 *
 * <p>Verdict to read in-game: does the (unclipped, splattered) interior terrain come out SHADED by the pack, and
 * is the main world NOT doubled? If yes, gbuffer-injection is viable and clipping/variants are the next milestones.
 */
public final class GbufferInjectionProbe {
    private static boolean loggedError = false;
    private static boolean loggedSuccess = false;

    private GbufferInjectionProbe() {}

    public static void run(WorldRenderContext ctx) {
        if (!DependencyChecker.isIrisShaderPackInUse())
            return;

        ClientTardis tardis = ClientTardisUtil.getCurrentTardis();
        if (tardis == null)
            return;

        PortalData data = PortalDataManager.get(tardis.getUuid());
        if (data == null || data.geometry() == null)
            return;

        try {
            data.geometry().debugInjectTerrainIntoGbuffer();
            if (!loggedSuccess) {
                AITMod.LOGGER.info("Phase B gbuffer-injection probe: drew interior terrain into the gbuffer "
                        + "at AFTER_ENTITIES (read in-game: shaded? main world not doubled?)");
                loggedSuccess = true;
            }
        } catch (Throwable t) {
            if (!loggedError) {
                AITMod.LOGGER.error("Phase B gbuffer-injection probe threw", t);
                loggedError = true;
            }
        }
    }
}

package dev.amble.ait.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;

/**
 * Counts world render passes, so a renderer can tell whether it has already drawn a block entity
 * this pass.
 *
 * <p>A block entity whose renderer reports {@code rendersOutsideBoundingBox} is put in the per-chunk
 * list <em>and</em> the global no-cull list, and the world renderer walks both, so the renderer is
 * called twice for it. Skipping the second call keeps what the no-cull list is for: when the chunk
 * section is culled the first call never happens, and the global one draws instead.
 *
 * <p>A pass counter rather than a tick counter, because a portal mod renders the world several times
 * per frame and each of those passes has to draw.
 *
 * <p>{@code -Dait.dedupeDraws=false} turns the skipping off while leaving the counters in place, so
 * the difference can be measured, or ruled out, without a rebuild.
 */
@Environment(EnvType.CLIENT)
public final class ClientRenderPass {

    private static final boolean DEDUPE =
            !"false".equalsIgnoreCase(System.getProperty("ait.dedupeDraws", "true"));

    private static int current;

    private ClientRenderPass() {}

    /**
     * @return whether this block entity should be drawn now, which is true for the first call of a
     *         pass and false for the duplicate that follows it.
     */
    public static boolean shouldDraw(AbstractLinkableBlockEntity entity) {
        return !DEDUPE || entity.firstDrawOfPass(current);
    }

    public static void init() {
        WorldRenderEvents.START.register(context -> current++);
    }

    public static int current() {
        return current;
    }
}

package dev.amble.ait.client.util;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import dev.amble.ait.compat.iris.IrisCompat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import net.minecraft.block.entity.BlockEntity;

/**
 * Tracks which block entities have already been drawn this world render pass, so a renderer can
 * skip the duplicate call.
 *
 * <p>A block entity whose renderer reports {@code rendersOutsideBoundingBox} is put in the per-chunk
 * list <em>and</em> the global no-cull list, and the world renderer walks both, so the renderer is
 * called twice for it. Skipping the second call keeps what the no-cull list is for: when the chunk
 * section is culled the first call never happens, and the global one draws instead.
 *
 * <p>Cleared per pass rather than per tick, because a portal mod renders the world several times per
 * frame and each of those passes has to draw.
 *
 * <p>The set lives here rather than as a field on the block entity: this is client render state, and
 * the server has no use for it.
 */
@Environment(EnvType.CLIENT)
public final class ClientRenderPass {

    // Identity, not equality: two block entities at the same position are the same draw only if they
    // are the same object, and BlockEntity does not override equals anyway.
    private static final Set<BlockEntity> DRAWN = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * Depth of the nesting that is drawing a block entity outside the normal block entity pass, such
     * as the BOTI gbuffer injection re-rendering the interior door clipped to its doorway. Such a draw
     * must neither be skipped nor use up the entry, or the real pass that follows skips the model.
     */
    private static int suspended;

    public static void suspend() {
        suspended++;
    }

    public static void resume() {
        suspended--;
    }

    /**
     * @return whether this block entity should be drawn now, which is true for the first call of a
     *         pass and false for the duplicate that follows it. Always true in Iris's shadow pass,
     *         which runs inside the same world render and must not use up the entry.
     */
    public static boolean shouldDraw(BlockEntity entity) {
        return suspended > 0 || IrisCompat.isRenderingShadowPass() || DRAWN.add(entity);
    }

    public static void init() {
        WorldRenderEvents.START.register(context -> DRAWN.clear());
    }
}

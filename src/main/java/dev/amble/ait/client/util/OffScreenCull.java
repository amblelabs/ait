package dev.amble.ait.client.util;

import java.util.Map;
import java.util.WeakHashMap;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;

/**
 * Rejects a block entity whose geometry is entirely behind the camera.
 *
 * <p>A renderer that reports {@code rendersOutsideBoundingBox} goes on the world renderer's global
 * no-cull list, and that list is walked with no visibility test of any kind. So an exterior or a
 * console draws its full geometry, and queues its portal, while the player faces the other way. The
 * per-chunk list is frustum tested, but being on the no-cull list means the draw happens anyway.
 *
 * <p>The test is a half space, not a frustum: the plane through the camera position with the
 * camera's forward as its normal. That is far weaker than a frustum, and deliberately so.
 *
 * <ul>
 * <li>It needs no render pass state. A stashed frustum is wrong in a portal mod's nested pass,
 * because Immersive Portals cancels {@code setupTerrain} at HEAD and the event Fabric fires from its
 * RETURN never arrives, so the stash silently belongs to another dimension. The camera does not have
 * that problem: the dispatcher's camera is reassigned for each nested pass.</li>
 * <li>It is unaffected by F3 frustum capture, which freezes the frustum handed to
 * {@code setupTerrain} but not the camera.</li>
 * <li>Nothing latches. There is no flag to be left true by a mod that cancels out of the world
 * render, which would otherwise cull everything for the rest of the session.</li>
 * </ul>
 *
 * <p>Every uncertain case fails open, which costs a draw rather than losing one.
 */
@Environment(EnvType.CLIENT)
public final class OffScreenCull {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    /**
     * Model half extents, keyed on the root they were measured from. Weak so a resource reload that
     * rebuilds the models does not pin the old ones.
     */
    private static final Map<ModelPart, float[]> BOUNDS = new WeakHashMap<>();

    /** A model that could not be measured, so the walk is not retried every frame. */
    private static final float[] UNMEASURABLE = new float[0];

    /**
     * Depth of the nesting that has asked for the cull to be left alone.
     *
     * <p>The interior preview screen calls block entity renderers directly, with camera arithmetic
     * and a matrix stack of its own, while the render dispatcher still holds whatever camera the last
     * world pass left. Those block entities are in {@code client.world} at their real positions, so
     * no comparison of worlds can tell that case apart: the screen has to say so.
     */
    private static int suspended;

    public static void suspend() {
        suspended++;
    }

    public static void resume() {
        suspended--;
    }

    /**
     * For a renderer that places the model with axis aligned rotations only, so the model's own box
     * stays axis aligned in the world.
     *
     * <p>The box is symmetric about the model origin, which makes it invariant under the sign flips
     * and axis swaps such a renderer applies without having to know which ones were applied. Worth
     * the looseness: a console's tall parts, and Copper's time column reaches several blocks above
     * its own block, then cost nothing while the camera looks anywhere near the horizontal, because
     * a per axis test ignores the extent perpendicular to the look direction. A sphere would not.
     *
     * @param origin the model origin in block space, since a renderer translates before it draws and
     *               the bound is measured about the model's own zero.
     * @param slop   blocks to add in every direction, for geometry a renderer draws outside the model
     *               root, such as monitor text in a space of its own.
     */
    public static boolean boxBehindCamera(BlockEntity entity, ModelPart root, Vec3d origin, double slop) {
        return behindCamera(entity, root, origin, 1.0, slop, true);
    }

    /**
     * For a renderer that rotates the model by an arbitrary angle, where only a sphere is a valid
     * bound.
     *
     * @param origin      the model origin in block space, including any translation the renderer
     *                    applies before it rotates. An exterior's rematerialisation keyframes move it
     *                    several blocks, so that part is asked for per frame rather than padded for.
     * @param radiusScale a multiplier on the model, for a renderer that scales it.
     * @param slop        blocks to add, for movement applied after the rotation.
     */
    public static boolean sphereBehindCamera(BlockEntity entity, ModelPart root, Vec3d origin,
            double radiusScale, double slop) {
        return behindCamera(entity, root, origin, radiusScale, slop, false);
    }

    /**
     * The one test both bounds use. They differ only in the radius term: per axis it is
     * {@code sum(r_i * |look_i|)}, and for a sphere the corner length, which is what makes the sphere
     * rotation invariant and the box tighter.
     */
    private static boolean behindCamera(BlockEntity entity, ModelPart root, Vec3d origin,
            double radiusScale, double slop, boolean perAxis) {
        if (suspended > 0 || entity == null || root == null || origin == null)
            return false;

        Camera camera = cameraOrNull();

        if (camera == null)
            return false;

        float[] radii = boundsOf(root);

        if (radii == null)
            return false;

        Vec3d eye = camera.getPos();
        Vector3f look = camera.getHorizontalPlane();
        BlockPos pos = entity.getPos();

        double cx = pos.getX() + origin.x - eye.x;
        double cy = pos.getY() + origin.y - eye.y;
        double cz = pos.getZ() + origin.z - eye.z;

        double centre = cx * look.x() + cy * look.y() + cz * look.z();

        double reach;

        if (perAxis) {
            // The corner furthest along the look direction. If even that one is behind the camera
            // plane then so is every other, and no part of the box can be on screen.
            reach = (radii[0] * radiusScale + slop) * Math.abs(look.x())
                    + (radii[1] * radiusScale + slop) * Math.abs(look.y())
                    + (radii[2] * radiusScale + slop) * Math.abs(look.z());
        } else {
            reach = Math.sqrt(radii[0] * radii[0] + radii[1] * radii[1] + radii[2] * radii[2])
                    * radiusScale + slop;
        }

        return centre + reach < 0;
    }

    private static Camera cameraOrNull() {
        Camera camera = client.getBlockEntityRenderDispatcher().camera;
        return camera != null && camera.isReady() ? camera : null;
    }

    /**
     * Half extents of the model about its own origin, in blocks, measured once.
     *
     * <p>{@code forEachCuboid} composes each part's pivot, rotation and scale for us and, usefully,
     * pays no attention to {@code visible} or {@code hidden}: a part switched off this frame is still
     * counted, so the bound cannot shrink under an animation. It is one pose rather than a union over
     * every pose, which the symmetrising about the origin is generous enough to absorb for the small
     * rotations these models animate. Cuboid extents exclude their dilation, hence the epsilon.
     *
     * @return null if the model could not be measured, meaning it is never culled.
     */
    private static float[] boundsOf(ModelPart root) {
        float[] cached = BOUNDS.get(root);

        if (cached != null)
            return cached == UNMEASURABLE ? null : cached;

        try {
            float[] radii = new float[]{0, 0, 0};
            Vector4f corner = new Vector4f();
            MatrixStack matrices = new MatrixStack();

            root.forEachCuboid(matrices, (entry, path, index, cuboid) -> {
                Matrix4f matrix = entry.getPositionMatrix();

                for (int i = 0; i < 8; i++) {
                    float x = ((i & 1) == 0 ? cuboid.minX : cuboid.maxX) / 16f;
                    float y = ((i & 2) == 0 ? cuboid.minY : cuboid.maxY) / 16f;
                    float z = ((i & 4) == 0 ? cuboid.minZ : cuboid.maxZ) / 16f;

                    matrix.transform(corner.set(x, y, z, 1f));

                    radii[0] = Math.max(radii[0], Math.abs(corner.x()));
                    radii[1] = Math.max(radii[1], Math.abs(corner.y()));
                    radii[2] = Math.max(radii[2], Math.abs(corner.z()));
                }
            });

            if (!Float.isFinite(radii[0]) || !Float.isFinite(radii[1]) || !Float.isFinite(radii[2]))
                throw new IllegalStateException("model bounds are not finite");

            for (int i = 0; i < 3; i++) {
                radii[i] += 0.125f;
            }

            BOUNDS.put(root, radii);
            return radii;
        } catch (Throwable t) {
            // A model that cannot be measured is one that is never culled, rather than one that
            // brings the frame down.
            AITMod.LOGGER.error("Failed to measure a model for off screen culling", t);
            BOUNDS.put(root, UNMEASURABLE);
            return null;
        }
    }
}

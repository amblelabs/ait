package dev.amble.ait.client.renderers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.datapack.DatapackConsole;
import dev.amble.ait.data.schema.console.ClientConsoleVariantSchema;
import dev.amble.ait.mixin.client.rendering.CuboidAccessor;
import dev.amble.ait.mixin.client.rendering.ModelPartAccessor;
import dev.amble.ait.registry.impl.console.variant.ClientConsoleVariantRegistry;

/**
 * Skips the parts of a model that cannot light up, on the pass that only draws emission.
 *
 * <p>A console is submitted twice a frame, once for its base texture and once for its emission, and
 * the two submissions cost about the same because they push the same geometry. But an emission
 * texture is mostly empty: the copper console's is 99.812% fully transparent, so the emission pass
 * pushed 6870 quads in order to light 0.188% of a texture.
 *
 * <p>Skipping the rest is not an approximation. The emission layer draws through
 * {@code EYES_PROGRAM}, whose fragment shader is
 * {@code texture(Sampler0, texCoord0) * vertexColor * ColorModulator * fog}, with no discard and no
 * alpha override, and the console path passes vertex alpha 1. The layer blends
 * {@code blendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ONE_MINUS_SRC_ALPHA)}, so at a source
 * alpha of zero the colour is {@code 0 * src + 1 * dst} and the alpha is {@code 1 * 0 + 1 * dst}:
 * the destination is untouched on both. The layer never writes depth, uses no stencil, and is built
 * with no outline mode, so a skipped quad cannot affect anything else either. The texture is bound
 * with GL_NEAREST and no mipmaps, so a quad samples exactly the texels in its own rect and cannot
 * pick up a neighbouring lit one.
 *
 * <p>That proof is about the vanilla pipeline. Under Iris or OptiFine the shader pack supplies its
 * own program and blend state, so it does not carry over; if a pack renders transparent texels as
 * black then the unculled output was already wrong there.
 *
 * <p>Parts are skipped by clearing {@link ModelPart#visible}, because {@code ModelPart.render}
 * returns on that before touching its cuboids or its children. Only the highest parts whose whole
 * subtree is unlit are cleared, so the per-frame cost is a walk of a short array rather than of
 * every part.
 *
 * <p>The cull is computed for {@link #hideUnlit}'s root, while the pass also draws whatever
 * {@code renderExtras} adds. Today that is only Hudolin's toolbox, a sibling of the root and so
 * outside the frontier, which merely under-culls. A model whose extras drew a part inside the root's
 * own subtree would have that glow hidden.
 *
 * <p>Two shapes of emission texture the mask cannot describe, neither of which ships: an animated
 * one, where an {@code .mcmeta} makes the PNG a vertical strip of frames and a texture coordinate
 * would index across frames rather than within one; and one whose aspect ratio differs from the
 * model's declared texture size, which would scale the two axes differently.
 *
 * <p>Every failure path leaves the model alone. A mistake here deletes glow rather than degrading
 * it, so a missing texture, an unreadable one, a coordinate that is not a finite number, or a model
 * that appears entirely unlit all fall back to drawing everything.
 */
@Environment(EnvType.CLIENT)
public final class EmissiveGeometry {

    /** Nothing to skip. Shared so every give-up path needs no null handling downstream. */
    private static final ModelPart[] NOTHING = new ModelPart[0];
    private static final boolean[] NO_STATE = new boolean[0];

    private static final Map<Identifier, Mask> MASKS = new HashMap<>();

    // Weakly keyed: getCachedModel never clears its field, but a datapack sync can rebuild the
    // variant registry and hand out new schemas with new trees, and holding the old roots would leak
    // them. ModelPart does not override equals, so this is identity keyed either way.
    private static final Map<ModelPart, Map<Identifier, ModelPart[]>> FRONTIERS = new WeakHashMap<>();

    /**
     * Clears the unlit parts of {@code root} for a single draw. The caller must
     * {@link Scope#restore()} in a finally block, because a part left cleared would also be missing
     * from the base pass and from the console generator's hologram, which share the same model.
     */
    public static Scope hideUnlit(ModelPart root, Identifier emission) {
        if (root == null || emission == null)
            return new Scope(NOTHING, NO_STATE);

        try {
            ModelPart[] frontier = frontierFor(root, emission);

            if (frontier.length == 0)
                return new Scope(NOTHING, NO_STATE);

            boolean[] previous = new boolean[frontier.length];

            for (int i = 0; i < frontier.length; i++) {
                ModelPart part = frontier[i];
                previous[i] = part.visible;
                part.visible = false;
            }

            return new Scope(frontier, previous);
        } catch (Throwable t) {
            // Drawing everything is always correct, and this runs inside the caller's matrix push, so
            // letting anything escape would corrupt the matrix stack rather than lose a little speed.
            AITMod.LOGGER.warn("could not work out what to cull for {}, drawing all of it", emission, t);
            MASKS.put(emission, null);
            return new Scope(NOTHING, NO_STATE);
        }
    }

    /**
     * Builds the masks up front and drops the caches, on every client resource reload.
     *
     * <p>Reading a PNG and scanning its alpha is a few milliseconds, and doing it lazily meant paying
     * that inside the frame that first drew a variant. Reload runs off the frame and already has the
     * manager, so the work belongs here. Anything missed is still built on demand.
     *
     * <p>The reload also has to happen: a resource pack can replace an emission texture, and a
     * frontier built against the old one would hide exactly the parts the pack just lit.
     */
    public static void init() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return AITMod.id("emissive_geometry");
                    }

                    @Override
                    public void reload(ResourceManager manager) {
                        MASKS.clear();
                        FRONTIERS.clear();
                        prebuild(manager);
                    }
                });
    }

    /**
     * Reads every console emission the registry knows about.
     *
     * <p>Best effort. The registry is filled by a datapack sync, so on the first reload of a session
     * it is usually empty and the masks are built on demand instead.
     */
    private static void prebuild(ResourceManager manager) {
        for (ClientConsoleVariantSchema schema : ClientConsoleVariantRegistry.getInstance().toList()) {
            Identifier emission = schema.emission();

            if (emission == null || emission.equals(DatapackConsole.EMPTY) || MASKS.containsKey(emission))
                continue;

            MASKS.put(emission, readMask(manager, emission));
        }
    }

    /** Restores what {@link #hideUnlit} cleared, to what it was rather than to visible. */
    public static final class Scope {

        private final ModelPart[] hidden;
        private final boolean[] previous;

        private Scope(ModelPart[] hidden, boolean[] previous) {
            this.hidden = hidden;
            this.previous = previous;
        }

        public void restore() {
            // Backwards, so that if one part ever appeared twice the first value written wins and the
            // part is not left hidden for every later pass.
            for (int i = this.previous.length - 1; i >= 0; i--) {
                this.hidden[i].visible = this.previous[i];
            }
        }
    }

    private static ModelPart[] frontierFor(ModelPart root, Identifier emission) {
        Map<Identifier, ModelPart[]> byTexture = FRONTIERS.computeIfAbsent(root, key -> new HashMap<>());
        ModelPart[] cached = byTexture.get(emission);

        if (cached != null)
            return cached;

        ModelPart[] frontier = buildFrontier(root, emission);
        byTexture.put(emission, frontier);
        return frontier;
    }

    private static ModelPart[] buildFrontier(ModelPart root, Identifier emission) {
        Mask mask = maskFor(emission);

        if (mask == null)
            return NOTHING;

        Map<ModelPart, Boolean> memo = new IdentityHashMap<>();

        if (!subtreeLit(root, mask, memo)) {
            // Nothing in the model can light up against this texture. Either it is the wrong texture
            // for this model or the read is wrong, and hiding the root would delete the glow outright.
            AITMod.LOGGER.warn("{} lights no part of this model, not culling", emission);
            return NOTHING;
        }

        List<ModelPart> frontier = new ArrayList<>();
        collect(root, mask, memo, frontier);

        if (AITMod.LOGGER.isDebugEnabled()) {
            long hidden = frontier.stream().mapToLong(part -> part.traverse().count()).sum();
            AITMod.LOGGER.debug("emissive cull {}: {} subtree(s) hidden, {} of {} parts still drawn",
                    emission, frontier.size(), root.traverse().count() - hidden, root.traverse().count());
        }

        return frontier.isEmpty() ? NOTHING : frontier.toArray(ModelPart[]::new);
    }

    private static void collect(ModelPart part, Mask mask, Map<ModelPart, Boolean> memo, List<ModelPart> out) {
        if (!subtreeLit(part, mask, memo)) {
            out.add(part);
            return;
        }

        for (ModelPart child : children(part)) {
            collect(child, mask, memo, out);
        }
    }

    /** Whether this part or anything beneath it can sample a lit texel. */
    private static boolean subtreeLit(ModelPart part, Mask mask, Map<ModelPart, Boolean> memo) {
        Boolean known = memo.get(part);

        if (known != null)
            return known;

        boolean result = ownQuadsLit(part, mask);

        if (!result) {
            for (ModelPart child : children(part)) {
                if (subtreeLit(child, mask, memo)) {
                    result = true;
                    break;
                }
            }
        }

        memo.put(part, result);
        return result;
    }

    private static boolean ownQuadsLit(ModelPart part, Mask mask) {
        for (ModelPart.Cuboid cuboid : ((ModelPartAccessor) (Object) part).ait$cuboids()) {
            for (ModelPart.Quad quad : ((CuboidAccessor) (Object) cuboid).ait$sides()) {
                if (quad == null || quad.vertices.length == 0)
                    continue;

                float u1 = Float.MAX_VALUE;
                float v1 = Float.MAX_VALUE;
                float u2 = -Float.MAX_VALUE;
                float v2 = -Float.MAX_VALUE;

                // Normalised already: the Quad constructor divides by the model's declared texture
                // size, so these index an emission image of any resolution.
                for (ModelPart.Vertex vertex : quad.vertices) {
                    u1 = Math.min(u1, vertex.u);
                    v1 = Math.min(v1, vertex.v);
                    u2 = Math.max(u2, vertex.u);
                    v2 = Math.max(v2, vertex.v);
                }

                if (mask.anyLitIn(u1, v1, u2, v2))
                    return true;
            }
        }

        return false;
    }

    private static Iterable<ModelPart> children(ModelPart part) {
        return ((ModelPartAccessor) (Object) part).ait$children().values();
    }

    private static Mask maskFor(Identifier emission) {
        if (MASKS.containsKey(emission))
            return MASKS.get(emission);

        Mask mask = readMask(MinecraftClient.getInstance().getResourceManager(), emission);
        MASKS.put(emission, mask);
        return mask;
    }

    private static Mask readMask(ResourceManager manager, Identifier emission) {
        Optional<Resource> resource = manager.getResource(emission);

        if (resource.isEmpty()) {
            AITMod.LOGGER.warn("no emission texture at {}, not culling", emission);
            return null;
        }

        try (InputStream stream = resource.get().getInputStream(); NativeImage image = NativeImage.read(stream)) {
            int width = image.getWidth();
            int height = image.getHeight();

            if (width <= 0 || height <= 0)
                return null;

            // A bit per texel rather than a byte: a 1024 square emission is 128 KiB this way instead
            // of a megabyte, and several of these stay resident for the life of the client.
            long[] lit = new long[(width * height + 63) >>> 6];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // getColor packs ABGR, so alpha is the top byte.
                    if ((image.getColor(x, y) >>> 24) != 0) {
                        int index = y * width + x;
                        lit[index >>> 6] |= 1L << (index & 63);
                    }
                }
            }

            return new Mask(width, height, lit);
        } catch (Exception e) {
            AITMod.LOGGER.warn("could not read {}, not culling", emission, e);
            return null;
        }
    }

    /** Which texels of an emission texture are not fully transparent, one bit each. */
    private record Mask(int width, int height, long[] lit) {

        boolean anyLitIn(float u1, float v1, float u2, float v2) {
            // Not a finite rect, so nothing can be concluded. Keeping the part is the safe answer:
            // a model declaring a texture size of zero gives every coordinate as NaN, and treating
            // that as unlit would silently delete its glow.
            if (!Float.isFinite(u1) || !Float.isFinite(v1) || !Float.isFinite(u2) || !Float.isFinite(v2))
                return true;

            int x0 = (int) Math.floor(Math.min(u1, u2) * this.width);
            int y0 = (int) Math.floor(Math.min(v1, v2) * this.height);

            // At least one texel per axis. A cuboid with a zero size on one axis, which the larger
            // consoles have plenty of, gives its side quads a rect of zero extent, and an empty rect
            // would report unlit and delete glow that dilation still draws.
            int x1 = Math.max(x0, (int) Math.ceil(Math.max(u1, u2) * this.width) - 1);
            int y1 = Math.max(y0, (int) Math.ceil(Math.max(v1, v2) * this.height) - 1);

            for (int y = y0; y <= y1; y++) {
                int row = Math.floorMod(y, this.height) * this.width;

                for (int x = x0; x <= x1; x++) {
                    // Wrapped, not clamped: textures upload with GL_REPEAT and some models do address
                    // past their declared texture size, so clamping would test the wrong texels.
                    int index = row + Math.floorMod(x, this.width);

                    if ((this.lit[index >>> 6] & (1L << (index & 63))) != 0)
                        return true;
                }
            }

            return false;
        }
    }
}

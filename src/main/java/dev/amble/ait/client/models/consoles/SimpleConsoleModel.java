package dev.amble.ait.client.models.consoles;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;

import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

@SuppressWarnings("rawtypes")
public abstract class SimpleConsoleModel extends SinglePartEntityModel implements ConsoleModel {

    // Using a map actually is the worst way to do this - DO NOT REPLICATE. - Loqor
    protected static final Map<BlockEntity, Object2FloatMap<String>> ANIMATION_CACHE = new WeakHashMap<>();

    protected static final MinecraftClient client = MinecraftClient.getInstance();

    /**
     * Shared stand-in for a state with nothing to animate. Built once: the subclasses used to return
     * a new empty animation every frame, which allocated for nothing and gave any cache keyed on
     * animation identity an unbounded number of keys.
     */
    protected static final Animation NO_ANIMATION = Animation.Builder.create(0).build();

    protected float getAngle(BlockEntity console, String key, float target, float delta) {
        Object2FloatMap<String> state = ANIMATION_CACHE.computeIfAbsent(console, k -> new Object2FloatOpenHashMap<>());
        float current = state.getOrDefault(key, 0f);
        float next = MathHelper.lerp(delta, current, target);
        state.put(key, next);
        return next;
    }

    protected float getLerpedDegrees(BlockEntity console, String key, float targetDegrees, float delta) {
        Object2FloatMap<String> state = ANIMATION_CACHE.computeIfAbsent(console, k -> new Object2FloatOpenHashMap<>());
        float currentRadians = state.getOrDefault(key, 0f);
        float currentDegrees = currentRadians * (180f / (float) Math.PI);
        float nextDegrees = MathHelper.lerpAngleDegrees(delta, currentDegrees, targetDegrees);
        float nextRadians = nextDegrees * ((float) Math.PI / 180f);
        state.put(key, nextRadians);
        return nextRadians;
    }

    public SimpleConsoleModel() {
        this(RenderLayer::getEntityCutoutNoCull);
    }

    public SimpleConsoleModel(Function<Identifier, RenderLayer> function) {
        super(function);
    }

    @Override
    public void animateBlockEntity(ConsoleBlockEntity console, TravelHandlerBase.State state, boolean hasPower) {
        // Split so the traversal and the keyframe work can be told apart. The traversal was the
        // suspect and it was: ModelPart.traverse builds a Stream per node.
        Profiler profiler = client.getProfiler();

        ModelPart[] parts = this.parts();

        profiler.push("ait:console_reset");
        profiler.visit("ait_console_reset_parts", parts.length);

        for (ModelPart part : parts) {
            part.resetTransform();
        }

        profiler.swap("ait:console_keyframes");

        if (hasPower && AITModClient.CONFIG.animateConsole) {
            profiler.visit("ait_console_animated");
            this.updateAnimation(console.ANIM_STATE, this.getAnimationForState(state), client.getTickDelta() + console.getAge());
        } else {
            profiler.visit("ait_console_not_animated");
        }

        profiler.pop();
    }

    /**
     * Name to part, including the names that resolve to nothing.
     *
     * <p>{@link SinglePartEntityModel#getChild} resolves a bone by walking the whole model with
     * {@code ModelPart.traverse()}, which builds a Stream per node, and the keyframe animation
     * helper asks once per animated bone per frame. A console is a few hundred parts and a couple of
     * dozen animated bones, so a single console spends milliseconds a frame re-answering the same
     * question.
     *
     * <p>Misses are cached too. A bone named in an animation but absent from the model can never
     * short-circuit the search, so it is the most expensive lookup there is, and two consoles in
     * this mod have one: {@code CopperAnimations} animates {@code top} and {@code
     * RenaissanceAnimation} animates {@code undefined}.
     *
     * <p>A plain {@link HashMap}: this is only ever read from the render thread.
     */
    private Map<String, Optional<ModelPart>> boneCache;

    /** The root the cache was built against, so a model that swaps its root rebuilds. */
    private ModelPart cachedRoot;

    @Override
    public Optional<ModelPart> getChild(String name) {
        ModelPart root = this.getPart();

        if (root == null)
            return super.getChild(name);

        Profiler profiler = client.getProfiler();
        profiler.visit("ait_bone_lookup");

        if (this.boneCache == null || this.cachedRoot != root) {
            this.boneCache = new HashMap<>();
            this.cachedRoot = root;
            profiler.visit("ait_bone_map_built");
        }

        Optional<ModelPart> cached = this.boneCache.get(name);

        if (cached != null) {
            profiler.visit("ait_bone_cache_hit");
            return cached;
        }

        // First ask for this name on this root. Resolved the way vanilla does, so a duplicated part
        // name still returns the first match in traversal order, then remembered.
        profiler.visit("ait_bone_traversal");

        Optional<ModelPart> resolved = "root".equals(name)
                ? Optional.of(root)
                : root.traverse().filter(part -> part.hasChild(name)).findFirst().map(part -> part.getChild(name));

        this.boneCache.put(name, resolved);
        return resolved;
    }

    private ModelPart[] flattened;
    private ModelPart flattenedRoot;

    /**
     * The model's parts as a flat array, walked once.
     *
     * <p>{@code ModelPart.traverse()} builds a {@code Stream} per node, so resetting the transforms of
     * an 818 part console through it allocated 818 streams a frame for a tree that never changes.
     * Rebuilt if the root is swapped, the same guard the bone cache uses.
     */
    private ModelPart[] parts() {
        ModelPart root = this.getPart();

        if (this.flattened == null || this.flattenedRoot != root) {
            this.flattened = root.traverse().toArray(ModelPart[]::new);
            this.flattenedRoot = root;
        }

        return this.flattened;
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, ConsoleBlockEntity linkableBlockEntity, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, float tickDelta) {;
        renderWithAnimations(linkableBlockEntity, tardis, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
    }

    // Overloaded method for compatibility with older code
    public void renderWithAnimations(ConsoleBlockEntity console, ClientTardis tardis, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        root.render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
    }

    /**
     * The transform from block space into this model's own space, applied once per geometry pass.
     *
     * <p>Extracted so the state-and-geometry pass and the geometry-only pass cannot drift apart. The
     * default is a no-op to match {@link #renderWithAnimations}, which does not push or translate
     * either: a subclass that transforms in one path and not the other would draw its glow somewhere
     * other than its geometry.
     */
    protected void applyRootTransform(MatrixStack matrices) {
    }

    /**
     * Geometry a model draws alongside its root part, on every layer.
     *
     * <p>Nothing state-like belongs here. It runs per layer, and the parts it touches are outside the
     * {@code resetTransform} and keyframe pass that {@link #animateBlockEntity} performs on
     * {@link #getPart()}.
     */
    protected void renderExtras(MatrixStack matrices, VertexConsumer vertices, int light, int overlay,
            float red, float green, float blue, float alpha) {
    }

    /**
     * The rate at which a control lerps toward its target, per frame.
     *
     * <p>Shared because the rate is only meaningful against how often the state runs, and that is a
     * property of the renderer rather than of any one model.
     *
     * <p>Deliberately left at the value this branch already used, because there is no single constant
     * that restores the old feel. The number of state runs a frame is not uniform across controls:
     *
     * <ul>
     * <li>most controls on a powered console: four on the last release, two on this branch, now one</li>
     * <li>copper's two shield keys: two, then one, now one. Unchanged. Their lerp target is the part's
     * own live pivot, so a second run in the same frame read back what the first had written and
     * {@code lerp(d, c, c)} is a no-op</li>
     * <li>every control on an unpowered console: two, then one, now one. Unchanged, because
     * {@code ConsoleRenderer.renderEmissions} returns before the emission pass when unpowered</li>
     * </ul>
     *
     * <p>So scaling this up to match the first row would leave the other two moving about twice as fast
     * as they ever have, trading a disclosed slowdown for an undisclosed speed-up. Matching the first
     * row alone would want roughly 0.35f, since repeated lerp compounds as 1-(1-d)^n rather than n*d.
     */
    protected float controlDelta() {
        return !AITModClient.CONFIG.animateControls ? 1.0f : 0.1f * client.getTickDelta();
    }

    @Override
    public void renderGeometryOnly(ClientTardis tardis, ConsoleBlockEntity console, ModelPart root,
            MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green,
            float blue, float alpha, float tickDelta) {
        matrices.push();
        this.applyRootTransform(matrices);
        root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.renderExtras(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
            float headPitch) {
    }

    public abstract Animation getAnimationForState(TravelHandlerBase.State state);

    public void renderMonitorText(Tardis tardis, ConsoleBlockEntity entity, MatrixStack matrices,
                                  VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // no op
    }
}

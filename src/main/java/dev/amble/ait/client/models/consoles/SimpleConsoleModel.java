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
import dev.amble.ait.client.util.ClientProfiling;
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
        // Split so the traversal and the keyframe work can be told apart. The traversal is the
        // suspect: ModelPart.traverse builds a Stream per node, and a console is a few hundred nodes.
        Profiler profiler = client.getProfiler();

        profiler.push("ait:console_reset");
        ClientProfiling.count("ait_console_reset_parts", this.countParts());
        this.getPart().traverse().forEach(ModelPart::resetTransform);

        profiler.swap("ait:console_keyframes");

        if (hasPower && AITModClient.CONFIG.animateConsole) {
            ClientProfiling.count("ait_console_animated");
            this.updateAnimation(console.ANIM_STATE, this.getAnimationForState(state), client.getTickDelta() + console.getAge());
        } else {
            ClientProfiling.count("ait_console_not_animated");
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

        ClientProfiling.count("ait_bone_lookup");

        if (this.boneCache == null || this.cachedRoot != root) {
            this.boneCache = new HashMap<>();
            this.cachedRoot = root;
            ClientProfiling.count("ait_bone_map_built");
        }

        Optional<ModelPart> cached = this.boneCache.get(name);

        if (cached != null) {
            ClientProfiling.count("ait_bone_cache_hit");
            return cached;
        }

        // First ask for this name on this root. Resolved the way vanilla does, so a duplicated part
        // name still returns the first match in traversal order, then remembered.
        ClientProfiling.count("ait_bone_traversal");

        Optional<ModelPart> resolved = "root".equals(name)
                ? Optional.of(root)
                : root.traverse().filter(part -> part.hasChild(name)).findFirst().map(part -> part.getChild(name));

        this.boneCache.put(name, resolved);
        return resolved;
    }

    private int partCount = -1;

    /** Counted once. Only used to report the traversal size into the profiler. */
    private int countParts() {
        if (this.partCount < 0)
            this.partCount = (int) this.getPart().traverse().count();

        return this.partCount;
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

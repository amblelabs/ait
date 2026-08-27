package dev.amble.ait.mixin.client.rendering;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;

import dev.amble.ait.client.util.ClientProfiling;

/**
 * Caches the name to part lookup that keyframe animation drives.
 *
 * <p>{@code getChild} resolves a bone by walking the whole model with
 * {@code ModelPart.traverse()}, which builds a Stream per node, and the animation helper calls it
 * once per animated bone per frame. A console is a few hundred parts and a couple of dozen animated
 * bones, so a single console spends milliseconds a frame re-answering the same question.
 *
 * <p>Misses are cached too. A bone named in an animation but absent from the model can never
 * short-circuit the search, so it is the most expensive lookup there is, and two consoles in this
 * mod have one.
 *
 * <p>The cache is per model instance and validated against the current root, so a model that swaps
 * its root rebuilds instead of answering from a stale tree.
 */
@Mixin(SinglePartEntityModel.class)
public abstract class SinglePartEntityModelMixin {

    @Unique
    private Map<String, Optional<ModelPart>> ait$boneCache;

    @Unique
    private ModelPart ait$cachedRoot;

    @Inject(method = "getChild", at = @At("HEAD"), cancellable = true)
    private void ait$cachedGetChild(String name, CallbackInfoReturnable<Optional<ModelPart>> cir) {
        ModelPart root = ((SinglePartEntityModel<?>) (Object) this).getPart();

        if (root == null)
            return;

        ClientProfiling.count("ait_bone_lookup");

        if (this.ait$boneCache == null || this.ait$cachedRoot != root) {
            this.ait$boneCache = new ConcurrentHashMap<>();
            this.ait$cachedRoot = root;
            ClientProfiling.count("ait_bone_map_built");
        }

        Optional<ModelPart> cached = this.ait$boneCache.get(name);

        if (cached != null) {
            ClientProfiling.count("ait_bone_cache_hit");
            cir.setReturnValue(cached);
            return;
        }

        // First ask for this name on this root. Resolve it the way vanilla does, so a duplicated
        // part name still returns the first match in traversal order, then remember the answer.
        ClientProfiling.count("ait_bone_traversal");

        Optional<ModelPart> resolved = "root".equals(name)
                ? Optional.of(root)
                : root.traverse().filter(part -> part.hasChild(name)).findFirst().map(part -> part.getChild(name));

        this.ait$boneCache.put(name, resolved);
        cir.setReturnValue(resolved);
    }
}

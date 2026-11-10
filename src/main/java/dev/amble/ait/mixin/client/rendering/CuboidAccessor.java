package dev.amble.ait.mixin.client.rendering;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.model.ModelPart;

/**
 * Reads the quads a cuboid is built from, for their texture coordinates.
 *
 * <p>Pure accessor, no injected behaviour. {@code Quad.vertices} is already public; only the array of
 * quads itself is not.
 */
@Mixin(ModelPart.Cuboid.class)
public interface CuboidAccessor {

    @Accessor("sides")
    ModelPart.Quad[] ait$sides();
}

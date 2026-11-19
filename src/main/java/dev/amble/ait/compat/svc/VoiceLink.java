package dev.amble.ait.compat.svc;

import java.util.function.UnaryOperator;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public record VoiceLink(RegistryKey<World> fromDim, Vec3d from, RegistryKey<World> toDim, UnaryOperator<Vec3d> transform,
                        boolean muffled) {
}

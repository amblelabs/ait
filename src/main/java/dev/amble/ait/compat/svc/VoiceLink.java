package dev.amble.ait.compat.svc;

import java.util.function.UnaryOperator;

import org.jetbrains.annotations.Nullable;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public record VoiceLink(RegistryKey<World> fromDim, Vec3d from, RegistryKey<World> toDim, UnaryOperator<Vec3d> transform,
                        @Nullable Vec3d exit, Muffle muffle) {

    public enum Muffle {
        NONE, INSIDE, OUTSIDE
    }

    public static VoiceLink portal(RegistryKey<World> fromDim, Vec3d from, RegistryKey<World> toDim, UnaryOperator<Vec3d> transform) {
        return new VoiceLink(fromDim, from, toDim, transform, null, Muffle.NONE);
    }

    public static VoiceLink door(RegistryKey<World> fromDim, Vec3d from, RegistryKey<World> toDim, Vec3d exit, Muffle muffle) {
        return new VoiceLink(fromDim, from, toDim, point -> exit, exit, muffle);
    }
}

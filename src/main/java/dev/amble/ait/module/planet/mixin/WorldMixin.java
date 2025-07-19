package dev.amble.ait.module.planet.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.core.AITDimensions;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetWorld;

@Mixin(World.class)
public abstract class WorldMixin implements PlanetWorld {

    @Shadow public abstract RegistryKey<World> getRegistryKey();

    @Unique private Planet planet;

    @Unique private boolean isAPlanet;

    @Override
    public boolean ait_planet$isAPlanet() {
        return isAPlanet;
    }

    @Override
    public @Nullable Planet ait_planet$getPlanet() {
        return planet;
    }

    @Override
    public void ait_planet$setPlanet(Planet planet) {
        this.planet = planet;
    }

    @Override
    public void ait_planet$setIsAPlanet(boolean isAPlanet) {
        this.isAPlanet = isAPlanet;
    }

    @Inject(method = "isInBuildLimit", at = @At("HEAD"), cancellable = true)
    private void ait$isInBuildLimit(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        World world = (World) (Object) this;
        if (world.getRegistryKey().equals(AITDimensions.SPACE)) {
            cir.setReturnValue(true);
        }
    }
}

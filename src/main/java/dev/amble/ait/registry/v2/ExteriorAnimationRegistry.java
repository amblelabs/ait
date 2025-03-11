package dev.amble.ait.registry.v2;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.animation.ClassicAnimation;
import dev.amble.ait.core.tardis.animation.ExteriorAnimation;
import dev.amble.ait.core.tardis.animation.PulsatingAnimation;
import dev.amble.lib.registry.SimpleAmbleRegistry;
import dev.amble.lib.registry.SimpleRegistryElementCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ExteriorAnimationRegistry extends SimpleAmbleRegistry<ExteriorAnimationRegistry.AnimationCreator> {

    public static final Identifier PULSATING = AITMod.id("pulsating");

    private final SimpleRegistryElementCodec<AnimationCreator> entry = SimpleRegistryElementCodec.of(this);

    public ExteriorAnimationRegistry() {
        super(AITMod.id("exterior/animation"));

        Registry.register(this.get(), AITMod.id("classic"), ClassicAnimation::new);
        Registry.register(this.get(), PULSATING, PulsatingAnimation::new);
    }

    public SimpleRegistryElementCodec<AnimationCreator> entry() {
        return entry;
    }

    @FunctionalInterface
    public interface AnimationCreator {
        ExteriorAnimation createAnimation(ExteriorBlockEntity exterior);
    }
}

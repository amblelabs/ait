package dev.amble.ait.core.tardis.animation.v2.datapack;

import dev.amble.lib.register.datapack.SimpleDatapackRegistry;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.animation.v2.TardisAnimation;

public class TardisAnimationRegistry extends SimpleDatapackRegistry<TardisAnimation> {
    public static final Identifier DEFAULT_DEMAT = AITMod.id("pulsating_demat");
    public static final Identifier DEFAULT_MAT = AITMod.id("pulsating_mat");
    private static TardisAnimationRegistry INSTANCE;

    private TardisAnimationRegistry() {
        super(DatapackAnimation::fromInputStream, DatapackAnimation.CODEC, "fx/animation/type", true, AITMod.MOD_ID);
    }

    public static TardisAnimationRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TardisAnimationRegistry();
        }

        return INSTANCE;
    }

    @Override
    protected void defaults() {

    }

    public TardisAnimation instantiate(Identifier id) {
        return this.getOrFallback(id).instantiate();
    }

    @Override
    public TardisAnimation fallback() {
        TardisAnimation fallback = this.get(AITMod.id("classic_demat"));

        if (fallback == null) {
            throw new IllegalStateException("Classic Demat Animation is null! No fallback.");
        }

        return fallback;
    }
}

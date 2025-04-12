package dev.amble.ait.core.roundels;

import dev.amble.lib.register.datapack.SimpleDatapackRegistry;

import dev.amble.ait.AITMod;


public class RoundelPatterns extends SimpleDatapackRegistry<RoundelPattern> {
    private static final RoundelPatterns instance = new RoundelPatterns();

    public RoundelPatterns() {
        super(RoundelPattern::fromInputStream, RoundelPattern.CODEC, "roundel/pattern", "roundel/pattern", true, AITMod.MOD_ID);
    }

    public static RoundelPatterns getInstance() {
        return instance;
    }

    public static RoundelPattern EMPTY;

    @Override
    protected void defaults() {
        EMPTY = register(new RoundelPattern(AITMod.id("empty")));
    }

    @Override
    public RoundelPattern fallback() {
        return null;
    }
}

package dev.amble.ait.core.roundels;

import dev.amble.lib.register.datapack.SimpleDatapackRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.resource.ResourceType;

import dev.amble.ait.AITMod;


public class RoundelPatterns extends SimpleDatapackRegistry<RoundelPattern> {
    private static final RoundelPatterns instance = new RoundelPatterns();

    public RoundelPatterns() {
        super(RoundelPattern::fromInputStream, RoundelPattern.CODEC, "roundel/pattern", true, AITMod.MOD_ID);
    }

    public static RoundelPattern BASE;

    @Override
    protected void defaults() {
        BASE = register(new RoundelPattern(AITMod.id("roundel/base"), AITMod.id("textures/block/roundel/base.png"), true));
    }

    @Override
    public void onCommonInit() {
        super.onCommonInit();
        this.defaults();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
    }

    @Override
    public RoundelPattern fallback() {
        return BASE;
    }

    public static RoundelPatterns getInstance() {
        return instance;
    }
}

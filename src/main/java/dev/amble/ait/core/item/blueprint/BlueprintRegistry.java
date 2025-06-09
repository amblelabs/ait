package dev.amble.ait.core.item.blueprint;

import dev.amble.ait.AITMod;
import dev.amble.lib.register.datapack.SimpleDatapackRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.resource.ResourceType;


public class BlueprintRegistry extends SimpleDatapackRegistry<BlueprintSchema> {
    public static LootFunctionType BLUEPRINT_TYPE;

    private static final BlueprintRegistry instance = new BlueprintRegistry();

    public BlueprintRegistry() {
        super(BlueprintSchema::fromInputStream, BlueprintSchema.CODEC, "blueprint", true, AITMod.MOD_ID);
    }

    @Override
    public void onCommonInit() {
        super.onCommonInit();
        this.defaults();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
    }

    @Override
    protected void defaults() {
    }

    @Override
    public BlueprintSchema fallback() {
        return null;
    }

    public static BlueprintRegistry getInstance() {
        return instance;
    }
}

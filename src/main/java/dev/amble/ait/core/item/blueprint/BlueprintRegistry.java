package dev.amble.ait.core.item.blueprint;

import dev.amble.lib.register.datapack.SimpleDatapackRegistry;

import net.minecraft.loot.function.LootFunctionType;

import dev.amble.ait.AITMod;



public class BlueprintRegistry extends SimpleDatapackRegistry<BlueprintSchema> {
    public static LootFunctionType BLUEPRINT_TYPE;

    private static final BlueprintRegistry instance = new BlueprintRegistry();

    public BlueprintRegistry() {
        super(BlueprintSchema::fromInputStream, BlueprintSchema.CODEC, "blueprint", true, AITMod.MOD_ID);
    }

    @Override
    protected void defaults() {
    }

    @Override
    public BlueprintSchema fallback() {
        throw new IllegalStateException("No fallback blueprint found");
    }

    public static BlueprintRegistry getInstance() {
        return instance;
    }
}

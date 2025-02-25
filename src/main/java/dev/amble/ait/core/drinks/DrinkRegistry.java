package dev.amble.ait.core.drinks;

import java.util.List;
import java.util.Optional;

import dev.amble.lib.register.datapack.SimpleDatapackRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.resource.ResourceType;

import dev.amble.ait.AITMod;

public class DrinkRegistry extends SimpleDatapackRegistry<Drink> {
    private static final DrinkRegistry instance = new DrinkRegistry();
    public DrinkRegistry() {
        super(Drink::fromInputStream, Drink.CODEC, "drink/effect", "drink/effect", true, AITMod.MOD_ID);
    }

    public static Drink EMPTY_MUG;

    @Override
    protected void defaults() {
        EMPTY_MUG = register(new Drink(AITMod.id("mug_empty"), Optional.empty(), Optional.empty(), List.of()));
    }

    @Override
    public void onCommonInit() {
        super.onCommonInit();
        this.defaults();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
    }

    @Override
    public Drink fallback() {
        return EMPTY_MUG;
    }

    public static DrinkRegistry getInstance() {
        return instance;
    }
}

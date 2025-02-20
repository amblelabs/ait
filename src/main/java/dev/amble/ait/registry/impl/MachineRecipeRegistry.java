package dev.amble.ait.registry.impl;

import java.util.Collection;
import java.util.Optional;

import dev.amble.lib.register.datapack.SimpleDatapackRegistry;

import net.minecraft.item.ItemStack;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.util.StackUtil;
import dev.amble.ait.data.datapack.DatapackMachineRecipe;
import dev.amble.ait.data.schema.MachineRecipeSchema;

public class MachineRecipeRegistry extends SimpleDatapackRegistry<MachineRecipeSchema> {

    private static MachineRecipeRegistry INSTANCE;

    protected MachineRecipeRegistry() {
        super(DatapackMachineRecipe::fromInputStream, DatapackMachineRecipe.CODEC, "machine_recipe", false, AITMod.MOD_ID);
    }

    @Override
    public MachineRecipeSchema fallback() {
        return null;
    }

    @Override
    protected void defaults() {
    }

    public Optional<MachineRecipeSchema> findMatching(Collection<ItemStack> set) {
        for (MachineRecipeSchema schema : REGISTRY.values()) {
            if (StackUtil.equals(set, schema.input()))
                return Optional.of(schema.copy());
        }

        return Optional.empty();
    }

    public Optional<MachineRecipeSchema> findMatching(ItemStack result) {
        for (MachineRecipeSchema schema : REGISTRY.values()) {
            if (ItemStack.areItemsEqual(schema.output(), result))
                return Optional.of(schema.copy());
        }

        return Optional.empty();
    }

    public static MachineRecipeRegistry getInstance() {
        if (INSTANCE == null) {
            AITMod.LOGGER.debug("MachineRecipeRegistry was not initialized, creating a new instance");
            INSTANCE = new MachineRecipeRegistry();
        }

        return INSTANCE;
    }
}

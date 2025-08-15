package dev.amble.ait.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import org.jetbrains.annotations.NotNull;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.compat.jei.client.FabricatorRecipeCategory;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.item.blueprint.BlueprintRegistry;

@JeiPlugin
public class AITJeiPlugin implements IModPlugin {

    @Override
    public @NotNull Identifier getPluginUid() {
        return new Identifier(AITMod.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(AITItems.BLUEPRINT);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(AITBlocks.FABRICATOR.asItem(), FabricatorRecipeCategory.TYPE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FabricatorRecipeCategory());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(FabricatorRecipeCategory.TYPE, BlueprintRegistry.getInstance().toList());
    }

}

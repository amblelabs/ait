package dev.amble.ait.compat.jei.client;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.item.blueprint.BlueprintItem;
import dev.amble.ait.core.item.blueprint.BlueprintSchema;

public class FabricatorRecipeCategory implements IRecipeCategory<BlueprintSchema> {

    public static final RecipeType<BlueprintSchema> TYPE = RecipeType.create(AITMod.MOD_ID, "fabricator", BlueprintSchema.class);

    public FabricatorRecipeCategory() {
    }

    @Override
    public @NotNull RecipeType<BlueprintSchema> getRecipeType() {
        return TYPE;
    }

    @Override
    public int getWidth() {
        return 196;
    }

    @Override
    public int getHeight() {
        return 76;
    }

    @Override
    public @NotNull Text getTitle() {
        return AITBlocks.FABRICATOR.getName();
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BlueprintSchema blueprint, IFocusGroup focusGroup) {
        builder.addInputSlot()
                .addItemStack(BlueprintItem.createStack(blueprint))
                .setPosition(5, 5)
                .setOutputSlotBackground();

        for (int i = 0; i < blueprint.inputs().size(); i++) {
            BlueprintSchema.Input input = blueprint.inputs().get(i);
            builder.addInputSlot()
                    .addItemStack(input.maxCountStack())
                    .setCustomRenderer(
                            VanillaTypes.ITEM_STACK,
                            new BlueprintInputItemStackRenderer(input.minimum(), input.maximum())
                    )
                    .setPosition( 57 + 26 * (i % 3),
                            5 + 26 * MathHelper.floor((float) i / 3))
                    .setOutputSlotBackground();
        }
        builder.addOutputSlot().addItemStack(blueprint.output())
                .setPosition(26 * 3 + 96, 5)
                .setOutputSlotBackground();

    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, BlueprintSchema recipe, IFocusGroup focuses) {
        builder.addRecipePlusSign().setPosition(32, 6);
        builder.addRecipeArrow().setPosition(26 * 4 + 35, 4);
    }

}

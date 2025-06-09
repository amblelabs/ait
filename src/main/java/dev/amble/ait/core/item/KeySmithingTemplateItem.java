package dev.amble.ait.core.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KeySmithingTemplateItem extends Item {

    private static final Formatting TITLE_FORMATTING = Formatting.GRAY;
    private static final Formatting DESCRIPTION_FORMATTING = Formatting.BLUE;
    private final String KEY;
    private final String INGREDIENT;

    public KeySmithingTemplateItem(Settings settings, String key, String ingredient) {
        super(settings);
        this.KEY = key;
        this.INGREDIENT = ingredient;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.translatable("message.ait.keysmithing.upgrade").formatted(TITLE_FORMATTING));
        tooltip.add(ScreenTexts.EMPTY);
        tooltip.add(Text.translatable("message.ait.keysmithing.key").formatted(TITLE_FORMATTING));
        tooltip.add(ScreenTexts.space().append(Text.literal(this.KEY)).formatted(DESCRIPTION_FORMATTING));
        tooltip.add(Text.translatable("message.ait.keysmithing.ingredient").formatted(TITLE_FORMATTING));
        tooltip.add(ScreenTexts.space().append(Text.literal(this.INGREDIENT)).formatted(DESCRIPTION_FORMATTING));
    }
}

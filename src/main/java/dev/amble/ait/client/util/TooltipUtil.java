package dev.amble.ait.client.util;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.function.Consumer;

public class TooltipUtil {

    public static void addShiftHiddenTooltip(ItemStack stack, List<Text> tooltip, Consumer<List<Text>> extraTooltips) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.ait.items.holdformoreinfo")
                    .formatted(Formatting.GRAY, Formatting.ITALIC));
            return;
        }

        extraTooltips.accept(tooltip);
    }

}

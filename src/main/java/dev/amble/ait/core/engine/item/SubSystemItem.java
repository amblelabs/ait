package dev.amble.ait.core.engine.item;

import static dev.amble.ait.client.util.TooltipUtil.addShiftHiddenTooltip;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import dev.amble.ait.core.engine.SubSystem;

public class SubSystemItem extends Item {
    protected final SubSystem.IdLike id;

    public SubSystemItem(Settings settings, SubSystem.IdLike id) {
        super(settings);

        this.id = id;
    }

    public SubSystem.IdLike id() {
        return this.id;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        addShiftHiddenTooltip(stack, tooltip, tooltips -> {
            tooltip.add(Text.literal(this.id().name().replace("_", " ")).formatted(Formatting.YELLOW));
            tooltip.add(Text.translatable("tooltip.ait.subsystem_item").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        });
    }
}

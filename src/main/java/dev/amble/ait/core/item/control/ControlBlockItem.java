package dev.amble.ait.core.item.control;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public abstract class ControlBlockItem extends BlockItem {
    public static final String CONTROL_ID_KEY = "controlId";
    public static final String CONSOLE_TYPE_ID_KEY = "consoleTypeId";

    protected ControlBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        findControlId(stack).ifPresent(s -> tooltip.add(Text.translatable(s.toTranslationKey("control")).formatted(Formatting.AQUA)));

        super.appendTooltip(stack, world, tooltip, context);
    }

    public static Optional<Identifier> findControlId(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();

        if (!nbt.contains(CONTROL_ID_KEY))
            return Optional.empty();

        return Optional.of(new Identifier(stack.getOrCreateNbt().getString(CONTROL_ID_KEY)));
    }

    public static Optional<Identifier> findConsoleTypeId(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();

        if (!nbt.contains(CONSOLE_TYPE_ID_KEY))
            return Optional.empty();

        return Optional.of(new Identifier(stack.getOrCreateNbt().getString(CONSOLE_TYPE_ID_KEY)));
    }
}

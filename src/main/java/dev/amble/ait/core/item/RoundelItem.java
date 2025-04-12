package dev.amble.ait.core.item;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import dev.amble.ait.core.blocks.AbstractRoundelBlock;
import dev.amble.ait.core.roundels.RoundelPattern;
import dev.amble.ait.core.roundels.RoundelPatterns;

public class RoundelItem
        extends BlockItem {

    public RoundelItem(Block block, Item.Settings settings) {
        super(block, settings);
        Validate.isInstanceOf(AbstractRoundelBlock.class, block);
    }

    public static void appendRoundelTooltip(ItemStack stack, List<Text> tooltip) {
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if (nbtCompound == null || !nbtCompound.contains("Patterns")) {
            return;
        }
        NbtList nbtList = nbtCompound.getList("Patterns", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < nbtList.size() && i < 6; ++i) {
            NbtCompound nbtCompound2 = nbtList.getCompound(i);
            DyeColor dyeColor = DyeColor.byId(nbtCompound2.getInt("Color"));
            RoundelPattern roundel = RoundelPatterns.getInstance().get(Identifier.tryParse(nbtCompound2.getString("Pattern")));
            if (roundel == null) continue;
            tooltip.add(Text.literal(roundel.id().getPath() + " | Color: " + dyeColor.getName()).formatted(Formatting.GRAY, Formatting.ITALIC));
        }
    }

    public DyeColor getColor() {
        return ((AbstractRoundelBlock)this.getBlock()).getColor();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        RoundelItem.appendRoundelTooltip(stack, tooltip);
    }
}

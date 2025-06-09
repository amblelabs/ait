package dev.amble.ait.core.item;

import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.tardis.handler.StatsHandler;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PersonalityMatrixItem extends Item {
    public PersonalityMatrixItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        return super.getDefaultStack();
    }

    public static ItemStack randomize() {
        ItemStack stack = new ItemStack(AITItems.PERSONALITY_MATRIX);
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt("r", (int) (Math.random() * 256));
        nbt.putInt("g", (int) (Math.random() * 256));
        nbt.putInt("b", (int) (Math.random() * 256));
        nbt.putString("name", StatsHandler.getRandomName());
        return stack;
    }

    public int[] getColor(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        if (!nbt.contains("r") && !nbt.contains("g") && !nbt.contains("b")) {
            return new int[]{255, 255, 255};
        }
        return new int[]{nbt.getInt("r"), nbt.getInt("g"), nbt.getInt("b")};
    }

    public static int colorToInt(int r, int g, int b) {
        int result = 0;
        result += r;
        result = result << 8;
        result += g;
        result = result << 8;
        result += b;

        return result;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        tooltip.add(Text.literal("Name: " + stack.getOrCreateNbt().getString("name")).formatted(Formatting.BLUE));
        tooltip.add(Text.literal("#" + colorToInt(getColor(stack)[0], getColor(stack)[1], getColor(stack)[2]))
                .formatted(Formatting.GRAY));
    }
}

package dev.amble.ait.core.item;




import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;


public class WaypointItem extends AbstractCoordinateModifierItem implements DyeableItem {

    public static final int DEFAULT_COLOR = 16777215;

    public WaypointItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getColor(ItemStack stack) {
        NbtCompound nbt = stack.getSubNbt(DISPLAY_KEY);

        if (nbt != null && nbt.contains(COLOR_KEY, NbtElement.NUMBER_TYPE))
            return nbt.getInt(COLOR_KEY);

        return DEFAULT_COLOR; // white
    }
}

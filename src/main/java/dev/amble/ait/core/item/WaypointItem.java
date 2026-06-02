package dev.amble.ait.core.item;

import static dev.amble.ait.client.util.TooltipUtil.addShiftHiddenTooltip;

import java.util.List;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedGlobalPos;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.tardis.control.impl.DirectionControl;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Waypoint;

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

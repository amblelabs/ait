package dev.amble.ait.module.planet.core.item;

import dev.amble.ait.core.item.RenderableArmorItem;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class SpacesuitItem extends RenderableArmorItem {
    public static final String OXYGEN_KEY = "oxygen";
    public static final double MAX_OXYGEN = 5.2D;

    public SpacesuitItem(ArmorMaterial material, Type type, Settings settings, boolean hasCustomRendering) {
        super(material, type, settings, hasCustomRendering);
    }

    @Override
    public ItemStack getDefaultStack() {
        if (this.type != Type.CHESTPLATE) {
            return super.getDefaultStack();
        }
        ItemStack stack = new ItemStack(this);
        NbtCompound compound = stack.getOrCreateNbt();
        compound.putDouble(OXYGEN_KEY, MAX_OXYGEN);
        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (this.type != Type.CHESTPLATE) return;
        NbtCompound compound = stack.getOrCreateNbt();

        if (world == null || world.getServer() == null) return;

        if (world.getServer().getTicks() % 20 != 0) {
            return;
        }

        Planet planet = PlanetRegistry.getInstance().get(world);

        if ((TardisServerWorld.isTardisDimension(world) || (planet != null && planet.hasOxygen()))) {
            compound.putDouble(OXYGEN_KEY, Math.min(MAX_OXYGEN, compound.getDouble(OXYGEN_KEY) + 0.2D));
        } else if (compound.getDouble(OXYGEN_KEY) > 0.0D) {
            compound.putDouble(OXYGEN_KEY, Math.max(0.0D, compound.getDouble(OXYGEN_KEY) - 0.0035D));
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (this.type != Type.CHESTPLATE) return;

        double oxygenLevel = stack.getOrCreateNbt().getDouble(OXYGEN_KEY);
        String oxygenFormatted = String.format("%.1f", oxygenLevel) + "L / " + MAX_OXYGEN + "L";

        tooltip.add(Text.translatable("message.ait.oxygen", oxygenFormatted).formatted(Formatting.BOLD, Formatting.BLUE));
    }
}
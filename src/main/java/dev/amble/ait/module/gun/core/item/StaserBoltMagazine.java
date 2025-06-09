package dev.amble.ait.module.gun.core.item;

import dev.amble.ait.api.ArtronHolderItem;
import dev.amble.ait.module.gun.core.entity.GunEntityTypes;
import dev.amble.ait.module.gun.core.entity.StaserBoltEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StaserBoltMagazine extends Item implements ArtronHolderItem {
    public StaserBoltMagazine(Settings settings) {
        super(settings);
    }

    public static final double MAX_FUEL = 2000;

    public PersistentProjectileEntity createStaserbolt(World world, ItemStack stack, LivingEntity shooter) {
        StaserBoltEntity staserBoltEntity = new StaserBoltEntity(GunEntityTypes.STASER_BOLT_ENTITY_TYPE, world);
        return staserBoltEntity.createFromConstructor(world, shooter);
    }
    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        NbtCompound nbt = stack.getOrCreateNbt();

        nbt.putDouble(FUEL_KEY, MAX_FUEL);

        return stack;
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(stack, world, player);
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putDouble(FUEL_KEY, 0);
    }

    @Override
    public double getMaxFuel(ItemStack stack) {
        return MAX_FUEL;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        int currentFuel = (int) Math.round(this.getCurrentFuel(stack));
        Formatting fuelColor = currentFuel > (MAX_FUEL / 4) ? Formatting.GREEN : Formatting.RED;

        tooltip.add(
                Text.translatable("message.ait.artron_units", currentFuel)
                        .formatted(fuelColor)
                        .append(Text.literal(" / ").formatted(Formatting.GRAY))
                        .append(Text.literal(String.valueOf(MAX_FUEL)).formatted(Formatting.GRAY))
        );

        super.appendTooltip(stack, world, tooltip, context);
    }
}

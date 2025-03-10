package dev.amble.ait.core.item.control;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import dev.amble.ait.core.entities.ConsoleControlEntity;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.registry.ControlRegistry;

public abstract class ControlBlockItem extends BlockItem {
    public static final String CONTROL_ID_KEY = "controlId";

    protected ControlBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient() && entity instanceof ConsoleControlEntity ce) {
            setControl(stack, ce.getControl().getId());
            return ActionResult.SUCCESS;
        }

        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        Optional<String> id = findControlId(stack);

        if (id.isPresent()) {
            tooltip.add(Text.translatable(id.get().toUpperCase()).formatted(Formatting.AQUA));
        }

        super.appendTooltip(stack, world, tooltip, context);
    }

    private static void setControl(ItemStack stack, String id) {
        stack.getOrCreateNbt().putString(CONTROL_ID_KEY, id);
    }

    public static Optional<String> findControlId(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();

        if (!nbt.contains(CONTROL_ID_KEY)) {
            return Optional.empty();
        }

        return Optional.of(stack.getOrCreateNbt().getString(CONTROL_ID_KEY));
    }

    public static Optional<Control> findControl(ItemStack stack) {
        Optional<String> id = findControlId(stack);
        if (id.isEmpty())
            return Optional.empty();

        return ControlRegistry.fromId(id.get());
    }
}

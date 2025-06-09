package dev.amble.ait.core.item;

import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.tardis.handler.distress.DistressCall;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HypercubeItem extends Item {
    public HypercubeItem(Settings settings) {
        super(settings.maxDamageIfAbsent(100));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (hand != Hand.MAIN_HAND) return TypedActionResult.fail(user.getStackInHand(hand));

        ItemStack held = user.getMainHandStack();

        if (user.getWorld().isClient()) {
            MinecraftClient.getInstance().gameRenderer.showFloatingItem(held);

            return TypedActionResult.success(user.getStackInHand(hand));
        }

        DistressCall call = getCall(held, world.getServer().getTicks());
        if (call == null) {
            call = DistressCall.create(user, held.hasCustomName() ? held.getName().getString() : "SOS", true);
            setCall(held, call);
        }

        boolean success = call.send(user.getUuid(), held);

        user.getItemCooldownManager().set(this, 15 * 20);

        return success ? TypedActionResult.success(user.getStackInHand(hand)) : TypedActionResult.fail(user.getStackInHand(hand));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (world.isClient()) return;

        DistressCall call = getCall(stack, world.getServer().getTicks());
        if (call == null) return;
        if (call.isSourceCall()) return;

        stack.setDamage((int) ((1f - (((float) call.getTimeLeft() / (call.lifetime())))) * stack.getMaxDamage()));

        if (call.isValid()) return;

        stack.setCount(0);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        if (world == null) return;

        DistressCall call = getCall(stack, 0);
        if (call == null) return;

        if (call.isSourceCall()) {
            tooltip.add(Text.translatable("tooltip.ait.distresscall.source").formatted(Formatting.BOLD, Formatting.GOLD));
        }
        tooltip.add(Text.literal(call.message()).formatted(Formatting.ITALIC, Formatting.RED));
        tooltip.add(call.sender().getTooltip());
    }

    public static DistressCall getCall(ItemStack stack, int ticks) {
        NbtCompound data = stack.getOrCreateNbt();
        if (!data.contains("DistressCall")) return null;

        return DistressCall.fromNbt(data.getCompound("DistressCall"), ticks);
    }
    public static void setCall(ItemStack stack, DistressCall call) {
        stack.getOrCreateNbt().put("DistressCall", call.toNbt());

        if (stack.hasCustomName()) {
            stack.setCustomName(null);
        }
    }

    public static ItemStack create(DistressCall call) {
        ItemStack stack = new ItemStack(AITItems.HYPERCUBE);
        setCall(stack, call);
        return stack;
    }
}

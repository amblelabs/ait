package dev.amble.ait.core.item;

import java.util.List;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.api.link.LinkableItem;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.tardis.Tardis;

// todo fix so many issues with having more than one of this item
public class SiegeTardisItem extends LinkableItem {

    public static final String CURRENT_TEXTURE_KEY = "siege_current_texture";

    public SiegeTardisItem(Settings settings) {
        super(settings.maxCount(1), "tardis-uuid", true);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (world.isClient())
            return;

        Tardis tardis = this.getTardis(world, stack);

        if (tardis == null) {
            stack.setCount(0);
            return;
        }

        if (!tardis.siege().isActive()) {
            tardis.setSiegeBeingHeld(null);
            stack.setCount(0);
            return;
        }

        if (entity instanceof ServerPlayerEntity player)
            tardis.siege().setSiegeBeingHeld(player.getUuid());

        tardis.travel().forcePosition(fromEntity(entity));

        if (!tardis.isSiegeBeingHeld()) {
            tardis.setSiegeBeingHeld(entity.getUuid());
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getHand() != Hand.MAIN_HAND)
            return ActionResult.FAIL; // bc i cba

        if (context.getWorld().isClient())
            return ActionResult.SUCCESS;

        ItemStack stack = context.getStack();
        Tardis tardis = this.getTardis(context.getWorld(), stack);

        ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
        context.getStack().setCount(0);

        player.getInventory().markDirty();

        if (tardis == null)
            return ActionResult.FAIL;

        if (!tardis.siege().isActive()) {
            tardis.setSiegeBeingHeld(null);
            return ActionResult.FAIL;
        }

        placeTardis(tardis, fromItemContext(context));
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound tag = stack.getOrCreateNbt();
        String text = tag.contains("tardis-uuid")
                ? tag.getUuid("tardis-uuid").toString().substring(0, 8)
                : Text.translatable("tooltip.ait.remoteitem.notardis").getString();

        tooltip.add(Text.literal("→ " + text).formatted(Formatting.BLUE));
    }

    public static CachedDirectedGlobalPos fromItemContext(ItemUsageContext context) {
        return CachedDirectedGlobalPos.create((ServerWorld) context.getWorld(),
                context.getBlockPos().offset(context.getSide()), (byte) 0);
    }

    public static CachedDirectedGlobalPos fromEntity(Entity entity) {
        return CachedDirectedGlobalPos.create((ServerWorld) entity.getWorld(), BlockPos.ofFloored(entity.getPos()),
                (byte) 0);
    }

    public static void pickupTardis(Tardis tardis, ServerPlayerEntity player) {
        if (tardis.travel().handbrake())
            return;

        tardis.travel().deleteExterior();
        tardis.siege().setSiegeBeingHeld(player.getUuid());
        player.getInventory().insertStack(create(tardis));
        player.getInventory().markDirty();
    }

    public static void placeTardis(Tardis tardis, CachedDirectedGlobalPos pos) {
        tardis.travel().forcePosition(pos);
        tardis.travel().placeExterior(false);
        tardis.setSiegeBeingHeld(null);
    }

    public static ItemStack create(Tardis tardis) {
        ItemStack stack = new ItemStack(AITItems.SIEGE_ITEM);
        stack.setCount(1);

        SiegeTardisItem.linkStatic(stack, tardis);
        return stack;
    }
}

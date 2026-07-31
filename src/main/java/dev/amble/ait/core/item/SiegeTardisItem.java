package dev.amble.ait.core.item;

import java.util.List;

import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.api.tardis.link.LinkableItem;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.tardis.Tardis;
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

// todo fix so many issues with having more than one of this item
public class SiegeTardisItem extends LinkableItem {
    static {
        TardisEvents.ENTER_TARDIS.register((tardis, entity) -> {
            if (!(entity instanceof ServerPlayerEntity player))
                return TardisEvents.Interaction.PASS;
            SiegeInventoryUtil.ScanResult siege = SiegeInventoryUtil.scanCarried(player, tardis.getUuid());
            if (!siege.blocksEntry())
                return TardisEvents.Interaction.PASS;

            player.sendMessage(Text.translatable("ait.tooltip.siege_item.enter").formatted(Formatting.RED), true);
            return TardisEvents.Interaction.FAIL;
        });
    }

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

        SiegeInventoryUtil.rememberTrackedSiegeItem(entity, tardis.getUuid());

        if (!tardis.siege().isActive()) {
            tardis.setSiegeBeingHeld(null);
            tardis.returnHome().clearSiegeItemContainer();
            return;
        }

        tardis.returnHome().trackSiegeItemEntity(entity);
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getHand() != Hand.MAIN_HAND || context.getPlayer() == null)
            return ActionResult.PASS;

        if (context.getWorld().isClient())
            return ActionResult.SUCCESS;

        Tardis tardis = this.getTardis(context.getWorld(), context.getStack());
        if (tardis == null) {
            context.getStack().decrement(1);
            return ActionResult.CONSUME;
        }

        if (!tardis.siege().isActive()) {
            tardis.setSiegeBeingHeld(null);
            context.getStack().decrement(1);
            return ActionResult.SUCCESS;
        }

        return placeTardis(tardis, fromItemContext(context), context.getPlayer())
                ? super.useOnBlock(context) : ActionResult.FAIL;
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
        if (tardis.travel().handbrake() || player.getServer() == null
                || !tardis.returnHome().canCreateSiegeItem(player.getServer())
                || !tardis.getExterior().hasValidExteriorBlock())
            return;

        int slot = player.getInventory().getEmptySlot();
        if (slot < 0 || !tardis.travel().tryDeleteExterior())
            return;

        tardis.returnHome().clearSiegeItemContainer();
        tardis.siege().setSiegeBeingHeld(player.getUuid());
        player.getInventory().setStack(slot, create(tardis));
        player.getInventory().markDirty();
    }

    public static boolean placeTardis(Tardis tardis, CachedDirectedGlobalPos pos) {
        return placeTardis(tardis, pos, null);
    }

    public static boolean placeTardis(Tardis tardis, CachedDirectedGlobalPos pos, @Nullable Entity carrier) {
        pos = tardis.returnHome().resolveSiegeExteriorPlacement(pos);
        ServerWorld world = pos == null ? null : pos.getWorld();
        if (world == null || !tardis.siege().isActive())
            return false;

        boolean movingExterior = tardis.getExterior().hasValidExteriorBlock();
        if (movingExterior && (carrier != null
                || !tardis.returnHome().canMaterializeSiegeExterior(world.getServer())))
            return false;

        var provisional = tardis.travel().placeProvisionalExterior(pos);
        if (provisional == null)
            return false;

        boolean prepared = movingExterior ? tardis.travel().tryDeleteExterior()
                : carrier == null
                ? tardis.returnHome().prepareSiegeExteriorPlacement(world.getServer(), false)
                : tardis.returnHome().prepareSiegeExteriorPlacement(world.getServer(), carrier);
        if (!prepared) {
            provisional.rollback();
            return false;
        }

        tardis.travel().forcePosition(pos);
        tardis.setSiegeBeingHeld(null);
        return true;
    }

    public static ItemStack create(Tardis tardis) {
        ItemStack stack = new ItemStack(AITItems.SIEGE_ITEM);
        stack.setCount(1);

        SiegeTardisItem.linkStatic(stack, tardis);
        return stack;
    }
}

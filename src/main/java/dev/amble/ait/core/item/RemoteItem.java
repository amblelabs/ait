package dev.amble.ait.core.item;

import dev.amble.ait.api.tardis.link.LinkableItem;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.impl.DirectionControl;
import dev.amble.ait.core.tardis.handler.travel.TravelUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase.State.LANDED;

public class RemoteItem extends LinkableItem {

    public RemoteItem(Settings settings) {
        super(settings, true);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        ItemStack itemStack = context.getStack();

        if (player == null)
            return ActionResult.PASS;

        if (!(world instanceof ServerWorld serverWorld))
            return ActionResult.PASS;

        Tardis tardis = RemoteItem.getTardisStatic(world, itemStack);

        if (tardis == null)
            return ActionResult.FAIL;

        if (tardis.getFuel() <= 0)
            player.sendMessage(Text.translatable("message.ait.remoteitem.warning1"));

        if (tardis.isRefueling())
            player.sendMessage(Text.translatable("message.ait.remoteitem.cancel.refuel"));

        //It was dematting before anyway so as a lazy fix its a feature now!!
        //player.sendMessage(Text.translatable("message.ait.remoteitem.warning2"));

        // Check if the Tardis is already present at this location before moving
        // it there
        CachedDirectedGlobalPos currentPosition = tardis.travel().position();

        if (currentPosition.getPos().equals(pos))
            return ActionResult.FAIL;

        if (!TardisServerWorld.isTardisDimension((ServerWorld) world)) {
            world.playSound(null, pos, AITSounds.REMOTE, SoundCategory.BLOCKS);

            BlockPos temp = pos.up();

            if (world.getBlockState(pos).isReplaceable())
                temp = pos;

            tardis.travel().speed(tardis.travel().maxSpeed().get());

            TravelUtil.travelTo(tardis, CachedDirectedGlobalPos.create(serverWorld, temp, DirectionControl
                    .getGeneralizedRotation(RotationPropertyHelper.fromYaw(player.getBodyYaw()))));
        } else {
            world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), SoundCategory.BLOCKS, 1F,
                    0.2F);
            player.sendMessage(Text.translatable("message.ait.remoteitem.warning3"), true);
        }

        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        Tardis tardis = RemoteItem.getTardisStatic(world, stack);

        if (tardis == null)
            return;

        if (tardis.travel().getState() != LANDED)
            tooltip.add(Text.literal("→ " + tardis.travel().getDurationAsPercentage() + "%")
                    .formatted(Formatting.GOLD));
    }
}

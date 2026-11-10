package dev.amble.ait.core.item;

import java.util.List;

import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.item.sonic.SonicMode;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Waypoint;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedGlobalPos;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ControlDiscItem extends AbstractCoordinateModifierItem {

    public static final String CAN_CONTAIN_PLAYERS = "can_contain_players";

    public ControlDiscItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (TardisServerWorld.isTardisDimension(world)) {
            user.sendMessage(Text.translatable("ait.control_disc.unusable_in_tardis_world"), true);
            return TypedActionResult.fail(user.getStackInHand(hand));
        }

        ItemStack discStack = user.getStackInHand(hand);
        ItemStack otherStack = user.getStackInHand(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);

        if (otherStack.getItem() instanceof SonicItem sonic && sonic.isLinked(otherStack)) {
            SonicMode mode = SonicItem.mode(otherStack);
            if (mode.equals(SonicMode.Modes.INTERACTION) && AbstractCoordinateModifierItem.getPos(discStack) == null) {
                CachedDirectedGlobalPos targetPos = CachedDirectedGlobalPos.create(world.getRegistryKey(),
                        user.getBlockPos(), DirectedGlobalPos.getGeneralizedRotation(user.getMovementDirection()));
                AbstractCoordinateModifierItem.setPos(discStack, targetPos);
                ControlDiscItem.setCanContainPlayers(discStack, true);
                user.playSound(AITSounds.DING, 1f, 1f);
                user.sendMessage(Text.translatable("ait.control_disc.set_position")
                        .append(Text.literal(" > " + targetPos)
                                .formatted(Formatting.BLUE)), true);
            } else if (mode.equals(SonicMode.Modes.OVERLOAD) && AbstractCoordinateModifierItem.getPos(discStack) != null) {
                ControlDiscItem.setCanContainPlayers(discStack, !ControlDiscItem.canContainPlayers(discStack));
                user.playSound(AITSounds.DING, 1f, 0.1f);
                user.sendMessage(Text.translatable("ait.control_disc.can_contain_players.toggle", ControlDiscItem.canContainPlayers(discStack))
                                .formatted(Formatting.BLUE), true);
            }
        }

        return super.use(world, user, hand);
    }

    public static boolean canContainPlayers(ItemStack stack) {
        NbtCompound main = stack.getOrCreateNbt();
        if (!main.contains(CAN_CONTAIN_PLAYERS))
            return false;
        return main.getBoolean(CAN_CONTAIN_PLAYERS);
    }

    public static void setCanContainPlayers(ItemStack stack, boolean canContainPlayers) {
        NbtCompound main = stack.getOrCreateNbt();
        main.putBoolean(CAN_CONTAIN_PLAYERS, canContainPlayers);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        NbtCompound main = stack.getOrCreateNbt();
        if (!main.contains(CAN_CONTAIN_PLAYERS))
            return;
        boolean canContainPlayers = main.getBoolean(CAN_CONTAIN_PLAYERS);
        tooltip.add(Text.translatable("ait.control_disc.can_contain_players.toggle", canContainPlayers)
                .formatted(Formatting.BLUE));
    }

    public static ItemStack create(Waypoint pos) {
        ItemStack stack = new ItemStack(AITItems.CONTROL_DISC);
        if (pos == null) return stack;

        setPos(stack, pos.getPos());

        if (pos.hasName())
            stack.setCustomName(Text.literal(pos.name()));

        return stack;
    }
}

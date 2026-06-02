package dev.amble.ait.core.item;

import dev.amble.ait.api.tardis.link.LinkableItem;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.item.sonic.SonicMode;
import dev.amble.ait.core.tardis.control.impl.DirectionControl;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Waypoint;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedGlobalPos;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.amble.ait.client.util.TooltipUtil.addShiftHiddenTooltip;

public class ControlDiscItem extends AbstractCoordinateModifierItem {

    public static final String CAN_CONTAIN_PLAYERS = "can_contain_players";

    public ControlDiscItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // if (world.isClient()) return TypedActionResult.consume(user.getMainHandStack()); //<-- Not sure if it being on either is an issue or not - Loqor
        ItemStack offhand = user.getOffHandStack();
        ItemStack mainhand = user.getMainHandStack();

        if (TardisServerWorld.isTardisDimension(world)) {
            user.sendMessage(Text.translatable("ait.control_disc.unusable_in_tardis_world"), true);
            return TypedActionResult.fail(user.getMainHandStack());
        }
        if (offhand.getItem() instanceof SonicItem sonic) {
            if (sonic.isLinked(offhand)) {
                SonicMode mode = SonicItem.mode(offhand);
                if (mode.equals(SonicMode.Modes.INTERACTION) && AbstractCoordinateModifierItem.getPos(mainhand) == null) {
                    CachedDirectedGlobalPos targetPos = CachedDirectedGlobalPos.create(world.getRegistryKey(),
                            user.getBlockPos(), DirectedGlobalPos.getGeneralizedRotation(user.getMovementDirection()));
                    AbstractCoordinateModifierItem.setPos(user.getMainHandStack(), targetPos);
                    ControlDiscItem.setCanContainPlayers(mainhand, true);
                    user.playSound(AITSounds.DING, 1f, 1f);
                    user.sendMessage(Text.translatable("ait.control_disc.set_position")
                            .append(Text.literal(" > " + targetPos)
                                    .formatted(Formatting.BLUE)), true);
                } else if (mode.equals(SonicMode.Modes.OVERLOAD) && AbstractCoordinateModifierItem.getPos(mainhand) != null) {
                    ControlDiscItem.setCanContainPlayers(mainhand, !ControlDiscItem.canContainPlayers(mainhand));
                    user.playSound(AITSounds.DING, 1f, 0.1f);
                    user.sendMessage(Text.translatable("ait.control_disc.can_contain_players.toggle", ControlDiscItem.canContainPlayers(mainhand))
                                    .formatted(Formatting.BLUE), true);
                }
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
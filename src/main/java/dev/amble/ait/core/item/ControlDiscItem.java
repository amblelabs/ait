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
    public ControlDiscItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // if (world.isClient()) return; <-- Not sure if it being on either is an issue or not - Loqor
        ItemStack offhand = user.getOffHandStack();
        if (offhand.getItem() instanceof SonicItem sonic) {
            if (sonic.isLinked(offhand) && SonicItem.mode(offhand) == SonicMode.Modes.INTERACTION) {
                CachedDirectedGlobalPos targetPos = CachedDirectedGlobalPos.create(world.getRegistryKey(),
                        user.getBlockPos(), DirectedGlobalPos.getGeneralizedRotation(user.getMovementDirection()));
                AbstractCoordinateModifierItem.setPos(user.getMainHandStack(), targetPos);
                System.out.println(user.getMainHandStack());
                user.playSound(AITSounds.DING, 1f, 1f);
                user.sendMessage(Text.translatable("ait.control_disc.set_position")
                        .append(Text.literal(" > " + targetPos)
                                .formatted(Formatting.BLUE)), true);
            }
        }
        return super.use(world, user, hand);
    }
}
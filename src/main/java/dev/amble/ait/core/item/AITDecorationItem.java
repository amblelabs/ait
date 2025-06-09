package dev.amble.ait.core.item;

import dev.amble.ait.core.AITEntityTypes;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.entities.BOTIPaintingEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AITDecorationItem extends Item {
    private final EntityType<? extends AbstractDecorationEntity> entityType;

    public AITDecorationItem(EntityType<? extends AbstractDecorationEntity> type, Item.Settings settings) {
        super(settings);
        this.entityType = type;
    }



    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.getBlockPos();
        Direction clickedSide = context.getSide();
        PlayerEntity player = context.getPlayer();
        ItemStack itemStack = context.getStack();

        Direction facing = clickedSide.getAxis().isVertical() ? player.getHorizontalFacing().getOpposite() : clickedSide;

        BlockPos placementPos = blockPos.offset(facing);

        if (player != null && !this.canPlaceOn(player, facing, itemStack, placementPos)) {
            return ActionResult.FAIL;
        }

        World world = context.getWorld();

        if (this.entityType == AITEntityTypes.GALLIFREY_FALLS_PAINTING_ENTITY_TYPE || this.entityType == AITEntityTypes.TRENZALORE_PAINTING_ENTITY_TYPE) {
            Optional<BOTIPaintingEntity> optional = BOTIPaintingEntity.placePainting((EntityType<? extends BOTIPaintingEntity>)
                    this.entityType, world, placementPos, facing);

            if (optional.isEmpty()) {
                return ActionResult.CONSUME;
            }

            BOTIPaintingEntity paintingEntity = optional.get();

            NbtCompound nbtData = itemStack.getNbt();
            if (nbtData != null) {
                EntityType.loadFromEntityNbt(world, player, paintingEntity, nbtData);
            }

            if (!world.isClient) {
                paintingEntity.onPlace();
                world.emitGameEvent(player, GameEvent.ENTITY_PLACE, paintingEntity.getPos());
                world.spawnEntity(paintingEntity);
            }

            itemStack.decrement(1);
            return ActionResult.success(world.isClient);
        }

        return ActionResult.PASS;
    }


    protected boolean canPlaceOn(PlayerEntity player, Direction side, ItemStack stack, BlockPos pos) {
        return !side.getAxis().isVertical() && player.canPlaceOn(pos, side, stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);


        if (stack.getItem() == AITItems.GALLIFREY_FALLS_PAINTING) {
            tooltip.add(Text.translatable("painting.ait.gallifrey_falls.title").formatted(Formatting.YELLOW));
            tooltip.add(Text.translatable("painting.ait.gallifrey_falls.author").formatted(Formatting.GRAY));
        }
        if (stack.getItem() == AITItems.TRENZALORE_PAINTING) {
            tooltip.add(Text.translatable("painting.ait.trenzalore.title").formatted(Formatting.YELLOW));
            tooltip.add(Text.translatable("painting.ait.trenzalore.author").formatted(Formatting.GRAY));
        }
    }
}

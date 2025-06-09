package dev.amble.ait.core.blocks.control;

import dev.amble.ait.core.blockentities.control.ControlBlockEntity;
import dev.amble.ait.core.blocks.types.HorizontalDirectionalBlock;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.item.control.ControlBlockItem;
import dev.amble.ait.core.item.sonic.SonicMode;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class ControlBlock extends HorizontalDirectionalBlock implements BlockEntityProvider {

    public ControlBlock(Settings settings) {
        super(settings);
    }

    @Override
    public Item asItem() {
        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack itemStack) {
        Optional<Identifier> id = ControlBlockItem.findControlId(itemStack);

        if (id.isEmpty())
            return;

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof ControlBlockEntity))
            return;

        ((ControlBlockEntity) be).setControlId(id.get());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (world.isClient())
            return ActionResult.SUCCESS;

        if (!(world.getBlockEntity(pos) instanceof ControlBlockEntity be))
            return ActionResult.FAIL;

        if (isHoldingScanningSonic(player))
            sendSonicMessage((ServerPlayerEntity) player, be);

        return be.run((ServerPlayerEntity) player, false)
                ? ActionResult.SUCCESS : ActionResult.FAIL;
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (world.isClient())
            return;

        if (!(world.getBlockEntity(pos) instanceof ControlBlockEntity be))
            return;

        if (isHoldingScanningSonic(player))
            sendSonicMessage((ServerPlayerEntity) player, be);

        be.run((ServerPlayerEntity) player, true);
    }

    protected static boolean isHoldingScanningSonic(PlayerEntity player) {
        return SonicItem.mode(player.getMainHandStack()) == SonicMode.Modes.SCANNING;
    }

    protected static void sendSonicMessage(ServerPlayerEntity player, ControlBlockEntity entity) {
        player.sendMessage(Text.translatable(entity.getControl().id().toTranslationKey("control")).formatted(Formatting.AQUA));
    }
}

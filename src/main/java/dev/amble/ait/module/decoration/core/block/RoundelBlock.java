package dev.amble.ait.module.decoration.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RoundelBlock extends Block {
    public static final EnumProperty<Direction> FACING = EnumProperty.of("facing", Direction.class);
    private final String id;

    public RoundelBlock(Settings settings, String RoundelID) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
        this.id = RoundelID;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        // Get the player's facing direction (this will set the block to face the direction the player is facing)
        return this.getDefaultState().with(FACING, context.getPlayer().getHorizontalFacing());
    }

    public String RoundelID() {
        return id;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState facingState, WorldAccess world, BlockPos pos, BlockPos facingPos) {
        // Update the facing if a neighboring block is adjacent
        if (facing == state.get(FACING).getOpposite()) {
            return state.with(FACING, facing);
        }
        return super.getStateForNeighborUpdate(state, facing, facingState, world, pos, facingPos);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);

        tooltip.add(Text.translatable("tooltip.ait.roundel_type").formatted(Formatting.BLUE, Formatting.ITALIC));
        tooltip.add(Text.literal(RoundelID()).formatted(Formatting.DARK_GRAY));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        // Add the facing property to the block's state manager
        builder.add(FACING);
    }

}

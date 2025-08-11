package dev.amble.ait.core.blocks.decoration;

import dev.amble.ait.core.AITEntityTypes;
import dev.amble.ait.core.blockentities.decoration.CopperRingsBlockEntity;
import dev.amble.ait.core.blockentities.decoration.CoralSeatBlockEntity;
import dev.amble.ait.core.entities.SeatEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CopperRingsBlock extends BlockWithEntity {

    public CopperRingsBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CopperRingsBlockEntity(pos, state);
    }
}

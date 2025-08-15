package dev.amble.ait.core.blocks;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.FoodMachineBlockEntity;
import dev.amble.ait.core.drinks.DrinkRegistry;
import dev.amble.ait.core.drinks.DrinkUtil;

public class FoodMachineBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final int MAX_ROTATION_INDEX = RotationPropertyHelper.getMax();
    private static final int MAX_ROTATIONS = MAX_ROTATION_INDEX + 1;
    public static final IntProperty ROTATION = Properties.ROTATION;

    protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(
            2.0,
            0.0,
            2,
            14.0,
            25.0,
            14
    );

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Y_SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Y_SHAPE;
    }

    public FoodMachineBlock(Settings settings) {
        super(FabricBlockSettings.of()
                .strength(3.0F, 6.0F)
                .requiresTool());
        this.setDefaultState(this.stateManager.getDefaultState().with(ROTATION, 0));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof FoodMachineBlockEntity machine)) return ActionResult.SUCCESS;
        if (!machine.isPoweredOn()) return ActionResult.PASS;
        ItemStack stack = player.getStackInHand(hand);

        // cycle through different modes
        if (player.isSneaking() && stack.isEmpty()) {
            world.playSound(null, pos, AITSounds.LOAD_WAYPOINT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            switch (machine.getMode()) {
                case FOOD_CUBES -> {
                    machine.setMode(FoodMachineBlockEntity.Mode.DRINKS);
                    player.sendMessage(Text.translatable("ait.foodmachine.mode.drinks").copy().formatted(Formatting.AQUA), true);
                }
                case DRINKS -> {
                    machine.setMode(FoodMachineBlockEntity.Mode.OVERCHARGED_FOOD_CUBES);
                    player.sendMessage(Text.translatable("ait.foodmachine.mode.overcharged_food_cubes").copy().formatted(Formatting.LIGHT_PURPLE), true);
                }
                case OVERCHARGED_FOOD_CUBES -> {
                    machine.setMode(FoodMachineBlockEntity.Mode.FOOD_CUBES);
                    player.sendMessage(Text.translatable("ait.foodmachine.mode.food_cubes").copy().formatted(Formatting.GREEN), true);
                }

            }
            return ActionResult.SUCCESS;
        }

        // logic for the current mode selected
        switch (machine.getMode()) {
            case FOOD_CUBES -> {
                machine.eatFuel();
                world.playSound(null, pos, AITSounds.POWER_CONVERT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                player.getInventory().insertStack(AITItems.FOOD_CUBE.getDefaultStack());
            }
            case DRINKS -> {
                machine.eatFuel();
                world.playSound(null, pos, AITSounds.COFFEE_MACHINE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (machine.getSelectedItem() == null) {
                    machine.setSelectedItem(DrinkUtil.setDrink(new ItemStack(AITItems.MUG), DrinkRegistry.getInstance().toList().get(1)));
                }
                player.getInventory().insertStack(machine.getSelectedItem().copy());
            }
            case OVERCHARGED_FOOD_CUBES -> {
                machine.eatFuel();
                world.playSound(null, pos, AITSounds.POWER_CONVERT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                player.getInventory().insertStack(AITItems.OVERCHARGED_FOOD_CUBE.getDefaultStack());
            }

        }
        return ActionResult.SUCCESS;
    }

    {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof FoodMachineBlockEntity machine)) return ActionResult.PASS;
            if (!machine.isPoweredOn()) return ActionResult.PASS;
            if (world.getBlockState(pos).getBlock() instanceof FoodMachineBlock) {
                if (player.isSneaking() && machine.getMode() == FoodMachineBlockEntity.Mode.DRINKS) {
                    if (!world.isClient) {
                        long now = world.getTime();
                        int cooldownTicks = 5;
                        if (now - machine.getLastDrinkTime() < cooldownTicks) {
                            return ActionResult.FAIL;
                        }
                        machine.setLastDrinkTime(now);
                        int currentIndex = (machine.getCurrentIndex() + 1) % DrinkRegistry.getInstance().size();
                        machine.setCurrentIndex(currentIndex);
                        ItemStack selectedItem = DrinkUtil.setDrink(new ItemStack(AITItems.MUG), DrinkRegistry.getInstance().toList().get(machine.getCurrentIndex()));
                        machine.setSelectedItem(selectedItem);
                        player.sendMessage(Text.translatable("ait.foodmachine.mode.refreshement_set_to", selectedItem.getName()), true);
                    }
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return AITBlockEntityTypes.FOOD_MACHINE_BLOCK_ENTITY_TYPE.instantiate(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(ROTATION, RotationPropertyHelper.fromYaw(ctx.getPlayerYaw()));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(ROTATION, rotation.rotate(state.get(ROTATION), MAX_ROTATIONS));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(ROTATION, mirror.mirror(state.get(ROTATION), MAX_ROTATIONS));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }


}

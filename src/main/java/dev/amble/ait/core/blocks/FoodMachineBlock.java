package dev.amble.ait.core.blocks;

import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.FoodMachineBlockEntity;
import dev.amble.ait.core.drinks.DrinkRegistry;
import dev.amble.ait.core.drinks.DrinkUtil;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
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
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.BlockPos;
import dev.amble.ait.core.AITBlockEntityTypes;

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
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ROTATION, 0));
    }


    private ItemStack selectedItem;

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof FoodMachineBlockEntity machine)) return ActionResult.SUCCESS;
            if (!machine.isPoweredOn()) return ActionResult.PASS;

            FoodMachineBlockEntity.FoodMachineMode currentMode = machine.getMode();

            //mode logic
            ItemStack stack = player.getStackInHand(hand);

            if (player.isSneaking() && stack.isEmpty()) {
                char currentModeMessage = 0;
                world.playSound(null, pos, AITSounds.SET_WAYPOINT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                switch (currentMode) {
                    case FOOD_CUBES:
                        machine.setMode(FoodMachineBlockEntity.FoodMachineMode.DRINKS);
                        currentModeMessage = 'D';
                        break;
                    case DRINKS:
                        machine.setMode(FoodMachineBlockEntity.FoodMachineMode.OVERCHARGED_FOOD_CUBES);
                        currentModeMessage = 'O';
                        break;
                    case OVERCHARGED_FOOD_CUBES:
                        machine.setMode(FoodMachineBlockEntity.FoodMachineMode.FOOD_CUBES);
                        currentModeMessage = 'F';
                        break;
                }
                switch (currentModeMessage) {
                    case 'F':
                        player.sendMessage(Text.of("Food Cubes").copy().formatted(Formatting.GREEN), true);
                        break;
                    case 'D':
                        player.sendMessage(Text.of("Drinks").copy().formatted(Formatting.AQUA), true);
                        break;
                    case 'O':
                        player.sendMessage(Text.of("Overcharged Food Cubes").copy().formatted(Formatting.LIGHT_PURPLE), true);
                        break;
                }
            }

            if (!player.isSneaking()) {
                if (machine.getMode() == FoodMachineBlockEntity.FoodMachineMode.FOOD_CUBES) {
                    machine.eatFuel(139);
                    world.playSound(null, pos, AITSounds.POWER_CONVERT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    player.getInventory().insertStack(AITItems.FOOD_CUBE.getDefaultStack());
                } else if (machine.getMode() == FoodMachineBlockEntity.FoodMachineMode.DRINKS) {
                    machine.eatFuel(193);
                    world.playSound(null, pos, AITSounds.COFFEE_MACHINE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    if (selectedItem == null) {
                        selectedItem = DrinkUtil.setDrink(new ItemStack(AITItems.MUG), DrinkRegistry.getInstance().toList().get(machine.getCurrentIndex()));
                    }
                    player.getInventory().insertStack(selectedItem.copy());
                } else if (machine.getMode() == FoodMachineBlockEntity.FoodMachineMode.OVERCHARGED_FOOD_CUBES) {
                    machine.eatFuel(437);
                    world.playSound(null, pos, AITSounds.POWER_CONVERT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    player.getInventory().insertStack(AITItems.OVERCHARGED_FOOD_CUBE.getDefaultStack());
                }
            }

        return ActionResult.SUCCESS;
    }

    private long lastDrinkTime = 0;

    {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
                BlockEntity be = world.getBlockEntity(pos);
                if (!(be instanceof FoodMachineBlockEntity machine)) return ActionResult.SUCCESS;
                if (!machine.isPoweredOn()) return ActionResult.PASS;
                if (world.getBlockState(pos).getBlock() instanceof FoodMachineBlock) {
                    if (player.isSneaking() && machine.getMode() == FoodMachineBlockEntity.FoodMachineMode.DRINKS) {
                        long now = System.currentTimeMillis();
                        if (now - lastDrinkTime < 270) {
                            return ActionResult.FAIL;
                        }
                        lastDrinkTime = now;
                        int currentIndex = (machine.getCurrentIndex() + 1) % DrinkRegistry.getInstance().size();
                        machine.setCurrentIndex(currentIndex);
                        this.selectedItem = DrinkUtil.setDrink(new ItemStack(AITItems.MUG), DrinkRegistry.getInstance().toList().get(machine.getCurrentIndex()));
                        player.sendMessage(Text.literal("Refreshment set to: " + selectedItem.getName().getString() + "!"), true);
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

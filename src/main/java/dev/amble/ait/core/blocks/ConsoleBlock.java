package dev.amble.ait.core.blocks;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.blocks.types.HorizontalDirectionalBlock;
import dev.amble.ait.core.item.HammerItem;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.schema.console.type.CopperType;
import dev.amble.ait.data.schema.console.type.CrystallineType;
import dev.amble.lib.api.ICantBreak;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ConsoleBlock extends HorizontalDirectionalBlock implements BlockEntityProvider, ICantBreak {

    private static final VoxelShape SHAPE;

    static {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0, 0, 0, 1, 0.875, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0, 0.875, -0.25, 1, 1, 1.25), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0, 1, 0, 1, 1.125, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(-0.25, 0.875, 0, 1.25, 1, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(-0.1875, 0.875, -0.125, 1.1875, 1, 0),
                BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(-0.1875, 0.875, 1, 1.1875, 1, 1.125),
                BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(1, 0.875, -0.1875, 1.125, 1, 1.1875),
                BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(-0.125, 0.875, -0.1875, 0, 1, 1.1875),
                BooleanBiFunction.OR);

        SHAPE = shape;
    }

    public ConsoleBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConsoleBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
                              BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ConsoleBlockEntity consoleBlockEntity) {
            if (world.getRegistryKey().equals(World.OVERWORLD)) return ActionResult.FAIL;
            consoleBlockEntity.useOn(world, player.isSneaking(), player);
            ItemStack itemStack = player.getStackInHand(hand);
            if (itemStack.getItem() instanceof HammerItem) {
                itemStack.getItem().useOnBlock(new ItemUsageContext(world, player, hand, itemStack, hit));
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull World world, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return (world1, blockPos, blockState, ticker) -> {
            if (ticker instanceof ConsoleBlockEntity console) {
                console.tick(world, blockPos, blockState, console);
            }
        };
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
                         ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (world.getBlockEntity(pos) instanceof ConsoleBlockEntity consoleBlockEntity) {
            if (world.getRegistryKey().equals(World.OVERWORLD)) {
                return;
            }
            consoleBlockEntity.markNeedsControl();
        }
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 15;
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!TardisServerWorld.isTardisDimension(world)) return;
        if (entity instanceof PlayerEntity player) {
            Random random = new Random();
            int x_random = random.nextInt(1, 10);
            int y_random = random.nextInt(1, 10);
            int z_random = random.nextInt(1, 10);

            boolean is_x_negative = false;
            boolean is_z_negative = false;
            if (random.nextInt(1, 3) == 1) {
                is_x_negative = true;
            }
            if (random.nextInt(1, 3) == 1) {
                is_z_negative = true;
            }

            world.playSound(null, pos, AITSounds.CLOISTER, SoundCategory.BLOCKS, 4f, 1f);

            player.addVelocity(0.15f * x_random * (is_x_negative ? -1 : 1), 0.1f * y_random,
                    0.15f * z_random * (is_z_negative ? -1 : 1));

            if (player instanceof ServerPlayerEntity) {
                for (int i = 0; i < 100; i++) {
                    ((ServerWorld) world).spawnParticles(ParticleTypes.ANGRY_VILLAGER,
                            pos.getX() + random.nextFloat(-2, 3), pos.getY() + random.nextFloat(2),
                            pos.getZ() + random.nextFloat(-2, 3), 1, random.nextFloat(-5, 5), random.nextFloat(-5, 5),
                            random.nextFloat(-5, 5), 1f);
                }
            }
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    // This will literally never happen
    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);

        if (world.getBlockEntity(pos) instanceof ConsoleBlockEntity console) {
            console.onBroken();
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof ConsoleBlockEntity consoleBlockEntity) {

            if (!consoleBlockEntity.isLinked()) return;

            if (!consoleBlockEntity.tardis().get().fuel().hasPower()) return;

            double d = pos.getX();
            double e = pos.getY();
            double f = pos.getZ();

            if ((consoleBlockEntity.getTypeSchema() instanceof CrystallineType)) {

                for (int i = 0; i < random.nextInt(15) + 1; ++i) {
                    boolean bl = random.nextBoolean();
                    float particleSpeed = random.nextFloat() / 15.0f;

                    world.addParticle(ParticleTypes.SMOKE,
                            (double) pos.getX() + 0.5,
                            (double) pos.getY() + 2,
                            (double) pos.getZ() + 0.5,
                            bl ? particleSpeed : -particleSpeed,
                            2.2E-3,
                            bl ? particleSpeed : -particleSpeed);

                    world.addParticle(ParticleTypes.CLOUD,
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5,
                            0.0,
                            0.1,
                            0.0);
                }
                return;
            }

            if (consoleBlockEntity.tardis() != null &&
                    !consoleBlockEntity.tardis().get().extra().getInsertedDisc().isEmpty() &&
                    consoleBlockEntity.getTypeSchema() instanceof CopperType) {
                for (int i = 0; i < random.nextInt(10) + 1; ++i) {
                    boolean bl = random.nextBoolean();
                    float b = (float)world.getRandom().nextInt(4) / 24.0f;

                    world.addParticle(ParticleTypes.NOTE,
                            d + 1.4,
                            e + 2.6,
                            f - 0.15f,
                            b + 1.5,
                            b,
                            b + 0.5);
                }
            }
        }
    }





    @Override
    public void onTryBreak(World world, BlockPos pos, BlockState state) {
        if (TardisServerWorld.isTardisDimension(world)) return;

        world.breakBlock(pos, true);
    }
}

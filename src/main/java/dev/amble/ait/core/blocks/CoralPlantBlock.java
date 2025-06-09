package dev.amble.ait.core.blocks;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.advancement.TardisCriterions;
import dev.amble.ait.core.blockentities.CoralBlockEntity;
import dev.amble.ait.core.blocks.types.HorizontalDirectionalBlock;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.tardis.handler.LoyaltyHandler;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.tardis.manager.TardisBuilder;
import dev.amble.ait.core.world.RiftChunkManager;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.exterior.variant.growth.CoralGrowthVariant;
import dev.amble.ait.registry.impl.DesktopRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static dev.amble.ait.client.util.TooltipUtil.addShiftHiddenTooltip;

@SuppressWarnings("deprecation")
public class CoralPlantBlock extends HorizontalDirectionalBlock implements BlockEntityProvider {
    private final VoxelShape DEFAULT = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 32.0, 16.0);
    public static final IntProperty AGE = Properties.AGE_7;

    public CoralPlantBlock(Settings settings) {
        super(settings);

        this.setDefaultState(
                this.getDefaultState().with(AGE, 0)
        );
    }

    protected IntProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return 7;
    }

    public int getAge(BlockState state) {
        return state.get(this.getAgeProperty());
    }

    public final boolean isMature(BlockState blockState) {
        return this.getAge(blockState) >= this.getMaxAge();
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        Vec3d centre = pos.up().toCenterPos();
        for (int i = 0; i < getAge(state); i++) {
            double offsetX = AITMod.RANDOM.nextGaussian() * getAge(state) * 0.01f;
            double offsetY = AITMod.RANDOM.nextGaussian() * getAge(state) * 0.01f;
            double offsetZ = AITMod.RANDOM.nextGaussian() * getAge(state) * 0.01f;
            world.addParticle(AITMod.CORAL_PARTICLE, centre.getX(), centre.getY() , centre.getZ(), offsetX, offsetY, offsetZ);
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBaseLightLevel(pos, 0) >= 4) {
            int i = this.getAge(state);
            if (i < this.getMaxAge()) {
                if (!(world.getBlockState(pos.down()).getBlock() instanceof SoulSandBlock)) {
                    world.breakBlock(pos, true);
                    return;
                }

                world.setBlockState(pos, state.with(AGE, i + 1), 2);
            }
        }

        tryCreate(world, pos, state);
    }

    private boolean tryCreate(ServerWorld world, BlockPos pos, BlockState state) {
        if (!this.isMature(state))
            return false;

        if (TardisServerWorld.isTardisDimension(world)) {
            this.createConsole(world, pos);
            return true;
        }

        if (world.getBlockEntity(pos) instanceof CoralBlockEntity coral)
            this.createTardis(world, pos, coral.creator, state);

        return true;
    }

    private void createConsole(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos, AITSounds.FABRICATOR_END, SoundCategory.BLOCKS);

        world.setBlockState(pos, AITBlocks.CONSOLE.getDefaultState());
    }

    private void createTardis(ServerWorld world, BlockPos pos, UUID creatorId, BlockState state) {
        if (!(world.getPlayerByUuid(creatorId) instanceof ServerPlayerEntity player))
            return;

        TardisBuilder builder = new TardisBuilder().at(CachedDirectedGlobalPos.create(world, pos,
                        CachedDirectedGlobalPos.getGeneralizedRotation(state.get(FACING))))
                .owner(player)
                .<FuelHandler>with(TardisComponent.Id.FUEL, fuel -> fuel.setCurrentFuel(5000))
                .<LoyaltyHandler>with(TardisComponent.Id.LOYALTY, loyaltyHandler -> loyaltyHandler.set(player, new Loyalty(Loyalty.Type.NEUTRAL)))
                .with(TardisComponent.Id.TRAVEL, travel -> travel.tardis().travel().autopilot(false))
                .exterior(ExteriorVariantRegistry.getInstance().get(CoralGrowthVariant.REFERENCE))
                .desktop(DesktopRegistry.DEFAULT_CAVE);

        ServerTardis created = ServerTardisManager.getInstance()
                .create(builder);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!(placer instanceof ServerPlayerEntity player))
            return;

        if (!RiftChunkManager.isRiftChunk((ServerWorld) world, pos) && !TardisServerWorld.isTardisDimension((ServerWorld) world)) {
            world.breakBlock(pos, !placer.isPlayer() || !player.isCreative());
            return;
        }

        if (!(world.getBlockState(pos.down()).getBlock() instanceof SoulSandBlock)) {
            world.breakBlock(pos, !placer.isPlayer() || !player.isCreative());
            return;
        }

        if (world.getBlockEntity(pos) instanceof CoralBlockEntity coral) {
            if (player.getUuid() != null) {
                coral.creator = player.getUuid();
                coral.markDirty();
            }
            TardisCriterions.PLACE_CORAL.trigger(player);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return (world.getBaseLightLevel(pos, 0) >= 4 || world.isSkyVisible(pos)) && super.canPlaceAt(state, world, pos);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof RavagerEntity && world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            world.breakBlock(pos, true, entity);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return DEFAULT;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return DEFAULT;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return AITBlocks.CORAL_PLANT.asItem().getDefaultStack();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE).add(FACING);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);

        addShiftHiddenTooltip(stack, tooltip, tooltips -> {
            tooltip.add(Text.translatable("tooltip.ait.tardis_coral").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        });
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CoralBlockEntity(pos, state);
    }
}

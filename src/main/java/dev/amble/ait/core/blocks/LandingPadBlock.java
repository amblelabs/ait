package dev.amble.ait.core.blocks;

import dev.amble.ait.client.screens.LandingPadScreen;
import dev.amble.ait.core.world.LandingPadManager;
import dev.amble.ait.data.landing.LandingPadRegion;
import dev.amble.ait.data.landing.LandingPadSpot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LandingPadBlock extends Block {
    private static final BooleanProperty ACTIVE = BooleanProperty.of("active"); // whether this block created a region

    public LandingPadBlock(FabricBlockSettings settings) {
        super(settings);

        this.setDefaultState(
                this.getStateManager().getDefaultState().with(ACTIVE, false)
        );
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);

        builder.add(ACTIVE);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        Vec3d centre = pos.up().toCenterPos();
        world.addParticle(ParticleTypes.GLOW, centre.getX(), centre.getY() - 0.5, centre.getZ(), 0.0, 0.0, 0.0);

        // I hate this its so annoying </3
        //if (random.nextDouble() < 0.2f)
        //    world.playSound(centre.getX(), centre.getY(), centre.getZ(), SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.BLOCKS, 0.1f, 1f, true);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            openScreen(pos);
            return super.onUse(state, world, pos, player, hand, hit);
        }

        return ActionResult.CONSUME;
    }

    @Environment(EnvType.CLIENT)
    private static void openScreen(BlockPos pos) {
        MinecraftClient.getInstance().setScreen(new LandingPadScreen(pos));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!(world instanceof ServerWorld serverWorld))
            return;

        LandingPadManager manager = LandingPadManager.getInstance(serverWorld);

        if (manager.getRegionAt(pos) != null) {
            world.breakBlock(pos, true);
            return;
        }

        world.setBlockState(pos, state.with(ACTIVE, true));
        manager.claim(pos);

        LandingPadRegion region = LandingPadManager.getInstance((ServerWorld) world).getRegionAt(pos);
        if (region != null) {
            for(LandingPadSpot spot : region.getSpots()) {
                spot.setPos(new BlockPos(spot.getPos().getX(), world.getChunk(ChunkSectionPos.getSectionCoord(spot.getPos().getX()), ChunkSectionPos.getSectionCoord(spot.getPos().getZ()))
                        .sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, spot.getPos().getX() & 15, spot.getPos().getZ() & 15) + 1, spot.getPos().getZ()));
                LandingPadManager.Network.syncTracked(LandingPadManager.Network.Action.ADD, (ServerWorld) world, new ChunkPos(pos));
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);

        if (!(world instanceof ServerWorld serverWorld))
            return;

        if (!state.get(ACTIVE)) return;

        LandingPadManager.getInstance(serverWorld).releaseAt(pos);
    }
}

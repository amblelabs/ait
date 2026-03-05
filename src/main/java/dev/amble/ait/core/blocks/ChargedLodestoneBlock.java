package dev.amble.ait.core.blocks;

import dev.amble.ait.core.AITEntityTypes;
import dev.amble.ait.core.entities.RiftEntity;
import dev.amble.ait.core.world.RiftChunkManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

/**
 * @author Loqor
 * The purpose of this block is to rip open rifts in rift chunks as opposed to the silly entities.
 * It mimics the Lodestone block from Doctor Who lore as opposed to the usual Lodestone in Minecraft.
 * */
public class ChargedLodestoneBlock extends Block {

    // 10 seconds = 200 ticks. We tick every 2 ticks, so 100 steps.
    private static final int TOTAL_STEPS = 30;
    private static final int TICK_INTERVAL = 2;

    public static final IntProperty CHARGE_TICK = IntProperty.of("charge_tick", 0, TOTAL_STEPS);

    private static final Vector3f COLOR_FROM = new Vector3f(1.0f, 0.85f, 0.0f); // bright gold
    private static final Vector3f COLOR_TO = new Vector3f(1.0f, 1.0f, 0.6f);    // pale yellow

    public ChargedLodestoneBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(CHARGE_TICK, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CHARGE_TICK);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        RiftChunkManager manager = RiftChunkManager.getInstance(serverWorld);
        ChunkPos chunkPos = new ChunkPos(pos);

        if (!isConsumable(manager, chunkPos)) {
            if (placer != null) {
                placer.sendMessage(Text.translatable("message.ait.riftscanner.info3"));
            }
            world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE,
                    SoundCategory.BLOCKS, 1, 0.5f);
            return;
        }

        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE,
                SoundCategory.BLOCKS, 1, 1.5f);

        world.setBlockState(pos, state.with(CHARGE_TICK, 1));
        world.scheduleBlockTick(pos, this, TICK_INTERVAL);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int step = state.get(CHARGE_TICK);

        if (step <= 0 || step > TOTAL_STEPS) return;

        ChunkPos chunkPos = new ChunkPos(pos);
        Chunk chunk = world.getChunk(pos);

        int centerX = chunkPos.getStartX() + 8;
        int centerZ = chunkPos.getStartZ() + 8;

        int topY = chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                centerX & 15, centerZ & 15);
        double targetY = topY + 12;

        double startX = pos.getX() + 0.5;
        double startY = pos.getY() + 1.0;
        double startZ = pos.getZ() + 0.5;

        double endX = centerX + 0.5;
        double endZ = centerZ + 0.5;

        double progress = (double) step / TOTAL_STEPS;

        spawnBeamParticles(world, startX, startY, startZ, endX, targetY, endZ, progress, step);

        if (step == TOTAL_STEPS) {
            // Animation complete — spawn the rift
            RiftEntity riftEntity = AITEntityTypes.RIFT_ENTITY.create(world);

            if (riftEntity != null) {
                riftEntity.refreshPositionAndAngles(endX, targetY, endZ, 0, -90);
                world.spawnEntity(riftEntity);

                // Set back to a regular lodestone
                world.setBlockState(pos, Blocks.LODESTONE.getDefaultState());
            }

            world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(),
                    SoundCategory.BLOCKS, 1.5f, 0.5f);

            // Reset block state
            world.setBlockState(pos, state.with(CHARGE_TICK, 0));
            return;
        }

        // Advance to the next step
        world.setBlockState(pos, state.with(CHARGE_TICK, step + 1));
        world.scheduleBlockTick(pos, this, TICK_INTERVAL);
    }

    private void spawnBeamParticles(ServerWorld world, double startX, double startY, double startZ,
                                    double endX, double endY, double endZ,
                                    double progress, int step) {
        // Lerp the current "head" of the beam
        double headX = startX + (endX - startX) * progress;
        double headY = startY + (endY - startY) * progress;
        double headZ = startZ + (endZ - startZ) * progress;

        // Pulse scale: sin wave makes the beam thickness breathe
        double pulse = 0.8 + 0.4 * Math.sin(step * 0.3);
        float particleSize = (float) (1.5 * pulse);

        DustColorTransitionParticleEffect particle = new DustColorTransitionParticleEffect(
                COLOR_FROM, COLOR_TO, particleSize
        );

        double totalDist = Math.sqrt(
                (headX - startX) * (headX - startX) +
                        (headY - startY) * (headY - startY) +
                        (headZ - startZ) * (headZ - startZ)
        );

        int particleCount = Math.max(1, (int) (totalDist / 0.5));

        for (int i = 0; i <= particleCount; i++) {
            double t = (double) i / particleCount;

            double px = startX + (headX - startX) * t;
            double py = startY + (headY - startY) * t;
            double pz = startZ + (headZ - startZ) * t;

            double spread = 0.15 * pulse;

            world.spawnParticles(particle, px, py, pz,
                    3,
                    spread,
                    spread,
                    spread,
                    0.0
            );
        }

        // Extra bright burst at the head of the beam
        DustColorTransitionParticleEffect headParticle = new DustColorTransitionParticleEffect(
                COLOR_TO, COLOR_FROM, particleSize * 1.3f
        );

        world.spawnParticles(headParticle, headX, headY, headZ,
                8, 0.2, 0.2, 0.2, 0.0);
    }

    private static boolean isConsumable(RiftChunkManager manager, ChunkPos pos) {
        return manager.isRiftChunk(pos)/* && manager.getArtron(pos) >= 250*/;
    }
}
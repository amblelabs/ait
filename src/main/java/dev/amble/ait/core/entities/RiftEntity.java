package dev.amble.ait.core.entities;

import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.*;
import dev.amble.ait.core.advancement.TardisCriterions;
import dev.amble.ait.core.entities.base.DummyAmbientEntity;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.util.StackUtil;
import dev.amble.ait.core.util.TagsUtil;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.module.planet.core.util.ISpaceImmune;
import dev.amble.lib.util.TeleportUtil;

public class RiftEntity extends DummyAmbientEntity implements ISpaceImmune {
    private int interactAmount = 0;
    private int ambientSoundCooldown = 0;
    private int currentSoundIndex = 0;

    private static final SoundEvent[] RIFT_SOUNDS = {
            AITSounds.DRUMS,
    };

    @Override
    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        return super.canSpawn(world, spawnReason);
    }

    private static final int[] RIFT_DURATIONS = {
            20,
    };

    public RiftEntity(EntityType<RiftEntity> type, World world) {
        super(type, world);
    }

    public RiftEntity(World world) {
        this(AITEntityTypes.RIFT_ENTITY, world);
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (player.getBoundingBox().intersects(this.getBoundingBox().shrink(0.5f, 0.5f, 0.5f))) {
            if (WorldUtil.getTimeVortex() == null) return;
            TeleportUtil.teleport(player, WorldUtil.getTimeVortex(), player.getPos(), player.bodyYaw);
        }
    }

    @Override
    public final ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.getWorld().isClient()) return ActionResult.SUCCESS;

        ItemStack stack = player.getStackInHand(hand);

        if (stack.getItem() instanceof SonicItem sonic) {
            double transfer = 1000;
            double available = Math.max(sonic.getMaxFuel(stack) - sonic.getCurrentFuel(stack), 0);

            if (available < transfer)
                return ActionResult.FAIL;

            double remainder = sonic.addFuel(transfer, stack);

            if (remainder > 0)
                return ActionResult.FAIL;

            this.getWorld().playSound(null, this.getBlockPos(), AITSounds.RIFT_SONIC, SoundCategory.AMBIENT, 1f, 1f);
            StackUtil.spawn(this.getWorld(), this.getBlockPos(), new ItemStack(AITItems.CORAL_FRAGMENT));
            this.discard();
            return ActionResult.SUCCESS;

        }
        interactAmount += 1;

        if (interactAmount == 1) {
            TardisCriterions.FIRST_RIFT.trigger((ServerPlayerEntity) player);
        }

        if (interactAmount >= 3) {
            boolean gotFragment = this.getWorld().getRandom().nextBoolean();

            player.damage(this.getWorld().getDamageSources().hotFloor(), 7);
            if (gotFragment) {

                Item randomItem = TagsUtil.getRandomItemFromTag(
                        this.getWorld(),
                        AITTags.Items.RIFT_SUCCESS_EXTRA_ITEM
                );

                // Since we don't really wanna have to use 3 billion charged zeiton crystals, just spawn more coral fragments. - Loqor
                ItemStack coralFragments = new ItemStack(AITItems.CORAL_FRAGMENT);

                coralFragments.setCount(this.getWorld().random.nextBetween(3, 8));
                StackUtil.spawn(this.getWorld(), this.getBlockPos(), coralFragments);

                StackUtil.spawn(this.getWorld(), this.getBlockPos(), new ItemStack(randomItem));
                this.getWorld().playSound(null, player.getBlockPos(), AITSounds.RIFT_SUCCESS, SoundCategory.AMBIENT, 1f, 1f);
            } else {
                Item randomItem = TagsUtil.getRandomItemFromTag(
                        this.getWorld(),
                        AITTags.Items.RIFT_FAIL_ITEM
                );

                StackUtil.spawn(this.getWorld(), this.getBlockPos(), new ItemStack(randomItem));
                this.getWorld().playSound(null, this.getBlockPos(), AITSounds.RIFT_FAIL, SoundCategory.AMBIENT, 1f, 1f);
                spreadTardisCoral(this.getWorld(), this.getBlockPos());
            }

            this.discard();

            return gotFragment ? ActionResult.SUCCESS : ActionResult.FAIL;
        }

        return ActionResult.CONSUME;
    }

    private void spreadTardisCoral(World world, BlockPos pos) {
        int radius = 4;

        for (BlockPos targetPos : BlockPos.iterate(pos.add(-radius, 0, -radius), pos.add(radius, 0, radius))) {
            if (world.random.nextBetween(0, 10) < 3) { // 30% chance per block
                Chunk targetChunk = getLoadedChunk(world, targetPos);
                if (targetChunk == null)
                    continue;

                targetPos = targetPos.withY(targetChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                        targetPos.getX() & 15, targetPos.getZ() & 15));

                BlockState currentState = world.getBlockState(targetPos);
                BlockState newState = getReplacementBlock(currentState);
                if (newState != null) {
                    world.setBlockState(targetPos, newState, Block.NOTIFY_ALL);

                    world.addParticle(AITMod.CORAL_PARTICLE,
                            targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5,
                            0, 0, 0);

                    if (newState.isOf(AITBlocks.TARDIS_CORAL_BLOCK)) {
                        placeCoralFans(world, targetPos);
                    }
                }
            }
        }
    }

    private BlockState getReplacementBlock(BlockState currentState) {
        Block block = currentState.getBlock();

        if (block instanceof SlabBlock) {
            BlockState replacement = AITBlocks.TARDIS_CORAL_SLAB.getDefaultState()
                    .with(Properties.SLAB_TYPE, currentState.get(Properties.SLAB_TYPE));
            return copyWaterlogged(currentState, replacement);
        }

        if (block instanceof StairsBlock) {
            BlockState replacement = AITBlocks.TARDIS_CORAL_STAIRS.getDefaultState()
                    .with(Properties.HORIZONTAL_FACING, currentState.get(Properties.HORIZONTAL_FACING))
                    .with(Properties.BLOCK_HALF, currentState.get(Properties.BLOCK_HALF))
                    .with(Properties.STAIR_SHAPE, currentState.get(Properties.STAIR_SHAPE));
            return copyWaterlogged(currentState, replacement);
        }


        if (canTransform(block)) return AITBlocks.TARDIS_CORAL_BLOCK.getDefaultState();

        return null;
    }

    private BlockState copyWaterlogged(BlockState currentState, BlockState replacement) {
        if (currentState.contains(Properties.WATERLOGGED) && replacement.contains(Properties.WATERLOGGED))
            return replacement.with(Properties.WATERLOGGED, currentState.get(Properties.WATERLOGGED));

        return replacement;
    }

    private boolean canTransform(Block block) {
        return block == Blocks.STONE || block == Blocks.DIRT || block == Blocks.GRASS_BLOCK ||
                block == Blocks.SAND || block == Blocks.DEEPSLATE;
    }

    private void placeCoralFans(World world, BlockPos pos) {
        if (!isCoralBlock(world.getBlockState(pos)))
            return;

        for (Direction dir : Direction.values()) {
            BlockPos adjacent = pos.offset(dir);
            if (getLoadedChunk(world, adjacent) == null)
                continue;

            if (world.getBlockState(adjacent).isAir()) {
                world.setBlockState(adjacent, AITBlocks.TARDIS_CORAL_FAN.getDefaultState()
                        .with(Properties.WATERLOGGED,false)
                        .with(Properties.FACING, dir), Block.NOTIFY_ALL);
            }
        }
    }

    private Chunk getLoadedChunk(World world, BlockPos pos) {
        return world.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
    }

    private boolean isCoralBlock(BlockState state) {
        return state.isOf(AITBlocks.TARDIS_CORAL_BLOCK);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient()) {
            if (ambientSoundCooldown > 0) {
                ambientSoundCooldown--;
            } else {
                this.getWorld().playSound(null, this.getBlockPos(), RIFT_SOUNDS[currentSoundIndex], SoundCategory.AMBIENT, 0.7f, 1.0f);
                ambientSoundCooldown = RIFT_DURATIONS[currentSoundIndex];
                currentSoundIndex = (currentSoundIndex + 1) % RIFT_SOUNDS.length;
            }
        }
    }
}

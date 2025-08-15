package dev.amble.ait.core.entities;


import dev.amble.lib.util.TeleportUtil;

import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.*;
import dev.amble.ait.core.entities.base.DummyAmbientEntity;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.util.StackUtil;
import dev.amble.ait.core.util.TagsUtil;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.core.world.RiftChunkManager;
import dev.amble.ait.module.planet.core.util.ISpaceImmune;

public class RiftEntity extends DummyAmbientEntity implements ISpaceImmune {
    private int interactAmount = 0;
    private int ambientSoundCooldown = 0;
    private int currentSoundIndex = 0;

    private static final SoundEvent[] RIFT_SOUNDS = {
            AITSounds.RIFT1_AMBIENT,
            AITSounds.RIFT2_AMBIENT,
            AITSounds.RIFT3_AMBIENT
    };

    private static final int[] RIFT_DURATIONS = {
            15 * 20,
            13 * 20,
            14 * 20
    };

    public RiftEntity(EntityType<?> type, World world) {
        super(AITEntityTypes.RIFT_ENTITY, world);
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
            if (!this.getWorld().isClient()) {
                sonic.addFuel(1000, stack);
                this.getWorld().playSound(null, this.getBlockPos(), AITSounds.RIFT_SONIC, SoundCategory.AMBIENT, 1f, 1f);
                StackUtil.spawn(this.getWorld(), this.getBlockPos(), new ItemStack(AITItems.CORAL_FRAGMENT));
                this.discard();
            }
            return ActionResult.SUCCESS;

        }
        interactAmount += 1;

        if (interactAmount >= 3) {
            boolean gotFragment = this.getWorld().getRandom().nextBoolean();

            player.damage(this.getWorld().getDamageSources().hotFloor(), 7);
            if (gotFragment) {

                Item randomItem = TagsUtil.getRandomItemFromTag(
                        this.getWorld(),
                        AITTags.Items.RIFT_SUCCESS_EXTRA_ITEM
                );

                StackUtil.spawn(this.getWorld(), this.getBlockPos(), new ItemStack(AITItems.CORAL_FRAGMENT));
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

        Chunk chunk = world.getChunk(pos);
        for (BlockPos targetPos : BlockPos.iterate(pos.add(-radius, 0, -radius), pos.add(radius, 0, radius))) {
            if (world.random.nextBetween(0, 10) < 3) { // 30% chance per block
                targetPos = targetPos.withY(chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
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

        if (block instanceof SlabBlock) return AITBlocks.TARDIS_CORAL_SLAB.getDefaultState()
                .with(Properties.SLAB_TYPE, currentState.get(Properties.SLAB_TYPE));

        if (block instanceof StairsBlock) return AITBlocks.TARDIS_CORAL_STAIRS.getDefaultState()
                .with(Properties.HORIZONTAL_FACING, currentState.get(Properties.HORIZONTAL_FACING))
                .with(Properties.SLAB_TYPE, currentState.get(Properties.SLAB_TYPE))
                .with(Properties.STAIR_SHAPE, currentState.get(Properties.STAIR_SHAPE));


        if (canTransform(block)) return AITBlocks.TARDIS_CORAL_BLOCK.getDefaultState();

        return null;
    }

    private boolean canTransform(Block block) {
        return block == Blocks.STONE || block == Blocks.DIRT || block == Blocks.GRASS_BLOCK ||
                block == Blocks.SAND || block == Blocks.DEEPSLATE;
    }

    private void placeCoralFans(World world, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos adjacent = pos.offset(dir);
            if (world.getBlockState(adjacent).isAir() && isCoralBlock(world.getBlockState(pos))) {
                world.setBlockState(adjacent, AITBlocks.TARDIS_CORAL_FAN.getDefaultState()
                        .with(Properties.WATERLOGGED,false)
                        .with(Properties.FACING, dir), Block.NOTIFY_ALL);
            }
        }
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
                this.getWorld().playSound(null, this.getBlockPos(), RIFT_SOUNDS[currentSoundIndex], SoundCategory.AMBIENT, 1.0f, 1.0f);
                ambientSoundCooldown = RIFT_DURATIONS[currentSoundIndex];
                currentSoundIndex = (currentSoundIndex + 1) % RIFT_SOUNDS.length;
            }
        }
    }

    public static boolean canSpawn(EntityType<RiftEntity> rift,
                                   ServerWorldAccess serverWorldAccess, SpawnReason spawnReason,
                                   BlockPos pos, net.minecraft.util.math.random.Random random) {
        if (!(serverWorldAccess instanceof StructureWorldAccess worldAccess))
            return false;

        if (!WorldUtil.canRiftsSpawn(serverWorldAccess.toServerWorld()))
            return false;

        boolean canSpawn = spawnReason == SpawnReason.STRUCTURE || (random.nextBoolean()
                && RiftChunkManager.isRiftChunk(worldAccess, pos));

        if (!canSpawn)
            return false;

        Chunk chunk = worldAccess.getChunk(pos);
        ChunkPos chunkPos = chunk.getPos();
        BlockPos startPos = new BlockPos(chunkPos.getStartX(), chunk.getBottomY(), chunkPos.getStartZ());
        BlockPos endPos = new BlockPos(chunkPos.getEndX(), worldAccess.getHeight(), chunkPos.getEndZ());
        Box box = new Box(startPos, endPos);

        if (serverWorldAccess.getEntitiesByType(rift, box, predicate -> true).isEmpty())
            return worldAccess.getBlockState(pos).isAir() && worldAccess.getBlockState(pos.down()).isAir();

        return false;
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        float g = packet.getYaw();
        float h = packet.getPitch();
        this.updateTrackedPosition(d, e, f);
        this.bodyYaw = packet.getHeadYaw();
        this.headYaw = packet.getHeadYaw();
        this.prevBodyYaw = this.bodyYaw;
        this.prevHeadYaw = this.headYaw;
        this.setId(packet.getId());
        this.setUuid(packet.getUuid());
        this.updatePositionAndAngles(d, e, f, g, h);
        this.setVelocity(packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityZ());
        this.updatePosition(d, e, f);
    }
}

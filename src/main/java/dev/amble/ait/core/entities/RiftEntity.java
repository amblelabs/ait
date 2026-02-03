package dev.amble.ait.core.entities;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import dev.amble.ait.core.advancement.TardisCriterions;
import dev.amble.lib.util.TeleportUtil;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.*;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.util.StackUtil;
import dev.amble.ait.core.util.TagsUtil;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.module.planet.core.util.ISpaceImmune;

public class RiftEntity extends AbstractDecorationEntity implements ISpaceImmune {
    private static final int WIDTH = 48;
    private static final int HEIGHT = 48;

    // Tracked data for scale (growth/shrink animation)
    private static final TrackedData<Float> SCALE = DataTracker.registerData(RiftEntity.class, TrackedDataHandlerRegistry.FLOAT);

    // Lifespan in ticks (default 5 minutes = 6000 ticks)
    private static final int DEFAULT_LIFESPAN = 5 * 60 * 20;
    // Growth duration in ticks (2 seconds)
    private static final int GROWTH_DURATION = 2 * 20;
    // Shrink start time (when remaining life is this many ticks, start shrinking)
    private static final int SHRINK_DURATION = 2 * 20;

    private int interactAmount = 0;
    private int ambientSoundCooldown = 0;
    private int currentSoundIndex = 0;
    private int remainingLife = DEFAULT_LIFESPAN;
    private int age = 0;

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

    public RiftEntity(EntityType<? extends RiftEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SCALE, 0.0f);
    }

    public float getScale() {
        return this.dataTracker.get(SCALE);
    }

    public void setScale(float scale) {
        this.dataTracker.set(SCALE, Math.max(0.0f, Math.min(1.0f, scale)));
    }

    /**
     * Opens a rift at the given position facing the given direction.
     * Returns the created rift entity if successful, empty otherwise.
     */
    public static Optional<RiftEntity> openRift(World world, BlockPos pos, Direction facing) {
        RiftEntity riftEntity = AITEntityTypes.RIFT_ENTITY.create(world);

        if (riftEntity == null) return Optional.empty();

        riftEntity.setPosition(pos.getX(), pos.getY(), pos.getZ());
        riftEntity.setFacing(facing);

        if (riftEntity.canStayAttached()) {
            return Optional.of(riftEntity);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (player.getBoundingBox().intersects(this.getBoundingBox().shrink(0.5f, 0.5f, 0.5f))) {
            if (WorldUtil.getTimeVortex() == null) return;
            TeleportUtil.teleport(player, WorldUtil.getTimeVortex(), player.getPos(), player.bodyYaw);
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (this.getWorld().isClient()) return ActionResult.SUCCESS;

        ItemStack stack = player.getStackInHand(hand);

        if (stack.getItem() instanceof SonicItem sonic) {
            sonic.addFuel(1000, stack);
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
            // Increment age
            this.age++;

            // Handle growth animation (scale increases from 0 to 1 over GROWTH_DURATION)
            if (this.age <= GROWTH_DURATION) {
                float growthProgress = (float) this.age / GROWTH_DURATION;
                this.setScale(growthProgress);
            }
            // Handle shrink animation (scale decreases from 1 to 0 over SHRINK_DURATION)
            else if (this.remainingLife <= SHRINK_DURATION) {
                float shrinkProgress = (float) this.remainingLife / SHRINK_DURATION;
                this.setScale(shrinkProgress);
            } else {
                // Fully grown, ensure scale is 1
                if (this.getScale() < 1.0f) {
                    this.setScale(1.0f);
                }
            }

            // Handle lifespan
            this.remainingLife--;
            if (this.remainingLife <= 0) {
                this.discard();
                return;
            }

            // Ambient sounds
            if (ambientSoundCooldown > 0) {
                ambientSoundCooldown--;
            } else {
                this.getWorld().playSound(null, this.getBlockPos(), RIFT_SOUNDS[currentSoundIndex], SoundCategory.AMBIENT, 1.0f, 1.0f);
                ambientSoundCooldown = RIFT_DURATIONS[currentSoundIndex];
                currentSoundIndex = (currentSoundIndex + 1) % RIFT_SOUNDS.length;
            }
        }
    }

    // AbstractDecorationEntity methods

    @Override
    public int getWidthPixels() {
        return WIDTH;
    }

    @Override
    public int getHeightPixels() {
        return HEIGHT;
    }

    @Override
    public void onBreak(@Nullable Entity entity) {
        // Rifts don't drop items when broken
        this.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 1.0f, 1.0f);
    }

    @Override
    public void onPlace() {
        this.playSound(AITSounds.RIFT1_AMBIENT, 1.0f, 1.0f);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putByte("facing", (byte) this.facing.getHorizontal());
        nbt.putInt("remainingLife", this.remainingLife);
        nbt.putInt("age", this.age);
        nbt.putFloat("scale", this.getScale());
        nbt.putInt("interactAmount", this.interactAmount);
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.facing = Direction.fromHorizontal(nbt.getByte("facing"));
        this.remainingLife = nbt.getInt("remainingLife");
        this.age = nbt.getInt("age");
        this.setScale(nbt.getFloat("scale"));
        this.interactAmount = nbt.getInt("interactAmount");
        super.readCustomDataFromNbt(nbt);
        this.setFacing(this.facing);
    }

    @Override
    public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        this.setPosition(x, y, z);
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.setPosition(x, y, z);
    }

    @Override
    public Vec3d getSyncedPos() {
        return Vec3d.of(this.attachmentPos);
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, this.facing.getId(), this.getDecorationBlockPos());
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.setFacing(Direction.byId(packet.getEntityData()));
    }

    public int getRemainingLife() {
        return this.remainingLife;
    }

    public int getAge() {
        return this.age;
    }
}

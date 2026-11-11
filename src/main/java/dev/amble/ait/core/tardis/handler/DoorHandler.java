package dev.amble.ait.core.tardis.handler;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.core.AITDimensions;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.amble.ait.core.blocks.DoorBlock;
import dev.amble.ait.core.blocks.ExteriorBlock;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.amble.ait.data.properties.integer.IntProperty;
import dev.amble.ait.data.properties.integer.IntValue;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedBlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.TriState;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;

public class DoorHandler extends KeyedTardisComponent implements TardisTickable {
    private static final long KNOCK_RESET_TIME_MS = 2500;
    private static final int KNOCKS_BEFORE_QUIET = 8;
    private static final float KNOCK_VOLUME_FULL = 1f;
    private static final float KNOCK_VOLUME_QUIET = 0.3f;

    private int knockCount = 0;
    private long lastKnockTime = 0;

    private static final BoolProperty LOCKED_DOORS = new BoolProperty("locked");
    private static final BoolProperty PREVIOUSLY_LOCKED = new BoolProperty("previously_locked");
    private static final BoolProperty DEADLOCKED = new BoolProperty("deadlocked");

    private static final Property<DoorState> DOOR_STATE = Property.forEnum("door_state", DoorState.class, DoorState.CLOSED);
    private static final Property<AnimationDoorState> ANIMATION_STATE = Property.forEnum("animation_state", AnimationDoorState.class, AnimationDoorState.CLOSED);

    public static final int OPEN_TICKS = 14;
    public static final int CLOSE_TICKS = 17;

    private static final IntProperty LEFT_DOOR_TICKS = new IntProperty("left_door_ticks");
    private static final IntProperty RIGHT_DOOR_TICKS = new IntProperty("right_door_ticks");

    private final BoolValue locked = LOCKED_DOORS.create(this);
    private final BoolValue previouslyLocked = PREVIOUSLY_LOCKED.create(this);
    private final BoolValue deadlocked = DEADLOCKED.create(this);

    private final Value<DoorState> doorState = DOOR_STATE.create(this);

    private final IntValue leftTicks = LEFT_DOOR_TICKS.create(this);
    private final IntValue rightTicks = RIGHT_DOOR_TICKS.create(this);

    @Exclude
    @Nullable private ParticleEffect doorOpenParticles = null;

    @Exclude(strategy = Exclude.Strategy.FILE)
    public final Value<AnimationDoorState> animationState = ANIMATION_STATE.create(this);

    static {
        TardisEvents.DEMAT.register(tardis -> tardis.door().isOpen() ? TardisEvents.Interaction.FAIL : TardisEvents.Interaction.PASS);

        TardisEvents.EXTERIOR_CHANGE.register(tardis -> {
            if (!tardis.door().hasDoubleDoor() && tardis.door().getDoorState() == DoorState.BOTH)
                tardis.door().setDoorState(DoorState.HALF);
        });

        TardisEvents.MAT.register(tardis -> {
            tardis.door().setDeadlocked(true);
            return TardisEvents.Interaction.PASS;
        });

        TardisEvents.LANDED.register(tardis -> tardis.door().setDeadlocked(false));
    }

    public DoorHandler() {
        super(Id.DOOR);
    }

    @Override
    public void onLoaded() {
        locked.of(this, LOCKED_DOORS);
        previouslyLocked.of(this, PREVIOUSLY_LOCKED);
        deadlocked.of(this, DEADLOCKED);

        doorState.of(this, DOOR_STATE);
        animationState.of(this, ANIMATION_STATE);

        leftTicks.of(this, LEFT_DOOR_TICKS);
        rightTicks.of(this, RIGHT_DOOR_TICKS);
    }

    @Override
    public void tick(MinecraftServer server) {
        if (this.shouldSucc())
            this.succ();

        boolean wasClosed = this.isClosed();
        this.countDown();

        if (wasClosed != this.isClosed())
            TardisEvents.REAL_DOOR_CLOSE.invoker().onClose(tardis);

        this.tickDoorLight();

        if (this.doorOpenParticles != null && !this.tardis().crash().isNormal() && server.getTicks() % 5 == 0 && tardis.door().isOpen() && !tardis.cloak().silent().get()) {
            Vec3d exteriorPosition = TardisUtil.offsetPos(tardis.travel().position().toPos(), -0.15F);
            exteriorPosition = TardisUtil.offsetDoorPosition(exteriorPosition, tardis.travel().position().getRotation());

            ServerWorld exteriorWorld = tardis.travel().position().getWorld();

            exteriorWorld.spawnParticles(this.doorOpenParticles, exteriorPosition.getX(),
                    exteriorPosition.getY() + 0.1, exteriorPosition.getZ(), 25, 0.25D, 1.1,
                    0.25D, 0.025D);
        }

        if (System.currentTimeMillis() - lastKnockTime > KNOCK_RESET_TIME_MS && knockCount > 0) {
            knockCount = 0;
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void tick(MinecraftClient client) {
        if (client.isPaused())
            return;

        this.countDown();
    }

    private void countDown() {
        if (this.leftTicks.get() > 0)
            this.leftTicks.flatMap(ticks -> --ticks, false);

        if (this.rightTicks.get() > 0)
            this.rightTicks.flatMap(ticks -> --ticks, false);
    }

    private static int reverse(int remaining, boolean opening) {
        int from = opening ? CLOSE_TICKS : OPEN_TICKS;
        int to = opening ? OPEN_TICKS : CLOSE_TICKS;

        return Math.round(to - remaining * (float) to / from);
    }

    private void succ() {
        TardisUtil.getLivingEntitiesInInterior(tardis.asServer()).stream()
                .filter(entity -> !entity.isSpectator())
                .forEach(entity -> {
                    DirectedBlockPos directed = tardis.getDesktop().getDoorPos();
                    if (!(tardis.asServer().world().getBlockEntity(directed.getPos()) instanceof DoorBlockEntity))
                        return;

                    Vec3d pos = directed.getPos().toCenterPos().offset(directed.toMinecraftDirection(), 0.5f);

                    float suckValue = tardis.travel().position().getDimension().equals(AITDimensions.SPACE) ? 0.1f: 0.08f;
                    Vec3d motion = pos.subtract(entity.getPos()).normalize().multiply(suckValue);

                    entity.setVelocity(entity.getVelocity().add(motion));
                    entity.velocityDirty = true;
                    entity.velocityModified = true;
                });
    }

    private boolean shouldSucc() {
        DirectedBlockPos directed = tardis.getDesktop().getDoorPos();

        if (directed == null)
            return false;

        return this.isOpen() && !tardis.flight().isFlying() && !tardis.areShieldsActive()
            && ((!tardis.travel().isLanded() && !tardis.travel().autopilot())
                || tardis.travel().position().getDimension().equals(AITDimensions.SPACE));
    }

    public boolean isRightOpen() {
        return this.doorState.get() == DoorState.BOTH;
    }

    public boolean isLeftOpen() {
        return this.doorState.get() == DoorState.HALF || this.doorState.get() == DoorState.BOTH;
    }

    public void setDeadlocked(boolean deadlocked) {
        this.deadlocked.set(deadlocked);
    }

    public void setLocked(boolean locked) {
        this.locked.set(locked);

        if (locked)
            this.setDoorState(DoorState.CLOSED);
    }

    public boolean locked() {
        return this.locked.get() || this.deadlocked.get();
    }

    public boolean hasDoubleDoor() {
        return this.tardis.getExterior().getVariant().door().isDouble();
    }

    public boolean isOpen() {
        return !isClosed();
    }

    public boolean isClosed() {
        return this.leftTicks.get() <= 0 && this.rightTicks.get() <= 0 && this.doorState.get() == DoorState.CLOSED;
    }

    public boolean areBothOpen() {
        return this.doorState.get() == DoorState.BOTH;
    }

    public void openDoors() {
        this.setDoorState(DoorState.HALF);

        if (this.hasDoubleDoor())
            this.setDoorState(DoorState.BOTH);
    }

    private void tickDoorLight() {
        if (!(this.tardis instanceof ServerTardis tardis))
            return;

        boolean lit = !this.isClosed();

        CachedDirectedGlobalPos exterior = tardis.travel().position();
        ServerWorld exteriorWorld = exterior == null ? null : exterior.getWorld();
        BlockState exteriorState = null;

        if (exteriorWorld != null && isLoaded(exteriorWorld, exterior.getPos())) {
            BlockState state = exteriorWorld.getBlockState(exterior.getPos());

            if (state.getBlock() instanceof ExteriorBlock)
                exteriorState = state;
        }

        DirectedBlockPos door = tardis.getDesktop().getDoorPos();
        ServerWorld interior = tardis.hasWorld() ? tardis.world() : null;
        BlockState doorState = null;

        if (interior != null && door != null && isLoaded(interior, door.getPos())) {
            BlockState state = interior.getBlockState(door.getPos());

            if (state.getBlock() instanceof DoorBlock)
                doorState = state;
        }

        if (doorState != null) {
            int level = 0;

            if (lit && exteriorWorld != null && (exteriorState != null || tardis.flight().isFlying())) {
                int rotation = exteriorState != null ? exteriorState.get(ExteriorBlock.ROTATION) : exterior.getRotation();
                level = sampleLight(exteriorWorld, exterior.getPos(), RotationPropertyHelper.toDegrees(rotation) + 180f);
            }

            if (doorState.get(DoorBlock.LEVEL_4) != level)
                interior.setBlockState(door.getPos(), doorState.with(DoorBlock.LEVEL_4, level), Block.NOTIFY_ALL);
        }

        if (exteriorState != null && tardis.travel().getState() == TravelHandlerBase.State.LANDED) {
            int level = tardis.fuel().hasPower() ? 4 : 0;

            if (lit && doorState != null && !tardis.cloak().cloaked().get())
                level = Math.max(level, sampleLight(interior, door.getPos(), RotationPropertyHelper.toDegrees(door.getRotation())));

            if (exteriorState.get(ExteriorBlock.LEVEL_4) != level)
                exteriorWorld.setBlockState(exterior.getPos(), exteriorState.with(ExteriorBlock.LEVEL_4, level), Block.NOTIFY_ALL);
        }
    }

    private static boolean isLoaded(ServerWorld world, BlockPos pos) {
        return world.getChunkManager().isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()),
                ChunkSectionPos.getSectionCoord(pos.getZ()));
    }

    private static int sampleLight(ServerWorld world, BlockPos pos, float yaw) {
        float rad = yaw * MathHelper.RADIANS_PER_DEGREE;
        BlockPos front = pos.add(Math.round(-MathHelper.sin(rad)), 0, Math.round(MathHelper.cos(rad)));

        return Math.max(world.getLightLevel(front), world.getLightLevel(front.up()));
    }

    public void closeDoors() {
        this.setDoorState(DoorState.CLOSED);
    }

    private void setDoorState(DoorState newState) {
        if (this.locked() && newState != DoorState.CLOSED)
            return;

        DoorState oldState = this.doorState.get();

        if (oldState != newState) {
            this.animationState.set(AnimationDoorState.match(newState, oldState));

            boolean leftOpening = newState != DoorState.CLOSED;
            boolean rightOpening = newState == DoorState.BOTH;

            if (leftOpening == (oldState == DoorState.CLOSED))
                this.leftTicks.set(reverse(this.leftTicks.get(), leftOpening));

            if (rightOpening != (oldState == DoorState.BOTH))
                this.rightTicks.set(reverse(this.rightTicks.get(), rightOpening));

            if (oldState == DoorState.CLOSED)
                TardisEvents.DOOR_OPEN.invoker().onOpen(tardis());

            if (newState == DoorState.CLOSED)
                TardisEvents.DOOR_CLOSE.invoker().onClose(tardis());
        }

        this.doorState.set(newState);
    }

    public DoorState getDoorState() {
        return doorState.get();
    }

    public boolean interact(ServerWorld world, @Nullable BlockPos pos, @Nullable ServerPlayerEntity player) {
        return this.interactAllDoors(world, pos, player, false);
    }

    public boolean interactAllDoors(ServerWorld world, @Nullable BlockPos pos, @Nullable ServerPlayerEntity player, boolean both) {
        ServerWorld interior = tardis.asServer().hasWorld() ? tardis.asServer().world() : null;
        InteractionResult result = TardisEvents.USE_DOOR.invoker().onUseDoor(tardis, interior, world, player, pos);

        if (result == InteractionResult.KNOCK) {
            long now = System.currentTimeMillis();
            if (now - lastKnockTime > KNOCK_RESET_TIME_MS) {
                knockCount = 0;
            }
            knockCount++;
            lastKnockTime = now;
            float knockVolume = knockCount > KNOCKS_BEFORE_QUIET ? KNOCK_VOLUME_QUIET : KNOCK_VOLUME_FULL;

            if (pos != null && world != interior)
                world.playSound(null, pos, AITSounds.KNOCK, SoundCategory.BLOCKS, knockVolume,
                        world.getRandom().nextBoolean() ? 0.5f : 0.3f);

            if (interior != null)
                interior.playSound(null, tardis.getDesktop().getDoorPos().getPos(), AITSounds.KNOCK,
                        SoundCategory.BLOCKS, knockVolume, world.getRandom().nextBoolean() ? 0.5f : 0.3f);
        }

        if (result == InteractionResult.BANG) {
            if (pos != null && world != interior)
                world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS, 1f,
                        1f);

            if (interior != null)
                interior.playSound(null, tardis.getDesktop().getDoorPos().getPos(),
                        SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS);
        }

        if (result.returns != TriState.DEFAULT)
            return result.returns.get();

        if (this.locked()) {
            if (player != null && pos != null) {
                player.sendMessage(Text.literal("\uD83D\uDD12"), true);

                long now = System.currentTimeMillis();
                if (now - lastKnockTime > KNOCK_RESET_TIME_MS) {
                    knockCount = 0;
                }
                knockCount++;
                lastKnockTime = now;
                float knockVolume = knockCount > KNOCKS_BEFORE_QUIET ? KNOCK_VOLUME_QUIET : KNOCK_VOLUME_FULL;

                world.playSound(null, pos, AITSounds.KNOCK, SoundCategory.BLOCKS, knockVolume,
                        world.getRandom().nextBoolean() ? 0.5f : 0.3f);

                if (interior != null)
                    interior.playSound(null, tardis.getDesktop().getDoorPos().getPos(), AITSounds.KNOCK,
                            SoundCategory.BLOCKS, knockVolume, world.getRandom().nextBoolean() ? 0.5f : 0.3f);
            }

            return false;
        }

        DoorSchema doorSchema = tardis.getExterior().getVariant().door();
        SoundEvent sound = this.getDoorState().next(doorSchema.isDouble()) == DoorState.CLOSED
                ? doorSchema.closeSound()
                : doorSchema.openSound();

        tardis.travel().position().getWorld().playSound(null, tardis.travel().position().getPos(), sound,
                SoundCategory.BLOCKS, 0.2F, world.getRandom().nextBoolean() ? 1f : 0.8f);

        if (interior != null)
            interior.playSound(null, tardis.getDesktop().getDoorPos().getPos(), sound,
                    SoundCategory.BLOCKS, 0.2F, world.getRandom().nextBoolean() ? 1f : 0.8f);

        if (player.isSneaking() || both) {
            if (this.isOpen()) {
                this.closeDoors();
            } else {
                this.openDoors();
            }
        } else {
            this.setDoorState(this.getDoorState().next(doorSchema.isDouble()));
        }

        TardisEvents.DOOR_USED.invoker().onDoorUsed(tardis, player);
        return true;
    }

    public boolean interactToggleLock(@Nullable ServerPlayerEntity player) {
        return this.interactToggleLock(player, false);
    }

    public boolean interactToggleLock(@Nullable ServerPlayerEntity player, boolean forced) {
        return this.interactLock(!this.locked(), player, forced);
    }

    public boolean interactLock(boolean lock, @Nullable ServerPlayerEntity player, boolean forced) {
        if (this.locked() == lock)
            return true;

        if (!forced && (tardis.travel().getState() == TravelHandlerBase.State.DEMAT
                || tardis.travel().getState() == TravelHandlerBase.State.MAT))
            return false;

        this.setLocked(lock);

        DoorState prevState = this.getDoorState();
        this.setDoorState(DoorState.CLOSED);

        if (prevState != DoorState.CLOSED)
            TardisEvents.DOOR_USED.invoker().onDoorUsed(tardis, player);

        if (!forced)
            this.previouslyLocked().set(locked);

        String lockedState = tardis.door().locked() ? "\uD83D\uDD12" : "\uD83D\uDD13";

        if (player != null)
            player.sendMessage(Text.literal(lockedState), true);

        SoundEvent keySound = lock ? AITSounds.KEY_LOCK : AITSounds.KEY_UNLOCK;

        tardis.travel().position().getWorld().playSound(null, tardis.travel().position().getPos(),
                keySound, SoundCategory.BLOCKS, 0.6F, 1F);

        ServerWorld interior = tardis.asServer().hasWorld() ? tardis.asServer().world() : null;

        if (interior != null)
            interior.playSound(null, tardis.getDesktop().getDoorPos().getPos(),
                    keySound, SoundCategory.BLOCKS, 0.6F, 1F);

        return true;
    }

    public BoolValue previouslyLocked() {
        return this.previouslyLocked;
    }

    @Environment(EnvType.CLIENT)
    public boolean isVisuallyOpen() {
        if (!AITModClient.CONFIG.animateDoors)
            return this.isOpen();

        return this.isOpen() || this.leftTicks.get() > 0 || this.rightTicks.get() > 0;
    }

    public int getLeftTicks() {
        return this.leftTicks.get();
    }

    public int getRightTicks() {
        return this.rightTicks.get();
    }

    /**
     * Sets the particles which will spawn at the doors when they are opened
     * Server-side Only
     * @param particle particle to spawn
     * @see #tick(MinecraftServer)
     * @author duzo
     */
    public void setDoorParticles(ParticleEffect particle) {
        this.doorOpenParticles = particle;
        this.sync();
    }

    public enum InteractionResult {
        /**
         * Same as returning {@literal false} in {@link DoorHandler#interact(ServerWorld, BlockPos, ServerPlayerEntity)}
         */
        CANCEL(TriState.FALSE),
        /**
         * Continues the execution.
         */
        CONTINUE(TriState.DEFAULT),
        /**
         * Same as returning {@literal false} in {@link DoorHandler#interact(ServerWorld, BlockPos, ServerPlayerEntity)},
         * but also play knocking sound.
         */
        KNOCK(TriState.FALSE),
        /**
         * Same as returning {@literal false} in {@link DoorHandler#interact(ServerWorld, BlockPos, ServerPlayerEntity)},
         * but also play door banging sound.
         */
        BANG(TriState.FALSE),
        /**
         * Same as returning {@literal true} in {@link DoorHandler#interact(ServerWorld, BlockPos, ServerPlayerEntity)}
         */
        SUCCESS(TriState.TRUE);

        private final TriState returns;

        InteractionResult(TriState returns) {
            this.returns = returns;
        }

        public TriState result() {
            return returns;
        }
    }

    public enum DoorState {
        CLOSED,
        HALF,
        BOTH;

        public DoorState next(boolean isDouble) {
            return switch (this) {
                case CLOSED -> HALF;
                case HALF -> isDouble ? BOTH : CLOSED;
                case BOTH -> CLOSED;
            };
        }
    }

    public enum AnimationDoorState {
        CLOSED,
        FIRST,
        SECOND,
        BOTH;

        public boolean is(DoorState doorState) {
            if (this == CLOSED && doorState == DoorState.CLOSED)
                return true;

            if (this == FIRST && doorState == DoorState.HALF)
                return true;

            return (this == BOTH || this == SECOND) && doorState == DoorState.BOTH;
        }

        public static AnimationDoorState match(DoorState newState, DoorState oldState) {
            AnimationDoorState animState = null;

            if (oldState == DoorState.HALF && newState == DoorState.BOTH)
                animState = AnimationDoorState.SECOND;

            if (oldState == DoorState.CLOSED && newState == DoorState.BOTH)
                animState = AnimationDoorState.BOTH;

            if (oldState == DoorState.BOTH && newState == DoorState.CLOSED)
                animState = AnimationDoorState.CLOSED;

            if (oldState == DoorState.CLOSED && newState == DoorState.HALF)
                animState = AnimationDoorState.FIRST;

            if (animState == null) {
                animState = CLOSED;
            }

            return animState;
        }
    }
}

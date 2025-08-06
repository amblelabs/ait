package dev.amble.ait.core.entities;

import java.util.List;

import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITEntityTypes;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.entities.base.LinkableDummyEntity;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.item.control.ControlBlockItem;
import dev.amble.ait.core.item.sonic.SonicMode;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.control.ControlTypes;
import dev.amble.ait.data.schema.console.ConsoleTypeSchema;

public class ConsoleControlEntity extends LinkableDummyEntity {
    private static final TrackedData<Float> WIDTH = DataTracker.registerData(ConsoleControlEntity.class,
            TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> HEIGHT = DataTracker.registerData(ConsoleControlEntity.class,
            TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Vector3f> OFFSET = DataTracker.registerData(ConsoleControlEntity.class,
            TrackedDataHandlerRegistry.VECTOR3F);
    private static final TrackedData<Boolean> PART_OF_SEQUENCE = DataTracker.registerData(ConsoleControlEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> SEQUENCE_INDEX = DataTracker.registerData(ConsoleControlEntity.class,
            TrackedDataHandlerRegistry.INTEGER); // <--->
    private static final TrackedData<Integer> SEQUENCE_LENGTH = DataTracker.registerData(ConsoleControlEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> WAS_SEQUENCED = DataTracker.registerData(ConsoleControlEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ON_DELAY = DataTracker.registerData(ConsoleControlEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Float> DURABILITY = DataTracker.registerData(ConsoleControlEntity.class,
            TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> STICKY = DataTracker.registerData(ConsoleControlEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<BlockPos> CONSOLE_BLOCK_POS = DataTracker.registerData(ConsoleControlEntity.class,
            TrackedDataHandlerRegistry.BLOCK_POS);
    private Control control;
    private static final float MAX_DURABILITY = 1.0f;

    public ConsoleControlEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
    }

    private ConsoleControlEntity(World world, Tardis tardis) {
        this(AITEntityTypes.CONTROL_ENTITY_TYPE, world);
        this.link(tardis);
    }

    public static ConsoleControlEntity create(World world, Tardis tardis) {
        return new ConsoleControlEntity(world, tardis);
    }

    @Override
    public void onRemoved() {
        if (this.getConsoleBlockPos() == null) {
            super.onRemoved();
            return;
        }

        if (this.getWorld().getBlockEntity(this.getConsoleBlockPos()) instanceof ConsoleBlockEntity console)
            console.markNeedsControl();
    }

    @Override
    public void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(WIDTH, 0.125f);
        this.dataTracker.startTracking(HEIGHT, 0.125f);
        this.dataTracker.startTracking(OFFSET, new Vector3f(0));
        this.dataTracker.startTracking(PART_OF_SEQUENCE, false);
        this.dataTracker.startTracking(SEQUENCE_INDEX, 0);
        this.dataTracker.startTracking(SEQUENCE_LENGTH, 0);
        this.dataTracker.startTracking(WAS_SEQUENCED, false);
        this.dataTracker.startTracking(ON_DELAY, false);
        this.dataTracker.startTracking(DURABILITY, MAX_DURABILITY);
        this.dataTracker.startTracking(STICKY, false);
        this.dataTracker.startTracking(CONSOLE_BLOCK_POS, BlockPos.ORIGIN);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.put("console", NbtHelper.fromBlockPos(this.getConsoleBlockPos()));

        nbt.putFloat("width", this.getControlWidth());
        nbt.putFloat("height", this.getControlHeight());
        nbt.putFloat("offsetX", this.getOffset().x());
        nbt.putFloat("offsetY", this.getOffset().y());
        nbt.putFloat("offsetZ", this.getOffset().z());
        nbt.putBoolean("partOfSequence", this.isPartOfSequence());
        nbt.putInt("sequenceColor", this.getSequenceIndex());
        nbt.putBoolean("wasSequenced", this.wasSequenced());
        nbt.putFloat("durability", this.getDurability());
        nbt.putBoolean("sticky", this.isSticky());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        NbtCompound console = nbt.getCompound("console");

        if (nbt.contains("console")) {
            this.setConsolePos(NbtHelper.toBlockPos(console));
        }

        if (nbt.contains("width") && nbt.contains("height")) {
            this.setControlWidth(nbt.getFloat("width"));
            this.setControlHeight(nbt.getFloat("height"));
            this.calculateDimensions();
        }

        if (nbt.contains("offsetX") && nbt.contains("offsetY") && nbt.contains("offsetZ"))
            this.setOffset(new Vector3f(nbt.getFloat("offsetX"), nbt.getFloat("offsetY"), nbt.getFloat("offsetZ")));

        if (nbt.contains("partOfSequence"))
            this.setPartOfSequence(nbt.getBoolean("partOfSequence"));

        if (nbt.contains("sequenceColor"))
            this.setSequenceIndex(nbt.getInt("sequenceColor"));

        if (nbt.contains("wasSequenced"))
            this.setWasSequenced(nbt.getBoolean("wasSequenced"));

        if (nbt.contains("durability"))
            this.setDurability(nbt.getFloat("durability"));
        if (nbt.contains("sticky"))
            this.setSticky(nbt.getBoolean("sticky"));
    }

    public void setConsolePos(BlockPos consoleBlockPos) {
        this.dataTracker.set(CONSOLE_BLOCK_POS, consoleBlockPos);
    }

    @Override
    public void onDataTrackerUpdate(List<DataTracker.SerializedEntry<?>> dataEntries) {
        this.setScaleAndCalculate(this.getDataTracker().get(WIDTH), this.getDataTracker().get(HEIGHT));
    }

    @Override
    public boolean canHit() {
        return !isRemoved();
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getStackInHand(hand);

        if (player.getOffHandStack().isOf(Items.COMMAND_BLOCK)) {
            controlEditorHandler(player);
            return ActionResult.SUCCESS;
        }

        if (handStack.isOf(AITBlocks.REDSTONE_CONTROL_BLOCK.asItem()) && this.getControl() != null) {
            NbtCompound nbt = handStack.getOrCreateNbt();
            nbt.putString(ControlBlockItem.CONTROL_ID_KEY, this.getControl().id().toString());
            ConsoleBlockEntity consoleBlockEntity = this.getConsole();
            nbt.putString(ControlBlockItem.CONSOLE_TYPE_ID_KEY, consoleBlockEntity.getTypeSchema().id().toString());
            return ActionResult.SUCCESS;
        }

        if (hand == Hand.MAIN_HAND && !this.run(player, player.getWorld(), false))
            this.playFailFx();

        return ActionResult.SUCCESS;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.getSource() instanceof TntEntity)
            return false;

        if (source.getAttacker() instanceof PlayerEntity player) {
            if (source.getSource() instanceof ProjectileEntity)
                source.getSource().discard();

            if (player.getOffHandStack().isOf(Items.COMMAND_BLOCK))
                controlEditorHandler(player);

            else if (!this.run((PlayerEntity) source.getAttacker(), source.getAttacker().getWorld(), true))
                this.playFailFx();
        }

        return false;
    }

    private void playFailFx() {
        if (this.getWorld().isClient())
            return;

        ServerWorld world = (ServerWorld) this.getWorld();

        // spawn particle above the control
        world.spawnParticles(AITMod.CORAL_PARTICLE, this.getX(), this.getY() + 0.25, this.getZ(), 1, 0.05, 0.05, 0.05, 0.025);
        world.playSound(null, this.getBlockPos(), SoundEvents.ITEM_SHIELD_BREAK, SoundCategory.BLOCKS, 0.2F, AITMod.RANDOM.nextFloat(0.5F, 1.5F));
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        if (this.getDataTracker().containsKey(WIDTH) && this.getDataTracker().containsKey(HEIGHT))
            return EntityDimensions.changing(this.getControlWidth(), this.getControlHeight());

        return super.getDimensions(pose);
    }

    @Override
    public void tick() {
        if (this.getWorld().isClient())
            return;

        if (this.control == null && this.getConsoleBlockPos() != null)
            this.discard();

        switch (this.getDurabilityState(this.getDurability())) {
            case JAMMED, SPARKING -> this.spark();
            case CATCH_FIRE -> this.onFire();
        }
    }

    @Override
    public boolean shouldRenderName() {
        return true;
    }

    public float getControlWidth() {
        return this.dataTracker.get(WIDTH);
    }

    public float getControlHeight() {
        return this.dataTracker.get(HEIGHT);
    }

    public void setControlWidth(float width) {
        this.dataTracker.set(WIDTH, width);
    }

    public void setControlHeight(float height) {
        this.dataTracker.set(HEIGHT, height);
    }

    public Control getControl() {
        return control;
    }

    public Vector3f getOffset() {
        return this.dataTracker.get(OFFSET);
    }

    public void setOffset(Vector3f offset) {
        this.dataTracker.set(OFFSET, offset);
    }

    public int getSequenceIndex() {
        return this.dataTracker.get(SEQUENCE_INDEX);
    }

    public void setSequenceIndex(int i) {
        this.dataTracker.set(SEQUENCE_INDEX, i);
    }

    public int getSequenceLength() {
        return this.dataTracker.get(SEQUENCE_LENGTH);
    }

    public void setSequenceLength(int n) {
        this.dataTracker.set(SEQUENCE_LENGTH, n);
    }

    public float getSequencePercentage() {
        return (this.getSequenceIndex() + 1f) / this.getSequenceLength();
    }

    public boolean wasSequenced() {
        return this.dataTracker.get(WAS_SEQUENCED);
    }

    public void setWasSequenced(boolean sequenced) {
        this.dataTracker.set(WAS_SEQUENCED, sequenced);
    }

    public void setPartOfSequence(boolean partOfSequence) {
        this.dataTracker.set(PART_OF_SEQUENCE, partOfSequence);
    }

    public boolean isPartOfSequence() {
        return this.dataTracker.get(PART_OF_SEQUENCE);
    }

    public boolean isOnDelay() {
        return this.dataTracker.get(ON_DELAY);
    }

    public float getDurability() {
        return this.dataTracker.get(DURABILITY);
    }

    public boolean isSticky() {
        return this.dataTracker.get(STICKY);
    }

    public DurabilityStates getDurabilityState(float durability) {
        return DurabilityStates.get(durability);
    }

    public void setDurability(float durability) {
        this.dataTracker.set(DURABILITY, durability);
    }

    public void setSticky(boolean sticky) {
        this.dataTracker.set(STICKY, sticky);
    }

    public void addDurability(float durability) {
        this.setDurability(Math.min(durability, MAX_DURABILITY));
    }

    public void subtractDurability(float durability) {
        this.setDurability(Math.max(this.getDurability() - durability, 0));
    }

    public boolean run(PlayerEntity player, World world, boolean leftClick) {
        if (isSticky()) {
            if (player.getMainHandStack().isOf(Items.SHEARS)) {
                this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1, 1);
                this.dataTracker.set(STICKY, false);
                return true;
            }

            this.playSound(SoundEvents.BLOCK_SLIME_BLOCK_BREAK, 0.4f, 1);
            player.getWorld().addParticle(
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.SLIME_BLOCK.getDefaultState()),
                    this.getX(), this.getY(), this.getZ(),
                    0.2, 0.5, -0.1
            );

            return true;
        } else if (player.getMainHandStack().isOf(Items.SLIME_BALL)) {
            this.playSound(SoundEvents.BLOCK_SLIME_BLOCK_BREAK, 1, 1);
            this.dataTracker.set(STICKY, true);
            return true;
        }

        if (world.isClient())
            return false;

        if (player.getMainHandStack().isOf(AITItems.TARDIS_ITEM))
            this.discard();

        if (!this.isLinked()) {
            AITMod.LOGGER.warn("Discarding invalid control entity at {}; console pos: {}", this.getPos(),
                    this.getConsoleBlockPos());

            this.discard();
            return false;
        }

        Tardis tardis = this.tardis().get();

        if (player.getMainHandStack().isOf(AITItems.SONIC_SCREWDRIVER) && this.getDurability() < 1.0f
                && SonicItem.mode(player.getMainHandStack()) == SonicMode.Modes.TARDIS) {
            Vec3d pos = this.getPos();
            this.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            ((ServerWorld) this.getEntityWorld()).spawnParticles(ParticleTypes.WAX_ON,
                    pos.getX(), pos.getY(), pos.getZ(), 2, 0.2, 0.4, 0.2, 0.02);
            this.setDurability(MAX_DURABILITY);
            return true;
        }

        control.runAnimation(tardis, (ServerPlayerEntity) player, (ServerWorld) world);

        if (this.isOnDelay())
            return false;

        if (!this.control.canRun(tardis, (ServerPlayerEntity) player))
            return false;

        if (world.getRandom().nextBetween(1, 10_000) == 72)
            this.getWorld().playSound(null, this.getBlockPos(), AITSounds.EVEN_MORE_SECRET_MUSIC, SoundCategory.MASTER,
                    1F, 1F);

        boolean hasMallet = player.getMainHandStack().isOf(AITItems.HAMMER);

        if (hasMallet) {
            this.playSound(AITSounds.KNOCK, 1, 0.25f);
            Vec3d pos = this.getPos();
            ((ServerWorld) world).spawnParticles(ParticleTypes.SCRAPE,
                    pos.getX(), pos.getY(), pos.getZ(), 2, 0.2, 0.4, 0.2, 0.02);
        }

        DurabilityStates state = this.getDurabilityState(this.getDurability());

        if (state == DurabilityStates.FULL) {
            if (hasMallet)
                this.subtractDurability(0.1f);
        }

        if (state == DurabilityStates.JAMMED) {
            if (!hasMallet) return false;
        }

        if (state == DurabilityStates.OCCASIONALLY_JAM && random.nextBetween(0, 10) == 5) {
            if (hasMallet) {
                this.setDurability(state.next().durability);
            } else {
                return false;
            }
        }

        if (state == DurabilityStates.SPARKING && random.nextBetween(0, 10) < 5) {
            if (hasMallet) {
                this.setDurability(state.next().durability);
            } else {
                return false;
            }
        }

        if (this.control.shouldHaveDelay(tardis) && !this.isOnDelay()) {
            this.dataTracker.set(ON_DELAY, true);

            Scheduler.get().runTaskLater(() -> this.dataTracker.set(ON_DELAY, false),
                    TaskStage.END_SERVER_TICK, TimeUnit.TICKS, this.control.getDelayLength());
        }

        Control.Result result = this.control.handleRun(tardis, (ServerPlayerEntity) player, (ServerWorld) world, this.getConsoleBlockPos(), leftClick);

        if (result == Control.Result.SEQUENCE) {
            // THIS IS LITERALLY A FEATURE DON'T REMOVE UNLESS I SAY SO DAMMIT - Loqor
            if (random.nextBetween(0, 10) == 5) {
                int subtractCauseICan = random.nextBetween(0, 200);
                this.subtractDurability(subtractCauseICan / 200f);
            }
        }

        ConsoleBlockEntity console = this.getConsole();
        if (console != null) {
            this.getWorld().playSound(null, this.getBlockPos(), this.control.getSound(console.getTypeSchema(), result), SoundCategory.BLOCKS, 0.7f,
                    1f);
        }

        return result.isSuccess();
    }

    public ConsoleBlockEntity getConsole() {
        if (this.getConsoleBlockPos() == null)
            return null;

        BlockEntity blockEntity = this.getWorld().getBlockEntity(this.getConsoleBlockPos());
        if (blockEntity instanceof ConsoleBlockEntity console)
            return console;

        AITMod.LOGGER.warn("Control entity at {} has no console block entity at {}", this.getPos(), this.getConsoleBlockPos());
        return null;
    }

    private void spark() {
        if (this.getEntityWorld().isClient()) return;
        Vec3d pos = this.getPos();
        ((ServerWorld) this.getEntityWorld()).spawnParticles(ParticleTypes.SMOKE, pos.getX(), pos.getY(), pos.getZ(), 1, 0, 0.1, 0, 0.01f);
        if (random.nextBetween(0, 40) == 5 && random.nextBoolean()) {
            this.playSound(SoundEvents.BLOCK_CHAIN_BREAK, 0.1f, random.nextBoolean() ? 1f : 2f);
            ((ServerWorld) this.getEntityWorld()).spawnParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX(), pos.getY(), pos.getZ(), 5, 0.2, 0.2, 0.2, 0.01);
            ((ServerWorld) this.getEntityWorld()).spawnParticles(ParticleTypes.LAVA, pos.getX(), pos.getY(), pos.getZ(), 3, 0.1, 0.1, 0.1, 0.01);
        }
        this.onFire();
    }

    private void onFire() {
        if (this.getEntityWorld().isClient()) return;
        Vec3d pos = this.getPos();
        ((ServerWorld) this.getEntityWorld()).spawnParticles(ParticleTypes.SMOKE, pos.getX(), pos.getY(), pos.getZ(), 1, 0, 0, 0, 0.0f);
        if (this.getEntityWorld().getServer().getTicks() % 10 == 0)
            ((ServerWorld) this.getEntityWorld()).spawnParticles(ParticleTypes.SMALL_FLAME, pos.getX(), pos.getY() + 0.2f, pos.getZ(), 1, 0, 0.075f, 0, 0);
    }

    public BlockPos getConsoleBlockPos() {
        return this.dataTracker.get(CONSOLE_BLOCK_POS);
    }

    public void setScaleAndCalculate(float width, float height) {
        this.setControlWidth(width);
        this.setControlHeight(height);
        this.calculateDimensions();
    }

    public void setControlData(ConsoleTypeSchema consoleType, ControlTypes type, BlockPos consoleBlockPosition) {
        this.setConsolePos(consoleBlockPosition);
        this.control = type.getControl();

        super.setCustomName(Text.translatable(this.control.id().toTranslationKey("control")));

        if (consoleType != null) {
            this.setControlWidth(type.getScale().width);
            this.setControlHeight(type.getScale().height);
        }
    }

    public void controlEditorHandler(PlayerEntity player) {
        float increment = 0.0125f;
        if (player.getMainHandStack().getItem() == Items.EMERALD_BLOCK)
            this.setPosition(this.getPos().add(player.isSneaking() ? -increment : increment, 0, 0));

        if (player.getMainHandStack().getItem() == Items.DIAMOND_BLOCK)
            this.setPosition(this.getPos().add(0, player.isSneaking() ? -increment : increment, 0));

        if (player.getMainHandStack().getItem() == Items.REDSTONE_BLOCK)
            this.setPosition(this.getPos().add(0, 0, player.isSneaking() ? -increment : increment));

        if (player.getMainHandStack().getItem() == Items.COD)
            this.setScaleAndCalculate(player.isSneaking()
                    ? this.getDataTracker().get(WIDTH) - increment
                    : this.getDataTracker().get(WIDTH) + increment, this.getDataTracker().get(HEIGHT));

        if (player.getMainHandStack().getItem() == Items.COOKED_COD)
            this.setScaleAndCalculate(this.getDataTracker().get(WIDTH),
                    player.isSneaking()
                            ? this.getDataTracker().get(HEIGHT) - increment
                            : this.getDataTracker().get(HEIGHT) + increment);

        if (this.getConsoleBlockPos() != null) {
            Vec3d centered = this.getPos().subtract(this.getConsoleBlockPos().toCenterPos());
            if (this.control != null)
                player.sendMessage(Text.literal("EntityDimensions.changing(" + this.getControlWidth() + "f, "
                        + this.getControlHeight() + "f), new Vector3f(" + centered.getX() + "f, " + centered.getY()
                        + "f, " + centered.getZ() + "f)),"));
        }
    }

    @Override
    public void setCustomName(@Nullable Text name) {}

    public enum DurabilityStates {
        JAMMED(0.0f),
        CATCH_FIRE(0.25f),
        SPARKING(0.5f),
        OCCASIONALLY_JAM(0.75f),
        FULL(ConsoleControlEntity.MAX_DURABILITY);
        public final float durability;
        DurabilityStates(float durabilityLevel) {
            this.durability = durabilityLevel;
        }

        public static DurabilityStates get(String id) {
            return DurabilityStates.valueOf(id.toUpperCase());
        }

        public static DurabilityStates get(float level) {
            level = DurabilityStates.normalize(level);

            for (int i = 0; i < values().length - 1; i++) {
                DurabilityStates current = values()[i];
                DurabilityStates next = values()[i + 1];

                if (current.durability <= level && level < next.durability)
                    return current;
            }

            return DurabilityStates.FULL;
        }

        public static float normalize(float durability) {
            return Math.min(Math.max(durability, DurabilityStates.JAMMED.durability), DurabilityStates.FULL.durability);
        }

        public DurabilityStates next() {
            return switch (this) {
                case JAMMED -> CATCH_FIRE;
                case CATCH_FIRE -> SPARKING;
                case SPARKING -> OCCASIONALLY_JAM;
                case OCCASIONALLY_JAM -> FULL;
                case FULL -> JAMMED;
            };
        }
    }
}

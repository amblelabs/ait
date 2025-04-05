package dev.amble.ait.core.entities;

import dev.drtheo.scheduler.api.Scheduler;
import dev.drtheo.scheduler.api.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.entities.ai.goals.DalekAttackGoal;
import dev.amble.ait.module.gun.core.entity.GunEntityTypes;
import dev.amble.ait.module.gun.core.entity.StaserBoltEntity;

public class DalekEntity extends HostileEntity implements RangedAttackMob {
    public final AnimationState startMovingTransitionState = new AnimationState();
    public final AnimationState stopMovingTransitionState = new AnimationState();
    public final AnimationState exterminateAnimationState = new AnimationState();
    public final AnimationState exterminateAltAnimationState = new AnimationState();
    public final AnimationState aimAnimationState = new AnimationState();
    public final AnimationState yellStayAnimationState = new AnimationState();
    public final AnimationState yellDoNotMoveAnimationState = new AnimationState();
    public static final TrackedDataHandler<DalekEntity.DalekState> DALEK_STATE = TrackedDataHandler.ofEnum(DalekEntity.DalekState.class);
    private static final TrackedData<DalekEntity.DalekState> STATE = DataTracker.registerData(DalekEntity.class, DALEK_STATE);
    public DalekEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    static {
        TrackedDataHandlerRegistry.register(DALEK_STATE);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(4, new DalekAttackGoal<>(this, 1.0, 80, 30.0f));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, OcelotEntity.class, 6.0f, 1.0, 1.2));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, CatEntity.class, 6.0f, 1.0, 1.2));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, WolfEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(STATE, DalekState.DEFAULT);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(AITSounds.DALEK_MOVE, 0.5f, 1.0f);
    }

    @Override
    public void setAttacking(boolean attacking) {
        super.setAttacking(attacking);
    }
    public static DefaultAttributeContainer.Builder createDalekAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.225)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1250.0);
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_CHAIN_HIT;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
    }
    @Environment(value = EnvType.CLIENT)
    public boolean isSpeaking() {
        return this.exterminateAltAnimationState.isRunning() ||
                this.exterminateAnimationState.isRunning() ||
                this.yellStayAnimationState.isRunning() ||
                this.yellDoNotMoveAnimationState.isRunning();
    }
    @Override
    public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
        return true;
    }
    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 1f;
    }
    @Override
    public double getHeightOffset() {
        return -0.5;
    }

    public DalekState getDalekState() {
        return this.dataTracker.get(STATE);
    }

    private DalekEntity setState(DalekEntity.DalekState state) {
        this.dataTracker.set(STATE, state);
        return this;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (STATE.equals(data)) {
            DalekState state = this.getDalekState();
            this.stopAnimations();
            switch (state) {
                case EXTERMINATE:
                    this.exterminateAltAnimationState.stop();
                    this.yellStayAnimationState.stop();
                    this.yellDoNotMoveAnimationState.stop();
                    this.exterminateAnimationState.start(this.age);
                    break;
                case EXTERMINATE_ALT:
                    this.exterminateAnimationState.stop();
                    this.yellStayAnimationState.stop();
                    this.yellDoNotMoveAnimationState.stop();
                    this.exterminateAltAnimationState.start(this.age);
                    break;
                case YELL_STAY:
                    this.exterminateAnimationState.stop();
                    this.exterminateAltAnimationState.stop();
                    this.yellDoNotMoveAnimationState.stop();
                    this.yellStayAnimationState.start(this.age);
                    break;
                case YELL_DONT_MOVE:
                    this.exterminateAnimationState.stop();
                    this.exterminateAltAnimationState.stop();
                    this.yellStayAnimationState.stop();
                    this.yellDoNotMoveAnimationState.start(this.age);
                    break;
                case ATTACK:
                    this.aimAnimationState.start(this.age);
                    break;
                default:
                    break;
            }
        }
        super.onTrackedDataSet(data);
    }

    public void stopAnimations() {
        this.aimAnimationState.stop();
        this.exterminateAltAnimationState.stop();
        this.exterminateAnimationState.stop();
        this.yellStayAnimationState.stop();
        this.yellDoNotMoveAnimationState.stop();
        this.startMovingTransitionState.stop();
        this.stopMovingTransitionState.stop();
    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        PersistentProjectileEntity projectile = createStaserbolt(this.getWorld(), this);
        setProjectileVelocity(target, projectile);
        playFireSound();
        this.getWorld().spawnEntity(projectile);
        schedulePostAttackActions();
    }
    private void setProjectileVelocity(LivingEntity target, PersistentProjectileEntity projectile) {
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - projectile.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        projectile.setVelocity(d, e + g * 0.2f, f, 1.6f, 14 - this.getWorld().getDifficulty().getId() * 4);
    }
    private void playFireSound() {
        this.playSound(AITSounds.IMPERIAL_FIRE, 0.23f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
    }
    private void schedulePostAttackActions() {
        Scheduler.get().runTaskLater(() -> {
            if (this.getWorld().getRandom().nextBoolean()) {
                handleRandomTrue();
            } else {
                handleRandomFalse();
            }
        }, TimeUnit.TICKS, 20);
    }
    private void handleRandomTrue() {
        if (this.getWorld().getRandom().nextBetween(0, 1) == 0) {
            playSoundAndSendStatus(AITSounds.IMPERIAL_EXTERMINATE_ALT, DalekState.EXTERMINATE);
        } else {
            playSoundAndSendStatus(AITSounds.IMPERIAL_STAY, DalekState.YELL_STAY);
        }
    }
    private void handleRandomFalse() {
        if (this.getWorld().getRandom().nextBetween(0, 1) == 0) {
            playSoundAndSendStatus(AITSounds.IMPERIAL_EXTERMINATE, DalekState.EXTERMINATE_ALT);
        } else {
            playSoundAndSendStatus(AITSounds.IMPERIAL_DO_NOT_MOVE, DalekState.YELL_DONT_MOVE);
        }
    }
    private void playSoundAndSendStatus(SoundEvent sound, DalekState state) {
        this.getWorld().playSound(null, this.getPos().getX(), this.getPos().getY(),
                this.getPos().getZ(), sound, SoundCategory.HOSTILE, 2.0F, 1.0F);
        this.setState(state);
    }
    public PersistentProjectileEntity createStaserbolt(World world, LivingEntity shooter) {
        StaserBoltEntity staserBoltEntity = new StaserBoltEntity(GunEntityTypes.STASER_BOLT_ENTITY_TYPE, world);
        return staserBoltEntity.createFromConstructor(world, shooter);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        this.setState(DalekState.DEFAULT);
        super.onDeath(damageSource);
    }

    public enum DalekState {
        DEFAULT,
        ATTACK,
        EXTERMINATE,
        EXTERMINATE_ALT,
        YELL_STAY,
        YELL_DONT_MOVE;

    }
}

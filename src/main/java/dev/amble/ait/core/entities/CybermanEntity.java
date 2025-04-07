package dev.amble.ait.core.entities;

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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.entities.ai.goals.CybermanAttackGoal;
import dev.amble.ait.module.gun.core.entity.GunEntityTypes;
import dev.amble.ait.module.gun.core.entity.StaserBoltEntity;
import dev.amble.ait.module.gun.core.item.GunItems;

public class CybermanEntity extends HostileEntity implements RangedAttackMob {
    public final AnimationState startMovingTransitionState = new AnimationState();
    public final AnimationState stopMovingTransitionState = new AnimationState();
    public final AnimationState walkingAnimationState = new AnimationState();
    public final AnimationState exterminateAltAnimationState = new AnimationState();
    public final AnimationState aimAnimationState = new AnimationState();
    public final AnimationState yellStayAnimationState = new AnimationState();
    public final AnimationState yellDoNotMoveAnimationState = new AnimationState();
    public static final TrackedDataHandler<CybermanEntity.CybermanState> DALEK_STATE = TrackedDataHandler.ofEnum(CybermanEntity.CybermanState.class);
    private static final TrackedData<CybermanEntity.CybermanState> STATE = DataTracker.registerData(CybermanEntity.class, DALEK_STATE);
    private static final Identifier DALEK_UPDATE = AITMod.id("update_cyberman_status");

    public CybermanEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public CybermanState getCybermanState() {
        return this.dataTracker.get(STATE);
    }

    private CybermanEntity setState(CybermanState state) {
        this.dataTracker.set(STATE, state);
        return this;
    }

    static {
        TrackedDataHandlerRegistry.register(DALEK_STATE);
    }


    @Override
    public void setAttacking(boolean attacking) {
        this.getWorld().sendEntityStatus(this, ATTACK);
        super.setAttacking(attacking);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (STATE.equals(data)) {
            CybermanState state = this.getCybermanState();
            this.stopAnimations();
            switch (state) {
                case WALK:
                    this.exterminateAltAnimationState.stop();
                    this.yellStayAnimationState.stop();
                    this.yellDoNotMoveAnimationState.stop();
                    this.walkingAnimationState.start(this.age);
                    break;
                case YELL_STAY:
                    this.walkingAnimationState.stop();
                    this.exterminateAltAnimationState.stop();
                    this.yellDoNotMoveAnimationState.stop();
                    this.yellStayAnimationState.start(this.age);
                    break;
                case YELL_DONT_MOVE:
                    this.walkingAnimationState.stop();
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
        this.walkingAnimationState.stop();
        this.yellStayAnimationState.stop();
        this.yellDoNotMoveAnimationState.stop();
        this.startMovingTransitionState.stop();
        this.stopMovingTransitionState.stop();
    }


    private void setProjectileVelocity(LivingEntity target, PersistentProjectileEntity projectile) {
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - projectile.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        projectile.setVelocity(d, e + g * 0.2f, f, 1.6f, 14 - this.getWorld().getDifficulty().getId() * 4);
    }
    private void playFireSound() {
        this.playSound(AITSounds.HANDBRAKE_DOWN, 0.23f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 1.74F;
    }

    @Override
    public double getHeightOffset() {
        return -0.6;
    }

    @Override
    public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
        return weapon == GunItems.CULT_STASER;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(4, new CybermanAttackGoal<>(this, 1.0, 80, 30.0f));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true, player -> {
            return !player.getName().getString().equals("westankrang");
        }));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, WolfEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this));
    }

    public static DefaultAttributeContainer.Builder createCybermanAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f)
                .add(EntityAttributes.GENERIC_ARMOR, 2f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2);
    }


    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SKELETON_DEATH;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        this.setState(CybermanState.DEFAULT);
        super.onDeath(damageSource);
    }


    public enum CybermanState {
        DEFAULT,
        ATTACK,
        WALK,
        YELL_STAY,
        YELL_DONT_MOVE;

    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        PersistentProjectileEntity projectile = createStaserbolt(this.getWorld(), this);
        setProjectileVelocity(target, projectile);
        playFireSound();
        this.getWorld().spawnEntity(projectile);
        //schedulePostAttackActions();
    }

    public PersistentProjectileEntity createStaserbolt(World world, LivingEntity shooter) {
        StaserBoltEntity staserBoltEntity = new StaserBoltEntity(GunEntityTypes.STASER_BOLT_ENTITY_TYPE, world);
        return staserBoltEntity.createFromConstructor(world, shooter);
    }
}

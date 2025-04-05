package dev.amble.ait.core.entities;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import dev.drtheo.scheduler.api.Scheduler;
import dev.drtheo.scheduler.api.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.entities.ai.goals.DalekAttackGoal;
import dev.amble.ait.core.entities.ai.goals.DalekControlTardisGoal;
import dev.amble.ait.core.entities.ai.goals.DalekNavTardisGoal;
import dev.amble.ait.core.entities.daleks.Dalek;
import dev.amble.ait.core.entities.daleks.DalekRegistry;
import dev.amble.ait.mixin.server.RaidAccessor;
import dev.amble.ait.module.gun.core.entity.GunEntityTypes;
import dev.amble.ait.module.gun.core.entity.StaserBoltEntity;

public class DalekEntity extends RaiderEntity implements RangedAttackMob {
    public final AnimationState startMovingTransitionState = new AnimationState();
    public final AnimationState stopMovingTransitionState = new AnimationState();
    public final AnimationState exterminateAnimationState = new AnimationState();
    public final AnimationState exterminateAltAnimationState = new AnimationState();
    public final AnimationState aimAnimationState = new AnimationState();
    public final AnimationState yellStayAnimationState = new AnimationState();
    public final AnimationState yellDoNotMoveAnimationState = new AnimationState();
    public static final TrackedDataHandler<DalekEntity.DalekState> DALEK_STATE = TrackedDataHandler.ofEnum(DalekEntity.DalekState.class);
    private static final TrackedData<DalekEntity.DalekState> STATE = DataTracker.registerData(DalekEntity.class, DALEK_STATE);
    private static final TrackedData<String> DALEK = DataTracker.registerData(DalekEntity.class, TrackedDataHandlerRegistry.STRING);
    private int ambianceTimer = 0;

    public DalekEntity(EntityType<? extends RaiderEntity> entityType, World world) {
        super(entityType, world);
        randomizeDalekType();
    }

    public void randomizeDalekType() {
        Identifier commander = AITMod.id("dalek/commander");
        List<Dalek> dalekList = new ArrayList<>(DalekRegistry.getInstance().toList());
        Dalek commanderDalek = DalekRegistry.getInstance().get(commander);
        dalekList.remove(commanderDalek);
        Dalek dalek = dalekList.get(this.getRandom().nextBetween(0, dalekList.size() - 1));
        if (!this.isPatrolLeader()) {
            this.setDalek(dalek);
        } else {
            this.setDalek(commanderDalek);
        }
    }

    static {
        TrackedDataHandlerRegistry.register(DALEK_STATE);
    }

    @Override
    public void setPatrolLeader(boolean patrolLeader) {
        super.setPatrolLeader(patrolLeader);
        this.setDalek(DalekRegistry.getInstance().get(AITMod.id("dalek/commander")));
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(4, new DalekAttackGoal<>(this, 1.0, 80, 30.0f));
        this.goalSelector.add(5, new ControlTardisGoal(this, 1.0, 100));
        this.goalSelector.add(5, new NavTardisGoal(this, 1.0, 100));
        this.goalSelector.add(3, new MoveToRaidCenterGoal<>(this));
        this.goalSelector.add(2, new DalekEntity.PatrolApproachGoal(this, 10.0f));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, OcelotEntity.class, 6.0f, 1.0, 1.2));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, CatEntity.class, 6.0f, 1.0, 1.2));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PassiveEntity.class, true));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, HostileEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) return;
        if (!this.getWorld().isChunkLoaded(this.getBlockPos())) return;
        if (this.isRemoved() || !this.isAlive()) return;

        if (this.ambianceTimer-- <= 0) {
            this.getWorld().playSound(
                    null,
                    this.getX(), this.getY(), this.getZ(),
                    AITSounds.DALEK_AMBIANCE,
                    SoundCategory.HOSTILE,
                    0.6f,
                    1.0f
            );
            this.ambianceTimer = 40;
        }
    }


    @Override
    public boolean isTeammate(Entity other) {
        if (super.isTeammate(other)) {
            return true;
        }
        if (other instanceof DalekEntity) {
            return this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
        }
        return false;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("dalek", this.getDalekData());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("dalek")) {
            this.setDalek(DalekRegistry.getInstance().get(Identifier.tryParse(nbt.getString("dalek"))));
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(STATE, DalekState.DEFAULT);
        this.dataTracker.startTracking(DALEK, DalekRegistry.IMPERIAL.id().toString());
    }

    public void setDalek(Dalek dalek) {
        this.dataTracker.set(DALEK, dalek.id().toString());
    }

    public String getDalekData() {
        return this.dataTracker.get(DALEK);
    }

    public Dalek getDalek() {
        return DalekRegistry.getInstance().get(Identifier.tryParse(this.getDalekData()));
    }

    @Override
    public void addBonusForWave(int wave, boolean unused) {
        this.setHealth(this.getMaxHealth());
        this.setState(DalekState.DEFAULT);
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
        return AITSounds.DALEK_DEATH;
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
        /*if (DALEK.equals(data)) {
            this.setDalek(DalekRegistry.getInstance()
                    .get(Identifier.tryParse(this.getDalekData())));
        }*/
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
        projectile.setDamage(25d);
        setProjectileVelocity(target, projectile);
        playFireSound();
        this.getWorld().spawnEntity(projectile);
        schedulePostAttackActions();
    }

    private void setProjectileVelocity(LivingEntity target, PersistentProjectileEntity projectile) {
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.1) - projectile.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        projectile.setPosition(this.getPos().add(0, 0.75, 0));
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

    @Override
    public SoundEvent getCelebratingSound() {
        return null;
    }

    public enum DalekState {
        DEFAULT,
        ATTACK,
        EXTERMINATE,
        EXTERMINATE_ALT,
        YELL_STAY,
        YELL_DONT_MOVE;
    }

    protected class PatrolApproachGoal extends Goal {
        private final RaiderEntity raider;
        private final float squaredDistance;
        public final TargetPredicate closeRaiderPredicate = TargetPredicate.createNonAttackable().setBaseMaxDistance(8.0).ignoreVisibility().ignoreDistanceScalingFactor();

        public PatrolApproachGoal(RaiderEntity dalek, float distance) {
            this.raider = dalek;
            this.squaredDistance = distance * distance;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = this.raider.getAttacker();
            return this.raider.getRaid() == null && ((RaidAccessor) this.raider).getPatrolling() && this.raider.getTarget() !=
                    null && !this.raider.isAttacking() && (livingEntity == null || livingEntity.getType() != EntityType.PLAYER);
        }

        @Override
        public void start() {
            super.start();
            this.raider.getNavigation().stop();
            List<RaiderEntity> list = this.raider.getWorld().getTargets(RaiderEntity.class, this.closeRaiderPredicate, this.raider, this.raider.getBoundingBox().expand(8.0, 8.0, 8.0));
            for (RaiderEntity raiderEntity : list) {
                raiderEntity.setTarget(this.raider.getTarget());
            }
        }

        @Override
        public void stop() {
            super.stop();
            LivingEntity livingEntity = this.raider.getTarget();
            if (livingEntity != null) {
                List<RaiderEntity> list = this.raider.getWorld().getTargets(RaiderEntity.class, this.closeRaiderPredicate,
                        this.raider, this.raider.getBoundingBox().expand(8.0, 8.0, 8.0));
                for (RaiderEntity raiderEntity : list) {
                    raiderEntity.setTarget(livingEntity);
                    raiderEntity.setAttacking(true);
                }
                this.raider.setAttacking(true);
            }
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.raider.getTarget();
            if (livingEntity == null) {
                return;
            }
            if (this.raider.squaredDistanceTo(livingEntity) > (double)this.squaredDistance) {
                this.raider.getLookControl().lookAt(livingEntity, 30.0f, 30.0f);
                if (this.raider.getRandom().nextInt(50) == 0) {
                    this.raider.playAmbientSound();
                }
            } else {
                this.raider.setAttacking(true);
            }
            super.tick();
        }
    }

    class ControlTardisGoal extends DalekControlTardisGoal {
        ControlTardisGoal(PathAwareEntity mob, double speed, int maxYDifference) {
            super(AITBlocks.CONSOLE, mob, speed, maxYDifference);
        }

        @Override
        public void tickStepping(WorldAccess world, BlockPos pos) {
            world.playSound(null, pos, AITSounds.DALEK_PLUNGER, SoundCategory.HOSTILE, 0.5f, 0.9f + DalekEntity.this.random.nextFloat() * 0.2f);
        }

        @Override
        public double getDesiredDistanceToTarget() {
            return 2;
        }
    }

    class NavTardisGoal extends DalekNavTardisGoal {
        NavTardisGoal(PathAwareEntity mob, double speed, int maxYDifference) {
            super(AITBlocks.EXTERIOR_BLOCK, mob, speed, maxYDifference);
        }

        @Override
        public double getDesiredDistanceToTarget() {
            return 2;
        }
    }
}

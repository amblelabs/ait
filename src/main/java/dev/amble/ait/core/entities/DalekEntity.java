package dev.amble.ait.core.entities;

import dev.drtheo.scheduler.api.Scheduler;
import dev.drtheo.scheduler.api.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
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
    private static final byte ATTACK = EntityStatuses.PLAY_ATTACK_SOUND;
    private static final byte EXTERMINATE = EntityStatuses.TAME_OCELOT_FAILED;
    private static final byte EXTERMINATE_ALT = EntityStatuses.TAME_OCELOT_SUCCESS;
    private static final byte YELL_STAY = EntityStatuses.LOOK_AT_VILLAGER;
    private static final byte YELL_DONT_MOVE = EntityStatuses.STOP_LOOKING_AT_VILLAGER;
    private static final Identifier DALEK_UPDATE = AITMod.id("update_dalek_status");
    public DalekEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
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
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(AITSounds.DALEK_MOVE, 0.5f, 1.0f);
    }

    static {
        ClientPlayNetworking.registerReceiver(DALEK_UPDATE, (client, handler, buf, responseSender) -> {
            int id = buf.readInt();
            byte status = buf.readByte();
            if (client.world == null) return;
            DalekEntity dalek = (DalekEntity) client.world.getEntityById(id);
            if (dalek == null) return;
            client.execute(() -> {
                switch (status) {
                    case EXTERMINATE:
                        dalek.exterminateAltAnimationState.stop();
                        dalek.yellStayAnimationState.stop();
                        dalek.yellDoNotMoveAnimationState.stop();
                        dalek.exterminateAnimationState.start(dalek.age);
                        break;
                    case EXTERMINATE_ALT:
                        dalek.exterminateAnimationState.stop();
                        dalek.yellStayAnimationState.stop();
                        dalek.yellDoNotMoveAnimationState.stop();
                        dalek.exterminateAltAnimationState.start(dalek.age);
                        break;
                    case YELL_STAY:
                        dalek.exterminateAnimationState.stop();
                        dalek.exterminateAltAnimationState.stop();
                        dalek.yellDoNotMoveAnimationState.stop();
                        dalek.yellStayAnimationState.start(dalek.age);
                        break;
                    case YELL_DONT_MOVE:
                        dalek.exterminateAnimationState.stop();
                        dalek.exterminateAltAnimationState.stop();
                        dalek.yellStayAnimationState.stop();
                        dalek.yellDoNotMoveAnimationState.start(dalek.age);
                        break;
                    default:
                        dalek.aimAnimationState.start(dalek.age);
                        break;
                }
            });
        });
    }

    @Override
    public void setAttacking(boolean attacking) {
        this.getWorld().sendEntityStatus(this, ATTACK);
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
        return 1.44f;
    }
    @Override
    public double getHeightOffset() {
        return -0.5;
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
            playSoundAndSendStatus(AITSounds.IMPERIAL_EXTERMINATE_ALT, EXTERMINATE);
        } else {
            playSoundAndSendStatus(AITSounds.IMPERIAL_STAY, YELL_STAY);
        }
    }
    private void handleRandomFalse() {
        if (this.getWorld().getRandom().nextBetween(0, 1) == 0) {
            playSoundAndSendStatus(AITSounds.IMPERIAL_EXTERMINATE, EXTERMINATE_ALT);
        } else {
            playSoundAndSendStatus(AITSounds.IMPERIAL_DO_NOT_MOVE, YELL_DONT_MOVE);
        }
    }
    private void playSoundAndSendStatus(SoundEvent sound, byte status) {
        this.getWorld().playSound(null, this.getPos().getX(), this.getPos().getY(),
                this.getPos().getZ(), sound, SoundCategory.HOSTILE, 2.0F, 1.0F);
        for (ServerPlayerEntity player : PlayerLookup.tracking(this)) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeUuid(this.getUuid());
            buf.writeByte(status);
            ServerPlayNetworking.send(player, DALEK_UPDATE, buf);
        }
    }
    public PersistentProjectileEntity createStaserbolt(World world, LivingEntity shooter) {
        StaserBoltEntity staserBoltEntity = new StaserBoltEntity(GunEntityTypes.STASER_BOLT_ENTITY_TYPE, world);
        return staserBoltEntity.createFromConstructor(world, shooter);
    }
}

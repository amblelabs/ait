package dev.amble.ait.core.entities;

import java.util.List;

import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.impl.GravitationalCircuit;
import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.link.LinkableLivingEntity;
import dev.amble.ait.client.util.ClientShakeUtil;
import dev.amble.ait.core.AITDimensions;
import dev.amble.ait.core.AITEntityTypes;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisDesktop;
import dev.amble.ait.core.tardis.handler.travel.TravelUtil;
import dev.amble.ait.core.tardis.control.impl.DirectionControl;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.mixin.rwf.LivingEntityAccessor;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;
import dev.amble.lib.data.CachedDirectedGlobalPos;

public class FlightTardisEntity extends LinkableLivingEntity implements JumpingMount {

    private static final List<ItemStack> EMPTY = List.of();
    private static final ItemStack AIR = new ItemStack(Items.AIR);
    private static final int PHASE_MIN_TICKS = 20;
    private static final int PHASE_MAX_TICKS = 60;
    private static final int PHASE_FAIL_RADIUS = 10_000;
    private static final int PHASE_FAIL_REPAIR_TICKS = 5_000;
    private static final float PHASE_DEMAT_DAMAGE = 45f;
    private BlockPos interiorPos;

    private int landedTicks = 0;
    private int phaseTicks = 0;

    private boolean prevHorizontalCollision = false;
    private boolean prevUpwardCollision = false;

    public FlightTardisEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        this.setInvulnerable(true);
    }

    private FlightTardisEntity(BlockPos riderPos, CachedDirectedGlobalPos pos, ServerTardis tardis) {
        this(AITEntityTypes.FLIGHT_TARDIS_TYPE, pos.getWorld());

        this.interiorPos = riderPos;

        this.link(tardis);
        this.setPosition(pos.getPos().toCenterPos());
        this.setVelocity(Vec3d.ZERO);

        this.setRotation(RotationPropertyHelper.toDegrees(
                DirectionControl.getGeneralizedRotation(pos.getRotation())
        ), 0);
    }

    public static FlightTardisEntity createAndSpawn(ServerPlayerEntity player, ServerTardis tardis) {
        CachedDirectedGlobalPos exteriorPos = tardis.travel().position();

        FlightTardisEntity entity = new FlightTardisEntity(
                player.getBlockPos(), exteriorPos, tardis
        );

        exteriorPos.getWorld().spawnEntity(entity);
        return entity;
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    protected float getOffGroundSpeed() {
        if (this.isLinked()  && this.tardis().get().travel() != null) {
            float spaceSpeed = this.getWorld().getRegistryKey().equals(AITDimensions.SPACE) ? 0.1f : 0.05f;
            return this.getMovementSpeed() * (this.tardis().get().travel().speed() * spaceSpeed);
        }
        return super.getOffGroundSpeed();
    }

    @Override
    public void tick() {
        this.setRotation(0, 0);
        super.tick();

        if (!this.getWorld().isClient()) {
            boolean upwardCollision = this.verticalCollision && !this.isOnGround();

            if (this.horizontalCollision && !this.prevHorizontalCollision) {
                this.playCollisionSound(true);
            } else if (upwardCollision && !this.prevUpwardCollision) {
                this.playCollisionSound(false);
            }

            this.prevHorizontalCollision = this.horizontalCollision;
            this.prevUpwardCollision = upwardCollision;
        }

        PlayerEntity player = this.getPlayer();

        if (player == null)
            return;

        if (!this.isLinked())
            return;

        Tardis tardis = this.tardis().get();

        if (this.getWorld().isClient()) {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player == this.getControllingPassenger()) {
                client.options.setPerspective(Perspective.THIRD_PERSON_BACK);

                if (!this.groundCollision)
                    ClientShakeUtil.shake((float) (tardis.travel().speed() + this.getVelocity().horizontalLength()) / tardis.travel().maxSpeed().get());
            }

            return;
        }

        if (!player.isInvisible())
            player.setInvisible(true);

        if (!player.isInvulnerable())
            player.setInvulnerable(true);

        tardis.flight().tickFlight((ServerPlayerEntity) player, this.getBlockPos());

        this.applyGravCircuitDamageEffects(tardis);
        if (this.tickPhase(tardis, player))
            return;

        if (tardis.door().isOpen()) {
            this.getWorld().getOtherEntities(this, this.getBoundingBox(), entity
                    -> !entity.isSpectator() && entity != player && entity instanceof LivingEntity).forEach(
                    entity -> TardisUtil.teleportInside(tardis.asServer(), entity)
            );
        }

        private boolean tickPhase(Tardis tardis, PlayerEntity player) {
            if (this.phaseTicks <= 0)
                return false;

            this.noClip = true;
            this.phaseTicks--;

            if (this.phaseTicks > 0)
                return false;

            this.noClip = false;

            if (this.getWorld().isSpaceEmpty(this, this.getBoundingBox()))
                return false;

            this.failPhase(tardis, player);
            return true;
        }

        public void startPhase() {
            if (this.phaseTicks > 0 || this.getWorld().isClient() || !this.isLinked())
                return;

            this.phaseTicks = AITMod.RANDOM.nextBetween(PHASE_MIN_TICKS, PHASE_MAX_TICKS);
            this.noClip = true;
            this.tardis().get().subsystems().demat().removeDurability(PHASE_DEMAT_DAMAGE);
        }

        private void failPhase(Tardis tardis, PlayerEntity player) {
            this.finishLand(tardis, player);
            tardis.travel().forceDestination(cached -> TravelUtil.jukePos(cached, 1, PHASE_FAIL_RADIUS));
            tardis.subsystems().engine().setDurability(0);
            tardis.subsystems().<GravitationalCircuit>get(SubSystem.Id.GRAVITATIONAL).setDurability(0);
            tardis.subsystems().demat().setDurability(0);
            tardis.crash().addRepairTicks(PHASE_FAIL_REPAIR_TICKS);
            tardis.travel().dematerialize();
        }

        boolean antigravs = tardis.travel().antigravs().get();

        if (player.isSneaking() && (this.isOnGround() || antigravs)
                && this.getWorld().isInBuildLimit(this.getBlockPos())) {
            if (antigravs)
                this.landedTicks = 0;

            if (antigravs || this.landedTicks++ > 60)
                this.finishLand(tardis, player);
        } else {
            this.landedTicks = 0;
        }
    }

    private void applyGravCircuitDamageEffects(Tardis tardis) {
        if (this.getWorld().isClient()) return;

        float durability = tardis.subsystems().<GravitationalCircuit>get(SubSystem.Id.GRAVITATIONAL).durability();

        float threshold = (float) DurableSubSystem.MAX_DURABILITY / 4;

        if (durability < threshold && tardis.travel().speed() > 0) {
            int chance = Math.max(10, (int)(durability));

            if (AITMod.RANDOM.nextInt(chance) == 0) {
                double forceScale = 0.5 + ((threshold - durability) * 4.0);

                Vec3d fling = new Vec3d(
                        (AITMod.RANDOM.nextDouble() - 0.5) * 2.0,
                        (AITMod.RANDOM.nextDouble() - 0.5) * 2.0,
                        (AITMod.RANDOM.nextDouble() - 0.5) * 2.0
                ).normalize().multiply(forceScale);

                this.addVelocity(fling.x, fling.y, fling.z);
                this.velocityModified = true;

                tardis.alarm().enable();

                ServerWorld serverWorld = (ServerWorld) this.getWorld();
                double x = this.getX();
                double y = this.getY() + (this.getHeight() / 2.0);
                double z = this.getZ();

                serverWorld.spawnParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 15, 0.5, 0.5, 0.5, 0.05);
                serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 10, 0.5, 0.5, 0.5, 0.2);

                this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 2.0F, 0.5F + AITMod.RANDOM.nextFloat());
            }
        }
    }

    private void playCollisionSound(boolean horizontal) {
        Box box = this.getBoundingBox();

        if (horizontal) {
            box = box.expand(0.2, 0, 0.2);
        } else {
            box = box.expand(0, 0.2, 0).offset(0, 0.2, 0);
        }

        Iterable<BlockPos> blocks = BlockPos.iterate(
                MathHelper.floor(box.minX), MathHelper.floor(box.minY), MathHelper.floor(box.minZ),
                MathHelper.ceil(box.maxX), MathHelper.ceil(box.maxY), MathHelper.ceil(box.maxZ)
        );

        for (BlockPos pos : blocks) {
            BlockState state = this.getWorld().getBlockState(pos);
            if (!state.isAir() && !state.isLiquid()) {
                SoundEvent stepSound = state.getSoundGroup().getStepSound();
                this.getWorld().playSound(null, this.getBlockPos(), stepSound, SoundCategory.BLOCKS, 3.0F, 1.0F / (AITMod.RANDOM.nextFloat() * 0.4F + 0.8F));
                break;
            }
        }
    }

    @Override
    public void setOnGround(boolean onGround, Vec3d movement) {
        if (!this.isOnGround() && onGround)
            this.playThud();

        super.setOnGround(onGround, movement);
    }

    @Override
    public void setOnGround(boolean onGround) {
        if (!this.isOnGround() && onGround)
            this.playThud();

        super.setOnGround(onGround);
    }

    private void playThud() {
        this.getWorld().playSound(null, this.getBlockPos(), AITSounds.LAND_THUD, SoundCategory.BLOCKS, 2F, 1F / (AITMod.RANDOM.nextFloat() * 0.4F + 0.8F));
    }

    private void finishLand(Tardis tardis, PlayerEntity player) {
        this.noClip = false;
        this.phaseTicks = 0;

        if (this.getWorld().isClient()) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            client.options.hudHidden = false;
            return;
        }

        if (!(player instanceof ServerPlayerEntity serverPlayer))
            return;

        if (this.interiorPos == null) {
            TardisUtil.teleportInside(tardis.asServer(), serverPlayer);
        } else {
            TardisUtil.teleportToInteriorPosition(tardis.asServer(), serverPlayer, this.interiorPos);
        }

        tardis.flight().exitFlight(serverPlayer);
        tardis.travel().speed(0);
        this.discard();
    }

    @Override
    public boolean doesRenderOnFire() {
        return false;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return EMPTY;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return AIR;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) { }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    public PlayerEntity getPlayer() {
        if (this.getControllingPassenger() instanceof PlayerEntity player)
            return player;

        return null;
    }

    @Nullable @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();

        if (entity instanceof LivingEntity living)
            return living;

        return null;
    }

    @Override
    protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput) {
        Planet planet = PlanetRegistry.getInstance().get(this.getWorld());
        boolean planetGravity = planet != null && planet.zeroGravity();

        if (!this.isLinked() || !this.tardis().get().fuel().hasPower()) {
            return new Vec3d(0, planetGravity ? 0.0f : -2f, 0);
        }

        float f = controllingPlayer.sidewaysSpeed * this.tardis().get().travel().speed();
        float g = controllingPlayer.forwardSpeed * this.tardis().get().travel().speed();

        float speedVal = this.isSubmergedInWater() ? 30f : 10f;

        boolean canFall = this.tardis().get().travel().antigravs().get() || planetGravity;

        double v = ((LivingEntityAccessor) controllingPlayer).getJumping() ? speedVal :
                controllingPlayer.isSneaking() ? -speedVal :
                canFall ? 0.0f : f > 0 || g > 0 ? -0.5f : -2f;

        if (v < 0 && this.isOnGround())
            return Vec3d.ZERO.add(0, -0.4f, 0);

        return new Vec3d(f, v, g);//return this.isOnGround() ? new Vec3d(0, 0, 0) : new Vec3d(f, v * 4f, g);
    }

    @Override
    protected float getSaddledSpeed(PlayerEntity controllingPlayer) {
        return (float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
    }

    @Override
    public double getMountedHeightOffset() {
        return 0.5f;
    }

    public float getRotation(float tickDelta) {
        return ((float) this.age + tickDelta) / 20.0f;
    }

    @Override
    protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
        Vec2f vec2f = new Vec2f(0, controllingPlayer.getYaw());
        this.setRotation(vec2f.y, vec2f.x);

        this.setBodyYaw(180.0f - this.getRotation(0.5f) / (float) Math.PI * 180f);
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains("InteriorPos")) {
            this.interiorPos = BlockPos.fromLong(nbt.getLong("InteriorPos"));
        } else {
            this.interiorPos = BlockPos.ORIGIN;
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.getWorld().isClient()) return;
        if (!this.isLinked()) return;


        TardisDesktop desktop = tardis().get().getDesktop();

        if (interiorPos == null)
            interiorPos = desktop.getConsolePos().iterator().next();

        if (interiorPos == null)
            interiorPos = desktop.getDoorPos().getPos();

        if (interiorPos == null)
            interiorPos = new BlockPos(0, 0, 0);

        nbt.putLong("InteriorPos", interiorPos.asLong());
    }

    public static DefaultAttributeContainer.Builder createDummyAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 5);
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return AITSounds.CLOISTER;
    }

    @Override
    public void setJumpStrength(int strength) {
    }

    @Override
    public boolean canJump() {
        return false;
    }

    @Override
    public void startJumping(int height) { }

    @Override
    public void stopJumping() { }
}
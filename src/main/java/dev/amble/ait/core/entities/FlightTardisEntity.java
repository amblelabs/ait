package dev.amble.ait.core.entities;

import java.util.List;

import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.impl.GravitationalCircuit;
import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;

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
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.link.LinkableLivingEntity;
import dev.amble.ait.core.AITDimensions;
import dev.amble.ait.core.AITEntityTypes;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisDesktop;
import dev.amble.ait.core.tardis.control.impl.DirectionControl;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.mixin.rwf.LivingEntityAccessor;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;
import dev.amble.lib.data.CachedDirectedGlobalPos;

public class FlightTardisEntity extends LinkableLivingEntity implements JumpingMount {

    private static final List<ItemStack> EMPTY = List.of();
    private static final ItemStack AIR = new ItemStack(Items.AIR);

    private static final TrackedData<Boolean> GROUND_COLLISION =
            DataTracker.registerData(FlightTardisEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Boolean> PHASING =
            DataTracker.registerData(FlightTardisEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final int PHASE_DURATION = 40;

    public static final int PHASE_COOLDOWN = 600;

    private static final float PHASE_SLOW_FACTOR = 0.35f;

    private BlockPos interiorPos;

    private int landedTicks = 0;

    boolean prevHorizontalCollision = false;
    boolean prevUpwardCollision = false;

    private static final double BOUNCE_RESTITUTION = 0.5;

    private boolean prevAdjacentToWall = false;
    private Vec3d incomingVelocity = Vec3d.ZERO;

    private int phaseTicks = 0;
    private int phaseCooldown = 0;
    private boolean phaseJustEnded = false;

    private int collisionEffectCooldown = 0;

    public FlightTardisEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        this.setInvulnerable(true);
    }

    private FlightTardisEntity(BlockPos riderPos, CachedDirectedGlobalPos pos, ServerTardis tardis) {
        this(AITEntityTypes.FLIGHT_TARDIS_TYPE, pos.getWorld());

        this.interiorPos = riderPos;

        this.link(tardis);
        this.setPosition(pos.getPos().toCenterPos().subtract(0, 0.5, 0));
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
        this.incomingVelocity = this.getVelocity();
        super.tick();

        if (!this.getWorld().isClient()) {
            if (phaseCooldown > 0)
                phaseCooldown--;

            if (phaseTicks > 0 && --phaseTicks == 0)
                phaseJustEnded = true;

            boolean phasing = phaseTicks > 0;
            if (this.dataTracker.get(PHASING) != phasing)
                this.dataTracker.set(PHASING, phasing);

            boolean grounded = this.isOnGround();
            if (this.dataTracker.get(GROUND_COLLISION) != grounded) {
                this.dataTracker.set(GROUND_COLLISION, grounded);
            }
        }

        this.noClip = this.dataTracker.get(PHASING);

        PlayerEntity player = this.getPlayer();

        if (player == null)
            return;

        if (!this.isLinked())
            return;

        Tardis tardis = this.tardis().get();

        this.tickBounce(player);

        if (this.getWorld().isClient()) {
            FlightTardisClientHelper.clientFlightTick(this, tardis);
            return;
        }

        if (!player.isInvisible())
            player.setInvisible(true);

        if (!player.isInvulnerable())
            player.setInvulnerable(true);

        tardis.flight().tickFlight((ServerPlayerEntity) player, this.getBlockPos());

        this.applyGravCircuitDamageEffects(tardis);

        if (phaseJustEnded) {
            phaseJustEnded = false;
            if (this.isInsideBlock() && player instanceof ServerPlayerEntity serverPlayer)
                this.applyPhasePunishment(serverPlayer, tardis);
        }

        if (tardis.door().isOpen()) {
            this.getWorld().getOtherEntities(this, this.getBoundingBox(), entity
                    -> !entity.isSpectator() && entity != player && entity instanceof LivingEntity && !(entity instanceof FlightTardisEntity) && entity != this.getControllingPassenger()).forEach(
                    entity -> TardisUtil.teleportInside(tardis.asServer(), entity)
            );
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

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(GROUND_COLLISION, false);
        this.dataTracker.startTracking(PHASING, false);
    }

    private void applyGravCircuitDamageEffects(Tardis tardis) {
        if (this.getWorld().isClient())
            return;

        float durability = tardis.subsystems().<GravitationalCircuit>get(SubSystem.Id.GRAVITATIONAL).durability();
        float threshold = (float) DurableSubSystem.MAX_DURABILITY / 4;

        if (durability >= threshold || tardis.travel().speed() == 0) return;

        int chance = Math.max(10, (int) durability);
        if (AITMod.RANDOM.nextInt(chance) != 0) return;

        float forceScale = MathHelper.lerp(1f - durability / threshold, 0.5f, 3.0f);

        Vec3d fling = new Vec3d(
                (AITMod.RANDOM.nextDouble() - 0.5) * 2.0,
                (AITMod.RANDOM.nextDouble() - 0.5) * 2.0,
                (AITMod.RANDOM.nextDouble() - 0.5) * 2.0
        ).normalize().multiply(forceScale);

        this.addVelocity(fling.x, fling.y, fling.z);
        this.velocityModified = true;

        tardis.alarm().enable();
        ServerWorld serverWorld = (ServerWorld) this.getWorld();
        double x = this.getX(), y = this.getY() + this.getHeight() / 2.0, z = this.getZ();
        serverWorld.spawnParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 15, 0.5, 0.5, 0.5, 0.05);
        serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 10, 0.5, 0.5, 0.5, 0.2);
        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 2.0F, 0.5F + AITMod.RANDOM.nextFloat());
    }

    private void spawnCollisionParticles(boolean horizontal) {
        Box box = this.getBoundingBox();

        if (horizontal) {
            box = box.expand(0.2, 0, 0.2);
        } else {
            box = box.expand(0, 0.2, 0).offset(0, 0.2, 0);
        }

        BlockPos center = this.getBlockPos();

        Iterable<BlockPos> blocks = BlockPos.iterate(
                MathHelper.floor(box.minX), MathHelper.floor(box.minY), MathHelper.floor(box.minZ),
                MathHelper.ceil(box.maxX), MathHelper.ceil(box.maxY), MathHelper.ceil(box.maxZ)
        );

        BlockPos closest = null;
        BlockState closestState = null;
        double closestDistSq = Double.MAX_VALUE;

        for (BlockPos pos : blocks) {
            BlockState state = this.getWorld().getBlockState(pos);
            if (state.isAir() || state.isLiquid())
                continue;

            double distSq = pos.getSquaredDistance(center);
            if (distSq < closestDistSq) {
                closestDistSq = distSq;
                closest = pos;
                closestState = state;
            }
        }

        if (closest == null)
            return;

        if (this.getWorld() instanceof ServerWorld serverWorld && !this.isCollidingOnGround()) {
            serverWorld.spawnParticles(
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, closestState),
                    this.getPos().x, this.getPos().y, this.getPos().z,
                    25, 0.4, 0.4, 0.4, 0.15
            );
            serverWorld.spawnParticles(ParticleTypes.POOF,
                    this.getPos().x, this.getPos().y, this.getPos().z,
                    5, 0.3, 0.3, 0.3, 0.05
            );
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

    private void tickBounce(PlayerEntity player) {
        if (this.noClip) {
            this.prevAdjacentToWall = false;
            return;
        }

        boolean adjacent = this.isAdjacentToHorizontalSolidBlock();

        if (adjacent && !this.prevAdjacentToWall) {
            this.applyBounce(player);

            if (!this.getWorld().isClient())
                this.playSound(AITSounds.LAND_THUD, 1, 1);
        }

        this.prevAdjacentToWall = adjacent;

        if (this.getWorld().isClient())
            return;

        if (collisionEffectCooldown > 0) {
            collisionEffectCooldown--;
        } else if (adjacent) {
            this.spawnCollisionParticles(true);
            collisionEffectCooldown = 15;
        }
    }

    private void applyBounce(PlayerEntity player) {
        Vec3d normal = this.wallNormal(player);

        if (normal.equals(Vec3d.ZERO))
            return;

        Vec3d in = this.incomingVelocity;
        double vn = in.x * normal.x + in.z * normal.z;

        if (vn >= 0)
            return;

        Vec3d reflected = in.subtract(normal.multiply((1.0 + BOUNCE_RESTITUTION) * vn));
        this.setVelocity(reflected.x, this.getVelocity().y, reflected.z);
        this.velocityModified = true;
    }

    private Vec3d wallNormal(PlayerEntity player) {
        Box box = this.getBoundingBox().expand(0.5, 0, 0.5);
        Vec3d entityPos = this.getPos();
        Vec3d wallCenter = null;
        double closestDistSq = Double.MAX_VALUE;

        for (BlockPos pos : BlockPos.iterate(
                MathHelper.floor(box.minX), MathHelper.floor(box.minY), MathHelper.floor(box.minZ),
                MathHelper.ceil(box.maxX), MathHelper.ceil(box.maxY), MathHelper.ceil(box.maxZ))) {
            BlockState state = this.getWorld().getBlockState(pos);
            if (state.isAir() || state.isLiquid() || state.isReplaceable()) continue;
            double distSq = Vec3d.ofCenter(pos).squaredDistanceTo(entityPos);
            if (distSq < closestDistSq) {
                closestDistSq = distSq;
                wallCenter = Vec3d.ofCenter(pos);
            }
        }

        Vec3d bounceDir;
        if (wallCenter != null) {
            Vec3d fromWall = entityPos.subtract(wallCenter);
            double hLen = Math.sqrt(fromWall.x * fromWall.x + fromWall.z * fromWall.z);
            bounceDir = hLen > 0.001 ? new Vec3d(fromWall.x / hLen, 0, fromWall.z / hLen) : Vec3d.ZERO;
        } else {
            float yaw = player.getYaw() * (float) Math.PI / 180f;
            bounceDir = new Vec3d(-Math.sin(yaw), 0, Math.cos(yaw)).negate();
        }

        return bounceDir;
    }

    private boolean isAdjacentToHorizontalSolidBlock() {
        Box searchBox = this.getBoundingBox().expand(0.1, -0.1, 0.1);
        return BlockPos.stream(searchBox).anyMatch(pos -> {
            BlockState state = this.getWorld().getBlockState(pos);
            return !state.isAir() && !state.isLiquid() && !state.isReplaceable();
        });
    }

    public void startPhase(ServerPlayerEntity player, Tardis tardis) {
        if (phaseTicks > 0) return;

        if (phaseCooldown > 0) {
            int seconds = MathHelper.ceil(phaseCooldown / 20.0f);
            player.sendMessage(Text.translatable("tardis.message.phase.cooldown", seconds)
                    .formatted(Formatting.RED), true);
            player.playSound(AITSounds.ERROR, 1, 1);
            return;
        }

        player.playSound(AITSounds.CLOISTER, 1, 2);
        tardis.subsystems().<DurableSubSystem>get(SubSystem.Id.DEMAT).removeDurability(25);
        phaseTicks = PHASE_DURATION;
        phaseCooldown = PHASE_COOLDOWN;
    }

    public boolean isInsideBlock() {
        Box box = this.getBoundingBox().contract(0.05);
        return BlockPos.stream(box).anyMatch(pos -> {
            BlockState state = this.getWorld().getBlockState(pos);
            return !state.isAir() && !state.isLiquid() && !state.isReplaceable();
        });
    }

    private boolean intersectsPhaseProof(Box box) {
        return BlockPos.stream(box.contract(0.001)).anyMatch(pos ->
                this.getWorld().getBlockState(pos).isIn(AITTags.Blocks.PHASE_PROOF));
    }

    private Vec3d clampPhaseProofMovement(Vec3d movement) {
        Box box = this.getBoundingBox();
        double x = movement.x, y = movement.y, z = movement.z;

        if (x != 0 && this.intersectsPhaseProof(box.offset(x, 0, 0)))
            x = 0;
        if (y != 0 && this.intersectsPhaseProof(box.offset(0, y, 0)))
            y = 0;
        if (z != 0 && this.intersectsPhaseProof(box.offset(0, 0, z)))
            z = 0;

        return new Vec3d(x, y, z);
    }

    @Override
    public void move(MovementType type, Vec3d movement) {
        if (this.noClip && !movement.equals(Vec3d.ZERO)) {
            Vec3d clamped = this.clampPhaseProofMovement(movement);

            if (!clamped.equals(movement)) {
                super.move(type, clamped);
                this.setVelocity(this.getVelocity().multiply(
                        clamped.x == movement.x ? 1 : 0,
                        clamped.y == movement.y ? 1 : 0,
                        clamped.z == movement.z ? 1 : 0));
                return;
            }
        }

        super.move(type, movement);
    }

    private void applyPhasePunishment(ServerPlayerEntity player, Tardis tardis) {
        player.setInvisible(false);
        player.setInvulnerable(false);
        tardis.flight().flying().set(false);

        tardis.subsystems().<DurableSubSystem>get(SubSystem.Id.ENGINE).setDurability(0);
        tardis.subsystems().<DurableSubSystem>get(SubSystem.Id.GRAVITATIONAL).setDurability(0);
        tardis.subsystems().<DurableSubSystem>get(SubSystem.Id.DEMAT).setDurability(0);
        tardis.crash().addRepairTicks(4000);

        BlockPos current = this.getBlockPos();
        int surfaceY = this.getWorld().getTopY(Heightmap.Type.WORLD_SURFACE, current.getX(), current.getZ());
        BlockPos surfacePos = new BlockPos(current.getX(), surfaceY, current.getZ());

        tardis.travel().forcePosition(cached -> cached.pos(surfacePos));
        tardis.travel().destination(cached -> cached.pos(surfacePos));
        tardis.travel().forceRemat();

        TardisUtil.teleportInside(tardis.asServer(), player);

        this.discard();
    }

    private void finishLand(Tardis tardis, PlayerEntity player) {
        if (this.getWorld().isClient()) {
            FlightTardisClientHelper.finishLandClient();
            return;
        }

        if (!(player instanceof ServerPlayerEntity serverPlayer))
            return;

        tardis.flight().exitFlight(serverPlayer);
        tardis.travel().speed(0);

        if (this.interiorPos == null) {
            TardisUtil.teleportInside(tardis.asServer(), serverPlayer);
        } else {
            TardisUtil.teleportToInteriorPosition(tardis.asServer(), serverPlayer, this.interiorPos);
        }

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

        int tardisSpeed = this.tardis().get().travel().speed();

        boolean submerged = this.isSubmergedInWater();
        float waterThrust = submerged ? 3f : 1f;
        float f = controllingPlayer.sidewaysSpeed * tardisSpeed * waterThrust;
        float g = controllingPlayer.forwardSpeed * tardisSpeed * waterThrust;

        float speedVal = this.isPhasing() ? submerged ? 10f : 5f : submerged ? 30f : 10f;

        boolean canFall = this.tardis().get().travel().antigravs().get() || planetGravity;

        int maxSpeed = Math.max(1, this.tardis().get().travel().maxSpeed().get());
        float fallSpeed = MathHelper.lerp((float) tardisSpeed / maxSpeed, 2.0f, 0.1f);

        double v = ((LivingEntityAccessor) controllingPlayer).getJumping() ? speedVal :
                controllingPlayer.isSneaking() ? -speedVal :
                canFall ? 0.0f : -fallSpeed;

        if (v < 0 && this.isOnGround())
            return Vec3d.ZERO.add(0, -0.4f, 0);

        if (this.isPhasing() || this.isInsideBlock()) {
            f *= PHASE_SLOW_FACTOR;
            g *= PHASE_SLOW_FACTOR;
            if (v > 0)
                v *= PHASE_SLOW_FACTOR;
        }

        return new Vec3d(f, v, g);
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

    public boolean isCollidingOnGround() {
        return this.dataTracker.get(GROUND_COLLISION);
    }

    public boolean isPhasing() {
        return this.dataTracker.get(PHASING);
    }
}
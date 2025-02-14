package dev.amble.ait.core.entities;

import java.util.List;

import dev.amble.lib.data.CachedDirectedGlobalPos;
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
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.link.LinkableLivingEntity;
import dev.amble.ait.client.util.ClientShakeUtil;
import dev.amble.ait.core.AITEntityTypes;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.impl.DirectionControl;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.mixin.rwf.LivingEntityAccessor;

public class FlightTardisEntity extends LinkableLivingEntity implements JumpingMount {

    private static final List<ItemStack> EMPTY = List.of();
    private static final ItemStack AIR = new ItemStack(Items.AIR);
    public float speedPitch;
    private Vec3d lastVelocity;
    private BlockPos interiorPos;

    public FlightTardisEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);

        this.setInvulnerable(true);
        this.lastVelocity = Vec3d.ZERO;
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
        if (this.tardis() != null)
            if (this.getVelocity().horizontalLength() > 0.1f) {
                return this.getMovementSpeed() * (this.tardis().get().travel().speed() * 0.03f);
            }
        return super.getOffGroundSpeed();
    }

    @Override
    public void tick() {
        this.lastVelocity = this.getVelocity();
        this.setRotation(0, 0);
        super.tick();

        PlayerEntity player = this.getPlayer();

        if (!this.isLinked()) return;

        Tardis tardis = this.tardis().get();

        if (this.getWorld().isClient()) {
            MinecraftClient client = MinecraftClient.getInstance();
            Tardis tardisClient = this.tardis().get().asClient();
            if (client.player == this.getControllingPassenger()) {
                client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
                client.options.hudHidden = true;
                if (!this.groundCollision)
                    ClientShakeUtil.shake((float) (tardisClient.travel().speed() + this.getVelocity().horizontalLength()) / tardisClient.travel().maxSpeed().get());
            }
            return;
        }

        if (player == null)
            return;

        if (!player.isInvisible())
            player.setInvisible(true);

        if (!player.isInvulnerable())
            player.setInvulnerable(true);

        boolean onGround = this.isOnGround();

        tardis.flight().tickFlight((ServerPlayerEntity) player);

        if (tardis.door().isOpen()) {
            this.getWorld().getOtherEntities(this, this.getBoundingBox(), entity
                    -> !entity.isSpectator() && entity != player && entity instanceof LivingEntity).forEach(
                    entity -> TardisUtil.teleportInside(tardis, entity)
            );
        }

        if (player.isSneaking() && (onGround || tardis.travel().antigravs().get())) {
            //System.out.println(onGround);
            this.finishLand(tardis, player);
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
        if (this.getWorld().isClient()) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            client.options.hudHidden = false;
            return;
        }
        if (!(player instanceof ServerPlayerEntity serverPlayer))
            return;

        if (this.interiorPos == null) {
            TardisUtil.teleportInside(tardis, serverPlayer);
        } else {
            TardisUtil.teleportToInteriorPosition(tardis, serverPlayer, this.interiorPos);
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
        if (this.tardis() == null || !this.tardis().get().fuel().hasPower()) return new Vec3d(0, 0, 0);
        float f = controllingPlayer.sidewaysSpeed * this.tardis().get().travel().speed();
        float g = controllingPlayer.forwardSpeed * this.tardis().get().travel().speed();

        float speedVal = this.isSubmergedInWater() ? 30f : 10f;

        double v = ((LivingEntityAccessor) controllingPlayer).getJumping() ? speedVal :
                controllingPlayer.isSneaking() ? -speedVal :
                        this.tardis().get().travel().antigravs().get() ? 0.0f : f > 0 || g > 0 ? -0.5f : -2f;

        if (v < 0 && this.isOnGround())
            return Vec3d.ZERO.add(0, -0.4f, 0);
        Vec3d yourmom = new Vec3d(f, v, g);
        return yourmom;//return this.isOnGround() ? new Vec3d(0, 0, 0) : new Vec3d(f, v * 4f, g);
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

    public Vec3d lerpVelocity(float tickDelta) {
        return this.lastVelocity.lerp(this.getVelocity(), tickDelta);
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
        this.interiorPos = BlockPos.fromLong(nbt.getLong("InteriorPos"));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        BlockPos consolePos = this.tardis().get().getDesktop().getConsolePos().iterator().next();
        BlockPos pos = WorldUtil.findSafeXZ(this.tardis().get().asServer().getInteriorWorld(), consolePos, 2);
        nbt.putLong("InteriorPos", this.interiorPos == null
                ? pos == null
                ? new BlockPos(0, 0, 0).asLong() :
                pos.asLong() : this.interiorPos.asLong());
    }

    public static DefaultAttributeContainer.Builder createDummyAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 5);
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

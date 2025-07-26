package dev.amble.ait.module.gun.core.entity;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.module.gun.core.item.GunItems;
import dev.amble.ait.module.planet.core.util.ISpaceImmune;

public class StaserBoltEntity extends PersistentProjectileEntity implements ISpaceImmune {
    public StaserBoltEntity(EntityType<? extends StaserBoltEntity> entityType, World world) {
        super(GunEntityTypes.STASER_BOLT_ENTITY_TYPE, world);
    }

    private StaserBoltEntity(World world, double x, double y, double z) {
        super(GunEntityTypes.STASER_BOLT_ENTITY_TYPE, x, y, z, world);
    }

    private StaserBoltEntity(World world, LivingEntity shooter) {
        super(GunEntityTypes.STASER_BOLT_ENTITY_TYPE, shooter, world);
    }

    // this exists because I forgot how to do the constructors so the registry doesn't scream at me - Loqor
    public StaserBoltEntity createFromConstructor(World world, LivingEntity shooter) {
        return new StaserBoltEntity(world, shooter);
    }

    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(GunItems.STASER_BOLT_MAGAZINE);
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult result = (BlockHitResult) hitResult;
            Block block = this.getWorld().getBlockState(result.getBlockPos()).getBlock();
            if (block instanceof IceBlock || block instanceof LanternBlock || block instanceof TorchBlock || this.getWorld().getBlockState(result.getBlockPos()).isReplaceable() || block instanceof GlassBlock || block instanceof PaneBlock || block instanceof StainedGlassBlock) {
                this.getWorld().breakBlock(result.getBlockPos(), false);
            }
            this.getWorld().playSound(null, result.getBlockPos(), AITSounds.STASER, SoundCategory.BLOCKS, 0.25f, 0.5f);
            this.remove(RemovalReason.DISCARDED);
        }
        if (getOwner() instanceof PlayerEntity player) {
            if (hitResult.getType() == HitResult.Type.ENTITY && player.getMainHandStack().getItem() == GunItems.CULT_STASER_RIFLE) {
                this.setDamage(3d);
            } else {
                this.setDamage(1.5d);
            }
        }

        super.onCollision(hitResult);
    }

    @Override
    public void tick() {
        Vec3d vec3d2;
        VoxelShape voxelShape;
        super.tick();
        Vec3d vec3d = this.getVelocity();
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            double d = vec3d.horizontalLength();
            this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875));
            this.setPitch((float)(MathHelper.atan2(vec3d.y, d) * 57.2957763671875));
            this.prevYaw = this.getYaw();
            this.prevPitch = this.getPitch();
        }
        BlockPos blockPos = this.getBlockPos();
        BlockState blockState = this.getWorld().getBlockState(blockPos);
        if (!(blockState.isAir() || (voxelShape = blockState.getCollisionShape(this.getWorld(), blockPos)).isEmpty())) {
            vec3d2 = this.getPos();
            for (Box box : voxelShape.getBoundingBoxes()) {
                if (!box.offset(blockPos).contains(vec3d2)) continue;
                this.inGround = true;
                break;
            }
        }
        this.inGroundTime = 0;
        Vec3d vec3d3 = this.getPos();
        vec3d2 = vec3d3.add(vec3d);
        HitResult hitResult = this.getWorld().raycast(new RaycastContext(vec3d3, vec3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
        if (hitResult.getType() != HitResult.Type.MISS) {
            vec3d2 = hitResult.getPos();
        }
        while (!this.isRemoved()) {
            EntityHitResult entityHitResult = this.getEntityCollision(vec3d3, vec3d2);
            if (entityHitResult != null) {
                hitResult = entityHitResult;
            }
            if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult)hitResult).getEntity();
                Entity entity2 = this.getOwner();
                if (entity instanceof PlayerEntity && entity2 instanceof PlayerEntity && !((PlayerEntity)entity2).shouldDamagePlayer((PlayerEntity)entity)) {
                    hitResult = null;
                    entityHitResult = null;
                }
            }
            if (hitResult != null) {
                this.onCollision(hitResult);
                this.velocityDirty = true;
            }
            if (entityHitResult == null || this.getPierceLevel() <= 0) break;
            hitResult = null;
        }
        vec3d = this.getVelocity();
        double e = vec3d.x;
        double f = vec3d.y;
        double g = vec3d.z;
        double h = this.getX() + e;
        double j = this.getY() + f;
        double k = this.getZ() + g;
        double l = vec3d.horizontalLength();
        this.setYaw((float)(MathHelper.atan2(e, g) * 57.2957763671875));
        this.setPitch((float)(MathHelper.atan2(f, l) * 57.2957763671875));
        this.setPitch(PersistentProjectileEntity.updateRotation(this.prevPitch, this.getPitch()));
        this.setYaw(PersistentProjectileEntity.updateRotation(this.prevYaw, this.getYaw()));
        float m = 0.99f;
        if (this.isTouchingWater()) {
            for (int o = 0; o < 4; ++o) {
                this.getWorld().addParticle(ParticleTypes.BUBBLE, h - e * 0.25, j - f * 0.25, k - g * 0.25, e, f, g);
            }
            m = this.getDragInWater();
        }
        this.setVelocity(vec3d.multiply(m));
        if (!this.hasNoGravity()) {
            Vec3d vec3d4 = this.getVelocity();
            this.setVelocity(vec3d4.x, vec3d4.y - (double)0.05f, vec3d4.z);
        }
        this.setPosition(h, j, k);
        this.checkBlockCollision();

        if (this.age > 100) {
            this.discard();
        }
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.BLOCK_CANDLE_EXTINGUISH;
    }
}

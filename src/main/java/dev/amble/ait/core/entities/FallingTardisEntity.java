package dev.amble.ait.core.entities;

import java.util.function.Predicate;

import dev.amble.lib.util.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITDamageTypes;
import dev.amble.ait.core.AITEntityTypes;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.blocks.ExteriorBlock;
import dev.amble.ait.core.entities.base.LinkableDummyEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;
import dev.amble.ait.module.planet.core.util.ISpaceImmune;

public class FallingTardisEntity extends LinkableDummyEntity implements ISpaceImmune {

    private static final int HURT_MAX = 100;
    private static final float HURT_AMOUNT = 40f;

    public int timeFalling;

    @Nullable public NbtCompound blockEntityData;

    private BlockState state;



    public FallingTardisEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
    }

    private FallingTardisEntity(World world, Vec3d pos, BlockState state, Tardis tardis) {
        super(AITEntityTypes.FALLING_TARDIS_TYPE, world);

        this.intersectionChecked = true;
        this.state = state;

        this.link(tardis);
        this.setPosition(pos.subtract(0f, 0.5f, 0f));
        this.setVelocity(Vec3d.ZERO);
    }

    public static void spawnFromBlock(World world, BlockPos pos, BlockState state) {
        if (!(world.getBlockEntity(pos) instanceof ExteriorBlockEntity exterior))
            return;

        Tardis tardis = exterior.tardis().get();

        FallingTardisEntity fallingBlockEntity = new FallingTardisEntity(world, pos.toCenterPos(),
                state.contains(Properties.WATERLOGGED) ? state.with(Properties.WATERLOGGED, false) : state, tardis);

        world.setBlockState(pos, state.getFluidState().getBlockState(), 3);
        world.spawnEntity(fallingBlockEntity);
    }

    @Override
    protected MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }

    @Override
    protected void tickInVoid() {
        this.stopFalling(true);
    }


    @Override
    public void tick() {
        this.timeFalling++;

        if (!this.hasNoGravity())
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));

        this.move(MovementType.SELF, this.getVelocity());


        if (!this.isLinked())
            return;

        Tardis tardis = this.tardis().get();

        this.setVelocity(this.getVelocity().multiply(tardis.travel().isCrashing() ? 1.05f : 0.98f));

        if (this.getY() <= (double) this.getWorld().getBottomY() + 2)
            this.tickInVoid();

        if (this.getWorld().isClient())
            return;

        if (this.timeFalling % 20 == 0)
            tardis.getDesktop().getConsolePos().forEach(console -> this.getWorld().playSound(null, console,
                    SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.BLOCKS, 0.25F, 1.0F));

        Planet planet = PlanetRegistry.getInstance().get(this.getWorld());
        boolean canFall = this.tardis().get().travel().antigravs().get() || planet != null && planet.zeroGravity();
        if (canFall) {
            this.stopFalling(true);
            return;
        }

        BlockPos blockPos = this.getBlockPos();

        if (blockPos == null)
            return;

        tardis.travel().forcePosition(cached -> cached.pos(blockPos).world(this.getWorld().getRegistryKey()));

        if (this.isOnGround())
            this.stopFalling(false);
    }

    public void stopFalling(boolean antigravs) {
        Tardis tardis = this.tardis().get();
        TravelHandler travel = tardis.travel();

        if (tardis instanceof ClientTardis)
            return;

        if (antigravs)
            travel.antigravs().set(true);

        Block block = this.state.getBlock();
        BlockPos blockPos = this.getBlockPos();

        boolean isCrashing = travel.isCrashing();

        tardis.asServer().world().getPlayers().forEach(player -> {
            SoundEvent sound = isCrashing ? SoundEvents.ENTITY_GENERIC_EXPLODE : AITSounds.LAND_CRASH;
            float volume = isCrashing ? 1.0F : 3.0F;

            player.playSound(sound, volume, 1.0f);
        });

        if (isCrashing) {
            this.getWorld().createExplosion(this, null, new ExplosionBehavior() {
                        @Override
                        public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
                            MinecraftServer server = ServerLifecycleHooks.get();
                            if (server == null) return false;
                            if (!server.getGameRules().getBoolean(AITMod.TARDIS_GRIEFING)) return false;

                            return super.canDestroyBlock(explosion, world, pos, state, power);
                        }
                    }, this.getPos(), 10, true,
                    World.ExplosionSourceType.TNT);

            travel.setCrashing(false);
        }

        if (this.state.contains(Properties.WATERLOGGED)
                && this.getWorld().getFluidState(blockPos).getFluid() == Fluids.WATER)
            this.state = this.state.with(Properties.WATERLOGGED, true);

        if (block instanceof ExteriorBlock exterior)
            exterior.onLanding(tardis, (ServerWorld) this.getWorld(), blockPos);

        travel.placeExterior(false);
        this.discard();
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        int i = MathHelper.ceil(fallDistance - 1.0F);

        if (i >= 0) {
            Predicate<Entity> predicate = EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR
                    .and(EntityPredicates.VALID_LIVING_ENTITY);
            DamageSource damageSource2 = AITDamageTypes.of(getWorld(), AITDamageTypes.TARDIS_SQUASH_DAMAGE_TYPE);
            float f = (float) Math.min(MathHelper.floor((float) i * HURT_AMOUNT), HURT_MAX);

            this.getWorld().getOtherEntities(this, this.getBoundingBox(), predicate).forEach(entity -> {
                if (entity instanceof ShulkerEntity shulker) {
                    shulker.kill();
                }
                entity.damage(damageSource2, f);
            });
        }

        return false;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.put("BlockState", NbtHelper.fromBlockState(this.state));
        nbt.putInt("Time", this.timeFalling);

        if (this.blockEntityData != null)
            nbt.put("TileEntityData", this.blockEntityData);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        this.state = NbtHelper.toBlockState(this.getWorld().createCommandRegistryWrapper(RegistryKeys.BLOCK),
                nbt.getCompound("BlockState"));
        this.timeFalling = nbt.getInt("Time");

        if (nbt.contains("TileEntityData", 10))
            this.blockEntityData = nbt.getCompound("TileEntityData");

        if (this.state.isAir())
            this.state = AITBlocks.EXTERIOR_BLOCK.getDefaultState();
    }

    public BlockState getBlockState() {
        return this.state;
    }

    @Override
    protected Text getDefaultName() {
        return Text.translatable("entity.minecraft.falling_block_type", AITBlocks.EXTERIOR_BLOCK.getName());
    }

    @Override
    public boolean entityDataRequiresOperator() {
        return true;
    }

    @Override
    public boolean hasNoGravity() {
        return false;
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, Block.getRawIdFromState(this.getBlockState()));
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);

        this.state = Block.getStateFromRawId(packet.getEntityData());
        this.intersectionChecked = true;

        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();

        this.setPosition(d, e, f);
    }
}

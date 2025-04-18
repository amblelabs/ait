package dev.amble.ait.core.entities;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import dev.amble.ait.module.planet.core.util.ISpaceImmune;

public class BOTIPaintingEntity extends AbstractDecorationEntity implements ISpaceImmune {
    private static final int WIDTH = 48;
    private static final int HEIGHT = 32;

    public BOTIPaintingEntity(EntityType<? extends BOTIPaintingEntity> entityType, World world) {
        super(entityType, world);
    }

    private BOTIPaintingEntity(EntityType<? extends BOTIPaintingEntity> entityType, World world, BlockPos pos) {
        super(entityType, world, pos);
    }

    public static Optional<BOTIPaintingEntity> placePainting(EntityType<? extends BOTIPaintingEntity> entityType, World world, BlockPos pos, Direction facing) {
        BOTIPaintingEntity paintingEntity = new BOTIPaintingEntity(entityType, world, pos);

        paintingEntity.setFacing(facing);

        if (paintingEntity.canStayAttached()) {
            return Optional.of(paintingEntity);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putByte("facing", (byte) this.facing.getHorizontal());
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.facing = Direction.fromHorizontal(nbt.getByte("facing"));
        super.readCustomDataFromNbt(nbt);
        this.setFacing(this.facing);
    }

    @Override
    public int getWidthPixels() {
        return WIDTH;
    }

    @Override
    public int getHeightPixels() {
        return HEIGHT;
    }

    @Override
    public void onBreak(@Nullable Entity entity) {

    }

    @Override
    public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        this.setPosition(x, y, z);
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.setPosition(x, y, z);
    }

    @Override
    public Vec3d getSyncedPos() {
        return Vec3d.of(this.attachmentPos);
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, this.facing.getId(), this.getDecorationBlockPos());
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.setFacing(Direction.byId(packet.getEntityData()));
    }

    @Override
    public void onPlace() {
        this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0f, 1.0f);
    }
}

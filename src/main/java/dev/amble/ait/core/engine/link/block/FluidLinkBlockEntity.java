package dev.amble.ait.core.engine.link.block;

import java.util.HashMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.link.IFluidLink;
import dev.amble.ait.core.engine.link.IFluidSource;
import dev.amble.ait.core.engine.link.tracker.WorldFluidTracker;
import dev.amble.ait.core.util.SoundData;

public abstract class FluidLinkBlockEntity extends InteriorLinkableBlockEntity implements IFluidLink {
    private boolean powered = false;
    private IFluidLink last;
    private IFluidSource source;
    private BlockPos lastPos;

    protected FluidLinkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putBoolean("HasFluid", this.powered);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (nbt.contains("HasFluid")) {
            this.powered = nbt.getBoolean("HasFluid");
        }
    }

    @Nullable @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void onGainFluid() {
        if (this.hasWorld() && this.getGainPowerSound() != null) {
            this.getGainPowerSound().play((ServerWorld) this.getWorld(), this.getPos());
        }
    }

    @Override
    public void onLoseFluid() {
        if (this.hasWorld() && this.getLosePowerSound() != null) {
            this.getLosePowerSound().play((ServerWorld) this.getWorld(), this.getPos());
        }
    }
    protected SoundData getLosePowerSound() {
        return new SoundData(AITSounds.SLOT_IN, SoundCategory.BLOCKS, 0.1F, 0.75F);
    }
    protected SoundData getGainPowerSound() {
        return new SoundData(AITSounds.FLUID_LINK_CONNECT, SoundCategory.BLOCKS, 0.1F, 0.75F);
    }

    public boolean isPowered() {
        return this.powered;
    }
    private void updatePowered() {
        boolean before = this.powered;
        this.powered = this.source(true) != null && this.source(true).level() > 0;

        if (before != this.powered) {
            this.syncToWorld();

            if (this.powered) {
                this.onGainFluid();
            } else {
                this.onLoseFluid();
            }
        }
    }

    @Override
    public IFluidSource source(boolean search) {
        if (source == null && search) {
            if (this.last() != null) {
                this.setSource(this.last().source(true));
            } else {
                if (this.hasWorld()) {
                    this.search(this.getWorld(), this.getPos());
                }
            }
        }

        return this.source;
    }
    public IFluidSource source() {
        return this.source(true);
    }

    @Override
    public void setSource(IFluidSource source) {
        this.source = source;

        this.syncToWorld();
    }

    @Override
    public IFluidLink last() {
        return this.last;
    }

    @Override
    public void setLast(IFluidLink last) {
        this.last = last;
    }

    public BlockPos getLastPos() {
        return lastPos;
    }

    public void setLastPos(BlockPos lastPos) {
        this.lastPos = lastPos;
    }

    public void onBroken(World world, BlockPos pos) {
        if (world.isClient())
            return;
        if (this.isPowered())
            this.onLoseFluid();
        this.clear();
    }

    public void onPlaced(World world, BlockPos pos, @Nullable LivingEntity placer) {
        if (world.isClient())
            return;

        this.search(world, pos);
        this.updatePowered();
    }

    private void search(World world, BlockPos pos) {
        HashMap<Direction, IFluidLink> connections = WorldFluidTracker.getConnections((ServerWorld) world, pos, null);

        for (Direction dir : connections.keySet()) {
            IFluidLink i = connections.get(dir);
            if (i == this || isCircular(i)) continue;

            Direction inferredDirection = getDirectionFromPositions(pos, pos.offset(dir));
            if (inferredDirection != null && isParallel(inferredDirection, getDirectionFromPositions(pos, this.getLastPos()))) continue;


            if (i != null && i.source(false) != null) {
                this.setLast(i);
                this.setLastPos(pos.offset(dir));

                if (i instanceof IFluidSource) {
                    this.setSource((IFluidSource) i);
                }

                break;
            }
        }
    }
    private void clear(boolean sync) {
        this.setLast(null);
        this.setLastPos(null);
        this.setSource(null);
        if (sync) this.syncToWorld();
    }
    private void clear() {
        this.clear(true);
    }

    public void onNeighborUpdate(World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos) {
        if (world.isClient())
            return;

        if (this.last() == null) { // if last is null, search for new source
            IFluidLink link = WorldFluidTracker.query((ServerWorld) world, sourcePos);
            if (link != null && link.source(false) != null && !isCircular(link)) {
                Direction inferredDirection = getDirectionFromPositions(pos, sourcePos);
                if (inferredDirection != null && isParallel(inferredDirection, getDirectionFromPositions(pos, this.getLastPos()))) return;

                this.setLast(link);
                this.setLastPos(sourcePos);
                this.setSource(link.source(false));
            }
        }

        if (sourcePos.equals(this.getLastPos())) {
            this.onLastUpdate(world, pos);
        }

        this.updatePowered();
    }
    private void onLastUpdate(World world, BlockPos pos) {
        if ((world.getBlockState(this.getLastPos()).isAir()) || (last != null && last.source(false) == null)) { // is last in chain and is removed
            this.clear();
        }
    }
    private void syncToWorld() {
        if (!(this.hasWorld())) return;

        this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(this.getCachedState()));
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
        this.getWorld().updateNeighborsAlways(this.getPos(), this.getCachedState().getBlock());

        for (Direction dir : Direction.values()) {
            if (this.getWorld().getBlockEntity(this.getPos().offset(dir)) instanceof  FluidLinkBlockEntity be) {
                be.onNeighborUpdate(this.getWorld(), this.getPos().offset(dir), null, this.getPos());
            }
        }
    }

    private boolean isCircular(IFluidLink link) {
        // TODO MAKE THIS LESS LAGGY SINCE IT CRASHES THE GAME SUPER OFTEN - Loqor
        /*IFluidLink current = this;
        while (current != null) {
            if (current == link) {
                return true;
            }
            current = current.last();
        }
        return false;*/
        return false;
    }

    private Direction getDirectionFromPositions(BlockPos from, BlockPos to) {
        for (Direction direction : Direction.values()) {
            if (from.offset(direction).equals(to)) {
                return direction;
            }
        }
        return null;
    }
    private boolean isParallel(Direction dir1, Direction dir2) {
        // return dir1.getAxis() == dir2.getAxis();
        // todo
        return false;
    }
}

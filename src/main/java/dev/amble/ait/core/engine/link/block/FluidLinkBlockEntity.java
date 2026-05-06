package dev.amble.ait.core.engine.link.block;

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
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.link.IFluidLink;
import dev.amble.ait.core.engine.link.IFluidSource;
import dev.amble.ait.core.engine.link.tracker.FluidNetworkRebuilder;
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

    @Override
    public IFluidSource source(boolean search) {
        return this.source;
    }

    public IFluidSource source() {
        return this.source;
    }

    @Override
    public void setSource(IFluidSource source) {
        this.source = source;
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

    /**
     * Applied by {@link FluidNetworkRebuilder} during the per-tick rebuild. Writes the new
     * upstream pointer / source / powered state and fires gain/lose callbacks on transitions.
     * Cables and subsystems must not mutate these fields outside this method.
     */
    public void applyNetworkAssignment(@Nullable IFluidSource newSource, @Nullable IFluidLink newLast,
                                       @Nullable BlockPos newLastPos, boolean newPowered) {
        boolean changed = this.source != newSource || this.last != newLast
                || (this.lastPos == null ? newLastPos != null : !this.lastPos.equals(newLastPos));
        boolean wasPowered = this.powered;

        this.source = newSource;
        this.last = newLast;
        this.lastPos = newLastPos;
        this.powered = newPowered;

        if (wasPowered != newPowered) {
            if (newPowered) {
                this.onGainFluid();
            } else {
                this.onLoseFluid();
            }
        }

        if (changed || wasPowered != newPowered) {
            this.broadcastState();
        }
    }

    private void broadcastState() {
        if (!this.hasWorld()) return;

        this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(this.getCachedState()));
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
    }

    public void onBroken(World world, BlockPos pos) {
        if (world.isClient())
            return;
        if (this.isPowered())
            this.onLoseFluid();

        this.source = null;
        this.last = null;
        this.lastPos = null;
        this.powered = false;

        FluidNetworkRebuilder.markBrokenAt((ServerWorld) world, pos);
    }

    public void onPlaced(World world, BlockPos pos, @Nullable LivingEntity placer) {
        if (world.isClient())
            return;

        FluidNetworkRebuilder.markDirty((ServerWorld) world, pos);
    }

    public void onNeighborUpdate(World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos) {
        if (world.isClient())
            return;

        FluidNetworkRebuilder.markDirty((ServerWorld) world, pos);
    }
}

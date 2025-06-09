package dev.amble.ait.api.tardis.link.v2.block;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.UUID;

public abstract class AbstractLinkableBlockEntity extends BlockEntity implements Linkable {

    protected TardisRef ref;

    public AbstractLinkableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public TardisRef tardis() {
        return ref;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        if (this.ref != null && this.ref.getId() != null)
            nbt.putUuid("tardis", this.ref.getId());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        NbtElement id = nbt.get("tardis");

        if (id == null)
            return;

        this.ref = TardisRef.createAs(this, NbtHelper.toUuid(id));

        if (this.world == null)
            return;

        this.onLinked();
    }

    @Override
    public void markRemoved() {
        super.markRemoved();

        if (this.ref == null || this.ref.isEmpty())
            return;

        if (!(this.world instanceof ServerWorld serverWorld))
            return;

        ServerTardisManager.getInstance().unmark(serverWorld, (ServerTardis) this.ref.get(), new ChunkPos(this.pos));
    }

    @Override
    public void link(Tardis tardis) {
        this.ref = TardisRef.createAs(this, tardis);
        this.handleLink();
    }

    @Override
    public void link(UUID id) {
        this.ref = TardisRef.createAs(this, id);
        this.handleLink();
    }

    private void mark() {
        if (this.world instanceof ServerWorld serverWorld)
            ServerTardisManager.getInstance().mark(serverWorld, (ServerTardis) this.tardis().get(),
                    new ChunkPos(this.pos));
    }

    private void handleLink() {
        this.mark();
        this.onLinked();

        this.sync();
        this.markDirty();
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        if (this.isLinked())
            this.mark();

        return createNbt();
    }

    protected void sync() {
        if (this.world != null && this.world.getChunkManager() instanceof ServerChunkManager chunkManager)
            chunkManager.markForUpdate(this.pos);
    }
}

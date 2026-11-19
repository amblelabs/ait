package dev.amble.ait.core.blockentities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.tardis.comms.RotaryPhones;
import dev.amble.ait.core.world.TardisServerWorld;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.AnimationState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RotaryPhoneBlockEntity extends BlockEntity {

    public static final int MAX_RECENTS = 6;

    public record Recent(String number, String label, boolean outgoing) {
    }

    public final AnimationState ringState = new AnimationState();

    @Nullable private UUID line;
    private final List<Recent> recents = new ArrayList<>();
    private boolean ringing;
    private boolean lifted;
    private int age;

    public RotaryPhoneBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.ROTARY_PHONE_BLOCK_ENTITY_TYPE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, RotaryPhoneBlockEntity phone) {
        if (world instanceof TardisServerWorld interior) {
            RotaryPhones.rememberExtension(interior, pos);
            return;
        }

        if (!world.isClient())
            return;

        phone.age++;

        if (phone.ringing)
            phone.ringState.startIfNotRunning(phone.age);
        else
            phone.ringState.stop();
    }

    @Override
    public void markRemoved() {
        super.markRemoved();

        if (this.world instanceof TardisServerWorld interior)
            RotaryPhones.forgetExtension(interior, this.pos);
    }

    public int getAge() {
        return this.age;
    }

    @Nullable public UUID line() {
        return this.line;
    }

    public void setLine(@Nullable UUID line) {
        this.line = line;
        this.recents.clear();
        this.markDirty();
    }

    public List<Recent> recents() {
        return this.recents;
    }

    public void addRecent(Recent recent) {
        this.recents.removeIf(existing -> existing.number().equals(recent.number()));
        this.recents.add(0, recent);

        while (this.recents.size() > MAX_RECENTS)
            this.recents.remove(this.recents.size() - 1);

        this.markDirty();
    }

    public void removeRecent(String number) {
        if (this.recents.removeIf(recent -> recent.number().equals(number)))
            this.markDirty();
    }

    public boolean isRinging() {
        return this.ringing;
    }

    public boolean isLifted() {
        return this.lifted;
    }

    public void setDisplay(boolean ringing, boolean lifted) {
        if (this.ringing == ringing && this.lifted == lifted)
            return;

        this.ringing = ringing;
        this.lifted = lifted;
        this.sync();
    }

    private void sync() {
        this.markDirty();

        if (this.world != null)
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        if (this.line != null)
            nbt.putUuid("Line", this.line);

        NbtList list = new NbtList();

        for (Recent recent : this.recents) {
            NbtCompound tag = new NbtCompound();

            tag.putString("Number", recent.number());
            tag.putString("Label", recent.label());
            tag.putBoolean("Outgoing", recent.outgoing());
            list.add(tag);
        }

        nbt.put("Recents", list);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.line = nbt.containsUuid("Line") ? nbt.getUuid("Line") : null;
        this.recents.clear();

        for (NbtElement element : nbt.getList("Recents", NbtElement.COMPOUND_TYPE)) {
            NbtCompound tag = (NbtCompound) element;
            this.recents.add(new Recent(tag.getString("Number"), tag.getString("Label"), tag.getBoolean("Outgoing")));
        }

        if (nbt.contains("Ringing"))
            this.ringing = nbt.getBoolean("Ringing");

        if (nbt.contains("Lifted"))
            this.lifted = nbt.getBoolean("Lifted");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();

        nbt.putBoolean("Ringing", this.ringing);
        nbt.putBoolean("Lifted", this.lifted);

        return nbt;
    }

    @Nullable @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}

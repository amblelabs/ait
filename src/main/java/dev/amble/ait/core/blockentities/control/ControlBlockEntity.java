package dev.amble.ait.core.blockentities.control;

import java.util.Optional;

import dev.drtheo.scheduler.api.Scheduler;
import dev.drtheo.scheduler.api.TimeUnit;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.api.link.v2.TardisRef;
import dev.amble.ait.api.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.blocks.control.RedstoneControlBlock;
import dev.amble.ait.core.item.control.ControlBlockItem;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.registry.impl.ControlRegistry;

public abstract class ControlBlockEntity extends InteriorLinkableBlockEntity {

    private Control control;
    private boolean onDelay = false;

    protected ControlBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        if (this.getControl() != null)
            nbt.putString(ControlBlockItem.CONTROL_ID_KEY, this.getControl().getId().toString());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (nbt.contains(ControlBlockItem.CONTROL_ID_KEY))
            this.setControlId(new Identifier(nbt.getString(ControlBlockItem.CONTROL_ID_KEY)));
    }

    /**
     * Gets the control Can be null if this hasnt been linked
     *
     * @return control
     */
    public Control getControl() {
        return this.control;
    }

    public void setControlId(Identifier id) {
        Optional<Control> found = ControlRegistry.fromId(id);

        if (found.isEmpty())
            return;

        this.control = found.get();
    }

    public boolean run(ServerPlayerEntity user, boolean isMine) {
        if (this.getControl() == null)
            return false;

        TardisRef found = this.tardis();

        if (found.isEmpty())
            return false;

        if (!(found.get() instanceof ServerTardis tardis))
            return false;

        if (!this.control.canRun(tardis, user))
            return false;

        if (this.control.shouldHaveDelay(tardis) && !this.onDelay)
            this.createDelay(this.control.getDelayLength());

        this.getWorld().playSound(null, pos, this.control.getSound(), SoundCategory.BLOCKS, 0.7f, 1f);
        return this.control.handleRun(tardis, user, user.getServerWorld(), this.pos, isMine);
    }

    public boolean run(ServerPlayerEntity user, RedstoneControlBlock.Mode mode) {
        boolean isMine = mode == RedstoneControlBlock.Mode.PUNCH;
        return this.run(user, isMine);
    }

    public void createDelay(long ticks) {
        this.onDelay = true;

        Scheduler.get().runTaskLater(() -> this.onDelay = false, TimeUnit.TICKS, ticks);
    }
}

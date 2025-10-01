package dev.amble.ait.core.blockentities.control;

import java.util.Optional;

import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.blocks.control.RedstoneControlBlock;
import dev.amble.ait.core.item.control.ControlBlockItem;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.data.schema.console.ConsoleTypeSchema;
import dev.amble.ait.registry.impl.ControlRegistry;
import dev.amble.ait.registry.impl.console.ConsoleRegistry;

public abstract class ControlBlockEntity extends InteriorLinkableBlockEntity {

    private Control control;
    private ConsoleTypeSchema consoleType;
    private boolean onDelay = false;

    protected ControlBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        if (this.getControl() != null)
            nbt.putString(ControlBlockItem.CONTROL_ID_KEY, this.getControl().id().toString());

        if (this.getConsoleType() != null)
            nbt.putString(ControlBlockItem.CONSOLE_TYPE_ID_KEY, this.getConsoleType().id().toString());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (nbt.contains(ControlBlockItem.CONTROL_ID_KEY))
            this.setControlId(new Identifier(nbt.getString(ControlBlockItem.CONTROL_ID_KEY)));

        if (nbt.contains(ControlBlockItem.CONSOLE_TYPE_ID_KEY))
            this.setConsoleId(new Identifier(nbt.getString(ControlBlockItem.CONSOLE_TYPE_ID_KEY)));
    }

    /**
     * Gets the control Can be null if this hasnt been linked
     *
     * @return control
     */
    public Control getControl() {
        return this.control;
    }

    public ConsoleTypeSchema getConsoleType() {
        if (this.consoleType == null) {
            // default
            this.consoleType = ConsoleRegistry.HARTNELL;
        }

        return this.consoleType;
    }

    public void setControlId(Identifier id) {
        Optional<Control> found = ControlRegistry.fromId(id);

        if (found.isEmpty())
            return;

        this.control = found.get();
    }

    public void setConsoleId(Identifier id) {
        Optional<ConsoleTypeSchema> found = ConsoleRegistry.getInstance().getOptional(id);

        if (found.isEmpty())
            return;

        this.consoleType = found.get();
    }

    public boolean run(ServerPlayerEntity user, boolean isMine) {
        if (this.getControl() == null || this.onDelay)
            return false;

        TardisRef found = this.tardis();

        if (!(found.get() instanceof ServerTardis tardis))
            return false;

        if (!this.control.canRun(tardis, user))
            return false;

        if (this.control.shouldHaveDelay(tardis) && !this.onDelay)
            this.createDelay(this.control.getDelayLength(tardis));

        Control.Result result = this.control.handleRun(tardis, user, user.getServerWorld(), this.pos, isMine);
        this.getWorld().playSound(null, pos, this.control.getSound(this.getConsoleType(), result), SoundCategory.BLOCKS, 0.7f, 1f);

        return result.isSuccess();
    }

    public boolean run(ServerPlayerEntity user, RedstoneControlBlock.Mode mode) {
        return this.run(user, mode == RedstoneControlBlock.Mode.PUNCH);
    }

    public void createDelay(long ticks) {
        this.onDelay = true;

        Scheduler.get().runTaskLater(() -> this.onDelay = false, TaskStage.END_SERVER_TICK, TimeUnit.TICKS, ticks);
    }
}

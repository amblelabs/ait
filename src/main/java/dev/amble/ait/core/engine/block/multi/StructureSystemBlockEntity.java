package dev.amble.ait.core.engine.block.multi;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.block.SubSystemBlockEntity;

public abstract class StructureSystemBlockEntity extends SubSystemBlockEntity {
    protected StructureSystemBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, SubSystem.IdLike id) {
        super(type, pos, state, id);
    }

    public boolean isStructureComplete() {
        return this.isStructureComplete(this.getWorld(), this.getPos());
    }
    public boolean isStructureComplete(World world, BlockPos pos) {
        return this.getStructure().check(world, pos);
    }
    protected boolean shouldRefresh(ServerWorld world, BlockPos pos) {
        return world.getServer().getTicks() % 40 == 0; // every 2 seconds
    }
    protected abstract MultiBlockStructure getStructure();

    @Override
    public void onGainFluid() {
        if (this.hasWorld() && this.isStructureComplete()) {
            super.onGainFluid();
        }
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        super.tick(world, pos, state);

        if (world.isClient()) return;

        if (this.shouldRefresh((ServerWorld) world, pos)) {
            this.processStructure();
        }
    }

    protected void processStructure() {
        boolean powered = this.isPowered();
        if (!powered) return;

        boolean complete = this.isStructureComplete();
        boolean broken = (this.system() instanceof DurableSubSystem durable) && durable.isBroken();
        boolean enabled = this.system().isEnabled();

        if (!complete && enabled) {
            this.onLoseFluid();
        }
        if (complete && !enabled && !broken) {
            this.onGainFluid();
        }
    }
}

package dev.amble.ait.core.blockentities;



import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.block.SubSystemBlockEntity;
import dev.amble.ait.core.engine.link.IFluidLink;
import dev.amble.ait.core.engine.link.IFluidSource;
import dev.amble.ait.core.engine.link.ITardisSource;
import dev.amble.ait.core.tardis.Tardis;

public class EngineBlockEntity extends SubSystemBlockEntity implements ITardisSource {
    public EngineBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.ENGINE_BLOCK_ENTITY_TYPE, pos, state, SubSystem.Id.ENGINE);

        if (!this.hasWorld()) return;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, @Nullable LivingEntity placer) {
        super.onPlaced(world, pos, placer);
        if (world.isClient())
            return;

        this.tardis().ifPresent(tardis -> tardis.subsystems().engine().setEnabled(true));
    }

    @Override
    public void onBroken(World world, BlockPos pos) {
        super.onBroken(world, pos);

        this.onLoseFluid(); // always.
    }

    @Override
    public Tardis getTardisForFluid() {
        return this.tardis().get();
    }

    @Override
    public void setSource(IFluidSource source) {

    }

    @Override
    public void setLast(IFluidLink last) {

    }

    @Override
    public IFluidSource source(boolean search) {
        return this;
    }

    @Override
    public IFluidLink last() {
        return this;
    }

    @Override
    public BlockPos getLastPos() {
        return this.getPos();
    }
}

package dev.amble.ait.api.tardis.link.v2.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.core.world.TardisServerWorld;

public abstract class InteriorLinkableBlockEntity extends AbstractLinkableBlockEntity {

    public InteriorLinkableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);

        if (this.ref != null)
            return;

        if (world instanceof TardisServerWorld tardisWorld)
            this.link(tardisWorld.getTardis());
    }
}

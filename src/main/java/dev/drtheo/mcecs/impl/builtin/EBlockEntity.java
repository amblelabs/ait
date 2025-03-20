package dev.drtheo.mcecs.impl.builtin;

import dev.drtheo.mcecs.MCECS;
import dev.drtheo.mcecs.base.EEntity;
import dev.drtheo.mcecs.impl.EntityManager;
import dev.drtheo.mcecs.impl.api.Owned;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Unique;

public class EBlockEntity extends BlockEntity implements Owned {

    @Unique
    private EEntity mcecs$entity;

    public EBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);

        if (this.mcecs$entity != null) {
            MCECS.LOGGER.error("Tried to re-initialize a block entity component");
            return;
        }

        this.mcecs$entity = EntityManager.create(
                new BlockEntityComponent(this)
        );
    }

    @Override
    public EEntity mcecs$entity() {
        return mcecs$entity;
    }
}

package loqor.ait.core.world;

import dev.drtheo.blockqueue.QueuedStructureTemplate;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.random.Random;

import loqor.ait.api.link.v2.block.InteriorLinkableBlockEntity;
import loqor.ait.core.tardis.ServerTardis;

public class QueuedTardisStructureTemplate extends QueuedStructureTemplate {

    private final ServerTardis tardis;

    public QueuedTardisStructureTemplate(StructureTemplate template, ServerTardis tardis) {
        super(template);
        this.tardis = tardis;
    }

    @Override
    protected void readNbt(BlockEntity blockEntity, NbtCompound nbt, Random random) {
        if (blockEntity instanceof InteriorLinkableBlockEntity linkable) {
            /*
             It's faster to remove the tardis from the nbt
             than make it do id -> string -> map -> string -> id
             */
            nbt.remove("tardis");
            linkable.link(tardis);
        }

        super.readNbt(blockEntity, nbt, random);
    }
}

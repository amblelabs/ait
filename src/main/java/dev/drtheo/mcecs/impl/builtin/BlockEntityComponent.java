package dev.drtheo.mcecs.impl.builtin;

import dev.drtheo.mcecs.MCECS;
import dev.drtheo.mcecs.base.comp.CompUid;
import dev.drtheo.mcecs.base.comp.Component;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.AttachmentTypeImpl;
import net.minecraft.block.entity.BlockEntity;

public class BlockEntityComponent extends Component<BlockEntityComponent> {

    public static final AttachmentType<BlockEntityComponent> SELF = AttachmentRegistry.create(MCECS.id("block_entity"));
    public static final CompUid<BlockEntityComponent> ID = new CompUid<>(BlockEntityComponent.class);

    private final BlockEntity block;

    public BlockEntityComponent(BlockEntity block) {
        this.block = block;

        block.getAttachedOrCreate(SELF);
    }

    public BlockEntity get() {
        return block;
    }

    @Override
    public CompUid<BlockEntityComponent> getUid() {
        return ID;
    }
}

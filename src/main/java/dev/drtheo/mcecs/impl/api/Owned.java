package dev.drtheo.mcecs.impl.api;

import dev.drtheo.mcecs.MCECS;
import dev.drtheo.mcecs.base.EEntity;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public interface Owned {

    AttachmentType<Boolean> OWNED = AttachmentRegistry.create(MCECS.id("owned"));

    EEntity mcecs$entity();
}

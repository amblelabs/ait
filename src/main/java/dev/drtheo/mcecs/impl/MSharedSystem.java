package dev.drtheo.mcecs.impl;

import dev.drtheo.mcecs.base.comp.Component;
import dev.drtheo.mcecs.base.event.LocalEvent;
import net.minecraft.util.Identifier;

public abstract class MSharedSystem extends MSystem {

    protected MSharedSystem(Identifier id) {
        super(id);
    }
}

package dev.drtheo.mcecs.impl;

import dev.drtheo.mcecs.base.EEntity;
import dev.drtheo.mcecs.base.comp.CompUid;
import dev.drtheo.mcecs.base.comp.Component;

public record MEntity(int i) implements EEntity {

    @Override
    public int index() {
        return i;
    }
}

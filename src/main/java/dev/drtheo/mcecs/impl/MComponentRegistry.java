package dev.drtheo.mcecs.impl;

import dev.drtheo.mcecs.base.comp.Component;
import dev.drtheo.mcecs.base.comp.ComponentRegistry;
import dev.drtheo.mcecs.base.comp.DynamicComponentRegistry;

public class MComponentRegistry extends DynamicComponentRegistry {

    public static final MComponentRegistry INSTANCE = new MComponentRegistry();
}

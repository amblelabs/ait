package dev.amble.ait.mixin.lookup;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.entity.SectionedEntityCache;
import net.minecraft.world.entity.SimpleEntityLookup;

@Mixin(SimpleEntityLookup.class)
public interface SimpleEntityLookupAccessor<T extends EntityLike> {

    @Accessor("cache")
    SectionedEntityCache<T> getCache();
}

package dev.amble.ait.mixin.lookup;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.collection.TypeFilterableList;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.entity.EntityTrackingSection;

@Mixin(EntityTrackingSection.class)
public interface EntityTrackingSectionAccessor<T extends EntityLike> {

    @Accessor("collection")
    TypeFilterableList<T> getCollection();
}

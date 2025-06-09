package dev.amble.ait.mixin.lookup;

import net.minecraft.util.collection.TypeFilterableList;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.entity.EntityTrackingSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityTrackingSection.class)
public interface EntityTrackingSectionAccessor<T extends EntityLike> {

    @Accessor("collection")
    TypeFilterableList<T> getCollection();
}

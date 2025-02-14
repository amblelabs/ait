package dev.amble.ait.data.schema.exterior.variant.dalek_mod;


import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.animation.ExteriorAnimation;
import dev.amble.ait.core.tardis.animation.PulsatingAnimation;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.DalekModDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.DalekModCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;


public abstract class DalekModVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/dalek_mod/dalek_mod_";

    protected DalekModVariant(Integer number) {
        super(DalekModCategory.REFERENCE, AITMod.id("exterior/dalek_mod/" + number),
                new Loyalty(Loyalty.Type.OWNER));
    }

    @Override
    public ExteriorAnimation animation(ExteriorBlockEntity exterior) {
        return new PulsatingAnimation(exterior);
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.REGISTRY.get(DalekModDoorVariant.REFERENCE);
    }


    @Override
    public boolean hasPortals() {
        return false;
    }

}

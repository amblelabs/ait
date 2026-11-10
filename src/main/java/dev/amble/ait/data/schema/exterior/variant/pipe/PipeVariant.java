package dev.amble.ait.data.schema.exterior.variant.pipe;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.PipeDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.PipeCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;

import net.minecraft.util.math.Vec3d;

public abstract class PipeVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/pipe/pipe_";

    protected PipeVariant(String name) {
        super(PipeCategory.REFERENCE, AITMod.id("exterior/pipe/" + name),
                new Loyalty(Loyalty.Type.NEUTRAL));
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.getInstance().get(PipeDoorVariant.REFERENCE);
    }


    @Override
    public boolean hasPortals() {
        return false;
    }

    @Override
    public Vec3d seatTranslations() {
           return new Vec3d(0.5, 1, 0.5);
       }
   }

package dev.amble.ait.data.schema.exterior.variant.classic;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.ClassicDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.ClassicCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;
import org.jetbrains.annotations.Nullable;

// a useful class for creating tardim variants as they all have the same filepath you know
public abstract class ClassicBoxVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/classic/classic_";

    protected ClassicBoxVariant(String name, String modId) {
        super(ClassicCategory.REFERENCE, new Identifier(modId, "exterior/classic/" + name),
                new Loyalty(Loyalty.Type.OWNER));
    }

    protected ClassicBoxVariant(String name) {
        this(name, AITMod.MOD_ID);
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.getInstance().get(ClassicDoorVariant.REFERENCE);
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public @Nullable Vec3d getPortalPosition() {
        return new Vec3d(0, 0.07, -0.599);
    }

    @Override
    public Vec3d seatTranslations() {
        return new Vec3d(0.5, 1, 0.5);
    }

    @Override
    public double portalHeight() {
        return 2.25d;
    }

    @Override
    public double portalWidth() {
        return 1.0625d;
    }
}

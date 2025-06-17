package dev.amble.ait.data.schema.exterior.variant.exclusive.persona;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.animation.ExteriorAnimation;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.ExclusiveCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;
import net.minecraft.util.math.Vec3d;

public class VelvetDoorVariant extends ExteriorVariantSchema {
	public VelvetDoorVariant() {
		super(ExclusiveCategory.REFERENCE, AITMod.id("exterior/exclusive/persona"));
	}

	@Override
	public Vec3d seatTranslations() {
		return Vec3d.ZERO;
	}

	@Override
	public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
		return super.adjustPortalPos(pos, direction).add(0, -0.3, 0);
	}

	@Override
	public DoorSchema door() {
		return DoorRegistry.PERSONA;
	}
}

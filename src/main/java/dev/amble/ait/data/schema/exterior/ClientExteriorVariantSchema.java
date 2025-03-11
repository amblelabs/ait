package dev.amble.ait.data.schema.exterior;

import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.registry.v2.AITClientRegistries;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientExteriorVariantSchema extends ExteriorVariantSchema {

    private final ExteriorModel model;
    private final DoorSchema door;

    protected ClientExteriorVariantSchema(ExteriorVariantSchema parent) {
        super(parent.id, parent.category, parent.modelId, parent.doorId,
                parent.animation, parent.texture, parent.emission, parent.loyalty,
                parent.overrides, parent.seatTranslations, parent.sonicTransform,
                parent.hasPortal, parent.portalSize, parent.straightPortalOffset,
                parent.diagonalPortalOffset);

        this.model = AITClientRegistries.EXTERIOR_MODEL.get(parent.modelId);
        this.door = AITClientRegistries.DOOR.get(parent.doorId);
    }

    public ExteriorModel model() {
        return model;
    }

    public DoorSchema door() {
        return door;
    }
}

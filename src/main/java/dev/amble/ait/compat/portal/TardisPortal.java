package dev.amble.ait.compat.portal;

import dev.amble.ait.client.AITModClient;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import qouteall.imm_ptl.core.portal.Portal;

public class TardisPortal extends Portal {
    public static final EntityType<TardisPortal> entityType = createPortalEntityType(TardisPortal::new);

	public TardisPortal(World world) {
		this(entityType, world);
	}

    private TardisPortal(EntityType<TardisPortal> type, World world) {
        super(type, world);
    }

    @Override
    public boolean isVisible() {
        return super.isVisible() && AITModClient.CONFIG.allowPortalsBoti;
    }
}

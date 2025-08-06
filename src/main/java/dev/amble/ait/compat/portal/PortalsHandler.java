package dev.amble.ait.compat.portal;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.TardisComponentRegistry;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedBlockPos;
import net.minecraft.util.Pair;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import qouteall.imm_ptl.core.api.PortalAPI;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.q_misc_util.my_util.DQuaternion;

public class PortalsHandler extends KeyedTardisComponent {
	@Nullable
	@Exclude
	private TardisPortal interior;	static final IdLike ID = new AbstractId<>("PORTALS", PortalsHandler::new, PortalsHandler.class);
	@Nullable
	@Exclude
	private TardisPortal exterior;
	public PortalsHandler() {
		super(ID);
	}

	public static void init() {
		TardisComponentRegistry.getInstance().register(ID);

		TardisEvents.DOOR_OPEN.register((tdis) -> {
			PortalsHandler handler = tdis.handler(ID);

			handler.generatePortals();
		});

		TardisEvents.DOOR_CLOSE.register((tdis) -> {
			PortalsHandler handler = tdis.handler(ID);

			handler.removePortals();
		});

		TardisEvents.DOOR_MOVE.register((tdis, newPos, oldPos) -> {
			PortalsHandler handler = tdis.handler(ID);

			handler.removePortals();

			if (tdis.door().isOpen()) handler.generatePortals();
		});

		TardisEvents.EXTERIOR_CHANGE.register((tdis) -> {
			PortalsHandler handler = tdis.handler(ID);

			handler.removePortals();

			if (tdis.door().isOpen()) handler.generatePortals();
		});
	}

	public @Nullable TardisPortal getExterior() {
		return exterior;
	}

	public @Nullable TardisPortal getInterior() {
		return interior;
	}

	private Pair<TardisPortal, TardisPortal> generatePortals() {
		CachedDirectedGlobalPos exteriorPos = this.tardis().travel().position();

		DirectedBlockPos tempPos = this.tardis().getDesktop().getDoorPos();
		CachedDirectedGlobalPos interiorPos = CachedDirectedGlobalPos.create(tardis().asServer().world(), tempPos.getPos(), tempPos.getRotation());

		removePortals();

		this.exterior = createPortal(exteriorPos, interiorPos, true);
		this.interior = createPortal(interiorPos, exteriorPos, false);

		return new Pair<>(exterior, interior);
	}

	private TardisPortal createPortal(CachedDirectedGlobalPos from, CachedDirectedGlobalPos to, boolean exterior) {
		Vec3d fromAdjusted = adjustPosition(exterior, from);
		Vec3d toAdjusted = adjustPosition(exterior, to);

		TardisPortal portal = new TardisPortal(from.getWorld());
		portal.link(this.tardis());

		ExteriorVariantSchema variant = this.tardis().getExterior().getVariant();
		portal.setOrientationAndSize(
				new Vec3d(1, 0, 0),
				new Vec3d(0, 1, 0),
				variant.portalWidth(),
				variant.portalHeight()
		);

		DQuaternion quat = DQuaternion.rotationByDegrees(new Vec3d(0, -1, 0),
				RotationPropertyHelper.toDegrees(from.getRotation()) + (exterior ? 180 : 0));
		DQuaternion toQuat = DQuaternion.rotationByDegrees(new Vec3d(0, -1, 0),
				RotationPropertyHelper.toDegrees(to.getRotation()) + (exterior ? 0 : 180));

		PortalAPI.setPortalOrientationQuaternion(portal, quat);
		portal.setOtherSideOrientation(toQuat);

		portal.setOriginPos(
				new Vec3d(fromAdjusted.getX() + 0.5, fromAdjusted.getY() + 1.2, fromAdjusted.getZ() + 0.5));
		portal.setDestinationDimension(to.getWorld().getRegistryKey());
		portal.setDestination(
				new Vec3d(toAdjusted.getX() + 0.5, toAdjusted.getY() + 1.2, toAdjusted.getZ() + 0.5));

		portal.renderingMergable = true;
		portal.getWorld().spawnEntity(portal);

		return portal;
	}

	private Vec3d adjustPosition(boolean exterior, CachedDirectedGlobalPos pos) {
		float multiplier = exterior ? 0.1F : 0.05F;

		ExteriorVariantSchema variant = this.tardis().getExterior().getVariant();
		Vec3d vec = pos.getPos().toCenterPos().subtract(0.5, 0.5, 0.5).add(Vec3d.of(pos.getVector()).multiply(-multiplier, 1, multiplier));

		if (exterior) {
			return variant.adjustPortalPos(vec, pos.getRotation());
		}

		return variant.door().adjustPortalPos(vec, pos.getRotationDirection());
	}

	private void removePortals() {
		if (interior != null) {
			PortalManipulation.removeConnectedPortals(interior, (p) -> {
			});
			interior.discard();
		}

		if (exterior != null) {
			PortalManipulation.removeConnectedPortals(exterior, (p) -> {
			});
			exterior.discard();
		}
	}


}

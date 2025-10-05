package dev.amble.ait.compat.portal;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.AITDimensions;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.util.EntityRef;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.TardisComponentRegistry;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedBlockPos;
import dev.amble.lib.data.DirectedGlobalPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import qouteall.imm_ptl.core.api.PortalAPI;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.imm_ptl.core.render.PortalEntityRenderer;
import qouteall.q_misc_util.my_util.DQuaternion;

public class PortalsHandler extends KeyedTardisComponent {

	public static final IdLike ID = new AbstractId<>("PORTALS", PortalsHandler::new, PortalsHandler.class);

	@Nullable
	private EntityRef<TardisPortal> interiorRef;

	@Nullable
	private EntityRef<TardisPortal> exteriorRef;

	public PortalsHandler() {
		super(ID);
	}

	@Override
	public void postInit(InitContext ctx) {
		if (this.isClient() || ctx.created())
			return;

		if (this.exteriorRef != null) {
			ServerWorld exteriorWorld = tardis.travel().position().getWorld();
			this.exteriorRef.setWorld(exteriorWorld);
		}

		if (this.interiorRef != null) {
			ServerWorld interiorWorld = tardis.asServer().world();
			this.interiorRef.setWorld(interiorWorld);
		}
	}

	public static void init() {
        Registry.register(Registries.ENTITY_TYPE, AITMod.id("ip_portal"), TardisPortal.ENTITY_TYPE);

        if (!AITMod.CONFIG.allowPortalsBoti) return;

		TardisComponentRegistry.getInstance().register(ID);

		// TODO: re-use the same two portal entities
		//  for exterior changing this could be achieved by moving the portals & changing their size
		//  for opening and closing doors, portals' rendering can be turned off

		TardisEvents.DOOR_OPEN.register((tdis) -> {
			PortalsHandler handler = tdis.handler(ID);
			handler.generatePortals();
		});

		TardisEvents.REAL_DOOR_CLOSE.register((tdis) -> {
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

        PortalVisualizerUtil.init();
	}

	@Environment(EnvType.CLIENT)
	public static void clientInit() {
		// TODO: make it so doors don't render twice.
		//  > maybe we should just cancel door rendering when there's BOTI present?
		//  > ...idk, need to discuss this - Theo

        PortalVisualizerUtil.clientInit();

        if (TardisPortal.ENTITY_TYPE != null)
            EntityRendererRegistry.register(TardisPortal.ENTITY_TYPE, PortalEntityRenderer::new);
	}

	public TardisPortal getInterior() {
		return this.interiorRef != null ? this.interiorRef.get() : null;
	}

	public TardisPortal getExterior() {
		return this.exteriorRef != null ? this.exteriorRef.get() : null;
	}

	private void generatePortals() {
		CachedDirectedGlobalPos exteriorPos = this.tardis().travel().position();

		DirectedBlockPos tempPos = this.tardis().getDesktop().getDoorPos();
		CachedDirectedGlobalPos interiorPos = CachedDirectedGlobalPos.create(tardis().asServer().world(), tempPos.getPos(), tempPos.getRotation());

		removePortals();

        if (!tardis.getExterior().getVariant().hasPortals()) return;

		this.exteriorRef = new EntityRef<>(exteriorPos.getWorld(), createExteriorPortal());
		this.interiorRef = new EntityRef<>(interiorPos.getWorld(), createInteriorPortal());
	}

    private TardisPortal createExteriorPortal() {
        DirectedBlockPos doorPos = tardis.getDesktop().getDoorPos();
        CachedDirectedGlobalPos exteriorPos = tardis.travel().getState() == TravelHandlerBase.State.LANDED
                ? tardis.travel().position() : tardis.travel().getProgress();

        Vec3d doorAdjust = adjustInteriorPos(tardis.getExterior().getVariant().door(), doorPos);
        Vec3d exteriorAdjust = adjustExteriorPos(tardis.getExterior().getVariant(), exteriorPos);

        TardisPortal portal = new TardisPortal(tardis.travel().getState() == TravelHandlerBase.State.FLIGHT ? WorldUtil.getTimeVortex() : exteriorPos.getWorld());

        portal.setOrientationAndSize(
                new Vec3d(1, 0, 0), // axisW
                new Vec3d(0, 1, 0), // axisH
                tardis.getExterior().getVariant().portalWidth(), // width
                tardis.getExterior().getVariant().portalHeight() // height
        );

        DQuaternion quat = DQuaternion.rotationByDegrees(new Vec3d(0, -1, 0), 180 + RotationPropertyHelper.toDegrees(exteriorPos.getRotation()));
        DQuaternion doorQuat = DQuaternion.rotationByDegrees(new Vec3d(0, -1, 0), RotationPropertyHelper.toDegrees(doorPos.getRotation()));

        PortalAPI.setPortalOrientationQuaternion(portal, quat);
        portal.setOtherSideOrientation(doorQuat);

        portal.setOriginPos(exteriorAdjust);

        portal.setDestinationDimension(tardis.asServer().world().getRegistryKey());
        portal.setDestination(doorAdjust);

        //portal.renderingMergable = true;
        portal.setInteractable(false);
        portal.getWorld().spawnEntity(portal);

        return portal;
    }

    private TardisPortal createInteriorPortal() {
        DirectedBlockPos doorPos = tardis.getDesktop().getDoorPos();
        CachedDirectedGlobalPos exteriorPos = tardis.travel().getState() == TravelHandlerBase.State.LANDED
                ? tardis.travel().position() : tardis.travel().getProgress();

        Vec3d doorAdjust = adjustInteriorPos(tardis.getExterior().getVariant().door(), doorPos);
        Vec3d exteriorAdjust = adjustExteriorPos(tardis.getExterior().getVariant(), exteriorPos);

        TardisPortal portal = new TardisPortal(tardis.asServer().world());

        portal.setOrientationAndSize(
                new Vec3d(1, 0, 0), // axisW
                new Vec3d(0, 1, 0), // axisH
                tardis.getExterior().getVariant().portalWidth(), // width
                tardis.getExterior().getVariant().portalHeight() // height
        );

        DQuaternion quat = DQuaternion.rotationByDegrees(new Vec3d(0, -1, 0), RotationPropertyHelper.toDegrees(doorPos.getRotation()));
        DQuaternion extQuat = DQuaternion.rotationByDegrees(new Vec3d(0, -1, 0), 180 + RotationPropertyHelper.toDegrees(exteriorPos.getRotation()));

        PortalAPI.setPortalOrientationQuaternion(portal, quat);
        portal.setOtherSideOrientation(extQuat);

        portal.setOriginPos(doorAdjust);

        portal.setDestinationDimension(tardis.travel().getState() == TravelHandlerBase.State.FLIGHT ? AITDimensions.TIME_VORTEX_WORLD : exteriorPos.getWorld().getRegistryKey());
        portal.setDestination(exteriorAdjust);

        //portal.renderingMergable = true;w
        portal.setInteractable(false);
        portal.getWorld().spawnEntity(portal);

        return portal;
    }

    private static Vec3d adjustExteriorPos(ExteriorVariantSchema exterior, DirectedGlobalPos directed) {
        return exterior.adjustPortalPos(directed.getPos().toCenterPos(), directed.getRotation()).add(0, 0.75f, 0);
    }

    private static Vec3d adjustInteriorPos(DoorSchema door, DirectedBlockPos directed) {
        return door.adjustPortalPos(directed.getPos().toCenterPos(),
                RotationPropertyHelper.toDirection(directed.getRotation()).get()
        ).add(0, 0.55f, 0);
    }

	private void removePortals() {
		removePortal(this.getInterior());
		removePortal(this.getExterior());
	}

	private static void removePortal(Portal portal) {
		if (portal == null)
			return;

		PortalManipulation.removeConnectedPortals(portal, (p) -> {});
		portal.discard();
	}
}

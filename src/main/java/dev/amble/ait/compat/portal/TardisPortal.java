package dev.amble.ait.compat.portal;

import java.util.UUID;

import dev.amble.ait.client.AITModClient;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.util.EntityRef;
import org.jetbrains.annotations.Nullable;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class TardisPortal extends Portal {

    public static EntityType<TardisPortal> ENTITY_TYPE = createPortalEntityType(TardisPortal::new);

    private Tardis tardis;

    public TardisPortal(Tardis tardis, World world) {
        this(ENTITY_TYPE, world);
        this.tardis = tardis;
    }

    public TardisPortal(EntityType<TardisPortal> type, World world) {
        super(type, world);
    }

    public @Nullable ServerTardis getServerTardis() {
        return this.tardis instanceof ServerTardis serverTardis ? serverTardis : null;
    }

    public boolean isExteriorPortal() {
        ServerTardis serverTardis = this.getServerTardis();
        PortalsHandler portals = serverTardis == null ? null : serverTardis.handler(PortalsHandler.ID);
        EntityRef<TardisPortal> exterior = portals == null ? null : portals.getExteriorRef();
        return exterior != null && this.getUuid().equals(exterior.getId());
    }

    @Override
    public boolean isVisible() {
        return super.isVisible() && AITModClient.CONFIG.allowPortalsBoti;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (!(this.getWorld() instanceof ServerWorld serverWorld) || !nbt.contains("Tardis"))
            return;

        this.tardis = ServerTardisManager.getInstance().demandTardis(serverWorld.getServer(), nbt.getUuid("Tardis"));

        if (this.tardis == null) {
            PortalManipulation.removeConnectedPortals(this, (p) -> {});
            this.discard();
            return;
        }

        PortalsHandler portalsHandler = this.tardis.handler(PortalsHandler.ID);

        EntityRef<TardisPortal> extPortal = portalsHandler.getExteriorRef();
        EntityRef<TardisPortal> intPortal = portalsHandler.getInteriorRef();

        UUID id = this.getUuid();

        if ((extPortal == null || !id.equals(extPortal.getId())) &&
                (intPortal == null || !id.equals(intPortal.getId()))) {
            PortalManipulation.removeConnectedPortals(this, (p) -> {});
            this.discard();
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (tardis != null) {
            nbt.putUuid("Tardis", tardis.getUuid());
        }
    }
}

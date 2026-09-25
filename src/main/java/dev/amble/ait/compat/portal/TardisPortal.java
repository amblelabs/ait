package dev.amble.ait.compat.portal;

import java.util.UUID;

import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.util.EntityRef;
import qouteall.imm_ptl.core.portal.Portal;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class TardisPortal extends Portal {

    public static EntityType<TardisPortal> ENTITY_TYPE = createPortalEntityType(TardisPortal::new);

    private TardisRef tardis;

    public TardisPortal(Tardis tardis, World world) {
        this(ENTITY_TYPE, world);
        this.tardis = TardisRef.createAs(this, tardis);
    }

    public TardisPortal(EntityType<TardisPortal> type, World world) {
        super(type, world);
    }

    @Override
    public boolean isVisible() {
        return super.isVisible() && AITModClient.CONFIG.allowPortalsBoti;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (!(this.getWorld() instanceof ServerWorld) || !nbt.contains("Tardis"))
            return;

        this.tardis = TardisRef.createAs(this, nbt.getUuid("Tardis"));
    }

    @Override
    public boolean isPortalValid() {
        return super.isPortalValid() && (this.getWorld().isClient() || this.isCurrent());
    }

    private boolean isCurrent() {
        Tardis tardis = this.tardis != null ? this.tardis.get() : null;

        if (tardis == null || !(tardis.handler(PortalsHandler.ID) instanceof PortalsHandler portalsHandler))
            return false;

        EntityRef<TardisPortal> extPortal = portalsHandler.getExteriorRef();
        EntityRef<TardisPortal> intPortal = portalsHandler.getInteriorRef();

        UUID id = this.getUuid();

        return (extPortal != null && id.equals(extPortal.getId())) || (intPortal != null && id.equals(intPortal.getId()));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (tardis != null) {
            nbt.putUuid("Tardis", tardis.getId());
        }
    }
}

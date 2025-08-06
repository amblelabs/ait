package dev.amble.ait.compat.portal;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.core.tardis.Tardis;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.world.World;
import qouteall.imm_ptl.core.portal.Portal;

import java.util.UUID;

public class TardisPortal extends Portal implements Linkable {
	private TardisRef ref;

	public TardisPortal(World world) {
		super(entityType, world);
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound tag) {
		super.writeCustomDataToNbt(tag);

		if (ref != null && ref.getId() != null)
			tag.putUuid("tardis", ref.getId());
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);

		NbtElement id = nbt.get("tardis");

		if (id == null)
			return;

		this.ref = TardisRef.createAs(this, NbtHelper.toUuid(id));

		if (this.getWorld() == null)
			return;

		this.onLinked();
	}

	@Override
	public void link(Tardis tardis) {
		this.link(tardis.getUuid());
	}

	@Override
	public void link(UUID id) {
		this.ref = TardisRef.createAs(this, id);
		this.onLinked();
	}

	@Override
	public TardisRef tardis() {
		return ref;
	}
}

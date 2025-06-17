package dev.amble.ait.core.ars;

import dev.amble.ait.AITMod;
import dev.amble.ait.registry.impl.DesktopRegistry;
import dev.amble.lib.register.JsonDecoder;
import dev.amble.lib.register.datapack.SimpleDatapackRegistry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ArsRegistry extends SimpleDatapackRegistry<ArsStructure> {
	private static final ArsRegistry INSTANCE = new ArsRegistry();

	private ArsRegistry() {
		super((stream) -> JsonDecoder.fromInputStream(stream, BasicArsStructure.CODEC, "ars"), BasicArsStructure.CODEC, "ars", true, AITMod.MOD_ID);
	}

	/**
	 * @return a list of all ArsStructures, including those from the DesktopRegistry.
	 */
	@Override
	public List<ArsStructure> toList() {
		List<ArsStructure> list = new ArrayList<>(REGISTRY.values());
		list.addAll(DesktopRegistry.getInstance().toList());
		return list;
	}

	@Override
	public ArsStructure get(Identifier id) {
		ArsStructure ars = super.get(id);
		if (ars != null) return ars;

		return DesktopRegistry.getInstance().get(id);
	}

	@Override
	public ArsStructure getOrElse(Identifier id, ArsStructure fallback) {
		ArsStructure ars = this.get(id);

		return ars != null ? ars : fallback;
	}

	@Override
	protected void defaults() {

	}

	@Override
	public ArsStructure fallback() {
		return DesktopRegistry.getInstance().fallback();
	}

	public static ArsRegistry getInstance() {
		return INSTANCE;
	}
}

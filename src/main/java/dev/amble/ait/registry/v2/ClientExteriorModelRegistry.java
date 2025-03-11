package dev.amble.ait.registry.v2;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.models.exteriors.TardimExteriorModel;
import dev.amble.lib.data.MoreCodec;
import dev.amble.lib.registry.SimpleAmbleRegistry;
import dev.amble.lib.registry.SimpleRegistryElementCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registry;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public class ClientExteriorModelRegistry extends SimpleAmbleRegistry<ExteriorModel> {

    public static final Identifier TARDIM = AITMod.id("tardim");

    private final SimpleRegistryElementCodec<ExteriorModel> entry = SimpleRegistryElementCodec.of(this);

    public ClientExteriorModelRegistry() {
        super(AITMod.id("exterior/model"));

        Registry.register(this.get(), AITMod.id("tardim"), (ExteriorModel) new TardimExteriorModel(TardimExteriorModel.getTexturedModelData().createModel()));
    }

    public SimpleRegistryElementCodec<ExteriorModel> entry() {
        return entry;
    }
}

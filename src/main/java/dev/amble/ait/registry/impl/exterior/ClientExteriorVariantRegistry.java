package dev.amble.ait.registry.impl.exterior;


import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.exteriors.BedrockExteriorModel;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.data.datapack.DatapackExterior;
import dev.amble.ait.data.datapack.exterior.BiomeOverrides;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.variant.adaptive.client.ClientAdaptiveVariant;
import dev.amble.ait.data.schema.exterior.variant.bookshelf.client.ClientBookshelfDefaultVariant;
import dev.amble.ait.data.schema.exterior.variant.booth.client.*;
import dev.amble.ait.data.schema.exterior.variant.box.client.*;
import dev.amble.ait.data.schema.exterior.variant.capsule.client.ClientCapsuleDefaultVariant;
import dev.amble.ait.data.schema.exterior.variant.capsule.client.ClientCapsuleFireVariant;
import dev.amble.ait.data.schema.exterior.variant.capsule.client.ClientCapsuleSoulVariant;
import dev.amble.ait.data.schema.exterior.variant.classic.client.*;
import dev.amble.ait.data.schema.exterior.variant.dalek_mod.client.*;
import dev.amble.ait.data.schema.exterior.variant.easter_head.client.ClientEasterHeadDefaultVariant;
import dev.amble.ait.data.schema.exterior.variant.easter_head.client.ClientEasterHeadFireVariant;
import dev.amble.ait.data.schema.exterior.variant.easter_head.client.ClientEasterHeadSoulVariant;
import dev.amble.ait.data.schema.exterior.variant.exclusive.doom.client.ClientDoomVariant;
import dev.amble.ait.data.schema.exterior.variant.exclusive.wanderer.client.ClientBoothWandererVariant;
import dev.amble.ait.data.schema.exterior.variant.geometric.client.ClientGeometricDefaultVariant;
import dev.amble.ait.data.schema.exterior.variant.geometric.client.ClientGeometricFireVariant;
import dev.amble.ait.data.schema.exterior.variant.geometric.client.ClientGeometricGildedVariant;
import dev.amble.ait.data.schema.exterior.variant.geometric.client.ClientGeometricSoulVariant;
import dev.amble.ait.data.schema.exterior.variant.growth.client.ClientGrowthVariant;
import dev.amble.ait.data.schema.exterior.variant.pipe.client.ClientPipeBlueVariant;
import dev.amble.ait.data.schema.exterior.variant.pipe.client.ClientPipeDefaultVariant;
import dev.amble.ait.data.schema.exterior.variant.pipe.client.ClientPipeRedVariant;
import dev.amble.ait.data.schema.exterior.variant.plinth.client.ClientPlinthDefaultVariant;
import dev.amble.ait.data.schema.exterior.variant.plinth.client.ClientPlinthFireVariant;
import dev.amble.ait.data.schema.exterior.variant.plinth.client.ClientPlinthSoulVariant;
import dev.amble.ait.data.schema.exterior.variant.renegade.client.ClientRenegadeCabinetVariant;
import dev.amble.ait.data.schema.exterior.variant.renegade.client.ClientRenegadeDefaultVariant;
import dev.amble.ait.data.schema.exterior.variant.renegade.client.ClientRenegadeTronVariant;
import dev.amble.ait.data.schema.exterior.variant.stallion.client.ClientStallionDefaultVariant;
import dev.amble.ait.data.schema.exterior.variant.stallion.client.ClientStallionFireVariant;
import dev.amble.ait.data.schema.exterior.variant.stallion.client.ClientStallionSoulVariant;
import dev.amble.ait.data.schema.exterior.variant.stallion.client.ClientStallionSteelVariant;
import dev.amble.ait.data.schema.exterior.variant.tardim.client.ClientTardimDefaultVariant;
import dev.amble.ait.data.schema.exterior.variant.tardim.client.ClientTardimFireVariant;
import dev.amble.ait.data.schema.exterior.variant.tardim.client.ClientTardimSoulVariant;
import dev.amble.lib.client.bedrock.BedrockModelRegistry;
import dev.amble.lib.register.datapack.DatapackRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class ClientExteriorVariantRegistry extends DatapackRegistry<ClientExteriorVariantSchema> implements
        SimpleSynchronousResourceReloadListener {

    private static final ClientExteriorVariantRegistry INSTANCE = new ClientExteriorVariantRegistry();

    public static DatapackRegistry<ClientExteriorVariantSchema> getInstance() {
        return INSTANCE;
    }

    /**
     * Will return the clients version of a servers door variant
     *
     * @return the first variant found as there should only be one client version
     */
    public static ClientExteriorVariantSchema withParent(ExteriorVariantSchema parent) {
        for (ClientExteriorVariantSchema schema : ClientExteriorVariantRegistry.getInstance().toList()) {
            if (schema.parent() == null)
                continue;

            if (schema.parent().id().equals(parent.id()))
                return schema;
        }

        return null;
    }

    @Override
    public ClientExteriorVariantSchema fallback() {
        return null;
    }

    /**
     * Do not call
     */
    @Override
    public void syncToClient(ServerPlayerEntity player) { }

    @Override
    public void readFromServer(PacketByteBuf buf) {
        for (ExteriorVariantSchema schema : ExteriorVariantRegistry.getInstance().toList()) {
            if (!(schema instanceof DatapackExterior variant)) continue;

            ClientExteriorVariantSchema clientSchema = convertDatapack(variant);

            if (clientSchema == null) continue;

            this.register(clientSchema);
        }
    }

    public static ClientExteriorVariantSchema convertDatapack(DatapackExterior variant) {
        if (!variant.wasDatapack())
            return convertNonDatapack(variant);

        return new ClientExteriorVariantSchema(variant.id()) {

            @Override
            public Identifier texture() {
                return variant.texture();
            }

            @Override
            public Identifier emission() {
                return variant.emission();
            }

            @Override
            public ExteriorModel model() {
                if (variant.model().isPresent()) {
                    return new BedrockExteriorModel(BedrockModelRegistry.getInstance().get(variant.model().get()));
                }

                var parent = getInstance().get(variant.getParentId());

                if (parent == null) return ClientExteriorVariantRegistry.CAPSULE_DEFAULT.model();

                return parent.model();
            }

            @Override
            public Vector3f sonicItemTranslations() {
                return new Vector3f(0.5f, 1.2f, 1.2f);
            }

            @Override
            public BiomeOverrides overrides() {
                return variant.overrides();
            }

            @Override
            public boolean hasTransparentDoors() {
                return variant.hasTransparentDoors();
            }
        };
    }

    private static ClientExteriorVariantSchema convertNonDatapack(DatapackExterior variant) {
        if (variant.wasDatapack())
            return convertDatapack(variant);

        return getInstance().get(variant.id());
    }

    public static ClientExteriorVariantSchema TARDIM_DEFAULT;
    public static ClientExteriorVariantSchema TARDIM_FIRE;
    public static ClientExteriorVariantSchema TARDIM_SOUL;
    public static ClientExteriorVariantSchema BOX_DEFAULT;
    public static ClientExteriorVariantSchema BOX_FIRE;
    public static ClientExteriorVariantSchema BOX_SOUL;
    public static ClientExteriorVariantSchema BOX_FUTURE;
    public static ClientExteriorVariantSchema BOX_CORAL;
    public static ClientExteriorVariantSchema BOX_CHERRY;
    public static ClientExteriorVariantSchema BOX_RENAISSANCE;
    public static ClientExteriorVariantSchema PRIME;
    public static ClientExteriorVariantSchema YETI;
    public static ClientExteriorVariantSchema DEFINITIVE;
    public static ClientExteriorVariantSchema PTORED;
    public static ClientExteriorVariantSchema MINT;
    public static ClientExteriorVariantSchema HUDOLIN;
    public static ClientExteriorVariantSchema SHALKA;
    public static ClientExteriorVariantSchema EXILE;
    public static ClientExteriorVariantSchema CAPSULE_DEFAULT;
    public static ClientExteriorVariantSchema CAPSULE_SOUL;
    public static ClientExteriorVariantSchema CAPSULE_FIRE;
    public static ClientExteriorVariantSchema BOOTH_DEFAULT;
    public static ClientExteriorVariantSchema BOOTH_FIRE;
    public static ClientExteriorVariantSchema BOOTH_SOUL;
    public static ClientExteriorVariantSchema BOOTH_VINTAGE;
    public static ClientExteriorVariantSchema BOOTH_BLUE;
    public static ClientExteriorVariantSchema BOOTH_GILDED;
    public static ClientExteriorVariantSchema HEAD_DEFAULT;
    public static ClientExteriorVariantSchema HEAD_SOUL;
    public static ClientExteriorVariantSchema HEAD_FIRE;
    public static ClientExteriorVariantSchema CORAL_GROWTH;
    public static ClientExteriorVariantSchema PLINTH_DEFAULT;
    public static ClientExteriorVariantSchema PLINTH_SOUL;
    public static ClientExteriorVariantSchema PLINTH_FIRE;
    public static ClientExteriorVariantSchema RENEGADE_DEFAULT;
    public static ClientExteriorVariantSchema RENEGADE_TRON;
    public static ClientExteriorVariantSchema RENEGADE_CABINET;
    public static ClientExteriorVariantSchema BOOKSHELF_DEFAULT;
    public static ClientExteriorVariantSchema GEOMETRIC_DEFAULT;
    public static ClientExteriorVariantSchema GEOMETRIC_FIRE;
    public static ClientExteriorVariantSchema GEOMETRIC_SOUL;
    public static ClientExteriorVariantSchema GEOMETRIC_GILDED;
    public static ClientExteriorVariantSchema STALLION_DEFAULT;
    public static ClientExteriorVariantSchema STALLION_FIRE;
    public static ClientExteriorVariantSchema STALLION_SOUL;
    public static ClientExteriorVariantSchema STALLION_STEEL;
    public static ClientExteriorVariantSchema ADAPTIVE;
    public static ClientExteriorVariantSchema DALEK_MOD_1963;
    public static ClientExteriorVariantSchema DALEK_MOD_1967;
    public static ClientExteriorVariantSchema DALEK_MOD_1970;
    public static ClientExteriorVariantSchema DALEK_MOD_1976;
    public static ClientExteriorVariantSchema DALEK_MOD_1980;
    public static ClientExteriorVariantSchema PIPE_DEFAULT;
    public static ClientExteriorVariantSchema PIPE_RED;
    public static ClientExteriorVariantSchema PIPE_BLUE;

    public static ClientExteriorVariantSchema DOOM;
    public static ClientExteriorVariantSchema BOOTH_WANDERER;

    @Override
    public void onClientInit() {
        // TARDIM
        TARDIM_DEFAULT = register(new ClientTardimDefaultVariant());
        TARDIM_FIRE = register(new ClientTardimFireVariant());
        TARDIM_SOUL = register(new ClientTardimSoulVariant());

        // Police Box
        BOX_DEFAULT = register(new ClientPoliceBoxDefaultVariant());
        BOX_SOUL = register(new ClientPoliceBoxSoulVariant());
        BOX_FIRE = register(new ClientPoliceBoxFireVariant());
        BOX_FUTURE = register(new ClientPoliceBoxFuturisticVariant());
        BOX_CORAL = register(new ClientPoliceBoxCoralVariant());
        BOX_RENAISSANCE = register(new ClientPoliceBoxRenaissanceVariant());
        BOX_CHERRY = register(new ClientPoliceBoxCherryVariant());

        // Classic Box
        PRIME = register(new ClientClassicBoxPrimeVariant());
        YETI = register(new ClientClassicBoxYetiVariant());
        DEFINITIVE = register(new ClientClassicBoxDefinitiveVariant());
        PTORED = register(new ClientClassicBoxPtoredVariant());
        MINT = register(new ClientClassicBoxMintVariant());
        HUDOLIN = register(new ClientClassicBoxHudolinVariant());
        SHALKA = register(new ClientClassicBoxShalkaVariant());
        EXILE = register(new ClientClassicBoxExileVariant());

        // Capsule
        CAPSULE_DEFAULT = register(new ClientCapsuleDefaultVariant());
        CAPSULE_SOUL = register(new ClientCapsuleSoulVariant());
        CAPSULE_FIRE = register(new ClientCapsuleFireVariant());

        // Booth
        BOOTH_DEFAULT = register(new ClientBoothDefaultVariant());
        BOOTH_FIRE = register(new ClientBoothFireVariant());
        BOOTH_SOUL = register(new ClientBoothSoulVariant());
        BOOTH_VINTAGE = register(new ClientBoothVintageVariant());
        BOOTH_BLUE = register(new ClientBoothBlueVariant());
        BOOTH_GILDED = register(new ClientBoothGildedVariant());

        // Easter Head
        HEAD_DEFAULT = register(new ClientEasterHeadDefaultVariant());
        HEAD_SOUL = register(new ClientEasterHeadSoulVariant());
        HEAD_FIRE = register(new ClientEasterHeadFireVariant());

        // Coral
        CORAL_GROWTH = register(new ClientGrowthVariant());

        // Dev Exclusives
        DOOM = register(new ClientDoomVariant());
        BOOTH_WANDERER = register(new ClientBoothWandererVariant());

        // Plinth
        PLINTH_DEFAULT = register(new ClientPlinthDefaultVariant());
        PLINTH_SOUL = register(new ClientPlinthSoulVariant());
        PLINTH_FIRE = register(new ClientPlinthFireVariant());

        // Renegade
        RENEGADE_DEFAULT = register(new ClientRenegadeDefaultVariant());
        RENEGADE_TRON = register(new ClientRenegadeTronVariant());
        RENEGADE_CABINET = register(new ClientRenegadeCabinetVariant());

        // Bookshelf
        BOOKSHELF_DEFAULT = register(new ClientBookshelfDefaultVariant());

        // Geometric
        GEOMETRIC_DEFAULT = register(new ClientGeometricDefaultVariant());
        GEOMETRIC_FIRE = register(new ClientGeometricFireVariant());
        GEOMETRIC_SOUL = register(new ClientGeometricSoulVariant());
        GEOMETRIC_GILDED = register(new ClientGeometricGildedVariant());

        // Stallion
        STALLION_DEFAULT = register(new ClientStallionDefaultVariant());
        STALLION_FIRE = register(new ClientStallionFireVariant());
        STALLION_SOUL = register(new ClientStallionSoulVariant());
        STALLION_STEEL = register(new ClientStallionSteelVariant());

        ADAPTIVE = register(new ClientAdaptiveVariant());

        // Dalek Mod
        DALEK_MOD_1963 = register(new ClientDalekMod1963Variant());
        DALEK_MOD_1967 = register(new ClientDalekMod1967Variant());
        DALEK_MOD_1970 = register(new ClientDalekMod1970Variant());
        DALEK_MOD_1976 = register(new ClientDalekMod1976Variant());
        DALEK_MOD_1980 = register(new ClientDalekMod1980Variant());

        // Jake
        //JAKE_DEFAULT = register(new ClientJakeDefaultVariant());

        // Present
        //PRESENT_DEFAULT = register(new ClientPresentDefaultVariant());
        //PRESENT_GREEN = register(new ClientPresentGreenVariant());
        //PRESENT_BLUE = register(new ClientPresentBlueVariant());

        // Pipe
        PIPE_DEFAULT = register(new ClientPipeDefaultVariant());
        PIPE_RED = register(new ClientPipeRedVariant());
        PIPE_BLUE = register(new ClientPipeBlueVariant());
        //PIPE_YELLOW = register(new ClientPipeYellowVariant());
    }

    @Override
    public Identifier getFabricId() {
        return AITMod.id("client_exterior");
    }

    @Override
    public void onCommonInit() {
        super.onCommonInit();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
    }

    @Override
    public void reload(ResourceManager manager) {
        for (ClientExteriorVariantSchema schema : REGISTRY.values()) {
            BiomeOverrides overrides = schema.overrides();

            if (overrides == null)
                continue;

            overrides.validate();
        }
    }
}

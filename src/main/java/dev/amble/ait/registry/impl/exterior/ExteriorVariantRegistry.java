package dev.amble.ait.registry.impl.exterior;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dev.amble.lib.register.datapack.DatapackRegistry;
import dev.amble.lib.register.unlockable.UnlockableRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.AITRegistryEvents;
import dev.amble.ait.data.datapack.DatapackExterior;
import dev.amble.ait.data.datapack.exterior.BiomeOverrides;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.variant.bookshelf.BookshelfDefaultVariant;
import dev.amble.ait.data.schema.exterior.variant.booth.*;
import dev.amble.ait.data.schema.exterior.variant.box.*;
import dev.amble.ait.data.schema.exterior.variant.capsule.CapsuleDefaultVariant;
import dev.amble.ait.data.schema.exterior.variant.capsule.CapsuleFireVariant;
import dev.amble.ait.data.schema.exterior.variant.capsule.CapsuleSoulVariant;
import dev.amble.ait.data.schema.exterior.variant.classic.*;
import dev.amble.ait.data.schema.exterior.variant.dalek_mod.*;
import dev.amble.ait.data.schema.exterior.variant.easter_head.EasterHeadDefaultVariant;
import dev.amble.ait.data.schema.exterior.variant.easter_head.EasterHeadFireVariant;
import dev.amble.ait.data.schema.exterior.variant.easter_head.EasterHeadSoulVariant;
import dev.amble.ait.data.schema.exterior.variant.exclusive.doom.DoomVariant;
import dev.amble.ait.data.schema.exterior.variant.exclusive.wanderer.BoothWandererVariant;
import dev.amble.ait.data.schema.exterior.variant.geometric.GeometricDefaultVariant;
import dev.amble.ait.data.schema.exterior.variant.geometric.GeometricFireVariant;
import dev.amble.ait.data.schema.exterior.variant.geometric.GeometricGildedVariant;
import dev.amble.ait.data.schema.exterior.variant.geometric.GeometricSoulVariant;
import dev.amble.ait.data.schema.exterior.variant.growth.CoralGrowthVariant;
import dev.amble.ait.data.schema.exterior.variant.pipe.PipeBlueVariant;
import dev.amble.ait.data.schema.exterior.variant.pipe.PipeDefaultVariant;
import dev.amble.ait.data.schema.exterior.variant.pipe.PipeRedVariant;
import dev.amble.ait.data.schema.exterior.variant.plinth.PlinthDefaultVariant;
import dev.amble.ait.data.schema.exterior.variant.plinth.PlinthFireVariant;
import dev.amble.ait.data.schema.exterior.variant.plinth.PlinthSoulVariant;
import dev.amble.ait.data.schema.exterior.variant.renegade.RenegadeCabinetVariant;
import dev.amble.ait.data.schema.exterior.variant.renegade.RenegadeDefaultVariant;
import dev.amble.ait.data.schema.exterior.variant.renegade.RenegadeTronVariant;
import dev.amble.ait.data.schema.exterior.variant.stallion.StallionDefaultVariant;
import dev.amble.ait.data.schema.exterior.variant.stallion.StallionFireVariant;
import dev.amble.ait.data.schema.exterior.variant.stallion.StallionSoulVariant;
import dev.amble.ait.data.schema.exterior.variant.stallion.StallionSteelVariant;
import dev.amble.ait.data.schema.exterior.variant.tardim.TardimDefaultVariant;
import dev.amble.ait.data.schema.exterior.variant.tardim.TardimFireVariant;
import dev.amble.ait.data.schema.exterior.variant.tardim.TardimSoulVariant;

public class ExteriorVariantRegistry extends UnlockableRegistry<ExteriorVariantSchema> {
    private static ExteriorVariantRegistry INSTANCE;

    protected ExteriorVariantRegistry() {
        super(DatapackExterior::fromInputStream, null, "exterior", true);
    }

    @Override
    public ExteriorVariantSchema fallback() {
        return ExteriorVariantRegistry.CAPSULE_DEFAULT;
    }

    @Override
    public void onCommonInit() {
        super.onCommonInit();
        this.defaults();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
    }

    @Override
    public void syncToClient(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(REGISTRY.size());

        for (ExteriorVariantSchema schema : REGISTRY.values()) {
            if (schema instanceof DatapackExterior variant) {
                buf.encodeAsJson(DatapackExterior.CODEC, variant);
                continue;
            }

            buf.encodeAsJson(DatapackExterior.CODEC,
                    new DatapackExterior(schema.id(), schema.categoryId(), schema.id(),
                            DatapackExterior.DEFAULT_TEXTURE, DatapackExterior.DEFAULT_TEXTURE, schema.requirement(),
                            BiomeOverrides.EMPTY,new Vec3d(0.5, 1, 0.5), false, false));
        }

        ServerPlayNetworking.send(player, this.packet, buf);
    }

    @Override
    public void readFromServer(PacketByteBuf buf) {
        PacketByteBuf copy = PacketByteBufs.copy(buf);
        ClientExteriorVariantRegistry.getInstance().readFromServer(copy);

        this.defaults();

        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            DatapackExterior variant = buf.decodeAsJson(DatapackExterior.CODEC);

            if (!variant.wasDatapack())
                continue;

            register(variant);
        }

        AITMod.LOGGER.info("Read {} exterior variants from server", size);
    }

    public static ExteriorVariantRegistry getInstance() {
        if (INSTANCE == null) {
            AITMod.LOGGER.debug("ExteriorVariantRegistry was not initialized, Creating a new instance");
            INSTANCE = new ExteriorVariantRegistry();
        }

        return INSTANCE;
    }

    public static List<ExteriorVariantSchema> withParent(ExteriorCategorySchema parent) {
        List<ExteriorVariantSchema> list = new ArrayList<>();

        for (ExteriorVariantSchema schema : ExteriorVariantRegistry.getInstance().REGISTRY.values()) {
            if (schema.category().equals(parent))
                list.add(schema);
        }

        return list;
    }

    public ExteriorVariantSchema pickRandomWithParent(ExteriorCategorySchema parent, Random random) {
        return DatapackRegistry.getRandom(ExteriorVariantRegistry.withParent(parent), random, this::fallback);
    }

    public ExteriorVariantSchema pickRandomWithParent(ExteriorCategorySchema parent) {
        return this.pickRandomWithParent(parent, RANDOM);
    }

    public static ExteriorVariantSchema TARDIM_DEFAULT;
    public static ExteriorVariantSchema TARDIM_FIRE;
    public static ExteriorVariantSchema TARDIM_SOUL;
    public static ExteriorVariantSchema BOX_DEFAULT;
    public static ExteriorVariantSchema BOX_FIRE;
    public static ExteriorVariantSchema BOX_SOUL;
    public static ExteriorVariantSchema BOX_FUTURE;
    public static ExteriorVariantSchema BOX_CORAL;
    public static ExteriorVariantSchema BOX_RENAISSANCE;
    public static ExteriorVariantSchema BOX_CHERRY;
    public static ExteriorVariantSchema PRIME;
    public static ExteriorVariantSchema YETI;
    public static ExteriorVariantSchema DEFINITIVE;
    public static ExteriorVariantSchema PTORED;
    public static ExteriorVariantSchema MINT;
    public static ExteriorVariantSchema HUDOLIN;
    public static ExteriorVariantSchema SHALKA;
    public static ExteriorVariantSchema EXILE;
    public static ExteriorVariantSchema CAPSULE_DEFAULT;
    public static ExteriorVariantSchema CAPSULE_SOUL;
    public static ExteriorVariantSchema CAPSULE_FIRE;
    public static ExteriorVariantSchema BOOTH_DEFAULT;
    public static ExteriorVariantSchema BOOTH_FIRE;
    public static ExteriorVariantSchema BOOTH_SOUL;
    public static ExteriorVariantSchema BOOTH_VINTAGE;
    public static ExteriorVariantSchema BOOTH_BLUE;
    public static ExteriorVariantSchema BOOTH_GILDED;
    public static ExteriorVariantSchema HEAD_DEFAULT;
    public static ExteriorVariantSchema HEAD_SOUL;
    public static ExteriorVariantSchema HEAD_FIRE;
    public static ExteriorVariantSchema CORAL_GROWTH;
    public static ExteriorVariantSchema PLINTH_DEFAULT;
    public static ExteriorVariantSchema PLINTH_SOUL;
    public static ExteriorVariantSchema PLINTH_FIRE;
    public static ExteriorVariantSchema RENEGADE_DEFAULT;
    public static ExteriorVariantSchema RENEGADE_TRON;
    public static ExteriorVariantSchema RENEGADE_CABINET;
    public static ExteriorVariantSchema BOOKSHELF_DEFAULT;
    public static ExteriorVariantSchema GEOMETRIC_DEFAULT;
    public static ExteriorVariantSchema GEOMETRIC_FIRE;
    public static ExteriorVariantSchema GEOMETRIC_SOUL;
    public static ExteriorVariantSchema GEOMETRIC_GILDED;
    public static ExteriorVariantSchema STALLION_DEFAULT;
    public static ExteriorVariantSchema STALLION_FIRE;
    public static ExteriorVariantSchema STALLION_SOUL;
    public static ExteriorVariantSchema STALLION_STEEL;
    public static ExteriorVariantSchema ADAPTIVE;
    public static ExteriorVariantSchema DALEK_MOD_1963;
    public static ExteriorVariantSchema DALEK_MOD_1967;
    public static ExteriorVariantSchema DALEK_MOD_1970;
    public static ExteriorVariantSchema DALEK_MOD_1976;
    public static ExteriorVariantSchema DALEK_MOD_1980;
    public static ExteriorVariantSchema PIPE_DEFAULT;
    public static ExteriorVariantSchema PIPE_RED;
    public static ExteriorVariantSchema PIPE_BLUE;

    // Exclusives
    public static ExteriorVariantSchema DOOM;
    public static ExteriorVariantSchema WANDERER;

    @Override
    protected void defaults() {
        AITRegistryEvents.EXTERIOR_DEFAULTS.invoker().defaults();

        // TARDIM
        TARDIM_DEFAULT = register(new TardimDefaultVariant());
        TARDIM_FIRE = register(new TardimFireVariant());
        TARDIM_SOUL = register(new TardimSoulVariant());

        // Police Box
        BOX_DEFAULT = register(new PoliceBoxDefaultVariant());
        BOX_SOUL = register(new PoliceBoxSoulVariant());
        BOX_FIRE = register(new PoliceBoxFireVariant());
        BOX_FUTURE = register(new PoliceBoxFuturisticVariant());
        BOX_CORAL = register(new PoliceBoxCoralVariant());
        BOX_RENAISSANCE = register(new PoliceBoxRenaissanceVariant());
        BOX_CHERRY = register(new PoliceBoxCherryVariant());

        // Classic Box
        PRIME = register(new ClassicBoxPrimeVariant());
        YETI = register(new ClassicBoxYetiVariant());
        HUDOLIN = register(new ClassicBoxHudolinVariant());
        DEFINITIVE = register(new ClassicBoxDefinitiveVariant());
        PTORED = register(new ClassicBoxPtoredVariant());
        MINT = register(new ClassicBoxMintVariant());
        SHALKA = register(new ClassicBoxShalkaVariant());
        EXILE = register(new ClassicBoxExileVariant());

        // Capsule
        CAPSULE_DEFAULT = register(new CapsuleDefaultVariant());
        CAPSULE_SOUL = register(new CapsuleSoulVariant());
        CAPSULE_FIRE = register(new CapsuleFireVariant());

        // Booth
        BOOTH_DEFAULT = register(new BoothDefaultVariant());
        BOOTH_FIRE = register(new BoothFireVariant());
        BOOTH_SOUL = register(new BoothSoulVariant());
        BOOTH_VINTAGE = register(new BoothVintageVariant());
        BOOTH_BLUE = register(new BoothBlueVariant());
        BOOTH_GILDED = register(new BoothGildedVariant());

        // Easter Head
        HEAD_DEFAULT = register(new EasterHeadDefaultVariant());
        HEAD_SOUL = register(new EasterHeadSoulVariant());
        HEAD_FIRE = register(new EasterHeadFireVariant());

        // Coral Growth
        CORAL_GROWTH = register(new CoralGrowthVariant());


        // Plinth
        PLINTH_DEFAULT = register(new PlinthDefaultVariant());
        PLINTH_SOUL = register(new PlinthSoulVariant());
        PLINTH_FIRE = register(new PlinthFireVariant());

        // Renegade
        RENEGADE_DEFAULT = register(new RenegadeDefaultVariant());
        RENEGADE_TRON = register(new RenegadeTronVariant());
        RENEGADE_CABINET = register(new RenegadeCabinetVariant());

        // Bookshelf
        BOOKSHELF_DEFAULT = register(new BookshelfDefaultVariant());

        // Geometric
        GEOMETRIC_DEFAULT = register(new GeometricDefaultVariant());
        GEOMETRIC_FIRE = register(new GeometricFireVariant());
        GEOMETRIC_SOUL = register(new GeometricSoulVariant());
        GEOMETRIC_GILDED = register(new GeometricGildedVariant());

        // Stallion
        STALLION_DEFAULT = register(new StallionDefaultVariant());
        STALLION_FIRE = register(new StallionFireVariant());
        STALLION_SOUL = register(new StallionSoulVariant());
        STALLION_STEEL = register(new StallionSteelVariant());

        // Adaptive
//        ADAPTIVE = register(new AdaptiveVariant());

        // Dalek Mod
        DALEK_MOD_1963 = register(new DalekMod1963Variant());
        DALEK_MOD_1967 = register(new DalekMod1967Variant());
        DALEK_MOD_1970 = register(new DalekMod1970Variant());
        DALEK_MOD_1976 = register(new DalekMod1976Variant());
        DALEK_MOD_1980 = register(new DalekMod1980Variant());

        // Pipe
        PIPE_DEFAULT = register(new PipeDefaultVariant());
        PIPE_RED = register(new PipeRedVariant());
        //PIPE_YELLOW = register(new PipeYellowVariant());
        PIPE_BLUE = register(new PipeBlueVariant());

        // Dev Exclusives
        DOOM = register(new DoomVariant());
        WANDERER = register(new BoothWandererVariant());
    }
}

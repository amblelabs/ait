package dev.amble.ait.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dev.amble.lib.container.impl.ItemContainer;
import dev.amble.lib.datagen.util.NoEnglish;
import dev.amble.lib.item.AItemSettings;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Rarity;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.link.LinkableItem;
import dev.amble.ait.core.drinks.DrinkRegistry;
import dev.amble.ait.core.drinks.DrinkUtil;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.item.SubSystemItem;
import dev.amble.ait.core.item.*;
import dev.amble.ait.core.item.blueprint.BlueprintItem;
import dev.amble.ait.core.item.blueprint.BlueprintRegistry;
import dev.amble.ait.core.item.blueprint.BlueprintSchema;
import dev.amble.ait.core.item.control.GenericControlBlockItem;
import dev.amble.ait.core.item.link.AbstractLinkItem;
import dev.amble.ait.core.item.link.FluidLinkItem;
import dev.amble.ait.core.item.link.MercurialLinkItem;
import dev.amble.ait.core.item.part.MachinePartItem;

public class AITItems extends ItemContainer {

    @NoEnglish
    public static final Item MUG = new DrinkItem(new AItemSettings().maxCount(1));
    public static final FoodComponent ZEITON_DUST_FOOD = new FoodComponent.Builder().hunger(4).saturationModifier(0.3f)
            .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1000, 3), 1.0F)
            .statusEffect(new StatusEffectInstance(AITStatusEffects.ZEITON_HIGH, 500, 1), 1.0F)
            .statusEffect(new StatusEffectInstance(StatusEffects.WITHER, 500, 1), 0.5F)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 25, 1), 0.1F)
            .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 45, 1), 0.7F)
            .statusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 65, 1), 0.3F)
            .build();

    // TARDIS
    @NoEnglish
    public static final Item TARDIS_ITEM = new TardisItemBuilder(
            new AItemSettings().group(AITItemGroups.MAIN).fireproof().maxCount(1));

    @NoEnglish
    public static final LinkableItem SIEGE_ITEM = new SiegeTardisItem(new FabricItemSettings().fireproof());

    // Functional Items
    @NoEnglish
    public static final Item REMOTE_ITEM = new RemoteItem(
            new AItemSettings().group(AITItemGroups.MAIN).maxCount(1).fireproof().maxDamageIfAbsent(300));
    @NoEnglish
    public static final Item ARTRON_COLLECTOR = new ArtronCollectorItem(
            new AItemSettings().group(AITItemGroups.MAIN).maxCount(1));
    public static final Item RIFT_SCANNER = new RiftScannerItem(
            new AItemSettings().group(AITItemGroups.MAIN).maxCount(1));
    public static final Item GEIGER_COUNTER = new RiftScannerItem(
            new AItemSettings().group(AITItemGroups.MAIN).maxCount(1));
    @NoEnglish
    public static final Item HAMMER = new HammerItem(3, -2.4F,
            new AItemSettings().group(AITItemGroups.MAIN).maxCount(1).maxDamage(600));
    public static final Item RESPIRATOR = new RenderableArmorItem(ArmorMaterials.IRON, ArmorItem.Type.HELMET,
            new AItemSettings().group(AITItemGroups.MAIN).maxCount(1).maxDamage(80), true);
    public static final Item FACELESS_RESPIRATOR = new RenderableArmorItem(ArmorMaterials.IRON,
            ArmorItem.Type.HELMET, new AItemSettings().group(AITItemGroups.MAIN).maxCount(1).maxDamage(80),
            true);

    public static final Item SONIC_SCREWDRIVER = new SonicItem(new AItemSettings().maxCount(1).group(AITItemGroups.MAIN));

    public static final Item HYPERCUBE = new HypercubeItem(new AItemSettings().maxCount(1).group(AITItemGroups.MAIN));
    public static final Item PSYCHPAPER = new PsychpaperItem(new AItemSettings().maxCount(1).group(AITItemGroups.MAIN));
    public static final Item HAZANDRA = new InteriorTeleporterItem(new AItemSettings().group(AITItemGroups.MAIN));

    // Keys/Horns - Templates
    public static final Item IRON_KEY = new KeyItem(new AItemSettings().group(AITItemGroups.MAIN));
    public static final Item GOLD_KEY = new KeyItem(
            new AItemSettings().rarity(Rarity.UNCOMMON).group(AITItemGroups.MAIN), KeyItem.Protocols.SNAP);
    public static final Item NETHERITE_KEY = new KeyItem(
            new AItemSettings().rarity(Rarity.RARE).group(AITItemGroups.MAIN).fireproof(), KeyItem.Protocols.SNAP,
            KeyItem.Protocols.HAIL);
    public static final Item CLASSIC_KEY = new KeyItem(
            new AItemSettings().rarity(Rarity.EPIC).group(AITItemGroups.MAIN), KeyItem.Protocols.SNAP,
            KeyItem.Protocols.HAIL);

    // Creative only skeleton key
    public static final Item SKELETON_KEY = new KeyItem(
            new AItemSettings().rarity(Rarity.EPIC).group(AITItemGroups.MAIN), KeyItem.Protocols.SKELETON,
            KeyItem.Protocols.HAIL, KeyItem.Protocols.SNAP);

   /* public static final Item IRON_GOAT_HORN = new TardisGoatHorn(new AItemSettings().group(AITItemGroups.MAIN), InstrumentTags.GOAT_HORNS);

    public static final Item GOLD_GOAT_HORN = new TardisGoatHorn(new AItemSettings().rarity(Rarity.UNCOMMON).group(AITItemGroups.MAIN), InstrumentTags.GOAT_HORNS);

    public static final Item CLASSIC_GOAT_HORN = new TardisGoatHorn(new AItemSettings().rarity(Rarity.EPIC).group(AITItemGroups.MAIN), InstrumentTags.GOAT_HORNS, TardisGoatHorn.Protocols.HAIL);

    public static final Item NETHERITE_GOAT_HORN = new TardisGoatHorn(new AItemSettings().rarity(Rarity.RARE).group(AITItemGroups.MAIN), InstrumentTags.GOAT_HORNS, TardisGoatHorn.Protocols.HAIL);*/

    @NoEnglish
    public static final Item GOLD_KEY_UPGRADE_SMITHING_TEMPLATE = new KeySmithingTemplateItem(
            new AItemSettings().group(AITItemGroups.MAIN), "Gold Key", "Gold Nugget");
    @NoEnglish
    public static final Item NETHERITE_KEY_UPGRADE_SMITHING_TEMPLATE = new KeySmithingTemplateItem(
            new AItemSettings().group(AITItemGroups.MAIN), "Netherite Key", "Netherite Scrap");
    @NoEnglish
    public static final Item CLASSIC_KEY_UPGRADE_SMITHING_TEMPLATE = new KeySmithingTemplateItem(
            new AItemSettings().group(AITItemGroups.MAIN), "Classic Key", "Amethyst Shard");


    // Crafting items
    public static final Item ZEITON_SHARD = new ZeitonShardItem(new AItemSettings());
    public static final Item CHARGED_ZEITON_CRYSTAL = new ChargedZeitonCrystalItem(
            new AItemSettings().group(AITItemGroups.MAIN).maxCount(1));
    public static final Item ZEITON_DUST = new Item(
            new AItemSettings().food(ZEITON_DUST_FOOD));
    public static final Item SUPERHEATED_ZEITON = new Item(new AItemSettings().group(AITItemGroups.MAIN));
    public static final Item PLASMIC_MATERIAL = new Item(new AItemSettings().group(AITItemGroups.MAIN));
    public static final Item CORAL_FRAGMENT = new Item(new AItemSettings().group(AITItemGroups.MAIN));
    public static final Item CORAL_CAGE = new Item(new AItemSettings().group(AITItemGroups.MAIN));
    public static final Item PERSONALITY_MATRIX = new PersonalityMatrixItem(new AItemSettings().group(AITItemGroups.MAIN));

    // Machine parts
    public static final Item ARTRON_FLUID_LINK = new FluidLinkItem(AbstractLinkItem.Type.ARTRON,
            new AItemSettings().group(AITItemGroups.FABRICATOR));
    public static final Item DATA_FLUID_LINK = new FluidLinkItem(AbstractLinkItem.Type.DATA,
            new AItemSettings().group(AITItemGroups.FABRICATOR));
    public static final Item VORTEX_FLUID_LINK = new FluidLinkItem(AbstractLinkItem.Type.VORTEX,
            new AItemSettings().group(AITItemGroups.FABRICATOR));

    public static final Item ARTRON_MERCURIAL_LINK = new MercurialLinkItem(AbstractLinkItem.Type.ARTRON,
            new AItemSettings().group(AITItemGroups.FABRICATOR));
    public static final Item DATA_MERCURIAL_LINK = new MercurialLinkItem(AbstractLinkItem.Type.DATA,
            new AItemSettings().group(AITItemGroups.FABRICATOR));
    public static final Item VORTEX_MERCURIAL_LINK = new MercurialLinkItem(AbstractLinkItem.Type.VORTEX,
            new AItemSettings().group(AITItemGroups.FABRICATOR));

    public static final Item ORTHOGONAL_ENGINE_FILTER = new MachinePartItem(MachinePartItem.Type.ORTHOGONAL_ENGINE_FILTER,
            new AItemSettings().group(AITItemGroups.FABRICATOR));
    public static final Item TRANSWARP_RESONATOR = new MachinePartItem(MachinePartItem.Type.TRANSWARP_RESONATOR,
            new AItemSettings().group(AITItemGroups.FABRICATOR));
    public static final Item PHOTON_ACCELERATOR = new MachinePartItem(MachinePartItem.Type.PHOTON_ACCELERATOR,
            new AItemSettings() .group(AITItemGroups.FABRICATOR));
    public static final Item HYPERION_CORE_SHAFT = new MachinePartItem(MachinePartItem.Type.HYPERION_CORE_SHAFT,
            new AItemSettings().group(AITItemGroups.FABRICATOR));

    // Components
    public static final Item DEMATERIALIZATION_CIRCUIT = new SubSystemItem(
            new AItemSettings().group(AITItemGroups.FABRICATOR), SubSystem.Id.DEMAT);

    public static final Item SHIELDS_CIRCUIT = new SubSystemItem(
            new AItemSettings().group(AITItemGroups.FABRICATOR), SubSystem.Id.SHIELDS);
    public static final Item BACKUP_CIRCUIT = new SubSystemItem(
            new AItemSettings().group(AITItemGroups.FABRICATOR), SubSystem.Id.EMERGENCY_POWER);
    public static final Item GRAVITATIONAL_CIRCUIT = new SubSystemItem(
            new AItemSettings().group(AITItemGroups.FABRICATOR), SubSystem.Id.GRAVITATIONAL);
    public static final Item CHAMELEON_CIRCUIT = new SubSystemItem(
            new AItemSettings().group(AITItemGroups.FABRICATOR), SubSystem.Id.CHAMELEON);
    public static final Item DESPERATION_CIRCUIT = new SubSystemItem(
            new AItemSettings().group(AITItemGroups.FABRICATOR), SubSystem.Id.DESPERATION);
    public static final Item STABILISERS = new SubSystemItem(
            new AItemSettings().group(AITItemGroups.FABRICATOR), SubSystem.Id.STABILISERS);
    public static final Item LIFE_SUPPORT = new SubSystemItem(
            new AItemSettings().group(AITItemGroups.FABRICATOR), SubSystem.Id.LIFE_SUPPORT);

    @NoEnglish
    public static final Item GALLIFREY_FALLS_PAINTING = new AITDecorationItem(AITEntityTypes.GALLIFREY_FALLS_PAINTING_ENTITY_TYPE,
            new AItemSettings().group(AITItemGroups.MAIN));
    @NoEnglish
    public static final Item TRENZALORE_PAINTING = new AITDecorationItem(AITEntityTypes.TRENZALORE_PAINTING_ENTITY_TYPE,
            new AItemSettings().group(AITItemGroups.MAIN));

    // Blueprint
    public static final Item BLUEPRINT = new BlueprintItem(
            new AItemSettings().rarity(Rarity.EPIC));

    // Waypoint-related
    public static final Item WAYPOINT_CARTRIDGE = new WaypointItem(
            new AItemSettings().group(AITItemGroups.MAIN).maxCount(1));

    // Music discs
    @NoEnglish
    public static final Item DRIFTING_MUSIC_DISC = new MusicDiscItem(1, AITSounds.DRIFTING_MUSIC,
            new AItemSettings().maxCount(1).rarity(Rarity.RARE), 169);

    @NoEnglish
    public static final Item WONDERFUL_TIME_IN_SPACE_MUSIC_DISC = new MusicDiscItem(1, AITSounds.WONDERFUL_TIME_IN_SPACE,
            new AItemSettings().maxCount(1).rarity(Rarity.RARE), 73);


    @NoEnglish
    public static final Item VENUS_MUSIC_DISC = new MusicDiscItem(1, AITSounds.VENUS_MUSIC,
            new AItemSettings().maxCount(1).rarity(Rarity.RARE), 342);

    @NoEnglish
    public static final Item GOOD_MAN_MUSIC_DISC = new MusicDiscItem(1, AITSounds.GOOD_MAN_MUSIC,
            new AItemSettings().maxCount(1).rarity(Rarity.RARE), 342);

    @NoEnglish
    public static final Item EARTH_MUSIC_DISC = new MusicDiscItem(1, AITSounds.EARTH_MUSIC,
            new AItemSettings().maxCount(1).rarity(Rarity.RARE), 315);


    // Block controls

     public static final Item REDSTONE_CONTROL = new
             GenericControlBlockItem(AITBlocks.REDSTONE_CONTROL_BLOCK, new
     AItemSettings().group(AITItemGroups.MAIN));
    public static boolean isUnlockedOnThisDay(int month, int day) {
        return getAdventDates(month, Calendar.JANUARY, day, 6);
    }

    private static void addDrinks(FabricItemGroupEntries entries) {
        DrinkRegistry.getInstance().toList().stream()/*.filter(entry -> entry != DrinkRegistry.EMPTY_MUG)*/
                .map(entry -> DrinkUtil.setDrink(new ItemStack(AITItems.MUG),
                        entry)).forEach(stack -> entries.add(stack, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS));
    }

    public static boolean getAdventDates(int monthBegin, int monthEnd, int dayBegin, int dayEnd) {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Handle the case where the date range spans across two years
        if (monthBegin == Calendar.DECEMBER && monthEnd == Calendar.JANUARY) {
            return (currentMonth == Calendar.DECEMBER && currentDay >= dayBegin) || (currentMonth == Calendar.JANUARY && currentDay <= dayEnd);
        } else {
            return currentMonth >= monthBegin && currentMonth <= monthEnd && currentDay >= dayBegin && currentDay <= dayEnd;
        }
    }

    public static boolean isInAdvent() {
        return getAdventDates(Calendar.DECEMBER, Calendar.JANUARY, 26, 6);
    }

    public static List<Item> get() {
        List<Item> list = new ArrayList<>();

        for (Item item : Registries.ITEM) {
            if (Registries.ITEM.getId(item).getNamespace().equalsIgnoreCase(AITMod.MOD_ID)) {
                list.add(item);
            }
        }

        return list;
    }

    static {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.addAfter(Items.MUSIC_DISC_RELIC, DRIFTING_MUSIC_DISC);
            entries.addAfter(DRIFTING_MUSIC_DISC, WONDERFUL_TIME_IN_SPACE_MUSIC_DISC);
            entries.addAfter(WONDERFUL_TIME_IN_SPACE_MUSIC_DISC, EARTH_MUSIC_DISC);
            entries.addAfter(EARTH_MUSIC_DISC, VENUS_MUSIC_DISC, GOOD_MAN_MUSIC_DISC);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            addDrinks(entries);
        });

        /*ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.addAfter(Items.BUCKET);
        });*/

        ItemGroupEvents.modifyEntriesEvent(RegistryKey.of(RegistryKeys.ITEM_GROUP, AITItemGroups.FABRICATOR.id())).register(entries -> {
            for (BlueprintSchema schema : BlueprintRegistry.getInstance().toList()) {
                entries.add(BlueprintItem.createStack(schema));
            }
        });
    }
}

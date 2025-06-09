package dev.amble.ait.module.planet.core;

import dev.amble.ait.core.item.HandlesItem;
import dev.amble.ait.module.planet.PlanetModule;
import dev.amble.ait.module.planet.core.item.AnorthositeSwordItem;
import dev.amble.ait.module.planet.core.item.PlanetToolMaterial;
import dev.amble.ait.module.planet.core.item.SpacesuitItem;
import dev.amble.lib.container.impl.ItemContainer;
import dev.amble.lib.item.AItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import org.jetbrains.annotations.Nullable;

public class PlanetItems extends ItemContainer {

    // SPACESUIT

    public static final Item SPACESUIT_HELMET = new SpacesuitItem(ArmorMaterials.IRON, ArmorItem.Type.HELMET,
            new AItemSettings().maxDamage(240), true);

    public static final Item SPACESUIT_CHESTPLATE = new SpacesuitItem(ArmorMaterials.IRON, ArmorItem.Type.CHESTPLATE,
            new AItemSettings().maxDamage(240), true);

    public static final Item SPACESUIT_LEGGINGS = new SpacesuitItem(ArmorMaterials.IRON, ArmorItem.Type.LEGGINGS,
            new AItemSettings().maxDamage(240), true);

    public static final Item SPACESUIT_BOOTS = new SpacesuitItem(ArmorMaterials.IRON, ArmorItem.Type.BOOTS,
            new AItemSettings().maxDamage(240), true);

    // TOOLS
    public static final Item MARTIAN_STONE_SWORD = new SwordItem(PlanetToolMaterial.MARTIAN_STONE, 3, -2.4f, new AItemSettings());
    public static final Item MARTIAN_STONE_SHOVEL = new ShovelItem(PlanetToolMaterial.MARTIAN_STONE, 0, 0f, new AItemSettings());
    public static final Item MARTIAN_STONE_PICKAXE = new PickaxeItem(PlanetToolMaterial.MARTIAN_STONE, 2, 2f, new AItemSettings());
    public static final Item MARTIAN_STONE_AXE = new AxeItem(PlanetToolMaterial.MARTIAN_STONE, 3, 1f, new AItemSettings());
    public static final Item MARTIAN_STONE_HOE = new HoeItem(PlanetToolMaterial.MARTIAN_STONE, 1, 2f, new AItemSettings());

    public static final Item ANORTHOSITE_SWORD = new AnorthositeSwordItem(PlanetToolMaterial.ANORTHOSITE, 3, -2.4f, new AItemSettings());
    public static final Item ANORTHOSITE_SHOVEL = new ShovelItem(PlanetToolMaterial.ANORTHOSITE, 0, 0f, new AItemSettings());
    public static final Item ANORTHOSITE_PICKAXE = new PickaxeItem(PlanetToolMaterial.ANORTHOSITE, 2, 2f, new AItemSettings());
    public static final Item ANORTHOSITE_AXE = new AxeItem(PlanetToolMaterial.ANORTHOSITE, 3, 1f, new AItemSettings());
    public static final Item ANORTHOSITE_HOE = new HoeItem(PlanetToolMaterial.ANORTHOSITE, 1, 2f, new AItemSettings());

    public static final Item HANDLES = new HandlesItem(new AItemSettings().maxCount(1)/*.group(PlanetModule.instance().getItemGroup())*/);

    // MATERIALS
    public static final Item FABRIC = new Item(new AItemSettings());

    @Override
    public @Nullable ItemGroup getDefaultGroup() {
        return PlanetModule.instance().getItemGroup();
    }

    // ITEM GROUP TOOL PLACEMENT
    static {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.addAfter(Items.STONE_SWORD, MARTIAN_STONE_SWORD);
            entries.addAfter(MARTIAN_STONE_SWORD, ANORTHOSITE_SWORD);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.addAfter(Items.PAPER, FABRIC);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.addAfter(Items.STONE_HOE, MARTIAN_STONE_SHOVEL);
            entries.addAfter(MARTIAN_STONE_SHOVEL, MARTIAN_STONE_PICKAXE);
            entries.addAfter(MARTIAN_STONE_PICKAXE, MARTIAN_STONE_AXE);
            entries.addAfter(MARTIAN_STONE_AXE, MARTIAN_STONE_HOE);

            entries.addAfter(MARTIAN_STONE_HOE, ANORTHOSITE_SWORD);
            entries.addAfter(ANORTHOSITE_SWORD, ANORTHOSITE_SHOVEL);
            entries.addAfter(ANORTHOSITE_SHOVEL, ANORTHOSITE_PICKAXE);
            entries.addAfter(ANORTHOSITE_PICKAXE, ANORTHOSITE_AXE);
            entries.addAfter(ANORTHOSITE_AXE, ANORTHOSITE_HOE);
        });
    }
}

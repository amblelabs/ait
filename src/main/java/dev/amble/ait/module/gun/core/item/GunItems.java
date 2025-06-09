package dev.amble.ait.module.gun.core.item;

import dev.amble.lib.container.impl.ItemContainer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.util.Rarity;


public class GunItems extends ItemContainer {

    public static final Item CULT_STASER = new BaseGunItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE));
    public static final Item CULT_STASER_RIFLE = new StaserRifleItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE));
    public static final Item STASER_BOLT_MAGAZINE = new StaserBoltMagazine(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE));

    static {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.addAfter(Items.CROSSBOW, CULT_STASER);
            entries.addAfter(CULT_STASER, CULT_STASER_RIFLE);
            entries.addAfter(Items.TIPPED_ARROW, STASER_BOLT_MAGAZINE);
        });
    }
}

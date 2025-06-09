package dev.amble.ait.module.decoration.core;

import dev.amble.ait.module.decoration.DecorationModule;
import dev.amble.lib.container.impl.BlockContainer;
import dev.amble.lib.item.AItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class DecorationBlocks extends BlockContainer {



    @Override
    public Item.Settings createBlockItemSettings(Block block) {
        return new AItemSettings().group(DecorationModule.instance().getItemGroup());
    }
}

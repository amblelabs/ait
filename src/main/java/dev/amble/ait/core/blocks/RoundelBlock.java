package dev.amble.ait.core.blocks;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.util.DyeColor;

import dev.amble.ait.core.AITBlocks;

public class RoundelBlock
        extends AbstractRoundelBlock {
    private static final Map<DyeColor, Block> COLORED_ROUNDELS = Maps.newHashMap();

    public RoundelBlock(DyeColor dyeColor, AbstractBlock.Settings settings) {
        super(dyeColor, settings);
        COLORED_ROUNDELS.put(dyeColor, this);
    }

    public static Block getForColor(DyeColor color) {
        return COLORED_ROUNDELS.getOrDefault(color, AITBlocks.ROUNDEL);
    }
}

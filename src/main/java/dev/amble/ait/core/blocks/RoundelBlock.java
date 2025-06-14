package dev.amble.ait.core.blocks;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

import dev.amble.ait.core.AITBlocks;

public class RoundelBlock
        extends AbstractRoundelBlock {
    private static final Map<Integer, Block> COLORED_ROUNDELS = new HashMap<>();

    public RoundelBlock(int dyeColor, AbstractBlock.Settings settings) {
        super(dyeColor, settings);
        COLORED_ROUNDELS.put(dyeColor, this);
    }

    public static Block getForColor(int color) {
        return COLORED_ROUNDELS.getOrDefault(color, AITBlocks.ROUNDEL);
    }
}
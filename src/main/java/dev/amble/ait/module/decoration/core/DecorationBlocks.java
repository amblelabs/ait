package dev.amble.ait.module.decoration.core;

import dev.amble.lib.container.impl.BlockContainer;
import dev.amble.lib.datagen.util.NoEnglish;
import dev.amble.lib.item.AItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.Item;

import dev.amble.ait.module.decoration.DecorationModule;
import dev.amble.ait.module.decoration.core.block.RoundelBlock;

public class DecorationBlocks extends BlockContainer {
    @NoEnglish
    public static final Block MINT_ROUNDEL = new RoundelBlock(
            FabricBlockSettings.create().nonOpaque().strength(0.01F, 0.01F).pistonBehavior(PistonBehavior.NORMAL), "MINT (Version A)");
    @NoEnglish
    public static final Block MINT_ROUNDEL_SIDE = new RoundelBlock(
            FabricBlockSettings.create().nonOpaque().strength(0.01F, 0.01F).pistonBehavior(PistonBehavior.NORMAL), "MINT (Version B)");
    @NoEnglish
    public static final Block WHITE_ROUNDEL = new RoundelBlock(
            FabricBlockSettings.create().nonOpaque().strength(0.01F, 0.01F).pistonBehavior(PistonBehavior.NORMAL),"WHITE (Version A)");
    @NoEnglish
    public static final Block WHITE_ROUNDEL_SIDE = new RoundelBlock(
            FabricBlockSettings.create().nonOpaque().strength(0.01F, 0.01F).pistonBehavior(PistonBehavior.NORMAL),"WHITE (Version B)");
    @NoEnglish
    public static final Block DARK_OAK_ROUNDEL = new RoundelBlock(
            FabricBlockSettings.create().nonOpaque().strength(0.01F, 0.01F).pistonBehavior(PistonBehavior.NORMAL),"DARK OAK (Version A)");
    @NoEnglish
    public static final Block DARK_OAK_ROUNDEL_SIDE = new RoundelBlock(
            FabricBlockSettings.create().nonOpaque().strength(0.01F, 0.01F).pistonBehavior(PistonBehavior.NORMAL),"DARK OAK (Version B)");
    @NoEnglish
    public static final Block TARDIM_ROUNDEL = new RoundelBlock(
            FabricBlockSettings.create().nonOpaque().strength(0.01F, 0.01F).pistonBehavior(PistonBehavior.NORMAL),"TARDIM (Version A)");
    @NoEnglish
    public static final Block TARDIM_ROUNDEL_SIDE = new RoundelBlock(
            FabricBlockSettings.create().nonOpaque().strength(0.01F, 0.01F).pistonBehavior(PistonBehavior.NORMAL),"TARDIM (Version B)");
    @NoEnglish
    public static final Block RENAISSANCE_ROUNDEL = new RoundelBlock(
            FabricBlockSettings.create().nonOpaque().strength(0.01F, 0.01F).pistonBehavior(PistonBehavior.NORMAL),"RENAISSANCE (Version A)");
    public static final Block RENAISSANCE_ROUNDEL_SIDE = new RoundelBlock(
            FabricBlockSettings.create().nonOpaque().strength(0.01F, 0.01F).pistonBehavior(PistonBehavior.NORMAL),"RENAISSANCE (Version B)");
    @NoEnglish
    public static final Block CRYSTALLINE_BLOCK = new RoundelBlock(
            FabricBlockSettings.create().strength(0.01F, 0.01F).pistonBehavior(PistonBehavior.NORMAL).luminance(state -> 50).nonOpaque(), "CRYSTALLINE (Crystal)");




    @Override
    public Item.Settings createBlockItemSettings(Block block) {
        return new AItemSettings().group(DecorationModule.instance().getItemGroup());
    }
}

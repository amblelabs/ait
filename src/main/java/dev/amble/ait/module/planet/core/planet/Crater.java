package dev.amble.ait.module.planet.core.planet;

import static dev.amble.ait.AITMod.MOD_ID;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

/**
 * @author Codiak540
 * Why: Custom crater for Mars/Moon
 */
public class Crater extends Feature<ProbabilityConfig> {

    public static final Identifier CRATER_ID = Identifier.of(MOD_ID, "crater");

    public Crater(Codec<ProbabilityConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<ProbabilityConfig> context) {

        float rand = (float) new Random(System.currentTimeMillis()).nextInt(11) / 10;
        if (rand < (context.getConfig().probability)) {
            int radius = 5 + new Random(System.currentTimeMillis()).nextInt(10);
            BlockPos pos = context.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, context.getOrigin()).up(radius / 3);
            pos.add(0, new Random(System.currentTimeMillis()).nextInt(4), 0);
            //Hangs if ran on client-side
            if (!context.getWorld().isClient()) {
                for (BlockPos p : BlockPos.iterate(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius))) {
                    if (p.isWithinDistance(pos, radius)) {
                        if (!context.getWorld().getBlockState(p).isAir())
                            context.getWorld().setBlockState(p, Blocks.AIR.getDefaultState(), 2);
                    }
                }
                return true;
            }
            //Is client side? Return false
            return false;
        }
        return false;
    }

}
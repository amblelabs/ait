package dev.amble.ait.module.planet.core.space.planet;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import static dev.amble.ait.AITMod.MOD_ID;

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
        if (context.getWorld().isClient()) return false;
        if (!((float) context.getWorld().getRandom().nextInt(11) / 10 < (context.getConfig().probability))) return false;
        int radius = 5 + context.getWorld().getRandom().nextInt(10);
        BlockPos pos = context.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, context.getOrigin()).up(radius / 3);
        pos.add(0, context.getWorld().getRandom().nextInt(4), 0);
        for (BlockPos p : BlockPos.iterate(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius))) {
            if (p.isWithinDistance(pos, radius))
                if (!context.getWorld().getBlockState(p).isAir())
                    context.getWorld().setBlockState(p, Blocks.AIR.getDefaultState(), 2);
        }
        return true;
    }
}
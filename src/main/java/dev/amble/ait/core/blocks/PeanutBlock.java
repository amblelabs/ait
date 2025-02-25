package dev.amble.ait.core.blocks;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PeanutBlock extends Block {

    public PeanutBlock(Settings settings) {
        super(settings.strength(-1.0f, 3600000.0f).emissiveLighting((state, world, pos) -> true).luminance(value -> 128)
                .slipperiness(100));
    }

    public void explode(World world, BlockPos pos) {
        world.createExplosion(null, world.getDamageSources().outOfWorld(), null, pos.toCenterPos(), 100, true,
                World.ExplosionSourceType.MOB);
        world.createExplosion(null, null, null, pos.toCenterPos(), 100, true, World.ExplosionSourceType.BLOCK);
        world.createExplosion(null, world.getDamageSources().outOfWorld(), null, pos.toCenterPos(), 100, true,
                World.ExplosionSourceType.TNT);
    }
}

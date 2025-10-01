package dev.amble.ait.core.blocks;

import dev.amble.ait.core.tardis.util.TardisUtil;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PeanutBlock extends Block {
    private static final float EXPLOSION_POWER = 100;

    public PeanutBlock(Settings settings) {
        super(settings.strength(-1.0f, 3600000.0f).emissiveLighting((state, world, pos) -> true).luminance(value -> 128)
                .slipperiness(100));
    }

    public void explode(World world, BlockPos pos) {
        world.createExplosion(null, world.getDamageSources().outOfWorld(), TardisUtil.EXPLOSION_BEHAVIOR, pos.toCenterPos(), EXPLOSION_POWER, TardisUtil.doCreateFire(world),
                World.ExplosionSourceType.MOB);
        world.createExplosion(null, null, TardisUtil.EXPLOSION_BEHAVIOR, pos.toCenterPos(), EXPLOSION_POWER, TardisUtil.doCreateFire(world), World.ExplosionSourceType.BLOCK);
        world.createExplosion(null, world.getDamageSources().outOfWorld(), TardisUtil.EXPLOSION_BEHAVIOR, pos.toCenterPos(), EXPLOSION_POWER, TardisUtil.doCreateFire(world),
                World.ExplosionSourceType.TNT);
    }
}

package dev.amble.ait.core.item;

import net.minecraft.item.Item;


public class CobbledSnowballItem extends Item {
    public CobbledSnowballItem(Settings settings) {
        super(settings);
    }

//    @Override
//    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
//        ItemStack itemStack = user.getStackInHand(hand);
//        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.1f);
//        if (!world.isClient) {
//            CobbledSnowballEntity cobbledSnowballEntity = new CobbledSnowballEntity(AITEntityTypes.COBBLED_SNOWBALL_TYPE, world);
//            cobbledSnowballEntity.setOwner(user);
//            cobbledSnowballEntity.setPos(user.getX(), user.getEyeY() - (double)0.1f, user.getZ());
//            cobbledSnowballEntity.setItem(itemStack);
//            cobbledSnowballEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.5f, 1.0f);
//            world.spawnEntity(cobbledSnowballEntity);
//        }
//        user.incrementStat(Stats.USED.getOrCreateStat(this));
//        if (!user.getAbilities().creativeMode) {
//            itemStack.decrement(1);
//        }
//        return TypedActionResult.success(itemStack, world.isClient());
//    }
}

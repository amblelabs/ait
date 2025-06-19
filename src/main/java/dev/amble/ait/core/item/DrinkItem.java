package dev.amble.ait.core.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.drinks.Drink;
import dev.amble.ait.core.drinks.DrinkRegistry;
import dev.amble.ait.core.drinks.DrinkUtil;

public class DrinkItem extends Item {
    private static final int MAX_USE_TIME = 32;

    public DrinkItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        Drink drink = DrinkUtil.getDrink(stack);
        if (drink != null) {
            return "ait.item.drink." + drink.id().getPath();
        }
        return this.getOrCreateTranslationKey();
    }

    @Override
    public ItemStack getDefaultStack() {
        return DrinkUtil.setDrink(super.getDefaultStack(), DrinkRegistry.EMPTY_MUG);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {

        PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity)user : null;
        if (playerEntity instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity)playerEntity, stack);
        }
        if (!world.isClient) {
            DrinkUtil.applyEffects(stack, user);
        }
        if (playerEntity != null) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!playerEntity.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }
        if (playerEntity == null || !playerEntity.getAbilities().creativeMode) {
            if (stack.isEmpty()) {
                return new ItemStack(AITItems.MUG);
            }
            if (playerEntity != null) {
                playerEntity.getInventory().insertStack(new ItemStack(AITItems.MUG));
            }
        }
        user.emitGameEvent(GameEvent.DRINK);
        return stack;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity playerEntity = context.getPlayer();
        ItemStack itemStack = context.getStack();
        BlockState blockState = world.getBlockState(blockPos);

        if (context.getSide() != Direction.DOWN && blockState.isIn(BlockTags.CONVERTABLE_TO_MUD) && DrinkUtil.getDrink(itemStack) == DrinkRegistry.EMPTY_MUG) {
            world.playSound(null, blockPos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 1.0f, 1.0f);
            playerEntity.setStackInHand(context.getHand(), ItemUsage.exchangeStack(itemStack, playerEntity, new ItemStack(AITItems.MUG)));
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
            if (!world.isClient) {
                ServerWorld serverWorld = (ServerWorld)world;
                for (int i = 0; i < 5; ++i) {
                    serverWorld.spawnParticles(ParticleTypes.SPLASH, (double)blockPos.getX() + world.random.nextDouble(), blockPos.getY() + 1, (double)blockPos.getZ() + world.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
                }
            }
            world.playSound(null, blockPos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, GameEvent.FLUID_PLACE, blockPos);
            world.setBlockState(blockPos, Blocks.MUD.getDefaultState());
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (DrinkUtil.getDrink(stack) != DrinkRegistry.getInstance().get(AITMod.id("mug_empty"))) {
            return ItemUsage.consumeHeldItem(world, user, hand);
        }

        return TypedActionResult.fail(stack);
    }



    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        DrinkUtil.buildTooltip(stack, tooltip, 1.0f);
    }
}

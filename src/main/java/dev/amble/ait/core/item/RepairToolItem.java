package dev.amble.ait.core.item;

import java.util.List;

import dev.amble.ait.core.engine.block.SubSystemBlockEntity;
import dev.amble.ait.core.entities.ConsoleControlEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.block.generic.GenericStructureSystemBlockEntity;

public class RepairToolItem extends Item {
    public RepairToolItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        tooltip.add(Text.translatable("tooltip.ait.repair_tool").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        float f;
        if (!(user instanceof PlayerEntity playerEntity)) {
            return;
        }
        if ((double)(f = RepairToolItem.getPullProgress(this.getMaxUseTime(stack) - remainingUseTicks)) < 0.1) {
            return;
        }

        HitResult hitResult = playerEntity.raycast(16, 0.0f, false);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            Vec3d pos3d = hitResult.getPos();
            BlockPos pos = new BlockPos((int) pos3d.x, (int) pos3d.y, (int) pos3d.z);
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof SubSystemBlockEntity subSystem) {
                if (subSystem.system() instanceof DurableSubSystem durable) {
                    playerEntity.sendMessage(Text.literal(durable.durability() + "/" + DurableSubSystem.MAX_DURABILITY).setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true)), true);
                    world.playSound(null, pos, SoundEvents.BLOCK_ANCIENT_DEBRIS_HIT, SoundCategory.BLOCKS, 0.5f, 0.8f);
                    if (durable.durability() < DurableSubSystem.MAX_DURABILITY) {
                        int val = world.getRandom().nextBetween(2, 10);
                        val = (val * DurableSubSystem.MAX_DURABILITY) / 100;
                        durable.addDurability(val);
                        stack.damage(1, playerEntity, p -> p.sendToolBreakStatus(playerEntity.getActiveHand()));

                        world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.BLOCKS, 0.5f, 1.5f);

                        for (int i = 0; i < (val / 2); i++) {
                            world.addImportantParticle(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0.1f, 0);
                        }
                        return;
                    }
                }
            }
        }

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult result = (EntityHitResult) hitResult;

            if (result.getEntity() instanceof ConsoleControlEntity consoleControl) {
                playerEntity.sendMessage(Text.literal(consoleControl.getDurability() + "/" + ConsoleControlEntity.MAX_DURABILITY).setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true)), true);
                world.playSound(null, consoleControl.getBlockPos(), SoundEvents.BLOCK_ANCIENT_DEBRIS_HIT, SoundCategory.BLOCKS, 0.5f, 0.8f);
                if (consoleControl.getDurability() < DurableSubSystem.MAX_DURABILITY) {
                    consoleControl.addDurability(world.getRandom().nextFloat());
                    stack.damage(1, playerEntity, p -> p.sendToolBreakStatus(playerEntity.getActiveHand()));

                    world.playSound(null, consoleControl.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.BLOCKS, 0.5f, 1.5f);

                    for (int i = 0; i < (world.getRandom().nextBetween(2, 5) / 2); i++) {
                        world.addImportantParticle(ParticleTypes.ENCHANT, consoleControl.getX() + 0.5, consoleControl.getY() + 1, consoleControl.getZ() + 0.5, 0, 0.1f, 0);
                    }
                    return;
                }
            }
        }

        world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.BLOCK_CHAIN_HIT, SoundCategory.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + f * 0.5f);
        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    public static float getPullProgress(int useTicks) {
        float f = (float)useTicks / 20.0f;
        if ((f = (f * f + f * 2.0f) / 3.0f) > 1.0f) {
            f = 1.0f;
        }
        return f;
    }
}
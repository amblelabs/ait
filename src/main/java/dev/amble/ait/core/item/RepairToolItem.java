package dev.amble.ait.core.item;

import dev.amble.ait.core.engine.block.SubSystemBlockEntity;
import dev.amble.ait.core.entities.ConsoleControlEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RepairToolItem extends Item {
    private static final int MAX_CHARGE = 80;

    public RepairToolItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }


    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return MAX_CHARGE;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTime) {
        if (world.isClient || !(user instanceof PlayerEntity player)) {
            return;
        }

        int chargeTime = MAX_CHARGE - remainingUseTime;

        if (chargeTime < MAX_CHARGE) {
            return;
        }

        if (!hasValidTarget(world, player)) {
            return;
        }

        performRepair(stack, world, player);
        player.getItemCooldownManager().set(this, 20);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        return super.finishUsing(stack, world, user);
    }

    private void performRepair(ItemStack stack, World world, PlayerEntity player) {
        Vec3d cameraPos = player.getCameraPosVec(1.0F);
        Vec3d rotation = player.getRotationVec(1.0F);
        double reachDistance = 5.0D;
        Vec3d endPos = cameraPos.add(rotation.x * reachDistance, rotation.y * reachDistance, rotation.z * reachDistance);
        Box box = player.getBoundingBox().stretch(rotation.multiply(reachDistance)).expand(1.0D, 1.0D, 1.0D);

        EntityHitResult entityHit = ProjectileUtil.raycast(
                player,
                cameraPos,
                endPos,
                box,
                entity -> !entity.isSpectator() && entity.canHit(),
                reachDistance * reachDistance
        );

        if (entityHit != null && entityHit.getType() == HitResult.Type.ENTITY) {
            Entity targetEntity = entityHit.getEntity();
            if (targetEntity instanceof ConsoleControlEntity consoleControl) {
                consoleControl.run(player, world, false);
                return;
            }
        }

        HitResult blockHitResult = player.raycast(reachDistance, 0.0F, false);
        if (blockHitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) blockHitResult;
            BlockPos pos = blockHit.getBlockPos();
            BlockEntity be = world.getBlockEntity(pos);

            if (be instanceof SubSystemBlockEntity subSystemBE) {
                subSystemBE.useOn(world.getBlockState(pos), world, player.isSneaking(), player, stack);
            }
        }
    }

    private boolean hasValidTarget(World world, PlayerEntity player) {
        Vec3d cameraPos = player.getCameraPosVec(1.0F);
        Vec3d rotation = player.getRotationVec(1.0F);
        double reachDistance = 5.0D;
        Vec3d endPos = cameraPos.add(rotation.x * reachDistance, rotation.y * reachDistance, rotation.z * reachDistance);
        Box box = player.getBoundingBox().stretch(rotation.multiply(reachDistance)).expand(1.0D, 1.0D, 1.0D);

        EntityHitResult entityHit = ProjectileUtil.raycast(
                player,
                cameraPos,
                endPos,
                box,
                entity -> !entity.isSpectator() && entity.canHit(),
                reachDistance * reachDistance
        );

        if (entityHit != null && entityHit.getType() == HitResult.Type.ENTITY) {
            if (entityHit.getEntity() instanceof ConsoleControlEntity) {
                return true;
            }
        }

        HitResult blockHitResult = player.raycast(reachDistance, 0.0F, false);
        if (blockHitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) blockHitResult;
            return world.getBlockEntity(blockHit.getBlockPos()) instanceof SubSystemBlockEntity;
        }

        return false;
    }
}
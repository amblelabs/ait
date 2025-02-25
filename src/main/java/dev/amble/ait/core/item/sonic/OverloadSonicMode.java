package dev.amble.ait.core.item.sonic;


import net.minecraft.block.*;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import dev.amble.ait.core.AITTags;
import dev.amble.ait.data.schema.sonic.SonicSchema;

public class OverloadSonicMode extends SonicMode {

    private static final ItemStack TOOL = new ItemStack(Items.DIAMOND_PICKAXE);

    static {
        TOOL.addEnchantment(Enchantments.FORTUNE, 5);
    }

    protected OverloadSonicMode(int index) {
        super(index);
    }

    @Override
    public Text text() {
        return Text.translatable("sonic.ait.mode.overload").formatted(Formatting.RED, Formatting.BOLD);
    }

    @Override
    public void stopUsing(ItemStack stack, World world, LivingEntity user, int ticks, int ticksLeft) {
        if (!(world instanceof ServerWorld serverWorld))
            return;

        this.process(serverWorld, user, ticks);
    }

    @Override
    public void tick(ItemStack stack, World world, LivingEntity user, int ticks, int ticksLeft) {
        if (!(world instanceof ServerWorld serverWorld) || ticks % 10 != 0)
            return;

        this.process(serverWorld, user, ticks);
    }

    private void process(ServerWorld world, LivingEntity user, int ticks) {
        HitResult hitResult = SonicMode.getHitResult(user);

        if (hitResult instanceof BlockHitResult blockHit)
            this.overloadBlock(blockHit.getBlockPos(), world, user, ticks);
    }

    private void overloadBlock(BlockPos pos, ServerWorld world, LivingEntity user, int ticks) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (!world.getBlockState(pos).isIn(AITTags.Blocks.SONIC_INTERACTABLE))
            return;

        if (canLit(ticks)
                && block instanceof TntBlock) {
            TntBlock.primeTnt(world, pos);

            world.removeBlock(pos, false);
            world.emitGameEvent(user, GameEvent.BLOCK_DESTROY, pos);
            return;
        }

    }

    private static boolean canSmelt(int ticks) {
        return ticks >= 40;
    }

    private static boolean canExtract(int ticks) {
        return ticks >= 50;
    }

    private static boolean canDisassemble(int ticks) {
        return ticks >= 40;
    }

    private static boolean canMelt(int ticks) {
        return ticks >= 30;
    }

    private static boolean canShatter(int ticks) {
        return ticks >= 20;
    }

    private static boolean canLit(int ticks) {
        return ticks >= 10;
    }

    private static boolean canCrack(int ticks) {
        return ticks >= 80;
    }

    private static boolean canResonateConcrete(int ticks) {
        return ticks >= 800;
    }

    @Override
    public int maxTime() {
        return 5 * 60 * 20; // 5 minutes
    }

    @Override
    public Identifier model(SonicSchema.Models models) {
        return models.overload();
    }
}

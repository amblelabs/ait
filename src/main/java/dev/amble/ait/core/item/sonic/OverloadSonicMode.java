package dev.amble.ait.core.item.sonic;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.*;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import dev.amble.ait.core.AITBlocks;
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

        if (canLit(ticks)
                && block.getDefaultState().contains(RedstoneTorchBlock.LIT)) {
            world.playSound(null, pos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 1.0f,
                    world.getRandom().nextFloat() * 0.4f + 0.8f);

            world.breakBlock(pos, true);
            world.emitGameEvent(user, GameEvent.BLOCK_DESTROY, pos);
            return;
        }

        if (canShatter(ticks)
                && (block == AITBlocks.CONSOLE
                || state.isIn(ConventionalBlockTags.GLASS_PANES)
                || state.isIn(ConventionalBlockTags.GLASS_BLOCKS)
                || state.isIn(BlockTags.LEAVES)))  {
            world.breakBlock(pos, true);
            world.emitGameEvent(user, GameEvent.BLOCK_DESTROY, pos);
            return;
        }

        if (canMelt(ticks)
                && (state.isIn(BlockTags.ICE))) {
            world.breakBlock(pos, false);
            world.setBlockState(pos, Blocks.WATER.getDefaultState());

            world.emitGameEvent(user, GameEvent.BLOCK_CHANGE, pos);
            return;
        }

        if (canDisassemble(ticks)
                && state.isIn(BlockTags.STONE_BRICKS)) {
            world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f,
                    new ItemStack(Items.STONE, 4)));

            world.breakBlock(pos, false);
            world.emitGameEvent(user, GameEvent.BLOCK_DESTROY, pos);
            return;
        }

        if (canDisassemble(ticks)
                && (block == Blocks.BRICKS || block == Blocks.BRICK_WALL))  {
            world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f,
                    new ItemStack(Items.BRICK, 4)));

            world.breakBlock(pos, false);
            world.emitGameEvent(user, GameEvent.BLOCK_DESTROY, pos);
            return;
        }

        if (canDisassemble(ticks)
                && state.isIn(BlockTags.SNOW))  {
            world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f,
                    new ItemStack(Items.SNOWBALL, 2)));

            world.breakBlock(pos, false);
            world.emitGameEvent(user, GameEvent.BLOCK_DESTROY, pos);
            return;
        }

        if (canDisassemble(ticks)
                && (block == Blocks.NETHER_BRICKS
                || block == Blocks.RED_NETHER_BRICKS
                || block == Blocks.NETHER_BRICK_WALL
                || block == Blocks.RED_NETHER_BRICK_WALL))  {
            world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f,
                    new ItemStack(Items.NETHER_BRICK, 4)));

            world.breakBlock(pos, false);
            world.emitGameEvent(user, GameEvent.BLOCK_DESTROY, pos);
            return;
        }

        if (canSmelt(ticks)
                && state.isIn(BlockTags.SAND)) {
            world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS,
                    1f, world.getRandom().nextFloat() * 0.4f + 0.8f);

            dropItem(pos, new ItemStack(Items.GLASS), world);

            world.removeBlock(pos, false);
            world.emitGameEvent(user, GameEvent.BLOCK_DESTROY, pos);
            return;
        }

        if (canResonateConcrete(ticks)
                && (block == Blocks.BLACK_CONCRETE
                || block == Blocks.CYAN_CONCRETE
                || block == Blocks.BLUE_CONCRETE
                || block == Blocks.BROWN_CONCRETE
                || block == Blocks.GRAY_CONCRETE
                || block == Blocks.GREEN_CONCRETE
                || block == Blocks.MAGENTA_CONCRETE
                || block == Blocks.ORANGE_CONCRETE
                || block == Blocks.PINK_CONCRETE
                || block == Blocks.RED_CONCRETE
                || block == Blocks.WHITE_CONCRETE
                || block == Blocks.PURPLE_CONCRETE
                || block == Blocks.LIGHT_GRAY_CONCRETE
                || block == Blocks.LIGHT_BLUE_CONCRETE
                || block == Blocks.LIME_CONCRETE)) {
            world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f,
                    new ItemStack(Items.AIR, 4)));

            world.breakBlock(pos, false);
            world.emitGameEvent(user, GameEvent.BLOCK_DESTROY, pos);
            return;
        }

        if (canExtract(ticks)
                && state.isIn(ConventionalBlockTags.ORES)) {
            BlockState newState = this.guessOreBase(block);

            if (newState == null)
                return;

            world.setBlockState(pos, newState);

            LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(world)
                    .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                    .add(LootContextParameters.BLOCK_STATE, state)
                    .addOptional(LootContextParameters.BLOCK_ENTITY, world.getBlockEntity(pos))
                    .addOptional(LootContextParameters.THIS_ENTITY, user)
                    .add(LootContextParameters.TOOL, TOOL);

            for (ItemStack dropped : state.getDroppedStacks(builder)) {
                dropItem(pos, dropped, world);
            }

            world.emitGameEvent(user, GameEvent.BLOCK_CHANGE, pos);
        }
    }

    private void dropItem(BlockPos pos, ItemStack stack, World world) {
        world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, stack));
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

    @Nullable private BlockState guessOreBase(Block block) {
        if (block == Blocks.NETHER_GOLD_ORE || block == Blocks.NETHER_QUARTZ_ORE)
            return Blocks.NETHERRACK.getDefaultState();

        // something_material_ore -> something
        // material_ore -> stone
        // martian_material_ore -> martian -> martian_stone

        Identifier id = Registries.BLOCK.getId(block);
        String ore = id.getPath();

        ore = ore.substring(0, ore.length() - 4);
        int i = ore.lastIndexOf('_');

        ore = i == -1 ? "stone" : ore.substring(0, i);
        id = id.withPath(ore);

        BlockState newState = Registries.BLOCK.get(id).getDefaultState();

        if (newState.isAir()) {
            id = id.withSuffixedPath("_stone");
            newState = Registries.BLOCK.get(id).getDefaultState();

            if (newState.isAir())
                return null;
        }

        return newState;
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

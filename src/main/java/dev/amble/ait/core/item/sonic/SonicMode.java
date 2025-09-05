package dev.amble.ait.core.item.sonic;

import java.util.function.Function;

import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.advancement.TardisCriterions;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import dev.amble.ait.data.enummap.Ordered;
import dev.amble.ait.data.schema.sonic.SonicSchema;

public abstract class SonicMode implements Ordered {

    private static final int MAX_DISTANCE = 16;

    public static class Modes {
        public static final SonicMode[] VALUES = new SonicMode[4];
        private static int lastIndex = 0;

        public static final SonicMode INTERACTION = register(InteractionSonicMode::new);
        public static final SonicMode OVERLOAD = register(OverloadSonicMode::new);
        public static final SonicMode SCANNING = register(ScanningSonicMode::new);
        public static final SonicMode TARDIS = register(TardisSonicMode::new);

        public static final SonicMode INACTIVE = new InactiveSonicMode();

        public static SonicMode register(Function<Integer, SonicMode> consumer) {
            SonicMode mode = consumer.apply(lastIndex);
            VALUES[lastIndex] = mode;

            lastIndex++;
            return mode;
        }

        public static SonicMode next(SonicMode mode) {
            int nextIndex = mode.index() + 1;

            if (nextIndex == VALUES.length)
                return VALUES[0];

            return VALUES[nextIndex];
        }

        public static SonicMode previous(SonicMode mode) {
            int previousIndex = mode.index() - 1;

            if (previousIndex < 0)
                return VALUES[VALUES.length - 1];

            return VALUES[previousIndex];
        }

        public static SonicMode get(int index) {
            if (index == -1)
                return INACTIVE;

            return VALUES[index];
        }

        public static SonicMode getAndWrap(Integer index) {
            if (index == null)
                return INACTIVE;

            while (index >= VALUES.length) {
                index -= VALUES.length;
            }

            return get(index);
        }

        public static int size() {
            return VALUES.length;
        }
    }

    private final int index;

    protected SonicMode(int index) {
        this.index = index;
    }

    public SonicMode next() {
        return Modes.next(this);
    }

    public SonicMode previous() {
        return Modes.previous(this);
    }

    public abstract Text text();

    public abstract int maxTime();

    public boolean startUsing(ItemStack stack, World world, PlayerEntity user, Hand hand) {
        return true;
    }

    public void tick(ItemStack stack, World world, LivingEntity user, int ticks, int ticksLeft) { }

    public void stopUsing(ItemStack stack, World world, LivingEntity user, int ticks, int ticksLeft) { }

    public void finishUsing(ItemStack stack, World world, LivingEntity user) {
        this.stopUsing(stack, world, user, this.maxTime(), 0);
    }

    public abstract Identifier model(SonicSchema.Models models);

    public int fuelCost() {
        return 1;
    }

    public static HitResult getHitResultForOutline(LivingEntity user) {
        return getHitResultForOutline(user, MAX_DISTANCE);
    }
    public static HitResult getHitResultForOutline(LivingEntity user, double distance) {
        BlockHitResult hitResult = null;

        if (user instanceof PlayerEntity player) {
            Vec3d eyePos = player.getCameraPosVec(1.0F);
            Vec3d rotation = player.getRotationVec(1.0F);
            Vec3d end = eyePos.add(rotation.multiply(distance));
            hitResult = player.getWorld().raycast(new RaycastContext(eyePos, end, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));
        }

        return hitResult;
    }
    public static HitResult getHitResult(LivingEntity user) {
        return getHitResult(user, MAX_DISTANCE);
    }
    public static HitResult getHitResult(LivingEntity user, double distance) {
        return ProjectileUtil.getCollision(user, entity -> !entity.isSpectator() && entity.canHit(), distance);
    }
    public static void checkSonicWoodAdvancementConditions(World world, LivingEntity user, HitResult hitResult) {
        if (!(user instanceof ServerPlayerEntity player))
            return;

        if (hitResult instanceof BlockHitResult blockHit) {
            BlockState state = world.getBlockState(blockHit.getBlockPos());

            if (state.isIn(AITTags.Blocks.WOODEN_BLOCKS)) {
                TardisCriterions.SONIC_WOOD.trigger(player);
            }
        }
    }

    @Override
    public int index() {
        return index;
    }
}

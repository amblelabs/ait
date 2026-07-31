package dev.amble.ait.core.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.link.LinkableItem;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.ServerAlarmHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.util.FallDamageUtil;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.enummap.EnumSet;
import dev.amble.ait.data.enummap.Ordered;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class KeyItem extends LinkableItem {

    private final EnumSet<Protocols> protocols;

    public KeyItem(Settings settings, Protocols... abs) {
        super(settings.maxCount(1), true);

        this.protocols = new EnumSet<>(Protocols::values);
        this.protocols.addAll(abs);
    }

    public enum Protocols implements Ordered {
        SNAP, HAIL, PERCEPTION, SKELETON;

        @Override
        public int index() {
            return ordinal();
        }
    }

    public boolean hasProtocol(Protocols var) {
        return this.protocols.contains(var);
    }

    public static boolean isKeyInInventory(PlayerEntity player) {
        return player.getInventory().contains(AITTags.Items.KEY);
    }

    public static Collection<ItemStack> getKeysInInventory(PlayerEntity player) {
        List<ItemStack> items = new ArrayList<>();

        for (ItemStack stack : player.getInventory().main) {
            if (stack != null && stack.getItem() instanceof KeyItem)
                items.add(stack);
        }

        return items;
    }

    public static boolean hasMatchingKeyInInventory(PlayerEntity player, Tardis tardis) {
        Collection<ItemStack> keys = getKeysInInventory(player);

        for (ItemStack stack : keys) {
            KeyItem key = (KeyItem) stack.getItem();

            if (key.hasProtocol(Protocols.SKELETON))
                return true;

            Tardis found = key.getTardis(player.getWorld(), stack);

            if (found == tardis)
                return true;
        }

        return false;
    }

    public static boolean hasLinkedKeyInInventory(PlayerEntity player, Tardis tardis) {
        if (player == null || tardis == null)
            return false;

        for (int slot = 0; slot < player.getInventory().size(); slot++) {
            ItemStack stack = player.getInventory().getStack(slot);

            if (stack.getItem() instanceof KeyItem key && key.isOf(stack, tardis))
                return true;
        }

        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof ServerPlayerEntity player))
            return;

        Tardis tardis = KeyItem.getTardisStatic(world, stack);

        if (tardis == null)
            return;

        KeyItem.hailMary(tardis, stack, player, false);
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        Entity owner = entity.getOwner();

        if (!(owner instanceof ServerPlayerEntity player))
            return;

        Tardis tardis = KeyItem.getTardisStatic(entity.getWorld(), entity.getStack());

        if (tardis == null)
            return;

        tardis.loyalty().subLevel(player, 10);
        tardis.getDesktop().playSoundAtEveryConsole(AITSounds.CLOISTER);
    }

    private static boolean hailMary(Tardis tardis, ItemStack stack, ServerPlayerEntity player,
                                    boolean confirmedFallOrVoidRescue) {
        if (player.getItemCooldownManager().isCoolingDown(stack.getItem()))
            return false;

        if (tardis == null) return false;

        if (!tardis.stats().hailMary().get())
            return false;

        TravelHandler travel = tardis.travel();
        KeyItem keyType = (KeyItem) stack.getItem().asItem();

        if (travel.handbrake())
            return false;

        if (!keyType.hasProtocol(Protocols.HAIL))
           return false;

        if (!tardis.loyalty().get(player).isOf(Loyalty.Type.PILOT))
            return false;

        ServerWorld world = player.getServerWorld();
        // fail silently if destination world is blacklisted
        if (!WorldUtil.getTravelWorlds().contains(world))
            return false;

        boolean fallRescue = AITMod.CONFIG.hailMaryFallAndVoidRescue
                && (confirmedFallOrVoidRescue || shouldRescueFallOrVoid(player));
        if (player.getHealth() > 4 && !fallRescue)
            return false;

        BlockPos pos = player.getBlockPos();

        CachedDirectedGlobalPos globalPos = CachedDirectedGlobalPos.create(world, pos,
                (byte) RotationPropertyHelper.fromYaw(player.getBodyYaw()));

        if (!tardis.returnHome().startHailMary(player, globalPos, fallRescue))
            return false;

        tardis.alarm().enable(ServerAlarmHandler.AlarmType.HAIL_MARY);
        // Alarm causes normally become active on the alarm handler's next tick.
        // The Hail Mary return state also starts ticking immediately and treats a
        // disabled alarm as a cancellation, so activate it in the same transaction.
        tardis.alarm().enable();
        tardis.shields().enable();
        tardis.shields().enableVisuals();
        tardis.removeFuel(4250 + 50 * tardis.travel().instability());

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 80, 3));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 6 * 20, 3));
        if (fallRescue) {
            int levitationTicks = Math.max(1, AITMod.CONFIG.hailMaryLevitationSeconds * 20);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, levitationTicks));
            player.fallDistance = 0;
            Vec3d velocity = player.getVelocity();
            player.setVelocity(velocity.x, Math.max(velocity.y, 0), velocity.z);
            player.velocityModified = true;
            travel.antigravs().set(true);
        }
        world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX(), pos.getY(), pos.getZ(), 10, 1, 1, 1, 1);

        player.getItemCooldownManager().set(stack.getItem(), 60 * 20);

        if (!AITMod.CONFIG.keepHailMaryActive)
            tardis.stats().hailMary().set(false);
        tardis.door().previouslyLocked().set(false);

        // like a sound to show it's been called
        world.playSound(null, pos, AITSounds.CLOISTER, SoundCategory.BLOCKS, 5f, 0.1f);
        world.playSound(null, pos, SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.BLOCKS, 5f, 0.1f);
        return true;
    }

    public static boolean tryHailMaryFallRescue(ServerPlayerEntity player, float fallDistance,
                                                float damageMultiplier) {
        if (!AITMod.CONFIG.hailMaryFallAndVoidRescue
                || player.hasStatusEffect(StatusEffects.SLOW_FALLING)
                || projectedFallDamage(player, fallDistance, damageMultiplier)
                < player.getHealth() + player.getAbsorptionAmount())
            return false;

        return tryHailMaryRescue(player);
    }

    public static boolean tryHailMaryVoidRescue(ServerPlayerEntity player) {
        int range = Math.max(0, AITMod.CONFIG.hailMaryFallRescueRange);
        if (!AITMod.CONFIG.hailMaryFallAndVoidRescue || range == 0
                || player.getVelocity().y >= 0 || player.getAbilities().allowFlying
                || player.isFallFlying() || player.hasNoGravity()
                || player.hasStatusEffect(StatusEffects.SLOW_FALLING)
                || player.hasStatusEffect(StatusEffects.LEVITATION)
                || player.getY() > player.getServerWorld().getBottomY() + range)
            return false;

        return tryHailMaryRescue(player);
    }

    private static boolean tryHailMaryRescue(ServerPlayerEntity player) {
        return tryHailMaryRescue(player, true);
    }

    public static boolean tryHailMaryLethalRescue(ServerPlayerEntity player, DamageSource source) {
        boolean fallOrVoid = source.isOf(DamageTypes.FALL) || source.isOf(DamageTypes.OUT_OF_WORLD);
        if (fallOrVoid && !AITMod.CONFIG.hailMaryFallAndVoidRescue)
            return false;
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)
                && !source.isOf(DamageTypes.OUT_OF_WORLD))
            return false;

        boolean rescued = tryHailMaryRescue(player, fallOrVoid);
        if (rescued && player.getHealth() <= 0)
            player.setHealth(1);
        return rescued;
    }

    private static boolean tryHailMaryRescue(ServerPlayerEntity player, boolean confirmedFallOrVoidRescue) {
        for (int slot = 0; slot < player.getInventory().size(); slot++) {
            ItemStack stack = player.getInventory().getStack(slot);
            if (!(stack.getItem() instanceof KeyItem))
                continue;

            Tardis tardis = KeyItem.getTardisStatic(player.getWorld(), stack);
            if (hailMary(tardis, stack, player, confirmedFallOrVoidRescue))
                return true;
        }

        return false;
    }

    private static boolean shouldRescueFallOrVoid(ServerPlayerEntity player) {
        int range = Math.max(0, AITMod.CONFIG.hailMaryFallRescueRange);
        if (range == 0 || player.isOnGround() || player.getVelocity().y >= 0
                || player.getAbilities().allowFlying || player.isFallFlying()
                || player.hasVehicle() || player.hasNoGravity() || player.isClimbing()
                || player.isTouchingWater() || player.hasStatusEffect(StatusEffects.SLOW_FALLING)
                || player.hasStatusEffect(StatusEffects.LEVITATION))
            return false;

        Vec3d start = player.getPos();
        BlockHitResult hit = player.getServerWorld().raycast(new RaycastContext(start,
                start.add(0, -range, 0), RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.ANY, player));

        if (hit.getType() == HitResult.Type.MISS)
            return player.getY() <= player.getServerWorld().getBottomY() + range;

        BlockPos landingPos = hit.getBlockPos();
        if (!player.getServerWorld().getFluidState(landingPos).isEmpty())
            return false;

        BlockState landingState = player.getServerWorld().getBlockState(landingPos);
        if (landingState.isIn(BlockTags.FALL_DAMAGE_RESETTING))
            return false;

        float remainingFallDistance = (float) Math.max(0, start.y - hit.getPos().y);
        float projectedFallDistance = player.fallDistance + remainingFallDistance;
        return projectedFallDamage(player, projectedFallDistance,
                getFallDamageMultiplier(player, landingState))
                >= player.getHealth() + player.getAbsorptionAmount();
    }

    private static float projectedFallDamage(ServerPlayerEntity player, float fallDistance,
                                             float damageMultiplier) {
        if (player.getType().isIn(EntityTypeTags.FALL_DAMAGE_IMMUNE)
                || FallDamageUtil.isPreventedByAIT(player))
            return 0;

        StatusEffectInstance jumpBoost = player.getStatusEffect(StatusEffects.JUMP_BOOST);
        float jumpReduction = jumpBoost == null ? 0 : jumpBoost.getAmplifier() + 1;
        float damage = MathHelper.ceil((fallDistance - 3 - jumpReduction) * damageMultiplier);
        if (damage <= 0)
            return 0;

        DamageSource source = player.getDamageSources().fall();
        if (player.isInvulnerableTo(source))
            return 0;

        if (!source.isIn(DamageTypeTags.BYPASSES_ARMOR)) {
            damage = DamageUtil.getDamageLeft(damage, player.getArmor(),
                    (float) player.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
        }

        if (source.isIn(DamageTypeTags.BYPASSES_EFFECTS))
            return damage;

        StatusEffectInstance resistance = player.getStatusEffect(StatusEffects.RESISTANCE);
        if (resistance != null && !source.isIn(DamageTypeTags.BYPASSES_RESISTANCE)) {
            int remaining = 25 - (resistance.getAmplifier() + 1) * 5;
            damage = Math.max(damage * remaining / 25f, 0);
        }

        if (damage <= 0 || source.isIn(DamageTypeTags.BYPASSES_ENCHANTMENTS))
            return damage;

        int protection = EnchantmentHelper.getProtectionAmount(player.getArmorItems(), source);
        return DamageUtil.getInflictedDamage(damage, protection);
    }

    private static float getFallDamageMultiplier(ServerPlayerEntity player, BlockState state) {
        if (state.isOf(Blocks.SLIME_BLOCK) && !player.bypassesLandingEffects())
            return 0;
        if (state.isIn(BlockTags.BEDS))
            return 0.5f;
        if (state.isOf(Blocks.HAY_BLOCK) || state.isOf(Blocks.HONEY_BLOCK))
            return 0.2f;

        return 1;
    }

    /*
     * @Override public ActionResult useOnBlock(ItemUsageContext context) { World
     * world = context.getWorld(); BlockPos pos = context.getBlockPos();
     * PlayerEntity player = context.getPlayer(); ItemStack stack =
     * context.getStack();
     *
     * if (world.isClient()) return ActionResult.SUCCESS;
     *
     * if (player == null || !player.isSneaking()) return ActionResult.PASS;
     *
     * if (!(world.getBlockEntity(pos) instanceof ConsoleBlockEntity consoleBlock))
     * return ActionResult.PASS;
     *
     * if (consoleBlock.tardis().isEmpty()) return ActionResult.FAIL;
     *
     * Tardis tardis = consoleBlock.tardis().get();
     *
     * if (tardis.loyalty().get(player).isOf(Loyalty.Type.COMPANION)) {
     * this.link(stack, consoleBlock.tardis().get()); return ActionResult.SUCCESS; }
     *
     * player.sendMessage(Text.translatable("message.ait.tardis.trust_issue",
     * true)); return ActionResult.FAIL; }
     */

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.getItem() == AITItems.SKELETON_KEY)
            tooltip.add(Text.translatable("tooltip.ait.skeleton_key").formatted(Formatting.DARK_PURPLE));
        super.appendTooltip(stack, world, tooltip, context);
    }
}

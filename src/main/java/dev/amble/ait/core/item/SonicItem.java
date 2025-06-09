package dev.amble.ait.core.item;

import dev.amble.ait.api.AITUseActions;
import dev.amble.ait.api.ArtronHolderItem;
import dev.amble.ait.api.tardis.link.LinkableItem;
import dev.amble.ait.client.sounds.ClientSoundManager;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.item.sonic.SonicMode;
import dev.amble.ait.data.schema.sonic.SonicSchema;
import dev.amble.ait.registry.impl.SonicRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class SonicItem extends LinkableItem implements ArtronHolderItem {

    public static final double MAX_FUEL = 1000;
    public static final String MODE_KEY = "mode";
    public static final String SONIC_TYPE = "sonic_type";

    public SonicItem(Settings settings) {
        super(settings, true);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        NbtCompound nbt = stack.getOrCreateNbt();

        nbt.putInt(MODE_KEY, -1);
        nbt.putDouble(FUEL_KEY, getMaxFuel(stack));

        if (SonicRegistry.DEFAULT != null)
            nbt.putString(SONIC_TYPE, SonicRegistry.DEFAULT.id().toString());

        return stack;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return AITUseActions.SONIC;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        SonicMode mode = mode(stack);

        if (mode == null)
            return TypedActionResult.fail(stack);

        if (!this.checkFuel(stack))
            return TypedActionResult.fail(stack);

        if (user.isSneaking()) {
            mode = mode.next();
            setMode(stack, mode);

            world.playSound(user, user.getBlockPos(), AITSounds.SONIC_SWITCH, SoundCategory.PLAYERS, 1F, 1F);
            user.sendMessage(mode.text(), true);

            return TypedActionResult.consume(stack);
        }

        if (mode.startUsing(stack, world, user, hand)) {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(stack);
        }

        return TypedActionResult.fail(stack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        SonicMode mode = mode(stack);

        if (mode == SonicMode.Modes.INACTIVE)
            return;

        if (world.isClient())
            ClientSoundManager.getSonicSound().onUse((AbstractClientPlayerEntity) user);

        int ticks = mode.maxTime() - remainingUseTicks;

        if (ticks % 10 == 0) {
            removeFuel(mode.fuelCost(), stack);

            if (!this.checkFuel(stack))
                return;
        }

        mode.tick(stack, world, user, ticks, remainingUseTicks);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        SonicMode mode = mode(stack);

        if (mode == SonicMode.Modes.INACTIVE)
            return;

        if (world.isClient())
            ClientSoundManager.getSonicSound().onFinishUse((AbstractClientPlayerEntity) user);

        mode.stopUsing(stack, world, user, mode.maxTime() - remainingUseTicks, remainingUseTicks);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        SonicMode mode = mode(stack);

        if (mode == SonicMode.Modes.INACTIVE)
            return stack;

        if (world.isClient())
            ClientSoundManager.getSonicSound().onFinishUse((AbstractClientPlayerEntity) user);

        mode.finishUsing(stack, world, user);
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return mode(stack).maxTime();
    }

    private boolean checkFuel(ItemStack stack) {
        SonicMode mode = mode(stack);

        if (this.isOutOfFuel(stack) && mode != SonicMode.Modes.INACTIVE) {
            mode = SonicMode.Modes.INACTIVE;

            setMode(stack, mode);
            return false;
        }

        return true;
    }

    private static final Text TEXT_MODE = Text.translatable("message.ait.sonic.mode");
    private static final Text TEXT_ARTRON = Text.translatable("message.ait.tooltips.artron_units").formatted(Formatting.BLUE);
    private static final Text TEXT_CASING = Text.translatable("message.ait.sonic.currenttype").formatted(Formatting.DARK_GRAY, Formatting.ITALIC);

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(TEXT_MODE.copy().append(mode(stack).text()));

        int fuel = (int) Math.round(this.getCurrentFuel(stack));
        boolean acceptableFuel = this.getCurrentFuel(stack) > this.getMaxFuel(stack) / 4;

        tooltip.add(TEXT_ARTRON.copy().append(Text.literal(String.valueOf(fuel))
                .formatted(acceptableFuel ? Formatting.GREEN : Formatting.RED)));

        tooltip.add(TEXT_CASING.copy().append(schema(stack).name()));
        super.appendTooltip(stack, world, tooltip, context);
    }

    public static SonicMode mode(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        return SonicMode.Modes.getAndWrap(nbtCompound.getInt(MODE_KEY));
    }

    public static void setMode(ItemStack stack, SonicMode mode) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putInt(MODE_KEY, mode.index());
    }

    public static SonicSchema schema(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        String rawId = nbt.getString(SONIC_TYPE);

        if (rawId == null)
            return SonicRegistry.DEFAULT;

        Identifier id = Identifier.tryParse(rawId);
        SonicSchema schema = SonicRegistry.getInstance().get(id);

        return schema == null ? SonicRegistry.DEFAULT : schema;
    }

    public static void setSchema(ItemStack stack, Identifier id) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putString(SONIC_TYPE, id.toString());
    }

    public static void setSchema(ItemStack stack, SonicSchema schema) {
        SonicItem.setSchema(stack, schema.id());
    }

    @Override
    public double getMaxFuel(ItemStack stack) {
        return MAX_FUEL;
    }
}

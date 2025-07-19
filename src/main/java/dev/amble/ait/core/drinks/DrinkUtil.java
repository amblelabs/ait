package dev.amble.ait.core.drinks;

import java.util.*;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.item.PersonalityMatrixItem;

public class DrinkUtil {

    public static final String CUSTOM_DRINK_EFFECTS_KEY = "CustomDrinkEffects";
    public static final String CUSTOM_DRINK_COLOR_KEY = "CustomDrinkColor";
    public static final String DRINK_KEY = "Drink";
    private static final int DEFAULT_COLOR = PersonalityMatrixItem.colorToInt(210, 227, 252);
    private static final Text NONE_TEXT = Text.translatable("effect.none").formatted(Formatting.GRAY);
    public static final Drink EMPTY = DrinkRegistry.EMPTY_MUG;

    public static List<StatusEffectInstance> getDrinkEffects(ItemStack stack) {
        return DrinkUtil.getDrinkEffects(stack.getNbt());
    }

    public static List<StatusEffectInstance> getDrinkEffects(Drink drink, Collection<StatusEffectInstance> custom) {
        ArrayList<StatusEffectInstance> list = Lists.newArrayList();
        list.addAll(drink.getEffects());
        list.addAll(custom);
        return list;
    }

    public static List<StatusEffectInstance> getDrinkEffects(NbtCompound nbt) {
        ArrayList<StatusEffectInstance> list = Lists.newArrayList();
        list.addAll(DrinkUtil.getDrink(nbt).getEffects());
        DrinkUtil.getCustomDrinkEffects(nbt, list);
        return list;
    }

    public static List<StatusEffectInstance> getCustomDrinkEffects(ItemStack stack) {
        return DrinkUtil.getCustomDrinkEffects(stack.getNbt());
    }

    public static List<StatusEffectInstance> getCustomDrinkEffects(@Nullable NbtCompound nbt) {
        ArrayList<StatusEffectInstance> list = Lists.newArrayList();
        DrinkUtil.getCustomDrinkEffects(nbt, list);
        return list;
    }

    public static void getCustomDrinkEffects(@Nullable NbtCompound nbt, List<StatusEffectInstance> list) {
        if (nbt != null && nbt.contains(CUSTOM_DRINK_EFFECTS_KEY, NbtElement.LIST_TYPE)) {
            NbtList nbtList = nbt.getList(CUSTOM_DRINK_EFFECTS_KEY, NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbtCompound = nbtList.getCompound(i);
                StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromNbt(nbtCompound);
                if (statusEffectInstance == null) continue;
                list.add(statusEffectInstance);
            }
        }
    }

    public static int getColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null && nbtCompound.contains(CUSTOM_DRINK_COLOR_KEY, NbtElement.NUMBER_TYPE)) {
            return nbtCompound.getInt(CUSTOM_DRINK_COLOR_KEY);
        }
        Drink drink = DrinkUtil.getDrink(stack);
        return Objects.equals(drink, EMPTY) ? DEFAULT_COLOR : DrinkUtil.getColor(drink, DrinkUtil.getDrinkEffects(stack));
    }

    public static int getColor(Drink drink, Collection<StatusEffectInstance> effects) {
        if (drink.getHasColor()) {
            // TODO: use hex or normal int rgb values instead of this shit
            Vector3f vector = drink.getColor();
            return PersonalityMatrixItem.colorToInt((int) (vector.x() * 255), (int) (vector.y() * 255), (int) (vector.z() * 255));
        }
        if (effects.isEmpty()) {
            return DEFAULT_COLOR;
        }
        float f = 0.0f;
        float g = 0.0f;
        float h = 0.0f;
        int j = 0;
        for (StatusEffectInstance statusEffectInstance : effects) {
            if (!statusEffectInstance.shouldShowParticles()) continue;
            int k = statusEffectInstance.getEffectType().getColor();
            int l = statusEffectInstance.getAmplifier() + 1;
            f += (float)(l * (k >> 16 & 0xFF)) / 255.0f;
            g += (float)(l * (k >> 8 & 0xFF)) / 255.0f;
            h += (float)(l * (k & 0xFF)) / 255.0f;
            j += l;
        }
        if (j == 0) {
            return 0;
        }
        f = f / (float)j * 255.0f;
        g = g / (float)j * 255.0f;
        h = h / (float)j * 255.0f;
        return (int)f << 16 | (int) g << 8 | (int)h;
    }

    public static void applyEffects(ItemStack drinkStack, LivingEntity user) {
        if (isMilk(getDrink(drinkStack))) {
            user.clearStatusEffects();
            return;
        }

        List<StatusEffectInstance> list = DrinkUtil.getDrinkEffects(drinkStack);
        for (StatusEffectInstance statusEffectInstance : list) {
            if (statusEffectInstance.getEffectType().isInstant()) {
                statusEffectInstance.getEffectType().applyInstantEffect(null, null, user, statusEffectInstance.getAmplifier(), 1.0);
                continue;
            }
            user.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
        }
    }

    private static boolean isMilk(Drink drink) {
        return DrinkRegistry.getInstance().getOrFallback(AITMod.id("milk")).equals(drink);
    }

    public static Drink getDrink(ItemStack stack) {
        return DrinkUtil.getDrink(stack.getNbt());
    }

    public static Drink getDrink(@Nullable NbtCompound compound) {
        if (compound == null) {
            return EMPTY;
        }
        return DrinkRegistry.getInstance().get(Identifier.tryParse(compound.getString(DRINK_KEY)));
    }

    public static ItemStack setDrink(ItemStack stack, Drink drink) {
        if (drink == null || drink == EMPTY) {
            stack.removeSubNbt(DRINK_KEY);
        } else {
            stack.getOrCreateNbt().putString(DRINK_KEY, drink.id().toString());
        }
        return stack;
    }

    public static ItemStack setCustomDrinkEffects(ItemStack stack, Collection<StatusEffectInstance> effects) {
        if (effects.isEmpty()) {
            return stack;
        }
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        NbtList nbtList = nbtCompound.getList(CUSTOM_DRINK_EFFECTS_KEY, NbtElement.LIST_TYPE);
        for (StatusEffectInstance statusEffectInstance : effects) {
            nbtList.add(statusEffectInstance.writeNbt(new NbtCompound()));
        }
        nbtCompound.put(CUSTOM_DRINK_EFFECTS_KEY, nbtList);
        return stack;
    }

    public static void buildTooltip(ItemStack stack, List<Text> list, float durationMultiplier) {
        DrinkUtil.buildTooltip(DrinkUtil.getDrinkEffects(stack), list, durationMultiplier);
    }

    public static void buildTooltip(List<StatusEffectInstance> statusEffects, List<Text> list, float durationMultiplier) {
        ArrayList<Pair<EntityAttribute, EntityAttributeModifier>> list2 = Lists.newArrayList();
        if (statusEffects.isEmpty()) {
            list.add(NONE_TEXT);
        } else {
            for (StatusEffectInstance statusEffectInstance : statusEffects) {
                MutableText mutableText = statusEffectInstance.getTranslationKey() == null ? Text.empty() : Text.translatable(statusEffectInstance.getTranslationKey());
                StatusEffect statusEffect = statusEffectInstance.getEffectType();
                Map<EntityAttribute, EntityAttributeModifier> map = statusEffect.getAttributeModifiers();
                if (!map.isEmpty()) {
                    for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : map.entrySet()) {
                        EntityAttributeModifier entityAttributeModifier = entry.getValue();
                        EntityAttributeModifier entityAttributeModifier2 = new EntityAttributeModifier(entityAttributeModifier.getName(), statusEffect.adjustModifierAmount(statusEffectInstance.getAmplifier(), entityAttributeModifier), entityAttributeModifier.getOperation());
                        list2.add(new Pair<>(entry.getKey(), entityAttributeModifier2));
                    }
                }
                if (statusEffectInstance.getAmplifier() > 0) {
                    mutableText = Text.translatable("potion.withAmplifier", mutableText, Text.translatable("potion.potency." + statusEffectInstance.getAmplifier()));
                }
                if (!statusEffectInstance.isDurationBelow(20)) {
                    mutableText = Text.translatable("potion.withDuration", mutableText, StatusEffectUtil.getDurationText(statusEffectInstance, durationMultiplier));
                }
                list.add(mutableText.formatted(statusEffect.getCategory().getFormatting()));
            }
        }
        if (!list2.isEmpty()) {
            list.add(ScreenTexts.EMPTY);
            list.add(Text.translatable("potion.whenDrank").formatted(Formatting.DARK_PURPLE));
            for (Pair pair : list2) {
                EntityAttributeModifier entityAttributeModifier3 = (EntityAttributeModifier)pair.getSecond();
                double d = entityAttributeModifier3.getValue();
                double e = entityAttributeModifier3.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE || entityAttributeModifier3.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL ? entityAttributeModifier3.getValue() * 100.0 : entityAttributeModifier3.getValue();
                if (d > 0.0) {
                    list.add(Text.translatable("attribute.modifier.plus." + entityAttributeModifier3.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(e), Text.translatable(((EntityAttribute)pair.getFirst()).getTranslationKey())).formatted(Formatting.BLUE));
                    continue;
                }
                if (!(d < 0.0)) continue;
                list.add(Text.translatable("attribute.modifier.take." + entityAttributeModifier3.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(e *= -1.0), Text.translatable(((EntityAttribute)pair.getFirst()).getTranslationKey())).formatted(Formatting.RED));
            }
        }
    }
}

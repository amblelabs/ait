package dev.amble.ait.core.item;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;


/** Bounded inspection used to keep a TARDIS from recovering its own siege representation. */
public final class TemporalRecoveryItemUtil {
    private static final int MAX_DEPTH = 32;
    private static final int MAX_NODES = 4_096;
    private static final int MAX_LIST_ENTRIES = 512;

    private TemporalRecoveryItemUtil() {
    }

    /**
     * Returns {@code true} when the stack contains the supplied TARDIS siege item, or
     * when the nested data is too large to inspect safely. Recovery fails closed to
     * avoid duplicating a TARDIS representation hidden inside a container item.
     */
    public static boolean blocksRecovery(ItemStack stack, UUID tardisId) {
        if (stack == null || stack.isEmpty() || tardisId == null)
            return false;

        ScanBudget budget = new ScanBudget();
        return contains(stack, tardisId, 0, budget) || !budget.complete;
    }

    private static boolean contains(ItemStack stack, UUID tardisId, int depth, ScanBudget budget) {
        if (stack == null || stack.isEmpty())
            return false;
        if (!budget.visit(depth))
            return false;

        if (stack.getItem() instanceof SiegeTardisItem siege) {
            try {
                if (tardisId.equals(siege.getTardisId(stack)))
                    return true;
            } catch (RuntimeException ignored) {
                budget.complete = false;
                return false;
            }
        }

        NbtCompound nbt = stack.getNbt();
        return nbt != null && contains(nbt, tardisId, depth + 1, budget);
    }

    private static boolean contains(NbtElement element, UUID tardisId, int depth, ScanBudget budget) {
        if (element == null || !budget.visit(depth))
            return false;

        if (element instanceof NbtCompound compound) {
            for (String key : List.copyOf(compound.getKeys())) {
                NbtElement child = compound.get(key);
                if (child instanceof NbtCompound childCompound && isSerializedStack(childCompound)) {
                    ItemStack nested = readStack(childCompound, budget);
                    if (nested != null && !nested.isEmpty()) {
                        if (contains(nested, tardisId, depth + 1, budget))
                            return true;
                        continue;
                    }
                }

                if (contains(child, tardisId, depth + 1, budget))
                    return true;
            }
            return false;
        }

        if (element instanceof NbtList list) {
            if (!budget.allowList(list.size()))
                return false;

            for (NbtElement child : list) {
                if (child instanceof NbtCompound childCompound && isSerializedStack(childCompound)) {
                    ItemStack nested = readStack(childCompound, budget);
                    if (nested != null && !nested.isEmpty()) {
                        if (contains(nested, tardisId, depth + 1, budget))
                            return true;
                        continue;
                    }
                }

                if (contains(child, tardisId, depth + 1, budget))
                    return true;
            }
        }

        return false;
    }

    private static boolean isSerializedStack(NbtCompound nbt) {
        return nbt.contains("id", NbtElement.STRING_TYPE)
                && nbt.contains("Count", NbtElement.NUMBER_TYPE);
    }

    @Nullable private static ItemStack readStack(NbtCompound nbt, ScanBudget budget) {
        try {
            return ItemStack.fromNbt(nbt);
        } catch (RuntimeException ignored) {
            budget.complete = false;
            return null;
        }
    }

    private static final class ScanBudget {
        private int remaining = MAX_NODES;
        private boolean complete = true;

        private boolean visit(int depth) {
            if (depth > MAX_DEPTH || this.remaining <= 0) {
                this.complete = false;
                return false;
            }

            this.remaining--;
            return true;
        }

        private boolean allowList(int size) {
            if (size <= MAX_LIST_ENTRIES)
                return true;

            this.complete = false;
            return false;
        }
    }
}

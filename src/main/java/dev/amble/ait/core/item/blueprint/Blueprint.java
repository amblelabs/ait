package dev.amble.ait.core.item.blueprint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

import dev.amble.ait.core.util.StackUtil;

public class Blueprint {
    private final BlueprintSchema source;
    private final List<ItemStack> requirements;
    private final List<ItemStack> initialRequirements;

    public Blueprint(BlueprintSchema source) {
        this.source = source;

        this.initialRequirements = source.inputs().toStacks();
        this.requirements = StackUtil.cloneList(initialRequirements);
    }

    public Blueprint(NbtCompound nbt) {
        this(BlueprintRegistry.getInstance().get(new Identifier(nbt.getString("id"))));

        this.requirements.clear();
        this.fromNbt(nbt);
    }

    /**
     * attempts to resolve a requirement from the list of requirements by removing it if this stack is a valid requirement
     * @param stack the stack to resolve
     * @return true if the stack was a valid requirement and was removed, false otherwise
     */
    public boolean tryAdd(ItemStack stack) {
        for (ItemStack requirement : requirements) {
            if (ItemStack.areItemsEqual(requirement, stack)) {
                // now we need to check if the stack has the same amount of items

                int deducted = Math.min(requirement.getCount(), stack.getCount());
                requirement.decrement(deducted);
                stack.decrement(deducted);

                if (requirement.isEmpty())
                    requirements.remove(requirement);

                return true;
            }
        }

        return false;
    }

    public int getCountLeftFor(ItemStack stack) {
        for (ItemStack requirement : requirements) {
            if (ItemStack.areItemsEqual(requirement, stack)) {
                return requirement.getCount();
            }
        }

        return 0;
    }

    public boolean isComplete() {
        return requirements.isEmpty();
    }

    public ItemStack getOutput() {
        return source.output().copy();
    }

    public Optional<ItemStack> tryCraft() {
        if (!isComplete())
            return Optional.empty();

        return Optional.of(getOutput());
    }

    public List<ItemStack> getRequirements() {
        return requirements;
    }

    /**
     * @return All the items that were inserted into the fabricator
     */
    public List<ItemStack> getInsertedItems() {
        // all the items missing from the initial requirements
        List<ItemStack> inserted = new ArrayList<>(initialRequirements);

        for (ItemStack j : requirements) {
            inserted.stream()
                    .filter(i -> ItemStack.areItemsEqual(i, j))
                    .forEach(i -> i.decrement(j.getCount()));
        }

        return inserted;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", source.id().toString());

        NbtList list = new NbtList();
        for (ItemStack stack : requirements) {
            list.add(stack.writeNbt(new NbtCompound()));
        }
        nbt.put("requirements", list);

        return nbt;
    }
    protected NbtCompound fromNbt(NbtCompound nbt) {
        NbtList list = nbt.getList("requirements", 10);
        for (int i = 0; i < list.size(); i++) {
            requirements.add(ItemStack.fromNbt(list.getCompound(i)));
        }

        return nbt;
    }

    public BlueprintSchema getSource() {
        return source;
    }
}

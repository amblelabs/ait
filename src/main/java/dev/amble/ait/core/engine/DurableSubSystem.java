package dev.amble.ait.core.engine;

import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.lib.util.ServerLifecycleHooks;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

/**
 * Base state for subsystems with durability. Subsystems physically installed in
 * a generic core must extend {@link CoreBoundDurableSubSystem} instead.
 */
public abstract class DurableSubSystem extends SubSystem {
    public static final int MAX_DURABILITY = 1250;
    private static final String ITEM_DATA_KEY = "AITSubsystemData";
    private static final String ITEM_DURABILITY_KEY = "Durability";
    private static final String ITEM_MAX_DURABILITY_KEY = "MaxDurability";

    private float durability = MAX_DURABILITY;

    protected DurableSubSystem(IdLike id) {
        super(id);
    }

    @Override
    protected void onEarlyInit(InitContext ctx) {
        this.durability = this.clampDurability(this.durability);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled && this.isBroken()) return;

        super.setEnabled(enabled);
    }

    public float durability() {
        return durability;
    }

    public float maxDurability() {
        return MAX_DURABILITY;
    }

    public void setDurability(float durability) {
        float before = this.durability;

        this.durability = this.clampDurability(durability);

        this.onDurabilityChange(before, this.durability);
    }
    public void addDurability(float durability) {
        this.setDurability(this.durability() + durability);
    }
    public void removeDurability(float durability) {
        this.addDurability(-durability);
    }

    public boolean isBroken() {
        return this.durability() <= 0;
    }

    @Override
    public void writeItemData(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return;

        float maximum = this.sanitizedMaximum();
        float current = this.clampDurability(this.durability());
        if (current >= maximum) {
            clearItemDurability(stack);
            return;
        }

        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound data = root.contains(ITEM_DATA_KEY, NbtElement.COMPOUND_TYPE)
                ? root.getCompound(ITEM_DATA_KEY) : new NbtCompound();
        data.putFloat(ITEM_DURABILITY_KEY, current);
        data.putFloat(ITEM_MAX_DURABILITY_KEY, maximum);
        root.put(ITEM_DATA_KEY, data);
    }

    @Override
    public void readItemData(ItemStack stack) {
        StackDurability stored = getItemDurability(stack);
        this.durability = this.clampDurability(stored == null ? this.maxDurability() : stored.durability());

        if (this.isBroken() && this.isEnabled())
            this.setEnabled(false);
        else
            this.sync();
    }

    public static StackDurability getItemDurability(ItemStack stack) {
        if (stack == null || stack.isEmpty() || stack.getNbt() == null
                || !stack.getNbt().contains(ITEM_DATA_KEY, NbtElement.COMPOUND_TYPE))
            return null;

        NbtCompound data = stack.getNbt().getCompound(ITEM_DATA_KEY);
        if (!data.contains(ITEM_DURABILITY_KEY, NbtElement.NUMBER_TYPE))
            return null;

        float maximum = data.contains(ITEM_MAX_DURABILITY_KEY, NbtElement.NUMBER_TYPE)
                ? data.getFloat(ITEM_MAX_DURABILITY_KEY) : MAX_DURABILITY;
        if (!Float.isFinite(maximum) || maximum <= 0)
            maximum = MAX_DURABILITY;

        float durability = data.getFloat(ITEM_DURABILITY_KEY);
        durability = Float.isFinite(durability)
                ? Math.max(0, Math.min(durability, maximum)) : maximum;
        return new StackDurability(durability, maximum);
    }

    private static void clearItemDurability(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(ITEM_DATA_KEY))
            return;

        NbtCompound data = nbt.getCompound(ITEM_DATA_KEY);
        data.remove(ITEM_DURABILITY_KEY);
        data.remove(ITEM_MAX_DURABILITY_KEY);
        if (data.isEmpty())
            nbt.remove(ITEM_DATA_KEY);
        if (nbt.isEmpty())
            stack.setNbt(null);
    }

    public record StackDurability(float durability, float maximum) {
    }

    private float clampDurability(float durability) {
        float maximum = this.sanitizedMaximum();
        if (Float.isNaN(durability))
            return maximum;

        return Math.max(0, Math.min(durability, maximum));
    }

    private float sanitizedMaximum() {
        float maximum = this.maxDurability();
        return Float.isFinite(maximum) && maximum > 0 ? maximum : MAX_DURABILITY;
    }

    @Override
    public boolean isUsable() {
        return super.isUsable() && !this.isBroken();
    }

    protected void onDurabilityChange(float before, float after) {
        if (before == 0 && after > 0) {
            this.onRepair();
        } else if (before > 0 && after == 0) {
            this.onBreak();
        }

        this.sync();
    }
    protected void onBreak() {
        this.setEnabled(false);
        TardisEvents.SUBSYSTEM_BREAK.invoker().onBreak(this);
    }

    protected void onRepair() {
        this.setEnabled(true);
        TardisEvents.SUBSYSTEM_REPAIR.invoker().onRepair(this);
    }
    protected int changeFrequency() {
        return 20;
    }
    protected abstract float cost();
    protected abstract boolean shouldDurabilityChange();

    @Override
    public void tick() {
        super.tick();

        if (!(this.isEnabled())) return;
        if (this.isBroken()) return;
        if (!ServerLifecycleHooks.isServer()) return;
        if (!this.shouldDurabilityChange()) return;
        if (ServerLifecycleHooks.get().getTicks() % this.changeFrequency() != 0) return;

        this.removeDurability(this.cost());
    }
}

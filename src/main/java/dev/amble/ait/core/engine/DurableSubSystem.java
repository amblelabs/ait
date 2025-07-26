package dev.amble.ait.core.engine;

import dev.amble.lib.util.ServerLifecycleHooks;

import net.minecraft.item.ItemStack;

import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.AITTags;

public abstract class DurableSubSystem extends SubSystem {
    public static final int MAX_DURABILITY = 1250;
    private float durability = MAX_DURABILITY;

    protected DurableSubSystem(IdLike id) {
        super(id);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled && this.isBroken()) return;

        super.setEnabled(enabled);
    }

    public float durability() {
        return durability;
    }

    public void setDurability(float durability) {
        float before = this.durability;

        this.durability = Math.max(0, Math.min(durability, MAX_DURABILITY));

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

    public boolean isRepairItem(ItemStack stack) {
        return stack.isIn(AITTags.Items.REPAIRS_SUBSYSTEM);
    }

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

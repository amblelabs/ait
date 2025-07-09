package dev.amble.ait.tardis.v2.data;

import dev.amble.ait.api.tardis.v2.data.TData;
import dev.amble.ait.api.tardis.v2.data.TDataHolder;
import dev.amble.ait.api.tardis.v2.data.properties.Value;
import dev.amble.ait.api.tardis.v2.data.properties.integer.IntValue;
import dev.amble.ait.tardis.v2.Tardis;

public class RepairData implements TData<RepairData> {

    public static final Integer UNSTABLE_TICK_START_THRESHOLD = 2_400;
    public static final Integer MAX_REPAIR_TICKS = 7_000;

    public static final TDataHolder<RepairData> ID = null;

    public Value<State> state() {

    }

    public Integer getRepairTicks() {
        return repairTicks.get();
    }

    public int getRepairTicksAsSeconds() {
        return this.getRepairTicks() / 20;
    }

    public void setRepairTicks(Tardis tardis, int ticks) {
        if (ticks > MAX_REPAIR_TICKS) {
            this.setRepairTicks(MAX_REPAIR_TICKS);
            return;
        }

        repairTicks.set(ticks);
    }

    public void addRepairTicks(Integer ticks) {
        repairTicks.set(getRepairTicks() + ticks);
    }

    public enum State {
        NORMAL, UNSTABLE, TOXIC
    }
}

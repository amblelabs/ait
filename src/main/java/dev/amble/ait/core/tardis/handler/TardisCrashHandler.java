package dev.amble.ait.core.tardis.handler;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import org.joml.Vector3f;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.data.properties.integer.IntProperty;

public class TardisCrashHandler extends KeyedTardisComponent implements TardisTickable {

    public static final Integer UNSTABLE_TICK_START_THRESHOLD = 2_400;
    public static final Integer MAX_REPAIR_TICKS = 7_000;

    private static final Property<State> STATE_PROPERTY = Property.forEnum("state", State.class, State.NORMAL);
    private static final IntProperty TARDIS_REPAIR_TICKS_PROPERTY = new IntProperty("repair_ticks", 0);

    private final Value<State> state = STATE_PROPERTY.create(this);
    private final Value<Integer> repairTicks = TARDIS_REPAIR_TICKS_PROPERTY.create(this);

    public boolean isToxic() {
        return this.getState() == State.TOXIC;
    }

    public boolean isUnstable() {
        return this.getState() == State.UNSTABLE;
    }

    public boolean isNormal() {
        return this.getState() == State.NORMAL;
    }

    @Override
    public void onLoaded() {
        state.of(this, STATE_PROPERTY);
        repairTicks.of(this, TARDIS_REPAIR_TICKS_PROPERTY);
    }

    @Override
    public void tick(MinecraftServer server) {
        if (tardis.isGrowth()) return;
        State state = this.state.get();
        int repairTicks = this.repairTicks.get();

        if (repairTicks > 0) {
            repairTicks = tardis.isRefueling() ? repairTicks - 10 : repairTicks - 1;
            this.setRepairTicks(repairTicks);
        } else {
            if (state == State.NORMAL)
                return;

            this.state.set(State.NORMAL);
            this.tardis().alarm().disable();
            return;
        }

        if (state == State.TOXIC && tardis.sonic().getExteriorSonic() == null)
            this.tardis().alarm().enable();

        if (repairTicks < UNSTABLE_TICK_START_THRESHOLD && state != State.UNSTABLE) {
            state = State.UNSTABLE;
            this.state.set(state);
        }

        CachedDirectedGlobalPos exteriorPosition = tardis.travel().position();
        ServerWorld exteriorWorld = exteriorPosition.getWorld();

        DoorHandler door = tardis.door();
        door.setDoorParticles(state != State.NORMAL ? ParticleTypes.CAMPFIRE_COSY_SMOKE : null);

        if (state != State.TOXIC)
            return;

        exteriorWorld.spawnParticles(
                new DustColorTransitionParticleEffect(new Vector3f(0.75f, 0.85f, 0.75f),
                        new Vector3f(0.15f, 0.25f, 0.15f), 3),
                exteriorPosition.getPos().toCenterPos().x, exteriorPosition.getPos().getY() + 0.1f,
                exteriorPosition.getPos().toCenterPos().z, 1, 0.05D, 0.75D, 0.05D, 0.01D);

        if (server.getTicks() % 40 != 0)
            return;

        if (TardisUtil.isInteriorEmpty(tardis.asServer()))
            return;

        int loyaltySubAmount = AITMod.RANDOM.nextInt(10, 25);

        for (ServerPlayerEntity serverPlayerEntity : tardis.asServer().world().getPlayers()) {
            ItemStack stack = serverPlayerEntity.getEquippedStack(EquipmentSlot.HEAD);

            if (stack.isIn(AITTags.Items.FULL_RESPIRATORS) || stack.isIn(AITTags.Items.HALF_RESPIRATORS))
                continue;

            // serverPlayerEntity.playSound(AITSounds.CLOISTER, 1f, 1f);
            serverPlayerEntity.damage(exteriorWorld.getDamageSources().magic(), 3f);
            serverPlayerEntity
                    .addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 100, 3, true, false, false));
            serverPlayerEntity
                    .addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 5, true, false, false));

            tardis.loyalty().get(serverPlayerEntity).subtract(loyaltySubAmount);
        }
    }

    public TardisCrashHandler() {
        super(Id.CRASH_DATA);
    }

    public State getState() {
        if (state.get() == null)
            return State.NORMAL;
        return state.get();
    }

    public void setState(State tardisState) {
        state.set(tardisState);
    }

    public Integer getRepairTicks() {
        return repairTicks.get();
    }

    public int getRepairTicksAsSeconds() {
        return this.getRepairTicks() / 20;
    }

    public void setRepairTicks(Integer ticks) {
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

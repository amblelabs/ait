package dev.amble.ait.core.tardis.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;

import dev.amble.ait.api.KeyedTardisComponent;
import dev.amble.ait.api.TardisTickable;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;

// use this as reference for starting other looping sounds on the exterior
public class ServerAlarmHandler extends KeyedTardisComponent implements TardisTickable {

    @Exclude
    public static final int CLOISTER_LENGTH_TICKS = 3 * 20;

    @Exclude
    private int soundCounter = 0;

    private static final BoolProperty ENABLED = new BoolProperty("enabled", false);
    private static final BoolProperty HOSTILE_PRESENCE = new BoolProperty("hostile_presence", true);

    private final BoolValue enabled = ENABLED.create(this);
    private final BoolValue hostilePresence = HOSTILE_PRESENCE.create(this);

    public ServerAlarmHandler() {
        super(Id.ALARMS);
    }

    @Override
    public void onLoaded() {
        enabled.of(this, ENABLED);
        hostilePresence.of(this, HOSTILE_PRESENCE);
    }

    public BoolValue enabled() {
        return enabled;
    }

    public void enable() {
        this.enabled.set(true);
    }

    public void disable() {
        this.enabled.set(false);
    }

    public BoolValue hostilePresence() {
        return hostilePresence;
    }

    public void toggle() {
        this.enabled.flatMap(value -> !value);
    }

    private boolean isDoorOpen() {
        return tardis.door().isOpen();
    }

    @Override
    public void tick(MinecraftServer server) {
        if (server.getTicks() % 20 == 0 && !this.enabled().get() && this.hostilePresence().get()) {
            for (Entity entity : TardisUtil.getEntitiesInInterior(tardis, 200)) {
                if (entity instanceof TntEntity || (entity instanceof HostileEntity && !entity.hasCustomName())
                        || entity instanceof ServerPlayerEntity player
                        && tardis.loyalty().get(player).level() == Loyalty.Type.REJECT.level) {
                    tardis.alarm().enabled().set(true);
                }
            }

            return;
        }

        if (!this.enabled().get())
            return;

        if (tardis.travel().getState() == TravelHandlerBase.State.FLIGHT)
            return;

        soundCounter++;

        if (soundCounter >= CLOISTER_LENGTH_TICKS) {
            soundCounter = 0;

            float volume = isDoorOpen() ? 1.0f : 0.3f;
            float pitch = isDoorOpen() ? 1f : 0.2f;

            tardis.travel().position().getWorld().playSound(null, tardis.travel().position().getPos(),
                    AITSounds.CLOISTER, SoundCategory.AMBIENT, volume, pitch);
        }
    }
}

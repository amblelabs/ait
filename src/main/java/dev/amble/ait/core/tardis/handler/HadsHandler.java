package dev.amble.ait.core.tardis.handler;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;

public class HadsHandler extends KeyedTardisComponent implements TardisTickable {
	private static final int CHECK_FREQUENCY = 20;
	private static final float EXTERIOR_CHECK_RADIUS = 16F;
	private static final int DANGER_PERSIST_TICKS = 200;

    private static final BoolProperty HADS_ENABLED = new BoolProperty("enabled", false);
    private static final BoolProperty IS_IN_ACTIVE_DANGER = new BoolProperty("is_in_active_danger", false);

    private final BoolValue enabled = HADS_ENABLED.create(this);
    private final BoolValue inDanger = IS_IN_ACTIVE_DANGER.create(this);

    public HadsHandler() {
        super(Id.HADS);
    }

    public boolean isActive() {
        return enabled.get();
    }

    public boolean isInDanger() {
        return inDanger.get();
    }

    @Override
    public void onLoaded() {
        enabled.of(this, HADS_ENABLED);
        inDanger.of(this, IS_IN_ACTIVE_DANGER);
    }

    @Override
    public void tick(MinecraftServer server) {
	    //if (!isActive()) return;

	    if (!tardis.subsystems().lifeSupport().isUsable()) {
		    tardis.alarm().enable(Text.literal("HADS: LIFE SUPPORT FAILURE"));

		    enabled.set(false);
		    inDanger.set(false);
		    return;
	    }

	    if (server.getTicks() % CHECK_FREQUENCY != 0) return; // Check every second

	    // check if hostiles interior
	    if (checkForDanger()) {
		    if (!inDanger.get()) {
			    panic();
		    }
	    } else {
		    inDanger.set(false);
	    }
    }

	private boolean checkForDanger() {
		boolean interiorDanger = !tardis().asServer().world().getEntitiesByType(TypeFilter.instanceOf(MobEntity.class), e -> e instanceof Monster).isEmpty();

		if (interiorDanger) return true;
		if (!tardis().travel().isLanded()) return false;

		ServerWorld exteriorWorld = tardis().travel().position().getWorld();
		Box checkBox = new Box(tardis().travel().position().getPos()).expand(EXTERIOR_CHECK_RADIUS);

		return !exteriorWorld.getEntitiesByType(TypeFilter.instanceOf(MobEntity.class), checkBox, e -> e instanceof Monster).isEmpty();
	}

	private void panic() {
		inDanger.set(true);

		tardis().travel().destination(tardis().stats().getHome());
		tardis().travel().dematerialize().ifPresentOrElse(dematQueue -> {
			tardis.alarm().enable(Text.literal("HADS: DEMATERIALIZING DUE TO HOSTILE PRESENCE"));

			tardis().travel().setFlightTicks(tardis().travel().getTargetTicks());

			var landQueue = tardis().travel().queueFor(TravelHandlerBase.State.LANDED);

			landQueue.thenRun(() -> {
				Scheduler.get().runTaskLater(() -> {
					if (checkForDanger()) return;

					// return back to where we were
					inDanger.set(false);
					tardis().travel().destination(tardis().travel().previousPosition());
					tardis().travel().dematerialize().ifPresent(_q -> {
						tardis().travel().setFlightTicks(tardis().travel().getTargetTicks());
					});
					tardis().alarm().disable();
				}, TaskStage.END_SERVER_TICK, TimeUnit.TICKS, DANGER_PERSIST_TICKS);
			});
		}, () -> {
			tardis.alarm().enable(Text.literal("HADS FAILURE"));
		});
	}
}

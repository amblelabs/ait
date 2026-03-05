package dev.amble.ait.core.tardis.handler;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.drtheo.queue.api.ActionQueue;
import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;

import java.util.List;

public class HadsHandler extends KeyedTardisComponent implements TardisTickable {
	private static final int CHECK_FREQUENCY = 20;
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
		    tardis.alarm().enable(Text.translatable("ait.tardis.life_support_failure"));

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
		// Idk how to make sense of this (why would the tardis HADS its way to its original position
        // and back again if the entity is already WITHIN the TARDIS?
        // Wouldn't taking it home be more dangerous than just dematting BEFORE the entity can get in? Use some sense smh - Loqor
        /*boolean interiorDanger = !tardis().asServer().world().getEntitiesByType(TypeFilter.instanceOf(MobEntity.class), e -> e instanceof Monster).isEmpty();

		if (interiorDanger) return true;*/
		if (!tardis().travel().isLanded()) return false;

		ServerWorld exteriorWorld = tardis().travel().position().getWorld();
		Box checkBox = new Box(tardis().travel().position().getPos()).expand(AITMod.CONFIG.exteriorRadius);

        List<Entity> hostileEntities = exteriorWorld.getEntitiesByType(TypeFilter.instanceOf(Entity.class), checkBox,
                e -> {
					// Now uses custom defined tags for filtering.
					boolean hadsTriggerer = e.getType().isIn(AITTags.EntityTypes.HADS_TRIGGERER);
					boolean isBoss = e.getType().isIn(AITTags.EntityTypes.BOSS);
					return hadsTriggerer || isBoss;
				});

		// This has some custom logic per-entity, e.g. creepers aren't an immediate threat
		// unless they start exploding - Loqor
        return hostileEntities.size() > AITMod.CONFIG.triggererThreshold || hostileEntities.stream().anyMatch(e -> {
					// If it's a boss, RUN.
					if (e.getType().isIn(AITTags.EntityTypes.BOSS)) {
						return true;
					}

					// If it's a creeper, check if it's exploding currently or not.
					if (e instanceof CreeperEntity creeperEntity) {
						return creeperEntity.getFuseSpeed() > 0;
					}

					// Otherwise, return false.
					return false;
				}
		);
    }

	private void panic() {
		inDanger.set(true);

		TravelHandler travel = tardis().travel();
		StatsHandler stats = tardis().stats();
		ServerAlarmHandler alarms = tardis().alarm();

		travel.destination(stats.getHome());
		travel.dematerialize().ifPresentOrElse(dematQueue -> {
			alarms.enable(Text.translatable("ait.tardis.hads_escape"));

			travel.setFlightTicks(travel.getTargetTicks());

			ActionQueue landQueue = travel.queueFor(TravelHandlerBase.State.LANDED);

			landQueue.thenRun(() -> {
				Scheduler.get().runTaskLater(() -> {
					if (checkForDanger()) return;

					// return back to where we were
					inDanger.set(false);
					travel.destination(travel.previousPosition());
					travel.dematerialize().ifPresent(_q -> {
						travel.setFlightTicks(travel.getTargetTicks());
					});
					alarms.disable();
				}, TaskStage.END_SERVER_TICK, TimeUnit.TICKS, DANGER_PERSIST_TICKS);
			});
		}, () -> {
			alarms.enable(Text.translatable("ait.tardis.hads_failure"));
		});
	}
}

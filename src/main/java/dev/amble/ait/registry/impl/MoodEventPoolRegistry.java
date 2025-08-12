package dev.amble.ait.registry.impl;

import java.util.List;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.handler.mood.MoodDictatedEvent;
import dev.amble.ait.core.tardis.handler.mood.TardisMood;
import dev.amble.ait.core.util.WorldUtil;

public class MoodEventPoolRegistry {
    public static final SimpleRegistry<MoodDictatedEvent> REGISTRY = FabricRegistryBuilder
            .createSimple(RegistryKey.<MoodDictatedEvent>ofRegistry(AITMod.id("mood_event_pool")))
            .buildAndRegister();

    private static final Random random = Random.create();

    public static MoodDictatedEvent register(MoodDictatedEvent schema) {
        return Registry.register(REGISTRY, schema.id(), schema);
    }

    // Negative mood events
    public static MoodDictatedEvent CHANGE_DIM;
    public static MoodDictatedEvent FORCE_DEMAT;
    public static MoodDictatedEvent FLY_TO_WAYPOINT;
    public static MoodDictatedEvent KICK_PLAYER_OUT;
    public static MoodDictatedEvent CLOAKING_WHEN_AFRAID;
    public static MoodDictatedEvent FAST_RETURN_AND_TAKEOFF;
    public static MoodDictatedEvent RANDOM_ALARM_ACTIVATION;
    public static MoodDictatedEvent RANDOM_POWER_OFF;
    public static MoodDictatedEvent RANDOM_SIEGE;

    // Neutral mood events
    public static MoodDictatedEvent ACTIVATE_AUTOPILOT;
    public static MoodDictatedEvent LOCK_DOORS;
    public static MoodDictatedEvent SHIELD_ACTIVATION;

    // Positive mood events
    public static MoodDictatedEvent ADD_LOYALTY;
    public static MoodDictatedEvent RANDOM_XP_GRANT;
    public static MoodDictatedEvent AUTO_REFUEL;
    public static MoodDictatedEvent FAST_FLIGHT;

    /**
     * @author Loqor ...
     * @implNote cost values should ONLY range between 16 and 256. ... for this,
     *           basically the pool will roll after the moods "race", resulting in a
     *           pool roll like so: ... # if the mood that won the race is negative:
     *           - if it lands on a negative, it will trigger the event. - if it
     *           lands on a positive, it will re-roll. - if it lands on a neutral
     *           with no negative or positive moods, it will trigger the event. - if
     *           it lands on a neutral with a positive mood, it will have a higher
     *           chance of re-rolling, but a small possibility of triggering the
     *           event. - if it lands on a neutral with a negative mood, it will
     *           have a higher chance of triggering the event, but a small
     *           possibility of re-rolling. ... # if the mood that won the race is
     *           positive: - if it lands on a positive, it will trigger the event. -
     *           if it lands on a negative, it will re-roll. - if it lands on a
     *           neutral with no negative or positive moods, it will trigger the
     *           event. - if it lands on a neutral with a negative mood, it will
     *           have a higher chance of re-rolling, but a small possibility of
     *           triggering the event. - if it lands on a neutral with a positive
     *           mood, it will have a higher chance of triggering the event, but a
     *           small possibility of re-rolling. ... # if the mood that won the
     *           race is neutral: - depending on the swayed weight of the neutral
     *           mood, it will trigger the event that corresponds to said swayed
     *           weight. - it does have a possibility of having little to no sway
     *           and just triggering any event it lands on.
     */
    public static void init() {

        //The ones i commented out do not work

        CHANGE_DIM = register(MoodDictatedEvent.Builder.create(AITMod.id("change_dim"), tardis -> {
            List<ServerWorld> listOfDims = WorldUtil.getTravelWorlds();
            ServerWorld randomWorld = listOfDims.get(random.nextInt(listOfDims.size()));

            tardis.travel().forceDestination(cached -> cached.world(randomWorld));
        }, 1, TardisMood.Alignment.NEGATIVE));

        RANDOM_SIEGE = register(
                MoodDictatedEvent.Builder.create(AITMod.id("random_siege"),
                        tardis -> tardis.siege().setActive(true), 1, TardisMood.Alignment.NEGATIVE));


        RANDOM_POWER_OFF = register(
                MoodDictatedEvent.Builder.create(AITMod.id("random_power_off"), tardis -> {
                    if (!tardis.travel().inFlight())
                        tardis.fuel().disablePower();
                }, 1, TardisMood.Alignment.NEGATIVE));

        RANDOM_ALARM_ACTIVATION = register(
                MoodDictatedEvent.Builder.create(AITMod.id("random_alarm_activation"),
                        tardis -> tardis.alarm().enable(), 1, TardisMood.Alignment.NEGATIVE));

        FORCE_DEMAT = register(
                MoodDictatedEvent.Builder.create(AITMod.id("force_demat"),
                        tardis -> tardis.travel().dematerialize(), 1, TardisMood.Alignment.NEGATIVE));

        FLY_TO_WAYPOINT = register(
                MoodDictatedEvent.Builder.create(AITMod.id("fly_to_waypoint"),
                        tardis -> tardis.waypoint().loadWaypoint(), 1, TardisMood.Alignment.NEUTRAL));

        //TODO: Fix
        //KICK_PLAYER_OUT = init(
                //MoodDictatedEvent.Builder.create(AITMod.id("kick_player_out"), tardis -> {
                    //tardis.door().setLocked(false);
                    //tardis.door().openDoors();

                    //if (tardis.getDesktop().getDoorPos() != null)
                      //  tardis.getInteriorWorld().playSound(null, tardis.getDesktop().getDoorPos().getPos(),
                        //        SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1f, 1f);

                    //if (tardis.travel().position() != null)
                      //  tardis.travel().position().getWorld().playSound(null, tardis.travel().position().getPos(),
                                //SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1f, 1f);
                //}, 1, TardisMood.Alignment.NEUTRAL));

        //SHIELD_ACTIVATION = init(
              //  MoodDictatedEvent.Builder.create(AITMod.id("shield_activation"), tardis -> {
                //    ShieldHandler shields = tardis.handler(TardisComponent.Id.SHIELDS);
                  //  shields.enable();

                    //if (shields.visuallyShielded().get())
                      //  shields.disableVisuals();
                //}, 1, TardisMood.Alignment.NEGATIVE));

        CLOAKING_WHEN_AFRAID = register(
                MoodDictatedEvent.Builder.create(AITMod.id("cloaking_when_afraid"), tardis -> {
                    tardis.cloak().cloaked().set(true);
                    }, 1, TardisMood.Alignment.NEGATIVE));

       // ACTIVATE_AUTOPILOT = init(
               // MoodDictatedEvent.Builder.create(AITMod.id("activate_autopilot"),
                    //    tardis -> tardis.travel().autopilot(true), 1, TardisMood.Alignment.NEUTRAL));

        AUTO_REFUEL = register(MoodDictatedEvent.Builder.create(AITMod.id("auto_refuel"),
                tardis -> tardis.fuel().refueling().set(true), 1, TardisMood.Alignment.POSITIVE));

        ADD_LOYALTY = register(
                MoodDictatedEvent.Builder.create(AITMod.id("add_loyalty"),
                        tardis -> tardis.world().getPlayers()
                                .forEach(player -> tardis.loyalty().get(player).add(7)),
                        1, TardisMood.Alignment.POSITIVE));

        RANDOM_XP_GRANT = register(MoodDictatedEvent.Builder.create(AITMod.id("random_xp_grant"),
                tardis -> tardis.world().getPlayers()
                        .forEach(player -> player
                                .addExperience((int) (player.getNextLevelExperience() + player.experienceProgress))),
                1, TardisMood.Alignment.POSITIVE));

        // instant flight basically, expensive.
        FAST_FLIGHT = register(
                MoodDictatedEvent.Builder.create(AITMod.id("fast_flight"), tardis -> {
                    if (tardis.travel().inFlight()) {
                        tardis.travel().decreaseFlightTime(
                                tardis.travel().getTargetTicks() - tardis.travel().getFlightTicks());
                    }
                    tardis.fuel().removeFuel(1000);
                }, 1, TardisMood.Alignment.POSITIVE));

        // TODO: should make this happen when a low loyalty player approaches the TARDIS
        //LOCK_DOORS = init(MoodDictatedEvent.Builder.create(AITMod.id("lock_doors"), tardis -> {
         //   tardis.door().setLocked(true);
         //   if (tardis.getDesktop().getDoorPos() != null)
             //   tardis.getInteriorWorld().playSound(null, tardis.getDesktop().getDoorPos().getPos(),
             //           SoundEvents.BLOCK_CHAIN_PLACE, SoundCategory.BLOCKS, 1f, 1f);

          //  DirectedGlobalPos.Cached exteriorPos = tardis.travel().position();

           // if (exteriorPos == null)
            //    return;

           // exteriorPos.getWorld().playSound(null, exteriorPos.getPos(), SoundEvents.BLOCK_CHAIN_PLACE,
                //    SoundCategory.BLOCKS, 1f, 1f);
        //}, 1, TardisMood.Alignment.NEUTRAL));
    }
}

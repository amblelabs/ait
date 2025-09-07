package dev.amble.ait.core;

import java.util.List;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;

public class AITSounds {
    // public static final SoundEvent SECRET_MUSIC = init("music/secret_music"); // removed for its large size - if you bring it back, COMPRESS IT
    public static final SoundEvent EVEN_MORE_SECRET_MUSIC = register("music/even_more_secret_music");
    public static final SoundEvent DRIFTING_MUSIC = register("music/drifting_by_radio");
    public static final SoundEvent WONDERFUL_TIME_IN_SPACE = register("music/wonderful_time_in_space");
    public static final SoundEvent EARTH_MUSIC = register("music/earth_nitrogenez");
    public static final SoundEvent VENUS_MUSIC = register("music/venus_nitrogenez");
    public static final SoundEvent GOOD_MAN_MUSIC = register("music/good_man_dian");
    public static final SoundEvent CHRONOLOGY_MUSIC = register("music/chronology");
    public static final SoundEvent MAD_MAN_MUSIC = register("music/mad_man_dian");
    public static final SoundEvent SPACE = register("music/space");

    // TARDIS
    public static final SoundEvent DEMAT = register("tardis/demat");
    public static final SoundEvent MAT = register("tardis/mat");
    public static final SoundEvent HOP_DEMAT = register("tardis/hop_takeoff");
    public static final SoundEvent HOP_MAT = register("tardis/hop_land");
    public static final SoundEvent FAIL_DEMAT = register("tardis/fail_takeoff");
    public static final SoundEvent FAIL_MAT = register("tardis/fail_land");
    public static final SoundEvent PHASING_DEMAT = register("tardis/engine_phasing_demat");
    public static final SoundEvent PHASING_REMAT = register("tardis/engine_phasing_remat");
    public static final SoundEvent EMERG_MAT = register("tardis/emergency_land");
    public static final SoundEvent FLIGHT_LOOP = register("tardis/flight_loop");
    public static final SoundEvent UNSTABLE_FLIGHT_LOOP = register("tardis/unstable_flight_loop");
    public static final SoundEvent LAND_THUD = register("tardis/land_thud");
    public static final SoundEvent ABORT_FLIGHT = register("tardis/abort_flight");
    public static final SoundEvent LAND_CRASH = register("tardis/land_crash");
    public static final SoundEvent SHUTDOWN = register("tardis/console_shutdown");
    public static final SoundEvent POWERUP = register("tardis/console_powerup");
    public static final SoundEvent WAYPOINT_ACTIVATE = register("tardis/waypoint_activate");
    public static final SoundEvent POLICE_BOX_DOOR_OPEN = register("tardis/police_box_door_open");
    public static final SoundEvent POLICE_BOX_DOOR_CLOSE = register("tardis/police_box_door_close");
    public static final SoundEvent TARDIS_BLING = register("tardis/bling");
    public static final SoundEvent NAV_NOTIFICATION = register("tardis/nav_notification");
    public static final SoundEvent SHIELD_AMBIANCE = register("tardis/shield_ambiance");


    public static final SoundEvent SIEGE_ENABLE = register("tardis/siege_enable");
    public static final SoundEvent SIEGE_DISABLE = register("tardis/siege_disable");

    //EIGHTH SOUNDS
    public static final SoundEvent EIGHTH_DEMAT = register("tardis/eighth_demat");
    public static final SoundEvent EIGHTH_FLIGHT = register("tardis/eighth_flight");
    public static final SoundEvent EIGHTH_MAT = register("tardis/eighth_mat");
    //GHOST MAT
    public static final SoundEvent GHOST_MAT = register("tardis/ghost_mat");
    //PROTON SOUNDS
    public static final SoundEvent PROTON_FLIGHT = register("tardis/proton_flight");
    public static final SoundEvent PROTON_DEMAT = register("tardis/proton_demat");
    public static final SoundEvent PROTON_MAT = register("tardis/proton_mat");
    //MASTER SOUNDS
    public static final SoundEvent MASTER_DEMAT = register("tardis/master_demat");
    public static final SoundEvent MASTER_MAT = register("tardis/master_mat");
    // BILL & TED
    public static final SoundEvent BNT_DEMAT = register("tardis/bnt/demat");
    public static final SoundEvent BNT_MAT = register("tardis/bnt/mat");
    // DRILL
    public static final SoundEvent DRILL_DEMAT = register("tardis/drill/demat");
    public static final SoundEvent DRILL_MAT = register("tardis/drill/mat");

    //STABILIZE
    public static final SoundEvent STABILIZE = register("tardis/stabilize_flight");

    // APPARATUS VIVI
    public static final SoundEvent MOODY = register("tardis/moody/moody");
    public static final SoundEvent MOODY1 = register("tardis/moody/moody1");
    public static final SoundEvent MOODY2 = register("tardis/moody/moody2");
    public static final SoundEvent MOODY3 = register("tardis/moody/moody3");
    public static final SoundEvent MOODY4 = register("tardis/moody/moody4");
    public static final SoundEvent MOODY5 = register("tardis/moody/moody5");

    public static final SoundEvent HAMMER_STRIKE = register("tardis/hammer_strike");
    public static final SoundEvent OWNER_BED = register("tardis/bed/owner");
    public static final SoundEvent PILOT_BED = register("tardis/bed/pilot");
    public static final SoundEvent COMPANION_BED = register("tardis/bed/companion");
    public static final SoundEvent NEUTRAL_BED = register("tardis/bed/neutral");
    public static final SoundEvent REJECT_BED = register("tardis/bed/reject");
    public static final SoundEvent LOYALTY_UP = register("tardis/moody/loyalty_up");
    public static final SoundEvent GROAN = register("tardis/groan");
    public static final SoundEvent REMOTE_LOCK = register("tardis/remote_lock");
    public static final SoundEvent REMOTE_UNLOCK = register("tardis/remote_unlock");
    public static final SoundEvent KEY_LOCK = register("tardis/key_lock");
    public static final SoundEvent KEY_UNLOCK = register("tardis/key_unlock");
    //Possible could be used for a mood related thing?
    public static final SoundEvent TARDIS_REJECTION_SFX = register("tardis/tardis_rejection_sfx");

    // Controls
    public static final SoundEvent THROTTLE_PULL = register("controls/demat_lever_pull");
    public static final SoundEvent HANDBRAKE_LEVER_PULL = register("controls/handbrake_lever_pull");
    public static final SoundEvent HANDBRAKE_UP = register("controls/handbrake_up");
    public static final SoundEvent HANDBRAKE_DOWN = register("controls/handbrake_down");
    public static final SoundEvent CRANK = register("controls/crank");
    public static final SoundEvent KNOCK = register("controls/knock");
    public static final SoundEvent SNAP = register("controls/snap");
    public static final SoundEvent BWEEP = register("controls/bweep");
    public static final SoundEvent ENGINE_REFUEL_CRANK = register("controls/engine_refuel_crank");
    public static final SoundEvent TELEPATHIC_CIRCUITS = register("controls/telepathic_circuits");
    public static final SoundEvent PROTOCOL_3 = register("controls/protocol_3");
    public static final SoundEvent PROTOCOL_3ALT = register("controls/protocol_3alt");
    public static final SoundEvent LAND_TYPE = register("controls/land_type");
    public static final SoundEvent DOOR_LOCK = register("controls/door_lock");
    public static final SoundEvent MONITOR = register("controls/monitor");
    public static final SoundEvent DOOR_CONTROL = register("controls/door_control");
    public static final SoundEvent DOOR_CONTROLALT = register("controls/door_controlalt");
    public static final SoundEvent POWER_FLICK = register("controls/power_flick");
    public static final SoundEvent PROTOCOL_19 = register("controls/protocol_19");
    public static final SoundEvent SAVE_WAYPOINT = register("controls/save_waypoint");
    public static final SoundEvent ANTI_GRAVS = register("controls/anti_gravs");
    public static final SoundEvent FAST_RETURN = register("controls/fast_return");
    public static final SoundEvent PROTOCOL_116_ON = register("controls/protocol_116_on");
    public static final SoundEvent PROTOCOL_116_OFF = register("controls/protocol_116_off");
    public static final SoundEvent SHIELDS = register("controls/shield");
    public static final SoundEvent ALARM = register("controls/alarm_flick");
    public static final SoundEvent SLOT_IN = register("controls/slot_in");
    public static final SoundEvent SONIC_PORT = register("controls/sonic_port");
    public static final SoundEvent HAIL_MARY = register("controls/hail_mary");
    public static final SoundEvent SIEGE = register("controls/siege");
    public static final SoundEvent DIRECTION = register("controls/direction");
    public static final SoundEvent THROTTLE_SNAP = register("controls/throttle_snap");
    public static final SoundEvent LOAD_WAYPOINT = register("controls/waypoint_load");
    public static final SoundEvent XYZ = register("controls/xyz");
    public static final SoundEvent RANDOMIZE = register("controls/randomize");
    public static final SoundEvent INCREMENT = register("controls/increment");
    public static final SoundEvent DIMENSION = register("controls/dimension");
    public static final SoundEvent ENGINE_OVERLOAD = register("controls/engine_overload");
    public static final SoundEvent ZAP = register("controls/zap");
    public static final SoundEvent ML = register("tardis/ml");
    public static final SoundEvent MONITOR_IDLE = register("tardis/monitor_idle");

    //Alt console sounds for renaissance
    public static final SoundEvent RENAISSANCE_LAND_TYPE = register("controls/renaissance_land_type");
    public static final SoundEvent RENAISSANCE_POWER_SIEGE = register("controls/renaissance_power_siege");
    public static final SoundEvent RENAISSANCE_SHIELDS = register("controls/renaissance_shields");
    public static final SoundEvent RENAISSANCE_SHIELDS_ALT = register("controls/renaissance_shields_alt");
    public static final SoundEvent RENAISSANCE_DOOR = register("controls/renaissance_door");
    public static final SoundEvent RENAISSANCE_DOOR_ALT = register("controls/renaissance_door_alt");
    public static final SoundEvent RENAISSANCE_LOCK = register("controls/renaissance_lock");
    public static final SoundEvent RENAISSANCE_ANTI_GRAV = register("controls/renaissance_anti_grav");
    public static final SoundEvent RENAISSANCE_HANDBRAKE = register("controls/renaissance_handbrake");
    public static final SoundEvent RENAISSANCE_HANDBRAKE_ALT = register("controls/renaissance_handbrake_alt");
    public static final SoundEvent RENAISSANCE_DIMENSION = register("controls/renaissance_dimension");

    //Alt console sounds for coral
    public static final SoundEvent CORAL_INCREMENT = register("controls/coral_increment");
    public static final SoundEvent CORAL_SHIELDS = register("controls/coral_shields");
    public static final SoundEvent CORAL_SHIELDS_ALT = register("controls/coral_shields_alt");
    public static final SoundEvent CORAL_MONITOR = register("controls/coral_monitor");
    public static final SoundEvent CORAL_LAND_TYPE = register("controls/coral_land_type");
    public static final SoundEvent CORAL_TELEPATHIC_CIRCUITS = register("controls/coral_telepathic_circuits");
    public static final SoundEvent CORAL_THROTTLE = register("controls/coral_throttle");
    public static final SoundEvent CORAL_DIMENSION = register("controls/coral_dimension");
    public static final SoundEvent CORAL_XYZ = register("controls/coral_xyz");

    //Alt console sounds for the copper
    public static final SoundEvent COPPER_TELEPATHIC_CIRCUITS = register("controls/copper_telepathic_circuit");
    public static final SoundEvent COPPER_ALARM = register("controls/copper_alarm");
    public static final SoundEvent COPPER_ALARM_ALT = register("controls/copper_alarm_alt");
    public static final SoundEvent COPPER_HAIL_MARY = register("controls/copper_hail_mary");

    // Console
    public static final SoundEvent CONSOLE_AMBIENT = register("tardis/console_ambient");
    public static final SoundEvent CONSOLE_BOOTUP = register("tardis/console_bootup");

    // Hums
    public static final SoundEvent TOYOTA_HUM = register("tardis/hums/toyota_hum");
    public static final SoundEvent CORAL_HUM = register("tardis/hums/coral_hum");
    public static final SoundEvent CHRISTMAS_HUM = register("tardis/hums/advent/christmas_hum");
    public static final SoundEvent EIGHT_HUM = register("tardis/hums/eight_hum");
    public static final SoundEvent COPPER_HUM = register("tardis/hums/copper_hum");
    public static final SoundEvent EXILE_HUM = register("tardis/hums/exile_hum");
    public static final SoundEvent PRIME_HUM = register("tardis/hums/prime_hum");
    public static final SoundEvent RENAISSANCE_HUM = register("tardis/hums/renaissance_hum");

    public static final SoundEvent CLOISTER = register("tardis/cloister");



    // Creaks
    public static final SoundEvent CREAK_ONE = register("tardis/creaks/creak_one");
    public static final SoundEvent CREAK_TWO = register("tardis/creaks/creak_two");
    public static final SoundEvent CREAK_THREE = register("tardis/creaks/creak_three");
    public static final SoundEvent CREAK_FOUR = register("tardis/creaks/creak_four");
    public static final SoundEvent CREAK_FIVE = register("tardis/creaks/creak_five");
    public static final SoundEvent CREAK_SIX = register("tardis/creaks/creak_six");
    public static final SoundEvent CREAK_SEVEN = register("tardis/creaks/creak_seven");
    public static final SoundEvent WHISPER = register("tardis/creaks/whisper");

    // Outside Sounds
    public static final SoundEvent VORTEX_SOUND = register("tardis/vortex_sound");
    public static final SoundEvent RAIN = register("tardis/exterior/rain");
    public static final SoundEvent THUNDER = register("tardis/exterior/thunder");

    // Tools
    public static final SoundEvent DING = register("tools/goes_ding");
    public static final SoundEvent STASER = register("tools/staser");
    public static final SoundEvent REMOTE = register("tools/remote");
    public static final SoundEvent REDSTONE_CONTROL_SWITCHAROO = register("tools/redstone_control_switcharoo");
    public static final SoundEvent HAMMER_HIT = register("tools/hammer_hit");
    public static final SoundEvent COFFEE_MACHINE = register("tardis/coffee_machine");

    // Rifts
    public static final SoundEvent RIFT1_AMBIENT = register("rift/ambient1");
    public static final SoundEvent RIFT2_AMBIENT = register("rift/ambient2");
    public static final SoundEvent RIFT3_AMBIENT = register("rift/ambient3");
    public static final SoundEvent RIFT_SUCCESS = register("rift/success");
    public static final SoundEvent RIFT_FAIL = register("rift/fail");
    public static final SoundEvent RIFT_SONIC = register("rift/sonic_charge");


    // Fabricator
    public static final SoundEvent FABRICATOR_START = register("tools/fabricator/start");
    public static final SoundEvent FABRICATOR_END = register("tools/fabricator/end");
    public static final SoundEvent FABRICATOR_LOOP = register("tools/fabricator/loop");

    // Sonic
    public static final SoundEvent SONIC_USE = register("sonic/use");
    public static final SoundEvent SONIC_SWITCH = register("sonic/switch");
    public static final SoundEvent SONIC_ON = register("sonic/sonic_on");
    public static final SoundEvent SONIC_OFF = register("sonic/sonic_off");
    public static final SoundEvent SONIC_TWEAK = register("sonic/sonic_tweak");
    public static final SoundEvent SONIC_MENDING = register("sonic/sonic_mending");
    public static final SoundEvent SONIC_DUEL = register("sonic/sonic_duel");

    // Secret
    public static final SoundEvent DOOM_DOOR_OPEN = register("tardis/secret/doom_door_open");
    public static final SoundEvent DOOM_DOOR_CLOSE = register("tardis/secret/doom_door_close");
    public static final SoundEvent ERROR = register("error");

    // Engine
    public static final SoundEvent ENGINE_REFUEL = register("tardis/engine_refuel");
    public static final SoundEvent ENGINE_LOOP = register("tardis/engine_loop");

    // Engine related stuff
    public static final SoundEvent POWER_CONVERT = register("tardis/power_convert");
    public static final SoundEvent FLUID_LINK_CONNECT = register("tools/fluid_link_connect");

    // Handles
    public static final SoundEvent HANDLES_AFFIRMATIVE = register("tardis/handles/affirmative");
    public static final SoundEvent HANDLES_DENIED = register("tardis/handles/denied");
    public static final SoundEvent HANDLES_FAULT_DEVELOPED = register("tardis/handles/fault_developed");
    public static final SoundEvent HANDLES_PLEASE_ASK_AGAIN = register("tardis/handles/please_ask_again");
    public static final SoundEvent HANDLES_PARDON = register("tardis/handles/pardon");
    public static final SoundEvent HANDLES_WHEN = register("tardis/handles/when");

    // Mobs
    // Cybermen2005
    public static final SoundEvent DELETE = register("mobs/cybermen/2005/delete");
    public static final SoundEvent UNNECESSARY = register("mobs/cybermen/2005/unnecessary");
    public static final SoundEvent SUPERIOR = register("mobs/cybermen/2005/superior");
    public static final SoundEvent DELETED = register("mobs/cybermen/2005/deleted");
    public static final SoundEvent CYBER_STUN = register("mobs/cybermen/2005/cyber_stun");
    public static final SoundEvent STOMP4 = register("mobs/cybermen/2005/stomp4");
    public static final SoundEvent STOMP3 = register("mobs/cybermen/2005/stomp3");
    public static final SoundEvent STOMP2 = register("mobs/cybermen/2005/stomp2");
    public static final SoundEvent STOMP1 = register("mobs/cybermen/2005/stomp1");
    public static final SoundEvent CYBER_FIRE = register("mobs/cybermen/2005/cyber_fire");
    public static final SoundEvent ELECTRIC_HIT = register("mobs/cybermen/2005/electric_hit");

    // Nightmare in silver
    public static final SoundEvent UPGRADE_IN_PROGRESS = register("mobs/cybermen/nightmare/upgrade_in_progress");
    public static final SoundEvent UPGRADE_IN_PROGRESS_ALT = register("mobs/cybermen/nightmare/upgrade_in_progress_alt");
    public static final SoundEvent SILVER_STOMP_LOOP = register("mobs/cybermen/nightmare/silver_stomp_loop");
    public static final SoundEvent SILVER_STOMP_START_FINISH = register("mobs/cybermen/nightmare/silver_stomp_start_finish");



    public static void init() {

    }
    private static SoundEvent register(String name) {
        return register(AITMod.id(name));
    }
    private static SoundEvent register(Identifier id) {
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
    public static List<SoundEvent> getSounds(String modid) {
        return Registries.SOUND_EVENT.stream().filter(sound -> sound.getId().getNamespace().equals(modid)).toList();
    }
}

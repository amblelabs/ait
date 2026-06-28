package dev.amble.ait.registry.impl;

import java.util.HashMap;
import java.util.List;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;

import net.minecraft.item.ItemStack;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.handles.HandlesResponse;
import dev.amble.ait.core.handles.HandlesSound;
import dev.amble.ait.core.item.HandlesItem;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.impl.SecurityControl;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.world.TardisServerWorld;

/**
 * Registry for Handles responses.
 * This is using Minecraft Registries, so just call HandlesResponseRegistry.register in your mod initialization.
 * @author james
 */
public class HandlesResponseRegistry {
    public static final SimpleRegistry<HandlesResponse> REGISTRY = FabricRegistryBuilder
            .createSimple(RegistryKey.<HandlesResponse>ofRegistry(AITMod.id("handles")))
            .buildAndRegister();
    private static HashMap<String, HandlesResponse> COMMANDS_CACHE;
    public static HandlesResponse DEFAULT;

    public static HandlesResponse register(HandlesResponse schema) {
        COMMANDS_CACHE = null;

        return Registry.register(REGISTRY, schema.id(), schema);
    }

    public static HandlesResponse get(String command) {
        if (COMMANDS_CACHE == null) {
            fillCommands();
        }
        HandlesResponse found = COMMANDS_CACHE.get(command);

        if (found != null) {
            return found;
        }

        int minDistance = Integer.MAX_VALUE;
        HandlesResponse closest = null;
        for (String key : COMMANDS_CACHE.keySet()) {
            int distance = COMMANDS_CACHE.get(key).distance(key, command);
            if (distance < minDistance) {
                minDistance = distance;
                closest = COMMANDS_CACHE.get(key);
            }
        }

        if (closest != null && minDistance <= AITModClient.CONFIG.handlesLevenshteinDistance) {
            return closest;
        }

        return DEFAULT;
    }

    private static void fillCommands() {
        COMMANDS_CACHE = new HashMap<>();
        for (HandlesResponse response : REGISTRY) {
            for (String command : response.getCommandWords()) {
                COMMANDS_CACHE.put(command, response);
            }
        }
    }

    public static void init() {
        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register(HandlesResponseRegistry::onChatMessage);

        DEFAULT = register(new HandlesResponse() {
            @Override
            public boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis) {
                return failure(source);
            }

            @Override
            public SoundEvent failureSound() {
                return AITMod.RANDOM.nextBoolean() ? AITSounds.HANDLES_PLEASE_ASK_AGAIN : AITSounds.HANDLES_PARDON;
            }

            @Override
            public List<String> getCommandWords() {
                return List.of();
            }

            @Override
            public Identifier id() {
                return AITMod.id("default");
            }
        });

        register(new HandlesResponse() {
            @Override
            public boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis) {
                sendChat(player, getHelpText());
                return success(source);
            }

            private Text getHelpText() {
                return Text.translatable("message.ait.handles.available_commands",
                        String.join(", ", COMMANDS_CACHE.keySet()));
            }

            @Override
            public List<String> getCommandWords() {
                return List.of("help");
            }

            @Override
            public Identifier id() {
                return AITMod.id("help");
            }
        });

        register(new HandlesResponse() {
            private static final List<String> JOKES = List.of(
                    "message.ait.handles.joke.dalek",
                    "message.ait.handles.joke.time_lords",
                    "message.ait.handles.joke.hide_and_seek",
                    "message.ait.handles.joke.no_time",
                    "message.ait.handles.joke.calm"
            );

            @Override
            public boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis) {
                sendChat(player, getRandomJoke());
                return success(source);
            }

            private Text getRandomJoke() {
                return Text.translatable(JOKES.get(AITMod.RANDOM.nextInt(JOKES.size())));
            }

            @Override
            public List<String> getCommandWords() {
                return List.of("tell me a joke");
            }

            @Override
            public Identifier id() {
                return AITMod.id("joke");
            }
        });

        register(new HandlesResponse() {
            private static final List<String> FUN_FACTS = List.of(
                    "message.ait.handles.fun_fact.green_tardis",
                    "message.ait.handles.fun_fact.gallifrey",
                    "message.ait.handles.fun_fact.handles"
            );

            @Override
            public boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis) {
                sendChat(player, getRandomFunFact());
                return success(source);
            }

            private Text getRandomFunFact() {
                return Text.translatable(FUN_FACTS.get(AITMod.RANDOM.nextInt(FUN_FACTS.size())));
            }

            @Override
            public List<String> getCommandWords() {
                return List.of("tell me a fun fact");
            }

            @Override
            public Identifier id() {
                return AITMod.id("fun_fact");
            }
        });

        register(new HandlesResponse() {
            @Override
            public boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis) {
                if (tardis.travel().inFlight()) {
                    sendChat(player, Text.translatable("message.ait.handles.already_in_flight"));
                    return failure(source);
                }

                tardis.travel().dematerialize();
                sendChat(player, Text.translatable("message.ait.handles.dematerializing"));
                return success(source);
            }

            @Override
            public List<String> getCommandWords() {
                return List.of("dematerialize", "take off");
            }

            @Override
            public Identifier id() {
                return AITMod.id("dematerialize");
            }
        });

        register(new HandlesResponse() {
            @Override
            public boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis) {
                if (!tardis.travel().inFlight()) {
                    sendChat(player, Text.translatable("message.ait.handles.not_in_flight"));
                    return failure(source);
                }

                tardis.travel().rematerialize();
                sendChat(player, Text.translatable("message.ait.handles.rematerializing"));
                return success(source);
            }

            @Override
            public List<String> getCommandWords() {
                return List.of("rematerialize", "land");
            }

            @Override
            public Identifier id() {
                return AITMod.id("rematerialize");
            }
        });

        register(new HandlesResponse() {
            @Override
            public boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis) {
                if (tardis.door().locked()) {
                    sendChat(player, Text.translatable("message.ait.handles.doors_already_locked"));
                    return failure(source);
                }

                tardis.door().setLocked(true);
                sendChat(player, Text.translatable("message.ait.handles.locking_doors"));
                return success(source);
            }

            @Override
            public List<String> getCommandWords() {
                return List.of("lock", "lock door");
            }

            @Override
            public Identifier id() {
                return AITMod.id("lock");
            }
        });

        register(new HandlesResponse() {
            @Override
            public boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis) {
                if (!tardis.door().locked()) {
                    sendChat(player, Text.translatable("message.ait.handles.doors_already_unlocked"));
                    return failure(source);
                }

                tardis.door().setLocked(false);
                sendChat(player, Text.translatable("message.ait.handles.unlocking_doors"));
                return success(source);
            }

            @Override
            public List<String> getCommandWords() {
                return List.of("unlock", "unlock door");
            }

            @Override
            public Identifier id() {
                return AITMod.id("unlock");
            }
        });

        register(new HandlesResponse() {
            @Override
            public boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis) {
                if (!tardis.waypoint().hasWaypoint()) {
                    sendChat(player, Text.translatable("message.ait.handles.no_waypoint"));
                    return failure(source);
                }

                sendChat(player, Text.translatable("message.ait.handles.setting_course_waypoint"));
                tardis.waypoint().loadWaypoint();
                return success(source);
            }

            @Override
            public List<String> getCommandWords() {
                return List.of("go to waypoint", "travel to waypoint");
            }

            @Override
            public Identifier id() {
                return AITMod.id("travel_waypoint");
            }
        });

        register(new HandlesResponse() {
            @Override
            public boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis) {
                if (tardis.door().isOpen()) {
                    sendChat(player, Text.translatable("message.ait.handles.doors_already_open"));
                    return failure(source);
                }

                sendChat(player, Text.translatable("message.ait.handles.opening_doors"));
                tardis.door().openDoors();
                return success(source);
            }

            @Override
            public List<String> getCommandWords() {
                return List.of("open", "open the door");
            }

            @Override
            public Identifier id() {
                return AITMod.id("open_door");
            }
        });

        register(new HandlesResponse() {
            @Override
            public boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis) {
                if (!tardis.door().isOpen()) {
                    sendChat(player, Text.translatable("message.ait.handles.doors_already_closed"));
                    return failure(source);
                }

                sendChat(player, Text.translatable("message.ait.handles.closing_doors"));
                tardis.door().closeDoors();
                return success(source);
            }

            @Override
            public List<String> getCommandWords() {
                return List.of("close", "close the door");
            }

            @Override
            public Identifier id() {
                return AITMod.id("close_door");
            }
        });


        register(new HandlesResponse() {
            @Override
            public boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis) {
                TravelHandlerBase.State state = tardis.travel().getState();
                sendChat(player, Text.translatable("message.ait.handles.tardis_state", state.name()));

                if (state == TravelHandlerBase.State.FLIGHT) {
                    sendChat(player, Text.translatable("message.ait.handles.flight_complete",
                            tardis.travel().getDurationAsPercentage()));
                }

                return success(source);
            }

            @Override
            public List<String> getCommandWords() {
                return List.of("progress", "flight status", "flight progress");
            }

            @Override
            public Identifier id() {
                return AITMod.id("progress");
            }
        });

        HandlesResponseRegistry.register(new HandlesResponse() {
            @Override
            public boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis) {
                sendChat(player, Text.translatable("message.ait.handles.toggled_shields"));
                tardis.shields().visuallyShielded().toggle();
                return success(source);
            }

            @Override
            public List<String> getCommandWords() {
                return List.of("toggle shields", "shields");
            }

            @Override
            public Identifier id() {
                return AITMod.id("toggle_shields");
            }
        });

        HandlesResponseRegistry.register(new HandlesResponse() {
            @Override
            public boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis) {
                if (tardis.isRefueling()) {
                    sendChat(player, Text.translatable("message.ait.handles.refueling_already_enabled"));
                    return failure(source);
                }

                sendChat(player, Text.translatable("message.ait.handles.enabling_refueling"));
                tardis.travel().handbrake(true);
                tardis.setRefueling(true);
                return success(source);
            }

            @Override
            public List<String> getCommandWords() {
                return List.of("refuel");
            }

            @Override
            public Identifier id() {
                return AITMod.id("enable_refuel");
            }
        });

        HandlesResponseRegistry.register(new HandlesResponse() {
            @Override
            public boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis) {
                if (!tardis.isRefueling()) {
                    sendChat(player, Text.translatable("message.ait.handles.refueling_already_disabled"));
                    return failure(source);
                }

                sendChat(player, Text.translatable("message.ait.handles.disabling_refueling"));
                tardis.travel().handbrake(false);
                tardis.setRefueling(false);
                return success(source);
            }

            @Override
            public List<String> getCommandWords() {
                return List.of("stop refuel");
            }

            @Override
            public Identifier id() {
                return AITMod.id("disable_refuel");
            }
        });

        HandlesResponseRegistry.register(new HandlesResponse() {
            @Override
            public boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis) {
                sendChat(player, Text.translatable("message.ait.handles.protocol_3_toggled"));
                tardis.cloak().cloaked().toggle();
                return success(source);
            }

            @Override
            public List<String> getCommandWords() {
                return List.of("p3");
            }

            @Override
            public Identifier id() {
                return AITMod.id("toggle_cloak");
            }
        });

        HandlesResponseRegistry.register(new HandlesResponse() {
            @Override
            public boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis) {
                sendChat(player, Text.translatable("message.ait.handles.antigravs_toggled"));
                tardis.travel().antigravs().toggle();
                return success(source);
            }

            @Override
            public List<String> getCommandWords() {
                return List.of("antigravs");
            }

            @Override
            public Identifier id() {
                return AITMod.id("toggle_antigravs");
            }
        });
    }


    private static boolean onChatMessage(SignedMessage signedMessage, ServerPlayerEntity player, MessageType.Parameters parameters) {
        ItemStack stack;
        String message = signedMessage.getSignedContent();

        boolean bl = message.toLowerCase().startsWith("handles");
        if (player.getWorld().isClient()) return true;
        if (!bl) return true;

        String command = message.toLowerCase().replace(",", "")
                .replace("handles ", "");
        HandlesResponse response = get(command);

        for (int i = 0; i < player.getInventory().size(); i++) {
            stack = player.getInventory().getStack(i);

            if (stack.getItem() instanceof HandlesItem item && item.isLinked(stack)) {
                Tardis tardis = item.getTardis(player.getWorld(), stack);

                if (tardis.butler().getHandles() == null) {
                    response.run(player, HandlesSound.of(player), tardis.asServer());
                    return false;
                }

                break;
            }
        }

        if (!(player.getWorld() instanceof TardisServerWorld tardisWorld))
            return true;

        Tardis tardis = tardisWorld.getTardis();

        if (tardis.butler().getHandles() == null)
            return true;

        if (response.requiresSudo() && tardis.stats().security().get()
                && !SecurityControl.hasMatchingKey(player, tardis))
            return true;

        response.run(player, HandlesSound.of(tardis.asServer()), tardis.asServer());
        return false;
    }
}

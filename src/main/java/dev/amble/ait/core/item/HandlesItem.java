package dev.amble.ait.core.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.link.LinkableItem;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

public class HandlesItem extends LinkableItem {
    private static final Map<String, HandlesResponses> RESPONSE_MAP = new HashMap<>();
    public HandlesItem(Settings settings) {
        super(settings, true);
    }

    static {
        //Misc stuff
        RESPONSE_MAP.put("help", HandlesResponses.HELP);
        RESPONSE_MAP.put("tell me a joke", HandlesResponses.JOKE);
        RESPONSE_MAP.put("tell me a fun fact", HandlesResponses.FUN_FACT);

        //Demat
        RESPONSE_MAP.put("take off", HandlesResponses.TAKE_OFF);
        RESPONSE_MAP.put("takeoff", HandlesResponses.TAKE_OFF);
        RESPONSE_MAP.put("start flight", HandlesResponses.TAKE_OFF);
        RESPONSE_MAP.put("fly", HandlesResponses.TAKE_OFF);
        RESPONSE_MAP.put("allons-y", HandlesResponses.TAKE_OFF);
        RESPONSE_MAP.put("geronimo", HandlesResponses.TAKE_OFF);
        RESPONSE_MAP.put("demat", HandlesResponses.TAKE_OFF);
        RESPONSE_MAP.put("dematerialize", HandlesResponses.TAKE_OFF);

        //Remat
        RESPONSE_MAP.put("land", HandlesResponses.LAND);
        RESPONSE_MAP.put("stop flight", HandlesResponses.LAND);
        RESPONSE_MAP.put("stop flying", HandlesResponses.LAND);

        //Shields
        RESPONSE_MAP.put("toggle shields", HandlesResponses.TOGGLE_SHIELDS);
        RESPONSE_MAP.put("shields", HandlesResponses.TOGGLE_SHIELDS);

        //Alarms
        RESPONSE_MAP.put("toggle alarms", HandlesResponses.TOGGLE_ALARMS);
        RESPONSE_MAP.put("alarms", HandlesResponses.TOGGLE_ALARMS);
        RESPONSE_MAP.put("cloister", HandlesResponses.TOGGLE_ALARMS);
        RESPONSE_MAP.put("cloister bells", HandlesResponses.TOGGLE_ALARMS);

        //Anti-Gravs
        RESPONSE_MAP.put("toggle antigravs", HandlesResponses.TOGGLE_ANTIGRAVS);
        RESPONSE_MAP.put("antigravs", HandlesResponses.TOGGLE_ANTIGRAVS);
        RESPONSE_MAP.put("antimavs", HandlesResponses.TOGGLE_ANTIGRAVS);
        RESPONSE_MAP.put("gravity", HandlesResponses.TOGGLE_ANTIGRAVS);

        //Cloak
        RESPONSE_MAP.put("toggle cloak", HandlesResponses.TOGGLE_CLOAK);
        RESPONSE_MAP.put("cloak", HandlesResponses.TOGGLE_CLOAK);
        RESPONSE_MAP.put("p3", HandlesResponses.TOGGLE_CLOAK);
        RESPONSE_MAP.put("protocol 3", HandlesResponses.TOGGLE_CLOAK);

        //Open door
        RESPONSE_MAP.put("open doors", HandlesResponses.OPEN_DOOR);
        RESPONSE_MAP.put("open the doors", HandlesResponses.OPEN_DOOR);
        RESPONSE_MAP.put("open", HandlesResponses.OPEN_DOOR);
        RESPONSE_MAP.put("door open", HandlesResponses.OPEN_DOOR);
        RESPONSE_MAP.put("open door", HandlesResponses.OPEN_DOOR);
        RESPONSE_MAP.put("open sesame", HandlesResponses.OPEN_DOOR);

        //Close door
        RESPONSE_MAP.put("close the doors", HandlesResponses.CLOSE_DOOR);
        RESPONSE_MAP.put("close doors", HandlesResponses.CLOSE_DOOR);
        RESPONSE_MAP.put("close", HandlesResponses.CLOSE_DOOR);
        RESPONSE_MAP.put("door close", HandlesResponses.CLOSE_DOOR);
        RESPONSE_MAP.put("close door", HandlesResponses.CLOSE_DOOR);

        //Toggle lock
        RESPONSE_MAP.put("toggle lock", HandlesResponses.TOGGLE_LOCK);
        RESPONSE_MAP.put("toggle door lock", HandlesResponses.TOGGLE_LOCK);
        RESPONSE_MAP.put("lock", HandlesResponses.TOGGLE_LOCK);
        RESPONSE_MAP.put("door lock", HandlesResponses.TOGGLE_LOCK);

        //Activate handbrake
        RESPONSE_MAP.put("activate handbrake", HandlesResponses.ACTIVATE_HANDBRAKE);
        RESPONSE_MAP.put("handbrake on", HandlesResponses.ACTIVATE_HANDBRAKE);

        //Disable handbrake
        RESPONSE_MAP.put("disable handbrake", HandlesResponses.DISABLE_HANDBRAKE);
        RESPONSE_MAP.put("handbrake off", HandlesResponses.DISABLE_HANDBRAKE);

        //Activate refuel
        RESPONSE_MAP.put("enable refuelling", HandlesResponses.ACTIVATE_REFUEL);
        RESPONSE_MAP.put("activate refuelling", HandlesResponses.ACTIVATE_REFUEL);
        RESPONSE_MAP.put("activate refuel", HandlesResponses.ACTIVATE_REFUEL);
        RESPONSE_MAP.put("refuel", HandlesResponses.ACTIVATE_REFUEL);
        RESPONSE_MAP.put("start refueling", HandlesResponses.ACTIVATE_REFUEL);
        RESPONSE_MAP.put("refueling on", HandlesResponses.ACTIVATE_REFUEL);

        //Disable refuelling
        RESPONSE_MAP.put("stop refueling", HandlesResponses.DISABLE_REFUEL);
        RESPONSE_MAP.put("stop refuel", HandlesResponses.DISABLE_REFUEL);
        RESPONSE_MAP.put("disable refueling", HandlesResponses.DISABLE_REFUEL);
        RESPONSE_MAP.put("disable refuel", HandlesResponses.DISABLE_REFUEL);
        RESPONSE_MAP.put("halt refueling process", HandlesResponses.DISABLE_REFUEL);
        RESPONSE_MAP.put("refueling off", HandlesResponses.DISABLE_REFUEL);

        //Displace
        RESPONSE_MAP.put("displace", HandlesResponses.DISPLACE);
        RESPONSE_MAP.put("waypoint", HandlesResponses.DISPLACE);
        RESPONSE_MAP.put("go to waypoint", HandlesResponses.DISPLACE);
        RESPONSE_MAP.put("fly to waypoint", HandlesResponses.DISPLACE);


    }

    /**
     * @see dev.amble.ait.core.handles.HandlesResponse
     */
    @Deprecated(forRemoval = true)
    public enum HandlesResponses implements StringIdentifiable {
        DEFAULT {
            @Override
            public Text getResponseText(Tardis tardis, PlayerEntity player) {
                return Text.translatable("message.ait.handles.default", player.getName());
            }

            @Override
            public void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                if (tardis == null) return;
                failed(tardis, player, world);
            }

            @Override
            public void failed(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITMod.RANDOM.nextBoolean() ? AITSounds.HANDLES_PARDON : AITSounds.HANDLES_PLEASE_ASK_AGAIN, SoundCategory.PLAYERS, 1f, 1f);
                });
            }

            @Override
            public void success(Tardis tardis, PlayerEntity player, ServerWorld world) {
            }
        },
        HELP {
            @Override
            public void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                success(tardis, player, world);
            }

            @Override
            public Text getResponseText(Tardis tardis, PlayerEntity player) {
                return Text.literal("<Handles> Here are all the available commands: "
                        + new ArrayList<>(RESPONSE_MAP.keySet()));
            }

            @Override
            public void failed(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_DENIED, SoundCategory.PLAYERS, 1f, 1f);
                });
            }

            @Override
            public void success(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_AFFIRMATIVE, SoundCategory.PLAYERS, 1f, 1f);
                });
            }
        },
        JOKE {
            @Override
            public void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                success(tardis, player, world);
            }

            @Override
            public Text getResponseText(Tardis tardis, PlayerEntity player) {
                List<String> jokes = List.of(
                        "Why did the Dalek apply for a job? It wanted to EX-TER-MINATE its competition!",
                        "How many Time Lords does it take to change a light bulb? None, they just change the timeline.",
                        "Why does the TARDIS always win hide-and-seek? Because it’s in another dimension!",
                        "What do you call a Time Lord with no time? A Lord!",
                        "Why was the TARDIS always calm? Because it’s bigger on the inside."
                );
                String randomJoke = jokes.get((int) (Math.random() * jokes.size()));
                return Text.literal("<Handles> " + randomJoke);
            }

            @Override
            public void failed(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_DENIED, SoundCategory.PLAYERS, 1f, 1f);
                });
            }

            @Override
            public void success(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_AFFIRMATIVE, SoundCategory.PLAYERS, 1f, 1f);
                });

                world.getServer().execute(() -> {
                    player.sendMessage(getResponseText(tardis, player), false);
                });
            }
        },
        FUN_FACT {
            @Override
            public void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                success(tardis, player, world);
            }

            @Override
            public Text getResponseText(Tardis tardis, PlayerEntity player) {
                List<String> funFacts = List.of(
                        "The first TARDIS was actually painted green!",
                        "Gallifrey has two suns and an orange sky!",
                        "Handles once saved the Doctor’s life by solving a centuries-old riddle."
                );
                String randomFact = funFacts.get((int) (Math.random() * funFacts.size()));
                return Text.literal("<Handles> " + randomFact);
            }

            @Override
            public void failed(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_DENIED, SoundCategory.PLAYERS, 1f, 1f);
                });
            }

            @Override
            public void success(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_AFFIRMATIVE, SoundCategory.PLAYERS, 1f, 1f);
                });

                world.getServer().execute(() -> {
                    player.sendMessage(getResponseText(tardis, player), false);
                });
            }
        },
        TAKE_OFF {
            @Override
            public Text getResponseText(Tardis tardis, PlayerEntity player) {
                return null;
            }

            @Override
            public void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                if (tardis == null) return;

                if (!tardis.travel().isLanded()) {
                    failed(tardis, player, world);
                    return;
                }

                boolean doors = tardis.door().isOpen();
                boolean handbrake = tardis.travel().handbrake();
                boolean speed = tardis.travel().speed() <= 0;

                if (handbrake) tardis.travel().handbrake(false);
                if (doors) tardis.door().closeDoors();
                if (speed) tardis.travel().increaseSpeed();

                world.getServer().execute(() -> {
                    player.sendMessage(Text.translatable("message.ait.handles.take_off", player.getName()));
                });
                success(tardis, player, world);
                }

            @Override
            public void failed(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_DENIED, SoundCategory.PLAYERS, 1f, 1f);
                });
            }

            @Override
            public void success(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_AFFIRMATIVE, SoundCategory.PLAYERS, 1f, 1f);
                });

            }
        },
        DISPLACE {
            @Override
            public Text getResponseText(Tardis tardis, PlayerEntity player) {
                return null;
            }

            @Override
            public void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                if (tardis == null) return;

                if (!tardis.travel().isLanded() || !tardis.waypoint().hasWaypoint() || !tardis.travel().autopilot()) {
                    failed(tardis, player, world);
                    return;
                }

                boolean doors = tardis.door().isOpen();
                boolean handbrake = tardis.travel().handbrake();
                boolean speed = tardis.travel().speed() <= 0;

                if (handbrake) tardis.travel().handbrake(false);
                if (doors) tardis.door().closeDoors();
                if (speed) tardis.travel().increaseSpeed();

                world.getServer().execute(() -> {
                    player.sendMessage(Text.translatable("message.ait.handles.displace", player.getName()));
                });
                success(tardis, player, world);
                }

            @Override
            public void failed(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_DENIED, SoundCategory.PLAYERS, 1f, 1f);
                });
            }

            @Override
            public void success(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_AFFIRMATIVE, SoundCategory.PLAYERS, 1f, 1f);
                });
            }
        },
        LAND {
            @Override
            public Text getResponseText(Tardis tardis, PlayerEntity player) {
                return null;
            }
            @Override
            public void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                if (tardis == null) return;

                if (tardis.travel().getState() == TravelHandlerBase.State.DEMAT) {
                    tardis.travel().handbrake(true);
                    success(tardis, player, world);
                    return;
                }

                if (!tardis.travel().inFlight() || tardis.flight().isFlying()) {
                    failed(tardis, player, world);
                    return;
                }

                boolean speed = tardis.travel().speed() > 0;
                boolean doors = tardis.door().isOpen();

                if (doors) tardis.door().closeDoors();
                if (speed) tardis.travel().speed(0);
                tardis.travel().handbrake(true);
                world.getServer().execute(() -> {
                    player.sendMessage(Text.translatable("message.ait.handles.land", player.getName()));
                });
                success(tardis, player, world);
            }

            @Override
            public void failed(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_DENIED, SoundCategory.PLAYERS, 1f, 1f);
                });
            }

            @Override
            public void success(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_AFFIRMATIVE, SoundCategory.PLAYERS, 1f, 1f);
                });
            }
        },
        ACTIVATE_REFUEL {
            @Override
            public Text getResponseText(Tardis tardis, PlayerEntity player) {
                return null;
            }
            @Override
            public void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                if (tardis == null) return;

                if (tardis.travel().inFlight() || tardis.flight().isFlying()) {
                    failed(tardis, player, world);
                    return;
                }

                if (tardis.travel().getState() == TravelHandlerBase.State.LANDED) {
                    tardis.travel().handbrake(true);
                    tardis.setRefueling(true);
                    world.getServer().execute(() -> {
                        player.sendMessage(Text.translatable("message.ait.handles.activate_refuel", player.getName()));
                    });
                    success(tardis, player, world);
                }
                    return;
                }

            @Override
            public void failed(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_DENIED, SoundCategory.PLAYERS, 1f, 1f);
                });
            }

            @Override
            public void success(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_AFFIRMATIVE, SoundCategory.PLAYERS, 1f, 1f);
                });
            }
        },
        DISABLE_REFUEL {
            @Override
            public Text getResponseText(Tardis tardis, PlayerEntity player) {
                return null;
            }
            @Override
            public void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                if (tardis == null) return;

                if (tardis.travel().getState() == TravelHandlerBase.State.LANDED) {
                    tardis.setRefueling(false);
                    world.getServer().execute(() -> {
                        player.sendMessage(Text.translatable("message.ait.handles.disable_refuel", player.getName()));
                    });
                    success(tardis, player, world);
                    return;
                }

            }
            @Override
            public void failed(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_DENIED, SoundCategory.PLAYERS, 1f, 1f);
                });
            }

            @Override
            public void success(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_AFFIRMATIVE, SoundCategory.PLAYERS, 1f, 1f);
                });
            }
        },
        TOGGLE_SHIELDS {
            @Override
            public Text getResponseText(Tardis tardis, PlayerEntity player) {
                return null;
            }
            @Override
            public void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                if (tardis == null) return;

                tardis.shields().visuallyShielded().toggle();
                world.getServer().execute(() -> {
                    player.sendMessage(Text.translatable("message.ait.handles.toggle_shields", player.getName()));
                });
                success(tardis, player, world);
            }
            @Override
            public void failed(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_DENIED, SoundCategory.PLAYERS, 1f, 1f);
                });
            }

            @Override
            public void success(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_AFFIRMATIVE, SoundCategory.PLAYERS, 1f, 1f);
                });
            }
        },
        OPEN_DOOR {
            @Override
            public Text getResponseText(Tardis tardis, PlayerEntity player) {
                return null;
            }


            @Override
            public void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                if (tardis == null) return;


                if (tardis.door().locked()) {
                    failed(tardis, player, world);
                    return;
                }

                tardis.door().openDoors();
                world.getServer().execute(() -> {
                    player.sendMessage(Text.translatable("message.ait.handles.open_doors", player.getName()));
                });
                success(tardis, player, world);
            }
            @Override
            public void failed(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_DENIED, SoundCategory.PLAYERS, 1f, 1f);
                });
            }

            @Override
            public void success(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_AFFIRMATIVE, SoundCategory.PLAYERS, 1f, 1f);
                });
            }
        },
        CLOSE_DOOR {
            @Override
            public Text getResponseText(Tardis tardis, PlayerEntity player) {
                return null;
            }
            @Override
            public void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                if (tardis == null) return;

                tardis.door().closeDoors();
                 world.getServer().execute(() -> {
                     player.sendMessage(Text.translatable("message.ait.handles.close_doors", player.getName()));
                 });
                success(tardis, player, world);
            }
            @Override
            public void failed(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_DENIED, SoundCategory.PLAYERS, 1f, 1f);
                });
            }

            @Override
            public void success(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_AFFIRMATIVE, SoundCategory.PLAYERS, 1f, 1f);
                });
            }
        },
        TOGGLE_LOCK {
            @Override
            public Text getResponseText(Tardis tardis, PlayerEntity player) {
                return null;
            }
            @Override
            public void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                if (tardis == null) return;

                tardis.door().interactToggleLock(null, true);
                world.getServer().execute(() -> {
                    player.sendMessage(Text.translatable("message.ait.handles.toggle_lock", player.getName()));
                });
                success(tardis, player, world);
            }
            @Override
            public void failed(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_DENIED, SoundCategory.PLAYERS, 1f, 1f);
                });
            }

            @Override
            public void success(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_AFFIRMATIVE, SoundCategory.PLAYERS, 1f, 1f);
                });
            }
        },
        ACTIVATE_HANDBRAKE {
            @Override
            public Text getResponseText(Tardis tardis, PlayerEntity player) {
                return null;
            }
            @Override
            public void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                if (tardis == null) return;

                if (tardis.travel().inFlight() || tardis.flight().isFlying()) {
                    failed(tardis, player, world);
                    return;
                }

                tardis.travel().handbrake(true);

                world.getServer().execute(() -> {
                    player.sendMessage(Text.translatable("message.ait.handles.activate_handbrake", player.getName()));
                });
                success(tardis, player, world);
            }
            @Override
            public void failed(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_DENIED, SoundCategory.PLAYERS, 1f, 1f);
                });
            }

            @Override
            public void success(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_AFFIRMATIVE, SoundCategory.PLAYERS, 1f, 1f);
                });
            }
        },
        DISABLE_HANDBRAKE {
            @Override
            public Text getResponseText(Tardis tardis, PlayerEntity player) {
                return null;
            }
            @Override
            public void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                if (tardis == null) return;

                tardis.travel().handbrake(false);

                world.getServer().execute(() -> {
                    player.sendMessage(Text.translatable("message.ait.handles.disable_handbrake", player.getName()));
                });
                success(tardis, player, world);
            }
            @Override
            public void failed(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_DENIED, SoundCategory.PLAYERS, 1f, 1f);
                });
            }

            @Override
            public void success(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_AFFIRMATIVE, SoundCategory.PLAYERS, 1f, 1f);
                });
            }
        },
        TOGGLE_ANTIGRAVS {
            @Override
            public Text getResponseText(Tardis tardis, PlayerEntity player) {
                return null;
            }
            @Override
            public void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                if (tardis == null) return;

                tardis.travel().antigravs().toggle();
                world.getServer().execute(() -> {
                    player.sendMessage(Text.translatable("message.ait.handles.toggle_antigravs", player.getName()));
                });
                success(tardis, player, world);
            }
            @Override
            public void failed(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_DENIED, SoundCategory.PLAYERS, 1f, 1f);
                });
            }

            @Override
            public void success(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_AFFIRMATIVE, SoundCategory.PLAYERS, 1f, 1f);
                });
            }
        },
        TOGGLE_CLOAK {
            @Override
            public Text getResponseText(Tardis tardis, PlayerEntity player) {
                return null;
            }
            @Override
            public void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                if (tardis == null) return;

                tardis.cloak().cloaked().set(!tardis.cloak().cloaked().get());
                world.getServer().execute(() -> {
                    player.sendMessage(Text.translatable("message.ait.handles.toggle_cloaked", player.getName()));
                });
                success(tardis, player, world);
            }
            @Override
            public void failed(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_DENIED, SoundCategory.PLAYERS, 1f, 1f);
                });
            }

            @Override
            public void success(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_AFFIRMATIVE, SoundCategory.PLAYERS, 1f, 1f);
                });
            }
        },
        TOGGLE_ALARMS {
            @Override
            public Text getResponseText(Tardis tardis, PlayerEntity player) {
                return null;
            }
            @Override
            public void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                if (tardis == null) return;

                tardis.alarm().toggle();
                world.getServer().execute(() -> {
                    player.sendMessage(Text.translatable("message.ait.handles.toggle_alarms", player.getName()));
                });
                success(tardis, player, world);
            }
            @Override
            public void failed(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_DENIED, SoundCategory.PLAYERS, 1f, 1f);
                });
            }

            @Override
            public void success(Tardis tardis, PlayerEntity player, ServerWorld world) {
                tardis.getDesktop().getConsolePos().forEach(pos -> {
                    player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            AITSounds.HANDLES_AFFIRMATIVE, SoundCategory.PLAYERS, 1f, 1f);
                });
            }
        };

        public abstract void run(@Nullable Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player,
                                 ItemStack stack);

        public abstract Text getResponseText(Tardis tardis, PlayerEntity player);
        public abstract void failed(Tardis tardis, PlayerEntity player, ServerWorld world);

        public abstract void success(Tardis tardis, PlayerEntity player, ServerWorld world);

        @Override
        public String asString() {
            return StringUtils.capitalize(this.toString().replace("_", " "));
        }
    }
}

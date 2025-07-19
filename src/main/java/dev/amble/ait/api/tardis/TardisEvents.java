package dev.amble.ait.api.tardis;

import java.util.Optional;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedBlockPos;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.impl.EngineSystem;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.DoorHandler;
import dev.amble.ait.data.landing.LandingPadSpot;

public final class TardisEvents {

    // Flight
    public static final Event<Demat> DEMAT = EventFactory.createArrayBacked(Demat.class, callbacks -> tardis -> {
        for (Demat callback : callbacks) {
            Interaction value = callback.onDemat(tardis);

            if (value != Interaction.PASS)
                return value;
        }

        return Interaction.SUCCESS;
    });

    public static final Event<EnterFlight> ENTER_FLIGHT = EventFactory.createArrayBacked(EnterFlight.class, callbacks -> tardis -> {
        for (EnterFlight callback : callbacks) {
            callback.onFlight(tardis);
        }
    });

    public static final Event<FinishFlight> FINISH_FLIGHT = EventFactory.createArrayBacked(FinishFlight.class, callbacks -> tardis -> {
        for (FinishFlight callback : callbacks) {
            Interaction value = callback.onFinish(tardis);

            if (value != Interaction.PASS)
                return value;
        }

        return Interaction.PASS;
    });

    public static final Event<Mat> MAT = EventFactory.createArrayBacked(Mat.class, callbacks -> tardis -> {
        for (Mat callback : callbacks) {
            Interaction value = callback.onMat(tardis);

            if (value != Interaction.PASS)
                return value;
        }

        return Interaction.SUCCESS;
    });

    public static final Event<StartFalling> START_FALLING = EventFactory.createArrayBacked(StartFalling.class, callbacks -> tardis -> {
        for (StartFalling callback : callbacks) {
            callback.onStartFall(tardis);
        }
    });

    public static final Event<BeforeLand> BEFORE_LAND = EventFactory.createArrayBacked(BeforeLand.class, callbacks -> (tardis, pos) -> {
        for (BeforeLand callback : callbacks) {
            Result<CachedDirectedGlobalPos> result = callback.onLanded(tardis, pos);

            if (result.type() != Interaction.PASS)
                return result;

            if (result.value().isPresent())
                pos = result.value().get();
        }

        return new Result<>(Interaction.SUCCESS, pos);
    });

    public static final Event<Landed> LANDED = EventFactory.createArrayBacked(Landed.class, callbacks -> tardis -> {
        for (Landed callback : callbacks) {
            callback.onLanded(tardis);
        }
    });

    public static final Event<Crash> CRASH = EventFactory.createArrayBacked(Crash.class, callbacks -> tardis -> {
        for (Crash callback : callbacks) {
            callback.onCrash(tardis);
        }
    });

    public static final Event<LandingPadAdjust> LANDING_PAD_ADJUST = EventFactory.createArrayBacked(LandingPadAdjust.class, callbacks -> (tardis, spot) -> {
        for (LandingPadAdjust callback : callbacks) {
            callback.onLandingPadAdjust(tardis, spot);
        }
    });

    // Power / Fuel
    public static final Event<NoFuel> OUT_OF_FUEL = EventFactory.createArrayBacked(NoFuel.class,
            callbacks -> tardis -> {
                for (NoFuel callback : callbacks) {
                    callback.onNoFuel(tardis);
                }
            });

    public static final Event<LosePower> LOSE_POWER = EventFactory.createArrayBacked(LosePower.class,
            callbacks -> tardis -> {
                for (LosePower callback : callbacks) {
                    callback.onLosePower(tardis);
                }
            });

    public static final Event<RegainPower> REGAIN_POWER = EventFactory.createArrayBacked(RegainPower.class,
            callbacks -> tardis -> {
                for (RegainPower callback : callbacks) {
                    callback.onRegainPower(tardis);
                }
            });
    public static final Event<UseBackupPower> USE_BACKUP_POWER = EventFactory.createArrayBacked(UseBackupPower.class,
            callbacks -> (tardis, power) -> {
                for (UseBackupPower callback : callbacks) {
                    callback.onUse(tardis, power);
                }
            });


    // Door
    public static final Event<OpenDoor> DOOR_OPEN = EventFactory.createArrayBacked(OpenDoor.class,
            callbacks -> tardis -> {
                for (OpenDoor callback : callbacks) {
                    callback.onOpen(tardis);
                }
            });

    public static final Event<CloseDoor> DOOR_CLOSE = EventFactory.createArrayBacked(CloseDoor.class,
            callbacks -> (tardis) -> {
                for (CloseDoor callback : callbacks) {
                    callback.onClose(tardis);
                }
            });

    public static final Event<MoveDoor> DOOR_MOVE = EventFactory.createArrayBacked(MoveDoor.class,
            callbacks -> (tardis, newPos, oldPos) -> {
                for (MoveDoor callback : callbacks) {
                    callback.onMove(tardis, newPos, oldPos);
                }
            });

    public static final Event<UseDoor> USE_DOOR = EventFactory.createArrayBacked(UseDoor.class,
            callbacks -> (tardis, interior, world, player, pos) -> {
                for (UseDoor callback : callbacks) {
                    DoorHandler.InteractionResult result = callback.onUseDoor(tardis, interior, world, player, pos);
                    if (result == DoorHandler.InteractionResult.CONTINUE)
                        continue;

                    return result;
                }

                return DoorHandler.InteractionResult.CONTINUE;
            });

    public static final Event<DoorUsed> DOOR_USED = EventFactory.createArrayBacked(DoorUsed.class,
            callbacks -> (tardis, player) -> {
                for (DoorUsed callback : callbacks) {
                    DoorHandler.InteractionResult result = callback.onDoorUsed(tardis, player);
                    if (result == DoorHandler.InteractionResult.CONTINUE)
                        continue;

                    return result;
                }

                return DoorHandler.InteractionResult.CONTINUE;
            });

    public static final Event<EnterTardis> ENTER_TARDIS = EventFactory.createArrayBacked(EnterTardis.class,
            callbacks -> (tardis, entity) -> {
                for (EnterTardis callback : callbacks) {
                    callback.onEnter(tardis, entity);
                }
            });

    public static final Event<LeaveTardis> LEAVE_TARDIS = EventFactory.createArrayBacked(LeaveTardis.class,
            callbacks -> (tardis, entity) -> {
                for (LeaveTardis callback : callbacks) {
                    callback.onLeave(tardis, entity);
                }
            });
    public static final Event<BreakDoor> BREAK_DOOR = EventFactory.createArrayBacked(BreakDoor.class,
            callbacks -> (tardis, pos) -> {
                for (BreakDoor callback : callbacks) {
                    callback.onBreak(tardis, pos);
                }
            });

    public static final Event<Shields> TOGGLE_SHIELDS = EventFactory.createArrayBacked(Shields.class,
            callbacks -> (tardis, active, visual) -> {
                for (Shields callback : callbacks) {
                    callback.onShields(tardis, active, visual);
                }
            });

    public static final Event<SyncTardis> SYNC_TARDIS = EventFactory.createArrayBacked(SyncTardis.class,
            callbacks -> (tardis, chunk) -> {
                for (SyncTardis callback : callbacks) {
                    callback.sync(tardis, chunk);
                }
            });

    public static final Event<SendTardis> SEND_TARDIS = EventFactory.createArrayBacked(SendTardis.class,
            callbacks -> (tardis, player) -> {
                for (SendTardis callback : callbacks) {
                    callback.send(tardis, player);
                }
            });

    public static final Event<UnloadTardis> UNLOAD_TARDIS = EventFactory.createArrayBacked(UnloadTardis.class,
            callbacks -> (tardis, chunk) -> {
                for (UnloadTardis callback : callbacks) {
                    callback.unload(tardis, chunk);
                }
            });

    public static final Event<ReconfigureDesktop> RECONFIGURE_DESKTOP = EventFactory
            .createArrayBacked(ReconfigureDesktop.class, callbacks -> tardis -> {
                for (ReconfigureDesktop callback : callbacks) {
                    callback.reconfigure(tardis);
                }
            });

    public static final Event<OnExteriorChange> EXTERIOR_CHANGE = EventFactory.createArrayBacked(OnExteriorChange.class,
            callbacks -> tardis -> {
                for (OnExteriorChange callback : callbacks) {
                    callback.onChange(tardis);
                }
            });

    public static final Event<OnForcedEntry> FORCED_ENTRY = EventFactory.createArrayBacked(OnForcedEntry.class,
            callbacks -> (tardis, player) -> {
                for (OnForcedEntry callback : callbacks) {
                    callback.onForcedEntry(tardis, player);
                }
            });

    // Subsystem
    public static final Event<OnSubSystemBreak> SUBSYSTEM_BREAK = EventFactory.createArrayBacked(OnSubSystemBreak.class,
            callbacks -> system -> {
                for (OnSubSystemBreak callback : callbacks) {
                    callback.onBreak(system);
                }
            });
    public static final Event<OnSubSystemRepair> SUBSYSTEM_REPAIR = EventFactory.createArrayBacked(OnSubSystemRepair.class,
            callbacks -> system -> {
                for (OnSubSystemRepair callback : callbacks) {
                    callback.onRepair(system);
                }
            });
    public static final Event<OnSubSystemEnable> SUBSYSTEM_ENABLE = EventFactory.createArrayBacked(OnSubSystemEnable.class,
            callbacks -> system -> {
                for (OnSubSystemEnable callback : callbacks) {
                    callback.onEnable(system);
                }
            });
    public static final Event<OnSubSystemDisable> SUBSYSTEM_DISABLE = EventFactory.createArrayBacked(OnSubSystemDisable.class,
            callbacks -> system -> {
                for (OnSubSystemDisable callback : callbacks) {
                    callback.onDisable(system);
                }
            });
    public static final Event<OnEnginesPhase> ENGINES_PHASE = EventFactory.createArrayBacked(OnEnginesPhase.class,
            callbacks -> system -> {
                for (OnEnginesPhase callback : callbacks) {
                    callback.onPhase(system);
                }
            });

    /**
     * Called when a TARDIS successfully ( passed all checks ) starts to take off,
     * before anything else is ran
     */
    @FunctionalInterface
    public interface Demat {
        /**
         * Called when a TARDIS successfully ( passed all checks ) starts to take off,
         * before anything else is ran.
         *
         * @param tardis
         *            the tardis taking off
         * @return event's result
         */
        Interaction onDemat(Tardis tardis);
    }

    @FunctionalInterface
    public interface EnterFlight {
        /**
         * Called when a TARDIS successfully ( passed all checks ) starts to take off,
         * before anything else is ran.
         *
         * @param tardis
         *            the tardis taking off
         * @return event's result
         */
        void onFlight(Tardis tardis);
    }

    @FunctionalInterface
    public interface FinishFlight {
        /**
         * Called when a TARDIS has finished flight,
         * If result is success, the TARDIS will remat
         *
         * @param tardis
         *            the tardis taking off
         * @return event's result
         */
        Interaction onFinish(ServerTardis tardis);
    }

    @FunctionalInterface
    public interface StartFalling {
        void onStartFall(Tardis tardis);
    }

    /**
     * Called when a TARDIS successfully ( passed all checks ) starts to land,
     * before anything else is ran
     */
    @FunctionalInterface
    public interface Mat {
        /**
         * Called when a TARDIS successfully ( passed all checks ) starts to land,
         * before anything else is ran.
         *
         * @param tardis
         *            the tardis landing
         * @return event's result
         */
        Interaction onMat(ServerTardis tardis);
    }

    /**
     * Called when a TARDIS has finished landing
     */
    @FunctionalInterface
    public interface Landed {
        /**
         * Called when a TARDIS has finished landing
         *
         * @param tardis
         *            the landed tardis
         */
        void onLanded(Tardis tardis);
    }

    @FunctionalInterface
    public interface BeforeLand {
        /**
         * Called when a TARDIS has finished landing
         *
         * @param tardis
         *            the landed tardis
         */
        Result<CachedDirectedGlobalPos> onLanded(Tardis tardis, CachedDirectedGlobalPos pos);
    }

    /**
     * Called when a TARDIS starts crashing
     */
    @FunctionalInterface
    public interface Crash {
        void onCrash(Tardis tardis);
    }

    /**
     * Called whenever a landing region adjusts where the tardis lands
     */
    @FunctionalInterface
    public interface LandingPadAdjust {
        void onLandingPadAdjust(Tardis tardis, LandingPadSpot spot);
    }

    /**
     * Called whenever the fuel is set to 0
     */
    @FunctionalInterface
    public interface NoFuel {
        void onNoFuel(Tardis tardis);
    }

    @FunctionalInterface
    public interface LosePower {
        /**
         * Called when a tardis' loses power
         */
        void onLosePower(Tardis tardis);
    }

    @FunctionalInterface
    public interface RegainPower {
        /**
         * Called when a tardis regains power
         */
        void onRegainPower(Tardis tardis);
    }
    @FunctionalInterface
    public interface UseBackupPower {
        /**
         * Called when a tardis fills itself with backup power
         */
        void onUse(Tardis tardis, double power);
    }

    /**
     * Called when a TARDIS Door opens ( called when its state is set to any of the
     * "open" states, but only if it was closed before )
     */
    @FunctionalInterface
    public interface OpenDoor {
        void onOpen(Tardis tardis);
    }

    /**
     * Called when a TARDIS Door closes
     */
    @FunctionalInterface
    public interface CloseDoor {
        void onClose(Tardis tardis);
    }

    @FunctionalInterface
    public interface UseDoor {
        DoorHandler.InteractionResult onUseDoor(Tardis tardis, ServerWorld interior, ServerWorld world, @Nullable ServerPlayerEntity player, @Nullable BlockPos pos);
    }

    @FunctionalInterface
    public interface DoorUsed {
        DoorHandler.InteractionResult onDoorUsed(Tardis tardis, ServerPlayerEntity player);
    }
    /**
     * Called when the interior door position is changed, meaning it was probably
     * moved
     */
    @FunctionalInterface
    public interface MoveDoor {
        void onMove(ServerTardis tardis, @Nullable DirectedBlockPos newPos, @Nullable DirectedBlockPos oldPos);
    }

    @FunctionalInterface
    public interface EnterTardis {
        void onEnter(Tardis tardis, Entity entity);
    }

    @FunctionalInterface
    public interface LeaveTardis {
        void onLeave(Tardis tardis, Entity entity);
    }

    @FunctionalInterface
    public interface BreakDoor {
        void onBreak(Tardis tardis, DirectedBlockPos pos);
    }

    @FunctionalInterface
    public interface Shields {
        void onShields(Tardis tardis, boolean active, boolean visual);
    }

    @FunctionalInterface
    public interface SyncTardis {
        void sync(ServerPlayerEntity player, ChunkPos chunk);
    }

    @FunctionalInterface
    public interface SendTardis {
        void send(Tardis tardis, ServerPlayerEntity player);
    }

    @FunctionalInterface
    public interface UnloadTardis {
        void unload(ServerPlayerEntity player, ChunkPos chunk);
    }

    @FunctionalInterface
    public interface ReconfigureDesktop {
        void reconfigure(Tardis tardis);
    }

    @FunctionalInterface
    public interface OnExteriorChange {
        void onChange(Tardis tardis);
    }

    @FunctionalInterface
    public interface OnForcedEntry {
        void onForcedEntry(Tardis tardis, Entity entity);
    }

    @FunctionalInterface
    public interface OnSubSystemBreak {
        void onBreak(DurableSubSystem system);
    }
    @FunctionalInterface
    public interface OnSubSystemRepair {
        void onRepair(DurableSubSystem system);
    }
    @FunctionalInterface
    public interface OnSubSystemEnable {
        void onEnable(SubSystem system);
    }
    @FunctionalInterface
    public interface OnSubSystemDisable {
        void onDisable(SubSystem system);
    }
    @FunctionalInterface
    public interface OnEnginesPhase {
        void onPhase(EngineSystem system);
    }


    public enum Interaction {
        SUCCESS, FAIL, PASS
    }

    public record Result<T>(Interaction type, Optional<T> t) {

        public static <T> Result<T> success() {
            return new Result<>(Interaction.SUCCESS);
        }

        public static <T> Result<T> fail() {
            return new Result<>(Interaction.FAIL);
        }

        public static <T> Result<T> pass() {
            return new Result<>(Interaction.PASS);
        }

        public Result(Interaction inter) {
            this(inter, Optional.empty());
        }

        public Result(T t) {
            this(Interaction.PASS, t);
        }

        public Result(Interaction inter, T t) {
            this(inter, Optional.ofNullable(t));
        }

        public Optional<T> value() {
            return t;
        }
    }
}

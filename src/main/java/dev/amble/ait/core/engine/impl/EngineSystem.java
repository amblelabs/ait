package dev.amble.ait.core.engine.impl;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import dev.amble.lib.util.ServerLifecycleHooks;
import org.joml.Vector3f;

import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.TardisEvents;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.sounds.travel.TravelSoundRegistry;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelUtil;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.data.Exclude;

public class EngineSystem extends DurableSubSystem {
    @Exclude(strategy = Exclude.Strategy.FILE)
    private Status status;
    @Exclude(strategy = Exclude.Strategy.FILE)
    private Phaser phaser;

    public EngineSystem() {
        super(Id.ENGINE);
    }

    @Override
    protected void onEnable() {
        super.onEnable();

        this.tardis().fuel().enablePower(true);
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        this.tardis().fuel().disablePower();
    }

    @Override
    protected float cost() {
        return 1f;
    }

    @Override
    protected int changeFrequency() {
        return 1200; // drain 0.05 durability every minute
    }

    @Override
    protected boolean shouldDurabilityChange() {
        return tardis.fuel().hasPower();
    }

    @Override
    public void tick() {
        super.tick();

        this.tickForDurability();
        this.phaser().tick();
        this.tryUpdateStatus();
    }

    public Status status() {
        if (this.status == null) this.status = Status.OKAY;

        return this.status;
    }
    private void tryUpdateStatus() {
        if (ServerLifecycleHooks.get() == null) return;
        if (ServerLifecycleHooks.get().getTicks() % 40 != 0) return;

        this.status = Status.from(this);
        this.sync();
    }
    private void tickForDurability() {
        if (this.durability() <= 5) {
            this.tardis.alarm().enabled().set(true);
        }
    }

    @Override
    public List<ItemStack> toStacks() {
        List<ItemStack> stacks = super.toStacks();

        stacks.add(AITBlocks.ENGINE_BLOCK.asItem().getDefaultStack());

        return stacks;
    }

    public Phaser phaser() {
        if (this.phaser == null) this.phaser = Phaser.create(this);

        return this.phaser;
    }

    public static class Phaser {
        @Exclude(strategy = Exclude.Strategy.NETWORK)
        private final Function<Phaser, Boolean> allowed;
        @Exclude(strategy = Exclude.Strategy.NETWORK)
        private final Consumer<Phaser> miss;
        @Exclude(strategy = Exclude.Strategy.NETWORK)
        private final Consumer<Phaser> start;
        @Exclude(strategy = Exclude.Strategy.NETWORK)
        private final Consumer<Phaser> cancel;
        private int countdown;
        private int initial;

        public Phaser(Consumer<Phaser> onStart, Consumer<Phaser> onMiss, Consumer<Phaser> onCancel, Function<Phaser, Boolean> canPhase) {
            this.countdown = 0;
            this.miss = onMiss;
            this.allowed = canPhase;
            this.start = onStart;
            this.cancel = onCancel;
        }

        public void tick() {
            if (this.countdown > 0) {
                this.countdown--;
                if (this.countdown == 0) {
                    this.miss.accept(this);
                }
            } else {
                this.attempt();
            }
        }
        private void attempt() {
            if (this.allowed.apply(this)) {
                this.start();
            }
        }
        public void start() {
            this.initial = AITMod.RANDOM.nextInt(140, 200); // 7-10 seconds
            this.countdown = this.initial;
            this.start.accept(this);
        }
        public boolean isPhasing() {
            return this.countdown > 0;
        }
        public void cancel() {
            this.cancel.accept(this);
            this.countdown = 0;
        }

        public static Phaser create(EngineSystem system) {
            return new Phaser(
                    (phaser) -> {
                        ServerTardis tdis = system.tardis().asServer();

                        TardisUtil.sendMessageToInterior(tdis, Text.translatable("tardis.message.engine.phasing").formatted(Formatting.RED));
                        TardisUtil.sendMessageToLinked(tdis, Text.translatable("tardis.message.engine.phasing").formatted(Formatting.RED));

                        tdis.alarm().enabled().set(true);
                        tdis.getDesktop().playSoundAtEveryConsole(AITSounds.HOP_DEMAT);
                        tdis.getExterior().playSound(AITSounds.HOP_DEMAT);

                        system.tardis().subsystems().demat().removeDurability(2);
                    },
                    (phaser) -> {
                        Tardis tardis1 = system.tardis();
                        TravelHandler travel = tardis1.travel();
                        TravelUtil.randomPos(tardis1, 1, 250, cached -> {
                            travel.forceDestination(cached);
                            if (travel.isLanded()) {
                                system.tardis().subsystems().demat().removeDurability(300);

                                system.tardis().getDesktop().playSoundAtEveryConsole(AITSounds.UNSTABLE_FLIGHT_LOOP);
                                system.tardis().getExterior().playSound(AITSounds.UNSTABLE_FLIGHT_LOOP);
                                tardis1.travel().forceDemat(TravelSoundRegistry.PHASING_DEMAT);
                            }

                            TardisEvents.ENGINES_PHASE.invoker().onPhase(system);
                        });
                    },
                    (phaser) -> {
                        SoundEvent sound = (phaser.countdown < (phaser.initial - (250))) ? AITSounds.HOP_MAT : AITSounds.LAND_THUD;

                        system.tardis().getDesktop().playSoundAtEveryConsole(sound);
                        system.tardis().getExterior().playSound(sound);

                        system.tardis().alarm().enabled().set(false);
                    },
                    (phaser) -> system.tardis().travel().isLanded() && system.tardis().subsystems().demat().durability() < 250 && !system.tardis().subsystems().demat().isBroken() && !system.tardis().travel().handbrake() && !system.tardis().isGrowth() && AITMod.RANDOM.nextInt(0, 1024) == 1
            );
        }
    }

    public static boolean hasEngine(Tardis t) {
        return t.subsystems().engine().isEnabled();
    }


    public enum Status {
        OKAY(132, 195, 240) {
            @Override
            public boolean isViable(EngineSystem system) {
                return true;
            }
        },
        OFF(0, 0, 0) {
            @Override
            public boolean isViable(EngineSystem system) {
                return !system.tardis.fuel().hasPower();
            }
        },
        CRITICAL(250, 33, 22) {
            @Override
            public boolean isViable(EngineSystem system) {
                return system.phaser().isPhasing() || system.tardis.subsystems().findBrokenSubsystem().isPresent();
            }
        },
        ERROR(250, 242, 22) {
            @Override
            public boolean isViable(EngineSystem system) {
                return system.tardis.alarm().enabled().get() || system.tardis.sequence().hasActiveSequence();
            }
        },
        LEAKAGE(114, 255, 33) {
            @Override
            public boolean isViable(EngineSystem system) {
                return false; // todo
            }
        };
        public abstract boolean isViable(EngineSystem system);

        public final Vector3f colour;

        Status(int red, int green, int blue) {
            this.colour = new Vector3f(red / 255f, green / 255f, blue / 255f);
        }

        public static Status from(EngineSystem system) {
            for (Status status : values()) {
                if (status.isViable(system) && !status.equals(OKAY)) {
                    return status;
                }
            }

            return OKAY;
        }
    }
}
